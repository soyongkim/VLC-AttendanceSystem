package com.example.soyongkim.vlc_receiver.model.item

import com.example.soyongkim.vlc_receiver.R

class AttendanceUser(private var _Name: String, private var _PermissionLevel : Int) {
    var Name
        get() = _Name
        set(value) {
            _Name = value
        }

    var PermissionLevel
        get() = _PermissionLevel
        set(value){
            _PermissionLevel = value
        }

    val Image: Int
        get() {
            return if (this._Name == "Manager") R.mipmap.icon_manager
            else if (this._Name == "Student") R.mipmap.logo_student
            else R.mipmap.ic_launcher
        }
}
