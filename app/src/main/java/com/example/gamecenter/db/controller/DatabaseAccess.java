package com.example.gamecenter.db.controller;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.gamecenter.R;
import com.example.gamecenter.db.model.DBGame;
import com.example.gamecenter.db.model.DBScore;
import com.example.gamecenter.db.model.DBUser;

import java.lang.reflect.Field;
import java.util.ArrayList;

public final class DatabaseAccess extends SQLiteOpenHelper {

    private static final String TAG = DatabaseAccess.class.getSimpleName();

    // Database basic info
    private static final String DATABASE_NAME = "game_center";
    private static final int DATABASE_VERSION = 1;


    // Entities info
    private static final class EntityUser {
        static final String TABLE_NAME = "user";
        static final String KEY_ID = "id";
        static final String KEY_USER_NAME = "user_name";
        static final String KEY_PASSWORD = "password";
    }

    private static final class EntityGame {
        static final String TABLE_NAME =  "game";
        static final String KEY_ID = "id";
        static final String KEY_TITLE = "title";
    };

    private static final class EntityScore {
        static final String TABLE_NAME = "score";
        static final String KEY_ID = "id";
        static final String KEY_TIMESTAMP = "timestamp";
        static final String KEY_SCORE = "score";
        static final String KEY_USER_ID = "user_id";
        static final String KEY_GAME_ID = "game_id";
    }

    // Table creation
    private static final String CREATE_USER_TABLE =
            "CREATE TABLE " + EntityUser.TABLE_NAME + " ("
                    + EntityUser.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + EntityUser.KEY_USER_NAME + " TEXT NOT NULL UNIQUE, "
                    + EntityUser.KEY_PASSWORD + " TEXT NOT NULL"
                    + ");";

    private static final String CREATE_GAME_TABLE =
            "CREATE TABLE " + EntityGame.TABLE_NAME + " ("
                    + EntityGame.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + EntityGame.KEY_TITLE + " TEXT NOT NULL UNIQUE"
                    + ");";

    private static final String CREATE_JOIN_TABLE =
            "CREATE TABLE " + EntityScore.TABLE_NAME + " ("
                    + EntityScore.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + EntityScore.KEY_TIMESTAMP + " TEXT DEFAULT CURRENT_TIMESTAMP, "
                    + EntityScore.KEY_SCORE + " INTEGER NOT NULL, "
                    + EntityScore.KEY_GAME_ID + " INTEGER NOT NULL, "
                    + EntityScore.KEY_USER_ID + " INTEGER NOT NULL"
                    + ");";



    // DB Accessors
    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    // App Context
    private Context context;

