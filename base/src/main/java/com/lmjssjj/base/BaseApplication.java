package com.lmjssjj.base;

import android.app.Application;

import com.lmjssjj.arouter.ARouter;

public class BaseApplication extends Application {

    public static boolean isApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.getInstance().init(this);
        isApplication = com.lmjssjj.common.BuildConfig.is_application;
    }
}
