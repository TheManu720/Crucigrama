package crossword;

import model.Palabra;
import java.awt.Point;
import java.util.*;

public class Crucigrama {
    private final int tamanio;
    private final char[][] tablero;

    public Crucigrama(int tamanio) {
        this.tamanio = tamanio;
        tablero = new char[tamanio][tamanio];

        for (int i = 0; i < tamanio; i++) {
            for (int j = 0; j < tamanio; j++) {
                tablero[i][j] = ' ';
            }
        }
    }

    public void colocarPalabras(List<Palabra> palabras) {
        colocarPalabraInicial(palabras);
        for (Palabra p : palabras) {
            boolean colocada = intentarColocarPalabra(p);
            if (!colocada) {
                for (int fila = 0; fila < tamanio; fila++) {
                    if (puedeColocarConEspacio(p, fila, 0, Direccion.VERTICAL)) {
                        colocarPalabra(p, fila, 0, Direccion.VERTICAL);
                        break;
                    }
                }
            }
        }
    }

    private void colocarPalabraInicial(List<Palabra> palabras) {
        if (palabras.isEmpty()) return;

        List<String> listaDeTextos = new ArrayList<>();
        for (Palabra p : palabras) {
            listaDeTextos.add(p.getTexto());
        }

        BancoPalabras banco = new BancoPalabras(listaDeTextos);
        String palabraMasCruzable = banco.encontrarMasCruzable();

        Palabra palabraInicial = null;
        Iterator<Palabra> it = palabras.iterator();
        while (it.hasNext()) {
            Palabra p = it.next();
            if (p.getTexto().equals(palabraMasCruzable)) {
                palabraInicial = p;
                it.remove();
                break;
            }
        }
        int filaInicio = tamanio / 2;
        assert palabraInicial != null;
        int colInicio = (tamanio - palabraInicial.getTexto().length()) / 2;
        if (colInicio < 0) colInicio = 0;
        if (puedeColocar(palabraInicial, filaInicio, colInicio)) {
            colocarPalabra(palabraInicial, filaInicio, colInicio, Direccion.HORIZONTAL);
        } else {
            colocarPalabra(palabraInicial, 0, 0, Direccion.HORIZONTAL);
        }
    }

    private boolean intentarColocarPalabra(Palabra palabra) {
        int[] mejorUbicacion = obtenerMejorUbicacion(palabra);
        if (mejorUbicacion != null) {
            int fila = mejorUbicacion[0];
            int col = mejorUbicacion[1];
            Direccion direccion = mejorUbicacion[2] == 0 ? Direccion.HORIZONTAL : Direccion.VERTICAL;
            colocarPalabra(palabra, fila, col, direccion);
            return true;
        }
        return false;
    }

    private int[] obtenerMejorUbicacion(Palabra palabra) {
        int mejorPuntaje = -1;
        int mejorFila = -1;
        int mejorCol = -1;
        int mejorDir = -1; // 0 = HORIZONTAL, 1 = VERTICAL
        String texto = palabra.getTexto();
        List<Point> posiciones = generarPosicionesEspiral();

        for (Point p : posiciones) {
            for (int i = 0; i < texto.length(); i++) {
                char c = texto.charAt(i);
                if (p.x >= 0 && p.x < tamanio && p.y >= 0 && p.y < tamanio && tablero[p.x][p.y] == c) {
                    // Horizontal
                    int fila = p.x;
                    int col = p.y - i;
                    if (puedeColocarConEspacio(palabra, fila, col, Direccion.HORIZONTAL)) {
                        int puntaje = contarCruces(palabra, fila, col, Direccion.HORIZONTAL);
                        if (puntaje > mejorPuntaje) {
                            mejorPuntaje = puntaje;
                            mejorFila = fila;
                            mejorCol = col;
                            mejorDir = 0;
                        }
                    }
                    // Vertical
                    fila = p.x - i;
                    col = p.y;
                    if (puedeColocarConEspacio(palabra, fila, col, Direccion.VERTICAL)) {
                        int puntaje = contarCruces(palabra, fila, col, Direccion.VERTICAL);
                        if (puntaje > mejorPuntaje) {
                            mejorPuntaje = puntaje;
                            mejorFila = fila;
                            mejorCol = col;
                            mejorDir = 1;
                        }
                    }
                }
            }
        }
        if (mejorPuntaje < 0) {
            for (Point p : posiciones) {
                if (puedeColocarConEspacio(palabra, p.x, p.y, Direccion.HORIZONTAL)) {
                    return new int[]{p.x, p.y, 0};
                }
                if (puedeColocarConEspacio(palabra, p.x, p.y, Direccion.VERTICAL)) {
                    return new int[]{p.x, p.y, 1};
                }
            }
        }

        return mejorDir != -1 ? new int[]{mejorFila, mejorCol, mejorDir} : null;
    }

