package com.example.tj.mpz;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private boolean isPageOpen = false;
    private Animation listSelectedAnim;
    private Animation nonlistSelectedAnim;
    private SlidingPageAnimationListener animationListener;
    private LinearLayout slidingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listSelectedAnim = AnimationUtils.loadAnimation(this,R.anim.translate_selected_list);
        nonlistSelectedAnim = AnimationUtils.loadAnimation(this,R.anim.translate_nonselected_list);

        animationListener = new SlidingPageAnimationListener(getApplicationContext(), isPageOpen);
        animationListener.setAnim(listSelectedAnim, nonlistSelectedAnim);
        animationListener.setAnimationLayout(slidingLayout);
    }

    public void anim(View view){
        if(isPageOpen){
            slidingLayout.startAnimation(nonlistSelectedAnim);

        }else
        {
            Log.d(TAG,"뜨냐");
            slidingLayout.setVisibility(View.VISIBLE);
            slidingLayout.startAnimation(listSelectedAnim);

        }
    }

    private class SlidingPageAnimationListener implements Animation.AnimationListener{

        private Animation listSelectedAnim;
        private Animation nonlistSelectedAnim;
        private Context context;
        private boolean isPageOpen;
        private LinearLayout slidingpage;

        public SlidingPageAnimationListener(Context context, Boolean isPaageOpen) {
            this.context = context;
            this.isPageOpen = isPaageOpen;
        }

        public void setAnimationLayout(LinearLayout target){
            this.slidingpage = target;
        }

        public void setAnim(Animation anim1, Animation anim2){
            anim1.setAnimationListener(this);
            anim2.setAnimationListener(this);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(isPageOpen){
                slidingpage.setVisibility(View.INVISIBLE);
                isPageOpen = false;
            }
            else
            {
                isPageOpen = true;
            }
        }
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

    }
}
