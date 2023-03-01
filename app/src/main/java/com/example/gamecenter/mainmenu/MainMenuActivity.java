package com.example.gamecenter.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamecenter.about.AboutActivity;
import com.example.gamecenter.account.LoginActivity;
import com.example.gamecenter.R;
import com.example.gamecenter.account.SettingsActivity;
import com.example.gamecenter.gamemenu.GameMenuActivity;
import com.example.gamecenter.scores.ScoreActivity;

public final class MainMenuActivity extends AppCompatActivity {

    private static final String TAG = "MainMenuActivity";

    public static final String INTENT_USER_ID = "com.example.gamecenter.USER_ID";

    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ListView menuList = findViewById(R.id.main_menu_list_view_menu);
        String[] items = getResources().getStringArray(R.array.menu_items);
        ArrayAdapter<String> adapt = new ArrayAdapter<>(this, R.layout.menu_item, items);
        menuList.setAdapter(adapt);
        menuList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent;
            switch(position) {
                case 0:
                    intent = new Intent(getApplicationContext(), GameMenuActivity.class);
                    intent.putExtra(GameMenuActivity.INTENT_USER_ID, userId);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(getApplicationContext(), ScoreActivity.class);
                    intent.putExtra(ScoreActivity.INTENT_USER_ID, userId);
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    intent.putExtra(SettingsActivity.INTENT_USER_ID, userId);
                    startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(getApplicationContext(), AboutActivity.class);
                    intent.putExtra(AboutActivity.INTENT_USER_ID, userId);
                    startActivity(intent);
                    break;
            }
        });

        userId = getIntent().getLongExtra(INTENT_USER_ID, -1);
        if(userId == -1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent intent = null;
        switch(item.getItemId()) {
            case R.id.menu_logoff:
                intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                break;
        }
        if(intent != null) {
            startActivity(intent);
        }
        return true;
    }
}