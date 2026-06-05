package kernel.planificacion;

import kernel.GestorMemoria;
import kernel.Planificador;
import model.Almacenamiento;
import model.Memoria;


/*
 * IAlgoritmoPlanificacion va a funcionar como una interfaz para implementar en diferentes algoritmos.
 * La idea es que cambiar entre algoritmos sea más sencillo evitando acoplamiento
 */
public interface IAlgoritmoPlanificacion {

    /*
     * Carga un lote de procesos desde cola_pendientes a memoria, cada algoritmo debe decididr cuantos cargaria, y en que orden.
     */
    void cargarLote (Planificador ctx, Memoria memoria, Almacenamiento almacenamiento, GestorMemoria controlador, int tiempoActualCPU);

    /*
     * Seleccionar el pid del siguiente proceso a ejecutar entre procesos Preparados
     * -1 si no hay ninguno
     */
    int seleccionarSiguiente (Planificador ctx);

    /*
     * Obtiene el nombre del algoritmo de planificación.
     */
    String getNombre();

}
