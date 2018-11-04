package com.example.soyongkim.vlc_receiver.model.item

import com.example.soyongkim.vlc_receiver.R

class Student(private var _Name: String, private var _StudentNum : String) {
    var Name
        get() = _Name
        set(value) {
            _Name = value
        }

    var StudentNum
        get() = _StudentNum
        set(value){
            _StudentNum = value
        }

    val Image: Int
        get(){
            return if(_StudentNum == "2018220889") R.mipmap.icon_student0
            else if(_StudentNum == "2016116545") R.mipmap.icon_student2
            else if(_StudentNum == "2016113067") R.mipmap.icon_student3
            else if(_StudentNum == "2016112530") R.mipmap.icon_student4
            else if(_StudentNum == "2014105004") R.mipmap.icon_student5
            else if(_StudentNum == "2013097010") R.mipmap.icon_student6
            else if(_StudentNum == "2013105016") R.mipmap.icon_student7
            else if(_StudentNum == "2014105019") R.mipmap.icon_student8
            else if(_StudentNum == "2014105022") R.mipmap.icon_student9

            else R.mipmap.cate_nocheck
        }


    val cateImage : Int
        get(){
            return if(_StudentNum == "2018220889") R.mipmap.cate_check
            else R.mipmap.cate_nocheck
        }
}
