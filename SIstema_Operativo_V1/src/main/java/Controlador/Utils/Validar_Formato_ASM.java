/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.Utils;

import java.util.*;

/**
 *
 * @author edurg
 */

public class Validar_Formato_ASM {

    private final Set<String> instruccionesPermitidas = Set.of(
            "MOV", "LOAD", "STORE", "ADD", "SUB", "INC", "DEC", "SWAP",
            "INT", "JMP", "CMP", "JE", "JNE", "PARAM", "PUSH", "POP");

    private final Set<String> registrosNumericos = Set.of("AX", "BX", "CX");
    private final Set<String> registrosAlfanumericos = Set.of("DX", "AH", "AL");
    private final Set<String> registrosTodos = new HashSet<>();
    private final Set<String> registrosProhibidosDestino = Set.of("AC"); // AC no puede ser destino directo

    private String ultimoError = "";

    public Validar_Formato_ASM() {
        registrosTodos.addAll(registrosNumericos);
        registrosTodos.addAll(registrosAlfanumericos);
        registrosTodos.add("AC");
        // AH y AL se consideran registros que aceptan alfanumérico
    }

    public String getUltimoError() {
        return ultimoError;
    }

    /**
     * Valida una línea completa de ASM.
     * Devuelve true si es válida, false si no.
     */
    public boolean validacion_Completa(String linea) {
        ultimoError = "";

        if (linea == null) {
            ultimoError = "Línea nula.";
            return false;
        }

        String l = linea.trim();
        if (l.isEmpty()) {
            ultimoError = "Línea vacía.";
            return false;
        }

        List<String> elementos = Separar_Instrucciones.separar_Instrucciones(linea);

        // Separar opcode y operandos
        String opcode = elementos.get(0);
        elementos.remove(0);

        // Despachar validación por instrucción
        switch (opcode.toUpperCase()) {
            case "MOV":
                return validarMOV(elementos);
            case "LOAD":
                return validarLOAD(elementos);
            case "STORE":
                return validarSTORE(elementos);
            case "ADD":
            case "SUB":
                return validarADD_SUB(elementos, opcode);
            case "INC":
            case "DEC":
                return validarINC_DEC(elementos);
            case "SWAP":
                return validarSWAP(elementos);
            case "INT":
                return validarINT(elementos);
            case "JMP":
            case "JE":
            case "JNE":
                return validarJMPSaltos(elementos, opcode);
            case "CMP":
                return validarCMP(elementos);
            case "PARAM":
                return validarPARAM(elementos);
            case "PUSH":
                return validarPUSH(elementos);
            case "POP":
                return validarPOP(elementos);
            default:
                ultimoError = "Instrucción no implementada en validador: " + opcode;
                return false;
        }
    }

    // -------------------------
    // Validadores por instrucción
    // -------------------------

    private boolean validarMOV(List<String> elems) {
        if (elems.size() != 2) {
            ultimoError = "MOV requiere 2 operandos: MOV dest, src";
            return false;
        }
        String dest = elems.get(0).toUpperCase();
        String src = elems.get(1).toUpperCase();

        // dest puede ser registro (excepto AC) o dirección numérica
        if (isRegister(dest)) {
            if (registrosProhibidosDestino.contains(dest)) {
                ultimoError = "MOV: no se permite usar " + dest + " como destino directo.";
                return false;
            }
        } else {
            // Si es un valor numerico, tampoco se puede, solo
            if (!validarNumeroRango(dest)) {
                ultimoError = "MOV: destino no es registro ni dirección válida: " + dest;
                return false;
            }
        }

        // src puede ser registro o número
        if (isRegister(src)) {
            // si src es registro numérico, ok; si es alfanumérico (DX/AH/AL) también ok
            return true;
        } else {

            if (dest.contains("AH") || dest.contains("AL") || dest.contains("DX")) {
                // A estos se puede agregar un valor que sea un string.
                return true;
            }

            if (!validarNumeroRango(src)) {
                ultimoError = "MOV: fuente inválida (ni registro ni número): " + src;
                return false;
            }
        }
        return true;
    }

    private boolean validarLOAD(List<String> elems) {
        // LOAD tiene 1 operando: registro o dirección. No permitir LOAD AC
        if (elems.size() != 1) {
            ultimoError = "LOAD requiere 1 operando: LOAD reg|direccion";
            return false;
        }
        String op = elems.get(0).toUpperCase();
        if (op.equals("AC")) {
            ultimoError = "LOAD no puede usar AC como operando directo.";
            return false;
        }
        if (isRegister(op)) {
            // si es registro, debe ser uno de los permitidos (AX,BX,CX,DX,AH,AL)
            return true;
        } else {
            // dirección numérica
            if (!validarNumeroRango(op)) {
                ultimoError = "LOAD: operando no es registro ni dirección válida: " + op;
                return false;
            }
        }
        return true;
    }

    private boolean validarSTORE(List<String> elems) {
        // STORE tiene 1 operando: registro o dirección. No permitir STORE AC como
        // destino.
        if (elems.size() != 1) {
            ultimoError = "STORE requiere 1 operando: STORE reg|direccion";
            return false;
        }
        String op = elems.get(0).toUpperCase();
        if (op.equals("AC")) {
            ultimoError = "STORE no puede usar AC como destino directo.";
            return false;
        }
        if (isRegister(op)) {
            return true;
        } else {
            if (!validarNumeroRango(op)) {
                ultimoError = "STORE: operando no es registro ni dirección válida: " + op;
                return false;
            }
        }
        return true;
    }

