package kernel.planificacion;

import kernel.GestorMemoria;
import kernel.Planificador;
import model.Almacenamiento;
import model.BCP;
import model.Codigo_ASM;
import model.Memoria;

public class AlgoritmoFCFS implements IAlgoritmoPlanificacion {

    @Override
    /**
     * Carga un lote de procesos en la memoria.
     * @param ctx
     * @param memoria
     * @param almacenamiento
     * @param controlador
     * @param tiempoActualCPU
     */
    public void cargarLote(Planificador ctx, Memoria memoria, Almacenamiento almacenamiento,
            GestorMemoria controlador, int tiempoActualCPU) {
        while (ctx.getCola_Procesos_Nuevos().size() < ctx.getMaxProcesosSimultaneos()) {
            if (ctx.getCola_Programas_Pendientes().isEmpty()) {
                break;
            }
            String nombrePrograma = ctx.getCola_Programas_Pendientes().get(0);
            Codigo_ASM codigo = ctx.obtener_Programa_Almacenamiento(almacenamiento, nombrePrograma);
            boolean creado = ctx.crearProcesoEnMemoria(nombrePrograma, codigo,
                memoria, controlador, tiempoActualCPU);
            if (!creado) {
                break;
            }
        }
    }

    @Override
    /**
     * Selecciona el siguiente proceso a ejecutar.
     * @param ctx
     * @return
     */
    public int seleccionarSiguiente(Planificador ctx) {
        for (BCP bcp : ctx.getCola_Procesos_Nuevos().values()) {
            String estado = bcp.getEstado();
            if ("Preparado".equals(estado) || "En Ejecuccion".equals(estado)) {
                return bcp.getPID();
            }
        }
        return -1;
    }

    @Override
    /**
     * Retorna el nombre del algoritmo de planificación.
     * @return
     */
    public String getNombre() {
        return "FCFS";
    }
}
