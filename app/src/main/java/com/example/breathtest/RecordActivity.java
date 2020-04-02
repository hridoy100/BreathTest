package com.example.breathtest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {


    int circleRadius = 300;
    int countColor = 0;

    int totalData = 1;

    private MediaRecorder mediaRecorder;

    ImageView breathCircle;

    private MediaPlayer mediaPlayer;
    RelativeLayout relativeLayout;
    private int customI = 1;
    CustomView customView;

    private static String audioFilePath;
    private static String inhaleTonePath;
    private static String exhaleTonePath;

    private static String recorded_inhale_path;
    private static String recorded_exhale_path;

    private boolean isRecording = false;

    int inhale_exhale_count=0;

    CountDownTimer cTimer = null;
    TextView count;
    int countVal=4; // 6
    int count_breadth = 0;
    LinearLayout topBarLayout;

    ObjectAnimator progressAnimator;
    ObjectAnimator birdAnimator;


    ProgressBar progressBarAnimation;
    ProgressBar progressBarAnimation2;

    AudioManager audioManager;
    ToneGenerator toneG;

    TextView inhale_exhale;

    Animation animFadeIn;
    Animation animFadeInLarge;

    Animation animFadeOut;

    ImageView inhaleBird;

    TextView breathCircleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        count = (TextView) findViewById(R.id.countText);
        topBarLayout = (LinearLayout) findViewById(R.id.topBarLayout);
        progressBarAnimation = (ProgressBar) findViewById(R.id.inhale_exhale_bar);

        breathCircle = (ImageView) findViewById(R.id.breath_img);
        breathCircleText = (TextView) findViewById(R.id.breath_text_circle);

        progressBarAnimation.setVisibility(View.INVISIBLE);
        inhale_exhale = (TextView) findViewById(R.id.inhale_exhale_text);

        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        animFadeInLarge = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in_large);
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);

        inhaleBird = (ImageView) findViewById(R.id.bird);

        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 80);

        inhaleTonePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+
                "/inhale.aac";
        exhaleTonePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+
                "/exhale.aac";
        audioFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+
                "/breath_recorded.mp3";
        configureMediaPlayer();

        relativeLayout = (RelativeLayout) findViewById(R.id.circleView);
        customView = new CustomView(this);
        relativeLayout.addView(customView);
//        startViewAnimation();

        startTimer();
