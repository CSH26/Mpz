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
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tj.mpz.R;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MusicDataActivity extends AppCompatActivity implements View.OnClickListener, Runnable, MediaPlayer.OnCompletionListener{

    private final String TAG = "MusicDataActivity";
    DialogView dialogView;
    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder; //녹음 부
    SeekBar volumeSeek, durationSeek;
    TextView volumeInfo, startDurationText, endDurationText;
    AudioManager audioManager;
    ImageButton at_a_time_Button;
    ImageView volumeImage, playImage, mr_RewindButton, mr_FastForwordButton, mr_StopButton;
    Button record_start, record_stop, record_save, record_play;
    boolean isQuite = false;
    int pastVolume = 0, maxSecond, runSecond = 0, runMinute = 0;
    boolean isRecording = false, checkBeforeSave = false;
    String selection, second, minute, fileAccess;
    String audiofilepath = "Not Found File Path", savedFilePath, baseFilePath, musicFormat;
    ContentResolver contentResolver;
    Cursor cursor;
    Context context;
    DurationCalc durationCalc;
    Handler handler;
    AlertDialogClickListener alertDialogClickListener;
    AlertDialog.Builder aBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_data);

        fileAccess = Environment.getExternalStorageState();

        aBuilder = new AlertDialog.Builder(MusicDataActivity.this);
        alertDialogClickListener = new AlertDialogClickListener();
        dialogView = new DialogView(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        contentResolver = getContentResolver();
        savedFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/myRecord.3gp";
        musicFormat = ".3gp";
        at_a_time_Button = (ImageButton)findViewById(R.id.at_a_time_Button);
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

        createAlertDialog();

        Intent intent = getIntent();
        Bundle musicBundle = intent.getExtras();
        long musicPosition = musicBundle.getLong("MUSIC_POSITION");
        selection = MediaStore.Audio.Media._ID+" = \'"+Long.toString(musicPosition)+"\'";
        cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, null, null);
        initFileSetting();
        initRecordButton();
        setListener();

        if (fileAccess.equals(Environment.MEDIA_MOUNTED)) {
            baseFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            baseFilePath = Environment.MEDIA_UNMOUNTED;
        }

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

    public void initRecordButton(){
        record_start.setEnabled(true);
        record_stop.setEnabled(true);
        record_play.setEnabled(false);
        record_save.setEnabled(true);
    }

    public void setListener(){
        at_a_time_Button.setOnClickListener(this);
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

        switch (view.getId()) {
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
                if (duration - current < 10000) {
                    mediaPlayer.seekTo(duration);
                } else {
                    mediaPlayer.seekTo(current + 10000);
                }
                break;
            case R.id.mr_stop_button:
                mrSetZeroPosition();
                break;
            case R.id.record_start:
                recordStart();
                break;
            case R.id.record_stop:
                if (isRecording) {
                    if (mediaPlayer.isPlaying()) {
                        mrSetZeroPosition();    // pause 기능 추가 할 것
                    }
                    isRecording = false;
                    setRecordButtonEnabled(true, false, true, true);
                    mediaRecorder.stop();
                }
                break;
            case R.id.record_play:
                Intent recordIntent = new Intent(getApplicationContext(), RecordCheckActivity.class);
                recordIntent.putExtra("RECORD_FILE_PATH", savedFilePath);
                startActivity(recordIntent);
                break;
            case R.id.record_save:
                if (checkBeforeSave) {
                    aBuilder.show();
                } else {
                    Toast.makeText(getApplicationContext(), "저장하기전에 Record를 먼저 진행하세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.at_a_time_Button:
                mrStart();
                recordStart();
                break;
        }
    }

    public void fileSave(String FilePath){
        if(baseFilePath.equals(Environment.MEDIA_UNMOUNTED)){
            Log.d(TAG,"SD카드에 접근 할 수 없습니다.");
        }else {
            FileInputStream fis;
            BufferedReader br;
            try {
                fis = new FileInputStream(savedFilePath);
                br = new BufferedReader(new InputStreamReader(fis));
            }catch (IOException e){
                e.printStackTrace();
            }

            //mediaRecorder.setOutputFile(baseFilePath+"/"+FilePath+musicFormat);
            Log.d(TAG,"SD카드에 접근 할 수 있습니다.");
            Log.d(TAG,"파일 이름은 "+baseFilePath+"/"+FilePath+musicFormat);
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
    public void recordStart(){
        isRecording = true;
        checkBeforeSave = true;
        setRecordButtonEnabled(false, true, false, false);

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(savedFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
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

    public void setRecordButtonEnabled(boolean startBtn, boolean stopBtn, boolean playBtn, boolean saveBtn){
        record_start.setEnabled(startBtn);
        record_stop.setEnabled(stopBtn);
        record_play.setEnabled(playBtn);
        record_save.setEnabled(saveBtn);
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
                case -2:  // 취소버튼이 눌렸을 때
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
