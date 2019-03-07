package com.example.soyongkim.vlc_receiver.controller.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonFormatter {
    public static String makeARPayload(String vtid, String aid, String type) throws JSONException {
        JSONObject jsonPayload = new JSONObject();
        JSONObject con = new JSONObject();
        JSONObject contents = new JSONObject();

        contents.put("vtid", vtid);
        contents.put("aid", aid);
        contents.put("type", type);

        con.put("con", contents);

        jsonPayload.put("m2m:cin", con);

        return jsonPayload.toString();
    }

    public static String makeVRPayload(String vtid, String cookie, String aid, String type) throws JSONException {
        JSONObject jsonPayload = new JSONObject();
        JSONObject con = new JSONObject();
        JSONObject contents = new JSONObject();

        contents.put("vtid", vtid);
        contents.put("cookie", cookie);
        contents.put("aid", aid);
        contents.put("type", type);

        con.put("con", contents);

        jsonPayload.put("m2m:cin", con);

        return jsonPayload.toString();
    }
}
