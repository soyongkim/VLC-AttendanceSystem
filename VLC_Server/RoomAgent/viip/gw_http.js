var http = require('http');
var shortid = require('shortid');

exports.http_request_ae = (path, method, ty, bodyString) => {
    return new Promise((resolve, reject) => {
        const options = {
            hostname: conf.cse.host,
            port: conf.cse.port,
            path: path,
            method: method,
            headers: {
                'X-M2M-RI': shortid.generate(),
                'Accept': `application/${conf.ae[ae_count].bodytype}`,
                'X-M2M-Origin': conf.ae[ae_count].id,
                'Locale': 'en'
            }
        };

        if(bodyString.length > 0) {
            options.headers['Content-Length'] = bodyString.length;
        }

        if(method === 'post') {
            const a = (ty === '') ? '' : (`; ty=${ty}`);
            options.headers['Content-Type'] = `application/vnd.onem2m-res+${conf.ae[ae_count].bodytype}${a}`;
        } else if(method === 'put') {
            options.headers['Content-Type'] = `application/vnd.onem2m-res+${conf.ae[ae_count].bodytype}`;
        }

        let res_body = [];
        const req = http.request(options, (res) => {
            res.on('data', (chunk) => res_body.push(chunk));

            res.on('end', () => {
                res_body = Buffer.concat(res_body).toString();
                const json_body = JSON.parse(res_body);

                //console.log(res_body);

                return resolve({res: res, res_body: json_body});
            });
        });

        req.on('error', (e) => {
            return reject(e);
        });

        //console.log(path);

        req.write(bodyString);
        req.end();
    });
};

/**
 * Send http request to local CSE
 * @param path
 * @param method
 * @param ty
 * @param bodyString
 */
exports.http_request_local = (path, method, ty, bodyString) => {
    return new Promise((resolve, reject) => {
        const options = {
            hostname: conf.cse.host,
            port: conf.cse.port,
            path: path,
            method: method,
            headers: {
                'X-M2M-RI': shortid.generate(),
                'Accept': `application/${conf.ae.bodytype}`,
                'X-M2M-Origin': conf.cse.name,
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

/**
 * Send http request to parent CSE
 * @param path
 * @param method
 * @param ty
 * @param bodyString
 */
exports.http_request_parent = (path, method, ty, bodyString) => {
    return new Promise((resolve, reject) => {
        const options = {
            hostname: conf.parent.host,
            port: conf.parent.port,
            path: path,
            method: method,
            headers: {
                'X-M2M-RI': shortid.generate(),
                'Accept': `application/${conf.ae[0].bodytype}`,
                'X-M2M-Origin': conf.cse.id,
                'Locale': 'en'
            }
        };

        if(bodyString.length > 0) {
            options.headers['Content-Length'] = bodyString.length;
        }

        if(method === 'post') {
            const a = (ty === '') ? '' : (`; ty=${ty}`);
            options.headers['Content-Type'] = `application/vnd.onem2m-res+${conf.ae[0].bodytype}${a}`;
        } else if(method === 'put') {
            options.headers['Content-Type'] = `application/vnd.onem2m-res+${conf.ae[0].bodytype}`;
        }

        let res_body = [];
        const req = http.request(options, (res) => {
            res.on('data', (chunk) => res_body.push(chunk));

            res.on('end', () => {
                res_body = Buffer.concat(res_body).toString();
                const json_body = JSON.parse(res_body);

                //debug(res_body);

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
