package crossword;

import input.ValidadorEntrada;
import model.Palabra;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;

public class CrucigramaGUI extends JFrame {
    private int tamanio;
    private Crucigrama crucigrama;
    private final JPanel panelTablero;
    private final JTextArea inputPalabras;
    private final JTextField campoTamanio;
    private final JLabel labelError;

    public CrucigramaGUI(int tamanioInicial) {
        this.tamanio = tamanioInicial;
        this.crucigrama = new Crucigrama(tamanio);

        setTitle("Crucigrama");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Entrada de ingreso de tamaño
        JPanel panelEntrada = new JPanel();
        panelEntrada.setLayout(new BoxLayout(panelEntrada, BoxLayout.Y_AXIS));
        JPanel panelTamanio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTamanio.setBorder(BorderFactory.createTitledBorder("Ingrese tamaño del tablero"));
        panelTamanio.add(new JLabel("Tamaño del tablero:"));
        campoTamanio = new JTextField(String.valueOf(tamanioInicial), 3);
        panelTamanio.add(campoTamanio);

        // Entrada para ingresar palabras
        JPanel panelPalabras = new JPanel(new BorderLayout());
        panelPalabras.setBorder(BorderFactory.createTitledBorder("Ingrese las palabras (una por fila)"));
        inputPalabras = new JTextArea(10, 18);
        panelPalabras.add(new JScrollPane(inputPalabras), BorderLayout.CENTER);

        JButton btnGenerar = new JButton("Generar Crucigrama");
        panelPalabras.add(btnGenerar, BorderLayout.SOUTH);

        labelError = new JLabel(" ");
        labelError.setForeground(Color.RED);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(btnGenerar, BorderLayout.NORTH);
        panelInferior.add(labelError, BorderLayout.SOUTH);

        panelPalabras.add(panelInferior, BorderLayout.SOUTH);

        panelEntrada.add(panelTamanio);
        panelEntrada.add(panelPalabras);

        // Panel para mostrar tablero
        panelTablero = new JPanel();
        panelTablero.setLayout(new GridLayout(tamanio, tamanio));
        panelTablero.setPreferredSize(new Dimension(500, 500));
        panelTablero.setBackground(Color.WHITE);

        JPanel panelContenedor = new JPanel(new BorderLayout());
        panelContenedor.add(panelEntrada, BorderLayout.WEST);
        panelContenedor.add(panelTablero, BorderLayout.CENTER);
        add(panelContenedor, BorderLayout.CENTER);

        btnGenerar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int nuevoTamanio = Integer.parseInt(campoTamanio.getText().trim());
                    if (nuevoTamanio < 10 || nuevoTamanio > 50) {
                        labelError.setText("El tamaño debe estar entre 10 y 50.");
                        return;
                    }

                    labelError.setText("");

                    tamanio = nuevoTamanio;
                    crucigrama = new Crucigrama(tamanio);
                    panelTablero.setLayout(new GridLayout(tamanio, tamanio));

                    inicializarTableroVacio();
                    generarCrucigrama();

                } catch (NumberFormatException ex) {
                    labelError.setText("Ingrese un número valido.");
                }
            }
        });
        inicializarTableroVacio();
    }

    private void inicializarTableroVacio() {
        panelTablero.removeAll();
        for (int i = 0; i < tamanio * tamanio; i++) {
            JLabel celda = new JLabel(" ", SwingConstants.CENTER);
            celda.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            celda.setFont(new Font("Monospaced", Font.BOLD, 20));
            panelTablero.add(celda);
        }
        panelTablero.revalidate();
        panelTablero.repaint();
    }

    private void generarCrucigrama() {
        String texto = inputPalabras.getText();
        String[] lineas = texto.split("\\r?\\n");
        ArrayList<Palabra> palabras = new ArrayList<>();
        HashSet<String> palabrasUnicas = new HashSet<>();
        String mensajeError = null;

        for (String linea : lineas) {
            String original = linea.trim();
            // Validaciones paso a paso
            if (original.isEmpty()) {
                mensajeError = "No se permiten líneas vacías.";
                break;
            }
            if (!ValidadorEntrada.esSoloLetras(original)) {
                mensajeError = "Palabra inválida: " + original;
                break;
            }
            if (!ValidadorEntrada.esLongitudValida(original)) {
                mensajeError = "Ingrese entre 3 y 10 caracteres.";
                break;
            }
            String palabraValidada = ValidadorEntrada.quitarAcentos(original).toUpperCase();

            if (!palabrasUnicas.contains(palabraValidada)) {
                palabras.add(new Palabra(palabraValidada));
                palabrasUnicas.add(palabraValidada);
            } else {
                mensajeError = "Palabra repetida: " + palabraValidada;
                break;
            }
        }
        if (mensajeError != null || palabras.size() < 4) {
            if (mensajeError == null) {
                mensajeError = "Ingrese mínimo 4 palabras válidas.";
            }
            labelError.setText(mensajeError);
            crucigrama = new CrucigramaNulo(tamanio);
        } else {
            labelError.setText("");
            crucigrama = new Crucigrama(tamanio);
            crucigrama.colocarPalabras(palabras);
        }

        mostrarTablero();
    }

    private void mostrarTablero() {
        panelTablero.removeAll();
        char[][] tablero = obtenerTablero();

        for (int i = 0; i < tamanio; i++) {
            for (int j = 0; j < tamanio; j++) {
                char c = tablero[i][j];
                JLabel celda = new JLabel(c == ' ' ? " " : String.valueOf(c), SwingConstants.CENTER);
                celda.setFont(new Font("Monospaced", Font.BOLD, 24));

                if (c != ' ') {
                    celda.setForeground(Color.BLACK);
                    celda.setBackground(Color.WHITE);
                    celda.setOpaque(true);
                    celda.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                } else {
                    celda.setForeground(Color.WHITE);
                    celda.setBackground(Color.WHITE);
                    celda.setOpaque(true);
                    celda.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                }
                panelTablero.add(celda);
            }
        }
        panelTablero.revalidate();
        panelTablero.repaint();
    }

    private char[][] obtenerTablero() {
        try {
            java.lang.reflect.Field field = Crucigrama.class.getDeclaredField("tablero");
            field.setAccessible(true);
            return (char[][]) field.get(crucigrama);
        } catch (Exception e) {
            e.printStackTrace();
            return new char[tamanio][tamanio];
        }
    }
}