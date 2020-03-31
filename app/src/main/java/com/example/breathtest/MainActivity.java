package com.example.breathtest;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    Button recordButton;
    TextView playButton;
    Button submitButton;
    TextView stopButton;

    int playingRecordNo=2;

    LinearLayout stopButtonLayout;
    LinearLayout playButtonLayout;

    private MediaRecorder mediaRecorder;

    private MediaPlayer mediaPlayer;

    private static String audioFilePath;

    private boolean isRecording = false;

    private static final int RECORD_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;

    ProgressBar progress_bar;

    Thread t;

    ObjectAnimator progressBarAnimator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBarAnimator = ObjectAnimator.ofInt(progress_bar, "progress", 0, 100);
        progressBarAnimator.setDuration(24200);
        audioSetup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }


    /*private MediaObserver observer = null;

    private class MediaObserver implements Runnable {
        private AtomicBoolean stop = new AtomicBoolean(false);

        public void stop() {
            stop.set(true);
        }

        @Override
        public void run() {
            while (!stop.get()) {
                progress_bar.setProgress((int)((double)mediaPlayer.getCurrentPosition() / (double)mediaPlayer.getDuration()*100));

                try {
                    Thread.sleep(1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }*/


    public void start_recording(View view){
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        startActivity(intent);
        /*
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(
                    MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();

            isRecording = true;
            stopButton.setEnabled(true);
            playButton.setEnabled(false);
            recordButton.setEnabled(false);
            playButtonLayout.setClickable(false);
            playButtonLayout.setVisibility(View.INVISIBLE);

            stopButtonLayout.setVisibility(View.VISIBLE);
            stopButtonLayout.setClickable(true);
            mediaRecorder.start();

        } catch (Exception e) {
            isRecording = false;
            e.printStackTrace();
        }

         */

    }


    public void play_sound(View view) {
        System.out.println("play_sound: "+audioFilePath);
        progress_bar.setProgress(0);
        progressBarAnimator.start();
        configureMediaPlayer();
        ////observer = new MediaObserver();
        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();

            playButton.setEnabled(false);
            recordButton.setEnabled(false);
            stopButton.setEnabled(true);

            playButtonLayout.setClickable(false);
            playButtonLayout.setVisibility(View.INVISIBLE);

            stopButtonLayout.setVisibility(View.VISIBLE);
            stopButtonLayout.setClickable(true);

            progress_bar.setVisibility(View.VISIBLE);

            mediaPlayer.start();
            ////t = new Thread(observer);
            ////t.start();

            Toast.makeText(getApplicationContext(), "Time: "+mediaPlayer.getDuration(), Toast.LENGTH_LONG).show();

        }
        catch (Exception e){
            mediaPlayer.stop();
            e.printStackTrace();
        }

    }
    public void stopAudio(View view)
    {
        progress_bar.setProgress(0);

        stopButton.setEnabled(false);

        playButton.setEnabled(true);
        playButtonLayout.setClickable(true);
        playButtonLayout.setVisibility(View.VISIBLE);

        stopButtonLayout.setVisibility(View.INVISIBLE);
        stopButtonLayout.setClickable(false);
        progress_bar.setVisibility(View.INVISIBLE);

//        t.interrupt();

        if (isRecording)
        {
            recordButton.setEnabled(false);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        } else {

            ////observer.stop();
//            mediaPlayer.release();
//            mediaPlayer = null;
            mediaPlayer.stop();
            recordButton.setEnabled(true);
        }
    }

    public void submit(View view){

    }

    protected boolean hasMicrophone() {
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }

    private void audioSetup()
    {
        recordButton =
                (Button) findViewById(R.id.recordButton);
        playButton = (TextView) findViewById(R.id.playButton);
        stopButton = (TextView) findViewById(R.id.stopButton);
        submitButton = (Button) findViewById(R.id.submit);

        playButtonLayout = (LinearLayout) findViewById(R.id.playButtonLayout);
        stopButtonLayout = (LinearLayout) findViewById(R.id.stopButtonLayout);

        playButtonLayout.setVisibility(View.VISIBLE);
        playButtonLayout.setClickable(true);
        playButton.setEnabled(true);

        stopButtonLayout.setVisibility(View.INVISIBLE);
        stopButtonLayout.setClickable(false);
        stopButton.setEnabled(false);

//        if (!hasMicrophone())
//        {
////            stopButton.setEnabled(false);
//            playButton.setEnabled(true);
//            recordButton.setEnabled(true);
//        } else {
//            playButton.setEnabled(false);
////            stopButton.setEnabled(false);
//        }

        audioFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+
                "/breath_record1.mp3";
        Log.d("audioSetup", audioFilePath);

        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(result == PackageManager.PERMISSION_GRANTED) {
            //do nothing..
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }

        requestPermission(Manifest.permission.RECORD_AUDIO,
                RECORD_REQUEST_CODE);

    }

    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this,
                permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permissionType}, requestCode
            );
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORD_REQUEST_CODE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    recordButton.setEnabled(false);

                    Toast.makeText(this,
                            "Record permission required",
                            Toast.LENGTH_LONG).show();
                } else {
                    requestPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            STORAGE_REQUEST_CODE);
                }
                return;
            }
            case STORAGE_REQUEST_CODE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    recordButton.setEnabled(false);
                    Toast.makeText(this,
                            "External Storage permission required",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void configureMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ////observer.stop();
                /////progress_bar.setProgress(mp.getCurrentPosition());
                // TODO Auto-generated method stub
                mediaPlayer.stop();
                mediaPlayer.reset();

                stopButton.setEnabled(false);

                playButton.setEnabled(true);
                playButtonLayout.setClickable(true);
                playButtonLayout.setVisibility(View.VISIBLE);

                stopButtonLayout.setVisibility(View.INVISIBLE);
                stopButtonLayout.setClickable(false);
                progress_bar.setVisibility(View.INVISIBLE);

                recordButton.setEnabled(true);
                if(playingRecordNo<=6){
                    playRecord();
                }
                else{
                    playingRecordNo=2;
                    progress_bar.setProgress(0);
                }

            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                progress_bar.setSecondaryProgress(percent);
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                stopButton.setEnabled(true);

            }
        });

//        audioFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+
//                "/breath_record1.mp3";
    }

    public void playRecord() {
        progress_bar.setProgress(0);
        configureMediaPlayer();
        ////observer = new MediaObserver();
        audioFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+
                "/breath_record"+playingRecordNo+".mp3";
        System.out.println("play_sound path: "+audioFilePath);
        playingRecordNo++;
        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();

            playButton.setEnabled(false);
            recordButton.setEnabled(false);
            stopButton.setEnabled(true);

            playButtonLayout.setClickable(false);
            playButtonLayout.setVisibility(View.INVISIBLE);

            stopButtonLayout.setVisibility(View.VISIBLE);
            stopButtonLayout.setClickable(true);

            progress_bar.setVisibility(View.VISIBLE);

            mediaPlayer.start();
            ////t = new Thread(observer);
            ////t.start();

            Toast.makeText(getApplicationContext(), "Time: "+mediaPlayer.getDuration(), Toast.LENGTH_LONG).show();

        }
        catch (Exception e){
            mediaPlayer.stop();
            e.printStackTrace();
        }



    }

}
