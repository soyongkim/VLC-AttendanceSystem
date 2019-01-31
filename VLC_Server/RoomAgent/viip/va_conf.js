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
conf.cnt = [];
conf.cnt.push({
    parent: `/${conf.cse.name}`,
    name: '*',
    label: ['Refer to all VTs in VA']
});

conf.ae = [];

conf.cin = [];
module.exports = conf;