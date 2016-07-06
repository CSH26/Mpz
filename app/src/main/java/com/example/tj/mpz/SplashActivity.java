package com.example.tj.mpz;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try{
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }

        finish();
    }
}
