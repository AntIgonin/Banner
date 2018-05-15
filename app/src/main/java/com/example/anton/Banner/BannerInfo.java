package com.example.anton.Banner;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static android.content.ContentValues.TAG;

public class BannerInfo {

    Context context;
    BannerConst bannerConst;
    JSONObject jsonObject;
    String url;
    RequestQueue queue;
    StringRequest banerReq;
    ViewGroup viewGroup;
    boolean volleyready;
    View view;

    String image;

    public BannerInfo(final Context context, final ViewGroup viewGroup, final View view){
        this.context = context;
        this.view = view;
        this.viewGroup = viewGroup;
        queue = Volley.newRequestQueue(context);
        bannerConst = new BannerConst(context);


        url = "http://adlibtech.ru/adv/bserv.php?" +
                "action=getAdContent" +
                "&appname=" + bannerConst.getName() +
                "&appbundle=com.example.anton.election" +
                "&appversion=" + bannerConst.getVersion() +
                "&deviceid=" + bannerConst.getdeviceUuid() +
                "&devicename=" + bannerConst.getdeviceName() +
                "&devicemodel=" + bannerConst.getdeviceModel() +
                "&systemname=" + bannerConst.getdeviceSystemName() +
                "&systemversion=" + bannerConst.getdeviceSystemVersion() +
                "&swidth=" + bannerConst.width() +
                "&sheight=" + bannerConst.height() +
                "&nettype=" + bannerConst.getnetType();



        url = url.replaceAll(" ", "%20");
        Log.d("URL",url);


        banerReq = new StringRequest(Request.Method.GET,url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        JSONParser jsonParser = new JSONParser();

                        try {
                            Object object = jsonParser.parse(s);

                            jsonObject = (JSONObject) object;

                            Log.wtf(TAG, "onResponse: "+s);



                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.fillInStackTrace();
                    }
                })
        {
        };
        queue.add(banerReq);

    }













}
