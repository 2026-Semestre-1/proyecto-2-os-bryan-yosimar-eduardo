package kernel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import kernel.planificacion.IAlgoritmoPlanificacion;
import util.Calcular_Tiempo_Estimado_Programa;
import model.Almacenamiento;
import model.BCP;
import model.Codigo_ASM;
import model.Memoria;

public class Planificador {

    private List<String> cola_Programas_Pendientes;
    private Map<Integer, BCP> cola_Procesos_Nuevos;
    private List<BCP> cola_Procesos_Terminados;

    private GestorMemoria controlador_Memoria;
    private int banderaOverlay = 0;
    private IAlgoritmoPlanificacion algoritmo;
    private int quantum = 0;
    private int maxProcesosSimultaneos = 5;
    private Map<String, Integer> contadorInstancias = new HashMap<>();
    private static final int TAMANO_BCP = 28;

    public Planificador() {
        this.cola_Programas_Pendientes = new ArrayList<>();
        this.cola_Procesos_Nuevos = new LinkedHashMap<>();
        this.cola_Procesos_Terminados = new ArrayList<>();
    }

    public List<String> getCola_Programas_Pendientes() {
        return cola_Programas_Pendientes;
    }

    public Map<Integer, BCP> getCola_Procesos_Nuevos() {
        return cola_Procesos_Nuevos;
    }

    public List<BCP> getCola_Procesos_Terminados() {
        return cola_Procesos_Terminados;
    }

    public int getMaxProcesosSimultaneos() {
        return maxProcesosSimultaneos;
    }

    public void setMaxProcesosSimultaneos(int maxProcesosSimultaneos) {
        this.maxProcesosSimultaneos = maxProcesosSimultaneos;
    }

    public String getSiguienteNombreInstancia(String nombreBase) {
        int count = this.contadorInstancias.getOrDefault(nombreBase, 0) + 1;
        this.contadorInstancias.put(nombreBase, count);
        return nombreBase + "_" + count;
    }

    public void agregar_Programa_Pendiente(String nombre) {
        this.cola_Programas_Pendientes.add(nombre);
    }

    public void agregar_Proceso_Nuevo(int pid, BCP bcp) {
        this.cola_Procesos_Nuevos.put(pid, bcp);
    }

    public void agregar_Proceso_Terminado(BCP bcp) {
        this.cola_Procesos_Terminados.add(bcp);
    }

    public void setControlador_Memoria(GestorMemoria pNuevo_Controlador) {
        this.controlador_Memoria = pNuevo_Controlador;
    }

    public void eliminar_Programa_Pendiente(String nombre) {
        this.cola_Programas_Pendientes.remove(nombre);
    }

    public void eliminar_Proceso_Nuevo(int pid) {
        this.cola_Procesos_Nuevos.remove(pid);
    }

    public void eliminar_Proceso_Terminado(int pid) {
        this.cola_Procesos_Terminados.remove(pid);
    }

    public void setQuantum(int pQuantum) {
        this.quantum = pQuantum;
    }

    public int getQuantum() {
        return this.quantum;
    }

    public Codigo_ASM obtener_Programa_Almacenamiento(Almacenamiento pMemoria_Secundaria, String pNombre_Programa) {
        System.out.println("Planificador: Obteniendo el programa: " + pNombre_Programa);
        Map<String, List<Integer>> indices_Programas = pMemoria_Secundaria.optener_Indices();
        List<Integer> indice_Codigo = indices_Programas.get(pNombre_Programa);
        Codigo_ASM codigo = pMemoria_Secundaria.optener_Programa(indice_Codigo.get(0), indice_Codigo.get(1));
        return codigo;
    }

