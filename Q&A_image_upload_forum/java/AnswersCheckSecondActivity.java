package phoenixCorp.taka;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class AnswersCheckSecondActivity extends AppCompatActivity {
    private static final String EXTRA_QUESTION = "phoenixCorp.taka";
    public static Intent newIntent(Context packageContext, String question) {
        Intent intent = new Intent(packageContext, AnswersCheckSecondActivity.class);
        intent.putExtra(EXTRA_QUESTION, question);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_fragment);
        String question = (String)getIntent().getSerializableExtra(EXTRA_QUESTION);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AnswersCheckOnline.newInstance(question)).commit();
    }
}
