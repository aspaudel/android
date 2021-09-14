package phoenixCorp.taka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import phoenixCorp.taka.database.QuestionDbSchema.QuestionTable;

public class QuestionLab {
    private static QuestionLab sQuestionLab;
    //private List<Question> mQuestions;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    public static QuestionLab get(Context context) {
        if(sQuestionLab == null) {
            sQuestionLab = new QuestionLab(context);
        }
        return sQuestionLab;
    }
    private QuestionLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new QuestionBaseHelper(mContext).getWritableDatabase();
        //mQuestions = new ArrayList<>();
    }
    public void addQuestion(Question question){
        ContentValues values = getContentValues(question);
        mDatabase.insert(QuestionTable.NAME, null, values);
        //mQuestions.add(question);
    }
    public void deleteQuestion(Question question) {
        //mQuestions.remove(question);
        mDatabase.delete(QuestionTable.NAME, QuestionTable.Cols.UUID + " = ?", new String[]{question.getId().toString()});
    }
    public Question getQuestion(UUID qId) {
        /*for(Question q : mQuestions) {
            if(q.getId().equals(qId)) {
                return q;
            }
        }*/
        QuestionCursorWrapper cursor = queryQuestions(QuestionTable.Cols.UUID + " = ?" , new String[] {qId.toString()});
        try {
            if(cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getQuestion();
        } finally {
            cursor.close();
        }
    }
    public File getPhotoFile(Question question, int n) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, question.getPhotoFileName(n, mDatabase));
    }
    public void deletePhotoFile(File file) {
        File mFile = mContext.getFilesDir();
        File[] mFiles = mFile.listFiles();
        for(int i = 0; i < mFiles.length; i++) {
            if(mFiles[i].getName().equals(file.getName())) {
                mContext.deleteFile(mFiles[i].getName());
            }
        }
    }
    public List<Question> getQuestions() {
        //return mQuestions;
        List<Question> questions = new ArrayList<>();
        QuestionCursorWrapper cursor = queryQuestions(null, null);
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                questions.add(cursor.getQuestion());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return questions;
    }
    private static ContentValues getContentValues(Question question) {
        ContentValues values = new ContentValues();
        values.put(QuestionTable.Cols.UUID, question.getId().toString());
        values.put(QuestionTable.Cols.QUESTION, question.getQuestion());
        values.put(QuestionTable.Cols.FILENO, question.getI());
        return values;
    }
    private QuestionCursorWrapper queryQuestions(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(QuestionTable.NAME, null, whereClause, whereArgs, null , null, null);
        return new QuestionCursorWrapper(cursor);
    }
}
