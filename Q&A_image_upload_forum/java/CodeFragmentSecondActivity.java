package phoenixCorp.taka;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class CodeFragmentSecondActivity extends AppCompatActivity {
    private static final String EXTRA_QUESTION_ID = "phoenixCorp.taka";
    public static Intent newIntent(Context packageContext, UUID questionId) {
        Intent intent = new Intent(packageContext, CodeFragmentSecondActivity.class);
        intent.putExtra(EXTRA_QUESTION_ID, questionId);
        return intent;
    }
    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_fragment);
        UUID questionId = (UUID)getIntent().getSerializableExtra(EXTRA_QUESTION_ID);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CFSAFragment.newInstance(questionId)).commit();
    }
}


