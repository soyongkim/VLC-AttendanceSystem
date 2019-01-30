const Promise = require('bluebird');
const shortid = require('shortid');
const url = require('url');
const crypto = require('crypto');

const conf = require('./is_conf.js');
const comm = require('./comm_service');

var wdt_id = require('shortid').generate();

console.log = require('debug')('hidden:is_ae');
const debug = require('debug')('viip:is_ae');


var active_service_table = [];
var cookie_aid_mapping_table = [];

// Initailize IS
const init = () => {
    comm.init();
};

exports.process_request = function(parent, body_Obj, ty) {
    if (ty != '4') {
      return ty;
    }
    var con = body_Obj['cin']['con'];

    if(con == "") {
      console.log("Data is nothing:");
      return;
    }

    // route Container
    if(url.parse(parent['ri']).pathname.split('/')[2] == 'cnt-Client-Message') {
        if(con.flag == 0) {
            check_ar_msg(con.vtid, con.vaid, con.flag, con.aid);
        } else {
            check_vr_msg(con.vtid, con.vaid, con.flag, con.aid, con.cookie);
        }
    } else if(url.parse(parent['ri']).pathname.split('/')[2] == 'cnt-ExternalRequest') {
        if(con == 'start')
            startService();
    }
}

const startService = () => {
    make_frame_msg(1, 1, `padding`);
    wdt.set_wdt(wdt_id, 60, serviceChange);
}

const serviceChange = () => {
    wdt.del_wdt(wdt_id);
    make_frame_msg(1, 2, `padding`);
    wdt.set_wdt(wdt_id, 60, stopService);
}

const stopService = () => {
    wdt.del_wdt(wdt_id);
    make_frame_msg(0, 0, `padding`);
}


const make_frame_msg = (type, flag, aid) => {

}

const check_ar_msg = (vtid, vaid, flag, aid) => {
    
}

const check_vr_msg = (vtid, vaid, flag, aid, cookie) => {

}

exports.init = init;