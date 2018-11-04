package com.example.soyongkim.vlc_receiver.model.item

import com.example.soyongkim.vlc_receiver.R

class Content (private var _name : String)
{
    var name
        get() = _name
        set(value){
            _name = value
        }

    val contentImage : Int
        get(){
            return if(_name == "Description") R.mipmap.icon_desc
            else R.mipmap.icon_video
        }
}