    public DatabaseAccess(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        getDatabaseName();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_GAME_TABLE);
        db.execSQL(CREATE_JOIN_TABLE);
        fillGameTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + EntityUser.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EntityGame.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EntityScore.TABLE_NAME);
        onCreate(db);
    }

    private void fillGameTable(SQLiteDatabase db) {
        Field[] fields = R.array.class.getFields();
        ContentValues values = new ContentValues();
        for(Field field: fields) {
            if(field.getName().startsWith("game_")) {
                try {
                    values.put(EntityGame.KEY_TITLE, context.getResources()
                            .getStringArray(field.getInt(null))[0]);
                    db.insert(EntityGame.TABLE_NAME, null, values);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Inserts
    public long insertUser(DBUser user) {
        long userId;

        if(mWritableDB == null) {
            mWritableDB = getWritableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put(EntityUser.KEY_USER_NAME, user.userName);
        values.put(EntityUser.KEY_PASSWORD, user.password);
        userId = mWritableDB.insert(EntityUser.TABLE_NAME, null, values);

        mWritableDB.close();
        mWritableDB = null;

        return userId;
    }

    public long insertScore(long userId, String title, long score) {
        DBGame game = getDBGameByTitle(title);

        long gamePlayedId;

        if(mWritableDB == null) {
            mWritableDB = getWritableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put(EntityScore.KEY_USER_ID, userId);
        values.put(EntityScore.KEY_GAME_ID, game.id);
        values.put(EntityScore.KEY_SCORE, score);
        gamePlayedId = mWritableDB.insert(EntityScore.TABLE_NAME, null, values);

        mWritableDB.close();
        mWritableDB = null;

        return gamePlayedId;
    }

    // Queries
    @SuppressLint("Range")
    public ArrayList<DBGame> getGamesList() {
        String query = "SELECT * FROM " + EntityGame.TABLE_NAME;
        Cursor cursor = null;

        ArrayList<DBGame> gameList = new ArrayList<>();

        if(mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }

        cursor = mReadableDB.rawQuery(query, null);
        cursor.moveToFirst();
        do {
            DBGame game = new DBGame();
            game.id = cursor.getLong(cursor.getColumnIndex(EntityGame.KEY_ID));
            game.title = cursor.getString(cursor.getColumnIndex(EntityGame.KEY_TITLE));
            gameList.add(game);
        } while(cursor.moveToNext());

        cursor.close();
        mReadableDB.close();
        mReadableDB = null;

        return gameList;
    }

    @SuppressLint("Range")
    public DBUser getDBUserByName(String userName) {
        Cursor cursor = null;
        DBUser user = null;

        if(mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }

        cursor = mReadableDB.query(
                EntityUser.TABLE_NAME,
                new String[]{EntityUser.KEY_ID, EntityUser.KEY_USER_NAME, EntityUser.KEY_PASSWORD},
                EntityUser.KEY_USER_NAME + "=?",
                new String[]{userName},
                null, null, null, null);

        if(cursor != null && cursor.getCount() == 1) {
            user = new DBUser();
            cursor.moveToFirst();
            user.id = cursor.getLong(cursor.getColumnIndex(EntityUser.KEY_ID));
            user.userName = cursor.getString(cursor.getColumnIndex(EntityUser.KEY_USER_NAME));
            user.password = cursor.getString(cursor.getColumnIndex(EntityUser.KEY_PASSWORD));
        }

        cursor.close();
        mReadableDB.close();
        mReadableDB = null;

        return user;
    }

    @SuppressLint("Range")
    public DBGame getDBGameByTitle(String title) {
        Cursor cursor = null;
        DBGame game = null;

        if(mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }

        cursor = mReadableDB.query(
                EntityGame.TABLE_NAME,
                new String[]{EntityGame.KEY_ID, EntityGame.KEY_TITLE},
                EntityGame.KEY_TITLE + "=?",
                new String[]{title},
                null, null, null, null);

        if(cursor != null && cursor.getCount() == 1) {
            game = new DBGame();
            cursor.moveToFirst();
            game.id = cursor.getLong(cursor.getColumnIndex(EntityGame.KEY_ID));
            game.title = cursor.getString(cursor.getColumnIndex(EntityGame.KEY_TITLE));

        }

        cursor.close();
        mReadableDB.close();
        mReadableDB = null;

        return game;
    }

    @SuppressLint("Range")
    public DBGame getDBGameById(long gameId) {
        Cursor cursor = null;
        DBGame game = null;

        if(mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }

        cursor = mReadableDB.query(
                EntityGame.TABLE_NAME,
                new String[]{EntityGame.KEY_ID, EntityGame.KEY_TITLE},
                EntityGame.KEY_ID + "=?",
                new String[]{String.valueOf(gameId)},
                null, null, null, null);

        if(cursor != null && cursor.getCount() == 1) {
            game = new DBGame();
            cursor.moveToFirst();
            game.id = cursor.getLong(cursor.getColumnIndex(EntityGame.KEY_ID));
            game.title = cursor.getString(cursor.getColumnIndex(EntityGame.KEY_TITLE));
        }

        cursor.close();
        mReadableDB.close();
        mReadableDB = null;

        return game;
    }

    @SuppressLint("Range")
    public ArrayList<DBScore> getDBScoreListByUserId(long userId) {
        Cursor cursor = null;
        ArrayList<DBScore> gamesPlayedList = new ArrayList<>();

        if(mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }

        cursor = mReadableDB.rawQuery(
                "select score.id, title, date(timestamp), time(timestamp), score "
                + "from score left join game on game_id = game.id "
                + "where user_id = ?;",
                new String[] { String.valueOf(userId) });
        Log.d(TAG, "cursor.getCount() = " + cursor.getCount());
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                DBScore gamePlayed = new DBScore();
                gamePlayed.id = cursor.getLong(0);
                gamePlayed.title = cursor.getString(1);
                gamePlayed.date = cursor.getString(2);
                gamePlayed.time = cursor.getString(3);
                gamePlayed.score = cursor.getLong(4);
                gamesPlayedList.add(gamePlayed);
            } while (cursor.moveToNext());
        }

        cursor.close();
        mReadableDB.close();
        mReadableDB = null;

        return gamesPlayedList;
    }

    public ArrayList<DBScore> getDBScoreListByUserIdAndGameId(long userId, long gameId) {
        Cursor cursor = null;
        ArrayList<DBScore> gamesPlayedList = new ArrayList<>();

        if(mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }

        cursor = mReadableDB.rawQuery(
                "select score.id, title, date(timestamp), time(timestamp), score "
                        + "from score left join game "
                        + "on game_id = game.id "
                        + "where user_id = ? and game_id = ?;",
                new String[]{
                        String.valueOf(userId),
                        String.valueOf(gameId)
                });

        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                DBScore gamePlayed = new DBScore();
                gamePlayed.id = cursor.getLong(0);
                gamePlayed.title = cursor.getString(1);
                gamePlayed.date = cursor.getString(2);
                gamePlayed.time = cursor.getString(3);
                gamePlayed.score = cursor.getLong(4);
                gamesPlayedList.add(gamePlayed);
            } while (cursor.moveToNext());
        }

        cursor.close();
        mReadableDB.close();
        mReadableDB = null;

        return gamesPlayedList;
    }

    // Delete
    public void deleteScoresByUserId(long userId) {
        if(mWritableDB == null) {
            mWritableDB = getWritableDatabase();
        }

        mWritableDB.delete(EntityScore.TABLE_NAME,
                EntityScore.KEY_USER_ID + "=?",
                new String[]{String.valueOf(userId)});

        mWritableDB.close();
        mWritableDB = null;
    }

    public void deleteUserByUserId(long userId) {
        // delete his scores first
        deleteScoresByUserId(userId);

        if(mWritableDB == null) {
            mWritableDB = getWritableDatabase();
        }

        mWritableDB.delete(EntityUser.TABLE_NAME,
                EntityUser.KEY_ID + "=?",
                new String[]{String.valueOf(userId)});

        mWritableDB.close();
        mWritableDB = null;
    }

}
