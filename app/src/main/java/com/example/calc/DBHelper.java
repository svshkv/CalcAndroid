package com.example.calc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "convertsdb";
    public static String TABLE_CONVERTS = "converts";

    public static String KEY_ID = "_id";
    public static String KEY_COURSEFROM = "courseFrom";
    public static String KEY_COURSETO = "courseTo";
    public static String KEY_VALUE = "value";
    public static String KEY_RESULT = "result";
    public static String KEY_DATE = "date";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_CONVERTS + "( " + KEY_ID
                + " integer primary key, " + KEY_COURSEFROM + " text, " + KEY_COURSETO
                + " text, " + KEY_VALUE + " real, " + KEY_RESULT + " real, " + KEY_DATE + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_CONVERTS);
        onCreate(sqLiteDatabase);
    }

    public void putInDB(SQLiteDatabase database, double value, double result, String courseFrom, String courseTo, String date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_VALUE, value);
        contentValues.put(DBHelper.KEY_RESULT, result);
        contentValues.put(DBHelper.KEY_DATE, date);
        contentValues.put(DBHelper.KEY_COURSEFROM, courseFrom);
        contentValues.put(DBHelper.KEY_COURSETO, courseTo);
        database.insert(DBHelper.TABLE_CONVERTS, null, contentValues);
    }

    public ArrayList getFromDB(SQLiteDatabase database) {
        String result = new String();
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = database.query(DBHelper.TABLE_CONVERTS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int courseFromIndex = cursor.getColumnIndex(DBHelper.KEY_COURSEFROM);
            int courseToIndex = cursor.getColumnIndex(DBHelper.KEY_COURSETO);
            int valueIndex = cursor.getColumnIndex(DBHelper.KEY_VALUE);
            int resultIndex = cursor.getColumnIndex(DBHelper.KEY_RESULT);
            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
            do {
                Log.d("mLog", "ID = " + cursor.getInt(idIndex) +
                        " courseFrom = " + cursor.getString(courseFromIndex) +
                        " courseTo = " + cursor.getString(courseToIndex) +
                        " date = " + cursor.getString(dateIndex) +
                        " value = " + cursor.getDouble(valueIndex) +
                        " result = " + cursor.getDouble(resultIndex));
                result = "(" + cursor.getString(dateIndex) + ") " + cursor.getDouble(valueIndex) + " " +
                        cursor.getString(courseFromIndex) + " => " + cursor.getDouble(resultIndex) + " " +
                        cursor.getString(courseToIndex) + ";";
                list.add(result);

            } while (cursor.moveToNext());

        } else
            Log.d("error", " 0 rows");


        Log.d("result", result);
        if (cursor.moveToFirst()) {
            if (list.size() > 10) {
                database.delete(TABLE_CONVERTS, KEY_ID + " = " + Integer.toString(cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ID))), null);
            }
        }
        cursor.close();
        return list;
    }


}
