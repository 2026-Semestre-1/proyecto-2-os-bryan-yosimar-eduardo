package kernel.planificacion;

import java.util.ArrayList;
import java.util.HashMap;

import kernel.GestorMemoria;
import kernel.Planificador;
import model.Almacenamiento;
import model.BCP;
import model.Codigo_ASM;
import model.Memoria;

public class AlgoritmoHRRN implements IAlgoritmoPlanificacion {

    @Override
    public void cargarLote(Planificador ctx, Memoria memoria, Almacenamiento almacenamiento, GestorMemoria controlador,
            int tiempoActualCPU) {

        // Igual que SJF/SRT: cargar procesos mientras haya espacio (<5)
        while (ctx.getCola_Procesos_Nuevos().size() < ctx.getMaxProcesosSimultaneos()) {
            if (ctx.getCola_Programas_Pendientes().isEmpty())
                break;

            String nombrePrograma = ctx.getCola_Programas_Pendientes().get(0);
            Codigo_ASM codigo = ctx.obtener_Programa_Almacenamiento(almacenamiento, nombrePrograma);
            boolean creado = ctx.crearProcesoEnMemoria(nombrePrograma, codigo, memoria, controlador, tiempoActualCPU);
            if (!creado)
                break;
        }

    }

    @Override
    public int seleccionarSiguiente(Planificador ctx) {

        int mejorPID = -1;
        int mejorLlegada = Integer.MAX_VALUE;

        for (Map.Entry<Integer, BCP> entry : ctx.getCola_Procesos_Nuevos().entrySet()) {
            BCP bcp = entry.getValue();
            String estado = bcp.getEstado();

            // Saltarse los procesos que esten en ejecucion.
            if (!"Preparado".equals(estado))
                continue;

            int servicio = Parser_String_To_Int.parseStringToInt(bcp.getTiempo_Ejecucion());
            int llegada = Parser_String_To_Int.parseStringToInt(bcp.getTiempo_Llegada());
            int espera = Parser_String_To_Int.parseStringToInt(bcp.getTiempo_Espera());

            double ratio = (double) (espera + servicio) / servicio;

            if (ratio > mejorRatio || (ratio == mejorRatio && llegada < mejorLlegada)) {
                mejorRatio = ratio;
                mejorLlegada = llegada;
                mejorPID = bcp.getPID();
            }
        }
        return mejorPID;
    }
    public String getNombre() {
        return "HRRN";
    }

}
