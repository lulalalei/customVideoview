package com.example.administrator.mymediaplay;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

public class NormalActivity extends AppCompatActivity {

    VideoView videoview;

    String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+
            File.separator+"video"+File.separator+"video.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_normal);
        videoview= (VideoView) findViewById(R.id.videoview);

        //网络播放
        //videoview.setVideoURI(Uri.parse(path));
        //本地视频播放
        videoview.setVideoPath(path);

        //使用MediaController控制视频播放
        MediaController controller = new MediaController(this);
        //设置videoview与MediaController建立关联
        videoview.setMediaController(controller);
        //设置MediaController与videoview建立关联，实现双向绑定
        controller.setMediaPlayer(videoview);
    }
}
