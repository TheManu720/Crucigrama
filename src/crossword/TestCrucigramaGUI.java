package crossword;
import javax.swing.*;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.util.function.Predicate;
import static org.junit.jupiter.api.Assertions.*;

public class TestCrucigramaGUI {
    @Test
    public void testCrearVentana() {
        CrucigramaGUI gui = new CrucigramaGUI(10);
        assertNotNull(gui);
    }
    @Test
    public void testCampoTextoExiste() {
        CrucigramaGUI gui = new CrucigramaGUI(10);
        boolean existe = buscarComponente(gui.getContentPane(),
                c -> c instanceof JTextArea || c instanceof JTextField
        );
        assertTrue(existe);
    }
    @Test
    public void testBotonGenerarExiste() {
        CrucigramaGUI gui = new CrucigramaGUI(10);
        boolean existe = buscarComponente(gui.getContentPane(),
                c -> c instanceof JButton && ((JButton) c).getText().equals("Generar Crucigrama")
        );
        assertTrue(existe);
    }
    private boolean buscarComponente(Container contenedor, Predicate<Component> criterio) {
        for (Component comp : contenedor.getComponents()) {
            if (criterio.test(comp)) {
                return true;
            } else if (comp instanceof Container subContenedor) {
                if (buscarComponente(subContenedor, criterio)) {
                    return true;
                }
            }
        }
        return false;
    }
}