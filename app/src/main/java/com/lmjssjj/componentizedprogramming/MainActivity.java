package com.lmjssjj.componentizedprogramming;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.lmjssjj.annotation.BindPath;
import com.lmjssjj.arouter.ARouter;

@BindPath("app/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jump(View view) {
        ARouter.getInstance().jumpActvity(this,"login/login", null);
    }
}