package com.example.gamecenter.scores;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.gamecenter.account.LoginActivity;
import com.example.gamecenter.R;
import com.example.gamecenter.db.controller.DatabaseAccess;
import com.example.gamecenter.db.model.DBGame;
import com.example.gamecenter.db.model.DBScore;
import com.example.gamecenter.gamemenu.GameMenuActivity;
import com.example.gamecenter.mainmenu.MainMenuActivity;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = "ScoreActivity";

    public static final String INTENT_USER_ID = "com.example.gamecenter.USER_ID";

    private long userId;

    private Spinner mSpinnerGameSelection;

    private RecyclerView mRecyclerScores;
    private ScoreRecyclerAdapter mScoreRecyclerAdapter;

    private DatabaseAccess mDB;

    private ArrayList<DBGame> gameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        mSpinnerGameSelection = findViewById(R.id.score_spinner_game);

        mRecyclerScores = findViewById(R.id.score_recycler_score);
        mRecyclerScores.setLayoutManager(new LinearLayoutManager(this));
        mScoreRecyclerAdapter = new ScoreRecyclerAdapter(this);
        mRecyclerScores.setAdapter(mScoreRecyclerAdapter);

        userId = getIntent().getLongExtra(INTENT_USER_ID, -1);

        mDB = new DatabaseAccess(this);

        gameList = mDB.getGamesList();
        String[] titleList = new String[gameList.size() + 1];
        titleList[0] = "All games";
        for(int i=1; i<titleList.length; ++i) {
            titleList[i] = gameList.get(i-1).title;
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.score_spinner_item, titleList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerGameSelection.setAdapter(adapter);
        mSpinnerGameSelection.setOnItemSelectedListener(this);

        if(userId == -1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<DBScore> gamesPlayedList;
        if(position > 0) {
            long gameId = gameList.get(position-1).id;
            gamesPlayedList = mDB.getDBScoreListByUserIdAndGameId(userId, gameId);
            showGameList(gamesPlayedList);
        } else {
            onNothingSelected(parent);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        ArrayList<DBScore> gamesPlayedList;
        gamesPlayedList = mDB.getDBScoreListByUserId(userId);
        showGameList(gamesPlayedList);
    }

    private void showGameList(ArrayList<DBScore> gameList) {
        mScoreRecyclerAdapter.updateDataset(gameList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.score_menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent intent = null;
        switch(item.getItemId()) {
            case R.id.menu_game_menu:
                intent = new Intent(this, GameMenuActivity.class);
                intent.putExtra(GameMenuActivity.INTENT_USER_ID, userId);
                break;
            case R.id.menu_logoff:
                intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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