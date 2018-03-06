package com.example.administrator.sheepgamebox;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.administrator.sheepgamebox.R;

import java.util.Random;
import java.util.Vector;

public class PlaneFightView extends SurfaceView implements SurfaceHolder.Callback,
        Runnable {
    //用于控制SurfaceView
    private SurfaceHolder sfh;
    //声明一条线程
    private Thread th;
    //线程消亡的标识位
    private boolean flag;
    //声明屏幕宽高
    public static int screenW,screenH;
    //定义游戏状态常量
    public static final int GAMEING = 1;//游戏中
    public static final int GAME_WIN = 2;//游戏胜利
    public static final int GAME_LOST = 3;//游戏失败
    public static int gamestate;
    //声明一个Resources实例便于加载图片
    private Resources res = this.getResources();
    //声明游戏需要用到的图片资源（图片声明）
    private Bitmap bmpBackGround;//游戏背景
    private Bitmap bmpBoom;//爆炸效果
    private Bitmap bmpBossBoom;//Boss爆炸效果
    //怪物图片
    private Bitmap bmpEnemy1;
    private Bitmap bmpEnemy2;
    private Bitmap bmpEnemyBoss;
    //
    private Bitmap bmpGameWin;//游戏胜利背景
    private Bitmap bmpGameLost;//游戏失败背景
    private Bitmap bmpPlayer;//主角飞机
    private Bitmap bmpPlayerHp;//主角血量
    public static Bitmap bmpBullet;//子弹
    public static Bitmap bmpEnemyBullet;//敌机子弹
    public static Bitmap bmpBossBullet;//Boss子弹
    //声明背景
    private GameBackGround gameBackGround;
    //
//声明一个玩家战机
    private PlayerPlane playerPlane;
    //
//声明一个敌机容器
    private Vector<Enemy> vcEnemy;
    //每次生成敌机的时间（毫秒）
    private int createEnemyTime = 20;
    private int count;//记录下敌机数目
    //敌人数组，1和2表示敌机的种类，-1表示Boss
//二维数组的每一维都是一组怪物
    private int enemyArray[][] = {
            {1,2},{1,1},{1,3,1,2},{1,2},{2,3},{3,1,3},{2,2},{1,2},{2,2},
            {1,3,1,1},{2,1},{1,3},{2,1},{-1}
    };
    //当前取出一维数组的下标
    private int enemyArrayIndex;
    //是否出现Boss
    private boolean isBoss;
    //随机库，为创建的敌机赋予随机坐标
    private Random random;
    //敌机子弹容器
    private Vector<Bullet> vcBullet;
    //添加子弹的计数器
    private int countEnemyBullet;
    //主角子弹容器
    private Vector<Bullet> vcBulletPlayer;
    //添加主角子弹的计数器
    private int countPlayerBullet;
    //
//声明爆炸效果容器
    private Vector<Boom> vcBoom;
    //
//声明Boss
    private Boss boss;
    //声明Boss子弹容器
    public static Vector<Bullet> vcBulletBoss;
    // 声明画布
    private Canvas canvas;
    //声明画笔
    private Paint paint;
    //声明玩家得分
    private int PlayerScore;
    //声明最高分
    private int HighestScore;
    //声明数据库
    private MySQLiteOpenHelper dbHelper;
    public PlaneFightView(Context context) {
        super(context);
        sfh = this.getHolder();
        //为sfh添加监听
        sfh.addCallback((SurfaceHolder.Callback) this);
        //实例化画笔
        paint = new Paint();
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
    private void initGame(){
        //令游戏处于开始状态
        gamestate = GAMEING;
        Bitmap bmpBackGroundold = BitmapFactory.decodeResource(res, R.drawable.map);
        bmpBackGround = Bitmap.createScaledBitmap(bmpBackGroundold,screenW,screenH,true);
        bmpBoom = BitmapFactory.decodeResource(res,R.drawable.boom);
        bmpBossBoom = bmpBoom;
        bmpEnemy1 =  BitmapFactory.decodeResource(res,R.drawable.enemy1);
        bmpEnemy2 =  BitmapFactory.decodeResource(res,R.drawable.enemy2);
        bmpGameLost = BitmapFactory.decodeResource(res,R.drawable.gamelose);
        bmpGameWin = BitmapFactory.decodeResource(res,R.drawable.gamewin);
        bmpPlayer = BitmapFactory.decodeResource(res,R.drawable.player);
        bmpBullet = BitmapFactory.decodeResource(res,R.drawable.bullet);
        bmpBossBullet = BitmapFactory.decodeResource(res,R.drawable.bullet);
        bmpEnemyBullet = BitmapFactory.decodeResource(res,R.drawable.bullet);
        bmpPlayerHp = BitmapFactory.decodeResource(res,R.drawable.hp);
        bmpEnemyBoss = BitmapFactory.decodeResource(res,R.drawable.boss);
        //实例游戏背景
        gameBackGround = new GameBackGround(bmpBackGround);
        //实例主角
        playerPlane = new PlayerPlane(bmpPlayer,bmpPlayerHp);
        //实例敌机容器
        vcEnemy = new Vector<Enemy>();
        //实例随机库
        random = new Random();
        //实例敌机子弹容器
        vcBullet = new Vector<Bullet>();
        //实例玩家子弹容器
        vcBulletPlayer = new Vector<Bullet>();
        //实例爆炸容器
        vcBoom = new Vector<Boom>();
        //实例Boss对象
        boss = new Boss(bmpEnemyBoss);
        //实例Boss子弹容器
        vcBulletBoss = new Vector<Bullet>();
        //实例数据库
        dbHelper = new MySQLiteOpenHelper(this.getContext(),"highestgrade.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name","null");
        values.put("score",0);
        db.insert("highestscore","null",values);
        Cursor cursor = db.query("highestscore",null,null,null,null,null,null,null);
        cursor.moveToFirst();
        HighestScore = cursor.getInt(cursor.getColumnIndex("score"));
        PlayerScore = 0;
    }
    //自定义绘图函数
    public void myDraw(){
        try {
            canvas = sfh.lockCanvas();
            //根据游戏状态不同进行绘制
            switch (gamestate){

                case GAMEING:
                    //游戏背景
                    gameBackGround.draw(canvas,paint);
                    //主角绘图
                    playerPlane.draw(canvas,paint);
                    //绘制最高分与当前分数
                    String PS = String.valueOf(PlayerScore);
                    String HS = String.valueOf(HighestScore);
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(30);
                    canvas.drawText(PS,screenW / 2 - 100,50,paint);
                    canvas.drawText(HS,screenW - 100,50,paint);
                    //敌机绘制
                    for(int i = 0; i < vcEnemy.size();i++){
                        vcEnemy.elementAt(i).draw(canvas,paint);
                    }
                    //敌机子弹绘制
                    for(int i = 0; i < vcBullet.size(); i++){
                        vcBullet.elementAt(i).draw(canvas,paint);
                    }
                    if(isBoss == true){
                        //Boss的绘制
                        boss.draw(canvas,paint);
                        //Boss子弹绘制
                        for(int i = 0;i < vcBulletBoss.size(); i++){
                            vcBulletBoss.elementAt(i).draw(canvas,paint);
                        }
                    }
                    //处理主角子弹绘制
                    for(int i = 0; i < vcBulletPlayer.size(); i++) {
                        vcBulletPlayer.elementAt(i).draw(canvas,paint);
                    }
                    //爆炸效果绘制
                    for(int i = 0; i < vcBoom.size(); i++ ){
                        vcBoom.elementAt(i).draw(canvas,paint);
                    }
                    break;
                case GAME_WIN:
                    String PS2 = String.valueOf(PlayerScore);
                    String HS2 = String.valueOf(HighestScore);
                    canvas.drawText(PS2,screenW / 2 - 100,50,paint);
                    canvas.drawText(HS2,screenW - 100,50,paint);
                    canvas.drawBitmap(bmpGameWin,0,0,paint);
                    break;
                case GAME_LOST:
                    String PS3 = String.valueOf(PlayerScore);
                    String HS3 = String.valueOf(HighestScore);
                    canvas.drawText(PS3,screenW / 2 - 100,50,paint);
                    canvas.drawText(HS3,screenW - 100,50,paint);
                    canvas.drawBitmap(bmpGameLost,0,0,paint);
                    break;
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
        switch (gamestate){
            case GAMEING:
                playerPlane.onTouchEvent(event);
                break;
            case GAME_WIN:
                break;
            case GAME_LOST:
                break;
        }
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (gamestate){
            case GAMEING:
                //主角的按下事件
                playerPlane.onKeyDown(keyCode,event);
                break;
            case GAME_WIN:
                break;
            case GAME_LOST:
                break;
        }
        return super.onKeyDown(keyCode,event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        switch (gamestate){
            case GAMEING:
                //主角的按下事件
                playerPlane.onKeyUp(keyCode,event);
                break;
            case GAME_WIN:
                break;
            case GAME_LOST:
                break;
        }
        return super.onKeyUp(keyCode,event);
    }
    private void logic(){
        switch (gamestate){
            case GAMEING:
                //背景逻辑
                gameBackGround.logic();
                //主角逻辑
                playerPlane.logic();
                //敌机逻辑
                for (int i = 0; i < vcEnemy.size(); i++) {
                    Enemy en = vcEnemy.elementAt(i);
                    //因为容器不断添加敌机，所以要对敌机作isDead判定
                    //如果已经死亡就从容器中移出，优化容器
                    if (en.isOut) {
                        vcEnemy.removeElementAt(i);
                    } else {
                        en.logic();
                    }
                }
                //生成敌机
                count++;
                if (count % createEnemyTime == 0) {
                    for (int i = 0; i < enemyArray[enemyArrayIndex].length; i++) {
                        //战机1
                        if (enemyArray[enemyArrayIndex][i] == 1) {
                            int x = random.nextInt(screenW - 100) + 50;
                            vcEnemy.addElement(new Enemy(bmpEnemy1, 1, x, -50));
                            //战机2左
                        } else if (enemyArray[enemyArrayIndex][i] == 2) {
                            int y = random.nextInt(20);
                            vcEnemy.addElement(new Enemy(bmpEnemy2, 2, -50, y));
                            //战机2右
                        } else if (enemyArray[enemyArrayIndex][i] == 3) {
                            int y = random.nextInt(20);
                            vcEnemy.addElement(new Enemy(bmpEnemy2, 3, screenW + 50, y));
                        }
                    }
                    //判断是否为Boss
                    if (enemyArrayIndex == enemyArray.length - 1) {
                        isBoss = true;
                    } else {
                        enemyArrayIndex++;
                    }
                }
                //子弹部分
                //每两秒添加一个敌机子弹
                countEnemyBullet++;
                if (countEnemyBullet % 80 == 0) {
                    for (int i = 0; i < vcEnemy.size(); i++) {
                        Enemy enemy = vcEnemy.elementAt(i);
                        //不同的类型敌机不同的子弹运行轨迹
                        int bulletType = 0;
                        switch (enemy.type) {
                            //敌机1
                            case Enemy.TYPE_ENEMY1:
                                bulletType = Bullet.BULLET_ENEMY1;
                                break;
                            //敌机2
                            case Enemy.TYPE_ENEMY2:
                            case Enemy.TYPE_ENEMY3:
                                bulletType = Bullet.BULLET_ENEMY2;
                                break;
                        }
                        vcBullet.add(new Bullet(bmpBullet, enemy.x + 10, enemy.y + 20, bulletType));
                    }
                }
                //处理敌机子弹逻辑
                for (int i = 0; i < vcBullet.size(); i++) {
                    Bullet bullet = vcBullet.elementAt(i);
                    if (bullet.isDead) {
                        vcBullet.removeElement(bullet);
                    } else {
                        bullet.logic();
                    }
                }
                //每一秒添加一个主角子弹
                countPlayerBullet++;
                if(countPlayerBullet % 10 == 0) {
                    vcBulletPlayer.add(new Bullet(bmpBullet, playerPlane.x + 15, playerPlane.y - 20, Bullet.BULLET_PLAYER));
                }
                //处理主角子弹逻辑
                for(int i = 0; i < vcBulletPlayer.size();i++) {
                    Bullet bullet = vcBulletPlayer.elementAt(i);
                    if (bullet.isDead) {
                        vcBulletPlayer.removeElement(bullet);
                    } else {
                        bullet.logic();
                    }
                }
                //处理敌机与主角的碰撞
                for(int i = 0; i < vcEnemy.size();i++ ){
                    if(playerPlane.isCollsionWith(vcEnemy.elementAt(i))){
                        //发生碰撞，血量减一
                        playerPlane.setPlayerHp(playerPlane.getPlayerHp() - 1);
                        //当主角血量小于0，判定游戏失败
                        if(playerPlane.getPlayerHp() <= -1){
                            gamestate = GAME_LOST;
                        }
                    }
                }
                //处理敌机子弹与主角碰撞
                for(int i = 0; i < vcBullet.size(); i++){
                    if(playerPlane.isCollisionWith(vcBullet.elementAt(i))){
                        //发生碰撞，主角血量-1
                        playerPlane.setPlayerHp(playerPlane.getPlayerHp() - 1);
                        //当主角血量小于0，判定游戏失败
                        if(playerPlane.getPlayerHp() <= -1){
                            gamestate = GAME_LOST;
                        }
                    }
                }
                //处理主角子弹与敌机碰撞
                for(int i = 0; i < vcBulletPlayer.size(); i++){
                    //取出主角子弹容器的每个元素
                    Bullet blPlayer = vcBulletPlayer.elementAt(i);
                    for(int j = 0; j < vcEnemy.size(); j++){
                        Enemy enemy = vcEnemy.elementAt(j);
                        //爆炸效果
                        //加分
                        //取出敌机容器的每个敌机与主角子弹遍历判断
                        if(enemy.isCollsionWith(blPlayer)){
                            //不同的敌机被击坠时得到的分数不同
                            switch (enemy.type){
                                case 1:
                                    PlayerScore += 100;
                                    break;
                                case 2:
                                    PlayerScore += 200;
                                    break;
                                case 3:
                                    PlayerScore += 200;

                            }
                            if(PlayerScore >= HighestScore){
                                HighestScore = PlayerScore;
                            }
                            vcBoom.add(new Boom(bmpBoom,vcEnemy.elementAt(j).x,vcEnemy.elementAt(j).y,2));
                        }
                    }
                }
                if( isBoss == true){
                    //Boss逻辑
                    boss.logic();
                    if(countPlayerBullet %30 == 0){
                        //Boss没发疯前的普通子弹
                        vcBulletBoss.add(new Bullet(bmpBossBullet,boss.x + 35,boss.y + 40,Bullet.BULLET_ENEMY1));
                    }
                    //Boss子弹逻辑
                    for(int i = 0; i < vcBulletBoss.size(); i++){
                        Bullet bullet = vcBulletBoss.elementAt(i);
                        if(bullet.isDead){
                            vcBulletBoss.removeElement(bullet);
                        }else{
                            bullet.logic();
                        }
                    }
                    //Boss子弹与主角的碰撞
                    for(int i = 0; i < vcBulletBoss.size(); i++){
                        if (playerPlane.isCollisionWith(vcBulletBoss.elementAt(i))){
                            //发生碰撞，主角血量 - 1
                            playerPlane.setPlayerHp(playerPlane.getPlayerHp() - 1);
                            //当主角血量小于0，判定游戏失败
                            if(playerPlane.getPlayerHp() <= -1){
                                gamestate = GAME_LOST;
                            }
                        }
                    }
                    //Boss被主角子弹击中,产生爆炸效果
                    for(int i = 0; i < vcBulletPlayer.size(); i++){
                        Bullet bullet = vcBulletPlayer.elementAt(i);
                        if (boss.isCollsionWith(bullet)){
                            if (boss.hp <= 0){
                                //游戏胜利
                                //加5000分
                                PlayerScore += 5000;
                                gamestate = GAME_WIN;
                            }else {
                                //及时删除本次碰撞的子弹，防止重复判定此子弹与Boss碰撞
                                bullet.isDead = true;
                                //Boss血量减一
                                boss.setHp(boss.hp - 1);
                                //在Boss上添加三个Boss爆炸效果
                                vcBoom.add(new Boom(bmpBossBoom,boss.x + 25,boss.y + 30,2));
                                vcBoom.add(new Boom(bmpBossBoom,boss.x + 25,boss.y + 40,2));
                                vcBoom.add(new Boom(bmpBossBoom,boss.x + 25,boss.y + 50,2));
                            }
                        }
                    }
                }
                //爆炸效果逻辑
                for(int i = 0; i < vcBoom.size(); i++){
                    Boom boom = vcBoom.elementAt(i);
                    if(boom.playEnd){
                        //播放完毕，从容器中删除
                        vcBoom.removeElement(boom);
                    }else {
                        boom.logic();
                    }
                }
                break;
            case GAME_WIN:
                if(PlayerScore == HighestScore){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("score",HighestScore);
                    db.update("highestscore",values,"name = ?",new String[]{
                            "null"
                    });
                    db.close();
                }
                break;
            case GAME_LOST:
                if(PlayerScore == HighestScore){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("score",HighestScore);
                    db.update("highestscore",values,"name = ?",new String[]{
                            "null"
                    });
                    db.close();
                }
                break;
        }
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
class GameBackGround{
    //游戏背景的图片资源
    //用两个相同图像以循环播放
    private Bitmap bmpBackGround1;
    private Bitmap bmpBackGround2;
    //游戏背景坐标
    private int bg1x,bg1y,bg2x,bg2y;
    //背景滚动速度
    private int speed = 3;

    //游戏背景构造函数
    public GameBackGround(Bitmap bmpBackGround){
        this.bmpBackGround1 = bmpBackGround;
        this.bmpBackGround2 = bmpBackGround;
        //让第一张背景底部正好填满整个屏幕
        bg1y = -Math.abs(bmpBackGround1.getHeight() - PlaneFightView.screenH);
        //第二张背景图接在第一张背景的上方
        bg2y = bg1y - bmpBackGround1.getHeight();
    }
    //游戏背景的绘图函数
    public void draw(Canvas canvas,Paint paint){
        //绘制两张背景
        canvas.drawBitmap(bmpBackGround1,bg1x,bg1y,paint);
        canvas.drawBitmap(bmpBackGround2,bg2x,bg2y,paint);
    }
    //游戏背景的逻辑函数
    public void logic(){
        bg1y += speed;
        bg2y += speed;
        //当第一张图片的Y坐标超过屏幕
        //立即将其坐标设置在第二张图的上方
        if(bg1y > PlaneFightView.screenH){
            bg1y = bg2y - bmpBackGround1.getHeight() + 111;
        }
        //当第二张图片的Y坐标超过屏幕
        //立即将其坐标设置在第一张图的上方
        if(bg2y > PlaneFightView.screenH){
            bg2y = bg1y - bmpBackGround1.getHeight() + 111;
        }
    }
}

class PlayerPlane{
    //主角的血量与血量位图
    //默认3血
    private int PlayerHp = 3;
    private Bitmap bmpPlayerHp;
    //主角的坐标以及位图
    public int x,y;
    private Bitmap bmpPlayer;
    //主角移动速度
    private int speed = 25;
    //主角移动标识
    private boolean isUp,isDown,isLeft,isRight;
    //碰撞后处于无敌时间
    //计时器
    private int noCollisionCount = 0;
    //无敌时间
    private int noCollisionTime = 60;
    //是否碰撞的标识位
    private boolean isCollision;
    //主角的构造函数
    public PlayerPlane(Bitmap bmpPlayer,Bitmap bmpPlayerHp){
        this.bmpPlayer = bmpPlayer;
        this.bmpPlayerHp = bmpPlayerHp;
        x = PlaneFightView.screenW / 2 - bmpPlayer.getWidth() / 2 + 200;
        y = PlaneFightView.screenH - bmpPlayer.getHeight() - 500;
    }
    //主角的绘图函数
    public void draw(Canvas canvas,Paint paint){
        //绘制主角
        //处于无敌状态时让主角闪烁
        if(isCollision){
            //每两次循环绘制一次主角
            if(noCollisionCount % 2 == 0){
                canvas.drawBitmap(bmpPlayer,x,y,paint);
            }
        }else {
            canvas.drawBitmap(bmpPlayer,x,y,paint);
        }
        //绘制主角血量
        for (int i = 0;i < PlayerHp; i++){
            canvas.drawBitmap(bmpPlayerHp,i*bmpPlayerHp.getWidth(),PlaneFightView.screenH - bmpPlayerHp.getHeight(),paint);
        }
    }
    //实体按键
    public void onKeyDown(int keyCode,KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
            isUp = true;
        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
            isDown = true;
        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN_LEFT){
            isLeft = true;
        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
            isRight = true;
        }
    }
    //实体按键抬起
    public void onKeyUp(int keyCode,KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
            isUp = false;
        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
            isDown = false;
        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN_LEFT){
            isLeft = false;
        }
        if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
            isRight = false;
        }
    }
    //主角触屏移动函数
    public void onTouchEvent(MotionEvent event){
        int getx = (int)event.getX(),gety = (int)event.getY();
        int x = getx - this.x;
        int y = gety - this.y;
        if(x - y > 0 && x + y > 0){
            this.x += speed;
        }
        else if(x - y > 0 && y + x < 0){
            this.y -= speed;
        }
        else if(x - y < 0 && y + x < 0 ){
            this.x -= speed;
        }
        else if(x - y < 0 && y + x > 0){
            this.y += speed;
        }
    }
    //主角逻辑
    public void logic(){
        //处理主角移动
        if(isLeft){
            x -= speed;
        }
        if(isRight){
            x += speed;
        }
        if(isUp){
            y -= speed;
        }
        if(isDown){
            y += speed;
        }
        //判断屏幕X边界
        if(x + bmpPlayer.getWidth() >= PlaneFightView.screenW){
            x = PlaneFightView.screenW - bmpPlayer.getWidth();
        }else if(x <= 0){
            x = 0;
        }
        //判断屏幕Y边界
        if(y + bmpPlayer.getHeight() >= PlaneFightView.screenH){
            y = PlaneFightView.screenH - bmpPlayer.getHeight();
        }else if(y <= 0){
            y = 0;
        }
        //处于无敌状态
        if(isCollision){
            //计时器倒计时
            noCollisionCount++;
            if(noCollisionCount >= noCollisionTime){
                //无敌时间过后，接触无敌状态及初始化计数器
                isCollision = false;
                noCollisionCount = 0;
            }
        }

    }
    //设置主角血量
    public void setPlayerHp(int hp){
        this.PlayerHp = hp;
    }
    public int getPlayerHp(){
        return PlayerHp;
    }
    public boolean isCollsionWith(Enemy enemy){
        if(isCollision == false) {
            int x2 = enemy.x;
            int y2 = enemy.y;
            int width = enemy.frameW;
            int height = enemy.frameH;
            if (x <= x2 && x >= x2 + width) {
                return false;
            } else if (x <= x2 && x + bmpPlayer.getWidth() <= x2) {
                return false;
            } else if (y >= y2 && y >= y2 + height) {
                return false;
            } else if (y <= y2 && y + bmpPlayer.getHeight() <= y2) {
                return false;
            }
            isCollision = true;
            return true;
        }else {
            return false;
        }
    }
    public boolean isCollisionWith(Bullet bullet){
        //是否处于无敌时间
        if(isCollision == false) {
            int x2 = bullet.bulletX;
            int y2 = bullet.bulletY;
            int w2 = bullet.bmpBullet.getWidth();
            int h2 = bullet.bmpBullet.getHeight();
            if (x >= x2 && x >= x2 + w2) {
                return false;
            } else if (x <= x2 && x + bmpPlayer.getWidth() <= x2) {
                return false;
            } else if (y >= y2 && y >= y2 + h2) {
                return false;
            } else if (y <= y2 && y + bmpPlayer.getHeight() <= y2) {
                return false;
            }
            isCollision = true;
            return true;
            //处于无敌状态，无视碰撞
        }else {
            return false;
        }
    }
}

class Enemy{
    //敌机的种类标识
    public int type;
    //敌机种类
    //敌机1
    public static final int TYPE_ENEMY1 = 1;
    //敌机2左往右
    public static final int TYPE_ENEMY2 = 2;
    //敌机2右往左
    public static final int TYPE_ENEMY3 = 3;
    //
    //


    //敌机图片资源
    public Bitmap bmpEnemy;
    //敌机坐标
    public int x,y;
    //敌机每帧的宽高
    public int frameW,frameH;
    //敌机当前帧下标
    private int frameIndex;
    //敌机的移动速度
    private int speed;
    //判断敌机是否已经出屏
    public boolean isOut;
    //敌机的构造函数
    public Enemy(Bitmap bmpEnemy,int enemyType,int x,int y){
        this.bmpEnemy = bmpEnemy;
        frameW = bmpEnemy.getWidth();
        frameH = bmpEnemy.getHeight();
        this.type = enemyType;
        this.x = x;
        this.y = y;
        //不同敌机的速度不一样
        switch (type){
            //敌机1
            case TYPE_ENEMY1:
                speed = 25;
                break;
            case TYPE_ENEMY2:
                speed = 3;
                break;
            case  TYPE_ENEMY3:
                speed = 3;
                break;
        }
    }
    //敌机绘图函数
    public void draw(Canvas canvas,Paint paint){
        canvas.save();
        canvas.drawBitmap(bmpEnemy,x,y,paint);
        canvas.restore();
    }
    //敌机逻辑AI
    public void logic(){
        //不断循环播放帧形成动画
        frameIndex++;
        if(frameIndex >= 10){
            frameIndex = 0;
        }
        //不同敌机逻辑
        switch (type){
            case TYPE_ENEMY1:
                if(isOut == false){
                    //减速出现加速返回
                    speed -= 1;
                    y += speed;
                }
                if(y <= -200){
                    isOut = true;
                }
                break;
            case TYPE_ENEMY2:
                if(isOut == false){
                    //斜右下角移动
                    x += speed / 2;
                    y += speed;
                    if(x > PlaneFightView.screenW){
                        isOut = true;
                    }
                }
                break;
            case TYPE_ENEMY3:
                if (isOut == false) {
                    //斜左下角移动
                    x -= speed / 2;
                    y += speed;
                    if(x < -50){
                        isOut = true;
                    }
                }
                break;
        }
    }
    //判断碰撞（敌机与主角子弹碰撞）
    public boolean isCollsionWith(Bullet bullet){
        int x2 = bullet.bulletX;
        int y2 = bullet.bulletY;
        int w2 = bullet.bmpBullet.getWidth();
        int h2 = bullet.bmpBullet.getHeight();
        if(x >= x2 && x >= x2 + w2){
            return false;
        }else if (x <= x2 && x + frameW <= x2){
            return false;
        }else if (y >= y2 && y >= y2 + h2){
            return false;
        }else if(y <= y2 && y + frameH <= y2){
            return false;
        }
        isOut = true;
        return true;
    }

}

class Bullet{
    //子弹图片资源
    public Bitmap bmpBullet;
    //子弹的坐标
    public int bulletX,bulletY;
    //子弹的速度
    public int speed;
    //子弹的种类以及常量
    public int bulletType;
    //主角的
    public static final int BULLET_PLAYER = -1;
    //敌机1的
    public static final int BULLET_ENEMY1 = 1;
    //敌机2的
    public static final int BULLET_ENEMY2 = 2;
    //BOSS的
    public static final int BULLET_BOSS = 3;
    //子弹是否超屏，优化处理
    public boolean isDead;

    //子弹构造函数
    public Bullet(Bitmap bmpBullet,int bulletX,int bulletY,int bulletType){
        this.bmpBullet = bmpBullet;
        this.bulletX = bulletX;
        this.bulletY = bulletY;
        this.bulletType = bulletType;
        //不同的子弹类型速度不一
        switch (bulletType){
            case BULLET_PLAYER:
                speed = 4;
                break;
            case BULLET_ENEMY1:
                speed = 4;
                break;
            case BULLET_ENEMY2:
                speed = 3;
                break;
            case BULLET_BOSS:
                speed = 5;
                break;
        }
    }
    //Boss状态下的子弹
    private int dir;//当前Boss子弹方向
    //8方向常量
    public static final int DIR_UP = -1;
    public static final int DIR_DOWN = 2;
    public static final int DIR_LEFT = 3;
    public static final int DIR_RIGHT = 4;
    public static final int DIR_UP_LEFT = 5;
    public static final int DIR_UP_RIGHT = 6;
    public static final int DIR_DOWN_LEFT = 7;
    public static final int DIR_DOWN_RIGHT = 8;
    //专用于处理Boss状态下的子弹
    public Bullet(Bitmap bmpBullet,int bulletX,int bulletY,int bulletType,int dir){
        this.bmpBullet = bmpBullet;
        this.bulletX = bulletX;
        this.bulletY = bulletY;
        this.bulletType = bulletType;
        speed = 40;
        this.dir = dir;
    }
    //子弹的绘制
    public void draw(Canvas canvas,Paint paint){
        canvas.drawBitmap(bmpBullet,bulletX,bulletY,paint);
    }
    //子弹的逻辑
    public void logic(){
        //不同的子弹类型逻辑不一
        //主角的子弹垂直向上运动
        switch (bulletType) {
            case BULLET_PLAYER:
                bulletY -= speed * 8;
                if (bulletY < 0) {
                    isDead = true;
                }
                break;
            //敌机1和敌机2的子弹都是垂直向下运动
            case BULLET_ENEMY1:
            case BULLET_ENEMY2:
                bulletY += speed;
                if (bulletY > PlaneFightView.screenH) {
                    isDead = true;
                }
                break;
            case BULLET_BOSS:
                //Boss状态下的子弹
                switch (dir){
                    case DIR_UP:
                        bulletY -= speed;
                        break;
                    case DIR_DOWN:
                        bulletY += speed;
                        break;
                    case DIR_LEFT:
                        bulletX -= speed;
                        break;
                    case DIR_RIGHT:
                        bulletX += speed;
                        break;
                    case DIR_UP_LEFT:
                        bulletY -= speed;
                        bulletX -= speed;
                        break;
                    case DIR_UP_RIGHT:
                        bulletY -= speed;
                        bulletX += speed;
                        break;
                    case DIR_DOWN_LEFT:
                        bulletY += speed;
                        bulletX -= speed;
                        break;
                    case DIR_DOWN_RIGHT:
                        bulletY += speed;
                        bulletX += speed;
                        break;
                }
                //同样的边界处理
                if(bulletY > PlaneFightView.screenH || bulletY <= 0 || bulletX > PlaneFightView.screenW || bulletX <= 0){
                    isDead = true;
                }
                break;
        }

    }
}

class Boom{
    //爆炸效果资源图
    private Bitmap bmpBoom;
    //爆炸效果的位置坐标
    private int boomX,boomY;
    //爆炸动画播放当前的帧下标
    private int currentFrameIndex = 0;
    //爆炸效果的总帧数
    private int totleFrame;
    //是否播放完毕，优化处理
    public boolean playEnd = false;
    //爆炸效果的构造函数
    public Boom(Bitmap bmpBoom,int x,int y,int totleFrame){
        this.bmpBoom = bmpBoom;
        this.boomX = x;
        this.boomY = y;
        this.totleFrame = totleFrame;
    }
    //爆炸效果绘制
    public void draw(Canvas canvas,Paint paint){
        canvas.save();
        canvas.drawBitmap(bmpBoom,boomX,boomY,paint);
        canvas.restore();
    }
    //爆炸效果的逻辑
    public void logic(){
        if(currentFrameIndex < totleFrame){
            currentFrameIndex++;
        }else {
            playEnd = true;
        }
    }

}

class Boss{
    //Boss的血量
    public int hp = 50;
    //Boss的图片资源
    private Bitmap bmpBoss;
    //Boss坐标
    public int x,y;
    //Boss每帧的宽高
    public int frameW,frameH;
    //Boss当前帧下标
    private int frameIndex;
    //Boss运动的速度
    private int speed = 5;
    //Boss的运动轨迹
    //一定时间会向着屏幕下方运动，并且发射大范围子弹
    //正常状态下，子弹垂直向下移动
    private boolean isCreazy;
    //进入疯狂状态下的时间间隔
    private int creazyTime = 200;
    //计数器
    private int count;

    //Boss的构造函数
    public Boss(Bitmap bmpBoss){
        this.bmpBoss = bmpBoss;
        frameW = bmpBoss.getWidth();
        frameH = bmpBoss.getHeight();
        //Boss的X坐标居中
        x = PlaneFightView.screenW / 2 - frameW / 2;
        y = 0;
    }
    //Boss的绘制
    public void draw(Canvas canvas,Paint paint){
        canvas.save();
        //canvas.clipRect(x,y,x + frameW,y + frameH);
        canvas.drawBitmap(bmpBoss,x ,y,paint);
        canvas.restore();
    }
    //Boss的逻辑
    public void logic(){
        //不断播放帧形成动画
        frameIndex++;
        if(frameIndex >= 10 ){
            frameIndex = 0;
        }
        //没有疯狂的状态
        if(isCreazy == false) {
            x += speed;
            if (x + frameW >= PlaneFightView.screenW) {
                speed = -speed;
            } else if (x <= 0) {
                speed = -speed;
            }
            count++;
            if (count % creazyTime == 0) {
                isCreazy = true;
                speed = 24;
            }
        }
        else {
            //疯狂的状态
            speed -= 1;
            //当Boss返回时创建大量子弹
            if(speed == 0){
                //添加八个方向的子弹
                PlaneFightView.vcBulletBoss.add(new Bullet(PlaneFightView.bmpBullet,x + 30,y,Bullet.BULLET_BOSS,Bullet.DIR_UP));
                PlaneFightView.vcBulletBoss.add(new Bullet(PlaneFightView.bmpBullet,x + 30,y,Bullet.BULLET_BOSS,Bullet.DIR_DOWN));
                PlaneFightView.vcBulletBoss.add(new Bullet(PlaneFightView.bmpBullet,x + 30,y,Bullet.BULLET_BOSS,Bullet.DIR_LEFT));
                PlaneFightView.vcBulletBoss.add(new Bullet(PlaneFightView.bmpBullet,x + 30,y,Bullet.BULLET_BOSS,Bullet.DIR_RIGHT));
                PlaneFightView.vcBulletBoss.add(new Bullet(PlaneFightView.bmpBullet,x + 30,y,Bullet.BULLET_BOSS,Bullet.DIR_UP_LEFT));
                PlaneFightView.vcBulletBoss.add(new Bullet(PlaneFightView.bmpBullet,x + 30,y,Bullet.BULLET_BOSS,Bullet.DIR_UP_RIGHT));
                PlaneFightView.vcBulletBoss.add(new Bullet(PlaneFightView.bmpBullet,x + 30,y,Bullet.BULLET_BOSS,Bullet.DIR_DOWN_LEFT));
                PlaneFightView.vcBulletBoss.add(new Bullet(PlaneFightView.bmpBullet,x + 30,y,Bullet.BULLET_BOSS,Bullet.DIR_DOWN_RIGHT));
            }
            y += speed;
            if(y <= 0){
                //恢复正常状态
                isCreazy = false;
                speed = 5;
            }
        }
    }
    //判断碰撞（被主角子弹击中）
    public boolean isCollsionWith(Bullet bullet){
        int x2 = bullet.bulletX;
        int y2 = bullet.bulletY;
        int w2 = bullet.bmpBullet.getWidth();
        int h2 = bullet.bmpBullet.getWidth();
        if(x >= x2 && x >= x2 + w2){
            return false;
        }else if (x <= x2 && x + frameW <= x2){
            return false;
        }else if (y >= y2 && y >= y2 + h2){
            return false;
        }else if (y <= y2 && y + frameH <= y2 ){
            return false;
        }
        return true;

    }
    //设置Boss血量
    public void setHp(int hp){
        this.hp = hp;
    }
}
