package com.example.administrator.sheepgamebox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;
import java.util.Stack;
public class MazeView extends SurfaceView implements SurfaceHolder.Callback,
        Runnable {
    //用于控制SurfaceView
    private SurfaceHolder sfh;
    //声明一条线程
    private Thread th;
    //线程消亡的标识位
    private boolean flag;
    //声明屏幕宽高
    public static int screenW,screenH;
    //声明一个Resources实例便于加载图片
    private Resources res = this.getResources();
    //声明游戏需要用到的图片资源（图片声明）
    Bitmap sheepleft,sheepright,sheepup,sheepdown,sl,sr,su,sd,drawsheep;
    private Bitmap newbm,bm;
    //声明画布
    private Canvas canvas;
    //声明画笔
    private Paint paintmaze;
    // width 每个格子的宽度和高度
    public int NUM = 12, width = 80, padding = 10;
    //声明格子阵
    Lattice[][] maze;
    //声明一只羊
    Sheep sheep;
    public MazeView(Context context) {
        super(context);
        sfh = this.getHolder();
        //为sfh添加监听
        sfh.addCallback((SurfaceHolder.Callback) this);
        //实例化画笔
        paintmaze = new Paint();
        //实例化画布
        canvas = new Canvas();
        setFocusable(true);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder){
        screenW = this.getWidth();
        screenH = this.getHeight();
        flag = true;
        initGame();//初始化游戏
        th = new Thread(this);
        th.start();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    //初始化游戏函数
    private void createMaze() {
        for(int i = 0; i < NUM; i++){
            for (int j = 0; j < NUM; j++){
                maze[i][j] = new Lattice(i, j);
                maze[i][j].setFather(null);
                maze[i][j].setFlag(Lattice.NOTINTREE);
            }
        }
        Random random = new Random();
        int rx = Math.abs(random.nextInt()) % NUM;
        int ry = Math.abs(random.nextInt()) % NUM;
        Stack<Lattice> s = new Stack<>();
        Lattice p = maze[rx][ry];
        Lattice neis[];
        s.push(p);
        while (!s.isEmpty()) {
            p = s.pop();
            p.setFlag(Lattice.INTREE);
            neis = getNeis(p);
            int ran = Math.abs(random.nextInt()) % 4;
            for (int a = 0; a <= 3; a++) {
                ran++;
                ran %= 4;
                if ((neis != null ? neis[ran] : null) == null|| neis[ran].getFlag() == Lattice.INTREE  ) {
                    continue;
                }
                s.push(neis[ran]);
                neis[ran].setFather(p);
            }
        }
    }
    private boolean isOutOfBorder(Lattice p) {
        return isOutOfBorder(p.getX(), p.getY());
    }
    private boolean isOutOfBorder(int x, int y) {
        if (x > NUM - 1 || y > NUM - 1 || x < 0 || y < 0) return true;
        else return false;
    }
    private Lattice[] getNeis(Lattice p) {
        final int[] adds = {-1, 0, 1, 0, -1};// 顺序为上右下左
        if (isOutOfBorder(p)) {
            return null;
        }
        Lattice[] ps = new Lattice[4];// 顺序为上右下左
        int xt;
        int yt;
        for (int i = 0; i <= 3; i++) {
            xt = p.getX() + adds[i + 1];
            yt = p.getY() + adds[i];
            if (isOutOfBorder(xt, yt)) {
                ps[i] = null;
                continue;
            }
            ps[i] = maze[xt][yt];
        }
        return ps;
    }
    private void setDrawMaze() {
        //设置背景图
        bm = BitmapFactory.decodeResource(res, R.drawable.bg);
        //得到新的图片
        newbm = Bitmap.createScaledBitmap(bm, screenW, screenH, true);
        //羊羊图片
        sl = BitmapFactory.decodeResource(res,R.drawable.xyyleft);
        sr = BitmapFactory.decodeResource(res,R.drawable.xyyright);
        su = BitmapFactory.decodeResource(res,R.drawable.xyyup);
        sd = BitmapFactory.decodeResource(res,R.drawable.xyydown);
        sheepleft = Bitmap.createScaledBitmap(sl, width, width, true);
        sheepdown = Bitmap.createScaledBitmap(sd, width, width, true);
        sheepright = Bitmap.createScaledBitmap(sr, width, width, true);
        sheepup = Bitmap.createScaledBitmap(su, width, width, true);
        drawsheep = sheepdown;
    }
    private void initGame(){
        //实例格子阵
        maze = new Lattice[NUM][NUM];
        //实例羊
        sheep = new Sheep(0,0);
        setDrawMaze();
        createMaze();

    }
    //自定义绘图函数
    public void myDraw(){
        try {
            canvas = sfh.lockCanvas();
            paintmaze.setColor(Color.BLACK);
            paintmaze.setStrokeWidth(10);
            canvas.drawBitmap(newbm, 0, 0, paintmaze);
            canvas.drawLine(padding,padding,padding,padding+NUM*width,paintmaze);
            canvas.drawLine(padding+width,padding,padding+NUM*width,padding,paintmaze);
            canvas.drawLine(padding+NUM*width,padding,padding+NUM*width,padding+NUM*width,paintmaze);
            canvas.drawLine(padding+(NUM-1)*width,padding+NUM*width,padding,padding+NUM*width,paintmaze);
            for(int i = 0;i < NUM;i++){
                for(int j = 0;j < NUM;j++){
                    Lattice site = maze[i][j];
                    Lattice[] nextsite;
                    nextsite= getNeis(site);
                    for(int k = 0;k < 4;k++){
                        if((nextsite != null ? nextsite[k] : null) == null||nextsite[k] == site.getFather()||nextsite[k].getFather() == site)
                            continue;
                        switch (k){
                            case 0:canvas.drawLine(padding+site.getX()*width,padding+site.getY()*width,padding+site.getX()*width + width,padding+site.getY()*width,paintmaze);break;
                            case 1:canvas.drawLine(padding+site.getX()*width + width,padding+site.getY()*width,padding+site.getX()*width+width,padding+site.getY()*width+width,paintmaze);break;
                            case 2:canvas.drawLine(padding+site.getX()*width,padding+site.getY()*width + width,padding+site.getX()*width + width,padding+site.getY()*width + width,paintmaze);break;
                            case 3:canvas.drawLine(padding+site.getX()*width,padding+site.getY()*width,padding+site.getX()*width,padding+site.getY()*width + width,paintmaze);break;
                        }
                    }
                }
            }
            canvas.drawBitmap(drawsheep,sheep.x*width+padding,sheep.y*width+padding,paintmaze);
            if(sheep.x >= NUM && sheep.y >= NUM){
                paintmaze.setTextSize(120);
                canvas.drawText("YOU WIN",screenW/2,screenH/2,paintmaze);
            }

        } catch (Exception e){
            //TODO:handle exception
        }finally {
            if(canvas != null){
                sfh.unlockCanvasAndPost(canvas);
            }
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
//触屏监听
    public boolean onTouchEvent(MotionEvent event){
        int getx,gety;
        int changex,changey;
        int x ,y;
        getx = (int)event.getX();
        gety = (int)event.getY();
        changex = sheep.x;
        changey = sheep.y;
        x = getx - sheep.x*width+padding;
        y = gety - sheep.y*width+padding;
        if(x - y > 0 && x + y > 0){
            changex++;
            if(!isOutOfBorder(changex,changey) && (maze[changex][changey].getFather() == maze[sheep.x][sheep.y]
                    || maze[changex][changey].getFather() == maze[sheep.x][sheep.y])
                    ){
                sheep.x = changex;
                sheep.y = changey;
                drawsheep = sheepright;
            }
        }
        else if(x - y > 0 && y + x < 0){
            changey--;
            if(!isOutOfBorder(changex,changey) && (maze[changex][changey].getFather() == maze[sheep.x][sheep.y]
                    || maze[changex][changey].getFather() == maze[sheep.x][sheep.y])
                    ){
                sheep.x = changex;
                sheep.y = changey;
                drawsheep = sheepup;
            }
        }
        else if(x - y < 0 && y + x < 0 ){
            changex--;
            if(!isOutOfBorder(changex,changey)&& (maze[changex][changey].getFather() == maze[sheep.x][sheep.y]
                    || maze[changex][changey].getFather() == maze[sheep.x][sheep.y])
                    ){
                sheep.x = changex;
                sheep.y = changey;
                drawsheep = sheepleft;
                invalidate();
            }
        }
        else if(x - y < 0 && y + x > 0){
            changey++;
            if(!isOutOfBorder(changex,changey) && (maze[changex][changey].getFather() == maze[sheep.x][sheep.y]
                    || maze[changex][changey].getFather() == maze[sheep.x][sheep.y])
                    ){
                sheep.x = changex;
                sheep.y = changey;
                drawsheep = sheepdown;
            }
        }
        return super.onTouchEvent(event);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        return super.onKeyDown(keyCode,event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){

        return super.onKeyUp(keyCode,event);
    }
    private void logic(){

    }
    @Override
    public void run(){
        while (flag) {
            long start = System.currentTimeMillis();
            myDraw();
            logic();
            long end = System.currentTimeMillis();
            try {
                if (end - start < 50) {
                    Thread.sleep(50 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class Lattice {
    static final int INTREE = 1;
    static final int NOTINTREE = 0;
    private int x = -1;
    private int y = -1;
    private int flag = NOTINTREE;
    private Lattice father = null;
    Lattice(int xx, int yy) {
        x = xx;
        y = yy;
    }
    int getX() {
        return x;
    }
    int getY() {
        return y;
    }
    int getFlag() {
        return flag;
    }
    Lattice getFather() {
        return father;
    }
    void setFather(Lattice f) {
        father = f;
    }
    void setFlag(int f) {
        flag = f;
    }
    public String toString() {
        return "(" + x + "," + y + ")\n";}
}
class Sheep{
    public int x,y;
    Bitmap sheepleft,sheepright,sheepup,sheepdown,sl,sr,su,sd;
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setX(int x) {
        this.x = x;
        return;
    }
    public void setY(int y) {
        this.y = y;
        return;
    }
    public Sheep(int x,int y){
        this.x = x;
        this.y = y;
    }
}









