package Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

public class ConfigKernel {

    private String algoritmoPlanificacion;
    private int tiempoQuantum;

    public String getAlgoritmoPlanificacion() {
        return algoritmoPlanificacion;
    }

    public void setAlgoritmoPlanificacion(String algoritmoPlanificacion) {
        this.algoritmoPlanificacion = algoritmoPlanificacion;
    }

    public int getTiempoQuantum() {
        return tiempoQuantum;
    }

    public void setTiempoQuantum(int tiempoQuantum) {
        this.tiempoQuantum = tiempoQuantum;
    }

    public static ConfigKernel cargarConfiguracion() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = ConfigKernel.class.getClassLoader()
                    .getResourceAsStream("Config/Config_Kernel.json");
            if (is == null) {
                System.out.println("No se encontro Config_Kernel.json en el classpath.");
                return new ConfigKernel();
            }
            return mapper.readValue(is, ConfigKernel.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new ConfigKernel();
        }
    }

    @Override
    public String toString() {
        return "ConfigKernel [algoritmoPlanificacion=" + algoritmoPlanificacion
                + ", tiempoQuantum=" + tiempoQuantum + "]";
    }
}
