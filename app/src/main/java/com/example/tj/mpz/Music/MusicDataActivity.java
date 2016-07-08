package com.example.tj.mpz.Music;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.tj.mpz.R;

public class MusicDataActivity extends AppCompatActivity implements View.OnClickListener, Runnable, MediaPlayer.OnCompletionListener{
    private final String TAG = "MusicDataActivity";
    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder; //녹음 부
    SeekBar volumeSeek, durationSeek;
    TextView volumeInfo, startDurationText, endDurationText;
    AudioManager audioManager;
    ImageView volumeImage, playImage, mr_RewindButton, mr_FastForwordButton, mr_StopButton;
    Button record_start, record_stop, record_save, record_play;
    boolean isQuite = false;
    int pastVolume = 0, maxSecond, runSecond = 0, runMinute = 0;
    boolean isRecording = false;
    String selection, second, minute;
    String audiofilepath = "Not Found File Path", savedFilePath;
    ContentResolver contentResolver;
    Cursor cursor;

    DurationCalc durationCalc;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_data);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        contentResolver = getContentResolver();
        savedFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/myRecord.3gp";
        mr_FastForwordButton = (ImageView)findViewById(R.id.mr_fast_forword_button);
        mr_RewindButton = (ImageView)findViewById(R.id.mr_rewind_button);
        mr_StopButton = (ImageView)findViewById(R.id.mr_stop_button);
        playImage = (ImageView)findViewById(R.id.playImage);
        volumeImage = (ImageView)findViewById(R.id.volumeImage);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        volumeInfo = (TextView)findViewById(R.id.volumeInfo);
        startDurationText = (TextView)findViewById(R.id.startDurationText);
        endDurationText = (TextView)findViewById(R.id.endDurationText);

        record_start = (Button)findViewById(R.id.record_start);
        record_stop = (Button)findViewById(R.id.record_stop);
        record_save = (Button)findViewById(R.id.record_save);
        record_play = (Button)findViewById(R.id.record_play);

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

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(runSecond < 10)
                    startDurationText.setText("0"+runMinute+":0"+runSecond);
                else if(runSecond < 60)
                    startDurationText.setText("0"+runMinute+":"+runSecond);

                if(runMinute>=10){
                    if(runSecond < 10)
                        startDurationText.setText(runMinute+":0"+runSecond);
                    else if(runSecond < 60)
                        startDurationText.setText(runMinute+":"+runSecond);
                }

            }
        };
    }

    public void setListener(){
        record_play.setOnClickListener(this);
        record_save.setOnClickListener(this);
        record_stop.setOnClickListener(this);
        record_start.setOnClickListener(this);
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
            durationCalc = new DurationCalc(mediaPlayer.getDuration());
            durationCalc.excute();
            maxSecond = durationCalc.getMaxSecond();
            second = durationCalc.getsSecond();
            minute = durationCalc.getsMinute();
            startDurationText.setText("00:00");
            endDurationText.setText(minute+":"+second);
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
                    startDurationText.setText("00:00");
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.record_start:
                isRecording = true;
                record_start.setEnabled(false);
                record_play.setEnabled(false);
                record_save.setEnabled(false);
                try{
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setOutputFile(savedFilePath);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mediaRecorder.prepare();
                }catch (Exception e){
                    e.printStackTrace();
                }
                mediaRecorder.start();
                break;
            case R.id.record_stop:
                record_stop.setEnabled(false);
                record_play.setEnabled(true);
                record_save.setEnabled(true);
                record_start.setEnabled(true);
                mediaRecorder.stop();
                break;
            case R.id.record_play:
                record_stop.setEnabled(true);
                record_play.setEnabled(false);
                record_start.setEnabled(false);
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(savedFilePath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                break;
            case R.id.end:
                try{
                    playImage.setImageResource(R.drawable.play_button);
                    mediaPlayer.stop();
                    mediaPlayer.prepare();
                    mediaPlayer.seekTo(0);
                    durationSeek.setProgress(0);
                    startDurationText.setText("00:00");
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    public void run() {
        int current = 0;
        runSecond = 0;
       // DurationCalc runDuration = new DurationCalc();
        while (mediaPlayer != null){
            try{
                Thread.sleep(1000);
                current = mediaPlayer.getCurrentPosition();
                if(mediaPlayer.isPlaying()){
                    durationSeek.setProgress(current);
                    setDurationPosition(current);
                    handler.sendEmptyMessage(0);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void setDurationPosition(int cur){
        durationCalc.setDuration(cur);
        runSecond = durationCalc.getDurationCalc();
        runMinute = 0;
        while(true){
            if(runSecond >= 60){
                runMinute++;
                runSecond -= 60;
            }
            else{
                break;
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playImage.setImageResource(R.drawable.play_button);
        mediaPlayer.seekTo(0);
        durationSeek.setProgress(0);
        startDurationText.setText("00:00");
    }

    @Override
    public void finish() {
        super.finish();
        mediaPlayer.stop();
    }
}
