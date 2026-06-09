package kernel.planificacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kernel.GestorMemoria;
import kernel.Planificador;
import model.Almacenamiento;
import model.BCP;
import model.Codigo_ASM;
import model.Memoria;
import util.Parser_String_To_Int;

public class AlgoritmoRR implements IAlgoritmoPlanificacion {

    private LinkedList<Integer> cola_rr = new LinkedList<>();

    @Override
    public void cargarLote(Planificador ctx, Memoria memoria, Almacenamiento almacenamiento,
            GestorMemoria controlador, int tiempoActualCPU) {
        // Igual que otros algoritmos: cargar procesos nuevos
        while (ctx.getCola_Procesos_Nuevos().size() < 5) {
            if (ctx.getCola_Programas_Pendientes().isEmpty())
                break;

            String nombrePrograma = ctx.getCola_Programas_Pendientes().get(0);
            Codigo_ASM codigo = ctx.obtener_Programa_Almacenamiento(almacenamiento, nombrePrograma);
            boolean creado = ctx.crearProcesoEnMemoria(nombrePrograma, codigo, memoria, controlador, tiempoActualCPU);
            if (!creado)
                break;

            // Añadir PID a la cola RR
            int pid = ctx.obtenerUltimoPidCreado();
            cola_rr.add(pid);
        }
    }

    @Override
    public int seleccionarSiguiente(Planificador ctx) {
        if (cola_rr.isEmpty())
            return -1;

        // Tomar el primero de la cola
        int pid = cola_rr.poll();
        // Volver a ponerlo al final (rotación circular)
        cola_rr.add(pid);

        return pid;
    }

    @Override
    public String getNombre() {
        return "RoundRobin";
    }

    // Método auxiliar para actualizar cola cuando un proceso termina
    public void removerProceso(int pid) {
        cola_rr.remove(Integer.valueOf(pid));
    }
}
