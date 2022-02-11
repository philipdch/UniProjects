package com.example.projectui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button manual_cooking;
    Button help;
    Button cleaning;
    Button timer;
    Button userMenus;
    TextView manual_cooking_help;
    TextView user_menou_help;
    TextView timer_help;
    TextView cleaning_help;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manual_cooking = findViewById(R.id.manual_cooking_button);
        timer=findViewById(R.id.timer_button);
        cleaning=findViewById(R.id.cleaning_button);
        userMenus = findViewById(R.id.user_menus_button);

        manual_cooking_help=findViewById(R.id.manual_cooking_help);
        user_menou_help=findViewById(R.id.user_menus_help);
        cleaning_help=findViewById(R.id.cleaning_help);
        timer_help=findViewById(R.id.timer_help);
        manual_cooking_help.setVisibility(View.GONE);
        user_menou_help.setVisibility(View.GONE);
        timer_help.setVisibility(View.GONE);
        cleaning_help.setVisibility(View.GONE);
        help=findViewById(R.id.help);
        

        manual_cooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(v.getContext(), ManualCookingActivity.class);;
                startActivity(act);
            }
        });
        cleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(v.getContext(), TimerCleaningActivity.class);
                act.putExtra("Text","Cleaning");
                startActivity(act);
            }
        });

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(v.getContext(), TimerCleaningActivity.class);
                act.putExtra("Text","Timer");
                startActivity(act);
            }
        });

        userMenus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(v.getContext(), MenuActivity.class);
                startActivity(act);
            }
        });



        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!help.isSelected()){
                    help.setSelected(true);
                    help.setTextColor(getResources().getColor(R.color.white));
                    manual_cooking_help.setVisibility(View.VISIBLE);
                    user_menou_help.setVisibility(View.VISIBLE);
                    timer_help.setVisibility(View.VISIBLE);
                    cleaning_help.setVisibility(View.VISIBLE);

                }
                else{
                    help.setSelected(false);
                    help.setTextColor(getResources().getColor(R.color.blue));
                    manual_cooking_help.setVisibility(View.GONE);
                    user_menou_help.setVisibility(View.GONE);
                    timer_help.setVisibility(View.GONE);
                    cleaning_help.setVisibility(View.GONE);
                }
            }
        });
    }
}
