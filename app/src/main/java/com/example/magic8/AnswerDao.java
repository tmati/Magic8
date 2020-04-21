package com.example.magic8;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AnswerDao {

    @Query("Select * from Answer ORDER BY RANDOM() LIMIT 1")
    Answer findRandomAnswer();

    @Insert
    void addAnswer (Answer toAdd);

    @Insert
    void addDefaultAnswers(List<Answer> defaultAnswers);

    @Delete
    void delete(Answer toDelete);

    @Query("SELECT COUNT(*) FROM Answer")
    int getRowCount();

}
