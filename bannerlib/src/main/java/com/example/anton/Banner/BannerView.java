package com.example.anton.Banner;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.util.AttributeSet;
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
import com.example.bannerlib.R;

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

    private int mPaddingLeft;

    private int mPaddingRight;

    private int mPaddingTop;

    private int mPaddingBottom;

    boolean isRunning = false;
    protected boolean IT_IS_DELAY = true;
    LayoutInflater inflater;
    protected CountDownTimer bannerTimer;

    public BannerView(Context context) {
        super(context);
        setWillNotDraw(false);

        intentContext = context;
        queue = Volley.newRequestQueue(context);
        this.viewGroup = viewGroup;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.addView(inflater.inflate(R.layout.content_banner_view, null));



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

       setVisibility(INVISIBLE);

    }

    private void init(){
        TextView BannerText = (TextView) findViewById(R.id.BannerText);
        TextView BannerKing = (TextView) findViewById(R.id.BannerKing);
        ImageButton CloseBanner = (ImageButton) findViewById(R.id.BannerButtonClose);
        ImageView imageView = (ImageView) findViewById(R.id.BannerImage);

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
                setVisibility(INVISIBLE);
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
                ImageView imageView = (ImageView) findViewById(R.id.BannerImage);
                imageView.getLayoutParams().width = Integer.parseInt(bannerConst.width());
                imageView.getLayoutParams().height = makeHeight(bitmap);
                imageView.setImageBitmap(bitmap);
                imageView.requestLayout();
                requestLayout();

            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("Response", "Error.BannerImage");
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
        int count = getChildCount();

        int maxHeight = 0;

        int maxWidth = 0;

        // Find out how big everyone wants to be
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        // Find rightmost and bottom-most child

        for (int i = 0; i < count; i++) {

            View child = getChildAt(i);

            if (child.getVisibility() != GONE) {

                int childRight;

                int childBottom;

                BannerView.LayoutParams lp

                        = (BannerView.LayoutParams) child.getLayoutParams();

                childRight = lp.x + child.getMeasuredWidth();

                childBottom = lp.y + child.getMeasuredHeight();

                maxWidth = Math.max(maxWidth, childRight);

                maxHeight = Math.max(maxHeight, childBottom);

            }

        }

        // Account for padding too

        maxWidth += mPaddingLeft + mPaddingRight;

        maxHeight += mPaddingTop + mPaddingBottom;

        // Check against minimum height and width

        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());

        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),

                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {

            View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                BannerView.LayoutParams lp =
                        (BannerView.LayoutParams) child.getLayoutParams();



                int childLeft = mPaddingLeft + lp.x;
                int childTop = mPaddingTop + lp.y;
                child.layout(childLeft, childTop,

                        childLeft + child.getMeasuredWidth(),

                        childTop + child.getMeasuredHeight());



            }

        }

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
                setVisibility(INVISIBLE);
                LoadingStringRequest();
                destTimer();
                IT_IS_DELAY = true;
            }else {
                setVisibility(VISIBLE);
                requestLayout();
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
      setVisibility(INVISIBLE);
      isRunning = true;
      LoadingStringRequest();
  }

  public void stop(){
      Log.d("BannerView", "Stop");
      setVisibility(GONE);
      destTimer();
      isRunning = false;
  }
  public boolean getIsRunning(){
      return isRunning;
  }

  public void setIsRunning(boolean isRunning){
        this.isRunning = isRunning;
    }

    @Override

    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {

        return new BannerView.LayoutParams(getContext(), attrs);

    }
    @Override

    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {

        return p instanceof BannerView.LayoutParams;

    }
    @Override

    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {

        return new LayoutParams(p);

    }

    @Override

    public boolean shouldDelayChildPressedState() {

        return false;

    }


    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int x;
        public int y;
        public LayoutParams(int width, int height, int x, int y) {

            super(width, height);

            this.x = x;

            this.y = y;

        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.BannerView);
            x = a.getDimensionPixelOffset(
                    R.styleable.BannerView_layout_x, 0);
            y = a.getDimensionPixelOffset(
                    R.styleable.BannerView_layout_y, 0);
            a.recycle();

        }


        public LayoutParams(ViewGroup.LayoutParams source) {

            super(source);

        }



    }


}
