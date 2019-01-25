const Promise = require('bluebird');
const shortid = require('shortid');
const url = require('url');
const express = require('express');
const fs = require('fs');
const ejs = require('ejs');
const http = require('./ps_http.js');
global.conf = require('./ps_conf.js');
const ps_app = require('./ps_app.js');
const crypto = require('crypto');

const SerialPort = require('serialport');
const serialPortBuffer = Buffer.alloc(conf.serial[0].bufferLength);

let serialport = null;
var ports = [];

var cnt_count = 0;
var wdt_id = require('shortid').generate();

global.ae_count = 0;
var wdt_aeid = require('shortid').generate();

console.log = require('debug')('hidden:ps_ae');
const debug = require('debug')('viip:ps_ae');

// Function definition
const init = () => {
    wdt.set_wdt(wdt_id, 2, recur_create_container);
    wdt.set_wdt(wdt_aeid, 3, recur_create_ae);
    init_serialport()
};

const init_serialport = () => {
    if(conf.serial[0].enabled === true && conf.serial[1].enabled === true) {
            for(var i=0; i< 2; i++) {
                serialport = new SerialPort(conf.serial[i].name, conf.serial[i].options);
                serialport.on('error', (err) => {
                    debug(`Error occurred on serialPort: ${err}`);
                    throw error;
                });
            ports[i] = serialport;
        }
    }
};

// 반복 호출 함수
const recur_create_ae = () => {
    //debug(`Before CREATE: ${ae_count}`);
    wdt.del_wdt(wdt_aeid);

    create_ae().then(() => {
        if(conf.ae[ae_count].cnt) {
            debug('--Call AECNT--');
            recur_create_aecnt().then(() => {
                ae_count++;
                debug('--DONE AECNT--' + ae_count);

                if(ae_count < conf.ae.length) {
                    debug('--set wdt--');
                    wdt.set_wdt(wdt_aeid, 2, recur_create_ae);
                }
            });

        } else {
            ae_count++;

            if(ae_count < conf.ae.length) {
                debug('--set wdt--');
                wdt.set_wdt(wdt_aeid, 2, recur_create_ae);
            }
        }
    });

    //debug(`AE Length: ${conf.ae.length}`);
    //debug(`After Create: ${ae_count}`);
};

const recur_create_aecnt = () => {
    return new Promise((resolve, reject) => {
        //debug('--AE CNT START -- ' + ae_count);
        for(var i=0; i<1; i++) {
            //debug('--AE CNT CREATE - ' + i);
            create_aecnt_local(i).then(() => debug('--AE CNT CREATED - ' + i)).catch((e) => reject(e));  
        }

        //debug('--AE CNT CREATE DONE- ');
        resolve();
    });
};

const recur_create_container = () => {
    create_containers_local();
    cnt_count++;
    debug(`bvalue: ${conf.cnt.length}`);
    debug(`bcnt: ${cnt_count}`);
    if(cnt_count >= conf.cnt.length) {
        wdt.del_wdt(wdt_id);
        debug(`acnt: ${cnt_count}`);
        cnt_count = 0;
    }
};

// 리소스 생성 함수
const create_ae = () => {
    var temp_ae_count = ae_count;
    const results_ae = {};

    results_ae['m2m:ae'] = {};
    results_ae['m2m:ae'].api = conf.ae[ae_count].app_id;
    results_ae['m2m:ae'].apn = conf.ae[ae_count].app_name;
    results_ae['m2m:ae'].rn = conf.ae[ae_count].name;
    results_ae['m2m:ae'].rr = true;

    const bodyString = JSON.stringify(results_ae);

    return new Promise((resolve, reject) => {
       http.http_request_ae(conf.ae[ae_count].parent, 'post', '2', bodyString).then((result) => {
           const {res, res_body} = result;
  
           const status = res.headers['x-m2m-rsc'];
  
           if(status === '2001') {
               // AE Created (2001)
               resolve(status);
           }
  
           return status;
       }).catch((e) => {
           console.log(`problem with ae_create request: ${e.message}`);
           reject(e);
       }).then((status) => {
           if(status === '5106' || status === '4105') {
               // AE Already exists (4105: CONFLICT, 5106: ALREADY_EXISTS)
               // Retrieve created AE info from CSE
               console.log(`x-m2m-rsc : ${status} <----`);
  
               if(conf.ae[ae_count].id === 'S')
                   conf.ae[ae_count].id = 'S' + shortid.generate();
  
               http.http_request_ae(`${conf.ae[ae_count].parent}/${conf.ae[ae_count].name}`, 'get', '', '').then((result) => {
                   const {res, res_body} = result;
  
                   status = res.headers['x-m2m-rsc'];
                  
                   if(status === 2000) {
                       // OK (2000)
                       const ae_id = res_body['m2m:ae']['aei'];
  
                       console.log(`x-m2m-rsc : ${status} - ${ae_id} <----`);
                      
                       if(conf.ae[ae_count].id !== ae_id){
                           console.log(`AE-ID created is ${ae_id} not equal to device AE-ID is ${conf.ae[ae_count].id}`);
                       }
                   } else {
                       console.log(`x-m2m-rsc : ${status}`);
                   }

                   resolve(status);
               }).catch((e) => {
                   console.log(`problem with request: ${e.message}`);
                   reject(e);
               });
           }
       });
    });
};
``
const create_aecnt_local = (index) => {
   const cnt = conf.ae[ae_count].cnt[index];
   var parent = `/${conf.cse.name}/${conf.ae[ae_count].name}`;

   const results_ct = {};

   results_ct['m2m:cnt'] = {};
   results_ct['m2m:cnt'].rn = cnt.name;
   results_ct['m2m:cnt'].lbl = cnt.label;

   const bodyString = JSON.stringify(results_ct);

   return http.http_request_local(parent, 'post', '3', bodyString).then((result, res_body) => {
       ps_app.create_cin_local(parent + '/' + cnt.name, 'x');
   });
};

