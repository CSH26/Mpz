package com.example.tj.mpz.Music;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.tj.mpz.R;

public class MusicDataActivity extends AppCompatActivity {
    private final String TAG = "MusicDataActivity";
    SeekBar volumeSeek;
    SeekBar durationSeek;
    TextView volumeInfo;
    AudioManager audioManager;
    ImageView volumeImage;
    boolean isQuite = false;
    int pastVolume = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_data);

        volumeImage = (ImageView)findViewById(R.id.volumeImage);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        volumeInfo = (TextView)findViewById(R.id.volumeInfo);

        SeekBarOnChangeListener seekBarOnChangeListener = new SeekBarOnChangeListener();
        volumeSeek = (SeekBar)findViewById(R.id.volumeSeek);
        durationSeek = (SeekBar)findViewById(R.id.durationSeek);
        volumeInfo = (TextView)findViewById(R.id.volumeInfo);

        initVolumeSeekBar();
        volumeSeek.setContentDescription("V");
        durationSeek.setContentDescription("D");
        volumeSeek.setOnSeekBarChangeListener(seekBarOnChangeListener);
        durationSeek.setOnSeekBarChangeListener(seekBarOnChangeListener);

        Intent intent = getIntent();
        Bundle musicBundle = intent.getExtras();
        int musicPosition = musicBundle.getInt("MUSIC_ID");

    }

    public void initVolumeSeekBar(){
        volumeInfo.setText(Integer.toString(getVolume()));
        volumeSeek.setProgress(getVolume());
    }
    private class SeekBarOnChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(seekBar.getContentDescription().equals("V")){
                setVolume(progress);
                volumeInfo.setText(Integer.toString(progress));
            }else {
                Log.d(TAG,"듀레이션시크바가 이동하였습니다.");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            volumeImage.setImageResource(R.drawable.volume_control);
            isQuite = false;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if(seekBar.getContentDescription().equals("V")){
                setVolume(seekBar.getProgress());
            }else {
                Log.d(TAG,"듀레이션시크바가 이동하였습니다.");
            }
        }
    }

    public void setVolume(int p){
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,p, AudioManager.FLAG_PLAY_SOUND);
    }

    public int getVolume(){
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void beQuite(View view){
        if(isQuite){
            volumeImage.setImageResource(R.drawable.volume_control);
            setVolume(pastVolume);
            volumeSeek.setProgress(pastVolume);
            isQuite = false;
        }else {
            volumeImage.setImageResource(R.drawable.volume_control_zero);
            pastVolume = getVolume();
            volumeSeek.setProgress(0);
            setVolume(0);
            isQuite = true;
        }
    }
}
