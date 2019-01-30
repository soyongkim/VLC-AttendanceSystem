var http = require('http');
const Promise = require('bluebird');
const shortid = require('shortid');
const conf = require('./va_conf.js');

var ae_count = 0;
var wdt_aeid = require('shortid').generate();

var cnt_count = 0;
var wdt_cntid = require('shortid').generate();

var cin_count = 0;
var wdt_cinid = require('shortid').generate();

console.log = require('debug')('hidden:va');
const debug = require('debug')('viip:va');

// origin added
const onem2m_http_request = (path, method, ty, bodyString, origin) => {
    return new Promise((resolve, reject) => {
        const options = {
            hostname: conf.cse.host,
            port: conf.cse.port,
            path: path,
            method: method,
            headers: {
                'X-M2M-RI': shortid.generate(),
                'Accept': `application/json`,
                'X-M2M-Origin': origin,
                'Locale': 'en'
            }
        };

        if(bodyString.length > 0) {
            options.headers['Content-Length'] = bodyString.length;
        }

        if(method === 'post') {
            const a = (ty === '') ? '' : (`; ty=${ty}`);
            options.headers['Content-Type'] = `application/vnd.onem2m-res+${conf.ae.bodytype}${a}`;
        } else if(method === 'put') {
            options.headers['Content-Type'] = `application/vnd.onem2m-res+${conf.ae.bodytype}`;
        }

        let res_body = [];
        const req = http.request(options, (res) => {
            res.on('data', (chunk) => res_body.push(chunk));

            res.on('end', () => {
                res_body = Buffer.concat(res_body).toString();
                const json_body = JSON.parse(res_body);

                //debug(json_body['m2m:dbg']);

                return resolve({res: res, res_body: json_body});
            });
        });

        req.on('error', (e) => {
            return reject(e);
        });

        //debug(path);

        req.write(bodyString);
        req.end();
    });
};

// Start to initialize resoruces
const init = () => {
    // if you don't want to make AE or Container, comment out these lines
    //wdt.set_wdt(wdt_aeid, 1, recur_crt_ae);
    wdt.set_wdt(wdt_cntid, 2, recur_crt_cnt);
};

// make AEs required to Server when initialized
const recur_crt_ae = () => {
    if(ae_count < conf.ae.length) {
        crt_ae(conf.ae[ae_count++]);
    }
    else {
        wdt.del_wdt(wdt_aeid);
        debug(`>> Complete making AE and start making AE Container`);
        recur_crt_ae_cnt();
    }
};

// make AE'Containers required to AE when initialized
const recur_crt_ae_cnt = () => {
    for(var i=0; i<ae_count; i++) {
        debug(`DEBUG: ae_count:${ae_count} | cnt-length:${conf.ae[i].cnt.length} | cnt:${JSON.stringify(conf.ae[i].cnt[0].name)}`)
        for(var j=0; j<conf.ae[i].cnt.length; j++) {
         crt_cnt(conf.ae[i].cnt[j], `/${conf.cse.name}/${conf.ae[i].name}`, conf.cse.id);
        }
    }
};

// make Conatainer requried to Server when initialized
const recur_crt_cnt = () => {
    if(cnt_count < conf.cnt.length) {
        crt_cnt(conf.cnt[cnt_count++], `/${conf.cse.name}`, conf.cse.id);
    }
    else {
        wdt.del_wdt(wdt_cntid);

        //debug(`>> Complete making Container and start making Content Instance`);
        wdt.set_wdt(wdt_cinid, 1, recur_crt_cin);
        //test_get();
    }
};

// make Contents Instance required to Server when initialized
const recur_crt_cin = () => {
    if(cin_count < conf.cin.length) {
     crt_cin(`${conf.cnt[0].parent}/${conf.cnt[0].name}`, conf.cin[cin_count], conf.cse.id);
     cin_count++;
  
    }
    else {
     wdt.del_wdt(wdt_cinid);
     debug(`>> Complete Resource Initialization`);
    }
 };
 
/**
 * make AE in Server
 *
 * @param {*} ae_info
 * @param ae_info.app_id
 * @param ae_info.app_name
 * @param ae_info.name
 * @param ae_info.parent
 * @param ae_info.id
 * @returns
 */
