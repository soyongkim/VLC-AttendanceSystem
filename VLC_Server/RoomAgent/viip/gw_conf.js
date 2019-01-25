// Configuration
const conf = {};
conf.parent = {};
conf.parent.host = parent_cbhost;
conf.parent.port = parent_cbhostport;
conf.parent.name = parent_cbname;
conf.parent.id = `/${conf.parent.name}`;

conf.cse = {};
conf.cse.host = 'localhost';
conf.cse.port = usecsebaseport;
conf.cse.name = usecsebase;
conf.cse.id = `/${conf.cse.name}`;

// Container for GW
// conf.cnt = [];
// conf.cnt.push({
//     parent: `/${conf.cse.name}`,
//     name: 'CNT-GW-DATA',
//     label: ['PS->GW DATA']
// });
// conf.cnt.push({
//     parent: `/${conf.cse.name}`,
//     name: 'CNT-GW-CONTROL',
//     label: ['PS->GW CONTROL']
// });

// AE for GW
// var aecnt = 0;
// conf.ae = [];
// conf.ae[aecnt] = {};
// conf.ae[aecnt].parent = conf.cse.id;
// conf.ae[aecnt].name = 'AE-VIIP-GW';
// conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
// conf.ae[aecnt].app_id = 'VIIP-GW';
// conf.ae[aecnt++].bodytype = 'json';

module.exports = conf;