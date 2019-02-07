const SerialPort = require('serialport');

let serialport = null;
var ports = [];

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
        }
    }
};


const make_frame = (path_arr, cinObj) => {
    var cin = {};
    cin.ctname = path_arr[path_arr.length-2];
    cin.con = (cinObj.con != null) ? cinObj.con : cinObj.content;
    if(cin.con == '') {
        debug('---- is not cin message');
    }
    else {
        // you can modify if you want to change the frame structure
        debug('<---- send to VT Device');
        var frame = {};
        frame.vtid = cin.con['vtid'];
        frame.type = cin.con['type'];
        frame.cookie = cin.con['cookie'];
        frame.aid = cin.con['aid'];
        set_frame(frame);
    }
}


const set_frame = (frame) => {
    return new Promise((resolve, reject) => {
        for(var i=0; i<conf.serial.length; i++) {
            if(conf.serial[i].enabled == true && conf.serial[i].id == vtid) {
                debug(`Set [ vtid(${frame.vtid}) | type(${frame.type}) | cookie(${frame.cookie} | aid(${frame.aid}) ]`);
                
                // vtid (2bytes)
                serialPortBuffer.write(frame.vtid, 0, 2, 'hex');

                // type (1byte)
                serialPortBuffer.write(frame.type, 2, 3, 'hex');

                // cookie id or ateendee mapping id (10bytes) 
                serialPortBuffer.write(frame.cookie, 3, 13, 'hex');

                // attendee id (2bytes)
                serialPortBuffer.write(frame.aid, 13, 15, 'hex');

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

exports.init = init;
exports.make_frame = make_frame;