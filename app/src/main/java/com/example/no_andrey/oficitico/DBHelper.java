package com.example.no_andrey.oficitico;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by No-Andrey on 22/9/2016.
 */
public class DBHelper extends SQLiteOpenHelper
{

    public static final String DATABASE_NAME = "oficitico.db";
    public static final String OFICITICO_TABLE_NAME = "information";
    public static final String OFICITICO_COLUMN_ID = "id";
    public static final String OFICITICO_COLUMN_NAME = "name";
    public static final String OFICITICO_COLUMN_LASTNAME = "last_name";
    public static final String OFICITICO_COLUMN_PHONE = "phone";
    public static final String OFICITICO_COLUMN_CELLPHONE = "cellphone";
    public static final String OFICITICO_COLUMN_ADDRESS = "address";
    public static final String OFICITICO_COLUMN_WORK_ACTIVITIES = "services";
    public static final String OFICITICO_COLUMN_IMAGE_PATH = "image_path";


    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(
                "create table information " +
                        "(id integer primary key ,name text, last_name text,phone text, cellphone text, address text, services text, image_path text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS information");
        onCreate(db);
    }

    public long insertPersonalInformation (String name, String last_name, String phone, String cellphone, String address, String image_path)
    {
        long row = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("last_name", last_name);
        contentValues.put("phone", phone);
        contentValues.put("cellphone", cellphone);
        contentValues.put("address", address);
        contentValues.put("image_path", image_path);
        contentValues.put("services", "");


        row = db.insert("information", null,contentValues);
        db.close();

        return row;
    }

    public Cursor getPersonData(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from information where id = "+id+"", null);

        return res;
    }

    public int updatePersonData(Long id, Map<String, String> values)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int rows;

        for (Map.Entry<String, String> entry: values.entrySet()) {
            contentValues.put(entry.getKey(), entry.getValue());
        }

/**
        contentValues.put("services", "hola");
        //contentValues.put("last_name", last_name);
        //contentValues.put("phone", phone);
**/
        rows = db.update("information", contentValues, "id = ?", new String[]{Long.toString(id)});

        return rows;
    }

    public Integer deletePerson(Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete("information", "id = ?", new String[]{Integer.toString(id)});
    }

    public ArrayList<String> getAllPeople()
    {
        ArrayList<String> arrayList = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from information", null);

        res.moveToFirst();

        while (res.isAfterLast() == false)
        {
            arrayList.add(res.getString(res.getColumnIndex(OFICITICO_COLUMN_NAME)));
            res.moveToNext();
        }

        return arrayList;
    }

}
