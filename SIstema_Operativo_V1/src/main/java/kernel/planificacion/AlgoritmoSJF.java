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
import util.Parser_String_To_Int;

public class AlgoritmoSJF implements IAlgoritmoPlanificacion {

    @Override
    public void cargarLote(Planificador ctx, Memoria memoria, Almacenamiento almacenamiento,
            GestorMemoria controlador, int tiempoActualCPU) {
        // Admitir procesos hasta llenar el límite (5) prefiriendo los programas de
        // menor duración
        while (ctx.getCola_Procesos_Nuevos().size() < 5) {
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
            String nombrePrograma = nombre;// pendientes.get(0);
            // Intentar crear proceso en memoria (Planificador maneja indices y BCP)
            Codigo_ASM codigo = ctx.obtener_Programa_Almacenamiento(almacenamiento, nombrePrograma);
            boolean creado = ctx.crearProcesoEnMemoria(nombrePrograma, codigo, memoria, controlador, tiempoActualCPU);
            if (!creado) {
                break; // no hay espacio, salir
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

        int bestPid = -1;

        // Establecemos una duraccion muy grande para comparrla con las demas.
        int bestDur = Integer.MAX_VALUE;
        int bestLlegada = Integer.MAX_VALUE;

        // Recorre la cola de procesos nuevos (LinkedHashMap mantiene orden de
        // inserción)
        for (Map.Entry<Integer, BCP> entry : ctx.getCola_Procesos_Nuevos().entrySet()) {
            BCP bcp = entry.getValue();
            String estado = bcp.getEstado();
            if (!"Preparado".equals(estado) && !"En Ejecuccion".equals(estado))
                continue;

            int dur = Parser_String_To_Int.parseStringToInt(bcp.getTiempo_Ejecucion());
            // int llegada = parseStringToInt(bcp.getTiempo_Llegada());

            if (dur < bestDur) {
                bestDur = dur;
                // bestLlegada = llegada;
                bestPid = bcp.getPID();
            }
        }
        return bestPid;
    }

    @Override
    public String getNombre() {
        return "SJF";
    }

}
