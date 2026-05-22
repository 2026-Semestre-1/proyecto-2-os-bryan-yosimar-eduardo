/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.Utils;

import java.util.List;

import Modelo.Codigo_ASM;
import Modelo.Instruccion;

/**
 *
 * @author edurg
 */
public class Calcular_Tiempo_Estimado_Programa {

    public static int calcular_Tiempo_Estimado(Codigo_ASM pCodigo_ASM) {

        // Aquie se tendria que recorrer cada una de las linea del programa y
        // mediante un swithc se optendria el tiempo que dura cada instruccion.
        // vamos sumando todos los pesos y ese es el tiempo estimado de duracion.

        int count = 0;
        List<Instruccion> instrucciones = pCodigo_ASM.getInstrucciones();
        for (Instruccion instruccion : instrucciones) {
            count += obtener_Duracion_Instruccion(instruccion);
        }
        return count;
    }

    public static int obtener_Duracion_Instruccion(Instruccion pInstruccion) {

        if (pInstruccion == null || pInstruccion.get_Intruccion_Completa().isEmpty()) {
            return 0;
        }

        String opcode = pInstruccion.getInstruccion_ASM().toUpperCase();

        switch (opcode) {
            case "LOAD":
                return 2; // Carga al AC
            case "STORE":
                return 2; // Almacena AC
            case "MOV":
                return 1; // Movimiento
            case "ADD":
                return 3; // Suma
            case "SUB":
                return 3; // Resta
            case "INC":
                return 1; // Incremento
            case "DEC":
                return 1; // Decremento
            case "SWAP":
                return 1; // Intercambio
            case "INT":
                // Procesarlo aqui por las instrucciones que no tienen registros.
                String operandos = pInstruccion.getRegistro_Destino().toUpperCase();
                // Diferenciar según tipo

                switch (operandos) {
                    case "20H":
                        return 2; // Finalizar
                    case "10H":
                        return 2; // Imprimir
                    case "09H":
                        return 3; // Entrada teclado
                    case "21H":
                        return 5; // Manejo de archivos
                    default:
                        return 2; // Valor por defecto
                }
            case "JMP":
                return 2; // Salto
            case "CMP":
                return 2; // Comparación
            case "JE":
                return 2; // Salto condicional
            case "JNE":
                return 2; // Salto condicional
            case "PARAM":
                return 3; // Parámetros en pila
            case "PUSH":
                return 1; // Push
            case "POP":
                return 1; // Pop
            default:
                // Instrucción desconocida
                System.out.println("Instrucción desconocida: " + pInstruccion);
                return 1; // Valor por defecto
        }
    }

}
