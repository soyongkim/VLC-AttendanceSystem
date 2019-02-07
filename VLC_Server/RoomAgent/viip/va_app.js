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


exports.mapping_vt_info = (body_Obj) => {
    vt_mapping_table[m_count].vt_name = `/${conf.cse.id}/${body_Obj.rn}`;
    vt_mapping_table[m_count].vt_id = m_count++;
};


exports.process_request = function (parent, body_Obj, ty) {
    if (ty != '4') {
        return ty;
    }
    var con = body_Obj['cin']['con'];
    var ae_rsc_nm = url.parse(parent['ri']).pathname.split('/')[2];
    if (ae_rsc_nm == '*') {
        for (var i = 0; i < m_count; i++) {
            noti_to_vt(`${vt_mapping_table[i].vt_name}/cnt-IS-Message`, con, conf.cse.id);
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
    }
};

const process_verify_msg = (con) => {
    var frame_aid = aid_count++;
    var tmp_cookie = con.cookie;
    con.type = 4;
    con.cookie = con.aid;
    con.aid = frame_aid;
    noti_to_vt(`${con.vtid}/cnt-IS-Message`, con, conf.cse.id);
    
    con.type = 2;
    con.cookie = tmp_cookie;
    setTimeout(function() {
        noti_to_vt(`${con.vtid}/cnt-IS-Message`, con, conf.cse.id);
    }, 1000);
};

const process_result_msg = (con) => {
    debug(`>> Send RESULT Message to ${con.vtid}`)
    noti_to_vt(`${con.vtid}/cnt-IS-Message`, con, conf.cse.id);
};

const noti_to_vt = (vt_path, con, origin) => {
    comm.crt_cin(vt_path, con, origin);
};

exports.init = init;