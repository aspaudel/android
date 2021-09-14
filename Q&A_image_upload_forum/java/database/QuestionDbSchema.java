package phoenixCorp.taka.database;

public class QuestionDbSchema {
    public static final class QuestionTable {
        public static final String NAME = "questions";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String QUESTION = "question";
            public static final String FILENO = "fileno";
        }
    }
}