    public boolean esta_en_cola_procesos_nuevos(String nombre) {
        for (BCP bcp : this.cola_Procesos_Nuevos.values()) {
            if (bcp.getNombre_Programa().equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    public boolean esta_en_cola_procesos_terminados(String nombre) {
        for (BCP bcp : this.cola_Procesos_Terminados) {
            if (bcp.getNombre_Programa().equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    public void extraer_Programas_Almacenamiento(Almacenamiento pMemoria_Secundaria) {
        Map<String, List<Integer>> indices_Programas = pMemoria_Secundaria.optener_Indices();
        for (String nombre_Programa : indices_Programas.keySet()) {
            if (nombre_Programa == null) {
                continue;
            }
            String nombreTrim = nombre_Programa.trim();
            if (nombreTrim.isEmpty()) {
                continue;
            }
            String lower = nombreTrim.toLowerCase();
            if (!lower.endsWith(".asm")) {
                continue;
            }
            if (!this.cola_Programas_Pendientes.contains(nombreTrim)
                    && !this.esta_en_cola_procesos_nuevos(nombreTrim)
                    && !this.esta_en_cola_procesos_terminados(nombreTrim)) {
                System.out.println("Planificador: Se agrego el programa: " + nombreTrim);
                this.agregar_Programa_Pendiente(nombreTrim);
            }
        }
        mostrar_lista_programas_pendientes();
    }

    public List<String> obtener_Nombre_Programa_Procesos() {
        List<String> nombres_Programas = new ArrayList<>();
        for (BCP bcp : this.cola_Procesos_Nuevos.values()) {
            nombres_Programas.add(bcp.getNombre_Programa());
        }
        return nombres_Programas;
    }

    public void finalizacion_Procesos(Memoria pMemoria_Principal, int pPID, int pTiempo_Finalizacion) {
        BCP bcp_Planificador = this.cola_Procesos_Nuevos.get(pPID);
        if (bcp_Planificador == null) {
            System.out.println("Controlador Planificador: El proceso no se encuentra en la cola de procesos nuevos.");
            return;
        }
        pMemoria_Principal.actualizar_Estado_BCP(pPID, "Terminado");
        pMemoria_Principal.modificar_Tiempo_Finalizacion_BCP(pPID, pTiempo_Finalizacion);
        BCP bcp_Proceso = pMemoria_Principal.obtener_Datos_BCP(pPID);
        String nombre_Programa = bcp_Planificador.getNombre_Programa();
        LocalTime momento_Creacion = bcp_Planificador.get_momento_creacion();
        bcp_Proceso.setNombre_Programa(nombre_Programa);
        bcp_Proceso.set_momento_creacion(momento_Creacion);
        bcp_Proceso.set_momento_finalizacion(LocalTime.now());
        this.eliminar_Proceso_Nuevo(pPID);
        this.agregar_Proceso_Terminado(bcp_Proceso);
        this.controlador_Memoria.limpiar_Memoria_Proceso(pPID, Integer.parseInt(bcp_Proceso.getMem_End()),
                nombre_Programa);
    }

    public void cambiar_Estado_Proceso_Nuevo() {
        System.out.println("Planificador: Cambiando el estado de los procesos nuevos a preparados.");
        for (BCP bcp : this.cola_Procesos_Nuevos.values()) {
            String estado = "Preparado";
            System.out
                    .println("[DEBUG ESTADO] PID=" + bcp.getPID() + " (" + bcp.getNombre_Programa() + ") -> " + estado);
            bcp.setEstado(estado);
            this.controlador_Memoria.actualizar_Estado_BCP(bcp.getPID(), estado);
        }
    }

    public Map<Integer, String> obtener_Estado_5_Procesos() {
        Map<Integer, String> procesos = new HashMap<>();
        int cant_Procesos = 0;
        for (BCP bcp : this.cola_Procesos_Terminados) {
            int pid = bcp.getPID();
            String estado = bcp.getEstado();
            procesos.put(pid, estado);
        }
        for (BCP bcp : this.cola_Procesos_Nuevos.values()) {
            int pid = bcp.getPID();
            String estado = bcp.getEstado();
            procesos.put(pid, estado);
            cant_Procesos++;
        }
        if (cant_Procesos < this.maxProcesosSimultaneos) {
            for (int i = 0; i < this.cola_Programas_Pendientes.size(); i++) {
                if (cant_Procesos == this.maxProcesosSimultaneos) {
                    break;
                }
                int pid = this.controlador_Memoria.get_Nuevo_PID();
                procesos.put(pid + i, "Nuevo");
                cant_Procesos++;
            }
        }
        return procesos;
    }

    /**
     * Se crea un proceso en memoria a partir de un programa, asignandosele un ID y
     * agreganose a la cola de nuevos.
     * Retorna true si fue creado en memoria y false si no se pudo crear por falta
     * de espacio.
     * 
     * @param nombrePrograma
     * @param codigo
     * @param memoria
     * @param controlador
     * @param tiempoActualCPU
     * @return
     */
    public boolean crearProcesoEnMemoria(String nombrePrograma, Codigo_ASM codigo,
            Memoria memoria, GestorMemoria controlador, int tiempoActualCPU) {
        int espacio_Necesario_Programa = codigo.getContador_Intrucciones();
        int espacio_Necesario_BCP = TAMANO_BCP;
        int hayEspacioUser = controlador
                .validar_Espacio_Disponible_Usuario(espacio_Necesario_Programa, nombrePrograma, memoria);
        int osCheck = memoria.validar_Espacio_Disponible_OS(espacio_Necesario_BCP);

        System.out.println("[DEBUG FSFS] prog=" + nombrePrograma
                + " | OS usado=" + memoria.getEspacio_Usado_OS()
                + " OS max=" + memoria.getEspacio_OS()
                + " necesita=" + espacio_Necesario_BCP
                + " | User usado=" + memoria.getEspacio_Usado_Usuario()
                + " User max=" + memoria.getEspacio_Usuario()
                + " necesitaProg=" + espacio_Necesario_Programa
                + " | osCheck=" + osCheck + " userCheck=" + hayEspacioUser);
        if (osCheck != 0 || hayEspacioUser == 0) {
            return false;
        }
        System.out.println("Planificador: PASS 1");
        int pid = memoria.asignar_Nuevo_PID_Proceso();
        System.out.println("Planificador: PASS 2");
        int tiempo_Estimado = Calcular_Tiempo_Estimado_Programa.calcular_Tiempo_Estimado(codigo);
        System.out.println("Planificador: PASS 3 -> Duracion estimada: " + tiempo_Estimado);

        if (hayEspacioUser == 3) {
            int totalOverlays = controlador.crearOverlay(espacio_Necesario_Programa, codigo, pid);
            int pcInicial = controlador.getInicioParticionProceso(pid);
            int tamParticion = controlador.getTamanoParticionProceso(pid);
            memoria.iniciar_Memoria_BCP(espacio_Necesario_Programa, 1, pid + 1, 1,
                    tiempoActualCPU, tiempo_Estimado, 0, pcInicial);
            int posBCP = memoria.buscar_Posicion_BCP(pid);
            memoria.getMemoria_Principal().put(posBCP + 3, String.valueOf(pcInicial));
            memoria.getMemoria_Principal().put(posBCP + 4, String.valueOf(pcInicial + tamParticion - 1));
            memoria.getMemoria_Principal().put(posBCP + 5, String.valueOf(pcInicial));
            BCP nuevo_BCP = memoria.obtener_Datos_BCP(pid);
            nuevo_BCP.setTieneOverlay(true);
            nuevo_BCP.setTotalOverlays(totalOverlays);
            nuevo_BCP.setOverlayActual(0);
            nuevo_BCP.setNombre_Programa(nombrePrograma);
            nuevo_BCP.set_momento_creacion(LocalTime.now());
            nuevo_BCP.setPosInicioOverlayMV(controlador.obtenerUltimaPosMV());
            controlador.guardarBCP(nuevo_BCP);
            memoria.modificar_Enlace_Siguiente_BCP(pid, -1);
            this.agregar_Proceso_Nuevo(pid, nuevo_BCP);
            this.eliminar_Programa_Pendiente(nombrePrograma);
            return true;
        }

        int pcInicial;
        if ("Paginacion".equals(controlador.getTipoGestionMemoria())) {
            pcInicial = 0;
        } else {
            pcInicial = memoria.getPosicion_Actual_Usuario();
        }

        int pos_MV = controlador.get_Pos_Actual_MV();
        if ("ParticionIgual".equals(controlador.getTipoGestionMemoria())) {
            controlador.asignar_Memoria_Programa(codigo, nombrePrograma, pid);
            pcInicial = controlador.getInicioParticionProceso(pid);
            memoria.iniciar_Memoria_BCP(espacio_Necesario_Programa, 1, pid + 1, 1,
                    tiempoActualCPU, tiempo_Estimado, pos_MV, pcInicial);
        } else if ("ParticionIgualDinamica".equals(controlador.getTipoGestionMemoria())) {
            controlador.asignar_Memoria_Programa(codigo, nombrePrograma, pid);
            pcInicial = controlador.getInicioParticionProceso(pid);
            memoria.iniciar_Memoria_BCP(espacio_Necesario_Programa, 1, pid + 1, 1,
                    tiempoActualCPU, tiempo_Estimado, pos_MV, pcInicial);
        } else if ("Dinamica".equals(controlador.getTipoGestionMemoria())) {
            controlador.asignar_Memoria_Programa(codigo, nombrePrograma, pid);
            pcInicial = controlador.getInicioParticionProceso(pid);
            memoria.iniciar_Memoria_BCP(espacio_Necesario_Programa, 1, pid + 1, 1,
                    tiempoActualCPU, tiempo_Estimado, pos_MV, pcInicial);
        }

        else {
            // Flujo que estaba antes por aquello
            memoria.iniciar_Memoria_BCP(espacio_Necesario_Programa, 1, pid + 1, 1,
                    tiempoActualCPU, tiempo_Estimado, pos_MV, pcInicial);
            if ("Paginacion".equals(controlador.getTipoGestionMemoria())) {
                int posBCP = memoria.buscar_Posicion_BCP(pid);
                memoria.getMemoria_Principal().put(posBCP + 3, String.valueOf(pcInicial));
                memoria.getMemoria_Principal().put(posBCP + 4,
                        String.valueOf(pcInicial + espacio_Necesario_Programa - 1));
                memoria.getMemoria_Principal().put(posBCP + 5, String.valueOf(pcInicial));
            }
            controlador.asignar_Memoria_Programa(codigo, nombrePrograma, pid);
        }
        System.out.println("Planificador: PASS 4");
        System.out.println("Planificador: PASS 5");
        int pid_Siguiente = (this.cola_Procesos_Nuevos.size() >= 4) ? -1 : pid + 1;
        memoria.modificar_Enlace_Siguiente_BCP(pid, pid_Siguiente);
        BCP nuevo_BCP = memoria.obtener_Datos_BCP(pid);
        nuevo_BCP.setNombre_Programa(nombrePrograma);
        nuevo_BCP.set_momento_creacion(LocalTime.now());
        System.out.println("Planificador: PASS 6");
        this.agregar_Proceso_Nuevo(pid, nuevo_BCP);
        System.out.println("Planificador: PASS 7");
        this.eliminar_Programa_Pendiente(nombrePrograma);
        System.out.println("Planificador: PASS 8");
        return true;
    }

    public void setAlgoritmoPlanificacion(IAlgoritmoPlanificacion algoritmo) {
        this.algoritmo = algoritmo;
    }

    // ############## Seccion de la parte de la interfaz de planificacion
    // #############

    public void cargarLote(Memoria memoria, Almacenamiento almacenamiento, int tiempoActualCPU) {
        if (this.algoritmo == null) {
            System.out.println("Planificador: No hay algoritmo configurado.");
            return;
        }
        this.algoritmo.cargarLote(this, memoria, almacenamiento, this.controlador_Memoria, tiempoActualCPU);
    }

    public int seleccionarSiguiente() {
        if (this.algoritmo == null) {
            return -1;
        }
        return this.algoritmo.seleccionarSiguiente(this);
    }

    public String getNombreAlgoritmo() {
        return this.algoritmo.getNombre();
    }

    public boolean hay_Procesos_Nuevos() {
        if (this.cola_Procesos_Nuevos.isEmpty()) {
            return false;
        }
        return true;
    }

    public int comprobar_Finalizacion_Proceso(int pPID) {
        int resultado = this.controlador_Memoria.comprobar_Finalizacion_Proceso(pPID);
        return resultado;
    }

    public void mostrar_lista_programas_pendientes() {
        System.out.println("-> Lista de programas pendientes:");
        for (String nombre_Programa : this.cola_Programas_Pendientes) {
            System.out.println("-> " + nombre_Programa);
        }
    }

    public void mostrar_lista_procesos_nuevos() {
        System.out.println("-> Lista de procesos nuevos:");
        for (int pid : this.cola_Procesos_Nuevos.keySet()) {
            System.out.println("-> " + pid);
        }
    }

    public void mostrar_lista_procesos_terminados() {
        System.out.println("-> Lista de procesos terminados:");
        for (BCP pid : this.cola_Procesos_Terminados) {
            System.out.println("-> " + pid.getPID());
        }
    }
}
