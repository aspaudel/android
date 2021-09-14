package phoenixCorp.taka;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CodeFragmentSecondActivityOnline extends AppCompatActivity {
    private static final String EXTRA_QUESTION = "phoenixCorp.taka";
    public static Intent newIntent(Context packageContext, String pregunta) {
        Intent intent = new Intent(packageContext, CodeFragmentSecondActivityOnline.class);
        intent.putExtra(EXTRA_QUESTION, pregunta);
        return intent;
    }
    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_fragment);
        String question = (String)getIntent().getSerializableExtra(EXTRA_QUESTION);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CFSAFragmentOnline.newInstance(question, getContentResolver())).commit();
    }
}

