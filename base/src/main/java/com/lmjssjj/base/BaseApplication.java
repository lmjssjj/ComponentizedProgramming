package com.lmjssjj.base;

import android.app.Application;

import com.lmjssjj.arouter.ARouter;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.getInstance().init(this);
    }
}
