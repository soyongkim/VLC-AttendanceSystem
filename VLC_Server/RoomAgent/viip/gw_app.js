/**
 * @file Main code of VIIP (VLC-IoT) GW
 * @copyright CHEOLMIN KIM
 * @author CHEOLMIN KIM [cheolminkim@vanilet.pe.kr]
 */

const debug = require('debug')('viip:gw_ae');

const Promise = require('bluebird');
const shortid = require('shortid');
const url = require('url');
const wdt = require('../wdt')
const http = require('./gw_http');
global.conf = require('./gw_conf.js');

var cnt_count = 0;
var wdt_id = require('shortid').generate();

global.ae_count = 0;
var wdt_aeid = require('shortid').generate();

// Function definition
const init = () => {
    debug('GW_APP INIT');
    //wdt.set_wdt(wdt_id, 2, recur_create_container);
    //wdt.set_wdt(wdt_aeid, 3, recur_create_ae);
    //init_virtual_heartbeat();
    wifi_init();

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
        for(var i=0; i<3; i++) {
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
     results_ae['m2m:ae'].rn = conf.ae[ae_count].name;
     results_ae['m2m:ae'].rr = true;

     const bodyString = JSON.stringify(results_ae);
     //debug('HOW MANY AE_COUNT :' + ae_count);
     //debug(`START CREATION - ${conf.ae[ae_count].id}`);
     //debug(`0 value - ${conf.ae[0].id}`);
     //debug(`1 value - ${conf.ae[1].id}`);
     //debug(`0 value - ${conf.ae[0].name}`);
     //debug(`1 value - ${conf.ae[1].name}`);

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

const create_aecnt_local = (index) => {
    const cnt = conf.ae[ae_count].cnt[index];
    var parent = `/${conf.cse.name}/${conf.ae[ae_count].name}`;

    const results_ct = {};

    results_ct['m2m:cnt'] = {};
    results_ct['m2m:cnt'].rn = cnt.name;
    results_ct['m2m:cnt'].lbl = cnt.label;

    const bodyString = JSON.stringify(results_ct);

    return http.http_request_local(parent, 'post', '3', bodyString).then((result) => {
        const {res, res_body} = result;
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

const create_cin_local =(cnt, content) => {
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

const create_cin_parent =(cnt, content) => {
  var results_ci = {};
  var bodyString = '';

  results_ci['m2m:cin'] = {};
  results_ci['m2m:cin'].con = content;

  bodyString = JSON.stringify(results_ci);

  http.http_request_parent(cnt, 'post', '4', bodyString).then((result) => {
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



var match = [];
match.push({
    vtid:'empty',
    vrid:'empty'
});
exports.binding_cin = function(parent, body_Obj, ty) {
    if(ty == '2') {
        //debug('AE binding:' + body_Obj.ae['rn']);
        var urlparse = body_Obj.ae['rn'];
        if(urlparse) {
            //debug('Avaliable - ' + body_Obj.ae['rn']);
            if(urlparse.startsWith('VT')) {
                let check = 0;
                for(var i=0; i<match.length; i++) {
                    if(match[i].vtid == urlparse) {
                        check = 1;
                        break;
                    }
                }

                if(check == 0) {
                    match.push({
                        vtid: '/'+urlparse,
                        vrid: 'Not Matching'
                    });
                }
            }
        }

        // for(var i=0; i<match.length; i++) {
        //     debug(`match[${i}]: VT=${match[i].vtid} VR=${match[i].vrid}`);
        // }
    }

    if (ty != '4') {
      return ty;
    }
    var req_con = body_Obj['cin']['con'];

    if(req_con == "") {
      console.log("con is nothing");
      return;
    }

    if(url.parse(parent['ri']).pathname.split('/')[2] == 'CNT-GW-DATA') {
        var res_con = {};
        res_con.serviceid = req_con['serviceid'];
        res_con.gwid = url.parse(usecsebase).pathname.split('-')[1] + '-' + url.parse(usecsebase).pathname.split('-')[2];
        res_con.vtid   = req_con['vtid'];
        res_con.upsid  = req_con['upsid'];
        res_con.upspwd = req_con['upspwd'];
        var cnt = usecseid + req_con['vtid'] + '/CNT-VLC-DATA';
        create_cin_local(cnt, res_con);
    }
    else if(url.parse(parent['ri']).pathname.split('/')[3] == 'CNT-VR-DESP') {
        var cnt =  parent['pi'] + '/CNT-VR-DATA';

        debug(`[${url.parse(parent['ri']).pathname.split('/')[2]}] Item Request Message`);
        debug(`[VT id] : ${req_con['vtid']}`);
        debug(`[Frame Value] : ${req_con['nValue']}`);
        
        //debug('match length :' + match.length);
        for (var i=0; i<match.length; i++) {
            //debug('For forward');
            if (match[i].vtid == req_con['vtid']) {
                //debug('Already exsist');
                match[i].vrid = '/'+url.parse(parent['ri']).pathname.split('/')[2];
            } else {
                match[i].vrid = 'Not Matching'
            }
        }

        //debug('Check : ' + JSON.stringify(match));
        //cin에서 vlc frame정보를 파싱하고 결과값을 반영
        if(req_con['vtid'] == '/VT1') {
            res_con = 'req_1';
        }
        else if(req_con['vtid'] == '/VT2') {
            res_con = 'req_2';
        }
        else if(req_con['vtid'] == '/VT3') {
            res_con = 'req_3';
        }
        else if(req_con['vtid'] == '/VT4') {
            res_con = 'req_4';
        }
        else if(req_con['vtid'] == '/VT5') {
            res_con = 'req_5';
        }
        else if(req_con['vtid'] == '/VT6') {
            res_con = 'req_6';
        }
        create_cin_local(cnt, res_con);
    }
    else if(url.parse(parent['ri']).pathname.split('/')[3] == 'CNT-VT-HEARTBEAT') {
        var res_con = {};
        res_con.gwid  = usecsebase;
        res_con.vtid  = req_con['vtid'];
        res_con.vrid  = 'Not Matching';
        res_con.state = req_con['state'];
        var cnt = '/' + parent_cbname + '/CNT-PS-HEARTBEAT';
        

        for(var i in match) {
            if(match[i].vtid == req_con['vtid']) {
                res_con.vrid  = match[i].vrid;
                break;
            } 
            else {
                res_con.vrid = 'Not Matching';
            }
        }

        //debug('VRID : ' + res_con.vrid);
        create_cin_parent(cnt, res_con);

        // 컨트롤 cin 위치 변경
        if(req_con['state'] == 'off') {
            res_con = {};
            res_con.state = 'on';
            cnt = usecseid + req_con['vtid'] + '/CNT-VLC-CONTROL';
            create_cin_local(cnt, res_con);
      }
    }
}

const init_virtual_heartbeat = () => {
    wdt.set_wdt(require('shortid').generate(), 3, generate_virtual_heartbeat_vt2);
    wdt.set_wdt(require('shortid').generate(), 4, generate_virtual_heartbeat_vt3);
    wdt.set_wdt(require('shortid').generate(), 3, generate_virtual_heartbeat_vt4);
    wdt.set_wdt(require('shortid').generate(), 4, generate_virtual_heartbeat_vt5);
    wdt.set_wdt(require('shortid').generate(), 3, generate_virtual_heartbeat_vt6);
}

const generate_virtual_heartbeat_vt2 = () => {
    var res_con = {};
    res_con.gwid  = usecsebase;
    res_con.vtid  = '/VT2';
    res_con.vrid  = 'Not Matching';
    res_con.state = 'on';
    var cnt = '/' + parent_cbname + '/CNT-PS-HEARTBEAT';
    

    for(var i in match) {
        if(match[i].vtid == '/VT2') {
            res_con.vrid  = match[i].vrid;
            break;
        } 
        else {
            res_con.vrid = 'Not Matching';
        }
    }

    create_cin_parent(cnt, res_con);
}

const generate_virtual_heartbeat_vt3 = () => {
    var res_con = {};
    res_con.gwid  = usecsebase;
    res_con.vtid  = '/VT3';
    res_con.vrid  = 'Not Matching';
    res_con.state = 'on';
    var cnt = '/' + parent_cbname + '/CNT-PS-HEARTBEAT';
    

    for(var i in match) {
        if(match[i].vtid == '/VT3') {
            res_con.vrid  = match[i].vrid;
            break;
        } 
        else {
            res_con.vrid = 'Not Matching';
        }
    }

    //debug('VRID : ' + res_con.vrid);
    create_cin_parent(cnt, res_con);

}

const generate_virtual_heartbeat_vt4 = () => {
    var res_con = {};
    res_con.gwid  = usecsebase;
    res_con.vtid  = '/VT4';
    res_con.vrid  = 'Not Matching';
    res_con.state = 'on';
    var cnt = '/' + parent_cbname + '/CNT-PS-HEARTBEAT';
    

    for(var i in match) {
        if(match[i].vtid == '/VT4') {
            res_con.vrid  = match[i].vrid;
            break;
        } 
        else {
            res_con.vrid = 'Not Matching';
        }
    }

    //debug('VRID : ' + res_con.vrid);
    create_cin_parent(cnt, res_con);
}

const generate_virtual_heartbeat_vt5 = () => {
    var res_con = {};
    res_con.gwid  = usecsebase;
    res_con.vtid  = '/VT5';
    res_con.vrid  = 'Not Matching';
    res_con.state = 'on';
    var cnt = '/' + parent_cbname + '/CNT-PS-HEARTBEAT';
    

    for(var i in match) {
        if(match[i].vtid == '/VT5') {
            res_con.vrid  = match[i].vrid;
            break;
        } 
        else {
            res_con.vrid = 'Not Matching';
        }
    }

    //debug('VRID : ' + res_con.vrid);
    create_cin_parent(cnt, res_con);
}

const generate_virtual_heartbeat_vt6 = () => {
    var res_con = {};
    res_con.gwid  = usecsebase;
    res_con.vtid  = '/VT6';
    res_con.vrid  = 'Not Matching';
    res_con.state = 'on';
    var cnt = '/' + parent_cbname + '/CNT-PS-HEARTBEAT';
    

    for(var i in match) {
        if(match[i].vtid == '/VT6') {
            res_con.vrid  = match[i].vrid;
            break;
        } 
        else {
            res_con.vrid = 'Not Matching';
        }
    }

    //debug('VRID : ' + res_con.vrid);
    create_cin_parent(cnt, res_con);
}

/**
 * Init Wi-Fi (Management channel, Upstream channel)
 */
const wifi_init = () => {

};

exports.init = init;
