package com.example.projectui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuButton extends RelativeLayout {

    private ImageButton menuButton;
    private TextView menuName;
    private int menuDetails[] = new int[5]; //contents of table: 0: power setting (1-10), 1: cooking time(0010 - 9950), 2: grill on (0 or 1), mixGrill on (0 or 1), mixGrill percentage if on (0 - 100)

    public MenuButton(Context context) {
        super(context);
    }

    public MenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.menu_button_row, this, true);
        menuButton = findViewById(R.id.menu_button);

        menuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!isSelected()){
                    menuButton.setSelected(true);
                }
            }
        });
    }
}
