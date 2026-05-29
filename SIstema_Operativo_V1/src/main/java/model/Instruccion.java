package model;

import java.util.ArrayList;
import java.util.List;

public class Instruccion {

    private String Instruccion_ASM;
    private String Registro_Destino;
    private String Operando1;
    private String Operando2;
    private String Instruccion_Completa;

    public Instruccion(String pInstruccion_ASM, String pRegistro_Destino, String pOperando1, String pOperando2) {
        this.Instruccion_ASM = pInstruccion_ASM;
        this.Registro_Destino = pRegistro_Destino;
        this.Operando1 = pOperando1;
        this.Operando2 = pOperando2;
    }

    public Instruccion(String pInstruccion_Completa) {
        pInstruccion_Completa = pInstruccion_Completa.trim().replace(",", "");
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"[^\"]*\"|\\S+").matcher(pInstruccion_Completa);
        List<String> partes = new ArrayList<>();
        while (m.find()) {
            String token = m.group();
            partes.add(token);
        }
        this.Instruccion_ASM = partes.size() > 0 ? partes.get(0) : null;
        this.Registro_Destino = partes.size() > 1 ? partes.get(1) : null;
        this.Operando1 = partes.size() > 2 ? partes.get(2) : null;
        this.Operando2 = partes.size() > 3 ? partes.get(3) : null;
        this.Instruccion_Completa = pInstruccion_Completa;
    }

    public String getInstruccion_ASM() {
        return Instruccion_ASM;
    }

    public void setInstruccion_ASM(String Instruccion_ASM) {
        this.Instruccion_ASM = Instruccion_ASM;
    }

    public String getRegistro_Destino() {
        return Registro_Destino;
    }

    public void setRegistro_Destino(String Registro_Destino) {
        this.Registro_Destino = Registro_Destino;
    }

    public String getOperando1() {
        return Operando1;
    }

    public void setOperando1(String Operando1) {
        this.Operando1 = Operando1;
    }

    public String getOperando2() {
        return Operando2;
    }

    public void setOperando2(String Operando2) {
        this.Operando2 = Operando2;
    }

    public String getInstruccion_Completa_Original() {
        return Instruccion_Completa;
    }

    public String get_Intruccion_Completa() {
        return this.Instruccion_ASM + " " + (this.Registro_Destino != null ? this.Registro_Destino.toString() : " ")
                + " " + (this.Operando1 != null ? "," + this.Operando1.toString() : " ") + " "
                + (this.Operando2 != null ? "," + this.Operando2.toString() : " ");
    }

    @Override
    public String toString() {
        return "Instruccion: -> " + "Instruccion_ASM = " + Instruccion_ASM + ", Registro_Destino = " + Registro_Destino
                + ", Operando1 = " + Operando1 + ", Operando2 = " + Operando2;
    }

    public void mostrar_Instruccion_Completa_Original() {
        System.out.println(getInstruccion_Completa_Original());
    }
}
