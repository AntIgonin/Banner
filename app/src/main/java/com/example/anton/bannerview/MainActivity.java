package com.example.anton.bannerview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.anton.Banner.BannerView;

public class MainActivity extends AppCompatActivity {
    BannerView bannerView;
    private static final String TAG = "asdads";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lvMain);
        bannerView = new BannerView(MainActivity.this,linearLayout);
        bannerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        linearLayout.addView(bannerView);
        bannerView.start();

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerView.stop();
            }
        });
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerView.start();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (bannerView.checkRun() != true){bannerView.start();}
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        bannerView.stop();
        Log.i(TAG, "onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();


        Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy()");
    }


}
