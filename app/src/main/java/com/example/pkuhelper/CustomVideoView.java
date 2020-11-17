package com.example.pkuhelper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class CustomVideoView extends VideoView {
    public CustomVideoView(Context context){
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public CustomVideoView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
    }
    protected void onMeasure(int widths,int heights){
        int width=getDefaultSize(0,widths);
        int height=getDefaultSize(0,heights);
        setMeasuredDimension(width,height);
    }
}
