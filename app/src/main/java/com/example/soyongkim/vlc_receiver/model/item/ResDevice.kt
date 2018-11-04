package com.example.soyongkim.vlc_receiver.model.item

import com.example.soyongkim.vlc_receiver.R

class   ResDevice(private var _ResID: Int,
                private var _Name: String,
                private var _Cate: String,
                private var _Auth: Int,
                private var _Interface: String,
                private var _Status: String,
                private var _Permission: Int,
                private var _Owner: String
) {
    val ResID get() = _ResID
    val Name get() = _Name
    val Cate get() = _Cate
    val Auth get() = _Auth
    val Interface get() = _Interface
    val Status get() = _Status
    val Permission get() = _Permission
    val Owner get() = _Owner
    val Image : Int
        get() {
            return R.mipmap.icon_student
        }

}