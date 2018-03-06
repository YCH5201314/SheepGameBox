package com.example.administrator.sheepgamebox;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by Administrator on 2017\11\23 0023.
 */

public class MySecondSurfaceView extends SurfaceView implements
        SurfaceHolder.Callback {
    private Canvas canvas;
    private SurfaceHolder sfh;
    private Paint paint;
    private int textX = 8, textY = 3;
    //第几关
    private int k=0;
    //有几只羊
    private int num=0;private int total=3;

    public MySecondSurfaceView(Context context) {
        super(context);
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        paint.setColor(Color.WHITE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        myDraw();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    //创建人物对象
    Bitmap wolfzm = BitmapFactory.decodeResource(this.getResources(), R.drawable.wolfzm);
    Bitmap wolfzb = BitmapFactory.decodeResource(this.getResources(), R.drawable.wolfzb);
    Bitmap wolfym = BitmapFactory.decodeResource(this.getResources(), R.drawable.wolfym);
    Bitmap wolfsm = BitmapFactory.decodeResource(this.getResources(), R.drawable.wolfsm);

    //创建障碍对象---树
    Bitmap tree = BitmapFactory.decodeResource(this.getResources(), R.drawable.tree);
    //创建背景对象
    Bitmap floor = BitmapFactory.decodeResource(this.getResources(), R.drawable.boxfloor);
    //创建箱子对象---羊
    Bitmap sheepno = BitmapFactory.decodeResource(this.getResources(), R.drawable.sheepno);
    Bitmap sheep = BitmapFactory.decodeResource(this.getResources(), R.drawable.sheep);
    //创建笼子对象
    Bitmap target = BitmapFactory.decodeResource(this.getResources(), R.drawable.target);
    //胜利显示图片
    Bitmap victor = BitmapFactory.decodeResource(this.getResources(), R.drawable.victory);

    public void myDraw() {
        Canvas canvas = sfh.lockCanvas();
        canvas.drawColor(Color.GREEN);
        canvas.drawBitmap(floor, 0, 0, paint);
        for (int i = 0; i < zhangai[k].length; i++) {
            for (int j = 0; j < zhangai[k][i].length; j++) {
                if (zhangai[k][i][j] == 1) {
                    canvas.drawBitmap(tree, 20 + j * 75, i * 75, paint);
                }
                if (zhangai[k][i][j] == 4) {
                    canvas.drawBitmap(sheepno, 20 + j * 75, i * 75, paint);
                }
                if (zhangai[k][i][j] == 8) {
                    canvas.drawBitmap(target, 20 + j * 75, i * 75, paint);
                }
                if (zhangai[k][i][j] == 12) {
                    canvas.drawBitmap(sheep, 20 + j * 75, i * 75, paint);
                    canvas.drawBitmap(target, 20 + j * 75, i * 75, paint);
                }
            }
        }
        if (flag == 37)
            canvas.drawBitmap(wolfzb, 20 + textY * 75, textX * 75, paint);
        if (flag == 38)
            canvas.drawBitmap(wolfsm, 20 + textY * 75, textX * 75, paint);
        if (flag == 39)
            canvas.drawBitmap(wolfym, 20 + textY * 75, textX * 75, paint);
        if (flag == 40)
            canvas.drawBitmap(wolfzm, 20 + textY * 75, textX * 75, paint);
        if (victory != 0) {
            canvas.drawBitmap(victor, 50, 300, paint);
        }
        sfh.unlockCanvasAndPost(canvas);
    }
    int flag=40;
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //37 向左   38  向上     39  向右  40   向下   以flag为标识位
        //玩家手指点击屏幕的动作
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            //向右移动
            if((int)event.getX()-(20+textY*75)>=75&&(int)event.getY()-textX*75<=75&&(int)event.getY()-textX*75>0) {
                //人 树
                if(zhangai[k][textX][textY+1]==1)
                {
                    flag=39;
                    myDraw();
                }
                //人 箱子 箱子
                else if(zhangai[k][textX][textY+1]==4&&zhangai[k][textX][textY+2]==4)
                {
                    flag=39;
                    myDraw();
                }
                //人 箱子 树
                else  if(zhangai[k][textX][textY+1]==4&&zhangai[k][textX][textY+2]==1)
                {
                    flag=39;
                    myDraw();
                }
                //人 箱子 目标箱子
                else if(zhangai[k][textX][textY+1]==4&&zhangai[k][textX][textY+2]==12)
                {
                    flag=39;
                    myDraw();
                }
                //人 目标箱子 树木
                else  if(zhangai[k][textX][textY+1]==12&&zhangai[k][textX][textY+2]==1)
                {
                    flag=39;
                    myDraw();
                }
                //人 目标箱子 箱子
                else if(zhangai[k][textX][textY+1]==12&&zhangai[k][textX][textY+2]==4)
                {
                    flag=39;
                    myDraw();
                }
                //人 目标箱子 目标箱子
                else if(zhangai[k][textX][textY+1]==12&&zhangai[k][textX][textY+2]==12)
                {
                    flag=39;
                    myDraw();
                }
                //人 空地
                else if(zhangai[k][textX][textY+1]==0)
                {
                    textY=textY+1;
                    flag=39;
                    myDraw();
                }
                //人 空目标
                else  if(zhangai[k][textX][textY+1]==8)
                {
                    textY=textY+1;
                    flag=39;
                    myDraw();
                }
                //人 箱子 空地
                else if(zhangai[k][textX][textY+1]==4&&zhangai[k][textX][textY+2]==0)
                {
                    zhangai[k][textX][textY+2]=4;
                    zhangai[k][textX][textY+1]=0;
                    textY=textY+1;
                    flag=39;
                    myDraw();
                }
                //人 箱子 空目标
                else  if(zhangai[k][textX][textY+1]==4&&zhangai[k][textX][textY+2]==8)
                {
                    zhangai[k][textX][textY+2]=12;
                    zhangai[k][textX][textY+1]=0;
                    textY=textY+1;
                    flag=39;
                    num++;
                    if(num==total)
                    {
                        victory=1;
                        myDraw();
                        k++;
                        if(k==1)
                        {
                            victory=0;
                            num=2;
                            total=3;
                            textX=7;
                            textY=2;
                            myDraw();
                        }
                        if(k==2)
                        {
                            victory=0;
                            num=1;
                            total=3;
                            textX=5;
                            textY=3;
                            myDraw();
                        }
                    }else
                        myDraw();
                }
                //人 目标箱子 空地
                else  if(zhangai[k][textX][textY+1]==12&&zhangai[k][textX][textY+2]==0)
                {
                    zhangai[k][textX][textY+2]=4;
                    zhangai[k][textX][textY+1]=8;
                    textY=textY+1;
                    flag=39;
                    num--;
                    myDraw();
                }
                //人 目标箱子 空目标
                else  if(zhangai[k][textX][textY+1]==12&&zhangai[k][textX][textY+2]==8)
                {
                    zhangai[k][textX][textY+2]=12;
                    zhangai[k][textX][textY+1]=8;
                    textY=textY+1;
                    flag=39;
                    myDraw();
                }
            }

            //向左移动
            if((int)event.getX()-(20+textY*75)<0&&(int)event.getY()-textX*75<=75&&(int)event.getY()-textX*75>0){
                //人 树
                if(zhangai[k][textX][textY-1]==1)
                {
                    flag=37;
                    myDraw();
                }
                //人 箱子 箱子
                else  if(zhangai[k][textX][textY-1]==4&&zhangai[k][textX][textY-2]==4)
                {
                    flag=37;
                    myDraw();
                }
                //人 箱子 树
                else if(zhangai[k][textX][textY-1]==4&&zhangai[k][textX][textY-2]==1)
                {
                    flag=37;
                    myDraw();
                }
                //人 箱子 目标箱子
                else if(zhangai[k][textX][textY-1]==4&&zhangai[k][textX][textY-2]==12)
                {
                    flag=37;
                    myDraw();
                }
                //人 目标箱子 树木
                else  if(zhangai[k][textX][textY-1]==12&&zhangai[k][textX][textY-2]==1)
                {
                    flag=37;
                    myDraw();
                }
                //人 目标箱子 箱子
                else if(zhangai[k][textX][textY-1]==12&&zhangai[k][textX][textY-2]==4)
                {
                    flag=37;
                    myDraw();
                }
                //人 目标箱子 目标箱子
                else if(zhangai[k][textX][textY-1]==12&&zhangai[k][textX][textY-2]==12)
                {
                    flag=37;
                    myDraw();
                }
                //人 空地
                else  if(zhangai[k][textX][textY-1]==0)
                {
                    textY=textY-1;
                    flag=37;
                    myDraw();
                }
                //人 空目标
                else if(zhangai[k][textX][textY-1]==8)
                {
                    textY=textY-1;
                    flag=37;
                    myDraw();
                }
                //人 箱子 空地
                else  if(zhangai[k][textX][textY-1]==4&&zhangai[k][textX][textY-2]==0)
                {
                    zhangai[k][textX][textY-2]=4;
                    zhangai[k][textX][textY-1]=0;
                    textY=textY-1;
                    flag=37;
                    myDraw();
                }
                //人 箱子 空目标
                else   if(zhangai[k][textX][textY-1]==4&&zhangai[k][textX][textY-2]==8)
                {
                    zhangai[k][textX][textY-2]=12;
                    zhangai[k][textX][textY-1]=0;
                    textY=textY-1;
                    flag=37;
                    num++;
                    if(num==total)
                    {
                        victory=1;
                        myDraw();
                        k++;
                        if(k==1)
                        {
                            victory=0;
                            num=2;
                            total=3;
                            textX=7;
                            textY=2;
                            myDraw();
                        }
                        if(k==2)
                        {
                            victory=0;
                            num=1;
                            total=3;
                            textX=5;
                            textY=3;
                            myDraw();
                        }
                    }else
                        myDraw();
                }
                //人 目标箱子 空地
                else  if(zhangai[k][textX][textY-1]==12&&zhangai[k][textX][textY-2]==0)
                {
                    zhangai[k][textX][textY-2]=4;
                    zhangai[k][textX][textY-1]=8;
                    textY=textY-1;
                    flag=37;
                    num--;
                    myDraw();
                }
                //人 目标箱子 空目标
                else if(zhangai[k][textX][textY-1]==12&&zhangai[k][textX][textY-2]==8)
                {
                    zhangai[k][textX][textY-2]=12;
                    zhangai[k][textX][textY-1]=8;
                    textY=textY-1;
                    flag=37;
                    myDraw();
                }
            }

            //向上移动
            if((int)event.getY()-textX*75<0&&(int)event.getX()-20-textY*75<=75&&(int)event.getX()-20-textY*75>0){
                //人 树
                if(zhangai[k][textX-1][textY]==1)
                {
                    flag=38;
                    myDraw();
                }
                //人 箱子 箱子
                else if(zhangai[k][textX-1][textY]==4&&zhangai[k][textX-2][textY]==4)
                {
                    flag=38;
                    myDraw();
                }
                //人 箱子 树
                else if(zhangai[k][textX-1][textY]==4&&zhangai[k][textX-2][textY]==1)
                {
                    flag=38;
                    myDraw();
                }
                //人 箱子 目标箱子
                else  if(zhangai[k][textX-1][textY]==4&&zhangai[k][textX-2][textY]==12)
                {
                    flag=38;
                    myDraw();
                }
                //人 目标箱子 树木
                else  if(zhangai[k][textX-1][textY]==12&&zhangai[k][textX-2][textY]==1)
                {
                    flag=38;
                    myDraw();
                }
                //人 目标箱子 箱子
                else if(zhangai[k][textX-1][textY]==12&&zhangai[k][textX-2][textY]==4)
                {
                    flag=38;
                    myDraw();
                }
                //人 目标箱子 目标箱子
                else if(zhangai[k][textX-1][textY]==12&&zhangai[k][textX-2][textY]==12)
                {
                    flag=38;
                    myDraw();
                }
                //人 空地
                else if(zhangai[k][textX-1][textY]==0)
                {
                    textX=textX-1;
                    flag=38;
                    myDraw();
                }
                //人 空目标
                else  if(zhangai[k][textX-1][textY]==8)
                {
                    textX=textX-1;
                    flag=38;
                    myDraw();
                }
                //人 箱子 空地
                else  if(zhangai[k][textX-1][textY]==4&&zhangai[k][textX-2][textY]==0)
                {
                    zhangai[k][textX-2][textY]=4;
                    zhangai[k][textX-1][textY]=0;
                    textX=textX-1;
                    flag=38;
                    myDraw();
                }
                //人 箱子 空目标
                else if(zhangai[k][textX-1][textY]==4&&zhangai[k][textX-2][textY]==8)
                {
                    zhangai[k][textX-2][textY]=12;
                    zhangai[k][textX-1][textY]=0;
                    textX=textX-1;
                    flag=38;
                    num++;
                    if(num==total)
                    {
                        victory=1;
                        myDraw();
                        k++;
                        if(k==1)
                        {
                            victory=0;
                            num=2;
                            total=3;
                            textX=7;
                            textY=2;
                            myDraw();
                        }
                        if(k==2)
                        {
                            victory=0;
                            num=1;
                            total=3;
                            textX=5;
                            textY=3;
                            myDraw();
                        }
                    }
                    else
                        myDraw();
                }
                //人 目标箱子 空地
                else if(zhangai[k][textX-1][textY]==12&&zhangai[k][textX-2][textY]==0)
                {
                    zhangai[k][textX-2][textY]=4;
                    zhangai[k][textX-1][textY]=8;
                    textX=textX-1;
                    flag=38;
                    num--;
                    myDraw();
                }
                //人 目标箱子 空目标
                else  if(zhangai[k][textX-1][textY]==12&&zhangai[k][textX-2][textY]==8)
                {
                    zhangai[k][textX-2][textY]=12;
                    zhangai[k][textX-1][textY]=8;
                    textX=textX-1;
                    flag=38;
                    myDraw();
                }
            }
            //向下移动
            if ((int) event.getY() - textX * 75 >= 75 && (int) event.getX() - 20 - textY * 75 <=75&&(int) event.getX() - 20 - textY * 75 >0) {
                //人 树
                if(zhangai[k][textX+1][textY]==1)
                {
                    flag=40;
                    myDraw();
                }
                //人 箱子 箱子
                else if(zhangai[k][textX+1][textY]==4&&zhangai[k][textX+2][textY]==4)
                {
                    flag=40;
                    myDraw();
                }
                //人 箱子 树
                else if(zhangai[k][textX+1][textY]==4&&zhangai[k][textX+2][textY]==1)
                {
                    flag=40;
                    myDraw();
                }
                //人 箱子 目标箱子
                else if(zhangai[k][textX+1][textY]==4&&zhangai[k][textX+2][textY]==12)
                {
                    flag=40;
                    myDraw();
                }
                //人 目标箱子 树木
                else if(zhangai[k][textX+1][textY]==12&&zhangai[k][textX+2][textY]==1)
                {
                    flag=40;
                    myDraw();
                }
                //人 目标箱子 箱子
                else if(zhangai[k][textX+1][textY]==12&&zhangai[k][textX+2][textY]==4)
                {
                    flag=40;
                    myDraw();
                }
                //人 目标箱子 目标箱子
                else if(zhangai[k][textX+1][textY]==12&&zhangai[k][textX+2][textY]==12)
                {
                    flag=40;
                    myDraw();
                }
                //人 空地
                else if(zhangai[k][textX+1][textY]==0)
                {
                    textX=textX+1;
                    flag=40;
                    myDraw();
                }
                //人 空目标
                else if(zhangai[k][textX+1][textY]==8)
                {
                    textX=textX+1;
                    flag=40;
                    myDraw();
                }
                //人 箱子 空地
                else if(zhangai[k][textX+1][textY]==4&&zhangai[k][textX+2][textY]==0)
                {
                    zhangai[k][textX+2][textY]=4;
                    zhangai[k][textX+1][textY]=0;
                    textX=textX+1;
                    flag=40;
                    myDraw();
                }
                //人 箱子 空目标
                else if(zhangai[k][textX+1][textY]==4&&zhangai[k][textX+2][textY]==8)
                {
                    zhangai[k][textX+2][textY]=12;
                    zhangai[k][textX+1][textY]=0;
                    textX=textX+1;
                    flag=40;
                    num++;
                    if(num==total)
                    {
                        victory=1;
                        myDraw();
                        k++;
                        if(k==1)
                        {
                            victory=0;
                            num=2;
                            total=3;
                            textX=7;
                            textY=2;
                            myDraw();
                        }
                        if(k==2)
                        {
                            victory=0;
                            num=1;
                            total=3;
                            textX=5;
                            textY=3;
                            myDraw();
                        }
                    }else
                        myDraw();
                }
                //人 目标箱子 空地
                else  if(zhangai[k][textX+1][textY]==12&&zhangai[k][textX+2][textY]==0)
                {
                    zhangai[k][textX+2][textY]=4;
                    zhangai[k][textX+1][textY]=8;
                    textX=textX+1;
                    flag=40;
                    num--;
                    myDraw();
                }
                //人 目标箱子 空目标
                else  if(zhangai[k][textX+1][textY]==12&&zhangai[k][textX+2][textY]==8)
                {
                    zhangai[k][textX+2][textY]=12;
                    zhangai[k][textX+1][textY]=8;
                    textX=textX+1;
                    flag=40;
                    myDraw();
                }
            }
        }
        return true;
    }
    int victory=0;
    int[][][]zhangai={
            {
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {1,1,1,1,1,1,1,1,0},
                    {1,0,0,1,1,0,0,1,0},
                    {1,0,8,0,0,8,4,1,0},
                    {1,0,4,1,0,4,8,1,0},
                    {1,1,0,0,0,1,0,1,0},
                    {0,1,1,1,0,0,0,1,0},
                    {0,0,0,1,1,1,1,1,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
            },
            {
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,1,1,1,1,1,1,1,0},
                    {0,1,0,8,0,12,0,1,0},
                    {0,1,0,0,1,0,0,1,0},
                    {0,1,0,4,12,0,0,1,0},
                    {0,1,1,0,0,0,1,1,0},
                    {0,0,1,0,0,0,1,0,0},
                    {0,0,1,1,1,1,1,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
            },
            {
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,1,1,1,1,1,1,1,0},
                    {1,1,0,0,0,0,0,1,0},
                    {1,0,1,1,0,1,0,1,0},
                    {1,0,1,1,0,4,0,1,0},
                    {1,0,0,0,12,8,0,1,0},
                    {1,0,1,4,8,1,0,1,0},
                    {1,0,0,0,0,0,1,1,0},
                    {1,1,1,1,1,1,1,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0},
            },
    };

}

