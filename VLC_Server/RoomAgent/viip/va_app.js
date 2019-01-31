const Promise = require('bluebird');
const shortid = require('shortid');
const url = require('url');
const crypto = require('crypto');

const conf = require('./va_conf.js');
const comm = require('./comm_service');
const sql = require('../mobius/sql_action');

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
    
    sql.retrieve_all_ae(function(err, result) {
        debug(`err:${err} result:${JSON.stringify(result)}`)
    });

    // redirect message if path is /R314/VT or /R314/* 
    if(url.parse(parent['ri']).pathname.split('/')[2] == '*') {
        // ae 검색하고 있는 ae에게 전부 메시지만들어서 전달하면 될 듯
        make_msg(con.type, con.flag, con.aid);
    } else {

    }
}

exports.init = init;