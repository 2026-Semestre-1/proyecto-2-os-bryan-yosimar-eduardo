package kernel.planificacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kernel.GestorMemoria;
import kernel.Planificador;
import model.Almacenamiento;
import model.BCP;
import model.Codigo_ASM;
import model.Memoria;

public class AlgoritmoLottery implements IAlgoritmoPlanificacion {

    /**
     * Clase interna para el rango de tickets asignados a cada proceso
     */
    private static class TicketRange {
        final int pid;
        final int startTicket;
        final int endTicket;

        TicketRange(int pid, int startTicket, int endTicket) {
            this.pid = pid;
            this.startTicket = startTicket;
            this.endTicket = endTicket;
        }
    }

    private final Random random = new Random();
    private List<TicketRange> rangos = new ArrayList<>();
    private int totalTickets = 0;
    private static final int INSTRUCCIONES_POR_TICKET = 10; // se mantiene 10 instrucciones por ticket, puede ajustarse para variar la distribución

    @Override
    /**
     * Carga procesos a memmoria desde la cola de pendientes 
     * @param ctx contexto del planificador
     * @param memoria memoria del sistema
     * @param almacenamiento almacenamiento del sistema
     * @param controlador gestor de memoria
     * @param tiempoActualCPU tiempo actual de la CPU para asignar tiempos de llegada
     */
    public void cargarLote(Planificador ctx, Memoria memoria,
            Almacenamiento almacenamiento, GestorMemoria controlador,
            int tiempoActualCPU) {
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
     * Selecciona el siguiente proceso a ejecutar utilizando el algoritmo de loterry
     * @param ctx contexto del planificador
     * @return PID del proceso seleccionado o -1 si no hay procesos disponibles
     */
    public int seleccionarSiguiente(Planificador ctx) {
        reconstruirRangos(ctx);
        if (rangos.isEmpty() || totalTickets == 0) {
            return -1;
        }
        int winner = random.nextInt(totalTickets) + 1;
        return buscarPidPorTicket(winner);
    }

    /**
     * Reconstruye los rangos de tickets para los procesos en estado "Preparado"
     * @param ctx contexto del planificador
     */
    private void reconstruirRangos(Planificador ctx) {
        rangos.clear();
        totalTickets = 0;

        // Los procesos preparados son los que se toman para asignar tickets
        for (BCP bcp : ctx.getCola_Procesos_Nuevos().values()) {
            String estado = bcp.getEstado();
            if (!"Preparado".equals(estado)) {
                continue;
            }
            int tickets = calcularTickets(bcp);
            if (tickets <= 0) continue;

            int start = totalTickets + 1;
            int end = totalTickets + tickets;
            rangos.add(new TicketRange(bcp.getPID(), start, end));
            totalTickets += tickets;
        }
    }

    /**
     * Calcula el número de tickets para un proceso basado en su ráfaga restante
     * @param bcp proceso para el cual calcular tickets
     * @return número de tickets asignados
     */
    private int calcularTickets(BCP bcp) {
        try {
            int rafaga = Integer.parseInt(bcp.getRafaga_Restante());
            // Se da al menos 1 ticket a cada proceso, y se asignan más tickets a procesos con ráfagas más largas
            return Math.max(1, rafaga / INSTRUCCIONES_POR_TICKET);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /**
     * Busca el PID del proceso ganador dado un número de ticket
     * Es una búsqueda binaria en la lista de rangos de tickets
     * @param ticket número de ticket ganador
     * @return PID del proceso ganador o -1 si no se encuentra
     */
    private int buscarPidPorTicket(int ticket) {
        int left = 0;
        int right = rangos.size() - 1;
        while (left <= right) {
            int mid = (left + right) >>> 1;
            TicketRange r = rangos.get(mid);
            if (ticket < r.startTicket) {
                right = mid - 1;
            } else if (ticket > r.endTicket) {
                left = mid + 1;
            } else {
                return r.pid;
            }
        }
        return -1;
    }

    @Override
    public String getNombre() {
        return "Lottery";
    }
}
