package com.example.administrator.sheepgamebox;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by Administrator on 2017\12\22 0022.
 */

public class MySurfaceView extends SurfaceView implements Callback ,Runnable{
    private SurfaceHolder sfh;//用于控制SurfaceView
    private Paint paint;//声明背景画笔
    private int foodX,foodY;//食物坐标
    private Thread th;//声明一条线程
    private boolean flag;//线程消亡的标识位
    private Canvas canvas;//声明一个画布
    private int screenW,screenH;//声明屏幕的宽高
    private int X=300;//500毫秒刷新一次，控制狼的速度
    // 定义游戏去相关属性
    private static final int ROWS = 8; // 游戏去行数
    private static final int COLS = 14; // 游戏去列数
    // 蛇身相关属性
    private int length = 3; // 蛇身的初始长度
    private int[] rows = new int[ROWS * COLS]; // 记录蛇身每个方块的行号
    private int[] columes = new int[ROWS * COLS]; // 记录蛇身的每个方格
    public static final int UP = 1, LEFT = 2, DOWN = 3, RIGHT = 4;// 蛇运动方向
    private int direction = RIGHT; // 用户按键的方向
    private int lastdirection = RIGHT; // 蛇当前正在运动的方向

    //创建图片对象
    Bitmap floor= BitmapFactory.decodeResource(this.getResources(),R.drawable.floor);
    Bitmap wolfzm=BitmapFactory.decodeResource(this.getResources(),R.drawable.wolfzm);
    Bitmap wolfsm=BitmapFactory.decodeResource(this.getResources(),R.drawable.wolfsm);
    Bitmap wolfzb=BitmapFactory.decodeResource(this.getResources(),R.drawable.wolfzb);
    Bitmap wolfym=BitmapFactory.decodeResource(this.getResources(),R.drawable.wolfym);
    Bitmap sheepno=BitmapFactory.decodeResource(this.getResources(),R.drawable.sheepno);
    Bitmap tree=BitmapFactory.decodeResource(this.getResources(),R.drawable.tree);
    Bitmap sheep=BitmapFactory.decodeResource(this.getResources(),R.drawable.sheep);
    Bitmap gameover=BitmapFactory.decodeResource(this.getResources(),R.drawable.gameover);
    public MySurfaceView(Context context) {
        super(context);
        //实例化SurfaceHolder
        sfh=this.getHolder();
        //为SurfaceViwe添加状态监听
        sfh.addCallback((Callback) this);
        //实例画笔
        paint=new Paint();
        //设置画笔颜色
        paint.setColor(Color.WHITE);
        //设置焦点
        setFocusable(true);
    }
    //SurfaceView视图创建，响应此函数
    @Override
    public void surfaceCreated(SurfaceHolder holder){
        screenW=this.getWidth();
        screenH=this.getHeight();
        flag=true;
        //实例线程
        th=new Thread( this);
        //启动线程
        th.start();
        Init();
    }
    public void Init(){
        food();
        createSnake();
    }

