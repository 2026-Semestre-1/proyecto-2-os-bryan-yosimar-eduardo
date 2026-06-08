package kernel.planificacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kernel.GestorMemoria;
import kernel.Planificador;
import model.Almacenamiento;
import model.BCP;
import model.Codigo_ASM;
import model.Memoria;
import util.Parser_String_To_Int;

public class AlgoritmoLotery implements IAlgoritmoPlanificacion {

    @Override
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
    public int seleccionarSiguiente(Planificador ctx) {

        return 1;
    }

    @Override
    public String getNombre() {
        return "Lottery";
    }

}
