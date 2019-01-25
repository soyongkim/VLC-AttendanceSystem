package com.example.soyongkim.vlc_receiver.model.item;

import org.json.JSONException;
import org.json.JSONObject;

public class Resource {
    private int rid;
    private String rName;
    private String rType;
    private String rCate;
    private String rStatus;
    private String owner;
    private int permission;

    public Resource(int _rid , String _rName , String _rType, String _rCate, String _rStatus, String _owner, int _permission)
    {
        rid = _rid;
        rName = _rName;
        rType = _rType;
        rCate = _rCate;
        rStatus = _rStatus;
        owner = _owner;
        permission = _permission;
    }

    public Resource(JSONObject obj) throws JSONException
    {
        if(obj.has("rid")) rid = obj.getInt("rid");
        else rid = -1;

        if(obj.has("permission")) permission = obj.getInt("permission");
        else permission = 4;

        if(obj.has("rtype")) rType = obj.getString("rtype");
        else rType = null;

        if(obj.has("rcate")) rCate = obj.getString("rcate");
        else rCate = null;

        if(obj.has("rname")) rName = obj.getString("rname");
        else rName = null;

        if(obj.has("owner")) owner = obj.getString("owner");
        else owner = null;

        if(obj.has("rstatus")) rStatus = obj.getString("rstatus");
        else rStatus = null;
    }
    public String getName()
    {
        return rName;
    }

    public String getType()
    {
        return rType;
    }

    public String getStatus()
    {
        return rStatus;
    }

    public String getCategory()
    {
        return rCate;
    }

    public String getOwner()
    {
        return owner;
    }

    public int getPermission()
    {
        return permission;
    }

    public String toString()
    {
        JSONObject obj = new JSONObject();
        try {
            obj.put("rid", rid)
                    .put("rname" , rName)
                    .put("rtype" , rType)
                    .put("rcate" , rCate)
                    .put("rstatus" , rStatus)
                    .put("permission" , permission)
                    .put("owner" , owner);

            return obj.toString();
        }catch(Exception e)
        {
            return null;
        }


    }
}