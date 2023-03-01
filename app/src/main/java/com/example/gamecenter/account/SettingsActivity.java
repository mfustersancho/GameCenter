package com.example.gamecenter.account;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.gamecenter.R;
import com.example.gamecenter.db.controller.DatabaseAccess;
import com.example.gamecenter.gamemenu.GameMenuActivity;
import com.example.gamecenter.mainmenu.MainMenuActivity;
import com.example.gamecenter.scores.ScoreActivity;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    public static final String INTENT_USER_ID = "com.example.gamecenter.USER_ID";

    private long userId;

    private DatabaseAccess mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seetings);

        Button btnDeleteScores = findViewById(R.id.settings_button_delete_scores);
        Button btnDeleteAccount = findViewById(R.id.settings_button_delete_account);

        btnDeleteScores.setOnClickListener(v ->
                confirmDialog("Alert!", "Are you sure do you want to delete your scroes?", this::deleteScores));
        btnDeleteAccount.setOnClickListener(v ->
                confirmDialog("Alert!", "Are you sure do you want to delete your account?", this::deleteAccount));

        mDB = new DatabaseAccess(this);

        userId = getIntent().getLongExtra(INTENT_USER_ID, -1);
        if(userId == -1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void deleteScores() {
        mDB.deleteScoresByUserId(userId);
        informativeDialog("Scores deleted", "Your scores has been deleted.");
    }

    private void deleteAccount() {
        mDB.deleteUserByUserId(userId);
        informativeDialog("Account deleted", "Your account has been deleted");
    }

    private void informativeDialog(String title, String body) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(body);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void confirmDialog(String title, String body, Runnable method) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(body);
        builder.setPositiveButton("Yes", (dialog, which) -> method.run());
        builder.setNegativeButton("No", (dialog, which) -> { });
        AlertDialog dialog = builder.create();
        dialog.show();
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
        }
        if(intent != null) {
            startActivity(intent);
        }
        return true;
    }

}