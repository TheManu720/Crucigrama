import input.ValidadorEntrada;
import model.Palabra;
import crossword.Crucigrama;
import java.util.ArrayList;
import java.util.List;

public class ConsolaCrucigrama {
    public static void main(String[] args) {
        ValidadorEntrada validadorEntrada = new ValidadorEntrada();
        int tamanio = validadorEntrada.solicitarTamanioTablero();
        List<String> palabrasIngresadas = validadorEntrada.solicitarPalabras();
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
