package com.example.soyongkim.vlc_receiver.model.item

import com.hoho.android.usbserial.driver.UsbSerialPort

object UsbSingleton {

    private lateinit var sPort: UsbSerialPort

   fun setUsbPort(sPort: UsbSerialPort) {
       this.sPort = sPort
   }

    fun getUsbPort() : UsbSerialPort {
        return this.sPort
    }
}