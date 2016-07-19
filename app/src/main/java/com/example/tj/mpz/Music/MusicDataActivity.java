package com.example.tj.mpz.Music;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class MusicDataActivity extends AppCompatActivity implements View.OnClickListener, Runnable, MediaPlayer.OnCompletionListener{

    private final int RECORD_CHECK_ACTIVITY_REQUEST_CODE = 2000;
    private final String TAG = "MusicDataActivity";
    DialogView dialogView;
    MediaPlayer mediaPlayer;
    SeekBar volumeSeek, durationSeek;
    TextView volumeInfo, startDurationText, endDurationText, dataTitle;
    AudioManager audioManager;
    ImageButton at_a_time_Button;
    ImageView volumeImage, playImage, mr_RewindButton, mr_FastForwordButton, mr_StopButton;
    boolean isQuite = false, isAtATime = false;
    int pastVolume = 0, maxSecond, runSecond = 0, runMinute = 0;
    MediaRecorder mediaRecorder;
    Button record_start, record_stop, record_save, record_play;
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

        Toast.makeText(getApplicationContext(),"파일 이름은 파일명_Recorded로 저장 됩니다.",Toast.LENGTH_SHORT).show();
        fileAccess = Environment.getExternalStorageState(); // sd카드에 접근 가능한지 체크하는 변수
        aBuilder = new AlertDialog.Builder(MusicDataActivity.this); // save 작동시에 띄워줄 dialog창
        alertDialogClickListener = new AlertDialogClickListener();
        dialogView = new DialogView(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        contentResolver = getContentResolver();
        savedFilePath = Environment.getExternalStorageDirectory().getAbsolutePath(); // myRecord라는 이름으로 녹음파일 저장
        musicFormat = ".3gp";
        at_a_time_Button = (ImageButton)findViewById(R.id.at_a_time_Button);  // 동시 작동 버튼
        mr_FastForwordButton = (ImageView)findViewById(R.id.mr_fast_forword_button); // 빨리 감기 버튼
        mr_RewindButton = (ImageView)findViewById(R.id.mr_rewind_button);   // 되감기 버튼
        mr_StopButton = (ImageView)findViewById(R.id.mr_stop_button);   // mr 정지 버튼
        playImage = (ImageView)findViewById(R.id.playImage);    // 플레이 버튼
        volumeImage = (ImageView)findViewById(R.id.volumeImage);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        volumeInfo = (TextView)findViewById(R.id.volumeInfo);   // 볼륨크기 텍스트
        startDurationText = (TextView)findViewById(R.id.startDurationText); // duration 위치 텍스트
        endDurationText = (TextView)findViewById(R.id.endDurationText); // duration 최종길이 텍스트
        dataTitle = (TextView)findViewById(R.id.dataTitle); // 타이틀 텍스트

        record_start = (Button)findViewById(R.id.record_start);
        record_stop = (Button)findViewById(R.id.record_stop);
        record_save = (Button)findViewById(R.id.record_save);
        record_play = (Button)findViewById(R.id.record_play);

        SeekBarOnChangeListener seekBarOnChangeListener = new SeekBarOnChangeListener();
        volumeSeek = (SeekBar)findViewById(R.id.volumeSeek);
        durationSeek = (SeekBar)findViewById(R.id.durationSeek);
        volumeInfo = (TextView)findViewById(R.id.volumeInfo);

        initVolumeSeekBar();
        // 시크바 리스너에서 볼륨 시크와 듀레이션 시크바를 구별할 디스크립션 설정
        volumeSeek.setContentDescription("V");
        durationSeek.setContentDescription("D");
        volumeSeek.setOnSeekBarChangeListener(seekBarOnChangeListener);
        durationSeek.setOnSeekBarChangeListener(seekBarOnChangeListener);

        Intent intent = getIntent();
        Bundle musicBundle = intent.getExtras();
        long musicPosition = musicBundle.getLong("MUSIC_POSITION");
        selection = MediaStore.Audio.Media._ID+" = \'"+Long.toString(musicPosition)+"\'";   // 클릭된 id에 해당하는 결과만 가져오기 위해 selection 추가
        cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, null, null);
        initFileSetting(musicBundle.getString("MUSIC_TITLE"));
        initRecordButton();
        setListener();

        if (fileAccess.equals(Environment.MEDIA_MOUNTED)) {
            baseFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            baseFilePath = Environment.MEDIA_UNMOUNTED;
        }
        createAlertDialog();
        try{
            new Thread(MusicDataActivity.this).start();
        }catch (Exception e){
            e.printStackTrace();
        }

        // 매초마다 duration 체크하여 증가
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

    // 녹음관련 컨트롤러 버튼들 활성/비활성화
    // save 버튼은 미구현으로 인해 false 지정
    public void initRecordButton(){
        record_start.setEnabled(true);
        record_stop.setEnabled(false);
        record_play.setEnabled(false);
        record_save.setEnabled(false);
    }

    // 각 위젯들에 대하여 리스너 설정
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

    public void initFileSetting(String title){
        savedFilePath += "/"+title+"_Recorded.3gp";
        cursor.moveToFirst();
        int fileColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        audiofilepath = cursor.getString(fileColumn); // 결과로 얻어온 파일의 경로를 얻어옴
        durationSeek.setProgress(0);
        dataTitle.setText(title);
        try{
            mediaPlayer.setDataSource(audiofilepath);
            mediaPlayer.prepare();
            durationSeek.setMax(mediaPlayer.getDuration());                 // 음원파일 길이를 설정하고
            durationCalc = new DurationCalc(mediaPlayer.getDuration());     // 밀리세컨드로 반환되는 것을 초로 계산
            durationCalc.excute();
            maxSecond = durationCalc.getMaxSecond();
            second = durationCalc.getsSecond();
            minute = durationCalc.getsMinute();
            startDurationText.setText("00:00");
            endDurationText.setText(minute+":"+second);    // duration 출력 텍스트에 표시
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
            if(seekBar.getContentDescription().equals("V")){  // 움직인 것이 볼륨 시크바 일 경우
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
            }else { // 플레이어 Duration 시크바가 움직임이 끝난경우
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

    // 볼륨이미지가 눌릴경우 음소거/해제
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
                recordStop();
                break;
            case R.id.record_play:  // 플레이를 누를경우 새로운 액티비티로 이동하여 재생
                Intent recordIntent = new Intent(getApplicationContext(), RecordCheckActivity.class);
                recordIntent.putExtra("RECORD_FILE_PATH", savedFilePath);
                recordIntent.putExtra("TITLE", dataTitle.getText().toString());
                startActivityForResult(recordIntent,RECORD_CHECK_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.record_save:
                if (checkBeforeSave) {
                    aBuilder.show();
                } else {
                    Toast.makeText(getApplicationContext(), "저장하기전에 Record를 먼저 진행하세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.at_a_time_Button:
                atATimeButtonClick();
                break;
        }
    }

    // 동시 시작 버튼을 눌렸을 경우 mr과 record기능을 동시에 작동
    public void atATimeButtonClick(){
        if(!isAtATime){
            mrStart();
            recordStart();
            isAtATime = true;
            at_a_time_Button.setImageResource(R.drawable.at_a_time_stop);
        }else{
            recordStop(); // mrStop도 같이 작동
            isAtATime = false;
            at_a_time_Button.setImageResource(R.drawable.at_a_time_image);
        }
    }

    public void fileSave(String FilePath){
        if(baseFilePath.equals(Environment.MEDIA_UNMOUNTED)){
            Log.d(TAG,"SD카드에 접근 할 수 없습니다.");
        }else {
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
                if(mediaPlayer.isPlaying()){  // 플레이 중일 경우 시크바를 1초마다 진행
                    durationSeek.setProgress(current);
                    setDurationPosition(current);
                    handler.sendEmptyMessage(0);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // 플레이어 시크바, 듀레이션 텍스트창을 초기화
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
        checkBeforeSave = true;  // 저장하기 전에 save버튼 눌림 방지 변수
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

    public void recordStop(){
        if (isRecording) {
            if (mediaPlayer.isPlaying()) {
                at_a_time_Button.setImageResource(R.drawable.at_a_time_image);
                mrSetZeroPosition();
            }
            isRecording = false;
            setRecordButtonEnabled(true, false, true, false);
            mediaRecorder.stop();
        }
    }

    // 초를 계산하는 메서드
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

    @Override // 플레이어가 플레이를 완료할 때
    public void onCompletion(MediaPlayer mp) {
        if(isRecording){
            isRecording = false;
            setRecordButtonEnabled(true, false, true, false);
            mediaRecorder.stop();
        }
        playImage.setImageResource(R.drawable.play_button);
        mediaPlayer.seekTo(0);
        durationSeek.setProgress(0);
        startDurationText.setText("00:00");
        at_a_time_Button.setImageResource(R.drawable.at_a_time_image);
    }

    @Override
    public void finish() {
        super.finish();
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();
        if(isRecording)
            mediaRecorder.stop();

    }

    private class AlertDialogClickListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (which){
                case -1: // 저장버튼을 눌렀을 경우
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
                    else { // 공백 문자로 저장을 누를경우
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

    // Dialog 박스 셋팅
    public void createAlertDialog(){
        aBuilder.setTitle("Save Box");
        dialogView.setMrInfo(dataTitle.getText().toString());
        aBuilder.setView(dialogView.getDialogView());
        aBuilder.setPositiveButton("저장", alertDialogClickListener);
        aBuilder.setNegativeButton("취소", alertDialogClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == RECORD_CHECK_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){  // 이전 액티비티로 돌아와서 플레이어 초기화
                mediaPlayer.seekTo(0);
                durationSeek.setProgress(0);
                startDurationText.setText("00:00");
                at_a_time_Button.setImageResource(R.drawable.at_a_time_image);
            }
        }

    }

}
