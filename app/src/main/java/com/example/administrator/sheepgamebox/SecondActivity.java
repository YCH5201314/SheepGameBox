package com.example.administrator.sheepgamebox;

/**
 * Created by Administrator on 2018\1\6 0006.
 */

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class SecondActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Button button;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new MySecondSurfaceView(this));
        //音乐
        mediaPlayer=MediaPlayer.create(this,R.raw.musica);
        mediaPlayer.start();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mediaPlayer.stop();
    }
}