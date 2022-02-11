package com.example.ergasia;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       final Switch s=(Switch) findViewById(R.id.offlineMode);
       final TextView switchText = (TextView)findViewById(R.id.onlineTextView);
       Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Anothershabby_pro_bold_v2_trial.ttf");
       switchText.setTypeface(font);
       switchText.setText(getString(R.string.onlineMode));
        //s.setTextOn("Offline")
        s.setShowText(true);
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (s.isChecked()) {
                    SocketHandler.setOffline();
                    Log.e("TOGGLE", "setting text");
                    switchText.setText(getString(R.string.offlineMode));
                } else {
                    SocketHandler.setOnline();
                    switchText.setText(getString(R.string.onlineMode));
                }
            }
        });

        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(v.getContext(), Search_Activity.class);
                startActivityForResult(next, 0);
            }
        });
    }
}