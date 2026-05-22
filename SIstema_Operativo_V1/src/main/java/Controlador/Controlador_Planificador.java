/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Controlador.Utils.Calcular_Tiempo_Estimado_Programa;
import Modelo.Almacenamiento;
import Modelo.BCP;
import Modelo.Codigo_ASM;
import Modelo.Memoria;

import Controlador.Controlador_Memoria;

/**
 *
 * @author edurg
 */
public class Controlador_Planificador {

    // Cola que registra los programas que existen en el almacenamiento y los carga
    // todo.
    // se tiene que actualizar conforme se agrega un nuevo programa a la memoria.
    private List<String> cola_Programas_Pendientes;

    // Cola procesos los (5) procesos nuevos que se van a poder trabajar.
    private Map<Integer, BCP> cola_Procesos_Nuevos;

    // Cola procesos terminados, nada mas se registra el PID de esto, existen en
    // memoria.
    private List<BCP> cola_Procesos_Terminados;

    private Controlador_Memoria controlador_Memoria;

    public Controlador_Planificador() {
        this.cola_Programas_Pendientes = new ArrayList<>();
        this.cola_Procesos_Nuevos = new LinkedHashMap<>();
        this.cola_Procesos_Terminados = new ArrayList<>();

    }

    // #### Seccion para la optencion de datos de las colas #####
    public List<String> getCola_Programas_Pendientes() {
        return cola_Programas_Pendientes;
    }

    public Map<Integer, BCP> getCola_Procesos_Nuevos() {
        return cola_Procesos_Nuevos;
    }

    public List<BCP> getCola_Procesos_Terminados() {
        return cola_Procesos_Terminados;
    }

    // #### Seccion para el agregado de datos a las colas #####
    public void agregar_Programa_Pendiente(String nombre) {
        this.cola_Programas_Pendientes.add(nombre);
    }

    public void agregar_Proceso_Nuevo(int pid, BCP bcp) {
        this.cola_Procesos_Nuevos.put(pid, bcp);
    }

    public void agregar_Proceso_Terminado(BCP bcp) {
        this.cola_Procesos_Terminados.add(bcp);
    }

    public void setControlador_Memoria(Controlador_Memoria pNuevo_Controlador) {
        this.controlador_Memoria = pNuevo_Controlador;
    }

    // ####### Seccion para optencion del primer proceso. #######

    public int get_PID_Primer_Proceso_Nuevo() {

        int count = 0;
        for (BCP bcp : this.cola_Procesos_Nuevos.values()) {
            if (count == 0) {
                return bcp.getPID();
            }
            count++;
            break;
        }

        return -1;
    }

    // #### Seccion para la eliminacion de datos de las colas #####
    public void eliminar_Programa_Pendiente(String nombre) {
        this.cola_Programas_Pendientes.remove(nombre);
    }

    public void eliminar_Proceso_Nuevo(int pid) {
        this.cola_Procesos_Nuevos.remove(pid);
    }

    public void eliminar_Proceso_Terminado(int pid) {
        this.cola_Procesos_Terminados.remove(pid);
    }

    // ####### Seccion para calculos adicionales. #######

