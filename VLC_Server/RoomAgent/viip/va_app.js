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

// when received heartbeat message from vt, initialize vt's TTL
const update_table_timer = (con) => {
    var first_register = true;
    for (var i = 0; i < vt_mapping_table.length; i++) {
        first_register = false;
        if (con.vtid == vt_mapping_table[i].vt_rn) {
            wdt.del_wdt(i);
            wdt.set_wdt(i, 30, expire_table_timer, i);
            vt_mapping_table[i].vt_state = con.state;
            debug(`>> update vt[${vt_mapping_table[i].vt_name}] : state[${vt_mapping_table[i].vt_state}] in table`);

            check_vt_state_table();
            break;
        }
        else if (i == vt_mapping_table.length-1) {
            if(!con.vtid) {
                register_vt_table(con.vtid);
            }
            else
                debug(`>> Not Found vt[${vt_mapping_table[i].vt_name}]'s info in table | ${vt_mapping_table[i].vt_rn} =/= ${con.vtid}`);
        }
    }

    if(first_register)
        register_vt_table(con);
};


const register_vt_table = (con) => {
    debug(`>> vt_info : ${JSON.stringify(con)}`);
    vt_mapping_table[m_count] = {};
    vt_mapping_table[m_count].vt_rn = con.vtid;
    vt_mapping_table[m_count].vt_name = `${conf.cse.id}/${con.vtid}`;
    vt_mapping_table[m_count].vt_state = 'idle';

    wdt.set_wdt(m_count, 30, expire_table_timer, m_count);
    m_count++;

    check_vt_state_table();
};


const expire_table_timer = (vt_count) => {
    wdt.del_wdt(vt_count);
    del_vt(vt_mapping_table[vt_count].vt_name, vt_mapping_table[vt_count].vt_rn);
    vt_mapping_table.splice(vt_count, 1);
    m_count--;
    check_vt_state_table();
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

const check_vt_state_table = () => {
    debug(`-- VT STATE TABLE --`);
    for (var i = 0; i < vt_mapping_table.length; i++) {
        debug(`[vt${i}] name: ${vt_mapping_table[i].vt_name} | rn: ${vt_mapping_table[i].vt_rn} | state: ${vt_mapping_table[i].vt_state}`);
    }
};

exports.process_request = function (parent, body_Obj, ty) {
    if (ty != '4') {
        return ty;
    }

    var con = body_Obj['cin']['con'];
    var ae_rsc_nm = url.parse(parent['ri']).pathname.split('/')[2];
    var hb_msg = url.parse(parent['ri']).pathname.split('/')[3];
    //debug(`>> hb_msg:${hb_msg}`);

    if (hb_msg == 'vt_heartbeat') {
        update_table_timer(con);
    }

    if (ae_rsc_nm == '*') {
        for (var i = 0; i < vt_mapping_table.length; i++) {
            noti_to_vt(`${vt_mapping_table[i].vt_name}/is_Message`, con, conf.cse.id);
        }
    } else if (ae_rsc_nm == 'specVT') {
        for (var i = 0; i < vt_mapping_table.length; i++) {
            if (con.vtid == vt_mapping_table[i].vt_rn) {
                debug(`>> Found matched name(${vt_mapping_table[i].vt_name}) to id(${con.vtid})`);
                con.vtid = vt_mapping_table[i].vt_name;
                if (con.type == 2)
                    process_verify_msg(con);
                else
                    process_result_msg(con);
            }
        }
    } else if (ae_rsc_nm == 'ExternalRequest') {
        check_vt_state_table();
    }
};

const process_verify_msg = (con) => {
    debug(`>> Send VERIFY Message to ${con.vtid}`);
    noti_to_vt(`${con.vtid}/is_Message`, con, conf.cse.id);
};

const process_result_msg = (con) => {
    debug(`>> Send RESULT Message to ${con.vtid}`);
    noti_to_vt(`${con.vtid}/is_Message`, con, conf.cse.id);
};

const noti_to_vt = (vt_path, con, origin) => {
    comm.crt_cin(vt_path, con, origin);
};

exports.init = init;