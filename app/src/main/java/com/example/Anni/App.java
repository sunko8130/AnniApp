package com.example.Anni;

import android.app.Application;
import android.content.Context;

import me.myatminsoe.mdetect.MDetect;

public class App extends Application {

    private static final String TAG = App.class.getSimpleName();
    private static Context context;
    public static MDetect mDetect = MDetect.INSTANCE;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mDetect.init(context);
    }
    public static Context getContext() {
        return context;
    }
}
