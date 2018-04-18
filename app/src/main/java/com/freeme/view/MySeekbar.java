package com.freeme.view;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.SeekBar;
import android.util.AttributeSet;

public class MySeekbar extends SeekBar {

    public MySeekbar(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public MySeekbar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public MySeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return false;
    }


}
