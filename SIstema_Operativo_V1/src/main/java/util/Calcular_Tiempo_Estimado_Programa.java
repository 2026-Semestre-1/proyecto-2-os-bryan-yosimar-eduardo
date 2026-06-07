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
            case "LOAD":
                return 1;
            case "STORE":
                return 1;
            case "MOV":
                return 1;
            case "ADD":
                return 1;
            case "SUB":
                return 1;
            case "INC":
                return 1;
            case "DEC":
                return 1;
            case "SWAP":
                return 1;
            case "INT":
                String operandos = pInstruccion.getRegistro_Destino().toUpperCase();
                switch (operandos) {
                    case "20H":
                        return 1;
                    case "10H":
                        return 1;
                    case "09H":
                        return 1;
                    case "21H":
                        return 1;
                    default:
                        return 1;
                }
            case "JMP":
                return 1;
            case "CMP":
                return 1;
            case "JE":
                return 1;
            case "JNE":
                return 1;
            case "PARAM":
                return 1;
            case "PUSH":
                return 1;
            case "POP":
                return 1;
            default:
                System.out.println("Instruccion desconocida: " + pInstruccion);
                return 1;
        }

    }
}