    private boolean validarADD_SUB(List<String> elems, String opcode) {
        // ADD/SUB requieren 1 operando: registro o número
        if (elems.size() != 1) {
            ultimoError = opcode + " requiere 1 operando: " + opcode + " reg|valor";
            return false;
        }
        String op = elems.get(0).toUpperCase();
        if (isRegister(op)) {
            // AX/BX/CX permitidos; DX/AH/AL no deberían usarse para operaciones aritméticas
            // en este diseño
            if (registrosAlfanumericos.contains(op) && !op.equals("DX")) { // El DX deberia de poser usarse para sumar.
                ultimoError = opcode + ": registro " + op + " no válido para operación aritmética";
                return false;
            }
            return true;
        } else {
            if (!validarNumeroRango(op)) {
                ultimoError = opcode + ": operando no es número válido: " + op;
                return false;
            }
        }
        return true;
    }

    private boolean validarINC_DEC(List<String> elems) {
        // INC o DEC sin operando (afecta AC) o con 1 registro
        if (elems.size() == 0)
            return true;
        if (elems.size() == 1) {
            String op = elems.get(0).toUpperCase();
            if (!isRegister(op)) {
                ultimoError = "INC/DEC: operando debe ser registro si se especifica";
                return false;
            }
            // permitir cualquier registro excepto AC? En tu diseño INC sin operando afecta
            // AC, con registro afecta registro
            return true;
        }
        ultimoError = "INC/DEC acepta 0 o 1 operando";
        return false;
    }

    private boolean validarSWAP(List<String> elems) {
        if (elems.size() != 2) {
            ultimoError = "SWAP requiere 2 registros: SWAP r1, r2";
            return false;
        }
        String r1 = elems.get(0).toUpperCase();
        String r2 = elems.get(1).toUpperCase();
        if (!isRegister(r1) || !isRegister(r2)) {
            ultimoError = "SWAP: ambos operandos deben ser registros válidos";
            return false;
        }
        if (r1.equals(r2)) {
            ultimoError = "SWAP: registros deben ser distintos";
            return false;
        }
        return true;
    }

    private boolean validarINT(List<String> elems) {
        if (elems.size() != 1) {
            ultimoError = "INT requiere 1 operando: INT 20H|10H|09H|21H";
            return false;
        }
        String op = elems.get(0).toUpperCase();
        // Aceptar formatos como 20H, 10H, 09H, 21H
        Set<String> validos = Set.of("20H", "10H", "09H", "21H");
        if (!validos.contains(op)) {
            ultimoError = "INT inválida: " + op;
            return false;
        }
        return true;
    }

    private boolean validarJMPSaltos(List<String> elems, String opcode) {
        if (elems.size() != 1) {
            ultimoError = opcode + " requiere 1 operando: offset (+N/-N) o dirección";
            return false;
        }
        String op = elems.get(0).trim();
        // offset puede empezar con + o -
        if (op.startsWith("+") || op.startsWith("-")) {
            try {
                Integer.parseInt(op);
                return true;
            } catch (NumberFormatException e) {
                ultimoError = opcode + ": offset inválido: " + op;
                return false;
            }
        } else {
            // dirección absoluta: número
            if (!validarNumeroEntero(op)) {
                ultimoError = opcode + ": dirección inválida: " + op;
                return false;
            }
            return true;
        }
    }

    private boolean validarCMP(List<String> elems) {
        if (elems.size() != 2) {
            ultimoError = "CMP requiere 2 operandos: CMP reg1, reg2";
            return false;
        }
        String a = elems.get(0).toUpperCase();
        String b = elems.get(1).toUpperCase();
        // cada operando puede ser registro o número
        if (!isRegister(a) && !validarNumeroEntero(a)) {
            ultimoError = "CMP: operando inválido: " + a;
            return false;
        }
        if (!isRegister(b) && !validarNumeroEntero(b)) {
            ultimoError = "CMP: operando inválido: " + b;
            return false;
        }
        return true;
    }

    private boolean validarPARAM(List<String> elems) {
        if (elems.size() == 0) {
            ultimoError = "PARAM requiere al menos 1 valor";
            return false;
        }
        if (elems.size() > 3) {
            ultimoError = "PARAM admite máximo 3 parámetros";
            return false;
        }
        for (String v : elems) {
            if (!validarNumeroRango(v)) {
                ultimoError = "PARAM: valor inválido: " + v;
                return false;
            }
        }
        return true;
    }

    private boolean validarPUSH(List<String> elems) {
        if (elems.size() != 1) {
            ultimoError = "PUSH requiere 1 operando: registro o valor";
            return false;
        }
        String op = elems.get(0).toUpperCase();
        if (isRegister(op))
            return true;
        if (!validarNumeroRango(op)) {
            ultimoError = "PUSH: operando inválido: " + op;
            return false;
        }
        return true;
    }

    private boolean validarPOP(List<String> elems) {
        if (elems.size() != 1) {
            ultimoError = "POP requiere 1 operando: registro destino";
            return false;
        }
        String op = elems.get(0).toUpperCase();
        if (!isRegister(op)) {
            ultimoError = "POP: destino debe ser un registro válido";
            return false;
        }
        if (op.equals("AC")) {
            ultimoError = "POP no puede escribir directamente en AC";
            return false;
        }
        return true;
    }

    // -------------------------
    // Helpers generales
    // -------------------------

    private boolean isRegister(String token) {
        if (token == null)
            return false;
        return registrosTodos.contains(token.toUpperCase());
    }

    private boolean validarNumeroEntero(String token) {
        if (token == null)
            return false;
        try {
            Integer.parseInt(token.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validarNumeroRango(String token) {
        if (!validarNumeroEntero(token))
            return false;
        try {
            int v = Integer.parseInt(token.trim());
            if (v < -255 || v > 255) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
