package util;

import java.util.*;

public class Validar_Formato_ASM {

    private final Set<String> instruccionesPermitidas = Set.of(
            "MOV", "LOAD", "STORE", "ADD", "SUB", "INC", "DEC", "SWAP",
            "INT", "JMP", "CMP", "JE", "JNE", "PARAM", "PUSH", "POP");

    private final Set<String> registrosNumericos = Set.of("AX", "BX", "CX");
    private final Set<String> registrosAlfanumericos = Set.of("DX", "AH", "AL");
    private final Set<String> registrosTodos = new HashSet<>();
    private final Set<String> registrosProhibidosDestino = Set.of("AC");

    private String ultimoError = "";

    public Validar_Formato_ASM() {
        registrosTodos.addAll(registrosNumericos);
        registrosTodos.addAll(registrosAlfanumericos);
        registrosTodos.add("AC");
    }

    public String getUltimoError() {
        return ultimoError;
    }

    public boolean validacion_Completa(String linea) {
        ultimoError = "";

        if (linea == null) {
            ultimoError = "L\u00ednea nula.";
            return false;
        }

        String l = linea.trim();
        if (l.isEmpty()) {
            ultimoError = "L\u00ednea vac\u00eda.";
            return false;
        }

        List<String> elementos = Separar_Instrucciones.separar_Instrucciones(linea);

        String opcode = elementos.get(0);
        elementos.remove(0);

        switch (opcode.toUpperCase()) {
            case "MOV": return validarMOV(elementos);
            case "LOAD": return validarLOAD(elementos);
            case "STORE": return validarSTORE(elementos);
            case "ADD": case "SUB": return validarADD_SUB(elementos, opcode);
            case "INC": case "DEC": return validarINC_DEC(elementos);
            case "SWAP": return validarSWAP(elementos);
            case "INT": return validarINT(elementos);
            case "JMP": case "JE": case "JNE": return validarJMPSaltos(elementos, opcode);
            case "CMP": return validarCMP(elementos);
            case "PARAM": return validarPARAM(elementos);
            case "PUSH": return validarPUSH(elementos);
            case "POP": return validarPOP(elementos);
            default:
                ultimoError = "Instrucci\u00f3n no implementada en validador: " + opcode;
                return false;
        }
    }

    private boolean validarMOV(List<String> elems) {
        if (elems.size() != 2) { ultimoError = "MOV requiere 2 operandos: MOV dest, src"; return false; }
        String dest = elems.get(0).toUpperCase();
        String src = elems.get(1).toUpperCase();
        if (isRegister(dest)) { if (registrosProhibidosDestino.contains(dest)) { ultimoError = "MOV: no se permite usar " + dest + " como destino directo."; return false; } }
        else { if (!validarNumeroRango(dest)) { ultimoError = "MOV: destino no es registro ni direcci\u00f3n v\u00e1lida: " + dest; return false; } }
        if (isRegister(src)) { return true; }
        else { if (dest.contains("AH") || dest.contains("AL") || dest.contains("DX")) { return true; }
            if (!validarNumeroRango(src)) { ultimoError = "MOV: fuente inv\u00e1lida (ni registro ni n\u00famero): " + src; return false; } }
        return true;
    }

    private boolean validarLOAD(List<String> elems) {
        if (elems.size() != 1) { ultimoError = "LOAD requiere 1 operando: LOAD reg|direccion"; return false; }
        String op = elems.get(0).toUpperCase();
        if (op.equals("AC")) { ultimoError = "LOAD no puede usar AC como operando directo."; return false; }
        if (isRegister(op)) { return true; }
        else { if (!validarNumeroRango(op)) { ultimoError = "LOAD: operando no es registro ni direcci\u00f3n v\u00e1lida: " + op; return false; } }
        return true;
    }

    private boolean validarSTORE(List<String> elems) {
        if (elems.size() != 1) { ultimoError = "STORE requiere 1 operando: STORE reg|direccion"; return false; }
        String op = elems.get(0).toUpperCase();
        if (op.equals("AC")) { ultimoError = "STORE no puede usar AC como destino directo."; return false; }
        if (isRegister(op)) { return true; }
        else { if (!validarNumeroRango(op)) { ultimoError = "STORE: operando no es registro ni direcci\u00f3n v\u00e1lida: " + op; return false; } }
        return true;
    }

    private boolean validarADD_SUB(List<String> elems, String opcode) {
        if (elems.size() != 1) { ultimoError = opcode + " requiere 1 operando: " + opcode + " reg|valor"; return false; }
        String op = elems.get(0).toUpperCase();
        if (isRegister(op)) { if (registrosAlfanumericos.contains(op) && !op.equals("DX")) { ultimoError = opcode + ": registro " + op + " no v\u00e1lido para operaci\u00f3n aritm\u00e9tica"; return false; } return true; }
        else { if (!validarNumeroRango(op)) { ultimoError = opcode + ": operando no es n\u00famero v\u00e1lido: " + op; return false; } }
        return true;
    }

