package com.example.anton.Banner;


import com.android.volley.toolbox.StringRequest;

import org.json.simple.JSONObject;

public class BannerInfo {
    JSONObject infoBanner;
    public BannerInfo(JSONObject jsonObject){
        infoBanner = jsonObject;
    }
    public String getInfoBanner(String paramInfo){
        return (String) infoBanner.get(paramInfo);
    }
}
