package phoenixCorp.taka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginTaka extends AppCompatActivity {

    private EditText mID;
    private EditText mPassword;
    private Button mLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_taka);

        mID = findViewById(R.id.id_text);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.login_button);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mID.getText().toString().equals("") && mPassword.getText().toString().equals("")) {
                    Intent i = new Intent(LoginTaka.this, MainActivity.class);
                    startActivity(i);
                } else
                {
                    Toast.makeText(getApplicationContext(), " Invalid Credentials ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
