package com.bignerdranch.watashinonikki;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.LoginFilter;

import com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryBaseHelper;
import com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryEntryCursorWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.CONTENT;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.DATE;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.FILEPATH;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.TITLE;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.UUId;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.WEATHER;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.NAME;

public class Diary {

    private static Diary sDiary;
    private SQLiteDatabase sDiaryDb;
    private Context sContext;

    private DiaryEntryCursorWrapper queryDiaryEntries(String whereClause,String[] whereArgs){

        Cursor cursor = sDiaryDb.query(
                NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                DATE + " desc"
        );

        return new DiaryEntryCursorWrapper(cursor);
    }

    private Diary(Context context){
        sContext = context;
        sDiaryDb = new DiaryBaseHelper(sContext)
                .getWritableDatabase();
    }

    public static Diary get(Context context){

        if( sDiary == null ) sDiary = new Diary(context);

        return sDiary;
    }

    public List<DiaryEntry> getEntries(){
        List<DiaryEntry> entries = new ArrayList<>();

        DiaryEntryCursorWrapper cursor = queryDiaryEntries(null, null);

        try{
            cursor.moveToFirst();

            while( !cursor.isAfterLast() ){
                entries.add(cursor.getDiaryEntry());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }

        return entries;
    }

    public DiaryEntry diaryEntryBasedonId(UUID id){
        String uuid = id.toString();

        DiaryEntryCursorWrapper cursor = queryDiaryEntries(UUId + " = ?", new String[]{uuid});

        try{
            if( cursor.getCount() ==0 ){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getDiaryEntry();
        }
        finally {
            cursor.close();
        }
    }

    public ContentValues getValues(DiaryEntry entry){
        //Content values allow us to structure our data so as to allow seamless updates of rows using exisiting SQLite API
        ContentValues values = new ContentValues();

        values.put(UUId,entry.getId().toString());
        values.put(TITLE, entry.getTitle());
        values.put(DATE, entry.getDate().getTime());
        values.put(WEATHER, entry.getWeather());
        values.put(CONTENT, entry.getContent());
        values.put(FILEPATH, entry.getImagePath());

        return values;
    }

    public void addEntry(DiaryEntry entry){
        ContentValues row = getValues(entry);
        sDiaryDb.insert(NAME, null, row);
    }

    public void modifyEntry(DiaryEntry entry){
        ContentValues updatedRow = getValues(entry);
        String uuid = entry.getId().toString();

        sDiaryDb.update(NAME, updatedRow, UUId + " = ?", new String[]{uuid} );
    }

    public void deleteEntry(UUID Id){
        String uuid = Id.toString();
        sDiaryDb.delete(NAME, UUId + " = ?" , new String[]{uuid});
    }
}
