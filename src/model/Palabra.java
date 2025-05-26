package model;

import crossword.Direccion;

public class Palabra {
    private final String texto;
    private int fila;
    private int columna;
    private Direccion direccion;

    public Palabra(String texto) {
        this.texto = texto;
        this.fila = -1;
        this.columna = -1;
        this.direccion = null;
    }

    public String getTexto() { return texto; }

    public void setPosicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public Object getFila() {
        return fila;
    }

    public Object getColumna() {
        return columna;
    }

    public Object getDireccion() {
        return direccion;
    }
}