package com.example.administrator.sheepgamebox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static Integer Version = 1;
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version) {
        super(context, name, factory, version);
    }
    public MySQLiteOpenHelper(Context context,String name,int version)
    {
        this(context,name,null,version);
    }
    public MySQLiteOpenHelper(Context context,String name)
    {
        this(context, name, Version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table highestscore(id integer primary key autoincrement,name varchar(200),score integer)";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

