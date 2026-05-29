package util;

import java.util.List;

import model.Codigo_ASM;
import model.Instruccion;

public class Calcular_Tiempo_Estimado_Programa {

    public static int calcular_Tiempo_Estimado(Codigo_ASM pCodigo_ASM) {
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
            case "LOAD": return 2;
            case "STORE": return 2;
            case "MOV": return 1;
            case "ADD": return 3;
            case "SUB": return 3;
            case "INC": return 1;
            case "DEC": return 1;
            case "SWAP": return 1;
            case "INT":
                String operandos = pInstruccion.getRegistro_Destino().toUpperCase();
                switch (operandos) {
                    case "20H": return 2;
                    case "10H": return 2;
                    case "09H": return 3;
                    case "21H": return 5;
                    default: return 2;
                }
            case "JMP": return 2;
            case "CMP": return 2;
            case "JE": return 2;
            case "JNE": return 2;
            case "PARAM": return 3;
            case "PUSH": return 1;
            case "POP": return 1;
            default:
                System.out.println("Instrucci\u00f3n desconocida: " + pInstruccion);
                return 1;
        }
    }
}
