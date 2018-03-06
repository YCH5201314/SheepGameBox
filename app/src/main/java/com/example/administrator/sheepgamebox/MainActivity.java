package com.example.administrator.sheepgamebox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        ImageButton snake=findViewById(R.id.imageButton1);
        ImageButton box=findViewById(R.id.imageButton2);
        ImageButton migong=findViewById(R.id.imageButton3);
        ImageButton plain=findViewById(R.id.imageButton4);
        snake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(MainActivity.this,FirstActivity.class);
                startActivity(intent1);
            }
        });
        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent2);
            }
        });
        migong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3=new Intent(MainActivity.this,ThirdActivity.class);
                startActivity(intent3);
            }
        });
        plain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent4=new Intent(MainActivity.this,FourthActivity.class);
                startActivity(intent4);
            }
        });


    }
}
