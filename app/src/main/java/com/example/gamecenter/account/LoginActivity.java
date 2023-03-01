package com.example.gamecenter.account;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gamecenter.R;
import com.example.gamecenter.db.controller.DatabaseAccess;
import com.example.gamecenter.db.model.DBUser;
import com.example.gamecenter.mainmenu.MainMenuActivity;

import pl.droidsonroids.gif.GifImageView;

public final class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    private static final int CONTROL_USERNAME = 0;
    private static final int CONTROL_LOGIN = 1;
    private static final int CONTROL_SIGNIN = 2;

    private DatabaseAccess mDB = new DatabaseAccess(this);

    private EditText mUserName;
    private TextView mLabelPassword;
    private EditText mPassword;
    private TextView mLabelRepeatedPassword;
    private EditText mRepeatedPassword;
    private Button mButton;

    private int mActivityState;

    private DBUser mUserCredentials;

    private GifImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserName = findViewById(R.id.login_text_username);
        mLabelPassword = findViewById(R.id.login_label_password);
        mPassword = findViewById(R.id.login_text_password);
        mLabelRepeatedPassword = findViewById(R.id.login_label_repeat_password);
        mRepeatedPassword = findViewById(R.id.login_text_repeat_password);
        mButton = findViewById(R.id.login_button);

        mLabelPassword.setVisibility(View.GONE);
        mPassword.setVisibility(View.GONE);
        mLabelRepeatedPassword.setVisibility(View.GONE);
        mRepeatedPassword.setVisibility(View.GONE);

        mButton.setText("Access");

        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mActivityState > CONTROL_USERNAME) {
                    mActivityState = CONTROL_USERNAME;
                    updateVisibilityState();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mButton.setOnClickListener(v -> userControlAccess());

        mActivityState = CONTROL_USERNAME;
    }

    private void userControlAccess() {
        switch(mActivityState) {
            case CONTROL_USERNAME:
                controlUsername();
                break;
            case CONTROL_LOGIN:
                controlLogin();
                break;
            case CONTROL_SIGNIN:
                controlSignin();
                break;

        }
    }

    // Username Control
    private void controlUsername() {
        if(mUserName.getText().length() == 0) {
            showDialog("Username missing", "You must enter an username");
            mUserName.requestFocus();
        } else {
            mUserCredentials = mDB.getDBUserByName(mUserName.getText().toString());
            if(mUserCredentials != null) {
                mActivityState = CONTROL_LOGIN;
                updateVisibilityState();
            } else {
                mActivityState = CONTROL_SIGNIN;
                updateVisibilityState();
            }
        }
    }

    // Log in control
    private void controlLogin() {
        if(mUserCredentials.userName.equals(mUserName.getText().toString())
            && mUserCredentials.password.equals(mPassword.getText().toString())) {
                goToMenu(mUserCredentials.id);
        } else {
            showDialog("Bad Credentials", "The password you given is not correct.");
            mPassword.requestFocus();
        }
    }

    // Sign in control
    private void controlSignin() {
        if(mPassword.getText().toString().equals(mRepeatedPassword.getText().toString())) {
            mUserCredentials = new DBUser();
            mUserCredentials.userName = mUserName.getText().toString();
            mUserCredentials.password = mPassword.getText().toString();
            long userId = mDB.insertUser(mUserCredentials);
            if(userId == -1) {
                showDialog("Error registering user", "An error occurred while trying to register the user.\nPlease try again.");
            } else {
                goToMenu(userId);
            }
        } else {
            showDialog("Bad Credentials", "Password and repeated password are not equals.");
            mRepeatedPassword.requestFocus();
        }
    }

    private void goToMenu(Long userId) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra(MainMenuActivity.INTENT_USER_ID, userId);
        startActivity(intent);
        finish();
    }

    private void updateVisibilityState() {
        switch (mActivityState) {
            case CONTROL_USERNAME:
                mLabelPassword.setVisibility(View.GONE);
                mPassword.setVisibility(View.GONE);
                mLabelRepeatedPassword.setVisibility(View.GONE);
                mRepeatedPassword.setVisibility(View.GONE);
                mButton.setText("Access");
                break;
            case CONTROL_LOGIN:
                mLabelPassword.setVisibility(View.VISIBLE);
                mPassword.setVisibility(View.VISIBLE);
                mPassword.requestFocus();
                mLabelRepeatedPassword.setVisibility(View.GONE);
                mRepeatedPassword.setVisibility(View.GONE);
                mButton.setText("Log in");
                break;
            case CONTROL_SIGNIN:
                mLabelPassword.setVisibility(View.VISIBLE);
                mPassword.setVisibility(View.VISIBLE);
                mPassword.requestFocus();
                mLabelRepeatedPassword.setVisibility(View.VISIBLE);
                mRepeatedPassword.setVisibility(View.VISIBLE);
                mButton.setText("Create user");
                break;
        }

    }

    private void showDialog(String title, String body) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(body);
        builder.setPositiveButton("Ok", (dialog, which) -> { });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}