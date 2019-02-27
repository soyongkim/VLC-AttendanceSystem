const SerialPort = require('serialport');
const debug = require('debug')('viip:vt');

let serialport = null;
var ports = [];

// frame state
var frmState = { code: 0, type: 'idle'};

// keep temporarily
var serialnum = 0;
conf.serial = [];
// usb0
conf.serial[serialnum] = {};
conf.serial[serialnum].enabled = true;
conf.serial[serialnum].name = "/dev/ttyUSB0";
conf.serial[serialnum].id = `/VT1`;
conf.serial[serialnum].bufferLength = 20;
conf.serial[serialnum].options = {};
conf.serial[serialnum++].options.baudRate = 115200;
// usb1
conf.serial[serialnum] = {};
conf.serial[serialnum].enabled = false;
conf.serial[serialnum].name = "/dev/ttyUSB1";
conf.serial[serialnum].id = `/VT2`;
conf.serial[serialnum].bufferLength = 20;
conf.serial[serialnum].options = {};
conf.serial[serialnum].options.baudRate = 115200;

const serialPortBuffer = Buffer.alloc(conf.serial[0].bufferLength);

// Initailize IS
const init = () => {
    init_serialport();
    wdt.set_wdt(require('shortid').generate(), 5, timer_upload_action);
};

/**
 * initialize serialport module to use on it (it is going to move to VT)
 */
const init_serialport = () => {
    for(var i=0; i<conf.serial.length; i++) {
        if(conf.serial[i].enabled == true) {
            serialport = new SerialPort(conf.serial[i].name, conf.serial[i].options);
            serialport.on('error', (err) => {
                debug(`Error occurred on serialPort: ${err}`);
                throw error;
            });
            ports[i] = serialport;
            make_general_frame(0);
        }
    }
};


const send_vt = (path_arr, cinObj) => {
    var cin = {};
    cin.ctname = path_arr[path_arr.length-2];
    cin.con = (cinObj.con != null) ? cinObj.con : cinObj.content;
    if(cin.con == '') {
        debug('---- is not cin message');
    }
    else {  
        if(cin.con['type'] == 0 || cin.con['type'] == 1) {
            if(cin.con['type'] == 0)
                frmState = { code: 0, type: 'idle'};
            else 
                frmState = { code: 1, type: 'active'};
            make_general_frame(cin.con['type']);
        } else {
            make_specific_frame(cin.con);
        }
    }
}

/**
 * make frame for general situation (idle or active)
 *
 * @param {*} type : idle or active
 */
const make_general_frame = (type) => {
    debug(`>> Make General Frame [${frmState.type}]`);
    var frame = {};
    frame.vtid = conf.ae.name;
    frame.type = type;
    frame.cookie = "00000000";
    frame.aid = "00000000000000000000";
    set_frame(frame);
}

/**
 * make frame for specific attendee (verify or result)
 *
 * @param {*} con : con.type(2,3), con.cookie, con.aid
 */
const make_specific_frame = (con) => {
    if(con.type == 2)
        debug(`>> Make Specific Frame [VERIFY] for (${con.aid})`);
    else
        debug(`>> Make Specific Frame [RESULT] for (${con.aid})`);
    var frame = {};
    frame.vtid = conf.ae.name;
    frame.type = con.type;
    frame.cookie = (con['cookie'] != "") ? con['cookie'] : "00000000";
    //debug(`ascii test: aid[${con['aid']} => ${ascii_to_hexa(con['aid'])}]`);
    frame.aid = ascii_to_hexa(con['aid']);
    // require modification
    set_frame(frame).then(() => {
        setTimeout(function() {
            make_general_frame(frmState.code);
        }, 1000);
    });
}


const set_frame = (frame) => {
    return new Promise((resolve, reject) => {
        for(var i=0; i<conf.serial.length; i++) {
            if(conf.serial[i].enabled == true && conf.serial[i].id == `/${frame.vtid}`) {
                debug(`Set [ vtid(${frame.vtid}) | type(${frame.type}) | cookie(${frame.cookie}) | aid(${frame.aid}) ]`);
                
                // vtid (4bytes)
                serialPortBuffer.write(ascii_to_hexa(frame.vtid), 0, 4, 'hex');

                // type (2bytes)
                //serialPortBuffer.write(frame.type, 4, 6, 'hex');
                serialPortBuffer.writeInt16BE(frame.type, 4, true);

                // cookie id or ateendee mapping id (4bytes) 
                serialPortBuffer.write(frame.cookie, 6, 10, 'hex');

                // attendee id (10bytes)
                serialPortBuffer.write(frame.aid, 10, 20, 'hex');

                debug(`Write to serialPort (${serialPortBuffer.toString('hex')})`);
            
                ports[i].write(serialPortBuffer, (error) => {
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
        }
    });
}

const ascii_to_hexa = (str) => {
	var arr1 = [];
	for (var n = 0, l = str.length; n < l; n++) 
     {
		var hex = Number(str.charCodeAt(n)).toString(16);
		arr1.push(hex);
	 }
	return arr1.join('');
}

// hb send 
function timer_upload_action() {
    if (sh_state == 'crtci' && mode === 'vt') {
        for (var j = 0; j < conf.cnt.length; j++) {
            if (conf.cnt[j].name == 'vt_heartbeat') {
                //var content = JSON.stringify({value: 'TAS' + t_count++});
                //var content = '[state]' + parseInt(Math.random()*100).toString();
                var content = {};
                content.state = frmState.type;
                content.vtid = conf.ae.name;
                debug(`HEARTBEAT Message Send [VT state]: ${content['state']} [VT ID]: ${content['vtid']} ---->`);
                var parent = conf.cnt[j].parent + '/' + conf.cnt[j].name;
                sh_adn.crtci(parent, j, content, this, function (status, res_body, to, socket) {
                    //console.log('x-m2m-rsc : ' + status + ' <----');
                });
                break;
            }
        }
    }
}

exports.init = init;
exports.send_vt = send_vt;