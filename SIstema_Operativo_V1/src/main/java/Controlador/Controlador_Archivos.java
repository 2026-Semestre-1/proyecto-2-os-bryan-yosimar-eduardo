/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Instruccion;
import Modelo.Codigo_ASM;
import Modelo.Configuracion;
import Controlador.Utils.Validar_Formato_ASM;

// Imports de librerias.
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

/**
 *
 * @author edurg
 */
public class Controlador_Archivos {

    /**
     * Funcion: leer_Archivo
     * Lee el archvios ASM a partir de una ruta, ademas carga y valida el contenido
     * de dichos archivos.
     * 
     * @param pRuta Ruta del archivo.
     * @return Un objeto Codigo_ASM con el contenido del archivo.
     */
    public static Codigo_ASM Cargar_Archivo_ASM(String pRuta) {

        // Intentamos abrir el archivo.
        try (BufferedReader contenido = new BufferedReader(new FileReader(pRuta))) {

            // System.out.println("Si se leyo el archivo.");
            Validar_Formato_ASM validar_Formato_ASM = new Validar_Formato_ASM();

            // Crear la instancia que va a contener el codigo leido y lo va a devolver.
            Codigo_ASM codigo = new Codigo_ASM();

            String linea;

            // Recorrer cada linea para procesarla.
            while ((linea = contenido.readLine()) != null) {
                // System.out.println("Linea: " + linea);
                linea = linea.trim(); // Como de momento los estamos separando por los espacios en blanco, estonces
                                      // eliminamos los del inicio y los del final.
                // String original = linea;
                if (linea.isEmpty()) {
                    continue;

                }

                if (!validar_Formato_ASM.validacion_Completa(linea)) {
                    codigo.setHay_errores(true);
                    codigo.setErrores(validar_Formato_ASM.getUltimoError());
                    return codigo;
                }

                Instruccion instr = new Instruccion(linea);

                codigo.agregar_Intruccion(instr);

            }

            contenido.close(); // Cerrar eso del buffer.
            return codigo; // Devolver el contenido.

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Funcion: Cargar_Configuracion_Json
     * Carga la configuracion basica del programa desde un archivo json
     * 
     * @param pRuta Ruta del archivo JSON.
     * @return Una lista de enteros con la configuracion.
     */
    public static Configuracion cargarConfiguracion(String rutaArchivo) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Configuracion config = mapper.readValue(new File(rutaArchivo), Configuracion.class);

            // Validaciones
            if (config.getMemoria() < 512 || config.getAlmacenamiento() < 512 || config.getMemoria_Virtual() < 64
                    || config.getCant_CPU() < 1) {
                System.out.println("Valores no permitidos en el archivo de configuracion.");
                return null;

            }

            return config;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
