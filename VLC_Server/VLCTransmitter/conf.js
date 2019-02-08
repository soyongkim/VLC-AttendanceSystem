/**
 * Created by Il Yeup, Ahn in KETI on 2017-02-23.
 */

/**
 * Copyright (c) 2018, OCEAN
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

var ip = require("ip");

var conf = {};
var cse = {};
var ae = {};
var cnt_arr = [];
var sub_arr = [];
var acp = {};

// vr or vt mode
global.mode = 'vt';

conf.useprotocol = 'http'; // select one for 'http' or 'mqtt' or 'coap' or 'ws'

// build cse
cse.host        = '192.168.34.2';
cse.port        = '7599';
cse.name        = 'R314';
cse.id          = '/R314';
cse.mqttport    = '1883';
cse.wsport      = '7577';

// build ae
if(mode === 'vt') {
  ae.name         = 'VT1';
  ae.appid        = 'VLCTransmitter';                                                                                                                                                                                                                                               
}
else {
  ae.name         = 'VR1';
  ae.appid        = 'VR';
}
ae.id           = 'S' + ae.name;
ae.parent       = '/' + cse.name;
ae.port         = '9727';
ae.bodytype     = 'json'; // select 'json' or 'xml' or 'cbor'
ae.tasport      = '3105';

var count = 0;
if(mode === 'vt') {
  // build cnt
  cnt_arr[count] = {};
  cnt_arr[count].parent = '/' + cse.name + '/' + ae.name;
  cnt_arr[count++].name = 'is_Message';
  cnt_arr[count] = {};
  cnt_arr[count].parent = '/' + cse.name + '/' + ae.name;
  cnt_arr[count++].name = 'vt_heartbeat';

  // build sub
  count = 0;
  sub_arr[count] = {};
  sub_arr[count].parent = '/' + cse.name + '/' + ae.name + '/' + cnt_arr[count].name;
  sub_arr[count].name = 'sub-is_Message';
  sub_arr[count++].nu = 'mqtt://' + cse.host + '/' + ae.id + '?ct=json'; // mqtt
}
else {
  cnt_arr[count] = {};
  cnt_arr[count].parent = '/' + cse.name + '/' + ae.name;
  cnt_arr[count++].name = 'CNT-VR-DATA';
  cnt_arr[count] = {};
  cnt_arr[count].parent = '/' + cse.name + '/' + ae.name;
  cnt_arr[count++].name = 'CNT-VR-DESP';

  count = 0;
  sub_arr[count] = {};
  sub_arr[count].parent = '/' + cse.name + '/' + ae.name + '/' + cnt_arr[count].name;
  sub_arr[count].name = 'CNT-VR-DATA-SUB';
  sub_arr[count++].nu = 'mqtt://' + cse.host + '/' + ae.id + '?ct=json'; // mqtt
  sub_arr[count] = {};
  sub_arr[count].parent = '/' + cse.name + '/' + ae.name + '/' + cnt_arr[count].name;
  sub_arr[count].name = 'CNT-VR-DESP-SUB';
  sub_arr[count++].nu = 'mqtt://' + cse.host + '/' + ae.id + '?ct=json'; // mqtt
}
// --------

/*// --------
sub_arr[count] = {};
sub_arr[count].parent = '/' + cse.name + '/' + ae.name + '/' + cnt_arr[1].name;
sub_arr[count].name = 'sub2';
//sub_arr[count++].nu = 'http://' + ip.address() + ':' + ae.port + '/noti?ct=json'; // http
//sub_arr[count++].nu = 'mqtt://' + cse.host + '/' + ae.id + '?rcn=9&ct=' + ae.bodytype; // mqtt
sub_arr[count++].nu = 'mqtt://' + cse.host + '/' + ae.id + '?ct=json'; // mqtt
// -------- */

// build acp: not complete
acp.parent = '/' + cse.name + '/' + ae.name;
acp.name = 'acp-' + ae.name;
acp.id = ae.id;


conf.usesecure  = 'disable';

if(conf.usesecure === 'enable') {
    cse.mqttport = '8883';
}

conf.cse = cse;
conf.ae = ae;
conf.cnt = cnt_arr;
conf.sub = sub_arr;
conf.acp = acp;


module.exports = conf;
