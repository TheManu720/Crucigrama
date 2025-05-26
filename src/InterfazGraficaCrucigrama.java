import crossword.CrucigramaGUI;

import javax.swing.*;

public class InterfazGraficaCrucigrama {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CrucigramaGUI ui = new CrucigramaGUI(1);
            ui.setVisible(true);
        });
    }
}
