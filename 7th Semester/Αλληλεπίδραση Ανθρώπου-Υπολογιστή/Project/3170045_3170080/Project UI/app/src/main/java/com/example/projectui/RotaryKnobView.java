package com.example.projectui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.view.GestureDetectorCompat;

public class RotaryKnobView extends RelativeLayout implements GestureDetector.OnGestureListener {
    private GestureDetectorCompat gestureDetector;
    private int maxValue = 10;
    private int minValue = 1;
    RotaryKnobListener listener = null;
    int value = 50;
    private Drawable knobDrawable = null;
    private ImageView knobImageView;
    private float divider = (float)300 / (maxValue - minValue);
    private float knobWidth;
    private float knobHeight;

    public RotaryKnobView( Context context) {
        this(context, null);
    }

    public RotaryKnobView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotaryKnobView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet,defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.power_knob, this, true);
        this.maxValue = maxValue + 1;
        knobImageView = (ImageView) findViewById(R.id.knobImageView);
        System.out.println("KNOB ID = " + R.id.knobImageView );
        knobWidth = getResources().getDimension(R.dimen.knob_width);
        knobHeight = getResources().getDimension(R.dimen.knob_height);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.RotaryKnobView,
                0, 0);
        try {
            minValue = a.getInt(R.styleable.RotaryKnobView_minValue, 1);
            maxValue = a.getInt(R.styleable.RotaryKnobView_maxValue, 10);
            divider = 300f / (maxValue - minValue);
            value = a.getInt(R.styleable.RotaryKnobView_initialValue, 1);
            knobDrawable = a.getDrawable(R.styleable.RotaryKnobView_knobDrawable);
            knobImageView.setImageDrawable(this.knobDrawable);
        } finally {
            a.recycle();
        }
        gestureDetector = new GestureDetectorCompat(context, (GestureDetector.OnGestureListener)this);
    }

    public final RotaryKnobView.RotaryKnobListener getListener() {
        return this.listener;
    }

    public final void setListener( RotaryKnobView.RotaryKnobListener newListener) {
        this.listener = newListener;
    }

    public final int getValue() {
        return this.value;
    }

    public final void setValue(int newValue) {
        this.value = newValue;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        System.out.println("IN ONSCROLL");
        float rotationDegrees = calculateAngle(e2.getX(), e2.getY());
        // use only -150 to 150 range (knob min/max points
        if (rotationDegrees >= -150 && rotationDegrees <= 150) {
            setKnobPosition(rotationDegrees);

            // Calculate rotary value
            // The range is the 300 degrees between -150 and 150, so we'll add 150 to adjust the
            // range to 0 - 300
            float valueRangeDegrees = rotationDegrees + 150;
            value = (int)((valueRangeDegrees / divider) + minValue);
            System.out.println("VALUE = "+value);
            if (listener != null){
                listener.onRotate(value);
            }
        }
        return true;
    }

    interface RotaryKnobListener {
        void onRotate(int value);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        System.out.println("IN ONTOUCHEVENT");
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }else {
            return super.onTouchEvent(event);
        }
    }

    private float calculateAngle(float x, float y) {
        float px = (float)((x / knobWidth) - 0.5);
        float py = (float)(( 1 - y / knobHeight) - 0.5);
        float angle = (float)-(Math.toDegrees(Math.atan2(py, px))) + 90;
        if (angle > 180) angle -= 360;
        return angle;
    }

    private void setKnobPosition(float degrees) {
        System.out.println("IN SETKNOBPOSITION");
        System.out.println("DEGREES = "+ degrees);
        Matrix matrix = new Matrix();
        knobImageView.setScaleType(ImageView.ScaleType.MATRIX);
        System.out.println("(" + knobWidth +", " + knobHeight + ")");
        matrix.setRotate(degrees, knobWidth/2, knobHeight/2);
        knobImageView.setImageMatrix(matrix);
    }
}
