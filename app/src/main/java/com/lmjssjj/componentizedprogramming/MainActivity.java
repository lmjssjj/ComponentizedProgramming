package com.lmjssjj.componentizedprogramming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lmjssjj.annotation.BindPath;
import com.lmjssjj.arouter.ARouter;
import com.lmjssjj.common.LiveDataBus;

@BindPath("app/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LiveDataBus.BusMutableLiveData<Boolean> isLoginLiveData = LiveDataBus.getInstance().with("isLogin", Boolean.class);
        isLoginLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Toast.makeText(MainActivity.this, aBoolean + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void jump(View view) {
        ARouter.getInstance().jumpActvity(this, "login/login", null);
    }
}