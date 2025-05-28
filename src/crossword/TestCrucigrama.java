package crossword;
import model.Palabra;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class TestCrucigrama {

    @Test
    public void testCrearCrucigrama() {
        Crucigrama crucigrama = new Crucigrama(10);
        assertNotNull(crucigrama);
    }

    @Test
    public void testColocarPalabra() {
        Crucigrama crucigrama = new Crucigrama(10);
        Palabra palabra = new Palabra("JAVA");

        crucigrama.colocarPalabras(new ArrayList<>(List.of(palabra)));

        assertNotNull(palabra.getFila());
        assertNotNull(palabra.getColumna());
        assertNotNull(palabra.getDireccion());

    }
}
