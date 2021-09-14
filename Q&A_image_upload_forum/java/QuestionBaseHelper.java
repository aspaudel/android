package phoenixCorp.taka;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import phoenixCorp.taka.database.QuestionDbSchema.QuestionTable;


public class QuestionBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "questionBase.db";
    QuestionBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + QuestionTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                QuestionTable.Cols.UUID + ", " + QuestionTable.Cols.QUESTION + ", " + QuestionTable.Cols.FILENO + ")");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
