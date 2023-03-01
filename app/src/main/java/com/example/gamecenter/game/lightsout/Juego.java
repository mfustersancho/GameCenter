package com.example.gamecenter.game.lightsout;

import com.example.gamecenter.game.lightsout.solver.base.GridInterface;
import com.example.gamecenter.game.lightsout.solver.base.PatternInterface;
import com.example.gamecenter.game.lightsout.solver.solver.Solver;
import com.example.gamecenter.game.lightsout.solver.solver.solution.Solution;
import com.example.gamecenter.game.lightsout.solver.solver.solution.Solutions;
import com.example.gamecenter.game.lightsout.solver.utils.Coord;
import com.example.gamecenter.game.lightsout.solver.utils.GridUtils;
import com.example.gamecenter.game.lightsout.solver.utils.PatternUtils;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.IntFunction;

public final class Juego {

    private int dimension;
    private int indice;
    private ArrayList<int[]> jugadas;
    private boolean[][] tablero;

    private int[] pista;

    public Juego(int dimension) {
        this.dimension = dimension;
        reiniciar();
    }

    public void reiniciar() {
        Random r = new Random();
        tablero = new boolean[dimension][dimension];
        jugadas = new ArrayList<>();
        for(int i=0; i<dimension; ++i) {
            for(int j=0; j<dimension; ++j) {
                if(r.nextBoolean()) {
                    jugada(i, j);
                }
            }
        }
        jugadas.clear();
        indice = -1;
        pista = new int[]{-1, -1};
    }

    public void jugada(int x, int y) {
        jugada(x, y, false);
    }

    public void jugada(int x, int y, boolean esAutomatica) {
        // Actualizar el botón de arriba
        if (y > 0) {
            tablero[y - 1][x] = !tablero[y - 1][x];
        }
        // Actualizar el botón de la izquierda
        if (x > 0) {
            tablero[y][x - 1] = !tablero[y][x - 1];
        }
        // Actualizar el botón pulsado
        tablero[y][x] = !tablero[y][x];
        // Actualizar el botón de la derecha
        if (x < dimension - 1) {
            tablero[y][x + 1] = !tablero[y][x + 1];
        }
        // Actualizar el botón de abajo
        if (y < dimension - 1) {
            tablero[y + 1][x] = !tablero[y + 1][x];
        }
        if(!esAutomatica) {
            jugadas.add(new int[]{x, y});
            ++indice;
            while(jugadas.size() > indice+1) {
                jugadas.remove(indice+1);
            }
        }
    }

    public void realizarJugadaAnterior() {
        if(indice == -1) {
            return;
        }
        jugada(jugadas.get(indice)[0], jugadas.get(indice)[1], true);
        --indice;
    }

    public void realizarJugadaPosterior() {
        if(indice == jugadas.size()-1) {
            return;
        }
        ++indice;
        jugada(jugadas.get(indice)[0], jugadas.get(indice)[1], true);
    }

    public boolean testFin() {
        for(int i=0; i<dimension; ++i) {
            for(int j=0; j<dimension; ++j) {
                if(tablero[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean[][] getTablero() {
        return tablero;
    }

    public int getDimension() {
        return dimension;
    }

    public boolean hayJugadasAnteriores() {
        return jugadas != null && indice != -1;
    }

    public boolean hayJugadasPosteriores() {
        return jugadas != null && indice  != jugadas.size() - 1;
    }


    // Devuelve {fila, columna} de la pista
    public ArrayList<int[]> obtenerPista() {
        // Prepara la entrada para el solver
        ArrayList<Coord> coords = new ArrayList<>();
        for(int y=0; y<dimension; ++y) {
            for(int x=0; x<dimension; ++x) {
                if(tablero[y][x]) {
                    coords.add(new Coord(y, x));
                }
            }
        }

        GridInterface startGrid = GridUtils.getGridWithSomeActivatedCoords(dimension, dimension, coords);
        GridInterface finalGrid = GridUtils.getEmptyGrid(dimension, dimension);
        PatternInterface pattern = PatternUtils.getClassicPattern();

        // Darle los datos al solver
        Solver solver = new Solver(startGrid, finalGrid, pattern);

        // Obtener todos los resultados
        Set<Solution> solutionsSet = solver.solve().getComputedSolutions();

        // Esconger la mejor solución (la que tiene menos clicks)
        Optional<Solution> bestSolution=solutionsSet.stream().min((una, otra) ->
                (int) (una.stream().count() - otra.stream().count()));

        // Preparar la mejor solución para devolvérsela a la vista
        ArrayList<int[]> hint = new ArrayList<>();
        bestSolution.get().forEach(coord -> {
            hint.add(new int[]{ coord.getRow(), coord.getColumn() });
        });

        return hint;
    }

}
