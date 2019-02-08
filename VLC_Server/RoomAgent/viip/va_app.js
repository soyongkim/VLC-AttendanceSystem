const Promise = require('bluebird');
const url = require('url');

const conf = require('./va_conf.js');
const comm = require('./comm_service');

console.log = require('debug')('hidden:va');
const debug = require('debug')('viip:va');

// table for mapping between vt_name and vt_id
var m_count = 0;
var vt_mapping_table = [];

// Initailize IS
const init = () => {
    comm.init();
};

// if new vt is register, it is mapped in table
const mapping_vt_info = (body_Obj) => {
    debug(`>> body_Obj : ${JSON.stringify(body_Obj)}`);
    var unformatted_rn = ""+body_Obj.ae.rn;
    var short_id = unformatted_rn.substring(3, 2);
    vt_mapping_table[m_count] = {};
    vt_mapping_table[m_count].vt_rn = body_Obj.ae.rn;
    vt_mapping_table[m_count].vt_name = `${conf.cse.id}/${body_Obj.ae.rn}`;
    vt_mapping_table[m_count].vt_id = short_id;

    wdt.set_wdt(m_count, 30, expire_table_timer, m_count);
    m_count++;

    check_mapping_table();
};

// when received heartbeat message from vt, initialize vt's TTL
const update_table_timer = (vt_rn) => {
    for(var i=0; i<vt_mapping_table.length; i++) {
        if(vt_rn == `/${vt_mapping_table[i].vt_rn}`) {
            wdt.del_wdt(i);
            wdt.set_wdt(i, 30, expire_table_timer, i);
            debug(`>> update vt[${vt_mapping_table[i].vt_name}]'s info in table`);
            check_mapping_table();
            break;
        }
        else if(i == vt_mapping_table.length-1) {
            debug(`>> Not Found vt[${vt_mapping_table[i].vt_name}]'s info in table | ${vt_mapping_table[i].vt_rn} =/= ${vt_rn}`);
        }
    }
};


const expire_table_timer = (vt_count) => {
    wdt.del_wdt(vt_count);
    del_vt(vt_mapping_table[vt_count].vt_name, vt_mapping_table[vt_count].vt_rn);
    vt_mapping_table.splice(vt_count, 1);
    m_count--;
    check_mapping_table();
};

/**
 * delete vt resource, when expired
 * @param {*} ae_nm : ex) /${conf.cse.id}/${body_Obj.rn} = vt_mapping_table[index].name
 */
const del_vt = (ae_rn, ae_name) => {
    var origin = `S${ae_name}`
    comm.del_rsc(ae_rn, '2', origin);
    comm.del_rsc(`${ae_rn}/is_Message`, '3', origin);
    comm.del_rsc(`${ae_rn}/vt_Heartbeat`, '3', origin);
}

const check_mapping_table = () => {
    debug(`-- VT MAPPING TABLE STATE --`);
    for(var i=0; i<vt_mapping_table.length; i++) {
        debug(`[vt${i}] name: ${vt_mapping_table[i].vt_name} | rn: ${vt_mapping_table[i].vt_rn} | id: ${vt_mapping_table[i].vt_id}`);
    }
};

exports.process_request = function (parent, body_Obj, ty) {
    if(ty == '2') {
        mapping_vt_info(body_Obj);
    }
    if (ty != '4') {
        return ty;
    }

    var con = body_Obj['cin']['con'];
    var ae_rsc_nm = url.parse(parent['ri']).pathname.split('/')[2];
    var hb_msg = url.parse(parent['ri']).pathname.split('/')[3];

    if(hb_msg) {
        update_table_timer(con.vtid);
    }

    if (ae_rsc_nm == '*') {
        for (var i = 0; i < m_count; i++) {
            noti_to_vt(`${vt_mapping_table[i].vt_name}/is_Message`, con, conf.cse.id);
        }
    } else if (ae_rsc_nm == 'specVT') {
        for (var i = 0; i < m_count; i++) {
            if (con.vtid == vt_mapping_table[i].vt_id) {
                debug(`>> Found matched name(${vt_mapping_table[i].vt_name}) to id(${con.vtid})`);
                if (con.type == 2)
                    process_verify_msg(con);
                else
                    process_result_msg(con);
            }
        }
    } else if (ae_rsc_nm == 'ExternalRequest') {
        check_mapping_table();
    }
};

const process_verify_msg = (con) => {
    var frame_aid = aid_count++;
    var tmp_cookie = con.cookie;
    con.type = 4;
    con.cookie = con.aid;
    con.aid = frame_aid;
    noti_to_vt(`${con.vtid}/is_Message`, con, conf.cse.id);
    
    con.type = 2;
    con.cookie = tmp_cookie;
    setTimeout(function() {
        noti_to_vt(`${con.vtid}/is_Message`, con, conf.cse.id);
    }, 1000);
};

const process_result_msg = (con) => {
    debug(`>> Send RESULT Message to ${con.vtid}`)
    noti_to_vt(`${con.vtid}/is_Message`, con, conf.cse.id);
};

const noti_to_vt = (vt_path, con, origin) => {
    comm.crt_cin(vt_path, con, origin);
};

exports.init = init;