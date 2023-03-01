package com.example.gamecenter.gamemenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.gamecenter.account.LoginActivity;
import com.example.gamecenter.R;
import com.example.gamecenter.mainmenu.MainMenuActivity;
import com.example.gamecenter.scores.ScoreActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;

public final class GameMenuActivity extends AppCompatActivity {

    private static final String TAG = "GameMenuActivity";

    public static final String INTENT_USER_ID = "com.example.gamecenter.USER_ID";

    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        ArrayList<GameInfo> gameList = new ArrayList<>();

        Field[] fields = R.array.class.getFields();
        for(Field field: fields) {
            if(field.getName().startsWith("game_")) {
                try {
                    addGameToList(gameList, field.getInt(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        userId = getIntent().getLongExtra(INTENT_USER_ID, -1);

        RecyclerView recyclerView = findViewById(R.id.game_list);
        GameListAdapter gameListAdapter = new GameListAdapter(gameList, userId);
        recyclerView.setAdapter(gameListAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(userId == -1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void addGameToList(ArrayList<GameInfo> gameList, int gameInfoResource) {
        GameInfo game = new GameInfo();
        int resourceId;

        String[] gameInfo = getResources().getStringArray(gameInfoResource);
        game.title = gameInfo[0];
        game.description = gameInfo[1];
        resourceId = getResources().getIdentifier(gameInfo[2],
                "drawable",
                "com.example.gamecenter");
        game.icon = getDrawable(resourceId);
        try {
            game.activity = Class.forName(gameInfo[3]);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        gameList.add(game);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.game_menu, menu);
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

}