    public Codigo_ASM obtener_Programa_Almacenamiento(Almacenamiento pMemoria_Secundaria, String pNombre_Programa) {

        System.out.println("Planificador: Obteniendo el programa: " +
                pNombre_Programa);
        // 1. Obtener los indices de todos los archivos.
        Map<String, List<Integer>> indices_Programas = pMemoria_Secundaria.optener_Indices();

        // 2. Obtener los indices del programa que se ocupa.
        List<Integer> indice_Codigo = indices_Programas.get(pNombre_Programa);

        // System.out.println("Planificador: Indice del programa: " +
        // indice_Codigo.get(0) + " - " + indice_Codigo.get(1));
        // 3. Obtener el codigo del programa.
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

    // Extraer los programas almacenado en memoria y que todavia no esten en la cola
    // de procesos nuevos.
    public void extraer_Programas_Almacenamiento(Almacenamiento pMemoria_Secundaria) {

        // 1. Obtener los indices de todos los archivos.
        Map<String, List<Integer>> indices_Programas = pMemoria_Secundaria.optener_Indices();

        // 2. Revisar cuales archivos no estan ya cargado en la cola de programas
        // pendientes.
        // for (String nombre_Programa : indices_Programas.keySet()) {

        for (String nombre_Programa : indices_Programas.keySet()) {

            if (nombre_Programa == null) {
                continue;
            }

            String nombreTrim = nombre_Programa.trim();
            if (nombreTrim.isEmpty()) {
                continue;
            }

            // Validar que sea un archivo .asm (insensible a mayúsculas)
            String lower = nombreTrim.toLowerCase();
            if (!lower.endsWith(".asm")) {
                // Ignorar archivos que no sean .asm
                continue;
            }

            // Revisar que los programas no esten tambien en la cola de procesos nuevos
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

    // ######### Seccion para la finalizacion de procesos. ####
    public void finalizacion_Procesos(Memoria pMemoria_Principal, int pPID, int pTiempo_Finalizacion) {

        // 1. Actualizamos el estado de la BCP en memoria

        pMemoria_Principal.actualizar_Estado_BCP(pPID, "Terminado");
        pMemoria_Principal.modificar_Tiempo_Finalizacion_BCP(pPID, pTiempo_Finalizacion);

        // 2. Recuperamos la BCP del proceso de la memoria.
        BCP bcp_Proceso = pMemoria_Principal.obtener_Datos_BCP(pPID);

        // 3. Obtenermos los datos del nombre del archivo y el momento de creacion que
        // estan
        // en la BCP del planificador.
        BCP bcp_Planificador = this.cola_Procesos_Nuevos.get(pPID);
        if (bcp_Planificador == null) {
            System.out.println("Controlador Planificador: El proceso no se encuentra en la cola de procesos nuevos.");
        }
        String nombre_Programa = bcp_Planificador.getNombre_Programa();
        LocalTime momento_Creacion = bcp_Planificador.get_momento_creacion();

        // 4. Seteal los datos de finalizacion.
        bcp_Proceso.setNombre_Programa(nombre_Programa);
        bcp_Proceso.set_momento_creacion(momento_Creacion);
        bcp_Proceso.set_momento_finalizacion(LocalTime.now());

        // 5. Eliminar el proceso de la cola de procesos nuevos.
        this.eliminar_Proceso_Nuevo(pPID);

        // 6. Agregar el proceso a la cola de procesos terminados.
        this.agregar_Proceso_Terminado(bcp_Proceso);

        // 7. Aqui iria la llamada a la funcion que elimina la memoria asignada a la BCP
        // y al proceso.
        this.controlador_Memoria.limpiar_Memoria_Proceso(pPID, Integer.parseInt(bcp_Proceso.getMem_End()));

    }

    // ##### Seccion para la manipulacion de los estados de los procesos. #####
    public void cambiar_Estado_Proceso_Nuevo() {

        System.out.println("Planificador: Cambiando el estado de los procesos nuevos a preparados.");

        int count_Iteraciones = 0;
        for (BCP bcp : this.cola_Procesos_Nuevos.values()) {

            String estado = "Preparado";
            if (count_Iteraciones == 0) {
                estado = "En Ejecuccion";
                count_Iteraciones++;
            }

            bcp.setEstado(estado);

            // Modificar el proceso en memoris.
            this.controlador_Memoria.actualizar_Estado_BCP(bcp.getPID(), estado);
        }

    }

    // ###### Seccion para la obtencion de 5 procesos y su estado para ####

    public Map<Integer, String> obtener_Estado_5_Procesos() {

        Map<Integer, String> procesos = new HashMap<>();

        int cant_Procesos = 0;

        // 1. Agregar los procesos terminados.
        for (BCP bcp : this.cola_Procesos_Terminados) {
            int pid = bcp.getPID();
            String estado = bcp.getEstado();
            procesos.put(pid, estado);

        }

        // 2. Obtenemos los procesos y su estado de la cola de nuevos.
        for (BCP bcp : this.cola_Procesos_Nuevos.values()) {
            int pid = bcp.getPID();
            String estado = bcp.getEstado();
            procesos.put(pid, estado);
            cant_Procesos++;
        }

        // 3. Agregamos los procesos y su estado de la cola de preparados (cuando
        // todavia son nombres de archivos).
        if (cant_Procesos < 5) {
            for (int i = 0; i < this.cola_Programas_Pendientes.size(); i++) {
                if (cant_Procesos == 5) {
                    break;
                }
                int pid = this.controlador_Memoria.get_Nuevo_PID();

                procesos.put(pid + i, "Nuevo");
                cant_Procesos++;
            }
        }

        // 3. Agregamos los procesos y su estado de la cola de listos.
        // for (int i = 0; i < this.cola_Procesos_Listos.size(); i++) {
        // int pid = this.cola_Procesos_Listos.get(i).getPID();
        // String estado = this.cola_Procesos_Listos.get(pid).getEstado();
        // procesos.put(pid, estado);
        // }

        // // 4. Agregamos los procesos y su estado de la cola de bloqueados.
        // for (int i = 0; i < this.cola_Procesos_Bloqueados.size(); i++) {
        // int pid = this.cola_Procesos_Bloqueados.get(i).getPID();
        // String estado = this.cola_Procesos_Bloqueados.get(pid).getEstado();
        // procesos.put(pid, estado);
        // }

        return procesos;
    }

    // ----####### Seccion para los algortimos de planificacion ########----
    public void FSFS_Planificador(Memoria pMemoria_Principal, Almacenamiento pMemoria_Secundaria,
            int pTiempo_Total_CPU) {

        System.out.println("Planificador: Iniciando el planificador FCFS.");
        // 1. Los primero es ver si en la cola de procesos que podemos aceptar hay
        // espacio para decidir agregar un nuevo proceso.

        // Recorrer 5 veces para agregar los 5 procesos
        for (int i = this.cola_Procesos_Nuevos.size(); i < 5; i++) {

            if (this.cola_Programas_Pendientes.isEmpty()) {
                System.out.println("Planificador: No hay programas pendientes...");
                break;
            }
            System.out.println("Planificador: Agregando proceso: " + i);
            // 2. Si hay espacio seleccionamos el primer elemento de la cola de programas.
            String nombre_Programa = this.cola_Programas_Pendientes.get(0);

            // 3. Validar que ese programa no haya sido cargado

            // 3. Obtener el codigo del programa.
            Codigo_ASM codigo = this.obtener_Programa_Almacenamiento(pMemoria_Secundaria, nombre_Programa);
            System.out.println("Planificador: Codigo del programa: " + codigo.getContador_Intrucciones());
            // 4. Comprobar si hay espacio para agregar el proceso en la memoria principal.
            // Se valida la parte del OS y la del usuario.

            int espacio_Necesario_Programa = codigo.getContador_Intrucciones();
            int espacio_Necesario_BCP = 24;

            int hay_esoacio_OS = this.controlador_Memoria
                    .validar_Espacio_Disponible_Usuario(espacio_Necesario_Programa);

            if (pMemoria_Principal.validar_Espacio_Disponible_OS(espacio_Necesario_BCP) == 0
                    && hay_esoacio_OS != 0) {

                // &&
                // pMemoria_Principal.validar_Espacio_Disponible_Usuario(espacio_Necesario_Programa)
                // == 0
                System.out.println("Planificador: PASS 1");
                // 5. Obtener el PID que tendra el nuevo proceso.
                int pid = pMemoria_Principal.asignar_Nuevo_PID_Proceso();

                System.out.println("Planificador: PASS 2");

                int tiempo_Estimado = Calcular_Tiempo_Estimado_Programa.calcular_Tiempo_Estimado(codigo);

                System.out.println("Planificador: PASS 3 -> Duracion estimada: " + tiempo_Estimado);

                // 6. General el nuevo BCP en la seccion de OS.
                // BCP nuevo_BCP = this.generar_Nueva_BCP(codigo, nombre_Programa, i, 1, pid,
                // 0);

                // Posicion en memoria virtual
                int pos_MV = this.controlador_Memoria.get_Pos_Actual_MV();

                pMemoria_Principal.iniciar_Memoria_BCP(espacio_Necesario_Programa, 1, pid + 1, 1, pTiempo_Total_CPU,
                        tiempo_Estimado, pos_MV);

                System.out.println("Planificador: PASS 4");

                // 7. Asignar el programa a la memoria principal.
                // pMemoria_Principal.asignar_memoria_a_programa(codigo);
                this.controlador_Memoria.asignar_Memoria_Programa(codigo);

                System.out.println("Planificador: PASS 5");

                // Aqui se deberia modificar la siguiente BCP que sigue a esta BCP
                int pid_Siguiente = 0; // Variable para saber cual sera la siguiente BCP.

                // Este seria el caso para este algortimo, en otros algortimos
                // la eleccion se hace de manera diferente.
                if (i == 4) {
                    pid_Siguiente = -1;
                } else {
                    pid_Siguiente = pid + 1;
                }

                pMemoria_Principal.modificar_Enlace_Siguiente_BCP(pid, pid_Siguiente);

                // 8. Obtener la BCP creada.
                BCP nuevo_BCP = pMemoria_Principal.obtener_Datos_BCP(pid);

                // 8.1. Agregar datos adicionales que solo tiene la BCP del planificador.

                // -> Nombre del archivo.
                nuevo_BCP.setNombre_Programa(nombre_Programa);

                // -> Hora:Minuto:Segndos de creacion.
                nuevo_BCP.set_momento_creacion(LocalTime.now());

                System.out.println("Planificador: PASS 6");

                // 9. Agregar el programa a la cola de procesos nuevos.
                this.agregar_Proceso_Nuevo(pid, nuevo_BCP);

                System.out.println("Planificador: PASS 7");

                // 10. Eliminar el programa de la cola de programas pendientes.
                this.eliminar_Programa_Pendiente(nombre_Programa);

                System.out.println("Planificador: PASS 8");

            } else {
                break;
            }
        }
    }

    // ####### Seccion para funciones auxiliaras de la parte de los algortimos
    // #######

    // -> FCFS:
    public void modificar_Siguiente_BCP_FCFS(Memoria pMemoria_Principal) {

        // Recorrermos la cola de procesos nuevos y les asignamos el siguiente PID a
        // todos, menos
        // al del final de la cola, ya que ese esta indefinido.
        for (int i = 0; i < 5; i++) {

            BCP bcp = this.cola_Procesos_Nuevos.get(i);
            int PID_Proceso_Actual = Integer.valueOf(bcp.getPID());

            // Aqui se deberia modificar la siguiente BCP que sigue a esta BCP
            int pid_Siguiente = 0; // Variable para saber cual sera la siguiente BCP.

            // Este seria el caso para este algortimo, en otros algortimos
            // la eleccion se hace de manera diferente.
            if (i == 4) {
                pid_Siguiente = -1;
            } else {
                pid_Siguiente = PID_Proceso_Actual + 1;
            }
            pMemoria_Principal.modificar_Enlace_Siguiente_BCP(PID_Proceso_Actual, pid_Siguiente);
            // bcp.setProximo_Proceso(String.valueOf(pid_Siguiente));

            BCP bcp_Actual = pMemoria_Principal.obtener_Datos_BCP(PID_Proceso_Actual);

            bcp_Actual.setNombre_Programa(bcp.getNombre_Programa());
            this.cola_Procesos_Nuevos.put(PID_Proceso_Actual, bcp_Actual);

            // this.controlador_Memoria.actualizar_Datos_BCP(bcp, PID_Proceso_Actual);
        }

    }

    // ###### Seccion para validaciones del programa ######

    /**
     * Funcion: hay_Procesos_Nuevos
     * Descripcion: Metodo que permite validar si hay procesos nuevos en la cola de
     * procesos nuevos.
     * 
     * @return true si hay procesos nuevos, false si no los hay.
     */
    public boolean hay_Procesos_Nuevos() {

        if (this.cola_Procesos_Nuevos.isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * Funcion: comprobar_Finalizacion_Proceso
     * Descripcion: Metodo que permite comprobar la finalizacion de un proceso.
     * 
     * @param pPID PID del proceso a comprobar.
     * @return Resultado de la comprobacion.
     */
    public int comprobar_Finalizacion_Proceso(int pPID) {
        // 1. Llamar a la funcion de memoria para verificar la finalizacion del proceso.
        int resultado = this.controlador_Memoria.comprobar_Finalizacion_Proceso(pPID);

        return resultado;
    }

    // ##### Seccion para mostrar datos en consola ######//#endregion

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
