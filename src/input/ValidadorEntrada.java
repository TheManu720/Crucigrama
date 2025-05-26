package input;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.text.Normalizer;

public class ValidadorEntrada {

    private static final int MIN_PALABRAS = 4;
    private static final int MIN_LONGITUD = 3;
    private static final int MAX_LONGITUD = 10;

    public List<String> solicitarPalabras() {
        Scanner scanner = new Scanner(System.in);
        List<String> palabras = new ArrayList<>();

        System.out.println("¿Cuántas palabras quieres ingresar? (mínimo " + MIN_PALABRAS + ")");
        int total = leerNumero(scanner);

        while (total < MIN_PALABRAS) {
            System.out.println("Debes ingresar al menos " + MIN_PALABRAS + " palabras.");
            total = leerNumero(scanner);
        }

        System.out.println("Cada palabra debe tener entre " + MIN_LONGITUD + " y " + MAX_LONGITUD + " caracteres.");

        while (palabras.size() < total) {
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

    private int leerNumero(Scanner scanner) {
        int num = -1;
        while (num < 0) {
            try {
                System.out.print("Número de palabras: ");
                num = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Debes ingresar un número.");
            }
        }
        return num;
    }

    public int solicitarTamanioTablero() {
        Scanner scanner = new Scanner(System.in);
        int tamanio = 0;

        while (tamanio < 10) {
            System.out.print("Ingrese el tamaño del tablero cuadrado (mínimo 10): ");
            try {
                tamanio = Integer.parseInt(scanner.nextLine().trim());
                if (tamanio < 10) {
                    System.out.println("El tamaño debe ser al menos 10.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Debes ingresar un número.");
            }
        }
        return tamanio;
    }

}