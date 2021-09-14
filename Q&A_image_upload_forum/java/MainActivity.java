package phoenixCorp.taka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CodeFragment.Callback, CodeFragment.Callback2,  AnswersCheckFragment.AnswerCallback, UserNameFragment.nvCallback {
    private FloatingActionButton mfab;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    public static MenuItem userName;

    @Override
    public void nv1() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CodeFragment.newInstance(getApplicationContext())).commit();
    }
    @Override
    public void nv2() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, QuestionsUploadFragment.newInstance()).commit();
    }
    @Override
    public void nv3() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AnswersCheckFragment.newInstance()).commit();
    }
    @Override
    public void onQuestionIdSelectedAnswers(String question) {
        Intent i = AnswersCheckSecondActivity.newIntent(getApplicationContext(), question);
        startActivity(i);
    }
    @Override
    public void onQuestionIdSelected(UUID id) {
        Intent i = CodeFragmentSecondActivity.newIntent(getApplicationContext(), id);
        startActivity(i);
    }
    @Override
    public void onQuestionIdSelected2(String pregunta) {
        Intent i = CodeFragmentSecondActivityOnline.newIntent(getApplicationContext(), pregunta);
        startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        mfab = findViewById(R.id.fab);
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "What ya looking at Mofo", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        userName = menu.findItem(R.id.userName);
        userNameStatus();
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();
            navigationView.setCheckedItem(R.id.home_button);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    public void userNameStatus() {
        if(USPreferences.getStoredUS(getApplicationContext()) == null) {
            MenuItem item1 = navigationView.getMenu().getItem(1);
            item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Toast.makeText(getApplicationContext(), "You are required to set the Username before engaging in the application", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            MenuItem item2 = navigationView.getMenu().getItem(2);
            item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Toast.makeText(getApplicationContext(), "You are required to set the Username before engaging in the application", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            MenuItem item3 = navigationView.getMenu().getItem(3);
            item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Toast.makeText(getApplicationContext(), "You are required to set the Username before engaging in the application", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
        }else {
            userName.setTitle(USPreferences.getStoredUS(getApplicationContext()));
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.code: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, CodeFragment.newInstance(getApplicationContext())).commit();
                 break;
            case R.id.questions_upload: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, QuestionsUploadFragment.newInstance()).commit();
                 break;
            case R.id.answer_check: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AnswersCheckFragment.newInstance()).commit();
                 break;
            case R.id.home_button: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();
                 break;
            case R.id.userName: getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, UserNameFragment.newInstance(navigationView)).commit();
                 break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.basic_settings, menu);
        return true;
    }
}
