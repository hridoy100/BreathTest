package com.example.breathtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RecordActivity extends AppCompatActivity {


    private MediaRecorder mediaRecorder;
    ImageView breathImage;

    private MediaPlayer mediaPlayer;

    private static String audioFilePath;

    private boolean isRecording = false;

    View view;
    CountDownTimer cTimer = null;
    TextView count;
    int countVal=6;
    int count_breadth = 0;
    LinearLayout topBarLayout;

    ObjectAnimator progressAnimator;
    ObjectAnimator progressAnimator2;


    ProgressBar progressBarAnimation;
    ProgressBar progressBarAnimation2;

    AudioManager audioManager;
    ToneGenerator toneG;

    TextView inhale_exhale;

    Animation animFadeIn;
    Animation animFadeInLarge;

    Animation animFadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        count = (TextView) findViewById(R.id.countText);
        topBarLayout = (LinearLayout) findViewById(R.id.topBarLayout);
        progressBarAnimation = (ProgressBar) findViewById(R.id.inhale_exhale_bar);

        breathImage = (ImageView) findViewById(R.id.breath_img);

        progressBarAnimation.setVisibility(View.INVISIBLE);
        inhale_exhale = (TextView) findViewById(R.id.inhale_exhale_text);

        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        animFadeInLarge = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in_large);
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);

        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 80);
        startTimer();
//        init();
    }

    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
                count.setText(Integer.toString(countVal-1));
                countVal--;
                // Play a System Sound
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
            }

            public void onFinish() {
                count.setVisibility(View.INVISIBLE);
                topBarLayout.setVisibility(View.VISIBLE);
                count.setY(-500);
                progressBarAnimation.setVisibility(View.VISIBLE);
                inhale_exhale.startAnimation(animFadeOut);
                breathImage.setVisibility(View.VISIBLE);
                breathImage.startAnimation(animFadeInLarge);
                init();
                start_recording();
            }
        };
        cTimer.start();
    }


    void animationTimer() {
        cTimer = new CountDownTimer(24000, 4000) {
            public void onTick(long millisUntilFinished) {
                if(count_breadth%2==0){
                    inhale_exhale.setText("INHALE");
                    inhale_exhale.setAlpha(1);
//                    progressBarAnimation.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//                    progressAnimator=ObjectAnimator.ofInt(progressBarAnimation, "progress", 0, 100);
                    breathImage.setImageResource(R.drawable.breath1);
                    breathImage.startAnimation(animFadeInLarge);
                    inhale_exhale.startAnimation(animFadeOut);
                }
                else {
                    inhale_exhale.setText("EXHALE");
                    inhale_exhale.setAlpha(1);
//                    progressBarAnimation.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//                    progressAnimator=ObjectAnimator.ofInt(progressBarAnimation, "progress", 100, 0);
//                    progressAnimator.setInterpolator(new DecelerateInterpolator());
                    breathImage.setImageResource(R.drawable.exhale2);
                    breathImage.startAnimation(animFadeInLarge);
                    inhale_exhale.startAnimation(animFadeOut);
                }
                count_breadth++;

            }

            public void onFinish() {
                System.out.println("On finish called 24000");
                progressAnimator.end();
                progressAnimator.cancel();
                progressAnimator.removeAllListeners();
            }
        };
        cTimer.start();
    }

    //cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    public void init(){

        progressAnimator = ObjectAnimator.ofInt(progressBarAnimation, "progress", 0, 100);
        progressAnimator.setDuration(4000);
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                progressAnimator.setRepeatMode(ValueAnimator.REVERSE);
                progressBarAnimation.setVisibility(View.INVISIBLE);
                inhale_exhale.setText("Done!");
                inhale_exhale.startAnimation(animFadeIn);
                breathImage.setVisibility(View.INVISIBLE);
                finishActivityCounter();
            }
        });
        animationTimer();
    }

    void finishActivityCounter() {
        cTimer = new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if (isRecording)
                {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    isRecording = false;
                }
                finish();
            }
        };
        cTimer.start();
    }

    public void start_recording(){
        audioFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+
                "/on_my_way.mp3";
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(
                    MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();

            isRecording = true;
            mediaRecorder.start();

        } catch (Exception e) {
            isRecording = false;
            e.printStackTrace();
        }



    }



}