    private boolean validarINC_DEC(List<String> elems) {
        if (elems.size() == 0) return true;
        if (elems.size() == 1) { String op = elems.get(0).toUpperCase(); if (!isRegister(op)) { ultimoError = "INC/DEC: operando debe ser registro si se especifica"; return false; } return true; }
        ultimoError = "INC/DEC acepta 0 o 1 operando"; return false;
    }

    private boolean validarSWAP(List<String> elems) {
        if (elems.size() != 2) { ultimoError = "SWAP requiere 2 registros: SWAP r1, r2"; return false; }
        String r1 = elems.get(0).toUpperCase(); String r2 = elems.get(1).toUpperCase();
        if (!isRegister(r1) || !isRegister(r2)) { ultimoError = "SWAP: ambos operandos deben ser registros v\u00e1lidos"; return false; }
        if (r1.equals(r2)) { ultimoError = "SWAP: registros deben ser distintos"; return false; }
        return true;
    }

    private boolean validarINT(List<String> elems) {
        if (elems.size() != 1) { ultimoError = "INT requiere 1 operando: INT 20H|10H|09H|21H"; return false; }
        String op = elems.get(0).toUpperCase();
        Set<String> validos = Set.of("20H", "10H", "09H", "21H");
        if (!validos.contains(op)) { ultimoError = "INT inv\u00e1lida: " + op; return false; }
        return true;
    }

    private boolean validarJMPSaltos(List<String> elems, String opcode) {
        if (elems.size() != 1) { ultimoError = opcode + " requiere 1 operando: offset (+N/-N) o direcci\u00f3n"; return false; }
        String op = elems.get(0).trim();
        if (op.startsWith("+") || op.startsWith("-")) { try { Integer.parseInt(op); return true; } catch (NumberFormatException e) { ultimoError = opcode + ": offset inv\u00e1lido: " + op; return false; } }
        else { if (!validarNumeroEntero(op)) { ultimoError = opcode + ": direcci\u00f3n inv\u00e1lida: " + op; return false; } return true; }
    }

    private boolean validarCMP(List<String> elems) {
        if (elems.size() != 2) { ultimoError = "CMP requiere 2 operandos: CMP reg1, reg2"; return false; }
        String a = elems.get(0).toUpperCase(); String b = elems.get(1).toUpperCase();
        if (!isRegister(a) && !validarNumeroEntero(a)) { ultimoError = "CMP: operando inv\u00e1lido: " + a; return false; }
        if (!isRegister(b) && !validarNumeroEntero(b)) { ultimoError = "CMP: operando inv\u00e1lido: " + b; return false; }
        return true;
    }

    private boolean validarPARAM(List<String> elems) {
        if (elems.size() == 0) { ultimoError = "PARAM requiere al menos 1 valor"; return false; }
        if (elems.size() > 3) { ultimoError = "PARAM admite m\u00e1ximo 3 par\u00e1metros"; return false; }
        for (String v : elems) { if (!validarNumeroRango(v)) { ultimoError = "PARAM: valor inv\u00e1lido: " + v; return false; } }
        return true;
    }

    private boolean validarPUSH(List<String> elems) {
        if (elems.size() != 1) { ultimoError = "PUSH requiere 1 operando: registro o valor"; return false; }
        String op = elems.get(0).toUpperCase();
        if (isRegister(op)) return true;
        if (!validarNumeroRango(op)) { ultimoError = "PUSH: operando inv\u00e1lido: " + op; return false; }
        return true;
    }

    private boolean validarPOP(List<String> elems) {
        if (elems.size() != 1) { ultimoError = "POP requiere 1 operando: registro destino"; return false; }
        String op = elems.get(0).toUpperCase();
        if (!isRegister(op)) { ultimoError = "POP: destino debe ser un registro v\u00e1lido"; return false; }
        if (op.equals("AC")) { ultimoError = "POP no puede escribir directamente en AC"; return false; }
        return true;
    }

    private boolean isRegister(String token) { if (token == null) return false; return registrosTodos.contains(token.toUpperCase()); }
    private boolean validarNumeroEntero(String token) { if (token == null) return false; try { Integer.parseInt(token.trim()); return true; } catch (NumberFormatException e) { return false; } }
    private boolean validarNumeroRango(String token) { if (!validarNumeroEntero(token)) return false; try { int v = Integer.parseInt(token.trim()); if (v < -255 || v > 255) { return false; } return true; } catch (NumberFormatException e) { return false; } }
}
