package com.example.anton.Banner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.anton.bannerview.R;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Timer;

import static android.content.ContentValues.TAG;


public class BannerView extends ViewGroup {

    private String HOST_NAME = "http://adlibtech.ru";
    Context intentContext;
    View rootView;
    ViewGroup viewGroup;
    BannerInfo bannerInfo;
    RequestQueue queue;
    BannerConst bannerConst;
    String url;
    private StringRequest banerReq;
    private JSONObject jsonObject;
    protected int mWidth = 0;
    protected int mHeight = 0;

    public BannerView(Context context, ViewGroup viewGroup) {
        super(context);
        setWillNotDraw(false);

        intentContext = context;

        queue = Volley.newRequestQueue(context);

        this.viewGroup = viewGroup;

        Log.d("Construktor", "CCCCCC");

        rootView = inflate(context, R.layout.content_banner_view, viewGroup);

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

        Log.d("Url",url);

        LoadingStringRequest();
    }

    private void init(){
        TextView BannerText = (TextView) rootView.findViewById(R.id.BannerText);
        TextView BannerKing = (TextView) rootView.findViewById(R.id.BannerKing);
        ImageButton CloseBanner = (ImageButton) rootView.findViewById(R.id.BannerButtonClose);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.BannerImage);

        BannerText.setVisibility(GONE);
        BannerKing.setText("ALVA ADS");

        final CountDownTimer timer2 = new CountDownTimer((Long) jsonObject.get("ad_show_time") * 1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                LoadingStringRequest();
            }
        };

        final CountDownTimer timer = new CountDownTimer((Long) jsonObject.get("ad_delay_time") * 1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                rootView.setVisibility(VISIBLE);
                requestLayout();
                timer2.start();
            }

        }.start();

        CloseBanner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                timer2.cancel();
                LoadingStringRequest();
            }
        });

        imageView.setOnClickListener(new OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) jsonObject.get("ad_client_url")));
                intentContext.startActivity(browserIntent);
                String urlClick =
                        "http://adlibtech.ru/adv/bserv.php?action=setAdClick&" +
                        "ad_id="+jsonObject.get("ad_id") +
                        "&ls_id="+ jsonObject.get("last_stat_id") ;
                         Log.d("Click",urlClick);
                StringRequest ClickReq = new StringRequest(Request.Method.GET,urlClick,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Log.d("Click",s);
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
                queue.add(ClickReq);

            }

        });

    }

    private void LoadingStringRequest(){
        rootView.setVisibility(GONE);
        banerReq = new StringRequest(Request.Method.GET,url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        JSONParser jsonParser = new JSONParser();

                        try {
                            Object object = jsonParser.parse(s);

                            jsonObject = (JSONObject) object;

                            Log.wtf(TAG, "onResponse: "+s);

                           if (jsonObject.get("ad_type").equals("image")){ LoadingImage();}


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

    private void LoadingImage(){

        String imageUrl = (String) jsonObject.get("ad_image");
        imageUrl = imageUrl.replace("http://localhost/kukuruznik", HOST_NAME);

        ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                init();
                ImageView imageView = (ImageView) rootView.findViewById(R.id.BannerImage);
                imageView.setImageBitmap(bitmap);
                imageView.getLayoutParams().width = bitmap.getWidth()*2;
                imageView.getLayoutParams().height = bitmap.getHeight()*2;
                imageView.requestLayout();
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("Response", "Не полетели! Candidats");
            }
        });
        queue.add(imageRequest);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        Log.d(TAG, "onMeasure:" + width + "x" + height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = (r - l);
            final int height = (b - t);

            int previewWidth = width;
            int previewHeight = height;
            if ((mWidth != 0) && (mHeight != 0)) {
                previewWidth = mWidth;
                previewHeight = mHeight;
            }
            Log.d(TAG, "onLayout L1: Desired:" + mWidth + "x" + mHeight + " Actual:" + previewWidth + "x" + previewHeight);


            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight)/2 ,
                        width , (height + scaledChildHeight)/2);
            }
        }
        Log.d(TAG, "onLayout L2:" + l + ", " + t + ", " + r + ", " + b);


    }
}
