/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Controlador.Utils.Calcular_Tiempo_Estimado_Programa;

/**
 *
 * @author edurg
 */
public class Planificador {
    // Cola que registra los programas que existen en el almacenamiento y los carga
    // todo.
    // se tiene que actualizar conforme se agrega un nuevo programa a la memoria.
    private List<String> cola_Programas_Pendientes;

    // Cola procesos los (5) procesos nuevos que se van a poder trabajar.
    private Map<Integer, BCP> cola_Procesos_Nuevos;

    // Cola procesos terminados, nada mas se registra el PID de esto, existen en
    // memoria.
    private List<Integer> cola_Procesos_Terminados;

    public Planificador() {
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

    public List<Integer> getCola_Procesos_Terminados() {
        return cola_Procesos_Terminados;
    }

    // #### Seccion para el agregado de datos a las colas #####
    public void agregar_Programa_Pendiente(String nombre) {
        this.cola_Programas_Pendientes.add(nombre);
    }

    public void agregar_Proceso_Nuevo(int pid, BCP bcp) {
        this.cola_Procesos_Nuevos.put(pid, bcp);
    }

    public void agregar_Proceso_Terminado(int pid) {
        this.cola_Procesos_Terminados.add(pid);
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
    public BCP generar_Nueva_BCP(Codigo_ASM pCodigo_ASM, String pNombre_Programa, int tiempo_Llegada, int pPrioridad,
            int pPID, int pCPU_Asignada) {

        int tiempo_Estimado = Calcular_Tiempo_Estimado_Programa.calcular_Tiempo_Estimado(pCodigo_ASM);

        // BCP nueva_BCP = new BCP(pPID, "Nuevo", String.valueOf(pPrioridad), "0", "0",
        // "0", "0", "0", "0", "0", "0", "0",
        // "0", String.valueOf(pCPU_Asignada), String.valueOf(tiempo_Llegada), "0", "0",
        // String.valueOf(tiempo_Estimado), "0", "-1", null);

        BCP nueva_BCP = new BCP(pPID, String.valueOf(pPrioridad), String.valueOf(pCPU_Asignada),
                String.valueOf(tiempo_Llegada), String.valueOf(tiempo_Estimado), pNombre_Programa,
                pCodigo_ASM.getContador_Intrucciones());

        // BCP(int pPID, String pEstado, String pPrioridad, String pMem_Init, String
        // pMem_End, String pPC, String pIR, String pAC,
        // String pAX, String pBX, String pCX, String pDX, String pIO_STATUS, String
        // pCPU_Asignada, String pTiempo_Llegada,
        // String pTiempo_Inicio, String pTiempo_Finalizacion, String pTiempo_Ejecucion,
        // String pProximo_Proceso, int[] pPila)
        return nueva_BCP;
    }

    public Codigo_ASM obtener_Programa_Almacenamiento(Almacenamiento pMemoria_Secundaria, String pNombre_Programa) {

        // 1. Obtener los indices de todos los archivos.
        Map<String, List<Integer>> indices_Programas = pMemoria_Secundaria.optener_Indices();

        // 2. Obtener los indices del programa que se ocupa.
        List<Integer> indice_Codigo = indices_Programas.get(pNombre_Programa);

        // 3. Obtener el codigo del programa.
        Codigo_ASM codigo = pMemoria_Secundaria.optener_Programa(indice_Codigo.get(0), indice_Codigo.get(1));

        return codigo;

    }

}