const create_containers_local = () => {
     const cnt = conf.cnt[cnt_count];
     var parent = `/${conf.cse.name}`;

     const results_ct = {};

     results_ct['m2m:cnt'] = {};
     results_ct['m2m:cnt'].rn = cnt.name;
     results_ct['m2m:cnt'].lbl = cnt.label;

     const bodyString = JSON.stringify(results_ct);

     http.http_request_local(parent, 'post', '3', bodyString).then((result) => {
         const {res, res_body} = result;

         if(res.headers['x-m2m-rsc'] === '2001') {
           //debug('Result : ' + cnt + '- x-m2m-rsc : ' + res.headers['x-m2m-rsc'] + '<----' );
           //debug('POST(' + parent + ') : Request Success');
       }
       else {
           //debug(cnt + '- x-m2m-rsc : ' + res.headers['x-m2m-rsc'] + '<----' ); 
           //debug('POST(' + parent + ') : Request Fail');
       }
       //debug(res_body);
     });

};

exports.create_cin_local = (cnt, content) => {
   var results_ci = {};
   var bodyString = '';

   results_ci['m2m:cin'] = {};
   results_ci['m2m:cin'].con = content;

   bodyString = JSON.stringify(results_ci);

   http.http_request_local(cnt, 'post', '4', bodyString).then((result) => {
       const {res, res_body} = result;

       if(res.headers['x-m2m-rsc'] === '2001') {
           //debug('Result : ' + cnt + '- x-m2m-rsc : ' + res.headers['x-m2m-rsc'] + '<----' );
           //debug('POST(' + cnt + ') : Request Success');
       }
       else {
           //debug(cnt + '- x-m2m-rsc : ' + res.headers['x-m2m-rsc'] + '<----' ); 
           //debug('POST(' + cnt + ') : Request Fail');
       }
       //debug(res_body);
   });
};

exports.process_cin_vlc_data = function(parent, body_Obj, ty) {
    if (ty != '4') {
      return ty;
    }
    var con = body_Obj['cin']['con'];

    if(con == "") {
      console.log("Data is nothing:");
      return;
    }

    if(url.parse(parent['ri']).pathname.split('/')[2] == 'cnt-museum') {
      var initData = "0102030405060708090a0b0c0d0e0f";
      set_vt_device(0, con, initData).then(() => {
          set_vt_device(1, con, initData).then();
      });
    }
}

const set_vt_device = (id, type, data) => {
    return new Promise((resolve, reject) => {
        if(conf.serial[0].enabled === true && conf.serial[1].enabled === true) {
            debug(`VLC frame to set (id: ${id}, type: ${type}, data: ${data})`);

            // id set
            if(id == 0)
                serialPortBuffer.write("0001", 0, 2, 'hex');
            else
                serialPortBuffer.write("0002", 0, 2, 'hex');

            // type set
            if(type == "image")
                type = "0001";
            else if(type == "video")
                type = "0002";

            serialPortBuffer.write(type, 2, 2, 'hex');
            serialPortBuffer.write(data, 4, data.length, 'hex');

            debug(`Writing to serialPort (${serialPortBuffer.toString('hex')}) -Length:${data.length}`);
        
            ports[id].write(serialPortBuffer, (error) => {
                if(error) {
                    error(`Error on writing serialport: ${error}`);
                    reject();
                }
                else {
                    debug(`${serialPortBuffer.length} bytes are written to serialPort`);
                    resolve();
                }
            });
        }
    });
}

exports.generate_key = function(order) {
    if(order == "start") {
        ps_key = crypto.randomBytes(16).toString('hex').toUpperCase();
        debug(`-key gerenated: ${ps_key}`);
        // send key to VLC Transmiiter
        var vtid = 0;
        var type = "1111";
	var data = ps_key;
        set_vt_device(vtid, type, data);
    } else {
        ps_key = "Nothing"
	var vtid = 0;
	var type = "1111";
	var data = "00000000000000000000"
	set_vt_device(vtid, type, data);
    }
}

exports.init = init;
