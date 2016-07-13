package com.example.tj.mpz.Music;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tj.mpz.R;

public class MusicListActivity extends AppCompatActivity {

    private final String TAG = "MusicListActivity";

    private Button startButton;
    ContentResolver contentResolver;
    Cursor cursor;
    ListView musicList;
    Animation showAnim;
    Animation behindAnim;
    boolean isPageOpen = false;
    LinearLayout slidingLayout;
    private int forwordPosition = -1;
    private int previousPosition = -1;
    TextView countView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        slidingLayout = (LinearLayout)findViewById(R.id.slidingLayout);
        showAnim = AnimationUtils.loadAnimation(this,R.anim.translate_selected_list);
        behindAnim = AnimationUtils.loadAnimation(this,R.anim.translate_nonselected_list);
        behindAnim.setFillAfter(true);
        SlidingPageAnimationListener animationListener = new SlidingPageAnimationListener();
        showAnim.setAnimationListener(animationListener);
        behindAnim.setAnimationListener(animationListener);

         /* 뷰 셋팅 */
        startButton = (Button)findViewById(R.id.startButton);

         /* 리스트 셋팅 */
        musicList = (ListView)findViewById(R.id.musicList);
        final View header = getLayoutInflater().inflate(R.layout.listview_header,null,false);

        contentResolver = getContentResolver();
        cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        final MusicListAdapter musicListAdapter = new MusicListAdapter(this , cursor);
        musicListAdapter.showList();
        musicList.setAdapter(musicListAdapter);
        musicList.addHeaderView(header);
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == getPreviousPosition()){
                    if(isPageOpen){
                        slidingLayout.startAnimation(behindAnim);
                        setPreviousPosition(position);
                    }
                    else{
                        slidingLayout.setVisibility(View.VISIBLE);
                        slidingLayout.startAnimation(showAnim);
                        setPreviousPosition(position);
                        setPosition(position);
                    }
                }else {
                    slidingLayout.setVisibility(View.VISIBLE);
                    slidingLayout.startAnimation(showAnim);
                    setPreviousPosition(position);
                    setPosition(position);
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(forwordPosition != -1) {
                    Intent musicIntent = new Intent(getApplicationContext(), MusicDataActivity.class);
                    musicIntent.putExtra("MUSIC_POSITION",musicListAdapter.getItemId(getForwordPosition()-1));
                    musicIntent.putExtra("MUSIC_TITLE",musicListAdapter.getItem(getForwordPosition()-1).getData(0));
                    startActivity(musicIntent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"음악을 선택해 주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        countView = (TextView)header.findViewById(R.id.count);
        countView.setText("곡수 : "+musicListAdapter.getCount());
    }

    public void setPosition(int p){
        forwordPosition = p;
    }

    public int getForwordPosition() {
        return forwordPosition;
    }

    public int getPreviousPosition() {
        return previousPosition;
    }

    public void setPreviousPosition(int previousPosition) {
        this.previousPosition = previousPosition;
    }

    private class SlidingPageAnimationListener implements Animation.AnimationListener{
        @Override
        public void onAnimationEnd(Animation animation) {

            if(isPageOpen){
                isPageOpen = false;
            }
            else {
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
