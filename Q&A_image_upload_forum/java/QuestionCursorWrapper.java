package phoenixCorp.taka;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import phoenixCorp.taka.database.QuestionDbSchema.QuestionTable;

public class QuestionCursorWrapper extends CursorWrapper {
    public QuestionCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public Question getQuestion() {
        String uuidString = getString(getColumnIndex(QuestionTable.Cols.UUID));
        String question1 = getString(getColumnIndex(QuestionTable.Cols.QUESTION));
        int fileNo = getInt(getColumnIndex(QuestionTable.Cols.FILENO));

        Question question = new Question(UUID.fromString(uuidString));
        question.setI(fileNo);
        question.setQuestion(question1);

        return question;
    }
}
