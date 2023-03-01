package com.example.gamecenter.game.game2048;

import java.util.ArrayList;
import java.util.Random;

public final class Juego {
    public static final int DIM = 4;

    private static final String TAG = "Juego";

    private int[][] tablero;
    private Random r;

    private int maxValor;

    public Juego() {
        maxValor = 0;
        r = new Random();
        reiniciarTablero();
    }

    public void reiniciarTablero() {
        maxValor = 0;
        tablero = new int[DIM][DIM];
        conseguirSiguientePieza();
    }

    public int[][] getTablero() {
        return tablero;
    }

    public void conseguirSiguientePieza() {
        Casilla c = getCasillaVacia();
        if(c != null)
            tablero[c.fila][c.columna] = Math.random() <= 0.2? 4 : 2;
    }

    public boolean hayMovimientosValidos() {
        for(int i=0; i<DIM; ++i) {
            for(int j=0; j<DIM; ++j) {
                if(tablero[i][j] == 0) return true;
                if(i < DIM-1 && tablero[i][j] == tablero[i+1][j]) return true;
                if(j < DIM-1 && tablero[i][j] == tablero[i][j+1]) return true;
            }
        }
        return false;
    }

    public ArrayList<MovimientoRealizado> moverHaciaArriba() {
        ArrayList<MovimientoRealizado> movimientosSuma = sumarDesdeArriba();
        ArrayList<MovimientoRealizado> movimientoCompactacion = compactarDesdeArriba();
        return mezclarMovimientos(movimientosSuma, movimientoCompactacion);
    }

    private ArrayList<MovimientoRealizado> sumarDesdeArriba() {
        maxValor = 0;
        ArrayList<MovimientoRealizado> movimientos = new ArrayList<>();
        for(int col=0; col<DIM; ++col) {
            for(int fil_ini=0; fil_ini<DIM-1; ++fil_ini) {
                if(tablero[fil_ini][col] != 0) {
                    for(int fil_cur = fil_ini + 1; fil_cur < DIM; ++fil_cur) {
                        if(tablero[fil_cur][col] != 0) {
                            if(tablero[fil_ini][col] == tablero[fil_cur][col]) {
                                tablero[fil_ini][col] <<= 1;
                                tablero[fil_cur][col] = 0;
                                movimientos.add(new MovimientoRealizado(
                                        new Casilla(fil_cur, col),
                                        new Casilla(fil_ini, col)));
                                if(tablero[fil_ini][col] > maxValor) maxValor = tablero[fil_ini][col];
                            }
                            break;
                        }
                    }
                }
            }
        }
        return movimientos;
    }

    private ArrayList<MovimientoRealizado> compactarDesdeArriba() {
        ArrayList<MovimientoRealizado> movimientos = new ArrayList<>();
        for(int col=0; col<DIM; ++col) {
            for(int fil_ini=0; fil_ini<DIM-1; ++fil_ini) {
                if(tablero[fil_ini][col] == 0) {
                    for(int fil_cur = fil_ini + 1; fil_cur < DIM; ++fil_cur) {
                        if(tablero[fil_cur][col] != 0) {
                            tablero[fil_ini][col] = tablero[fil_cur][col];
                            tablero[fil_cur][col] = 0;
                            movimientos.add(new MovimientoRealizado(
                                    new Casilla(fil_cur, col),
                                    new Casilla(fil_ini, col)));
                            break;
                        }
                    }
                }
            }
        }
        return movimientos;
    }

    public ArrayList<MovimientoRealizado> moverHaciaAbajo() {
        ArrayList<MovimientoRealizado> movimientosSuma = sumarDesdeAbajo();
        ArrayList<MovimientoRealizado> movimientoCompactacion = compactarDesdeAbajo();
        return mezclarMovimientos(movimientosSuma, movimientoCompactacion);
    }

    private ArrayList<MovimientoRealizado> sumarDesdeAbajo() {
        maxValor = 0;
        ArrayList<MovimientoRealizado> movimientos = new ArrayList<>();
        for(int col=0; col<DIM; ++col) {
            for(int fil_ini=DIM-1; fil_ini>0; --fil_ini) {
                if(tablero[fil_ini][col] != 0) {
                    for(int fil_cur = fil_ini - 1; fil_cur >= 0; --fil_cur) {
                        if(tablero[fil_cur][col] != 0) {
                            if(tablero[fil_ini][col] == tablero[fil_cur][col]) {
                                tablero[fil_ini][col] <<= 1;
                                tablero[fil_cur][col] = 0;
                                movimientos.add(new MovimientoRealizado(
                                        new Casilla(fil_cur, col),
                                        new Casilla(fil_ini, col)));
                                if(tablero[fil_ini][col] > maxValor) maxValor = tablero[fil_ini][col];
                            }
                            break;
                        }
                    }
                }
            }
        }
        return movimientos;
    }

    private ArrayList<MovimientoRealizado> compactarDesdeAbajo() {
        ArrayList<MovimientoRealizado> movimientos = new ArrayList<>();
        for(int col=0; col<DIM; ++col) {
            for(int fil_ini=DIM-1; fil_ini>0; --fil_ini) {
                if(tablero[fil_ini][col] == 0) {
                    for(int fil_cur = fil_ini - 1; fil_cur >= 0; --fil_cur) {
                        if(tablero[fil_cur][col] != 0) {
                            tablero[fil_ini][col] = tablero[fil_cur][col];
                            tablero[fil_cur][col] = 0;
                            movimientos.add(new MovimientoRealizado(
                                    new Casilla(fil_cur, col),
                                    new Casilla(fil_ini, col)));
                            break;
                        }
                    }
                }
            }
        }
        return movimientos;
    }

