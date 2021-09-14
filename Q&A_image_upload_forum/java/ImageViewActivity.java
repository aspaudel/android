package phoenixCorp.taka;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.UUID;

public class ImageViewActivity extends AppCompatActivity {
    private static final String EXTRA_QUESTIONid = "phoenixCorp.taka";
    public static Intent newIntent(Context packageContext, UUID questionId) {
        Intent i = new Intent(packageContext, ImageViewActivity.class);
        i.putExtra(EXTRA_QUESTIONid, questionId);
        return i;
    }
    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_fragment);
        UUID questionId = (UUID)getIntent().getSerializableExtra(EXTRA_QUESTIONid);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ImageViewFragment.newInstance(questionId)).commit();
    }
}
