package com.yy.adam.qrcode;

import android.app.Application;
import android.content.Context;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.MobileAds;

public class MainApp extends Application{

    public static Context s_GlobalCtx;

    @Override
    public void onCreate() {
        super.onCreate();
        s_GlobalCtx = this;

        someInit();
    }

    private void someInit() {
        initFlur();
        initAd();
    }

    private void initFlur() {
        new FlurryAgent.Builder()
                .build(this, "7PGYWWCTZS9VFDJR8YNB");
    }

    private void initAd() {
        MobileAds.initialize(this, "ca-app-pub-5644941632262899~9662224413");
    }
}
