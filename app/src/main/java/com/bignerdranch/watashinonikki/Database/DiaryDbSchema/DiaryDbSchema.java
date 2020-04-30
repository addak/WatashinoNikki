package com.bignerdranch.watashinonikki.Database.DiaryDbSchema;

public class DiaryDbSchema {
    public static final class DiaryEntryTable{
        public static final String NAME = "DiaryEntries";

        public static final class Cols{
            public static final String UUId = "uuid";
            public static final String TITLE = "Title";
            public static final String DATE = "Date";
            public static final String WEATHER = "Weather";
            public static final String CONTENT = "Content";
            public static final String FILEPATH = "Filepath";
        }
    }
}
