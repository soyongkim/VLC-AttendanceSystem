package com.example.soyongkim.vlc_receiver.controller.util;

import java.nio.charset.Charset;

public class TypeChangeUtil {
    public static int byteToIntId(byte[] src) {
        int s1 = 0 & 0xFF;
        int s2 = 0 & 0xFF;
        int s3 = src[0] & 0xFF;
        int s4 = src[1] & 0xFF;
        return ((s1 << 24) + (s2 << 16) + (s3 << 8) + (s4 << 0));
    }

    public static int byteToIntType(byte[] src) {
        int s1 = 0 & 0xFF;
        int s2 = 0 & 0xFF;
        int s3 = src[2] & 0xFF;
        int s4 = src[3] & 0xFF;
        return ((s1 << 24) + (s2 << 16) + (s3 << 8) + (s4 << 0));
    }

    public static byte[] byteToStringData(byte[] src) {
        byte[] data = new byte[10];
        for(int i=4; src[i] != 0; i++) {
            data[i-4] = src[i];
        }
        //String value = new String(data, Charset.forName("UTF-8"));
        return data;
    }
}
