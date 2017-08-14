package com.example.administrator.mymediaplay;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

import static com.example.administrator.mymediaplay.R.id.pause_iv;

public class CustomActivity extends AppCompatActivity implements View.OnClickListener{

    RelativeLayout container_rl;
    CustomVideoView videoview;
    SeekBar seekbar_progress,seekbar_voice;
    ImageView playControl_iv,voice_iv,screen_iv;
    TextView time_current_tv,time_total_tv;

    String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+
            File.separator+"video"+File.separator+"video.mp4";

    //记录是否是全屏
    private boolean isFullScreen;
    //记录手势是否合法
    private boolean isAdjust=false;
    //误触临界值
    private int threshold=54;
    private final int UPDATE_UI=1;
    private int screen_width,screen_height;
    private AudioManager audioManager;
    private float mBrightness;
    float lastX=0,lastY=0;

    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==UPDATE_UI)
            {
                //获取总时间
                int duration = videoview.getDuration();
                updateTextViewWithTimeFormat(time_total_tv,duration);
                seekbar_progress.setMax(duration);
                //返回的是毫秒值
                int currentPosition = videoview.getCurrentPosition();
                updateTextViewWithTimeFormat(time_current_tv,currentPosition);
                seekbar_progress.setProgress(currentPosition);

                handler.sendEmptyMessageDelayed(UPDATE_UI,500);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        container_rl= (RelativeLayout) findViewById(R.id.container_rl);
        videoview= (CustomVideoView) findViewById(R.id.videoview);
        seekbar_progress= (SeekBar) findViewById(R.id.seekbar_progress);
        seekbar_voice= (SeekBar) findViewById(R.id.seekbar_voice);
        playControl_iv= (ImageView) findViewById(pause_iv);
        voice_iv= (ImageView) findViewById(R.id.voice_iv);
        screen_iv= (ImageView) findViewById(R.id.screen_iv);
        time_current_tv= (TextView) findViewById(R.id.time_current_tv);
        time_total_tv= (TextView) findViewById(R.id.time_total_tv);

        //获取屏幕的宽高
        screen_width=getResources().getDisplayMetrics().widthPixels;
        screen_height=getResources().getDisplayMetrics().heightPixels;

        playControl_iv.setOnClickListener(this);
        screen_iv.setOnClickListener(this);

        //本地视频播放
        videoview.setVideoPath(path);
        videoview.start();
        handler.sendEmptyMessage(UPDATE_UI);

        //获取系统的音频管理器
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //获取当前设备的音量最大值
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //获取设备当前的音量
        int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekbar_voice.setMax(maxVolume);
        seekbar_voice.setProgress(streamVolume);

        seekbar_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTextViewWithTimeFormat(time_current_tv,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始拖动时要停止ui的更新
                handler.removeMessages(UPDATE_UI);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止拖动时,得到当前进度并更新ui
                int progress = seekBar.getProgress();
                videoview.seekTo(progress);
                handler.sendEmptyMessage(UPDATE_UI);
            }
        });

        seekbar_voice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //设置当前设备的音量
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                //seekbar_voice.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //int progress = seekBar.getProgress();
                //seekbar_voice.setProgress(progress);
            }
        });

        //控制videoview的手势事件
        videoview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        lastX=x;
                        lastY=y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float detlaX = x - lastX;
                        float detlaY = y - lastY;
                        float absDetlaX = Math.abs(detlaX);
                        float absDetlaY = Math.abs(detlaY);
                        if (absDetlaX>threshold&&absDetlaY>threshold)
                        {
                            if (absDetlaX<absDetlaY)
                            {
                                isAdjust=true;
                            }
                            else
                            {
                                isAdjust=false;
                            }
                        }
                        else if (absDetlaX<threshold&&absDetlaY>threshold)
                        {
                            isAdjust=true;
                        }
                        else if (absDetlaX>threshold&&absDetlaY<threshold)
                        {
                            isAdjust=false;
                        }
                        if (isAdjust)
                        {
                            //调节亮度
                            if (x<screen_width/2)
                            {
                                if (detlaY>0)
                                {
                                    //降低亮度
                                }
                                else
                                {

                                }
                                changeBrightness(-detlaY);
                            }
                            else
                            {
                                if (detlaY>0)
                                {
                                    //降低音量
                                }
                                else
                                {

                                }
                                changeVolume(-detlaY);
                            }
                        }

                        Log.e("=======","==detlaY===="+detlaY+" ,detlaX="+detlaX);
                        break;
                    case MotionEvent.ACTION_UP:
                        lastX=event.getX();
                        lastY=event.getY();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    //调节音量
    private void changeVolume(float detlaY)
    {
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int index=(int)(detlaY/screen_height*maxVolume*3);
        int volume=Math.max(streamVolume+index,0);
        if (volume>=maxVolume)
        {
            volume=maxVolume;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume,0);
        seekbar_voice.setProgress(volume);
    }

    private void changeBrightness(float detlaY)
    {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        mBrightness = attributes.screenBrightness;
        float index = detlaY / screen_height / 3;
        mBrightness+=index;
        if (mBrightness>1.0f)
        {
            mBrightness=1.0f;
        }
        if (mBrightness<0.1f)
        {
            mBrightness=0.1f;
        }
        attributes.screenBrightness=mBrightness;
        getWindow().setAttributes(attributes);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.pause_iv:
                //控制视频的播放和暂停
                if (videoview.isPlaying())
                {
                    playControl_iv.setImageResource(R.drawable.play_btn_style);
                    //暂停播放
                    videoview.pause();
                    //停止刷新ui
                    handler.removeMessages(UPDATE_UI);
                }
                else
                {
                    playControl_iv.setImageResource(R.drawable.pause_btn_style);
                    //继续播放
                    videoview.start();
                    handler.sendEmptyMessage(UPDATE_UI);
                }
                break;
            case R.id.screen_iv:
                if (isFullScreen)
                {
                    //切换竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                else
                {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
            default:
                break;
        }
    }

    //格式化视频播放总时间
    private void updateTextViewWithTimeFormat(TextView textView,int millsecond)
    {
        int second=millsecond/1000;
        int hh=second/3600;
        int mm=second%3600/60;
        int ss=second%60;
        String string="";
        if (hh!=0)
        {
            string=String.format("%02d:%02d:%02d",hh,mm,ss);
        }
        else
        {
            string=String.format("%02d:%02d",mm,ss);
        }
        textView.setText(string);
    }

    private void setVideoViewScale(int width,int height)
    {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoview.getLayoutParams();
        params.width=width;
        params.height=height;
        videoview.setLayoutParams(params);

        ViewGroup.LayoutParams lp = container_rl.getLayoutParams();
        lp.width=width;
        lp.height=height;
        container_rl.setLayoutParams(lp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止刷新ui
        handler.removeMessages(UPDATE_UI);
    }

    //对屏幕方向改变的监听
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //当屏幕方向为横屏的时候
        if (getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE)
        {
            setVideoViewScale(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            seekbar_voice.setVisibility(View.VISIBLE);
            voice_iv.setVisibility(View.VISIBLE);
            isFullScreen=true;
            //移除半屏状态
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            //设置全屏状态
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else
        {
            setVideoViewScale(RelativeLayout.LayoutParams.MATCH_PARENT, dp2px(240));
            seekbar_voice.setVisibility(View.GONE);
            voice_iv.setVisibility(View.GONE);
            isFullScreen=false;
            //移除全屏状态
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //设置半屏状态
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    private int dp2px(float value)
    {
        final float scale = getResources().getDisplayMetrics().densityDpi;
        return (int)(value*(scale/160)+0.5f);
    }
}
