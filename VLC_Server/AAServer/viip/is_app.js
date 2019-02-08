const Promise = require('bluebird');
const url = require('url');
const crypto = require('crypto');

const conf = require('./is_conf.js');
const comm = require('./comm_service');
const sql = require('../mobius/sql_action');

var idgen = require('shortid');

console.log = require('debug')('hidden:is_ae');
const debug = require('debug')('viip:is_ae');

// table for active service
var active_service_table = [];

// table for mapping between cookie and attendee id
var cookie_mapping_table = [];


// Initailize IS
const init = () => {
    comm.init();
};

exports.process_request = function (request, parent, body_Obj, ty) {
    if (ty != '4') {
        return ty;
    }

    // check contents
    var con = body_Obj['cin']['con'];
    if (con == "") {
        console.log("Data is nothing:");
        return;
    }

    // route Container
    var cnt_rsc_nm = url.parse(parent['ri']).pathname.split('/')[2];
    if (cnt_rsc_nm == 'cnt-Client-Message') {
        sql.select_csr_poa(request.header.host, function (err, result) {
            if (err == null) {
                con.locationID = result.cb;
                if (con.flag == 0) {
                    check_ar_msg(con);
                } else {
                    check_vr_msg(con);
                }
            }
        });
    } else if (cnt_rsc_nm == 'cnt-ExternalRequest') {
        if (con.type == 'start') {
            var dup = 'x';
            for(var i=0; i<active_service_table.length; i++) {
                if(active_service_table[i].name == con.name) {
                   dup = 'o';
                   break;
                }
            }
            if(dup == 'x')
                startService(con);
        }
        else if (con.type == 'check')
            check_mapping_table();
    }
}

const registerSchedule = (scheduleInfo) => {
    var cnt = {};
    cnt.name = scheduleInfo.name;
    cnt.locationID = scheduleInfo.locationID;
    cnt.onRef = scheduleInfo.time;
    comm.crt_cnt(cnt, `/${conf.cse.name}/ASEntity`, conf.cse.id).then((result) => {
        // make schedule table
        sql.create_schdule_table(cnt.name, function (err, res) {
            if (!err) {
                var list_string = 'value ';
                for (var i = 0; i < scheduleInfo.atdlist.length; i++) {
                    list_string += `(\'${scheduleInfo.atdlist[i].atd_id}\', \'${scheduleInfo.atdlist[i].atd_name}\', \'wait\'),`;
                }
                list_string = list_string.substring(0, list_string.length - 1);
                sql.insert_schedule_atd(cnt.name, list_string);
            }
        });
    });
};

const startService = (con) => {
    for (var i = 0; i < conf.schedule.length; i++) {
        debug(`> i count: ${i} | conf : ${conf.schedule[i].name} | con: ${con.name}`);
        if (conf.schedule[i].name == con.name) {
            debug(`-- Start Service (${con.name}) -- location: ${conf.schedule[i].locationID}`);
            active_service_table.push({
                name: conf.schedule[i].name,
                locationID: conf.schedule[i].locationID,
                time: conf.schedule[i].time,
                state: 'approval'
            });
            make_frame_msg(conf.schedule[i].locationID, `*`, 1, ``, ``);
            check_mapping_table();

            var wdt_id = idgen.generate();
            wdt.set_wdt(wdt_id, 5, changeService, con.name, wdt_id);
            break;
        }
    }
}

const changeService = (wdt_id, name) => {
    debug(`argument check: ${name} | ${wdt_id}`);
    wdt.del_wdt(wdt_id);
    for(var i=0; i< active_service_table.length; i++) {
        if(name == active_service_table[i].name) {
            debug(`>> Change Service[${active_service_table[i].name} : ${i}]`);
            active_service_table[i].state = 'late';
            check_mapping_table();
            wdt.set_wdt(wdt_id, 5, stopService, name, wdt_id);
            break;
        }
    }
}

const stopService = (wdt_id, name) => {
    debug(`argument check: ${name} | ${wdt_id}`);
    wdt.del_wdt(wdt_id);
    for(var i=0; i< active_service_table.length; i++) {
        if(name == active_service_table[i].name) {
            sql.update_schedule_atd(active_service_table[i].name, '', 'x', 'rest', function (err, res) {
                if (!err) {
                    make_frame_msg(active_service_table[i].locationID, `*`, 0, ``, ``);
                    debug(`>> Stop Service[${active_service_table[i].name}]`);
                    active_service_table.splice(i, 1);
                } else {
                    debug(`>> Error : Stop Service ${JSON.stringify(res)}--`);
                }
            });
            check_mapping_table();
            break;
        }
    }
}

const check_mapping_table = () => {
    debug(`---- check active mapping table ----`)
    for (var i = 0; i < active_service_table.length; i++) {
        debug(`[${i}] name: ${active_service_table[i].name} | state: ${active_service_table[i].state}`);
    }
    debug(`---- check end ----`)
};

const make_frame_msg = (va_location, vtid, type, cookie, aid) => {
    var contents = {};
    contents.type = type;
    contents.cookie = cookie;
    contents.aid = aid;
    if (vtid == '*')
        comm.crt_cin(`/${va_location}/*`, contents, conf.cse.id);
    else {
        contents.vtid = vtid;
        comm.crt_cin(`/${va_location}/specVT`, contents, conf.cse.id);
    }
}

const check_ar_msg = (con) => {
    for (var i = 0; i < active_service_table.length; i++) {
        if (con.locationID == active_service_table[i].locationID) {
            sql.select_schedule_atd(active_service_table[i].name, con.attendeeID, function (err, res) {
                if (!err) {
                    var m_cookie = crypto.randomBytes(4).toString('hex').toUpperCase();
                    cookie_mapping_table.push({
                        code: active_service_table[i].name,
                        aid: con.attendeeID,
                        cookie: m_cookie,
                        state: active_service_table[i].state
                    });
                    debug(`>> Make Cookie(${m_cookie}) for ${active_service_table[i].name} in ${con.locationID} || state : ${active_service_table[i].state}`);
                    make_frame_msg(con.locationID, con.vtid, 2, m_cookie, con.attendeeID);
                }
                else {
                    debug(`>> Not Found attendee's infomation`)
                }
            });
        }
    }
}

const check_vr_msg = (con) => {
    for (var i = 0; i < cookie_mapping_table.length; i++) {
        if (con.cookie == cookie_mapping_table[i].cookie) {
            sql.update_schedule_atd(code, cookie_mapping_table[i].code, cookie_mapping_table[i].aid, cookie_mapping_table[i].state, 'spec', function (err, res) {
                if (!err) {
                    debug(`>> ${cookie_mapping_table[i].aid} complete attendance!`);
                    make_frame_msg(con.locationID, con.vtid, 3, ``, con.aid);
                    cookie_mapping_table.splice(i, 1);
                }
            });
            break;
        }
    }
}

// register virtual schedule
exports.startRegister = () => {
    for (var i = 0; i < conf.schedule.length; i++) {
        registerSchedule(conf.schedule[i]);
    }
}

exports.init = init;