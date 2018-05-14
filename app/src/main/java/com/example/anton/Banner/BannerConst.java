package com.example.anton.Banner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;


public class BannerConst {


    Context context;
    ApplicationInfo applicationInfo;
    TelephonyManager telephonyManager;
    DisplayMetrics displayMetrics;


    long startTime;
    long endTime;
    private long takenTime;


    public BannerConst(Context context) {

        this.context = context;


        applicationInfo = context.getApplicationInfo();
        telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

        displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

    }

    public String getName() {
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public String getBundle() {
        return context.getPackageName();
    }

    public String getVersion() {

        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getdeviceUuid() {

        String deviceUuid = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return deviceUuid;
    }

    public String getdeviceName() {

        String name = Build.MANUFACTURER;

        return name;
    }

    public String getdeviceModel() {

        //String model = Build.MODEL;

        return "Android_SDK";
    }

    public String getdeviceSystemName() {

        Field[] fields = Build.VERSION_CODES.class.getFields();
        String osName = fields[Build.VERSION.SDK_INT + 1 ].getName();

        return osName;
    }

    public String getdeviceSystemVersion() {
        String version = String.valueOf(Build.VERSION.SDK_INT);
        return version;
    }

    public String width() {
        return String.valueOf(displayMetrics.widthPixels);
    }


    public String height() {
        return String.valueOf(displayMetrics.heightPixels);
    }


    public String getnetType() {
        return null;
    }



}
