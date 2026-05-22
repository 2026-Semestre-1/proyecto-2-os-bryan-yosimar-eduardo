/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Modelo.Instruccion;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edurg
 */
public class Codigo_ASM {
    private List<Instruccion> Instrucciones; // Lista de instrucciones, esta representara el programa que se lee desde
                                             // el archivo.
    private int contador_Intrucciones = 0;

    private boolean hay_errores = false;
    private String errores = "";

    public Codigo_ASM() {

        Instrucciones = new ArrayList<>(); // Iniciealizar la lista de las instrucciones.

    }

    public int getContador_Intrucciones() {
        return contador_Intrucciones;
    }

    public void agregar_Intruccion(Instruccion pIntruccion) {

        Instrucciones.add(pIntruccion);
        contador_Intrucciones++;

    }

    public List<Instruccion> getInstrucciones() {
        return this.Instrucciones;
    }

    public boolean isHay_errores() {
        return this.hay_errores;
    }

    public void setHay_errores(boolean pHay_errores) {
        this.hay_errores = pHay_errores;
    }

    public String getErrores() {
        return this.errores;
    }

    public void setErrores(String pErrores) {
        this.errores = pErrores;
    }

    @Override
    public String toString() {
        // Se ocupa darle formato.

        StringBuilder cadena_Instrucciones = new StringBuilder("Instrucciones del programa ASM:\n");

        // Recorrer todas las instrucciones de la lista y aplivarles el toString.
        for (Instruccion instruc : Instrucciones) {
            cadena_Instrucciones.append(instruc.toString()).append("\n");

        }
        // System.out.println(cadena_Instrucciones.toString());
        return cadena_Instrucciones.toString();
    }
}
