// Configuration
const conf = {};

conf.cse = {};
conf.cse.host = 'localhost';
conf.cse.port = 7579;
conf.cse.name = usecsebase;
conf.cse.id = '/'+ usecsebase;

// Container for PS
conf.cnt = [];
conf.cnt.push({
    parent: `/${conf.cse.name}`,
    name: 'cnt-ps-key',
    label: ['Manager->PS(Create)->KEY']
});
conf.cnt.push({
    parent: `/${conf.cse.name}`,
    name: 'cnt-museum',
    label: ['Manager->PS(Create)->KEY']
});


var serialnum = 0;
conf.serial = [];
// usb0
conf.serial[serialnum] = {};
conf.serial[serialnum].enabled = true;
conf.serial[serialnum].name = "/dev/ttyUSB0";
conf.serial[serialnum].bufferLength = 20;
conf.serial[serialnum].options = {};
conf.serial[serialnum++].options.baudRate = 115200;
// usb1
conf.serial[serialnum] = {};
conf.serial[serialnum].enabled = true;
conf.serial[serialnum].name = "/dev/ttyUSB1";
conf.serial[serialnum].bufferLength = 20;
conf.serial[serialnum].options = {};
conf.serial[serialnum].options.baudRate = 115200;

// AE for PS
var aecnt = 0;
conf.ae = [];
// Virtual Students
// Student 1
conf.ae[aecnt] = {};
conf.ae[aecnt].parent = conf.cse.id;
conf.ae[aecnt].name = 'std_2018220889';
conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
conf.ae[aecnt].app_name = 'Soyong';
conf.ae[aecnt].app_id = '2018220889';
conf.ae[aecnt].cnt = [];
conf.ae[aecnt].cnt.push({
    parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
    name: 'cnt-state'
});
conf.ae[aecnt++].bodytype = 'json';
// Student 2
conf.ae[aecnt] = {};
conf.ae[aecnt].parent = conf.cse.id;
conf.ae[aecnt].name = 'std_2016116545';
conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
conf.ae[aecnt].app_name = 'Aaron';
conf.ae[aecnt].app_id = '2016116545';
conf.ae[aecnt].cnt = [];
conf.ae[aecnt].cnt.push({
    parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
    name: 'cnt-state'
});
conf.ae[aecnt++].bodytype = 'json';
// Student 3
conf.ae[aecnt] = {};
conf.ae[aecnt].parent = conf.cse.id;
conf.ae[aecnt].name = 'std_2016113067';
conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
conf.ae[aecnt].app_name = 'Donald';
conf.ae[aecnt].app_id = '2016113067';
conf.ae[aecnt].cnt = [];
conf.ae[aecnt].cnt.push({
    parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
    name: 'cnt-state'
});
conf.ae[aecnt++].bodytype = 'json';
// Student 4
conf.ae[aecnt] = {};
conf.ae[aecnt].parent = conf.cse.id;
conf.ae[aecnt].name = 'std_2016112530';
conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
conf.ae[aecnt].app_name = 'Gabriel';
conf.ae[aecnt].app_id = '2016112530';
conf.ae[aecnt].cnt = [];
conf.ae[aecnt].cnt.push({
    parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
    name: 'cnt-state'
});
conf.ae[aecnt++].bodytype = 'json';
// Student 5
conf.ae[aecnt] = {};
conf.ae[aecnt].parent = conf.cse.id;
conf.ae[aecnt].name = 'std_2014105004';
conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
conf.ae[aecnt].app_name = 'Martin';
conf.ae[aecnt].app_id = '2014105004';
conf.ae[aecnt].cnt = [];
conf.ae[aecnt].cnt.push({
    parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
    name: 'cnt-state'
});
conf.ae[aecnt++].bodytype = 'json';
// Student 6
conf.ae[aecnt] = {};
conf.ae[aecnt].parent = conf.cse.id;
conf.ae[aecnt].name = 'std_2013097010';
conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
conf.ae[aecnt].app_name = 'Albert';
conf.ae[aecnt].app_id = '2013097010';
conf.ae[aecnt].cnt = [];
conf.ae[aecnt].cnt.push({
    parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
    name: 'cnt-state'
});
conf.ae[aecnt++].bodytype = 'json';
// Student 7
conf.ae[aecnt] = {};
conf.ae[aecnt].parent = conf.cse.id;
conf.ae[aecnt].name = 'std_2013105016';
conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
conf.ae[aecnt].app_name = 'Robert';
conf.ae[aecnt].app_id = '2013105016';
conf.ae[aecnt].cnt = [];
conf.ae[aecnt].cnt.push({
    parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
    name: 'cnt-state'
});
conf.ae[aecnt++].bodytype = 'json';
// Student 8
conf.ae[aecnt] = {};
conf.ae[aecnt].parent = conf.cse.id;
conf.ae[aecnt].name = 'std_2014105019';
conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
conf.ae[aecnt].app_name = 'Sabastian';
conf.ae[aecnt].app_id = '2014105019';
conf.ae[aecnt].cnt = [];
conf.ae[aecnt].cnt.push({
    parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
    name: 'cnt-state'
});
conf.ae[aecnt++].bodytype = 'json';
// Student 9
conf.ae[aecnt] = {};
conf.ae[aecnt].parent = conf.cse.id;
conf.ae[aecnt].name = 'std_2014105022';
conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
conf.ae[aecnt].app_name = 'Wallace';
conf.ae[aecnt].app_id = '2014105022';
conf.ae[aecnt].cnt = [];
conf.ae[aecnt].cnt.push({
    parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
    name: 'cnt-state'
});
conf.ae[aecnt++].bodytype = 'json';

// Key State for PS
global.ps_key = `1111`;

module.exports = conf;