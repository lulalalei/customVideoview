package com.example.administrator.mymediaplay;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by Administrator on 2017/8/11.
 */

public class CustomVideoView extends VideoView{

    int defaultWidth=720;
    int defaultHeight=1280;

    public CustomVideoView(Context context) {
        this(context,null);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = getDefaultSize(defaultWidth,widthMeasureSpec);
        int heightSize = getDefaultSize(defaultHeight,heightMeasureSpec);
        setMeasuredDimension(widthSize,heightSize);
    }

}
