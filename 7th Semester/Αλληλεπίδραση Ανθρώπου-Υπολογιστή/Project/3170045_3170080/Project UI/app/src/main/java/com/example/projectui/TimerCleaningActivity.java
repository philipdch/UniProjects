package com.example.projectui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Integer.parseInt;

public class TimerCleaningActivity extends AppCompatActivity {
    Button plus_minutes;
    Button plus_seconds;
    Button cos_minutes;
    Button cos_seconds;
    Button start;
    Button back;
    TextView text;
    TextView text_minites;
    TextView text_seconds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timerclieaning);
        text = findViewById(R.id.cooking_timer);
        plus_minutes = findViewById(R.id.minutes_plus);
        plus_seconds = findViewById(R.id.seconds_plus10);
        cos_minutes = findViewById(R.id.minutes_cos);
        cos_seconds = findViewById(R.id.seconds_cos10);
        text_minites = findViewById(R.id.minutes);
        text_seconds = findViewById(R.id.seconds);

        start = findViewById(R.id.start);
        text.setText(getIntent().getStringExtra("Text"));
        back = findViewById(R.id.back);

        final Common common=new Common();

        text_minites.setInputType(InputType.TYPE_CLASS_NUMBER);
        text_seconds.setInputType(InputType.TYPE_CLASS_NUMBER);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checksecondslimit=true;
                boolean checktotallimit=true;
                Intent act = new Intent(v.getContext(), TimerActivity.class);
                if(parseInt(text_minites.getText().toString())==99 && parseInt(text_seconds.getText().toString())>51){
                    checktotallimit=false;
                }
                if(parseInt(text_seconds.getText().toString())>=60){
                    checksecondslimit=false;
                }
                if(!checksecondslimit ||!checktotallimit){
                    CharSequence text=null;
                    int duration = Toast.LENGTH_SHORT;
                    if(!checktotallimit){text="max time is 99:50 minutes";}
                    if(!checksecondslimit){text="max of seconds are 59sec";}
                    Context context=getApplicationContext();
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                if (checksecondslimit && checktotallimit) {
                    if (text_minites.getText().toString().length() == 1) {
                        text_minites.setText(0 + text_minites.getText().toString());
                    }
                    if (text_seconds.getText().toString().length() == 1) {
                        text_minites.setText(0 + text_seconds.getText().toString());
                    }
                    act.putExtra("SENT_TIMER", text_minites.getText().toString() + ":" + text_seconds.getText().toString());
                    startActivity(act);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(v.getContext(), MainActivity.class);
                startActivity(act);
            }
        });

        plus_minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.plus_minutes(text_minites,getApplicationContext());
            }
        });

        cos_minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.cos_minutes(text_minites,getApplicationContext());           }
        });

        plus_seconds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.plus_seconds(text_seconds,getApplicationContext());
            }
        });

        cos_seconds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.cos_seconds(text_seconds,getApplicationContext());
            }
        });

    }
}