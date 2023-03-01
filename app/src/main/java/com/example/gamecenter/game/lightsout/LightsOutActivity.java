package com.example.gamecenter.game.lightsout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.gamecenter.account.LoginActivity;
import com.example.gamecenter.R;
import com.example.gamecenter.db.controller.DatabaseAccess;
import com.example.gamecenter.gamemenu.GameMenuActivity;
import com.example.gamecenter.mainmenu.MainMenuActivity;
import com.example.gamecenter.scores.ScoreActivity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public final class LightsOutActivity extends AppCompatActivity {

    public static final String INTENT_USER_ID = "com.example.gamecenter.USER_ID";

    private long userId;

    private DatabaseAccess mDB;

    private TableRow[][] mMatrizBotones;
    private Juego mJuego;
    private Button mBotonAnterior;
    private Button mBotonSiguiente;
    private Button mBotonReiniciar;
    private Button mBotonPista;

    private TableLayout mJocLayout;
    private TableRow.LayoutParams mRowLayout;
    private TableRow.LayoutParams mBotonLayout;

    private TextView mTiempoDeJuego;

    private boolean mTimerRunning;

    private int mSegundos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights_out);

        mDB = new DatabaseAccess(this);

        mJocLayout = findViewById(R.id.layoutJoc);
        mRowLayout = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        mBotonLayout = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        mBotonAnterior = findViewById(R.id.bPrevious);
        mBotonSiguiente = findViewById(R.id.bNext);
        mBotonReiniciar = findViewById(R.id.bReload);
        mBotonPista = findViewById(R.id.bHint);

        mTiempoDeJuego = findViewById(R.id.tiempo_de_juego);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    actualizarTimerVista();
                });
                if(isRunningTimer()) {
                    mSegundos++;
                }
            }
        }, 0, 1000);

        mBotonAnterior.setOnClickListener(v -> {
            mJuego.realizarJugadaAnterior();
            actualizarTablero();
            actualizarBotonesJugadas();
        });

        mBotonSiguiente.setOnClickListener(v -> {
            mJuego.realizarJugadaPosterior();
            actualizarTablero();
            actualizarBotonesJugadas();
        });

        mBotonReiniciar.setOnClickListener(v -> reiniciarJuego());

        mBotonPista.setOnClickListener(v -> obtenerPista());

        userId = getIntent().getLongExtra(INTENT_USER_ID, -1);
        if(userId == -1) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        reiniciarJuego();
    }

    private void obtenerPista() {
        ArrayList<int[]> solucion = mJuego.obtenerPista();
        solucion.forEach(pista -> {
            mMatrizBotones[pista[0]][pista[1]].setBackground(getDrawable(R.drawable.luz_pista));
        });
        mBotonPista.setEnabled(false);
    }

    private void botonPulsado(int x, int y) {
        mJuego.jugada(x, y);
        actualizarTablero();
        if(mJuego.testFin()) {
            finJuego();
        } else {
            mBotonAnterior.setEnabled(true);
            mBotonSiguiente.setEnabled(false);

            // activar el timer al efectuar la primera jugada
            if(!isRunningTimer()) {
                activarTimer();
            }
        }
    }

    private void actualizarTablero() {
        boolean[][] tablero = mJuego.getTablero();
        for(int i = 0; i< mJuego.getDimension(); ++i) {
            for(int j = 0; j< mJuego.getDimension(); ++j) {
                mMatrizBotones[i][j].setBackground(ContextCompat.getDrawable(mMatrizBotones[i][j].getContext(),
                        tablero[i][j]? R.drawable.luz_encendida : R.drawable.luz_apagada));
            }
        }
    }

    private void reiniciarJuego() {
        // reiniciar el conjunto timer
        desactivarTimer();
        mSegundos = 0;
        actualizarTimerVista();

        mJocLayout.removeAllViewsInLayout();
        mJuego = new Juego(5);
        mMatrizBotones = new TableRow[mJuego.getDimension()][mJuego.getDimension()];
        int dimension = mJuego.getDimension();
        mBotonPista.setEnabled(true);

        // Obtener dimensiones de la pantalla
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int windowWidth = displayMetrics.widthPixels;

        for (int i = 0; i < dimension; ++i) {
            // Generar un TableRow
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(mRowLayout);
            for (int j = 0; j < dimension; ++j) {
                // Generar un botón
                mMatrizBotones[i][j] = new TableRow(tr.getContext());
                mMatrizBotones[i][j].setLayoutParams(mBotonLayout);

                // Dar tamaño a los botones
                int minDimension = windowWidth / dimension;
                mMatrizBotones[i][j].setMinimumWidth(minDimension);
                mMatrizBotones[i][j].setMinimumHeight(minDimension);

                // Dar el listener
                final int x = j;
                final int y = i;
                mMatrizBotones[i][j].setOnClickListener(view -> botonPulsado(x, y));

                // Añadir el botón a la row
                tr.addView(mMatrizBotones[i][j]);
            }
            // Añadir la row a la TableLayout
            mJocLayout.addView(tr, mRowLayout);
        }

        mBotonAnterior.setEnabled(false);
        mBotonSiguiente.setEnabled(false);

        actualizarTablero();
    }

    private void actualizarBotonesJugadas() {
        mBotonAnterior.setEnabled(mJuego.hayJugadasAnteriores());
        mBotonSiguiente.setEnabled(mJuego.hayJugadasPosteriores());
    }

    private synchronized void activarTimer() {
        mTimerRunning = true;
    }

    private synchronized void desactivarTimer() {
        mTimerRunning = false;
    }

    private void actualizarTimerVista() {
        mTiempoDeJuego.setText(String.format("%02d:%02d", mSegundos/60, mSegundos%60));
    }

    private void finJuego() {
        desactivarTimer();
        guardarPuntuacion();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("You win!");
        builder.setMessage("Congratulations! You switched off all the lights.\nPlay again?");
        builder.setPositiveButton("Yes", (dialog, which) -> { reiniciarJuego(); });
        builder.setNegativeButton("No", (dialog, witch) -> {
            Intent intent = new Intent(this, MainMenuActivity.class);
            intent.putExtra(MainMenuActivity.INTENT_USER_ID, userId);
            startActivity(intent);
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private synchronized boolean isRunningTimer() {
        return mTimerRunning;
    }

    private void guardarPuntuacion() {
        int puntos = mSegundos;
        String title = getResources().getStringArray(R.array.game_lightout)[0];
        mDB.insertScore(userId, title, puntos);
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