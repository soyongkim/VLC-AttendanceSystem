package com.example.soyongkim.vlc_receiver.model.onem2m

/**
 * Created by Home on 2018-11-07.
 */
class CSEBase(private var _host: String, private var _port: String, private var _cseName: String) {
    var host
        get() = _host
        set(value) {
            _host = value
        }

    var port
        get() = _port
        set(value) {
            _port = value
        }

    var cseName
        get() = _cseName
        set(value) {
            _port = value
        }

    val serviceUrl: String
        get() = "http://$host:$port/$cseName"

}
