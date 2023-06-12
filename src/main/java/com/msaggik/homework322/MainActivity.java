package com.msaggik.homework322;

import static android.widget.Toast.*;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements Runnable {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private SeekBar seekBar;
    private boolean wasPlaing = false;
    private FloatingActionButton fadPlayPause;
    private TextView seekBarHint;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fadPlayPause = findViewById(R.id.fadPlayPause);
        seekBarHint = findViewById(R.id.seekBarHint);
        seekBar = findViewById(R.id.seekBar);

        fadPlayPause.setOnClickListener(view -> playSong());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE);

                int timeTrack = (int) Math.ceil(progress/1000f);

                // вывод на экран времени отсчёта трека
                if (timeTrack < 10) {
                    seekBarHint.setText("00:0" + timeTrack);
                } else if (timeTrack < 60){
                    seekBarHint.setText("00:" + timeTrack);
                } else if (timeTrack >= 60) {
                    seekBarHint.setText("01:" + (timeTrack - 60));
                }



            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
                seekBarHint.setVisibility(View.VISIBLE);
                }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
             if (mediaPlayer != null && mediaPlayer.isPlaying()){
                 mediaPlayer.seekTo(seekBar.getProgress());
              }
            }
            long prevTime = 0;

            private void sumbmit() {
                long currentTime = System.currentTimeMillis();

                boolean isAction = false;
                synchronized (this) {
                    if (currentTime - prevTime > TimeUnit.SECONDS.toMillis(10)) {
                        prevTime = currentTime;
                        isAction = true;
                    }
                }
                if (isAction) {

                }
            }

        });
    }
    private void playSong(){
        try {
            if (mediaPlayer !=null && mediaPlayer.isPlaying()) {
                clearMediaPlayer();
                seekBar.setProgress(0);
                wasPlaing = true;
                fadPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
            }
            if(!wasPlaing){
                if(mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                fadPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
                AssetFileDescriptor descriptor = getAssets().openFd("barredeen-boku-no-love.mp3");
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();

                mediaPlayer.prepare();
                mediaPlayer.setLooping(false);
                seekBar.setMax(mediaPlayer.getDuration());

                mediaPlayer.start();
                new Thread(this).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        long back_pressed = 0;
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            stopAllServices();
            finish();
        } else {
            makeText(MainActivity.this, "Чтобы выйти, нажмите на кнопку НАЗАД ещё раз.",
                    LENGTH_LONG).show();
            back_pressed = System.currentTimeMillis();
        }
    }

    private void stopAllServices() {
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        clearMediaPlayer();

    }
    private void  clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void run() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();

        while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            try {

                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition();

            }catch (InterruptedException e){
                e.printStackTrace();
                return;
            }catch (Exception e){
                return;
            }
 seekBar.setProgress(currentPosition);
        }
    }
}
