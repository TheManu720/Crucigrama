package input;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidadorEntradaTest {

    @Test
    void testEsSoloLetras_valido(){
        assertTrue(ValidadorEntrada.esSoloLetras("Hola"));
        assertTrue(ValidadorEntrada.esSoloLetras("Hola"));
    }

    @Test
    void testEsSoloLetras_invalido(){
        assertFalse(ValidadorEntrada.esSoloLetras("Hola123"));
        assertFalse(ValidadorEntrada.esSoloLetras("!Hola"));
    }

    @Test
    void testEsSoloLongitud_valido(){
        assertTrue(ValidadorEntrada.esLongitudValida("Sol"));
        assertTrue(ValidadorEntrada.esLongitudValida("Montaña"));
    }

    @Test
    void testEsSoloLongitud_invalido(){
        assertFalse(ValidadorEntrada.esLongitudValida("Hi"));
        assertFalse(ValidadorEntrada.esLongitudValida("abdcefghijk"));
    }

    @Test
    void testQuitarAcentos(){
        assertEquals("ARBOL", ValidadorEntrada.quitarAcentos("ÁRBOL"));
        assertEquals("MAMA", ValidadorEntrada.quitarAcentos("MAMÁ"));
    }
}
