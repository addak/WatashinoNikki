package com.bignerdranch.watashinonikki.Database.DiaryDbSchema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.CONTENT;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.DATE;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.FILEPATH;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.TITLE;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.UUId;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.WEATHER;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.NAME;

public class DiaryBaseHelper extends SQLiteOpenHelper {

    private static int VERSION = 1;
    private static String DATABASE_NAME = "diary.db";

    public DiaryBaseHelper(Context context) { super(context, DATABASE_NAME, null, VERSION); }

    @Override
    public void onCreate(SQLiteDatabase Db) {
        Db.execSQL("create table " + NAME + "(" + "_id integer primary key autoincrement,"  +
                UUId + "," +
                TITLE + "," +
                DATE + "," +
                CONTENT + "," +
                WEATHER + "," +
                FILEPATH + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
