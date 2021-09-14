package phoenixCorp.taka;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ZoomActivity extends AppCompatActivity {
    private static final String EXTRA_URL = "phoenixCorp.taka";
    public static Intent newIntent(Context packageContext, String url) {
        Intent intent = new Intent(packageContext, ZoomActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_fragment);
        String url = (String)getIntent().getSerializableExtra(EXTRA_URL);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ZoomImage.newInstance(url)).commit();
    }
}
