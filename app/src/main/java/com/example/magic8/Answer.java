package com.example.magic8;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Answer {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "answerText")
    public String answertext;

    public Answer() {

    }

    public Answer(String answer) {
        this.answertext = answer;
    }

}