//        init();
    }


    /*
    public void startViewAnimation() {
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (customI < 80) { // Please change '70' according to how long you want to go
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int baseRadius=250; // base radius is basic radius of circle from which to start animation
                            if(inhale_exhale_count%2==0)
                                customView.updateView(-customI+baseRadius);
                            else
                                customView.updateView(customI+baseRadius);
                            customI++;
                        }
                    });
                } else {
                    customI = 0;
                    customView.resetColor(0, 133, 119);
                    timer.cancel();
                }
            }
        }, 0, 200); // change '500' to milliseconds for how frequent you want to update radius
    }

     */

    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
                count.setText(Integer.toString(countVal-1));
                countVal--;
                // Play a System Sound
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
//                if(millisUntilFinished<=2000){
//                    play_inhale();
//                }
            }

            public void onFinish() {
                count.setVisibility(View.INVISIBLE);
                topBarLayout.setVisibility(View.VISIBLE);
                count.setY(-500);

                customView.setVisibility(View.VISIBLE);
                customView.updateView(circleRadius);
                breathCircle.setVisibility(View.VISIBLE);

                

                play_inhale();

                /*progressBarAnimation.setVisibility(View.VISIBLE);
                inhale_exhale.startAnimation(animFadeOut);
                breathImage.setVisibility(View.VISIBLE);
                breathImage.startAnimation(animFadeInLarge);
                */

//                init();
//                start_recording();
            }
        };
        cTimer.start();
    }

    void play_inhale(){
        try {
            mediaPlayer.setDataSource(inhaleTonePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            inhaleBird.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(R.drawable.left_to_right_bird)
                    .into(inhaleBird);

            breathCircleText.setText("থামুন");
//            customView.updateView(circleRadius);
//            breathCircle.setVisibility(View.VISIBLE);
        }
        catch (Exception e){
            mediaPlayer.stop();
            e.printStackTrace();
        }
    }

    void play_exhale(){
        try {
            mediaPlayer.setDataSource(exhaleTonePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            inhaleBird.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(R.drawable.right_to_keft_bird)
                    .into(inhaleBird);

            breathCircleText.setText("থামুন");
//            customView.updateView(circleRadius);
        }
        catch (Exception e){
            mediaPlayer.stop();
            e.printStackTrace();
        }
    }

    void animateBirdLtR(){
        inhaleBird.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(R.drawable.left_to_right_bird)
                .into(inhaleBird);
        birdAnimator = ObjectAnimator.ofFloat(inhaleBird, "translationX", Resources.getSystem().getDisplayMetrics().widthPixels-inhaleBird.getWidth());
        birdAnimator.setDuration(4000);
//        birdAnimator.setRepeatMode(ValueAnimator.REVERSE);
//        birdAnimator.setRepeatCount(1);
        birdAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                inhaleBird.setVisibility(View.VISIBLE);
                System.out.println("Completed..");
                /*if(inhale_exhale_count<6) {
                    if(isRecording) {
                        mediaRecorder.stop();
                        isRecording = false;
                    }
                    play_exhale();

                }*/
                if(inhale_exhale_count>=6)
                    finishActivityCounter();

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                Glide.with(getApplicationContext())
                        .load(R.drawable.right_to_keft_bird)
                        .into(inhaleBird);
            }

        });

        breathCircleText.setText("শ্বাস নিন");

        birdAnimator.start();
    }

    void animateBirdRtL(){
        inhaleBird.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(R.drawable.right_to_keft_bird)
                .into(inhaleBird);
        birdAnimator = ObjectAnimator.ofFloat(inhaleBird, "translationX", Resources.getSystem().getDisplayMetrics().widthPixels-inhaleBird.getWidth(), 0);
        birdAnimator.setDuration(4000);
//        birdAnimator.setRepeatMode(ValueAnimator.REVERSE);
//        birdAnimator.reverse();
//        birdAnimator.setRepeatCount(1);
        birdAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                inhaleBird.setVisibility(View.VISIBLE);
                System.out.println("Completed..");
                /*if(inhale_exhale_count<6) {
                    if(isRecording) {
                        mediaRecorder.stop();
                        isRecording = false;
                    }

                    play_inhale();
                }*/
                if(inhale_exhale_count>=6)
                    finishActivityCounter();

            }

        });

        breathCircleText.setText("শ্বাস ছাড়ুন");

        birdAnimator.start();
    }


    void startFourSecondTimer() {

        cTimer = new CountDownTimer(4000, 50) {

            public void onTick(long millisUntilFinished) {
                if(inhale_exhale_count%2==0) {
                    customView.updateView(circleRadius+=3);
                    customView.updateColor(countColor--);
                }
                else {
                    customView.updateView(circleRadius-=3);
                    customView.updateColor(countColor++);
                }
//                breathCircle.getLayoutParams().width -=10;
//                breathCircle.getLayoutParams().height -=10;
                /*if (customI < 80) { // Please change '70' according to how long you want to go
                    //runOnUiThread(new Runnable() {
                        //@Override
                        //public void run() {
                            int baseRadius=250; // base radius is basic radius of circle from which to start animation
                            if(inhale_exhale_count%2==0)
                                customView.updateView(customI+baseRadius);
                            else
                                customView.updateView(-customI+baseRadius);
                            customI++;
                        //}
                    //});
                } else {
                    customI = 0;
                    customView.resetColor(0, 133, 119);
                    cTimer.cancel();
                }

                 */
            }

            public void onFinish() {
                if(inhale_exhale_count<6 && isRecording) {
                    mediaRecorder.stop();
                    isRecording = false;
                }
                if(inhale_exhale_count<6) {
                    if (inhale_exhale_count % 2 == 0) {
                        play_inhale();
                    } else {
                        play_exhale();
                    }
                }
            }
        };
        cTimer.start();
    }



    public void configureMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mediaPlayer.stop();
                mediaPlayer.reset();
                if(inhale_exhale_count%2==0) {
                    animateBirdLtR();
                }
                else {
                    animateBirdRtL();
                }


                inhale_exhale_count++;

                if(inhale_exhale_count<=6)
                {
                    start_recording();
                    startFourSecondTimer();
                }

                /*if(!mediaPlayer.isPlaying()){
                    init();
                    animationTimer();
                }*/
