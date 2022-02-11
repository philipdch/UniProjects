package com.example.projectui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class TimerActivity extends AppCompatActivity {

    TextView counter;
    Button pause;
    Button plus_min;
    Button stop;
    CountDownTimer countDownTimer;
    long timer;
    boolean timerRunning;
    boolean bool;
    ProgressBar timerPb;
    CountDownTimer mCountDownTimer;
    int i=0;
    boolean isIncreased;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        pause = findViewById(R.id.pause);
        plus_min = findViewById(R.id.plus_1min);
        stop = findViewById(R.id.stop);
        counter = findViewById(R.id.timer);
        isIncreased = false;

        String time = getIntent().getStringExtra("SENT_TIMER");
        String timePattern = "([0-9]{1,2})(:)([0-9]{2})";
        Pattern compiledPattern = Pattern.compile(timePattern);
        String newTime = "";
        String minutes = "";
        String seconds = "";
        if (time != null) {
            Matcher matcher = compiledPattern.matcher(time);
            if (matcher.find()) {
                minutes = matcher.group(1);
                seconds = matcher.group(3);
                if (minutes != null && minutes.length() < 2) {
                    newTime = "0" + minutes + seconds;
                } else {
                    newTime = minutes + seconds;
                }
            }
            if (!newTime.isEmpty()) {
                counter.setText(newTime);
            } else {
                Toast.makeText(this, "Timer value is not a valid time!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        stop.setVisibility(View.GONE);
        final int min = parseInt(minutes);
        final int sec = parseInt(seconds);
        timer = (min * 60 * 1000) + (sec * 1000);
        System.out.println(sec);
        startTimer();

        timerPb = (ProgressBar) findViewById(R.id.timer_progressBar);
        timerPb.setMax(min*60 + sec);
        Animation an = new RotateAnimation(270.0f, 270.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        an.setFillAfter(true);
        timerPb.startAnimation(an);
        mCountDownTimer=new CountDownTimer(timer,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress"+ i+ millisUntilFinished);
                i++;
                timerPb.setProgress((int) Math.round(millisUntilFinished / 1000.0));

            }

            @Override
            public void onFinish() {
                //Do what you want
                i++;
                timerPb.setProgress(100);
            }
        };

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()){
                    pause.setSelected(false);
                    pause.setText(getResources().getString(R.string.pause));
                    stop.setVisibility(View.GONE);
                    pause.setBackground(getDrawable(R.drawable.pause));
                    bool=false;
                    startTimer();
                }else{
                    pause.setSelected(true);
                    pause.setText(getResources().getString(R.string.resume));
                    pause.getBackground().setColorFilter(getResources().getColor(R.color.mainGreen_old), PorterDuff.Mode.SRC);
                    stop.setVisibility(View.VISIBLE);
                    bool = true;
                    stopTimer();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(v.getContext(), MainActivity.class);
                startActivity(act);
            }
        });

        plus_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bool) {
                    stop.setVisibility(View.GONE);
                    pause.setSelected(true);
                }
                if(min < 98 && sec <50) {
                    plus();
                }
            }
        });

    }

    public void plus() {
        isIncreased = true;
        timer += 60000;
        update();
        countDownTimer.cancel();
        startTimer();
    }

   /* public  void startstop(){
        if(timerRunning){
            stopTimer();
            stop.setVisibility(View.VISIBLE);
        }
        else{
            startTimer();
            stop.setVisibility(View.GONE);
        }
    }//*/

    public void startTimer() {
        if(timerPb != null && isIncreased) {
            timerPb.setMax((int) timer / 1000);
            System.out.println("SETTING MAX = " + timer);
        }
            countDownTimer = new CountDownTimer(timer, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    i++;
                    timerPb.setProgress((int) Math.round(millisUntilFinished / 1000.0));
                    timer = millisUntilFinished;
                    update();
                }

                @Override
                public void onFinish() {
                }
            }.start();
        //start_stop.setText("pause");

        timerRunning = true;
    }

    public void stopTimer() {
        countDownTimer.cancel();
        //start_stop.setText("start");
        timerRunning = false;
    }

    public void update() {
        int minutes = (int) timer / 60000;
        int seconds = (int) timer % 60000 / 1000;

        String timerLeft;
        timerLeft = "" + minutes;
        timerLeft += ":";
        if (seconds < 10) {
            timerLeft += "0";
        }
        timerLeft += seconds;

        if (minutes < 10) {
            counter.setText(0 + timerLeft);
        } else {
            counter.setText(timerLeft);
        }
        if (timerLeft.equals("0:01")) {
            Intent act = new Intent(this, DoneActivity.class);
            startActivity(act);
        }

    }

    @Override
    public void onBackPressed(){
        //Do nothing - disable back button
    }
}
