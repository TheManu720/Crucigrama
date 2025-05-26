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
        if (palabras.isEmpty()) return;

        // 1. Extraer solo los textos para encontrar la más cruzable
        List<String> textos = new ArrayList<>();
        for (Palabra p : palabras) textos.add(p.getTexto());

        // 2. Encontrar la palabra más cruzable
        String textoInicial = encontrarPalabraMasCruzable(textos);

        // 3. Encontrar el objeto Palabra que corresponde
        Palabra palabraInicial = null;
        Iterator<Palabra> it = palabras.iterator();
        while (it.hasNext()) {
            Palabra p = it.next();
            if (p.getTexto().equals(textoInicial)) {
                palabraInicial = p;
                it.remove(); // La saco para no repetir
                break;
            }
        }

        // 4. Colocar esa palabra inicial centrada
        int filaInicio = tamanio / 2;
        assert palabraInicial != null;
        int colInicio = (tamanio - palabraInicial.getTexto().length()) / 2;
        if (colInicio < 0) colInicio = 0;

        if (puedeColocar(palabraInicial, filaInicio, colInicio)) {
            colocarPalabra(palabraInicial, filaInicio, colInicio, Direccion.HORIZONTAL);
        } else {
            colocarPalabra(palabraInicial, 0, 0, Direccion.HORIZONTAL);
        }

        // 5. Colocar las demás palabras restantes
        for (Palabra p : palabras) {
            boolean colocada = intentarColocarPalabra(p);
            if (!colocada) {
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

        List<Point> posiciones = generarPosicionesEspiral();

        for (Point p : posiciones) {
            char c;

            // Intentamos con cada letra de la palabra para buscar cruces
            for (int i = 0; i < texto.length(); i++) {
                c = texto.charAt(i);

                // Si en el tablero en la posición actual coincide la letra que queremos cruzar
                if (p.x >= 0 && p.x < tamanio && p.y >= 0 && p.y < tamanio && tablero[p.x][p.y] == c) {
                    // Intentar colocar vertical
                    int startRow = p.x - i;
                    int startCol = p.y;
                    if (puedeColocarVerticalConEspacio(palabra, startRow, startCol)) {
                        int puntaje = contarCruces(palabra, startRow, startCol, Direccion.VERTICAL);
                        if (puntaje > mejorPuntaje) {
                            mejorPuntaje = puntaje;
                            mejorFila = startRow;
                            mejorCol = startCol;
                            mejorDireccion = Direccion.VERTICAL;
                        }
                    }

                    // Intentar colocar horizontal
                    startRow = p.x;
                    startCol = p.y - i;
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

        // Si no hubo cruces, intentamos colocar la palabra sin cruzar, pero también en espiral
        if (mejorPuntaje < 0) {
            for (Point p : posiciones) {
                if (puedeColocarHorizontalConEspacio(palabra, p.x, p.y)) {
                    mejorFila = p.x;
                    mejorCol = p.y;
                    mejorDireccion = Direccion.HORIZONTAL;
                    break;
                } else if (puedeColocarVerticalConEspacio(palabra, p.x, p.y)) {
                    mejorFila = p.x;
                    mejorCol = p.y;
                    mejorDireccion = Direccion.VERTICAL;
                    break;
                }
            }
        }

        if (mejorDireccion != null) {
            colocarPalabra(palabra, mejorFila, mejorCol, mejorDireccion);
            return true;
        }

        return false;
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
            // Mover derecha paso veces
            for (int i = 0; i < paso; i++) {
                x++;
                if (dentroDelTablero(x, y))
                    posiciones.add(new Point(x, y));
            }
            // Mover abajo paso veces
            for (int i = 0; i < paso; i++) {
                y++;
                if (dentroDelTablero(x, y))
                    posiciones.add(new Point(x, y));
            }
            paso++;
            // Mover izquierda paso veces
            for (int i = 0; i < paso; i++) {
                x--;
                if (dentroDelTablero(x, y))
                    posiciones.add(new Point(x, y));
            }
            // Mover arriba paso veces
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

    private String encontrarPalabraMasCruzable(List<String> palabras) {
        int mejorPuntaje = -1;
        String mejorPalabra = palabras.getFirst();

        for (String palabra : palabras) {
            int puntaje = 0;
            Set<Character> letras = new HashSet<>();
            for (char c : palabra.toCharArray()) letras.add(c);

            for (String otra : palabras) {
                if (otra.equals(palabra)) continue;
                for (char c : otra.toCharArray()) {
                    if (letras.contains(c)) puntaje++;
                }
            }

            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje;
                mejorPalabra = palabra;
            }
        }

        return mejorPalabra;
    }

    public int getTamanio() { return tamanio; }

    public char getLetra(int fila, int columna) {
        return tablero[fila][columna];
    }

    public boolean esLetraVisible(int fila, int columna) {
        return tablero[fila][columna] != ' ';
    }

}
