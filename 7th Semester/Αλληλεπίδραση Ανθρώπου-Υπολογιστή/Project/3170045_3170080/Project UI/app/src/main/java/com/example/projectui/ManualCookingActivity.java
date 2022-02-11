package com.example.projectui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.service.autofill.VisibilitySetterAction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Locale;

import static java.lang.Integer.parseInt;
import static java.lang.Math.atan2;

public class ManualCookingActivity extends AppCompatActivity {


    // final static int minutes=10;
    //final static int seconds=6;
    //final static int k=9;
    Button plus_minutes;
    Button plus_seconds;
    Button cos_minutes;
    Button cos_seconds;
    Button start;
    Button back;
    TextView text;
    TextView text_minites;
    TextView text_seconds;
    Button help_mixgrill;
    LinearLayout grillTimeText;
    Switch grill_swich;
    SeekBar grillSeekBar;
    TextView grillValueText;
    TextView grillMinutesTextView;
    TextView grillSecondsTextView;
    TextView mixgrillText;

    //RotaryKnobView knob;
    ArrayList<Integer> persent = new ArrayList<>();
    int per = 0;


    //ArrayList<ArrayList<Integer>> arrayList_minutes=new ArrayList<ArrayList<Integer>>();
    //ArrayList<ArrayList<Integer>> arrayList_second=new ArrayList<>();
    //int position_minutes;
    //int position_seconds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manualcooking);
        // knob=findViewById(R.id.knob);
        // knob.setListener(this);
        grillMinutesTextView=findViewById(R.id.grill_minutes_text);
        grillMinutesTextView.setVisibility(View.GONE);
        grillSecondsTextView=findViewById(R.id.grill_seconds_text);
        grillSecondsTextView.setVisibility(View.GONE);
        text = findViewById(R.id.cooking_timer);
        plus_minutes = findViewById(R.id.minutes_plus);
        plus_seconds = findViewById(R.id.seconds_plus10);
        cos_minutes = findViewById(R.id.minutes_cos);
        cos_seconds = findViewById(R.id.seconds_cos10);
        text_minites = findViewById(R.id.minutes);
        text_seconds = findViewById(R.id.seconds);
        grillTimeText = findViewById(R.id.grill_time_layout);
        grillTimeText.setVisibility(View.GONE);
        start = findViewById(R.id.start);
        back = findViewById(R.id.back);
        help_mixgrill = findViewById(R.id.help);
        grill_swich = findViewById(R.id.switch_grill);
        grillSeekBar = findViewById(R.id.grill_seekbar);
        grillSeekBar.incrementProgressBy(5);
        grillSeekBar.setVisibility(View.GONE);
        grillValueText = findViewById(R.id.grill_percent_text);
        grillValueText.setVisibility(View.GONE);
        mixgrillText = findViewById(R.id.mixgrill_text);
        mixgrillText.setVisibility(View.GONE);

        text_minites.setInputType(InputType.TYPE_CLASS_NUMBER);
        text_seconds.setInputType(InputType.TYPE_CLASS_NUMBER);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        final Common common = new Common();
        for (int i = 0; i < 20; i++) {
            per = per + 5;
            persent.add(per);
        }
        System.out.println(persent.get(0).toString());
        help_mixgrill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(ManualCookingActivity.this, getResources().getString(R.string.helpmixgrill), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP| Gravity.START, 0, 0);
                toast.show();
            }
        });

        grill_swich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    grillSeekBar.setProgress(grillSeekBar.getMax());
                    int val = (grillSeekBar.getProgress() * (grillSeekBar.getWidth() - 2 * grillSeekBar.getThumbOffset())) / grillSeekBar.getMax();
                    String barValueText = grillSeekBar.getProgress() + "%";
                    grillValueText.setText(barValueText);
                    grillValueText.setX(grillSeekBar.getX() + val + grillSeekBar.getThumbOffset() / 2.0f);
                    calculated_persent();
                    grillSeekBar.setVisibility(View.VISIBLE);
                    grillValueText.setVisibility(View.VISIBLE);
                    grillSecondsTextView.setVisibility(View.VISIBLE);
                    grillMinutesTextView.setVisibility(View.VISIBLE);
                    grillTimeText.setVisibility(View.VISIBLE);
                    mixgrillText.setVisibility(View.VISIBLE);
                } else {
                    grillSeekBar.setVisibility(View.GONE);
                    grillValueText.setVisibility(View.GONE);
                    grillSecondsTextView.setVisibility(View.GONE);
                    grillMinutesTextView.setVisibility(View.GONE);
                    grillTimeText.setVisibility(View.GONE);
                    mixgrillText.setVisibility(View.GONE);
                }
            }
        });

        grillSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 5;
                progress = progress * 5;
                if(progress < 10) {
                    progress = 10;
                    seekBar.setProgress(10);
                }
                int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                String barValueText = progress + "%";
                grillValueText.setText(barValueText);
                grillValueText.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2.0f);
                calculated_persent();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(v.getContext(), MainActivity.class);
                startActivity(act);
            }
        });

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

        text_minites.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(grill_swich.isChecked()) {
                    calculated_persent();
                }
            }
        });

        text_seconds.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(grill_swich.isChecked()) {
                    calculated_persent();
                }
            }
        });

        plus_minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.plus_minutes(text_minites, getApplicationContext());
            }
        });

        cos_minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.cos_minutes(text_minites, getApplicationContext());
            }
        });

        plus_seconds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.plus_seconds(text_seconds, getApplicationContext());
            }
        });

        cos_seconds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.cos_seconds(text_seconds, getApplicationContext());
            }
        });
    }

    public void calculated_persent(){
        System.out.println("TEXT MINUTES: " + text_minites.getText());
        System.out.println("TEXT SECONDS: " + text_seconds.getText());
        if(text_minites.getText().toString().equals("") || text_seconds.getText().toString().equals("")) return;
        long totalSeconds=(parseInt(text_minites.getText().toString())*60*1000 + parseInt(text_seconds.getText().toString())*1000);
        System.out.println(totalSeconds);
        String viewToString = grillValueText.getText().toString();
        String grillPercentage = viewToString.substring(0, viewToString.length() - 1);
        long persentseconts= (totalSeconds*parseInt(grillPercentage)/100);
        System.out.println(persentseconts);
        int minutes=(int)  persentseconts/ 60000;
        int seconds=(int) persentseconts %60000/1000;
        grillMinutesTextView.setText(String.format(Locale.getDefault(),"%02d", minutes));
        grillSecondsTextView.setText(String.format(Locale.getDefault(),"%02d", seconds));
    }
}




