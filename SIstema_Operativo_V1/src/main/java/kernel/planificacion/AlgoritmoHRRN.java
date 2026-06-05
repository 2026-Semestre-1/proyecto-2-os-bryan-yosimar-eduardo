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

    }

    @Override
    public int seleccionarSiguiente(Planificador ctx) {
        return 0;
    }

    @Override
    public String getNombre() {
        return "HRRN";
    }

}
