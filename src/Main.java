import input.InputHandler;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        InputHandler inputHandler = new InputHandler();
        List<String> palabras = inputHandler.solicitarPalabras();

        System.out.println("\nPalabras ingresadas:");
        for (String p : palabras) {
            System.out.println(p);
        }

        // Aquí continuarás con la generación del crucigrama...
    }
}