//
            }
        });

    }


    void animationTimer() {
        cTimer = new CountDownTimer(24000, 4000) {
            public void onTick(long millisUntilFinished) {
                if(count_breadth%2==0){
                    inhale_exhale.setText("INHALE");
                    inhale_exhale.setAlpha(1);
//                    progressBarAnimation.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//                    progressAnimator=ObjectAnimator.ofInt(progressBarAnimation, "progress", 0, 100);
//                    breathImage.setImageResource(R.drawable.breath1);
//                    breathImage.startAnimation(animFadeInLarge);
                    inhale_exhale.startAnimation(animFadeOut);
                }
                else {
                    inhale_exhale.setText("EXHALE");
                    inhale_exhale.setAlpha(1);
//                    progressBarAnimation.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//                    progressAnimator=ObjectAnimator.ofInt(progressBarAnimation, "progress", 100, 0);
//                    progressAnimator.setInterpolator(new DecelerateInterpolator());
//                    breathImage.setImageResource(R.drawable.exhale2);
//                    breathImage.startAnimation(animFadeInLarge);
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
//                breathImage.setVisibility(View.INVISIBLE);
                finishActivityCounter();
            }
        });
        animationTimer();
    }

    void finishActivityCounter() {

//        try {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            if(isRecording){
//                mediaRecorder.stop();
//                isRecording = false;
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }

        List<File> files = new ArrayList<>();
        for(int i=1; i<=inhale_exhale_count; i++)
            files.add(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+
                "/breath_record"+i+".mp3"));
        File mergedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+
                "/breath_recorded.mp3");
//        mergeAudio(files);
        ///mergeSongs(mergedFile,files);

        inhaleBird.setVisibility(View.INVISIBLE);
        breathCircle.setVisibility(View.INVISIBLE);

        breathCircleText.setText("Done!");
        breathCircleText.startAnimation(animFadeIn);
        breathCircleText.setTextColor(Color.parseColor("#008577"));
        customView.setVisibility(View.INVISIBLE);

        if (isRecording)
        {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        }

        cTimer = new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                mediaPlayer.stop();
                mediaPlayer.release();

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
                "/breath_record"+inhale_exhale_count+".mp3";
        Toast.makeText(getApplicationContext(), "Count: "+inhale_exhale_count, Toast.LENGTH_LONG).show();

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(
                    MediaRecorder.OutputFormat.DEFAULT);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.prepare();

            isRecording = true;
            mediaRecorder.start();

        } catch (Exception e) {
            isRecording = false;
            e.printStackTrace();
        }



    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(isRecording){
//            mediaRecorder.stop();
//        }
//        else if(mediaPlayer!=null && mediaPlayer.isPlaying()){
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isRecording){
            mediaRecorder.stop();
            cTimer.cancel();
        }
        else if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

    }

    private void mergeSongs(File mergedFile, List<File> mp3Files){

        FileInputStream fisToFinal = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mergedFile);
            fisToFinal = new FileInputStream(mergedFile);
            for(File mp3File:mp3Files){
                if(!mp3File.exists())
                    continue;
                FileInputStream fisSong = new FileInputStream(mp3File);
                SequenceInputStream sis = new SequenceInputStream(fisToFinal, fisSong);
                byte[] buf = new byte[1024];
                try {
                    for (int readNum; (readNum = fisSong.read(buf)) != -1;)
                        fos.write(buf, 0, readNum);
                } finally {
                    if(fisSong!=null){
                        fisSong.close();
                    }
                    if(sis!=null){
                        sis.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(fos!=null){
                    fos.flush();
                    fos.close();
                }
                if(fisToFinal!=null){
                    fisToFinal.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
/*
    public static void mergeAudio(List<File> filesToMerge) {

        String output = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "breath_recorded.mp3";
        File mergedFile = new File(output);
        List<AACTrackImpl> aacTracks = new ArrayList<>();
        Movie result = new Movie();
        int k=1;
        try {
            for (File file : filesToMerge) {
                try {

                    result.addTrack(new AACTrackImpl(new FileDataSourceImpl(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "breath_record"+k+".mp3")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                k++;
            }
            Container out = new DefaultMp4Builder().build(result);

            FileChannel fc = new RandomAccessFile(output, "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
        }
        catch (Exception e){
            e.printStackTrace();
        /*
        while (filesToMerge.size()!=1){

            try {

                String[] videoUris = new String[]{
                        filesToMerge.get(0).getPath(),
                        filesToMerge.get(0).getPath()
                };

                List<AACTrackImpl> audioTracks = new ArrayList<>();

                for (Movie m : inMovies) {
                    for (Track t : m.getTracks()) {
                        if (t.getHandler().equals("soun")) {
                            audioTracks.add(t);
                        }
                        if (t.getHandler().equals("vide")) {
                            videoTracks.add(t);
                        }
                    }
                }

                Movie result = new Movie();

                if (!audioTracks.isEmpty()) {
                    result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                }
                if (!videoTracks.isEmpty()) {
                    result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                }

                Container out = new DefaultMp4Builder().build(result);

                FileChannel fc = new RandomAccessFile(output, "rw").getChannel();
                out.writeContainer(fc);
                fc.close();

            } catch (IOException e) {
                e.printStackTrace();
            }*/ /*
        }
    }
*/

}