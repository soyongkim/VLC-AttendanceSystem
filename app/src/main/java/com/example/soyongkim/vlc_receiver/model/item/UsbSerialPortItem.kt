package com.example.soyongkim.vlc_receiver.model.item

import android.hardware.usb.UsbDevice
import com.example.soyongkim.vlc_receiver.R
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort

class UsbSerialPortItem (private var _port: UsbSerialPort)
{
    val port : UsbSerialPort
        get(){
            return _port
        }

    val driver : UsbSerialDriver
        get() {
            return _port.driver
        }

    val device : UsbDevice
        get() {
            return _port.driver.device
        }

    val usbimage : Int
        get(){
            return R.mipmap.icon_usb
        }
}
