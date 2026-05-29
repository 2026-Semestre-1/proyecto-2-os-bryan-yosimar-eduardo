package model;

import java.util.ArrayList;
import java.util.List;

public class Codigo_ASM {
    private List<Instruccion> Instrucciones;
    private int contador_Intrucciones = 0;
    private boolean hay_errores = false;
    private String errores = "";

    public Codigo_ASM() { Instrucciones = new ArrayList<>(); }

    public int getContador_Intrucciones() { return contador_Intrucciones; }
    public void agregar_Intruccion(Instruccion pIntruccion) { Instrucciones.add(pIntruccion); contador_Intrucciones++; }
    public List<Instruccion> getInstrucciones() { return this.Instrucciones; }
    public boolean isHay_errores() { return this.hay_errores; }
    public void setHay_errores(boolean pHay_errores) { this.hay_errores = pHay_errores; }
    public String getErrores() { return this.errores; }
    public void setErrores(String pErrores) { this.errores = pErrores; }

    @Override
    public String toString() {
        StringBuilder cadena_Instrucciones = new StringBuilder("Instrucciones del programa ASM:\n");
        for (Instruccion instruc : Instrucciones) { cadena_Instrucciones.append(instruc.toString()).append("\n"); }
        return cadena_Instrucciones.toString();
    }
}
