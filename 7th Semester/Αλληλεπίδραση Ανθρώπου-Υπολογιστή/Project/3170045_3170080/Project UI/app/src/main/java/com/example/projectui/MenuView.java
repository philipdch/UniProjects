package com.example.projectui;

import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuView extends LinearLayout {

    private ImageButton menuImageButton;
    private TextView menuText;
    private MenuSettings settings;

    public MenuView(Context context) {
        this(context, null);
    }
    public MenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MenuView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // put all custom logic in this constructor, which always runs
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.menu_button_row, this, true);
        menuImageButton = linearLayout.findViewById(R.id.menu_button);
        menuText = linearLayout.findViewById(R.id.menu_name);
    }

    public MenuSettings getSettings() {
        return settings;
    }

    public void setSettings(MenuSettings settings) {
        this.settings = settings;
    }

}
