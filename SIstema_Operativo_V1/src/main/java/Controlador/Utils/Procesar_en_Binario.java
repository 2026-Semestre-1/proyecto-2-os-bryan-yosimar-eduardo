/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edurg
 */
public class Procesar_en_Binario {
    
    public static List<String> generar_Binario(String pInstruccion_ASM, String pRegistro_Destino, Integer pValor) {
    
        List<String> binario = new ArrayList<>();
        
        //String instruccion = operador_a_Binario(pInstruccion_ASM);
        
        binario.add(operador_a_Binario(pInstruccion_ASM));
        binario.add(registro_a_Binario(pRegistro_Destino));
        
        if (pValor == null) {
            binario.add("00000000");

        } else {
            binario.add(valor_a_Binario(pValor));
        }
        
        return binario;

    }
    
    public static String operador_a_Binario(String pInstruccion_ASM) {
    
        switch(pInstruccion_ASM.toLowerCase()) {
            case "load":
                return "0001";
                
            case "store":
                return "0010";
                
            case "mov":
                return "0011"; 
                
            case "sub":
                return "0100"; 
                
            case "add":
                return "0101";  
                
            default:// -> En caso de que no se encuentre una coincidencia significa que hay errores en el formato.
                return null;
        }
        
    }
    
    
    public static String registro_a_Binario(String pRegistro_Destino) {

        switch(pRegistro_Destino.toLowerCase()) {
            case "ax":
                return "0001";
                
            case "bx":
                return "0010";
                
            case "cx":
                return "0011"; 
                
            case "dx":
                return "0100";               
                
            default:// -> En caso de que no se encuentre una coincidencia significa que hay errores en el formato.
                return null;
        }        
    
    }
   

    public static String valor_a_Binario(Integer pValor){
        String valor = "";
        
        // Primero revisamos el signo:
        if (pValor < 0) {
            valor = "1";
            pValor *= -1;
        } else {
            valor = "0";
        }
        
        // Despues pasamos el valor a binario:
        String binario = Integer.toBinaryString(pValor);
        
        // Revisar el largo del valor binario, no puede tener mas de 7 elementos.
        
        String binario_rellenado = String.format("%7s", binario).replace(' ', '0');
        
        //System.out.println("Binario: " + binario_rellenado);
        
        return valor + binario_rellenado; // en teoria deberia de unirse, pero no estoy seguro.
        
    }
    
    
    
    
}
