package com.example.tj.mpz.Music;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tj.mpz.R;

public class RecordCheckActivity extends AppCompatActivity implements View.OnClickListener, Runnable, MediaPlayer.OnCompletionListener {

    private final String TAG = "RecordCheckActivity";
    MediaPlayer mediaPlayer;

    AlertDialogClickListener alertDialogClickListener;
    AlertDialog.Builder aBuilder;
    DialogView dialogView;
    SeekBar volumeSeek, durationSeek;
    TextView volumeInfo, startDurationText, endDurationText;
    AudioManager audioManager;
    ImageView volumeImage, playImage, mr_RewindButton, mr_FastForwordButton, mr_StopButton;
    ImageButton saveButton;
    boolean isQuite = false;
    int pastVolume = 0, maxSecond, runSecond = 0, runMinute = 0;

    String second, minute;
    String audiofilepath = "Not Found File Path", savedFilePath;

    DurationCalc durationCalc;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_check);

        alertDialogClickListener = new AlertDialogClickListener();
        aBuilder = new AlertDialog.Builder(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);

        saveButton = (ImageButton)findViewById(R.id.saveButton);
        dialogView = new DialogView(this);
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

        SeekBarOnChangeListener seekBarOnChangeListener = new SeekBarOnChangeListener();
        volumeSeek = (SeekBar)findViewById(R.id.volumeSeek);
        durationSeek = (SeekBar)findViewById(R.id.durationSeek);
        volumeInfo = (TextView)findViewById(R.id.volumeInfo);

        initVolumeSeekBar();
        volumeSeek.setContentDescription("V");
        durationSeek.setContentDescription("D");
        volumeSeek.setOnSeekBarChangeListener(seekBarOnChangeListener);
        durationSeek.setOnSeekBarChangeListener(seekBarOnChangeListener);
        createAlertDialog();

        Intent intent = getIntent();
        Bundle recordBundle = intent.getExtras();
        String record_file_path = recordBundle.getString("RECORD_FILE_PATH");
        initFileSetting(record_file_path);
        setListener();

        try{
            new Thread(RecordCheckActivity.this).start();
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

    public void fileSave(String FilePath){

    }

    public void setListener(){
        saveButton.setOnClickListener(this);
        playImage.setOnClickListener(this);
        mr_FastForwordButton.setOnClickListener(this);
        mr_RewindButton.setOnClickListener(this);
        mr_StopButton.setOnClickListener(this);
    }

    public void initFileSetting(String path){

        audiofilepath = path;
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
                mrStart();
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
                mrSetZeroPosition();
                break;
            case R.id.saveButton:
                aBuilder = new AlertDialog.Builder(this);
                aBuilder.show();
                break;
        }
    }

    public void run() {
        int current = 0;
        runSecond = 0;

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

    public void mrSetZeroPosition(){
        try {
            playImage.setImageResource(R.drawable.play_button);
            mediaPlayer.stop();
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            durationSeek.setProgress(0);
            startDurationText.setText("00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mrStart(){
        if (mediaPlayer.isPlaying()) {
            playImage.setImageResource(R.drawable.play_button);
            mediaPlayer.pause();
        } else {
            playImage.setImageResource(R.drawable.pause_button);
            mediaPlayer.start();
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

    private class AlertDialogClickListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (which){
                case -1:
                    if(!dialogView.getFileName().equals("")) {
                        Log.d(TAG, "파일 저장 이름은 " + dialogView.getFileName());
                        fileSave(dialogView.getFileName());
                        Toast.makeText(getApplicationContext(), "저장 되었습니다.", Toast.LENGTH_SHORT).show();

                        if (dialogView != null)
                        {
                            ViewGroup parent = (ViewGroup) dialogView.getParent();
                            if (parent != null)
                            {
                                parent.removeView(dialogView);
                            }
                        }

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "파일 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                        if (dialogView != null)
                        {
                            ViewGroup parent = (ViewGroup) dialogView.getParent();
                            if (parent != null)
                            {
                                parent.removeView(dialogView);
                            }
                        }
                    }
                    break;
                case -2:
                    if (dialogView != null)
                    {
                        ViewGroup parent = (ViewGroup) dialogView.getParent();
                        if (parent != null)
                        {
                            parent.removeView(dialogView);
                        }
                    }
                    break;
            }
        }
    }

    public void createAlertDialog(){
        aBuilder.setTitle("Save Box");
        aBuilder.setView(dialogView.getDialogView());
        aBuilder.setPositiveButton("저장", alertDialogClickListener);
        aBuilder.setNegativeButton("취소", alertDialogClickListener);

    }

}
