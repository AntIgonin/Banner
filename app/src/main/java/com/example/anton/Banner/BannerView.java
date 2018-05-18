package com.example.anton.Banner;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
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
    boolean isRunning = false;
    protected boolean IT_IS_DELAY = true;

    protected CountDownTimer bannerTimer;

    public BannerView(Context context, ViewGroup viewGroup) {
        super(context);
        setWillNotDraw(false);

        intentContext = context;
        queue = Volley.newRequestQueue(context);
        this.viewGroup = viewGroup;
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

        Log.d("URL",url);


    }

    private void init(){
        TextView BannerText = (TextView) rootView.findViewById(R.id.BannerText);
        TextView BannerKing = (TextView) rootView.findViewById(R.id.BannerKing);
        ImageButton CloseBanner = (ImageButton) rootView.findViewById(R.id.BannerButtonClose);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.BannerImage);

        createdTimer((Long) jsonObject.get("ad_delay_time"));

        BannerText.setVisibility(GONE);
        BannerKing.setText("ALVA ADS");


        BannerKing.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(intentContext, WebActivity.class);
                intent.putExtra("web", "http://alvastudio.com");
                intentContext.startActivity(intent);
            }
        });

        CloseBanner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                destTimer();
                rootView.setVisibility(INVISIBLE);
                LoadingStringRequest();
            }
        });

        imageView.setOnClickListener(new OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent intent = new Intent(intentContext, WebActivity.class);
                intent.putExtra("web", String.valueOf(jsonObject.get("ad_client_url")));
                intentContext.startActivity(intent);


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
        if(isRunning) {
            banerReq = new StringRequest(Request.Method.GET, url,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {

                            JSONParser jsonParser = new JSONParser();

                            try {
                                Object object = jsonParser.parse(s);
                                jsonObject = (JSONObject) object;
                                Log.d("Url", s);
                                BannerInfo bannerInfo = new BannerInfo(jsonObject);
                                if (jsonObject.get("ad_type").equals("image")) {
                                    LoadingImage();
                                }
                                init();
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
                    }) {
            };
            queue.add(banerReq);
        }
    }

    private void LoadingImage(){
        String imageUrl = (String) jsonObject.get("ad_image");
        imageUrl = imageUrl.replace("http://localhost/kukuruznik", HOST_NAME);

        ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {

                ImageView imageView = (ImageView) rootView.findViewById(R.id.BannerImage);
                imageView.getLayoutParams().width = Integer.parseInt(bannerConst.width());
                imageView.getLayoutParams().height = makeHeight(bitmap);
                imageView.setImageBitmap(bitmap);
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

    private int makeHeight(Bitmap bitmap){
        double ratio = (double) bitmap.getWidth()/(double) bitmap.getHeight();
        return (int) (Integer.parseInt(bannerConst.width())/ratio);
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

  public String getInfoAboutBanner(String infoName){
        return bannerInfo.getInfoBanner(infoName);
  }

  protected void createdTimer(long time){
      bannerTimer = new CountDownTimer(time * 1000,1000) {
          @Override
          public void onTick(long millisUntilFinished) {}
          @Override
          public void onFinish() {
            if (IT_IS_DELAY == false){
                rootView.setVisibility(INVISIBLE);
                LoadingStringRequest();
                destTimer();
                IT_IS_DELAY = true;
            }else {
                rootView.setVisibility(VISIBLE);
                destTimer();
                IT_IS_DELAY = false;
                createdTimer((Long) jsonObject.get("ad_show_time"));
            }

          }
      }.start();
  }



  protected void destTimer(){
      if (bannerTimer != null) {
          bannerTimer.cancel();
          bannerTimer = null;
      }
  }

  public void start(){
      Log.d("BannerView", "Start");
      rootView.setVisibility(INVISIBLE);
      isRunning = true;
      LoadingStringRequest();
  }

  public void stop(){
      Log.d("BannerView", "Stop");
      rootView.setVisibility(GONE);
      destTimer();
      isRunning = false;
  }
  public boolean checkRun(){
      return isRunning;
  }


}
