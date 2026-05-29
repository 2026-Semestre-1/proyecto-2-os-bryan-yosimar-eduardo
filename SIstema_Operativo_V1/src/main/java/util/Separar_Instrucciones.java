package util;

import java.util.ArrayList;
import java.util.List;

import model.Instruccion;

public class Separar_Instrucciones {

    public static List<String> separar_Instrucciones(String pLinea) {

        String linea = pLinea.trim();

        System.out.println("[DEBUG PARSER] Intentando separar la instrucción: '" + linea + "'");

        if (linea.isEmpty()) {
            System.out.println("[DEBUG PARSER] Línea vacía — ¿se omitió correctamente en la carga?");
            return null;
        }

        if (linea.startsWith(";")) {
            System.out.println("[DEBUG PARSER] Línea identificada como comentario — ¿se omitió correctamente?");
            return null;
        }

        linea = linea.replace(",", " ");

        List<String> lista = new ArrayList<>();

        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\"[^\"]*\"|\\S+")
                .matcher(linea);

        while (m.find()) {
            String token = m.group();
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
