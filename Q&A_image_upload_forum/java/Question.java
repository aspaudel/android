package phoenixCorp.taka;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.UUID;

import phoenixCorp.taka.database.QuestionDbSchema.QuestionTable;


public class Question {
    private UUID mId;
    private String mQuestion;
    private int i = -1;
    public String getQuestion() {
        return mQuestion;
    }
    public void setQuestion(String question) {
        mQuestion = question;
    }
    public UUID getId() {
        return mId;
    }
    public Question(UUID id) {
        mId = id;
    }
    public Question() {
        this(UUID.randomUUID());
    }
    public void setI(int fileNo) {
        i = fileNo;
    }
    public String getPhotoFileName(int n, SQLiteDatabase mDatabase) {
        i = n;
        i++;
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionTable.Cols.UUID, mId.toString());
        contentValues.put(QuestionTable.Cols.QUESTION, mQuestion);
        contentValues.put(QuestionTable.Cols.FILENO, i);
        mDatabase.update(QuestionTable.NAME, contentValues, QuestionTable.Cols.UUID + " = ?", new String[]{mId.toString()});
        return "IMG" + "_" + i + getId().toString() + getQuestion() + ".jpg";
    }
    public int getI() {
        return i;
    }
}
