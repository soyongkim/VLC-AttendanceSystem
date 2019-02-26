package com.example.soyongkim.vlc_receiver.controller.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TypeChangeUtil {
    public static int byteToIntId(byte[] src) {
        int s1 = 0 & 0xFF;
        int s2 = 0 & 0xFF;
        int s3 = src[0] & 0xFF;
        int s4 = src[1] & 0xFF;
        return ((s1 << 24) + (s2 << 16) + (s3 << 8) + (s4 << 0));
    }

    public static String byteToId(byte[] src) {
        byte[] data = new byte[4];
        for(int i=0; i<4; i++) {
            if(src[i] != 0)
                data[i] = src[i];
        }
        String value = new String(data, Charset.forName("UTF-8"));
        return value.trim();
    }

    public static int byteToType(byte[] src) {
        byte[] data = new byte[2];
        for(int i=4; i<6; i++) {
            data[i-4] = src[i];
        }

        return new BigInteger(data).intValue();
    }

    public static byte[] byteToCookie(byte[] src) {
        byte[] data = new byte[4];
        for(int i=6; i<10; i++) {
            data[i-6] = src[i];
        }
        return data;
    }

    public static String byteToAid(byte[] src) {
        byte[] data = new byte[10];
        for(int i=10; i<20; i++) {
            data[i-10] = src[i];
        }
        String value = new String(data, Charset.forName("UTF-8"));
        return value;
    }

    public static int byteToIntType(byte[] src) {
        int s1 = 0 & 0xFF;
        int s2 = 0 & 0xFF;
        int s3 = src[2] & 0xFF;
        int s4 = src[3] & 0xFF;
        return ((s1 << 24) + (s2 << 16) + (s3 << 8) + (s4 << 0));
    }

    public static String byteToStringData(byte[] src) {
        byte[] data = new byte[16];
        for(int i=4; i<20; i++) {
            data[i-4] = src[i];
        }
        String value = new String(data, Charset.forName("UTF-8"));
        return value;
    }
}
