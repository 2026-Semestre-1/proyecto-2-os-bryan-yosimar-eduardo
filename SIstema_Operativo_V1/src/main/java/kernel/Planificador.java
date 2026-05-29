package kernel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public Planificador() {
        this.cola_Programas_Pendientes = new ArrayList<>();
        this.cola_Procesos_Nuevos = new LinkedHashMap<>();
        this.cola_Procesos_Terminados = new ArrayList<>();
    }

    public List<String> getCola_Programas_Pendientes() { return cola_Programas_Pendientes; }
    public Map<Integer, BCP> getCola_Procesos_Nuevos() { return cola_Procesos_Nuevos; }
    public List<BCP> getCola_Procesos_Terminados() { return cola_Procesos_Terminados; }
    public void agregar_Programa_Pendiente(String nombre) { this.cola_Programas_Pendientes.add(nombre); }
    public void agregar_Proceso_Nuevo(int pid, BCP bcp) { this.cola_Procesos_Nuevos.put(pid, bcp); }
    public void agregar_Proceso_Terminado(BCP bcp) { this.cola_Procesos_Terminados.add(bcp); }
    public void setControlador_Memoria(GestorMemoria pNuevo_Controlador) { this.controlador_Memoria = pNuevo_Controlador; }

    public int get_PID_Primer_Proceso_Nuevo() {
        int count = 0;
        for (BCP bcp : this.cola_Procesos_Nuevos.values()) {
            if (count == 0) { return bcp.getPID(); }
            count++;
            break;
        }
        return -1;
    }

    public void eliminar_Programa_Pendiente(String nombre) { this.cola_Programas_Pendientes.remove(nombre); }
    public void eliminar_Proceso_Nuevo(int pid) { this.cola_Procesos_Nuevos.remove(pid); }
    public void eliminar_Proceso_Terminado(int pid) { this.cola_Procesos_Terminados.remove(pid); }

    public Codigo_ASM obtener_Programa_Almacenamiento(Almacenamiento pMemoria_Secundaria, String pNombre_Programa) {
        System.out.println("Planificador: Obteniendo el programa: " + pNombre_Programa);
        Map<String, List<Integer>> indices_Programas = pMemoria_Secundaria.optener_Indices();
        List<Integer> indice_Codigo = indices_Programas.get(pNombre_Programa);
        Codigo_ASM codigo = pMemoria_Secundaria.optener_Programa(indice_Codigo.get(0), indice_Codigo.get(1));
        return codigo;
    }

    public boolean esta_en_cola_procesos_nuevos(String nombre) {
        for (BCP bcp : this.cola_Procesos_Nuevos.values()) {
            if (bcp.getNombre_Programa().equals(nombre)) { return true; }
        }
        return false;
    }

    public boolean esta_en_cola_procesos_terminados(String nombre) {
        for (BCP bcp : this.cola_Procesos_Terminados) {
            if (bcp.getNombre_Programa().equals(nombre)) { return true; }
        }
        return false;
    }

    public void extraer_Programas_Almacenamiento(Almacenamiento pMemoria_Secundaria) {
        Map<String, List<Integer>> indices_Programas = pMemoria_Secundaria.optener_Indices();
        for (String nombre_Programa : indices_Programas.keySet()) {
            if (nombre_Programa == null) { continue; }
            String nombreTrim = nombre_Programa.trim();
            if (nombreTrim.isEmpty()) { continue; }
            String lower = nombreTrim.toLowerCase();
            if (!lower.endsWith(".asm")) { continue; }
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
        pMemoria_Principal.actualizar_Estado_BCP(pPID, "Terminado");
        pMemoria_Principal.modificar_Tiempo_Finalizacion_BCP(pPID, pTiempo_Finalizacion);
        BCP bcp_Proceso = pMemoria_Principal.obtener_Datos_BCP(pPID);
        BCP bcp_Planificador = this.cola_Procesos_Nuevos.get(pPID);
        if (bcp_Planificador == null) {
            System.out.println("Controlador Planificador: El proceso no se encuentra en la cola de procesos nuevos.");
        }
        String nombre_Programa = bcp_Planificador.getNombre_Programa();
        LocalTime momento_Creacion = bcp_Planificador.get_momento_creacion();
        bcp_Proceso.setNombre_Programa(nombre_Programa);
        bcp_Proceso.set_momento_creacion(momento_Creacion);
        bcp_Proceso.set_momento_finalizacion(LocalTime.now());
        this.eliminar_Proceso_Nuevo(pPID);
        this.agregar_Proceso_Terminado(bcp_Proceso);
        this.controlador_Memoria.limpiar_Memoria_Proceso(pPID, Integer.parseInt(bcp_Proceso.getMem_End()));
    }

    public void cambiar_Estado_Proceso_Nuevo() {
        System.out.println("Planificador: Cambiando el estado de los procesos nuevos a preparados.");
        int count_Iteraciones = 0;
        for (BCP bcp : this.cola_Procesos_Nuevos.values()) {
            String estado = "Preparado";
            if (count_Iteraciones == 0) {
                estado = "En Ejecuccion";
                count_Iteraciones++;
            }
            System.out.println("[DEBUG ESTADO] PID=" + bcp.getPID() + " (" + bcp.getNombre_Programa() + ") -> " + estado);
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
        if (cant_Procesos < 5) {
            for (int i = 0; i < this.cola_Programas_Pendientes.size(); i++) {
                if (cant_Procesos == 5) { break; }
                int pid = this.controlador_Memoria.get_Nuevo_PID();
                procesos.put(pid + i, "Nuevo");
                cant_Procesos++;
            }
        }
        return procesos;
    }

    public void FSFS_Planificador(Memoria pMemoria_Principal, Almacenamiento pMemoria_Secundaria,
            int pTiempo_Total_CPU) {
        System.out.println("Planificador: Iniciando el planificador FCFS.");
        for (int i = this.cola_Procesos_Nuevos.size(); i < 5; i++) {
            if (this.cola_Programas_Pendientes.isEmpty()) {
                System.out.println("Planificador: No hay programas pendientes...");
                break;
            }
            System.out.println("Planificador: Agregando proceso: " + i);
            String nombre_Programa = this.cola_Programas_Pendientes.get(0);
            Codigo_ASM codigo = this.obtener_Programa_Almacenamiento(pMemoria_Secundaria, nombre_Programa);
            System.out.println("Planificador: Codigo del programa: " + codigo.getContador_Intrucciones());
            int espacio_Necesario_Programa = codigo.getContador_Intrucciones();
            int espacio_Necesario_BCP = 26;
            int hay_esoacio_OS = this.controlador_Memoria
                    .validar_Espacio_Disponible_Usuario(espacio_Necesario_Programa);
            int osCheck = pMemoria_Principal.validar_Espacio_Disponible_OS(espacio_Necesario_BCP);
            System.out.println("[DEBUG FSFS] i=" + i + " prog=" + nombre_Programa
                    + " | OS usado=" + pMemoria_Principal.getEspacio_Usado_OS()
                    + " OS max=" + pMemoria_Principal.getEspacio_OS()
                    + " necesita=" + espacio_Necesario_BCP
                    + " | User usado=" + pMemoria_Principal.getEspacio_Usado_Usuario()
                    + " User max=" + pMemoria_Principal.getEspacio_Usuario()
                    + " necesitaProg=" + espacio_Necesario_Programa
                    + " | osCheck=" + osCheck + " userCheck=" + hay_esoacio_OS);
            if (osCheck == 0 && hay_esoacio_OS != 0) {
                System.out.println("Planificador: PASS 1");
                int pid = pMemoria_Principal.asignar_Nuevo_PID_Proceso();
                System.out.println("Planificador: PASS 2");
                int tiempo_Estimado = Calcular_Tiempo_Estimado_Programa.calcular_Tiempo_Estimado(codigo);
                System.out.println("Planificador: PASS 3 -> Duracion estimada: " + tiempo_Estimado);
                int pos_MV = this.controlador_Memoria.get_Pos_Actual_MV();
                pMemoria_Principal.iniciar_Memoria_BCP(espacio_Necesario_Programa, 1, pid + 1, 1, pTiempo_Total_CPU,
                        tiempo_Estimado, pos_MV);
                System.out.println("Planificador: PASS 4");
                this.controlador_Memoria.asignar_Memoria_Programa(codigo);
                System.out.println("Planificador: PASS 5");
                int pid_Siguiente = 0;
                if (i == 4) { pid_Siguiente = -1; }
                else { pid_Siguiente = pid + 1; }
                pMemoria_Principal.modificar_Enlace_Siguiente_BCP(pid, pid_Siguiente);
                BCP nuevo_BCP = pMemoria_Principal.obtener_Datos_BCP(pid);
                nuevo_BCP.setNombre_Programa(nombre_Programa);
                nuevo_BCP.set_momento_creacion(LocalTime.now());
                System.out.println("Planificador: PASS 6");
                this.agregar_Proceso_Nuevo(pid, nuevo_BCP);
                System.out.println("Planificador: PASS 7");
                this.eliminar_Programa_Pendiente(nombre_Programa);
                System.out.println("Planificador: PASS 8");
            } else { break; }
        }
    }

    public void modificar_Siguiente_BCP_FCFS(Memoria pMemoria_Principal) {
        for (int i = 0; i < 5; i++) {
            BCP bcp = this.cola_Procesos_Nuevos.get(i);
            int PID_Proceso_Actual = Integer.valueOf(bcp.getPID());
            int pid_Siguiente = 0;
            if (i == 4) { pid_Siguiente = -1; }
            else { pid_Siguiente = PID_Proceso_Actual + 1; }
            pMemoria_Principal.modificar_Enlace_Siguiente_BCP(PID_Proceso_Actual, pid_Siguiente);
            BCP bcp_Actual = pMemoria_Principal.obtener_Datos_BCP(PID_Proceso_Actual);
            bcp_Actual.setNombre_Programa(bcp.getNombre_Programa());
            this.cola_Procesos_Nuevos.put(PID_Proceso_Actual, bcp_Actual);
        }
    }

    public boolean hay_Procesos_Nuevos() {
        if (this.cola_Procesos_Nuevos.isEmpty()) { return false; }
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
