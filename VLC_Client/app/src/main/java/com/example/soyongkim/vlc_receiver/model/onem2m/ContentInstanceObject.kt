package com.example.soyongkim.vlc_receiver.model.onem2m

import com.example.soyongkim.vlc_receiver.controller.util.JsonFormatter

/**
 * Created by Home on 2018-11-08.
 */
class ContentInstanceObject(private var _content: String) {
    private var content
        get() = _content
        set(value) {
            _content = value
        }

    fun makeXML(): String {
        var xml = ""

        xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        xml += "<m2m:cin "
        xml += "xmlns:m2m=\"http://www.onem2m.org/xml/protocols\" "
        xml += "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
        xml += "<cnf>text</cnf>"
        xml += "<con>$content</con>"
        xml += "</m2m:cin>"

        return xml
    }

    fun makeJSON(): String {
        return JsonFormatter.makeOneM2MFormat(content)
    }
}
