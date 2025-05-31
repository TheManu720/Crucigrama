package crossword;

import java.util.*;

public class BancoPalabras {
    private final List<String> palabras;

    public BancoPalabras(List<String> palabras) {
        this.palabras = new ArrayList<>(palabras);
    }

    public String encontrarMasCruzable() {
        int mejorPuntaje = -1;
        String mejorPalabra = palabras.get(0);

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
}