    /*
     游戏绘图
     */
    public void myDraw(){
        try{
            canvas=sfh.lockCanvas();
            if(canvas!=null){
                canvas.drawBitmap(floor,0,0,paint);
                for(int i=0;i<wall.length;i++){
                    for(int j=0;j<wall[i].length;j++){
                        if(wall[i][j]==1){
                            canvas.drawBitmap(tree,15+j*75,15+i*75,paint);
                        }
                        canvas.drawBitmap(tree,15,15,paint);
                        if(wall[i][j]==4){
                            canvas.drawBitmap(sheep,15+j*75,15+i*75,paint);
                        }
                        if(wall[i][j]==8){
                            canvas.drawBitmap(sheepno,15+j*75,15+i*75,paint);
                        }
                    }
                }
                switch (direction){//蛇头wall[rows[0]][columes[0]]
                    case UP:
                        canvas.drawBitmap(wolfsm,15+columes[0]*75,15+rows[0]*75,paint);
                        break;
                    case DOWN:
                        canvas.drawBitmap(wolfzm,15+columes[0]*75,15+rows[0]*75,paint);
                        break;
                    case LEFT:
                        canvas.drawBitmap(wolfzb,15+columes[0]*75,15+rows[0]*75,paint);
                        break;
                    case RIGHT:
                        canvas.drawBitmap(wolfym,15+columes[0]*75,15+rows[0]*75,paint);
                        break;
                    default:
                        break;
                }
                if(!flag){
                    canvas.drawBitmap(gameover,40,350,paint);
                }
            }
        }catch ( Exception e ){

        }finally {
            if(canvas!=null){
                sfh.unlockCanvasAndPost(canvas);
            }
        }
    }
    /*
    触屏事件监听
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){
        //玩家点击屏幕的动作
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            //向左走
            if(event.getY()/screenH-event.getX()/screenW>0&&
                    event.getY()/screenH+event.getX()/screenW-1<0){
                if(direction==RIGHT){
                }else{
                    lastdirection=direction;
                    direction=LEFT;
                }
            }
            //向上走
            if(event.getY()/screenH-event.getX()/screenW<0&&event.getY()/screenH+event.getX()/screenW-1<0){
                if(direction==DOWN){
                }else{
                    lastdirection=direction;
                    direction=UP;
                }
            }
            //向右走
            if(event.getY()/screenH-event.getX()/screenW<0&&event.getY()/screenH+event.getX()/screenW-1>0){
                if(direction==LEFT){
                }else{
                    lastdirection=direction;
                    direction=RIGHT;
                }
            }
            //向下走
            if(event.getY()/screenH-event.getX()/screenW>0&&event.getY()/screenH+event.getX()/screenW-1>0){
                if(direction==UP){
                }else {
                    lastdirection=direction;
                    direction=DOWN;
                }
            }
        }
        return true;
    }
    /*
    游戏逻辑
     */
    public void logic(){
        moveSnake();
    }
    @Override
    public void run(){
        while(flag){
            long start=System.currentTimeMillis();
            logic();
            myDraw();
            long end=System.currentTimeMillis();
            try {
                if(end-start<X){
                    Thread.sleep(X-(end-start));
                }
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }
    /*
    SurfaceView视图发生改变，响应此函数
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    /*
    SurfaceView视图消亡时，响应此函数
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag=false;
    }

    //随机出现食物
    public void food(){
        boolean b=true;
        do {
            //确保在屏幕里产生食物
            foodX=(int)(Math.random()*9);//0---8
            foodY=(int)(Math.random()*15);//0---14 wall[rows[0]][columes[0]]狼的位置
            if(wall[foodY][foodX]==1||wall[foodY][foodX]==4){
                b=false;
            }
            else if(foodY==rows[0]&&foodX==columes[0]){
                b=false;
            }
            else{
                b=true;
                wall[foodY][foodX]=8;
            }
        }while(!b);
    }

    // 创建蛇身
    public void createSnake() {
        length = 3; // 蛇身初始长度
        direction = RIGHT; // 蛇身运动方向
        lastdirection = RIGHT; // 蛇身在改变运动方向前的运动方向
        // 初始化蛇身位置
        for (int i = 0; i <length; i++) {
            rows[i] = 1;
            columes[i] = length - i;
            wall[rows[i]][columes[i]]=4;

        }
    }
    // 移动蛇
    public void moveSnake() {
        // 去掉蛇尾
        wall[rows[length]][columes[length]]=0;
        // 显示除蛇头外蛇身
        // 移动除蛇头外蛇身,记录新的蛇身的位置
        for (int i = length; i > 0; i--) {
            rows[i] = rows[i - 1];
            columes[i] = columes[i - 1];
            wall[rows[i]][columes[i]]=4;
        }
        // 根据蛇身运动方向，决定蛇头位置 蛇头wall[rows[0]][columes[0]]
        switch (direction) {
            case UP: {
                if (lastdirection == DOWN) {
                    rows[0] += 1;
                } else {
                    rows[0] -= 1;
                    lastdirection = UP;
                }
                break;
            }
            case LEFT: {
                if (lastdirection == RIGHT) {
                    columes[0] += 1;
                } else {
                    columes[0] -= 1;
                    lastdirection = LEFT;
                }
                break;
            }
            case DOWN: {
                if (lastdirection == UP) {
                    rows[0] -= 1;
                } else {
                    rows[0] += 1;
                    lastdirection = DOWN;
                }
                break;
            }
            case RIGHT: {
                if (lastdirection == LEFT) {
                    columes[0] -= 1;
                } else {
                    columes[0] += 1;
                    lastdirection = RIGHT;
                }
                break;
            }
        }
        //吃到羊
        if(wall[rows[0]][columes[0]]==8){
            length++;
            wall[rows[0]][columes[0]]=0;
            food();
            //吃的越多走的越慢
            if(length>=5&&length<7){
                X=500;
            }
            else if(length>=7&&length<9){
                X=600;
            }
            else if(length>=9)X=700;
        }
        //撞到树
        if(wall[rows[0]][columes[0]]==1){
            flag=false;

        }
        //撞到自己的身体
        if(wall[rows[0]][columes[0]]==4){
            flag=false;
        }
    }


    //重新开始游戏
    public void restart(){
        flag=true;
    }


    int wall[][]={
            {1,1,1,1,1,1,1,1,1},//8列  14行
            {1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,1,0,1},
            {1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1},
    };
}
