package com.shm1193.dron_for_git;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteDB extends SQLiteOpenHelper {

    public MySQLiteDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         *  DB 파일 생성 경로 -> data/data/(패키지명)/database
         *  경로는 자동생성 후 그 안에 .db 가 만들어진다.
         */
        String createQuery = "CREATE TABLE CUSTOMER (name TEXT, hp INTEGER, materialInfo TEXT, materialWgt TEXT);";
        db.execSQL(createQuery);
        Log.v("[Status]: ", "Creating DB is completed");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // DB 안에 입력한 정보를 채워넣는다
    public void insert(String name, String hp, String mtInfo, String mtWgt) {
        // 읽고 쓰기가 가능하게 DB를 연다
        SQLiteDatabase db = getWritableDatabase();

        // DB에 입력한 값으로 행 추가
        String insertQuery = "INSERT OR REPLACE INTO CUSTOMER VALUES('" + name + "', " + hp + ", '" + mtInfo + "', '" + mtWgt + "');";
        db.execSQL(insertQuery);
        db.close();

        Log.v("[Status]: ", "Inserting Data is completed");
    }

    // 안에 저장된 값을 초기화 시킴
    public void delete() {
        // 읽고 쓰기가 가능하게 DB를 연다
        SQLiteDatabase db = getWritableDatabase();
        String deleteQuery = "DELETE FROM CUSTOMER";

        db.execSQL(deleteQuery);
        db.close();
    }

    // 미리 데이터가 지정되어 있을 경우에 대비해서 결과를 한번에 읽어서 보낼 때에는 이 함수 이용하기!!!
    public String getResult() {
        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        String result = "";
        Cursor cursor = db.rawQuery("SELECT * FROM CUSTOMER", null);


        /** 여기가 반복됨;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; */
        while (cursor.moveToNext()) {
            result += "이름: " + cursor.getString(0) + "\n"
                    + "HP: " + cursor.getString(1) + "\n"
                    + "물건 정보: " + cursor.getString(2) + "\n"
                    + "물건 무게: " + cursor.getString(3) + "\n";
        }

        return result;
    }

    // 단순히 나중에 Query를 붙일 때 필요한 단일 값들을 리턴하기 위해 필요한 함수!!
    public String getString(int type_from_0to3) {
        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        String result = "";
        Cursor cursor = db.rawQuery("SELECT * FROM CUSTOMER", null);

        return cursor.getString(type_from_0to3);
    }

}