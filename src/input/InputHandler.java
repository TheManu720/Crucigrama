package input;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.text.Normalizer;

public class InputHandler {

    private static final int MIN_PALABRAS = 4;
    private static final int MIN_LONGITUD = 3;
    private static final int MAX_LONGITUD = 10;

    public List<String> solicitarPalabras() {
        Scanner scanner = new Scanner(System.in);
        List<String> palabras = new ArrayList<>();

        System.out.println("Ingresa al menos " + MIN_PALABRAS + " palabras para el crucigrama.");
        System.out.println("Cada palabra debe tener entre " + MIN_LONGITUD + " y " + MAX_LONGITUD + " caracteres.");

        while (palabras.size() < MIN_PALABRAS) {
            System.out.print("Ingresa la palabra #" + (palabras.size() + 1) + ": ");
            String palabra = scanner.nextLine().trim();

            if (palabra.isEmpty()) {
                System.out.println("No se permite una palabra vacía.");
            } else if (!esSoloLetras(palabra)) {
                System.out.println("Caracter inválido. Solo se permiten letras.");
            } else if (!esLongitudValida(palabra)) {
                System.out.println("Palabra inválida. Debe tener entre " + MIN_LONGITUD + " y " + MAX_LONGITUD + " caracteres.");
            } else {
                String limpia = quitarAcentos(palabra).toUpperCase();
                if (palabras.contains(limpia)) {
                    System.out.println("La palabra ya fue ingresada.");
                } else {
                    palabras.add(limpia);
                }
            }
        }
        return palabras;
    }

    private boolean esSoloLetras(String palabra){
        return palabra.matches("\\p{L}+");
    }

    private boolean esLongitudValida(String palabra){
        int len = palabra.length();
        return len >= MIN_LONGITUD && len <= MAX_LONGITUD;
    }

    private String quitarAcentos(String palabra) {
        String normalizada = Normalizer.normalize(palabra, Normalizer.Form.NFD);
        return normalizada.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}

