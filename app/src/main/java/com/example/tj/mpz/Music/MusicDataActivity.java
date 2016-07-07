package com.example.tj.mpz.Music;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.tj.mpz.R;

import java.io.File;
import java.io.IOException;

public class MusicDataActivity extends AppCompatActivity implements View.OnClickListener, Runnable, MediaPlayer.OnCompletionListener{
    private final String TAG = "MusicDataActivity";
    MediaPlayer mediaPlayer;
    SeekBar volumeSeek, durationSeek;
    TextView volumeInfo;
    AudioManager audioManager;
    ImageView volumeImage, playImage, mr_RewindButton, mr_FastForwordButton, mr_StopButton;

    boolean isQuite = false;
    int pastVolume = 0;

    String selection;
    String audiofilepath = "Not Found File Path";
    ContentResolver contentResolver;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_data);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        contentResolver = getContentResolver();

        mr_FastForwordButton = (ImageView)findViewById(R.id.mr_fast_forword_button);
        mr_RewindButton = (ImageView)findViewById(R.id.mr_rewind_button);
        mr_StopButton = (ImageView)findViewById(R.id.mr_stop_button);
        playImage = (ImageView)findViewById(R.id.playImage);
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
        long musicPosition = musicBundle.getLong("MUSIC_POSITION");
        selection = MediaStore.Audio.Media._ID+" = \'"+Long.toString(musicPosition)+"\'";
        cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, null, null);
        initFileSetting();
        setListener();
        try{
            new Thread(MusicDataActivity.this).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setListener(){
        playImage.setOnClickListener(this);
        mr_FastForwordButton.setOnClickListener(this);
        mr_RewindButton.setOnClickListener(this);
        mr_StopButton.setOnClickListener(this);
    }

    public void initFileSetting(){
        cursor.moveToFirst();
        int fileColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        audiofilepath = cursor.getString(fileColumn);
        durationSeek.setProgress(0);
        try{
            mediaPlayer.setDataSource(audiofilepath);
            mediaPlayer.prepare();
            durationSeek.setMax(mediaPlayer.getDuration());
            DurationCalc durationCalc = new DurationCalc(mediaPlayer.getDuration());
            Log.d(TAG,"초로 변환한 단위는 "+durationCalc.excute());
        }catch (Exception e){
            e.printStackTrace();
        }
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
                mediaPlayer.seekTo(seekBar.getProgress());
                Log.d(TAG,"듀레이션 시크바가 멈출때의 값은 "+seekBar.getProgress());
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

    public void onClick(View view){
        switch (view.getId()){
            case R.id.playImage:
                if(mediaPlayer.isPlaying()){
                    playImage.setImageResource(R.drawable.play_button);
                    mediaPlayer.pause();
                }else {
                    playImage.setImageResource(R.drawable.pause_button);
                    mediaPlayer.start();
                }
                break;
            case R.id.mr_rewind_button:
                mediaPlayer.seekTo(0);
                durationSeek.setProgress(0);
                mediaPlayer.start();
                break;
            case R.id.mr_fast_forword_button:
                int duration = mediaPlayer.getDuration();
                int current = mediaPlayer.getCurrentPosition();
                if(duration-current < 10000){
                   mediaPlayer.seekTo(duration);
                }else {
                   mediaPlayer.seekTo(current+10000);
                }
                break;
            case R.id.mr_stop_button:
                try{
                    playImage.setImageResource(R.drawable.play_button);
                    mediaPlayer.stop();
                    mediaPlayer.prepare();
                    mediaPlayer.seekTo(0);
                    durationSeek.setProgress(0);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    public void run() {
        int current = 0;
        while (mediaPlayer != null){
            try{
                Thread.sleep(1000);
                current = mediaPlayer.getCurrentPosition();
                if(mediaPlayer.isPlaying()){
                    durationSeek.setProgress(current);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playImage.setImageResource(R.drawable.play_button);
        mediaPlayer.seekTo(0);
        durationSeek.setProgress(0);
    }
}