    public ArrayList<MovimientoRealizado> moverHaciaIzquierda() {
        ArrayList<MovimientoRealizado> movimientosSuma = sumarDesdeIzquierda();
        ArrayList<MovimientoRealizado> movimientoCompactacion = compactarDesdeIzquierda();
        return mezclarMovimientos(movimientosSuma, movimientoCompactacion);
    }

    private ArrayList<MovimientoRealizado> sumarDesdeIzquierda() {
        maxValor = 0;
        ArrayList<MovimientoRealizado> movimientos = new ArrayList<>();
        for(int fil=0; fil<DIM; ++fil) {
            for(int col_ini=0; col_ini<DIM-1; ++col_ini) {
                if(tablero[fil][col_ini] != 0) {
                    for(int col_cur = col_ini + 1; col_cur < DIM; ++col_cur) {
                        if(tablero[fil][col_cur] != 0) {
                            if(tablero[fil][col_ini] == tablero[fil][col_cur]) {
                                tablero[fil][col_ini] <<= 1;
                                tablero[fil][col_cur] = 0;
                                movimientos.add(new MovimientoRealizado(
                                        new Casilla(fil, col_cur),
                                        new Casilla(fil, col_ini)));
                                if(tablero[fil][col_ini] > maxValor) maxValor = tablero[fil][col_ini];
                            }
                            break;
                        }
                    }
                }
            }
        }
        return movimientos;
    }

    private ArrayList<MovimientoRealizado> compactarDesdeIzquierda() {
        ArrayList<MovimientoRealizado> movimientos = new ArrayList<>();
        for(int fil=0; fil<DIM; ++fil) {
            for(int col_ini=0; col_ini<DIM-1; ++col_ini) {
                if(tablero[fil][col_ini] == 0) {
                    for(int col_cur = col_ini + 1; col_cur < DIM; ++col_cur) {
                        if(tablero[fil][col_cur] != 0) {
                            tablero[fil][col_ini] = tablero[fil][col_cur];
                            tablero[fil][col_cur] = 0;
                            movimientos.add(new MovimientoRealizado(
                                    new Casilla(fil, col_cur),
                                    new Casilla(fil, col_ini)));
                            break;
                        }
                    }
                }
            }
        }
        return movimientos;
    }

    public ArrayList<MovimientoRealizado> moverHaciaDerecha() {
        ArrayList<MovimientoRealizado> movimientosSuma = sumarDesdeDerecha();
        ArrayList<MovimientoRealizado> movimientoCompactacion = compactarDesdeDerecha();
        return mezclarMovimientos(movimientosSuma, movimientoCompactacion);
    }

    private ArrayList<MovimientoRealizado> sumarDesdeDerecha() {
        maxValor = 0;
        ArrayList<MovimientoRealizado> movimientos = new ArrayList<>();
        for(int fil=0; fil<DIM; ++fil) {
            for(int col_ini=DIM-1; col_ini>0; --col_ini) {
                if(tablero[fil][col_ini] != 0) {
                    for(int col_cur = col_ini - 1; col_cur >= 0; --col_cur) {
                        if(tablero[fil][col_cur] != 0) {
                            if(tablero[fil][col_ini] == tablero[fil][col_cur]) {
                                tablero[fil][col_ini] <<= 1;
                                tablero[fil][col_cur] = 0;
                                movimientos.add(new MovimientoRealizado(
                                        new Casilla(fil, col_cur),
                                        new Casilla(fil, col_ini)));
                                if(tablero[fil][col_ini] > maxValor) maxValor = tablero[fil][col_ini];
                            }
                            break;
                        }
                    }
                }
            }
        }
        return movimientos;
    }

    private ArrayList<MovimientoRealizado> compactarDesdeDerecha() {
        ArrayList<MovimientoRealizado> movimientos = new ArrayList<>();
        for(int fil=0; fil<DIM; ++fil) {
            for(int col_ini=DIM-1; col_ini>0; --col_ini) {
                if(tablero[fil][col_ini] == 0) {
                    for(int col_cur = col_ini - 1; col_cur >= 0; --col_cur) {
                        if(tablero[fil][col_cur] != 0) {
                            tablero[fil][col_ini] = tablero[fil][col_cur];
                            tablero[fil][col_cur] = 0;
                            movimientos.add(new MovimientoRealizado(
                                    new Casilla(fil, col_cur),
                                    new Casilla(fil, col_ini)));
                            break;
                        }
                    }
                }
            }
        }
        return movimientos;
    }

    private ArrayList<MovimientoRealizado> mezclarMovimientos(ArrayList<MovimientoRealizado> movimientosSuma,
                                                              ArrayList<MovimientoRealizado> movimientosCompactacion) {
        ArrayList<MovimientoRealizado> resultado = new ArrayList<>();
        for(MovimientoRealizado movimiento: movimientosSuma) {
            resultado.add(movimiento);
        }
        for(MovimientoRealizado movimiento: movimientosCompactacion) {
            resultado.add(movimiento);
        }
        return resultado;
    }

    public int obtenerMaxValor() {
        int retValue = maxValor;
        maxValor = 0;
        return retValue;
    }

    private Casilla getCasillaVacia() {
        ArrayList<Casilla> casillasVacias = new ArrayList<>();

        for(int y=0; y<DIM; ++y) {
            for(int x=0; x<DIM; ++x) {
                if(tablero[y][x] == 0) {
                    casillasVacias.add(new Casilla(y, x));
                }
            }
        }

        if(casillasVacias.size() > 0) {
            return casillasVacias.get(r.nextInt(casillasVacias.size()));
        } else {
            return null;
        }
    }

}