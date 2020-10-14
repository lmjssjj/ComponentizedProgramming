package com.lmjssjj.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.lmjssjj.annotation.BindPath;
import com.lmjssjj.arouter.ARouter;

@BindPath("login/login")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void jump(View view) {
        ARouter.getInstance().jumpActvity(this,"app/main", null);
    }
}
