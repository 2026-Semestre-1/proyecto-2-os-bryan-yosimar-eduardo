/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.Utils;

import java.util.ArrayList;
import java.util.List;

import Modelo.Instruccion;

/**
 *
 * @author edurg
 */
public class Separar_Instrucciones {

    public static List<String> separar_Instrucciones(String pLinea) {

        String linea = pLinea.trim();

        if (linea.isEmpty()) {
            return null;
        }

        linea = linea.replace(",", " ");

        List<String> lista = new ArrayList<>();

        // Expresión regular: captura tokens entre comillas o separados por espacios
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\"[^\"]*\"|\\S+")
                .matcher(linea);

        while (m.find()) {
            String token = m.group();
            // Quitar comillas si quieres guardar solo el contenido
            // if (token.startsWith("\"") && token.endsWith("\"")) {
            // token = token.substring(1, token.length() - 1);
            // }
            lista.add(token);
        }

        mostrar_Separacion(lista);
        return lista;
    }

    public static void mostrar_Separacion(List<String> lista) {

        System.out.println("Instruccion: " + lista.get(0));

        if (lista.size() > 1) {
            System.out.println("Registro Destino: " + lista.get(1));
        }
        if (lista.size() > 2) {
            System.out.println("Operando 1: " + lista.get(2));
        }
        if (lista.size() > 3) {
            System.out.println("Operando 2: " + lista.get(3));
        }
    }

}
