package com.bignerdranch.watashinonikki.Database.DiaryDbSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.watashinonikki.DiaryEntry;

import java.util.Date;
import java.util.UUID;

import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.CONTENT;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.DATE;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.FILEPATH;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.TITLE;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.UUId;
import static com.bignerdranch.watashinonikki.Database.DiaryDbSchema.DiaryDbSchema.DiaryEntryTable.Cols.WEATHER;

public class DiaryEntryCursorWrapper extends CursorWrapper {

    public DiaryEntryCursorWrapper(Cursor cursor) { super(cursor); }

    public DiaryEntry getDiaryEntry(){

        String uuidString = getString(getColumnIndex(UUId));
        String titleString = getString(getColumnIndex(TITLE));
        long dateLong = getLong( getColumnIndex(DATE) );
        String weatherString = getString( getColumnIndex(WEATHER) );
        String contentString = getString( getColumnIndex(CONTENT));
        String imagePathString = getString( getColumnIndex(FILEPATH) );

        DiaryEntry entry = new DiaryEntry(UUID.fromString(uuidString));
        entry.setDate(new Date(dateLong));
        entry.setTitle(titleString);
        entry.setWeather(weatherString);
        entry.setContent(contentString);
        entry.setImagePath(imagePathString);

        return entry;

    }
}