    private List<Point> generarPosicionesEspiral() {
        List<Point> posiciones = new ArrayList<>();
        int cx = tamanio / 2;
        int cy = tamanio / 2;
        posiciones.add(new Point(cx, cy));
        int paso = 1;
        int x = cx;
        int y = cy;

        while (posiciones.size() < tamanio * tamanio) {
            for (int i = 0; i < paso; i++) {
                x++;
                if (dentroDelTablero(x, y))
                    posiciones.add(new Point(x, y));
            }
            for (int i = 0; i < paso; i++) {
                y++;
                if (dentroDelTablero(x, y))
                    posiciones.add(new Point(x, y));
            }
            paso++;
            for (int i = 0; i < paso; i++) {
                x--;
                if (dentroDelTablero(x, y))
                    posiciones.add(new Point(x, y));
            }
            for (int i = 0; i < paso; i++) {
                y--;
                if (dentroDelTablero(x, y))
                    posiciones.add(new Point(x, y));
            }
            paso++;
        }
        return posiciones;
    }

    private boolean dentroDelTablero(int x, int y) {
        return x >= 0 && x < tamanio && y >= 0 && y < tamanio;
    }

    private int contarCruces(Palabra palabra, int fila, int col, Direccion direccion) {
        String texto = palabra.getTexto();
        int cruces = 0;

        if (direccion == Direccion.HORIZONTAL) {
            for (int i = 0; i < texto.length(); i++) {
                if (tablero[fila][col + i] == texto.charAt(i)) cruces++;
            }
        } else {
            for (int i = 0; i < texto.length(); i++) {
                if (tablero[fila + i][col] == texto.charAt(i)) cruces++;
            }
        }
        return cruces;
    }

    private boolean puedeColocar(Palabra palabra, int fila, int col) {
        String texto = palabra.getTexto();

        if (col < 0 || col + texto.length() > tamanio || fila < 0 || fila >= tamanio) return false;
        for (int i = 0; i < texto.length(); i++) {
            char actual = tablero[fila][col + i];
            if (actual != ' ' && actual != texto.charAt(i)) {
                return false; // Conflicto de letras
            }
        }
        return true;
    }

    private void colocarPalabra(Palabra palabra, int fila, int col, Direccion direccion) {
        String texto = palabra.getTexto();

        if (direccion == Direccion.HORIZONTAL) {
            for (int i = 0; i < texto.length(); i++) {
                tablero[fila][col + i] = texto.charAt(i);
            }
        } else {
            for (int i = 0; i < texto.length(); i++) {
                tablero[fila + i][col] = texto.charAt(i);
            }
        }
        palabra.definirUbicacion(fila, col, direccion);
    }

    public void imprimir() {
        for (int i = 0; i < tamanio; i++) {
            for (int j = 0; j < tamanio; j++) {
                System.out.print(tablero[i][j] == ' ' ? '.' : tablero[i][j]);
                System.out.print(' ');
            }
            System.out.println();
        }
    }

    private boolean puedeColocarConEspacio(Palabra palabra, int fila, int col, Direccion dir) {
        String texto = palabra.getTexto();
        if (dir == Direccion.HORIZONTAL) {
            if (col < 0 || col + texto.length() > tamanio) return false;
            if (col > 0 && tablero[fila][col - 1] != ' ') return false;
            if (col + texto.length() < tamanio && tablero[fila][col + texto.length()] != ' ') return false;

            for (int i = 0; i < texto.length(); i++) {
                int c = col + i;
                char actual = tablero[fila][c];
                if (actual != ' ' && actual != texto.charAt(i)) return false;
                if (actual == ' ') {
                    if (fila > 0 && tablero[fila - 1][c] != ' ') return false;
                    if (fila < tamanio - 1 && tablero[fila + 1][c] != ' ') return false;
                }
            }
        } else {
            if (fila < 0 || fila + texto.length() > tamanio) return false;
            if (fila > 0 && tablero[fila - 1][col] != ' ') return false;
            if (fila + texto.length() < tamanio && tablero[fila + texto.length()][col] != ' ') return false;

            for (int i = 0; i < texto.length(); i++) {
                int f = fila + i;
                char actual = tablero[f][col];
                if (actual != ' ' && actual != texto.charAt(i)) return false;
                if (actual == ' ') {
                    if (col > 0 && tablero[f][col - 1] != ' ') return false;
                    if (col < tamanio - 1 && tablero[f][col + 1] != ' ') return false;
                }
            }
        }
        return true;
    }
}
