package com.example.gamecenter.game.game2048;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.example.gamecenter.account.LoginActivity;
import com.example.gamecenter.R;
import com.example.gamecenter.db.controller.DatabaseAccess;
import com.example.gamecenter.gamemenu.GameMenuActivity;
import com.example.gamecenter.mainmenu.MainMenuActivity;
import com.example.gamecenter.scores.ScoreActivity;

import java.util.ArrayList;

public final class g2048Activity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String TAG = "g2048Activity";

    public static final String INTENT_USER_ID = "com.example.gamecenter.USER_ID";

    private long userId;
    private DatabaseAccess mDB;

    private TextView[][] mTablero;
    private TextView mTxtScore;
    private TextView mTxtBest;
    private Juego mJuego;
    private GestureDetectorCompat mDetector;
    private final int UMBRAL_VELOCIDAD = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g2048);

        mDB = new DatabaseAccess(this);

        mJuego = new Juego();
        mTablero = new TextView[Juego.DIM][Juego.DIM];
        mDetector = new GestureDetectorCompat(this, this);
        mTxtScore = findViewById(R.id.txtScore);
        mTxtBest = findViewById(R.id.txtBest);

        Button newGame = findViewById(R.id.btnNewGame);
        newGame.setOnClickListener(view -> {
            reiniciarJuego();
        });

        inicializarComponentes();
        //inicializarTablero();
        userId = getIntent().getLongExtra(INTENT_USER_ID, -1);
        if(userId == -1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        inicializarTablero();
    }

    //region Eventos touch
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(mDetector.onTouchEvent(e)) {
            return true;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) { }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) { }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
        movimiento(vX, vY);
        return true;
    }
    //endregion

    private void inicializarComponentes() {
        // Meter en el array mTablero cada una de las casillas;
        mTablero[0][0] = findViewById(R.id.num0c0);
        mTablero[0][1] = findViewById(R.id.num0c1);
        mTablero[0][2] = findViewById(R.id.num0c2);
        mTablero[0][3] = findViewById(R.id.num0c3);

        mTablero[1][0] = findViewById(R.id.num1c0);
        mTablero[1][1] = findViewById(R.id.num1c1);
        mTablero[1][2] = findViewById(R.id.num1c2);
        mTablero[1][3] = findViewById(R.id.num1c3);

        mTablero[2][0] = findViewById(R.id.num2c0);
        mTablero[2][1] = findViewById(R.id.num2c1);
        mTablero[2][2] = findViewById(R.id.num2c2);
        mTablero[2][3] = findViewById(R.id.num2c3);

        mTablero[3][0] = findViewById(R.id.num3c0);
        mTablero[3][1] = findViewById(R.id.num3c1);
        mTablero[3][2] = findViewById(R.id.num3c2);
        mTablero[3][3] = findViewById(R.id.num3c3);
    }

    private void movimiento(float vX, float vY) {
        ArrayList<MovimientoRealizado> movimientosRealizados = new ArrayList<>();

        if(Math.abs(vX) > Math.abs(vY)) {
            if(vX > UMBRAL_VELOCIDAD) {
                movimientosRealizados = mJuego.moverHaciaDerecha();
            }
            else if(vX < -UMBRAL_VELOCIDAD) {
                movimientosRealizados = mJuego.moverHaciaIzquierda();
            }
        } else {
            if(vY > UMBRAL_VELOCIDAD) {
                movimientosRealizados = mJuego.moverHaciaAbajo();
            }
            else if(vY < -UMBRAL_VELOCIDAD) {
                movimientosRealizados = mJuego.moverHaciaArriba();
            }
        }

        int maxValorMovimiento = mJuego.obtenerMaxValor();
        String scoreString = mTxtScore.getText().toString();
        String maxScoreString = mTxtBest.getText().toString();
        int puntuacionActual = Integer.parseInt(scoreString);
        int puntuacionMaxima = Integer.parseInt(maxScoreString);

        puntuacionActual += maxValorMovimiento;
        mTxtScore.setText(String.valueOf(puntuacionActual));
        if(puntuacionMaxima < puntuacionActual) {
            mTxtBest.setText(String.valueOf(puntuacionActual));
        }

        mJuego.conseguirSiguientePieza();
        actualizarTablero(movimientosRealizados);
        if(!mJuego.hayMovimientosValidos()) {
            finJuego();
        }
    }

    private void actualizarTablero(ArrayList<MovimientoRealizado> movimientosRealizados) {

        for(MovimientoRealizado m: movimientosRealizados) {
            int[] initialLocation = new int[2];
            int[] finalLocation = new int[2];
            mTablero[m.casillaInicial.fila][m.casillaInicial.columna].getLocationInWindow(initialLocation);
            mTablero[m.casillaFinal.fila][m.casillaFinal.columna].getLocationInWindow(finalLocation);

            LinearLayout original = (LinearLayout) mTablero[m.casillaInicial.fila][m.casillaInicial.columna].getParent();

            float elevation = original.getElevation();
            original.setElevation(10);

            TranslateAnimation animation = new TranslateAnimation(0, finalLocation[0] - initialLocation[0],
                                                                0, finalLocation[1] - initialLocation[1]);
            animation.setDuration(UMBRAL_VELOCIDAD);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation animation) {}
                @Override public void onAnimationRepeat(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    inicializarTablero();
                    original.setElevation(elevation);
                }
            });
            animation.setInterpolator(new LinearInterpolator());
            original.startAnimation(animation);
        }
    }

    private void inicializarTablero() {
        for(int i=0; i<Juego.DIM; ++i) {
            for(int j=0; j<Juego.DIM; ++j) {
                cambiarCasilla(i, j);
            }
        }
    }

    private void cambiarCasilla(int i, int j) {
        int numero = mJuego.getTablero()[i][j];
        int maxCapacity = Math.min(mTablero[i][j].getWidth(), mTablero[i][j].getHeight());
        int nDigits = (int)(Math.log10(numero))+2;
        float textSize = (maxCapacity - 10f) / nDigits;
        switch(mJuego.getTablero()[i][j]) {
            case 0:
                mTablero[i][j].setTextColor(getResources().getColor(R.color.transparent, getTheme()));
                mTablero[i][j].setBackground(getDrawable(R.drawable.tile_0));
                mTablero[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
            case 2:
                mTablero[i][j].setTextColor(getResources().getColor(R.color.tile_dark_foreground, getTheme()));
                mTablero[i][j].setBackground(getDrawable(R.drawable.tile_2));
                mTablero[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
            case 4:
                mTablero[i][j].setTextColor(getResources().getColor(R.color.tile_dark_foreground, getTheme()));
                mTablero[i][j].setBackground(getDrawable(R.drawable.tile_4));
                mTablero[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
            case 8:
                mTablero[i][j].setTextColor(getResources().getColor(R.color.tile_bright_foreground, getTheme()));
                mTablero[i][j].setBackground(getDrawable(R.drawable.tile_8));
                mTablero[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
            case 16:
                mTablero[i][j].setTextColor(getResources().getColor(R.color.tile_bright_foreground, getTheme()));
                mTablero[i][j].setBackground(getDrawable(R.drawable.tile_16));
                mTablero[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
            case 32:
                mTablero[i][j].setTextColor(getResources().getColor(R.color.tile_bright_foreground, getTheme()));
                mTablero[i][j].setBackground(getDrawable(R.drawable.tile_32));
                mTablero[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
            case 64:
                mTablero[i][j].setTextColor(getResources().getColor(R.color.tile_bright_foreground, getTheme()));
                mTablero[i][j].setBackground(getDrawable(R.drawable.tile_64));
                mTablero[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
            case 128:
                mTablero[i][j].setTextColor(getResources().getColor(R.color.tile_bright_foreground, getTheme()));
                mTablero[i][j].setBackground(getDrawable(R.drawable.tile_128));
                mTablero[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
            case 256:
                mTablero[i][j].setTextColor(getResources().getColor(R.color.tile_bright_foreground, getTheme()));
                mTablero[i][j].setBackground(getDrawable(R.drawable.tile_256));
                mTablero[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
            case 512:
                mTablero[i][j].setTextColor(getResources().getColor(R.color.tile_bright_foreground, getTheme()));
                mTablero[i][j].setBackground(getDrawable(R.drawable.tile_512));
                mTablero[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
            case 1024:
                mTablero[i][j].setTextColor(getResources().getColor(R.color.tile_bright_foreground, getTheme()));
                mTablero[i][j].setBackground(getDrawable(R.drawable.tile_1024));
                mTablero[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
            case 2048:
                mTablero[i][j].setTextColor(getResources().getColor(R.color.tile_bright_foreground, getTheme()));
                mTablero[i][j].setBackground(getDrawable(R.drawable.tile_2048));
                mTablero[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
            default:
                mTablero[i][j].setTextColor(getResources().getColor(R.color.tile_bright_foreground, getTheme()));
                mTablero[i][j].setBackground(getDrawable(R.drawable.tile_rest));
                mTablero[i][j].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                break;
        }
        mTablero[i][j].setText(String.valueOf(mJuego.getTablero()[i][j]));
    }

    private void finJuego() {
        guardarPuntuacion();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("End!");
        builder.setMessage("There's no more possible moves.\nPlay again?");
        builder.setPositiveButton("Yes", (dialog, which) -> { reiniciarJuego(); });
        builder.setNegativeButton("No", (dialog, witch) -> {
            Intent intent = new Intent(this, MainMenuActivity.class);
            intent.putExtra(MainMenuActivity.INTENT_USER_ID, userId);
            startActivity(intent);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void guardarPuntuacion() {
        long puntos = Long.valueOf(mTxtScore.getText().toString());
        String title = getResources().getStringArray(R.array.game_2048)[0];
        mDB.insertScore(userId, title, puntos);
    }

    private void reiniciarJuego() {
        mJuego.reiniciarTablero();
        inicializarTablero();
        mTxtScore.setText("0");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.game, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Intent intent = null;
        switch(item.getItemId()) {
            case R.id.menu_home_menu:
                intent = new Intent(this, MainMenuActivity.class);
                intent.putExtra(MainMenuActivity.INTENT_USER_ID, userId);
                break;
            case R.id.menu_scores:
                intent = new Intent(this, ScoreActivity.class);
                intent.putExtra(ScoreActivity.INTENT_USER_ID, userId);
                break;
            case R.id.menu_logoff:
                intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                break;
            case R.id.menu_game_menu:
            default:
                intent = new Intent(this, GameMenuActivity.class);
                intent.putExtra(GameMenuActivity.INTENT_USER_ID, userId);
        }
        if(intent != null) {
            startActivity(intent);
        }
        return true;
    }

}