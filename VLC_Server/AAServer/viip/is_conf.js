// Configuration
const conf = {};

conf.cse = {};
conf.cse.host = 'localhost';
conf.cse.port = 7579;
conf.cse.name = usecsebase;
conf.cse.id = '/'+ usecsebase;

// AE
var aecnt = 0; 
conf.ae = [];
// Virtual Students
// Student 1
// conf.ae[aecnt] = {};
// conf.ae[aecnt].parent = conf.cse.id;
// conf.ae[aecnt].name = 'std_2018220889';
// conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
// conf.ae[aecnt].app_name = 'Soyong';
// conf.ae[aecnt].app_id = '2018220889';
// conf.ae[aecnt].cnt = [];
// conf.ae[aecnt].cnt.push({
//     parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
//     name: 'cnt-COMP724'
// });
// conf.ae[aecnt].cnt.push({
//     parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
//     name: 'cnt-ITEC401'
// });
// conf.ae[aecnt++].bodytype = 'json';
// // Student 2
// conf.ae[aecnt] = {};
// conf.ae[aecnt].parent = conf.cse.id;
// conf.ae[aecnt].name = 'std_2016116545';
// conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
// conf.ae[aecnt].app_name = 'Aaron';
// conf.ae[aecnt].app_id = '2016116545';
// conf.ae[aecnt].cnt = [];
// conf.ae[aecnt].cnt.push({
//     parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
//     name: 'cnt-COMP724'
// });
// conf.ae[aecnt].cnt.push({
//     parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
//     name: 'cnt-COMP402'
// });
// conf.ae[aecnt++].bodytype = 'json';
// // Student 3
// conf.ae[aecnt] = {};
// conf.ae[aecnt].parent = conf.cse.id;
// conf.ae[aecnt].name = 'std_2016113067';
// conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
// conf.ae[aecnt].app_name = 'Donald';
// conf.ae[aecnt].app_id = '2016113067';
// conf.ae[aecnt].cnt = [];
// conf.ae[aecnt].cnt.push({
//     parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
//     name: 'cnt-ITEC401'
// });
// conf.ae[aecnt++].bodytype = 'json';

// Attendance Schedule Entity
conf.ae[aecnt] = {};
conf.ae[aecnt].parent = conf.cse.id;
conf.ae[aecnt].name = 'ASEntity';
conf.ae[aecnt].id = `S${conf.ae[aecnt].name}`;
conf.ae[aecnt].app_name = 'Attendance Schedule';
conf.ae[aecnt].app_id = 'IT5';
conf.ae[aecnt].cnt = [];
// conf.ae[aecnt].cnt.push({
//     parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
//     name: 'comp_724',
//     locationID: `R529`,
//     onRef: [
//         `08:00`,
//         `08:15`,
//         `11:00`
//     ]
// });
// conf.ae[aecnt].cnt.push({
//     parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
//     name: 'itec_401',
//     locationID: `R527`,
//     onRef: [
//         `08:00`,
//         `08:15`,
//         `11:00`
//     ]
// });
// conf.ae[aecnt].cnt.push({
//     parent: `/${conf.cse.name}/${conf.ae[aecnt].name}`,
//     name: 'comp_402',
//     locationID: `R314`,
//     onRef: [
//         `08:00`,
//         `08:15`,
//         `11:00`
//     ]
// });
conf.ae[aecnt++].bodytype = 'json';

// Container
conf.cnt = [];
// Client Message for attendance
conf.cnt.push({
    parent: `/${conf.cse.name}`,
    name: 'cnt-Client-Message',
    label: ['Message from Client trying to attend']
});
// for testing
conf.cnt.push({
    parent: `/${conf.cse.name}`,
    name: 'cnt-ExternalRequest',
    label: ['Request from Postman for testing']
});

// Contents Instance
conf.cin = [];

// register schedule message sample.
conf.schedule = [];
conf.schedule.push({
    name: `comp_724`,
    locationID: `R529`,
    time: [
        `08:00`,
        `08:15`,
        `11:00`
    ],
    atdlist: [
        {atd_id: `2018220889`, atd_name: `soyongkim`},
        {atd_id: `2015110289`, atd_name: `albert`},
        {atd_id: `2012304587`, atd_name: `david`}
    ]
});

conf.schedule.push({
    name: 'itec_401',
    locationID: `R527`,
    time: [
        `08:00`,
        `08:15`,
        `11:00`
    ],
    atdlist: [
        {atd_id: `2018220889`, atd_name: `soyongkim`},
        {atd_id: `2015110289`, atd_name: `albert`},
    ]
});

conf.schedule.push({
    name: 'comp_402',
    locationID: `R314`,
    time: [
        `08:00`,
        `08:15`,
        `11:00`
    ],
    atdlist: [
        {atd_id: `2018220889`, atd_name: `soyongkim`}
    ]
});

module.exports = conf;