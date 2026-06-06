package kernel;

import model.Instruccion;
import model.Codigo_ASM;
import util.Validar_Formato_ASM;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;

import com.fasterxml.jackson.databind.ObjectMapper;

import Config.Configuracion;
import Config.ConfigParticion;
import Config.ConfigPaginacion;

import java.io.InputStream;

public class GestorArchivos {

    public static Codigo_ASM Cargar_Archivo_ASM(String pRuta) {

        try (BufferedReader contenido = new BufferedReader(new FileReader(pRuta))) {

            Validar_Formato_ASM validar_Formato_ASM = new Validar_Formato_ASM();

            Codigo_ASM codigo = new Codigo_ASM();

            String linea;

            while ((linea = contenido.readLine()) != null) {
                linea = linea.trim();
                System.out.println("[DEBUG CARGA] Línea leída: '" + linea + "'");
                if (linea.isEmpty() || linea.startsWith(";")) {
                    System.out.println("[DEBUG CARGA] Línea vacía o comentario — omitida");
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

            contenido.close();
            return codigo;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Configuracion cargarConfiguracion() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = GestorArchivos.class.getClassLoader()
                    .getResourceAsStream("Config/Config_Mem.json");
            if (is == null) {
                System.out.println("No se encontro el archivo de configuracion en el classpath.");
                return null;
            }
            Configuracion config = mapper.readValue(is, Configuracion.class);

            if (config.getMemoria() < 1 || config.getAlmacenamiento() < 512 || config.getMemoria_Virtual() < 64
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

    public static ConfigParticion cargarConfigParticion() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = GestorArchivos.class.getClassLoader()
                    .getResourceAsStream("Config/Config_Particion.json");
            if (is == null) {
                System.out.println("No se encontro Config_Particion.json en el classpath.");
                return null;
            }
            ConfigParticion config = mapper.readValue(is, ConfigParticion.class);

            int suma = config.getDinamica().stream().mapToInt(Integer::intValue).sum();
            if (suma != 100) {
                System.out.println("Error: Los porcentajes de 'dinamica' suman " + suma + "%, debe ser 100%.");
                return null;
            }

            return config;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ConfigPaginacion cargarConfigPaginacion() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = GestorArchivos.class.getClassLoader()
                    .getResourceAsStream("Config/Config_Paginacion.json");
            if (is == null) {
                System.out.println("No se encontro Config_Paginacion.json en el classpath.");
                return null;
            }
            ConfigPaginacion config = mapper.readValue(is, ConfigPaginacion.class);
            return config;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
