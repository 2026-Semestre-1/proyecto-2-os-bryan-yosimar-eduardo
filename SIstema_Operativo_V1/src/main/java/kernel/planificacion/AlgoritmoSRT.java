package kernel.planificacion;

import java.util.Map;

import kernel.GestorMemoria;
import kernel.Planificador;
import model.Almacenamiento;
import model.BCP;
import model.Codigo_ASM;
import model.Memoria;
import util.Parser_String_To_Int;

public class AlgoritmoSRT implements IAlgoritmoPlanificacion {

    @Override
    public void cargarLote(Planificador ctx, Memoria memoria, Almacenamiento almacenamiento,
            GestorMemoria controlador, int tiempoActualCPU) {
        // Igual que SJF: cargar procesos mientras haya espacio (<5)
        while (ctx.getCola_Procesos_Nuevos().size() < ctx.getMaxProcesosSimultaneos()) {
            if (ctx.getCola_Programas_Pendientes().isEmpty())
                break;

            String nombrePrograma = ctx.getCola_Programas_Pendientes().get(0);
            Codigo_ASM codigo = ctx.obtener_Programa_Almacenamiento(almacenamiento, nombrePrograma);
            ctx.getCola_Programas_Pendientes().remove(0);
            String nombreInstancia = ctx.getSiguienteNombreInstancia(nombrePrograma);
            boolean creado = ctx.crearProcesoEnMemoria(nombreInstancia, codigo, memoria, controlador, tiempoActualCPU);
            if (!creado) {
                ctx.getCola_Programas_Pendientes().add(0, nombrePrograma);
                break;
            }
        }
    }

    @Override
    public int seleccionarSiguiente(Planificador ctx) {
        int mejorPID = -1;

        int mejorTiempoRestante = Integer.MAX_VALUE;

        int mejorTiempoLlegada = Integer.MAX_VALUE; // Esto es para el tema de desempate.

        for (Map.Entry<Integer, BCP> entry : ctx.getCola_Procesos_Nuevos().entrySet()) {
            BCP bcp = entry.getValue();
            String estado = bcp.getEstado();
            if (!"Preparado".equals(estado) && !"En Ejecuccion".equals(estado))
                continue;

            int restante = Parser_String_To_Int.parseStringToInt(bcp.getRafaga_Restante());
            int llegada = Parser_String_To_Int.parseStringToInt(bcp.getTiempo_Llegada());

            if (restante < mejorTiempoRestante || (restante == mejorTiempoRestante && llegada < mejorTiempoLlegada)) {
                mejorTiempoRestante = restante;
                mejorTiempoLlegada = llegada;
                mejorPID = bcp.getPID();
            }
        }
        return mejorPID;
    }

    @Override
    public String getNombre() {
        return "SRT";
    }

}
