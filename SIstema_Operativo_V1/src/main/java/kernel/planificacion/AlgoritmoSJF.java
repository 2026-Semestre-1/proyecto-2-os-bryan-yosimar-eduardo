package kernel.planificacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kernel.GestorMemoria;
import kernel.Planificador;
import model.Almacenamiento;
import model.BCP;
import model.Codigo_ASM;
import model.Memoria;

public class AlgoritmoSJF implements IAlgoritmoPlanificacion {

    @Override
    public void cargarLote(Planificador ctx, Memoria memoria, Almacenamiento almacenamiento,
            GestorMemoria controlador, int tiempoActualCPU) {
        // Admitir procesos hasta llenar el límite (5) prefiriendo los programas de
        // menor duración
        while (ctx.getCola_Procesos_Nuevos().size() < ctx.getMaxProcesosSimultaneos()) {
            if (ctx.getCola_Programas_Pendientes().isEmpty())
                break;

            // Orden temporal de pendientes por duración estimada (si disponible)
            // List<String> pendientes = new
            // ArrayList<>(ctx.getCola_Programas_Pendientes());
            // pendientes.sort((a, b) -> {
            // int da = ctx.obtenerDuracionProgramaEnAlmacenamiento(almacenamiento, a);
            // int db = ctx.obtenerDuracionProgramaEnAlmacenamiento(almacenamiento, b);
            // return Integer.compare(da, db);
            // });
            String nombre = ctx.getCola_Programas_Pendientes().get(0);
            String nombrePrograma = nombre;
            Codigo_ASM codigo = ctx.obtener_Programa_Almacenamiento(almacenamiento, nombrePrograma);
            ctx.getCola_Programas_Pendientes().remove(0);
            String nombreInstancia = ctx.getSiguienteNombreInstancia(nombrePrograma);
            boolean creado = ctx.crearProcesoEnMemoria(nombreInstancia, codigo, memoria, controlador, tiempoActualCPU);
            if (!creado) {
                ctx.getCola_Programas_Pendientes().add(0, nombrePrograma);
                break;
            }
            // eliminar de pendientes lo hace crearProcesoEnMemoria o Planificador según
            // diseño
        }
    }

    @Override
    public int seleccionarSiguiente(Planificador ctx) {

        // Este algoritmo elige al que tenga la duracion mas corta de toda la cola.
        // Aunque llegue un proceso nuevo, este algortimo no suelta el que tiene hasta
        // que lo termine.
        // Cuando ya termina el proceso en ejecucion, entonces se mira cual es el
        // siguiente mas corto.

        int mejorPID = -1;

        // Establecemos una duraccion muy grande para comparrla con las demas.
        int mejorDuracion = Integer.MAX_VALUE;
        int mejorLlegada = Integer.MAX_VALUE;

        // Recorre la cola de procesos nuevos (LinkedHashMap mantiene orden de
        // inserción)
        for (Map.Entry<Integer, BCP> entry : ctx.getCola_Procesos_Nuevos().entrySet()) {
            BCP bcp = entry.getValue();
            String estado = bcp.getEstado();
            if (!"Preparado".equals(estado))
                continue;

            int dur = parseStringToInt(bcp.getTiempo_Ejecucion());
            int llegada = parseStringToInt(bcp.getTiempo_Llegada());

            if (dur < mejorDuracion || (dur == mejorDuracion && llegada < mejorLlegada)) {
                mejorDuracion = dur;
                mejorLlegada = llegada;
                mejorPID = bcp.getPID();
            }
        }
        return mejorPID;
    }

    @Override
    public String getNombre() {
        return "SJF";
    }

    /**
     * Nombre: parseStringToInt
     * 
     * Descripcion: Esta funcion es un parser seguro para convertir a un valor
     * entero un string.
     * 
     * @param s (String): El string que se desea convertir a un valor entero.
     * @return (Integer): El valor entero del string.
     */
    private Integer parseStringToInt(String s) {
        try {
            if (s == null || s.isEmpty())
                return Integer.MAX_VALUE;
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return Integer.MAX_VALUE;
        }
    }

}
