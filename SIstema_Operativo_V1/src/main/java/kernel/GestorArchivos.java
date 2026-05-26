package kernel;

import model.Instruccion;
import model.Codigo_ASM;
import config.Configuracion;
import util.Validar_Formato_ASM;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

public class GestorArchivos {

    public static Codigo_ASM Cargar_Archivo_ASM(String pRuta) {

        try (BufferedReader contenido = new BufferedReader(new FileReader(pRuta))) {

            Validar_Formato_ASM validar_Formato_ASM = new Validar_Formato_ASM();

            Codigo_ASM codigo = new Codigo_ASM();

            String linea;

            while ((linea = contenido.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) { continue; }

                if (!validar_Formato_ASM.validacion_Completa(linea)) {
                    codigo.setHay_errores(true);
                    codigo.setErrores(validar_Formato_ASM.getUltimoError());
                    return codigo;
                }

                Instruccion instr = new Instruccion(linea);

                codigo.agregar_Intruccion(instr);

            }

            contenido.close();
            return codigo;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Configuracion cargarConfiguracion(String rutaArchivo) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Configuracion config = mapper.readValue(new File(rutaArchivo), Configuracion.class);

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
