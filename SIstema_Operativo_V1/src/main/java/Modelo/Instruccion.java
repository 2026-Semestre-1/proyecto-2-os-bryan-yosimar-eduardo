/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edurg
 */
public class Instruccion {

    private String Instruccion_ASM; // Instruccion a realizar.

    private String Registro_Destino; // El registro en el que se va a guardar el resultado de la accion.

    private String Operando1; // Valor o registros, dependiendo la instrucción.

    private String Operando2; // Valor o registros, dependiendo la instrucción.

    private String Instruccion_Completa;

    /**
     * Funcion: Constructor de la clase Instruccion.
     * 
     * Descripcion: Constructor para instrucciones completas con sus operandos
     * y el registro destino.
     * 
     * @param pInstruccion_ASM  (String): Instruccion a realizar.
     * @param pRegistro_Destino (String): El registro en el que se va a guardar el
     *                          resultado de la accion.
     * @param pOperando1        (String): Valor o registros, dependiendo la
     *                          instrucción.
     * @param pOperando2        (String): Valor o registros, dependiendo la
     *                          instrucción.
     */
    public Instruccion(String pInstruccion_ASM, String pRegistro_Destino, String pOperando1, String pOperando2) {

        this.Instruccion_ASM = pInstruccion_ASM;
        this.Registro_Destino = pRegistro_Destino;
        this.Operando1 = pOperando1;
        this.Operando2 = pOperando2;

    }

    /**
     * Funcion:Constructor de la clase Instruccion.
     * 
     * Descripcion: Constructor para instrucciones completas.
     * 
     * @param pInstruccion_Completa (String): Instruccion completa a realizar.
     *                              Incluye
     *                              el opcode, registro y operandos.
     */
    public Instruccion(String pInstruccion_Completa) {

        // Separar la intruccion.

        // pInstruccion_Completa.trim();
        pInstruccion_Completa = pInstruccion_Completa.trim().replace(",", "");

        // Usar el mismo regex para separar tokens
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\"[^\"]*\"|\\S+")
                .matcher(pInstruccion_Completa);

        List<String> partes = new ArrayList<>();
        while (m.find()) {
            String token = m.group();
            // if (token.startsWith("\"") && token.endsWith("\"")) {
            // token = token.substring(1, token.length() - 1);
            // }
            partes.add(token);
        }

        this.Instruccion_ASM = partes.size() > 0 ? partes.get(0) : null;
        this.Registro_Destino = partes.size() > 1 ? partes.get(1) : null;
        this.Operando1 = partes.size() > 2 ? partes.get(2) : null;
        this.Operando2 = partes.size() > 3 ? partes.get(3) : null;

        this.Instruccion_Completa = pInstruccion_Completa;
    }

    /**
     * Funcion: getInstruccion_ASM
     * 
     * Descripcion: Obtiene la instruccion ASM.
     * 
     * @return La instruccion ASM.
     */
    public String getInstruccion_ASM() {
        return Instruccion_ASM;
    }

    /**
     * Funcion: setInstruccion_ASM
     * 
     * Descripcion: Establece la instruccion ASM.
     * 
     * @param Instruccion_ASM La instruccion ASM.
     */
    public void setInstruccion_ASM(String Instruccion_ASM) {
        this.Instruccion_ASM = Instruccion_ASM;
    }

    /**
     * Funcion: getRegistro_Destino
     * 
     * Descripcion: Obtiene el registro destino.
     * 
     * @return El registro destino.
     */
    public String getRegistro_Destino() {
        return Registro_Destino;
    }

    /**
     * Funcion: setRegistro_Destino
     * 
     * Descripcion: Establece el registro destino.
     * 
     * @param Registro_Destino El registro destino.
     */
    public void setRegistro_Destino(String Registro_Destino) {
        this.Registro_Destino = Registro_Destino;
    }

    /**
     * Funcion: getOperando1
     * 
     * Descripcion: Obtiene el operando 1.
     * 
     * @return El operando 1.
     */
    public String getOperando1() {
        return Operando1;
    }

    /**
     * Funcion: setOperando1
     * 
     * Descripcion: Establece el operando 1.
     * 
     * @param Operando1 El operando 1.
     */
    public void setOperando1(String Operando1) {
        this.Operando1 = Operando1;
    }

    /**
     * Funcion: getOperando2
     * 
     * Descripcion: Obtiene el operando 2.
     * 
     * @return El operando 2.
     */
    public String getOperando2() {
        return Operando2;
    }

    /**
     * Funcion: setOperando2
     * 
     * Descripcion: Establece el operando 2.
     * 
     * @param Operando2 El operando 2.
     */
    public void setOperando2(String Operando2) {
        this.Operando2 = Operando2;
    }

    /**
     * Funcion: getInstruccion_Completa_Original
     * 
     * Descripcion: Obtiene la instruccion completa original.
     * 
     * @return La instruccion completa original.
     */
    public String getInstruccion_Completa_Original() {
        return Instruccion_Completa;
    }

    /**
     * Funcion: get_Intruccion_Completa
     * 
     * Descripcion: Obtiene la instruccion completa con sus operandos.
     * 
     * @return La instruccion completa con sus operandos.
     */
    public String get_Intruccion_Completa() {
        // System.out.println(toString());
        return this.Instruccion_ASM + " "
                + (this.Registro_Destino != null ? this.Registro_Destino.toString() : " ")
                + " "
                + (this.Operando1 != null ? "," + this.Operando1.toString() : " ")
                + " "
                + (this.Operando2 != null
                        ? "," + this.Operando2.toString()
                        : " ");
    }

    /**
     * Funcion: toString
     * 
     * Descripcion: Convierte la instruccion a una representacion en String.
     * 
     * @return La instruccion en formato String.
     */
    @Override
    public String toString() {
        return "Instruccion: -> " + "Instruccion_ASM = " + Instruccion_ASM + ", Registro_Destino = " + Registro_Destino
                + ", Operando1 = " + Operando1 + ", Operando2 = " + Operando2;
    }

    /**
     * Funcion: mostrar_Instruccion_Completa_Original
     * 
     * Descripcion: Muestra la instruccion completa original.
     */
    public void mostrar_Instruccion_Completa_Original() {
        System.out.println(getInstruccion_Completa_Original());
    }

}
