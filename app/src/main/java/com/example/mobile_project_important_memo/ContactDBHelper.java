package com.example.mobile_project_important_memo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*디비 헬퍼 클래스*/
public class ContactDBHelper extends SQLiteOpenHelper {

    public static  final int DB_VERSION = 1 ;
    public static  final String DB_NAME = "memo_data.db";

    /*생성자*/
    public ContactDBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ContractDBCtrct.SQL_CREATE_TBL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
