const Promise = require('bluebird');
const shortid = require('shortid');
const url = require('url');
const crypto = require('crypto');

const conf = require('./va_conf.js');
const comm = require('./comm_service');

console.log = require('debug')('hidden:va');
const debug = require('debug')('viip:va');


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

    // Container route
    if(url.parse(parent['ri']).pathname.split('/')[2] == 'cnt-VLC-Message') {
        make_msg(con.type, con.flag, con.aid);
    } else if(url.parse(parent['ri']).pathname.split('/')[2] == 'cnt-AR-Message') {
        check_ar_msg(vtid, vaid, flag, aid);
    }  else if(url.parse(parent['ri']).pathname.split('/')[2] == 'cnt-AR-Message') {
        check_vr_msg(vtid, vaid, flag, aid, cookie);
    }
}

const make_msg = (type, flag, aid) => {

}

const check_ar_msg = (vtid, vaid, flag, aid) => {
    
}

const check_vr_msg = (vtid, vaid, flag, aid, cookie) => {

}

exports.init = init;