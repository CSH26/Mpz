package com.example.tj.mpz;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.tj.mpz.Music.MusicListActivity;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 로딩화면 론칭
        //startActivity(new Intent(this,SplashActivity.class));
        // 리스트화면 론칭
        startActivity(new Intent(this,MusicListActivity.class));
        /*
        slidingLayout = (LinearLayout)findViewById(R.id.slidingLayout);
        listSelectedAnim = AnimationUtils.loadAnimation(this,R.anim.translate_selected_list);
        nonlistSelectedAnim = AnimationUtils.loadAnimation(this,R.anim.translate_nonselected_list);

        animationListener = new SlidingPageAnimationListener();
        listSelectedAnim.setAnimationListener(animationListener);
        nonlistSelectedAnim.setAnimationListener(animationListener);
        */
    }
}
