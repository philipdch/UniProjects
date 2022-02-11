package com.example.projectui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class DoneActivity extends AppCompatActivity {

    TextView doneTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);
        doneTextView = findViewById(R.id.done);
        doneTextView.getBackground().setColorFilter(getResources().getColor(R.color.mainGreen_old), PorterDuff.Mode.SRC);
        int TIME_OUT = 1500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(DoneActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, TIME_OUT);
    }

    @Override
    public void onBackPressed(){
        //Do nothing - disable back button
    }
}
