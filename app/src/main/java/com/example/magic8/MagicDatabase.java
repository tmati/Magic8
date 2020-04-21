package com.example.magic8;

import androidx.room.Database;

@Database(entities = {Answer.class}, version = 1)
public abstract class MagicDatabase extends androidx.room.RoomDatabase {
    public abstract AnswerDao answerDao();
}
