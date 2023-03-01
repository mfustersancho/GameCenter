package com.example.gamecenter.about;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;

import com.example.gamecenter.R;
import com.example.gamecenter.account.LoginActivity;
import com.example.gamecenter.gamemenu.GameMenuActivity;
import com.example.gamecenter.mainmenu.MainMenuActivity;
import com.example.gamecenter.scores.ScoreActivity;

public class AboutActivity extends AppCompatActivity {

    public static final String INTENT_USER_ID = "com.example.gamecenter.USER_ID";

    private long userId;

    TableLayout mTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mTableLayout = findViewById(R.id.about_table);

        mTableLayout.getChildAt(0).setOnClickListener(v -> {
            launchExplorer("https://www.facebook.com/miquel.fuster.sancho");
        });

        mTableLayout.getChildAt(1).setOnClickListener(v -> {
            launchExplorer("https://twitter.com/Miquel_Fuster");
        });

        mTableLayout.getChildAt(2).setOnClickListener(v -> {
            launchExplorer("https://www.instagram.com/mfussan/");
        });

        mTableLayout.getChildAt(3).setOnClickListener(v -> {
            launchExplorer("https://pastebin.com/u/Miquel_Fuster");
        });

        mTableLayout.getChildAt(4).setOnClickListener(v -> {
            launchExplorer("https://github.com/Ak4n0");
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
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent intent = null;
        switch(item.getItemId()) {
            case R.id.menu_scores:
                intent = new Intent(this, ScoreActivity.class);
                intent.putExtra(ScoreActivity.INTENT_USER_ID, userId);
                break;
            case R.id.menu_game_menu:
                intent = new Intent(this, GameMenuActivity.class);
                intent.putExtra(GameMenuActivity.INTENT_USER_ID, userId);
                break;
            case R.id.menu_logoff:
                intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                break;
            case R.id.menu_home_menu:
            default:
                intent = new Intent(this, MainMenuActivity.class);
                intent.putExtra(MainMenuActivity.INTENT_USER_ID, userId);
                break;
        }
        if(intent != null) {
            startActivity(intent);
        }
        return true;
    }

    private void launchExplorer(String uri) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(browserIntent);
    }
}