import input.ValidadorEntrada;
import model.Palabra;
import crossword.Crucigrama;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ValidadorEntrada validadorEntrada = new ValidadorEntrada();

        // Pedir tamaño del tablero (mínimo 10)
        int tamanio = validadorEntrada.solicitarTamanioTablero();

        // Pedir palabras
        List<String> palabrasIngresadas = validadorEntrada.solicitarPalabras();

        // Convertir a objetos Palabra
        List<Palabra> palabras = new ArrayList<>();
        for (String texto : palabrasIngresadas) {
            palabras.add(new Palabra(texto));
        }

        Crucigrama crucigrama = new Crucigrama(tamanio);
        crucigrama.colocarPalabras(palabras);

        System.out.println("\nCrucigrama generado:");
        crucigrama.imprimir();
    }
}

