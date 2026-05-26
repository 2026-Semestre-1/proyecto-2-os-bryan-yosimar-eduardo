package util;

import java.util.ArrayList;
import java.util.List;

public class Procesar_en_Binario {
    
    public static List<String> generar_Binario(String pInstruccion_ASM, String pRegistro_Destino, Integer pValor) {
        List<String> binario = new ArrayList<>();
        binario.add(operador_a_Binario(pInstruccion_ASM));
        binario.add(registro_a_Binario(pRegistro_Destino));
        if (pValor == null) { binario.add("00000000"); }
        else { binario.add(valor_a_Binario(pValor)); }
        return binario;
    }
    
    public static String operador_a_Binario(String pInstruccion_ASM) {
        switch(pInstruccion_ASM.toLowerCase()) {
            case "load": return "0001";
            case "store": return "0010";
            case "mov": return "0011"; 
            case "sub": return "0100"; 
            case "add": return "0101";  
            default: return null;
        }
    }
    
    public static String registro_a_Binario(String pRegistro_Destino) {
        switch(pRegistro_Destino.toLowerCase()) {
            case "ax": return "0001";
            case "bx": return "0010";
            case "cx": return "0011"; 
            case "dx": return "0100";               
            default: return null;
        }        
    }

    public static String valor_a_Binario(Integer pValor){
        String valor = "";
        if (pValor < 0) { valor = "1"; pValor *= -1; }
        else { valor = "0"; }
        String binario = Integer.toBinaryString(pValor);
        String binario_rellenado = String.format("%7s", binario).replace(' ', '0');
        return valor + binario_rellenado;
    }
}
