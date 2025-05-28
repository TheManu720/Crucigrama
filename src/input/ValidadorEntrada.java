package input;

import java.text.Normalizer;

public class ValidadorEntrada {
    private static final int MIN_LONGITUD = 3;
    private static final int MAX_LONGITUD = 10;

    public static boolean esSoloLetras(String palabra){
        return palabra.matches("\\p{L}+");
    }

    public static boolean esLongitudValida(String palabra){
        int len = palabra.length();
        return len >= MIN_LONGITUD && len <= MAX_LONGITUD;
    }

    public static String quitarAcentos(String palabra) {
        if (palabra == null) return null;
        palabra = palabra.replace("Ñ", "##NN##").replace("ñ", "##nn##");
        String normalizada = Normalizer.normalize(palabra, Normalizer.Form.NFD);
        normalizada = normalizada.replaceAll("\\p{M}", "");
        normalizada = normalizada.replace("##NN##", "Ñ").replace("##nn##", "ñ");

        return normalizada;
    }
}