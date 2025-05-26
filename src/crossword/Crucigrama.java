package crossword;

import model.Palabra;

import java.util.List;

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
        if (palabras.isEmpty()) return;

        Palabra primera = palabras.get(0);
        int filaInicio = tamanio / 2;
        int colInicio = (tamanio - primera.getTexto().length()) / 2;
        if (colInicio < 0) colInicio = 0;

        // La primera palabra puede usar el método básico para colocar sin validar espacios
        if (puedeColocar(primera, filaInicio, colInicio, Direccion.HORIZONTAL)) {
            colocarPalabra(primera, filaInicio, colInicio, Direccion.HORIZONTAL);
        } else {
            // En caso que la primera palabra no quepa centrada (raro), colocar en fila 0
            colocarPalabra(primera, 0, 0, Direccion.HORIZONTAL);
        }

        for (int i = 1; i < palabras.size(); i++) {
            Palabra p = palabras.get(i);
            boolean colocada = intentarColocarPalabra(p);
            if (!colocada) {
                // En caso de no poder cruzar, colocar vertical en la primera columna libre, pero validando espacio
                for (int fila = 0; fila < tamanio; fila++) {
                    if (puedeColocarVerticalConEspacio(p, fila, 0)) {
                        colocarPalabra(p, fila, 0, Direccion.VERTICAL);
                        break;
                    }
                }
            }
        }
    }


    private boolean intentarColocarPalabra(Palabra palabra) {
        String texto = palabra.getTexto();
        int mejorPuntaje = -1;
        int mejorFila = -1;
        int mejorCol = -1;
        Direccion mejorDireccion = null;

        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);

            for (int fila = 0; fila < tamanio; fila++) {
                for (int col = 0; col < tamanio; col++) {
                    if (tablero[fila][col] == c) {
                        // Intentar colocar vertical, con validación de espacios
                        int startRow = fila - i;
                        int startCol = col;
                        if (puedeColocarVerticalConEspacio(palabra, startRow, startCol)) {
                            int puntaje = contarCruces(palabra, startRow, startCol, Direccion.VERTICAL);
                            if (puntaje > mejorPuntaje) {
                                mejorPuntaje = puntaje;
                                mejorFila = startRow;
                                mejorCol = startCol;
                                mejorDireccion = Direccion.VERTICAL;
                            }
                        }

                        // Intentar colocar horizontal, con validación de espacios
                        startRow = fila;
                        startCol = col - i;
                        if (puedeColocarHorizontalConEspacio(palabra, startRow, startCol)) {
                            int puntaje = contarCruces(palabra, startRow, startCol, Direccion.HORIZONTAL);
                            if (puntaje > mejorPuntaje) {
                                mejorPuntaje = puntaje;
                                mejorFila = startRow;
                                mejorCol = startCol;
                                mejorDireccion = Direccion.HORIZONTAL;
                            }
                        }
                    }
                }
            }
        }

        if (mejorPuntaje >= 0) {
            colocarPalabra(palabra, mejorFila, mejorCol, mejorDireccion);
            return true;
        }
        return false;
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


    private boolean puedeColocar(Palabra palabra, int fila, int col, Direccion direccion) {
        String texto = palabra.getTexto();

        if (direccion == Direccion.HORIZONTAL) {
            if (col < 0 || col + texto.length() > tamanio || fila < 0 || fila >= tamanio) return false;
            for (int i = 0; i < texto.length(); i++) {
                char actual = tablero[fila][col + i];
                if (actual != ' ' && actual != texto.charAt(i)) {
                    return false; // Conflicto de letras
                }
            }
        } else { // VERTICAL
            if (fila < 0 || fila + texto.length() > tamanio || col < 0 || col >= tamanio) return false;
            for (int i = 0; i < texto.length(); i++) {
                char actual = tablero[fila + i][col];
                if (actual != ' ' && actual != texto.charAt(i)) {
                    return false; // Conflicto
                }
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

        palabra.setPosicion(fila, col);
        palabra.setDireccion(direccion);
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


    private boolean puedeColocarHorizontalConEspacio(Palabra palabra, int fila, int col) {
        String texto = palabra.getTexto();

        // Verifica que la palabra quepa en el tablero
        if (col < 0 || col + texto.length() > tamanio) return false;

        // Verificar espacio antes y después de la palabra (si está dentro del tablero)
        if (col > 0 && tablero[fila][col - 1] != ' ') return false;
        if (col + texto.length() < tamanio && tablero[fila][col + texto.length()] != ' ') return false;

        for (int i = 0; i < texto.length(); i++) {
            int c = col + i;
            char actual = tablero[fila][c];
            char letra = texto.charAt(i);

            // Si hay letra y no coincide => no se puede
            if (actual != ' ' && actual != letra) return false;

            // Verificar que las celdas superiores e inferiores estén vacías (si no estamos cruzando)
            if (actual == ' ') {
                if (fila > 0 && tablero[fila - 1][c] != ' ') return false;
                if (fila < tamanio - 1 && tablero[fila + 1][c] != ' ') return false;
            }
        }

        return true;
    }


    private boolean puedeColocarVerticalConEspacio(Palabra palabra, int fila, int col) {
        String texto = palabra.getTexto();

        if (fila < 0 || fila + texto.length() > tamanio) return false;

        // Verificar espacio antes y después de la palabra
        if (fila > 0 && tablero[fila - 1][col] != ' ') return false;
        if (fila + texto.length() < tamanio && tablero[fila + texto.length()][col] != ' ') return false;

        for (int i = 0; i < texto.length(); i++) {
            int f = fila + i;
            char actual = tablero[f][col];
            char letra = texto.charAt(i);

            if (actual != ' ' && actual != letra) return false;

            // Verificar izquierda y derecha
            if (actual == ' ') {
                if (col > 0 && tablero[f][col - 1] != ' ') return false;
                if (col < tamanio - 1 && tablero[f][col + 1] != ' ') return false;
            }
        }

        return true;
    }
}