const crt_ae = (ae_info) => {
    const results_ae = {};

    results_ae['m2m:ae'] = {};
    results_ae['m2m:ae'].api = ae_info.app_id;
    results_ae['m2m:ae'].apn = ae_info.app_name;
    results_ae['m2m:ae'].rn = ae_info.name;
    results_ae['m2m:ae'].rr = true;

    const bodyString = JSON.stringify(results_ae);

    return new Promise((resolve, reject) => {
        onem2m_http_request(ae_info.parent, 'post', '2', bodyString, ae_info.id).then((result) => {
            const {res, res_body} = result;
   
            const status = res.headers['x-m2m-rsc'];
   
            if(status === '2001') {
                // AE Created (2001)
                resolve(status);
            }

            return status;
        }).catch((e) => {
            debug(`problem with ae_create request: ${e.message}`);
            reject(e);
        }).then((status) => {
            if(status === '5106' || status === '4105') {
                // AE Already exists (4105: CONFLICT, 5106: ALREADY_EXISTS)
                // Retrieve created AE info from CSE
                console.log(`x-m2m-rsc : ${status} <----`);
   
                if(ae_info.id === 'S')
                    ae_info.id = 'S' + shortid.generate();
   
                onem2m_http_request(`${ae_info.parent}/${ae_info.name}`, 'get', '', '', ae_info.id).then((result) => {
                    const {res, res_body} = result;
   
                    status = res.headers['x-m2m-rsc'];
                   
                    if(status === 2000) {
                        // OK (2000)
                        const ae_id = res_body['m2m:ae']['aei'];
   
                        console.log(`x-m2m-rsc : ${status} - ${ae_id} <----`);
                       
                        if(conf.ae[ae_count].id !== ae_id){
                            console.log(`AE-ID created is ${ae_id} not equal to device AE-ID is ${ae_info.id}`);
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


/**
 * make Container in Server
 *
 * @param {*} cnt
 * @param {*} parent
 * @param {*} origin 
 */
const crt_cnt = (cnt, parent, origin) => {
    const results_ct = {};
    results_ct['m2m:cnt'] = {};
    results_ct['m2m:cnt'].rn = (cnt.name) ? cnt.name : '';
    results_ct['m2m:cnt'].li = (cnt.locationID) ? cnt.locationID : '';
    results_ct['m2m:cnt'].or = (cnt.onRef) ? cnt.onRef : '';

    const bodyString = JSON.stringify(results_ct);

    onem2m_http_request(parent, 'post', '3', bodyString, origin).then((result) => {
        const {res, res_body} = result;

        if(res.headers['x-m2m-rsc'] === '2001') {
          if(cnt.name == 'cnt-attendance-schedule') {
              debug(`>> Complete making cnt-attendance-schedule and starting make cin`);
              wdt.set_wdt(wdt_cinid, 1, recur_create_cin);
          }
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

/**
 * make Contents Instance in Server
 *
 * @param {*} cnt
 * @param {*} content
 * @param {*} origin
 */
const crt_cin = (cnt, content, origin) => {
  var results_ci = {};
  var bodyString = '';

  results_ci['m2m:cin'] = {};
  results_ci['m2m:cin'].con = content;

  bodyString = JSON.stringify(results_ci);

  onem2m_http_request(cnt, 'post', '4', bodyString, origin).then((result) => {
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

// test that the resources required to Server is made well
const test_get = () => {
    var cnt = `/${conf.cse.name}/`;
    var bodyString = '';
    onem2m_http_request(cnt, 'get', 3, bodyString).then((result) => {
        const {res, res_body} = result;
        if(res.headers['x-m2m-rsc'] === '2000') {
            debug('Result : ' + cnt + '- x-m2m-rsc : ' + res.headers['x-m2m-rsc'] + ' <----' );
            debug(`TEST:${JSON.stringify(res_body['m2m:cnt'].or)}`);
        }
        //debug('POST(' + cnt + ') : Request Success');
    });
}

exports.init = init;
exports.crt_ae = crt_ae;
exports.crt_cnt = crt_cnt;
exports.crt_cin = crt_cin;