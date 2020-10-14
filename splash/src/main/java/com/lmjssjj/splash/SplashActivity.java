package com.lmjssjj.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.lmjssjj.annotation.BindPath;

@BindPath("splash/splash")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}
