package com.example.admin.barcodescanneractivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
   public DatabaseHelper(Context context)
    {
        super(context,"test.sqlite",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
          String sql="create table contact(id integer primary key AUTOINCREMENT,name varchar(100),email varchar(100),mobile varchar(100),title varchar(100),address varchar(200),sync_status int)";
          sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
