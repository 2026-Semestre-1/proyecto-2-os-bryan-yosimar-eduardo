/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author edurg
 */
public class Memoria {

    private Map<Integer, String> Memoria_Principal;

    // Valores de tamaño para cada seccion de memoria.
    private int espacio_Total = 0;

    private int espacio_OS = 0;

    private int espacio_Usuario = 0;

    // Valores para saber la cantidad de espacio utilizado en cada seccion.

    private int espacio_Usado_OS = 0;

    private int espacio_Usado_Usuario = 0;

    // Valores para saber la posicion actual de la ultima escritura nueva.

    private int posicion_Actual_OS = 0;

    private int posicion_Actual_Usuario = 0;

    private static final int TAMANO_BCP = 26;

    public Memoria(int pTamano_Memoria) {
        // Crear el tamaño de memoria principal correspondiente a lo especificado en el
        // archivo de configuracion.
        this.Memoria_Principal = new HashMap<>(pTamano_Memoria);

        // Registrar los tamaños asignados a cada seccion.
        this.espacio_Total = pTamano_Memoria;

        this.espacio_OS = (int) (pTamano_Memoria * 0.2);

        this.espacio_Usuario = pTamano_Memoria - this.espacio_OS;

        // Incializar posiciones.

        // this.posicion_Actual_OS = 0;

        this.posicion_Actual_Usuario = this.espacio_OS;

        // iniciar_Memoria_Registros(); // -> Los regisstro deben estar como variables
        // en la parte del CPU.
    }

    /**
     * Obtiene la memoria principal.
     * 
     * @return La memoria principal.
     */
    public Map<Integer, String> getMemoria_Principal() {
        return Memoria_Principal;
    }

    /**
     * Obtiene el tamaño total de la memoria.
     * 
     * @return El tamaño total de la memoria.
     */
    public int getEspacio_Total() {
        return espacio_Total;
    }

    /**
     * Obtiene el tamaño del espacio del sistema operativo.
     * 
     * @return El tamaño del espacio del sistema operativo.
     */
    public int getEspacio_OS() {
        return espacio_OS;
    }

    /**
     * Obtiene el tamaño del espacio del usuario.
     * 
     * @return El tamaño del espacio del usuario.
     */
    public int getEspacio_Usuario() {
        return espacio_Usuario;
    }

    /**
     * Obtiene el espacio utilizado en el sistema operativo.
     * 
     * @return El espacio utilizado en el sistema operativo.
     */
    public int getEspacio_Usado_OS() {
        return espacio_Usado_OS;
    }

    /**
     * Obtiene el espacio utilizado en el usuario.
     * 
     * @return El espacio utilizado en el usuario.
     */
    public int getEspacio_Usado_Usuario() {
        return espacio_Usado_Usuario;
    }

    /**
     * Obtiene la posicion actual del sistema operativo.
     * 
     * @return La posicion actual del sistema operativo.
     */
    public int getPosicion_Actual_OS() {
        return posicion_Actual_OS;
    }

    /**
     * Obtiene la posicion actual del usuario.
     * 
     * @return La posicion actual del usuario.
     */
    public int getPosicion_Actual_Usuario() {
        return posicion_Actual_Usuario;
    }

    public void setPosicion_Actual_Usuario(int pPosicion) {
        this.posicion_Actual_Usuario = pPosicion;
    }

    public void setEspacio_Usado_Usuario(int pEspacio) {
        this.espacio_Usado_Usuario = pEspacio;
    }

    public void setPosicion_Actual_OS(int pPosicion) {
        this.posicion_Actual_OS = pPosicion;
    }

    public void setEspacio_Usado_OS(int pEspacio) {
        this.espacio_Usado_OS = pEspacio;
    }

    // ########### Seccion para la opbtencion de datos desde la memoria. ###########

    public String obtener_Instruccion(int pPosicion) {
        // Revisar por si vamos a ocupar funciones separadas para cceder a posiciones
        // no
        // autorizadas de memoria,
        // por eso de los casos de overflow.
        return Memoria_Principal.get(pPosicion);
    }

    // ########### Seccion para la insercion o actualizacion de datos en la memoria.
    // ###########

    /**
     * Agrega una instruccion a la memoria.
     * 
     * @param pPosicion    La posicion donde se agregara la instruccion.
     * @param pInstruccion La instruccion a agregar.
     */
    public void agregar_Instruccion_OS(int pPosicion, String pInstruccion) {
        Memoria_Principal.put(pPosicion, pInstruccion);
    }

    public void agregar_Instruccion_Usuario(int pPosicion, String pInstruccion) {
        Memoria_Principal.put(pPosicion, pInstruccion);
    }

    public void iniciar_Memoria_Registros() {
        Memoria_Principal.put(0, ((Integer) espacio_OS).toString()); // PC // -> Espezamos de donde termina el espacio
                                                                     // del OS.
        Memoria_Principal.put(1, "0000 0000 00000000"); // IR
        Memoria_Principal.put(2, "0"); // AC
        Memoria_Principal.put(3, "0"); // AX
        Memoria_Principal.put(4, "0"); // BX
        Memoria_Principal.put(5, "0"); // CX
        Memoria_Principal.put(6, "0"); // DX

        // Se agrega la ultima posicion escrita + 1 para registrar el que sigue.
        posicion_Actual_OS += 7;

        // Se registra la cantidad de espacio usado.
        espacio_Usado_OS += 7;
    }

    public int asignar_Nuevo_PID_Proceso() {

        // Se tendra que recorrer a partir de la posicion 7 de la memoria para ver cual
        // es el ultimo BCP agregado
        // y de esta manera obtener el PID de esta BCP y sumarle 1 para crear el PID de
        // la proxima BCP.

        int pid = 0;
        // El i += 10 es suponiendo que cada 10 espacios esta el PID
        // de una BCP
        // pero tambien hay que tener cuidado por si manejamos mas
        // cosas que BCP
        // en la parte del OS.
        for (int i = 0; i < posicion_Actual_OS; i += TAMANO_BCP) {
            if (Memoria_Principal.get(i) != null && !Memoria_Principal.get(i).equals("")) {
                pid = Integer.parseInt(Memoria_Principal.get(i));
            }
        }
        return pid + 1;

    }

    /**
     * Inicia la memoria para un nuevo proceso.
     * 
     * @param pTamano_Proceso El tamaño del proceso.
     * @return La posicion inicial del proceso.
     */
    public int iniciar_Memoria_BCP(int pTamano_Proceso, int pPrioridad, int pProximo_Proceso, int pCPU_Asignada,
            int pMomento_Llegada, int pDuracion_Estimada, int pos_Actual_MV) {

        // registrar los datos iniciales de un nuevo proceso en memoria, se crea la BCP
        // que en la seccion del
        // OS y se registran los datosinicales del proceso.

        // Asumimos que ya validamos el espacio disponibles tanto en la secion del OS
        // como en la del usuario
        // para agregar una nueva BCP y cargar el programa.

        int pid = asignar_Nuevo_PID_Proceso(); // Obtener el nuevo PID para esta BCP.

        // System.out.println("Memoria: Asignando nuevo PID: " + pid);
        // Registramos manualmente cada uno de las partes necesarias para la BCP.

        // System.out.println("Memoria: Posicion actual del OS: " + posicion_Actual_OS);
        // PID -> Identificador del proceso. #0
        Memoria_Principal.put(posicion_Actual_OS, ((Integer) pid).toString());

        // STATE -> Estado del proceso. #1
        Memoria_Principal.put(posicion_Actual_OS + 1, "NEW");

        // PRIORITY -> Prioridad del proceso. #2
        Memoria_Principal.put(posicion_Actual_OS + 2, ((Integer) pPrioridad).toString());

        if (this.validar_Espacio_Disponible_Usuario(pTamano_Proceso) == 0) {
            // Caso el que se asigna a la memoria principal.
            // MEM_INIT -> Puntero de memoria. -> Inicio de donde estan las instrucciones de
            // este proceso. #3
            Memoria_Principal.put(posicion_Actual_OS + 3, ((Integer) posicion_Actual_Usuario).toString());

            // MEM_END -> Fin de la memoria del proceso. #4
            Memoria_Principal.put(posicion_Actual_OS + 4,
                    ((Integer) (posicion_Actual_Usuario + pTamano_Proceso - 1)).toString());

            // PC -> Contador de programa. #5 -> Apuntamos por defecto a la primera posicion
            // del programa.
            Memoria_Principal.put(posicion_Actual_OS + 5, ((Integer) posicion_Actual_Usuario).toString());

        } else {
            // Caso en el que se va para la memoria virtual.

            // MEM_INIT -> Puntero de memoria. -> Inicio de donde estan las instrucciones de
            // este proceso. #3
            Memoria_Principal.put(posicion_Actual_OS + 3,
                    ((Integer) (this.getEspacio_Total() + pos_Actual_MV)).toString());

            // MEM_END -> Fin de la memoria del proceso. #4
            Memoria_Principal.put(posicion_Actual_OS + 4,
                    ((Integer) (this.getEspacio_Total() + pos_Actual_MV + pTamano_Proceso - 1)).toString());

            // PC -> Contador de programa. #5 -> Apuntamos por defecto a la primera posicion
            // del programa.
            Memoria_Principal.put(posicion_Actual_OS + 5,
                    ((Integer) (this.getEspacio_Total() + pos_Actual_MV)).toString());
        }

        // IR -> Registro de instruccion. #6
        Memoria_Principal.put(posicion_Actual_OS + 6, "0000 0000 00000000");

        // AC -> Acumulador. #7
        Memoria_Principal.put(posicion_Actual_OS + 7, "0");

        // AX -> Registro de uso general A. #8
        Memoria_Principal.put(posicion_Actual_OS + 8, "0");

        // BX -> Registro de uso general B. #9
        Memoria_Principal.put(posicion_Actual_OS + 9, "0");

        // CX -> Registro de uso general C. #10
        Memoria_Principal.put(posicion_Actual_OS + 10, "0");

        // DX -> Registro de uso general D. #11
        Memoria_Principal.put(posicion_Actual_OS + 11, "0");

        // IO_STATUS -> Lista de los archivos abiertos: Se guardan en una sola posicion
        // en memoria: Arhci1, Archi2, Archi3. #12
        Memoria_Principal.put(posicion_Actual_OS + 12, "NONE");

        // CPU asignada -> Se asigna la CPU a este proceso. #13
        Memoria_Principal.put(posicion_Actual_OS + 13, "1");

        // Momento en el que llega el proceso al planificador. #14
        Memoria_Principal.put(posicion_Actual_OS + 14, ((Integer) pMomento_Llegada).toString());

        // Momento en el que inicia la ejecucion del proceso. #15
        Memoria_Principal.put(posicion_Actual_OS + 15, "-1");

        // Momento en el que finaliza la ejecucion del proceso #16
        Memoria_Principal.put(posicion_Actual_OS + 16, "0");

        // Tiempo esperado de ejecucion #17
        // System.out.println("Duracion estimada: " + pDuracion_Estimada);
        Memoria_Principal.put(posicion_Actual_OS + 17, ((Integer) pDuracion_Estimada).toString());

        // Enlace al siguiente BCP #18
        Memoria_Principal.put(posicion_Actual_OS + 18,
                (pProximo_Proceso == -1) ? "NONE" : String.valueOf(pProximo_Proceso));

        // Reservar 5 posiciones al final del BCP para poder simular la pila. #19-23
        for (int i = 0; i < 5; i++) {
            Memoria_Principal.put(posicion_Actual_OS + 19 + i, "");
        }

        // Registro AH (1/2) y AL (1/2)
        Memoria_Principal.put(posicion_Actual_OS + 24, "0");
        Memoria_Principal.put(posicion_Actual_OS + 25, "0");

        // Actualizamos la posicion actual del OS y el espacio usado.
        this.posicion_Actual_OS += 26; // Se le suma uno ya que empezaremos a escribir a partie de esa posicion.
        this.espacio_Usado_OS += 26;

        return pid;
    }

    /**
     * Asigna memoria a un programa, se va recorriendo cada una de las instrucciones
     * de la list
     * y se les va asignando una posicion en la parte del usuario..
     * 
     * @param pCodigo_ASM El codigo ASM del programa.
     * @return 0: Si todo fue un exito | 1: Si se ingresa al area del OS | 2: Si
     *         sale del espacio de memoria total.
     */
    public int asignar_memoria_a_programa(Codigo_ASM pCodigo_ASM) {

        int pos_inicial = posicion_Actual_Usuario;

        int tamano_codigo = pCodigo_ASM.getContador_Intrucciones();

        if (pos_inicial + tamano_codigo <= espacio_Total) {

            // Sacar la lista completa de instrucciones.
            List<Instruccion> instrucciones = pCodigo_ASM.getInstrucciones();

            for (Instruccion instruccion_actual : instrucciones) {
                Memoria_Principal.put(pos_inicial, instruccion_actual.get_Intruccion_Completa());
                pos_inicial++;
                // System.out.println("Pass -> " + pos_inicial);

            }

            this.posicion_Actual_Usuario += tamano_codigo;
            this.espacio_Usado_Usuario += tamano_codigo;

            return 0; // Si se puede guardar el programa en memoria.
        } else {
            return 1; // No hay suficiente espacio para cargar el programa.
        }

    }

    // ############### Seccion para los metodos de modificacion de memoria:

    /**
     * Esta funcion se encarga de modificar el valor de una posicion especifica de
     * la memoria y validar si la posicion ingresada existe.
     * 
     */
    public int modificar_valor_en_memoria(int pPosicion, String pValor) {

        if (Memoria_Principal.containsKey(pPosicion)) {
            Memoria_Principal.replace(pPosicion, pValor);
            return 0;
        } else {
            return 1;
        }

    }

    public int actualizar_Registros_BCP(int pPID, CPU pCPU) {

        // Actualizar los registros de la BCP de un proceso.

        // 1. Primero buscar la posicion de la BCP del proceso mediante la PID.
        int posicion_BCP = buscar_Posicion_BCP(pPID);

        if (posicion_BCP == -1) {
            return 1; // No se encontro la BCP.
        }

        // 2. Actualizar los registros de la BCP.

        Memoria_Principal.put(posicion_BCP + 5, String.valueOf(pCPU.getPC())); // PC
        Memoria_Principal.put(posicion_BCP + 6, pCPU.getIR()); // IR
        Memoria_Principal.put(posicion_BCP + 7, String.valueOf(pCPU.getAC())); // AC
        Memoria_Principal.put(posicion_BCP + 8, String.valueOf(pCPU.getAX())); // AX
        Memoria_Principal.put(posicion_BCP + 9, String.valueOf(pCPU.getBX())); // BX
        Memoria_Principal.put(posicion_BCP + 10, String.valueOf(pCPU.getCX())); // CX
        Memoria_Principal.put(posicion_BCP + 11, pCPU.getDX()); // DX
        Memoria_Principal.put(posicion_BCP + 24, pCPU.getAH()); // AH
        Memoria_Principal.put(posicion_BCP + 25, pCPU.getAL()); // AL

        return 0;
    }

    public int actualizar_Estado_BCP(int pPID, String pEstado) {

        // Actualizar el estado de la BCP de un proceso.

        // 1. Primero buscar la posicion de la BCP del proceso mediante la PID.
        int posicion_BCP = buscar_Posicion_BCP(pPID);

        if (posicion_BCP == -1) {
            return 1; // No se encontro la BCP.
        }

        // 2. Actualizar el estado de la BCP.

        Memoria_Principal.put(posicion_BCP + 1, pEstado);
        return 0;
    }

    public int actualizar_Priodidad_BCP(int pPID, int pPriodidad) {

        // Actualizar la priodidad de la BCP de un proceso.

        // 1. Primero buscar la posicion de la BCP del proceso mediante la PID.
        int posicion_BCP = buscar_Posicion_BCP(pPID);

        if (posicion_BCP == -1) {
            return 1; // No se encontro la BCP.
        }

        // 2. Actualizar la priodidad de la BCP.

        Memoria_Principal.put(posicion_BCP + 2, String.valueOf(pPriodidad));
        return 0;
    }

    public int modificar_Lista_Archivos_BCP(int pPID, String pNuevo_Archivo) {

        // Actualizar la lista de archivos de la BCP de un proceso.

        // 1. Primero buscar la posicion de la BCP del proceso mediante la PID.
        int posicion_BCP = buscar_Posicion_BCP(pPID);

        if (posicion_BCP == -1) {
            return 1; // No se encontro la BCP.
        }

        // 2. Obtener los datos ya registrdos en en la lista de archivos.

        String lista_Archivos = Memoria_Principal.get(posicion_BCP + 12);

        if (lista_Archivos.equals("NONE")) {
            lista_Archivos = pNuevo_Archivo;
        } else {
            lista_Archivos += ", " + pNuevo_Archivo;
        }

        // 3. Actualizar la lista de archivos de la BCP.
        Memoria_Principal.put(posicion_BCP + 12, lista_Archivos);
        return 0;
    }

    public int modificar_Tiempo_Inicio_BCP(int pPID, int pTiempo_Inicio) {

        // Actualizar el tiempo de inicio de la BCP de un proceso.

        // 1. Primero buscar la posicion de la BCP del proceso mediante la PID.
        int posicion_BCP = buscar_Posicion_BCP(pPID);

        if (posicion_BCP == -1) {
            return 1; // No se encontro la BCP.
        }

        // 2. Actualizar el tiempo de inicio de la BCP.
        Memoria_Principal.put(posicion_BCP + 15, String.valueOf(pTiempo_Inicio));
        return 0;
    }

    public int modificar_Tiempo_Finalizacion_BCP(int pPID, int pTiempo_Finalizacion) {

        // Actualizar el tiempo empleado de la BCP de un proceso.

        // 1. Primero buscar la posicion de la BCP del proceso mediante la PID.
        int posicion_BCP = buscar_Posicion_BCP(pPID);

        if (posicion_BCP == -1) {
            return 1; // No se encontro la BCP.
        }

        // 2. Actualizar el tiempo de finalizacion de la BCP.
        Memoria_Principal.put(posicion_BCP + 16, String.valueOf(pTiempo_Finalizacion));
        return 0;
    }

    public int modificar_Enlace_Siguiente_BCP(int pPID, int pProximo_Proceso) {

        // Actualizar el enlace al siguiente BCP de un proceso.

        // 1. Primero buscar la posicion de la BCP del proceso mediante la PID.
        int posicion_BCP = buscar_Posicion_BCP(pPID);

        if (posicion_BCP == -1) {
            return 1; // No se encontro la BCP.
        }

        // 2. Actualizar el enlace al siguiente BCP.
        Memoria_Principal.put(posicion_BCP + 18, String.valueOf(pProximo_Proceso));
        return 0;
    }

    // ####### Espacio para las funciones de manipulacion de la pila de las BCP.
    // #######

    /**
     * Libera una posicion en memoria, se utiliza la direccion especifica de esa
     * instruccion.
     * 
     * @param pDireccion La direccion de la instruccion.
     * @return 0: Si todo fue un exito | 1: Si se ingresa al area del OS | 2: Si
     *         sale del espacio de memoria total.
     */
    public int liberar_Memoria(int pDireccion) { // Lo recomendo el autocompletado y me parecio util a futuro.
        if (pDireccion < espacio_OS) {
            return 1; // No se puede liberar el area del OS.
        }

        if (pDireccion < espacio_Total) {
            getMemoria_Principal().remove(pDireccion);
            return 0;
        } else {
            return 2; // No se puede liberar la memoria de esa direccion ya que no existe.
        }
    }

    /**
     * Funcion: liberar_Memoria_Proceso
     * 
     * Descripcion: Libera la memoria asignada a un proceso.
     * 
     * @param pPID (int): El PID del proceso al que se le liberara la memoria.
     * @return Entero positivo: Si todo fue un exito | -1: Si no se encontro la BCP
     *         del proceso.
     */
    public int liberar_Memoria_Proceso(int pPID) {

        // 1. Buscar la posicion de la BCP del proceso mediante la PID.
        int posicion_BCP = buscar_Posicion_BCP(pPID);

        if (posicion_BCP == -1) {
            return -1; // No se encontro la BCP.
        }

        // 2. Obtener la posicion inicial y final del proceso en la memoria de usuario.
        int posicion_Inicial = Integer.parseInt(Memoria_Principal.get(posicion_BCP + 3).trim());
        int posicion_Final = Integer.parseInt(Memoria_Principal.get(posicion_BCP + 4).trim());

        // 3. Limpiar la memoria del programa (espacio de usuario).
        for (int i = posicion_Inicial; i <= posicion_Final; i++) {
            Memoria_Principal.put(i, ""); // en lugar de remove()
        }

        // 4. Limpiar la memoria del bloque BCP (espacio del SO).
        for (int i = posicion_BCP; i < posicion_BCP + TAMANO_BCP; i++) {
            Memoria_Principal.put(i, "");
        }
        System.out.println("Memoria: Memoria liberada desde la posicion " + posicion_Inicial + " hasta la posicion "
                + posicion_Final + ".");
        return posicion_Final; // Si es exitoso, devolver la ultima posicion en la que se estuvo ocupado por el
                               // programa eliminado.
    }

    // ###### Seccion para la manipulacion de los datos de la pila de cada BCP.
    // #####

    public int agregar_Dato_Pila(int pPID, String pDato) {

        // 1. Buscar la posicion de la BCP del proceso mediante la PID.
        int posicion_BCP = buscar_Posicion_BCP(pPID);

        if (posicion_BCP == -1) {
            return 1; // No se encontro la BCP.
        }

        // 2. Recorrer los 5 espacios de la pila para encontrar uno vacio en el que se
        // pueda escribir.
        for (int i = posicion_BCP + 19; i <= posicion_BCP + 23; i++) {
            if (Memoria_Principal.get(i) == null || Memoria_Principal.get(i).equals("")) {
                Memoria_Principal.put(i, pDato);
                return 0; // Se pudo agregar el dato dentro de los margenes de la pila.
            }
        }
        return 2; // Overflow de la pila.

    }

    public Integer obtener_Dato_Pila(int pPID) {

        // 1. Buscar la posicion de la BCP del proceso mediante la PID.
        int posicion_BCP = buscar_Posicion_BCP(pPID);

        if (posicion_BCP == -1) {
            return null; // No se encontro la BCP.
        }

        // 2. Recorrer los 5 espacios de la pila empezando del 23 hasta al 19,
        // (recirrido de atras hacia adelante)
        // para obtener los datos que esten en el ultimo espacio escrito.
        for (int i = posicion_BCP + 23; i >= posicion_BCP + 19; i--) {
            if (Memoria_Principal.get(i) != null && !Memoria_Principal.get(i).equals("")) {
                String dato_Obtenido = Memoria_Principal.get(i);
                Memoria_Principal.put(i, "");
                return Integer.parseInt(dato_Obtenido.trim());
            }
        }
        return null; // Underflow de la pila.

    }

    // ############# Espacio para funciones auxiliares: #############

    public int buscar_Posicion_BCP(int pPID) {

        for (int i = 0; i < espacio_OS; i += TAMANO_BCP) {
            if (Memoria_Principal.get(i) != null && !Memoria_Principal.get(i).equals("")) {
                // System.out.println("Memoria: PID: " + Memoria_Principal.get(i) + "Posicion: "
                // + i);
                int pid = Integer.parseInt(Memoria_Principal.get(i).trim());
                if (pid == pPID) {
                    return i;
                }
            }
        }
        return -1;
    }

    public BCP obtener_Datos_BCP(int pPID) {

        // Obtener los datos de un BCP.

        // 1. Primero buscar la posicion de la BCP del proceso mediante la PID.
        int posicion_BCP = buscar_Posicion_BCP(pPID);

        // System.out.println("Memoria: Posicion de la BCP: " + posicion_BCP);

        if (posicion_BCP == -1) {
            return null; // No se encontro la BCP.
        }

        // 2. Obtener los datos de la BCP.

        int pid = Integer.parseInt(Memoria_Principal.get(posicion_BCP));
        String estado = Memoria_Principal.get(posicion_BCP + 1);
        String prioridad = Memoria_Principal.get(posicion_BCP + 2);
        String mem_init = Memoria_Principal.get(posicion_BCP + 3);
        String mem_end = Memoria_Principal.get(posicion_BCP + 4);
        String pc = Memoria_Principal.get(posicion_BCP + 5);
        String ir = Memoria_Principal.get(posicion_BCP + 6);
        String ac = Memoria_Principal.get(posicion_BCP + 7);
        String ax = Memoria_Principal.get(posicion_BCP + 8);
        String bx = Memoria_Principal.get(posicion_BCP + 9);
        String cx = Memoria_Principal.get(posicion_BCP + 10);
        String dx = Memoria_Principal.get(posicion_BCP + 11);
        String io_status = Memoria_Principal.get(posicion_BCP + 12);
        String cpu_asignada = Memoria_Principal.get(posicion_BCP + 13);
        String tiempo_llegada = Memoria_Principal.get(posicion_BCP + 14);
        String tiempo_inicio = Memoria_Principal.get(posicion_BCP + 15);
        String tiempo_finalizacion = Memoria_Principal.get(posicion_BCP + 16);
        String tiempo_ejecucion = Memoria_Principal.get(posicion_BCP + 17);
        String proximo_proceso = Memoria_Principal.get(posicion_BCP + 18);
        int[] pila = new int[5];
        for (int i = 0; i < 5; i++) {
            String dato = Memoria_Principal.get(posicion_BCP + 19 + i);
            if (dato == null || dato.equals("")) {
                pila[i] = 0;
            } else {
                pila[i] = Integer.parseInt(dato);
            }
        }

        String ah = Memoria_Principal.get(posicion_BCP + 24);
        String al = Memoria_Principal.get(posicion_BCP + 25);

        // System.out.println("Memoria: Datos de AH y AL: " + ah + " ::::: " + al);

        return new BCP(pid, estado, prioridad, mem_init, mem_end, pc, ir, ac, ax, bx, cx, dx, ah, al, io_status,
                cpu_asignada,
                tiempo_llegada, tiempo_inicio, tiempo_finalizacion, tiempo_ejecucion, proximo_proceso, pila);
    }

    // Funcion para obterner la BCP de un programa mediante la pos de inicio.
    public BCP obtener_Datos_BCP_Pos_Inicio(int pPosicion_Inicial) {

        // 1. Recorrer la memoria en la parte del OS.
        for (int i = 0; i < espacio_OS; i += TAMANO_BCP) {
            if (Memoria_Principal.get(i) != null && !Memoria_Principal.get(i).equals("")) {
                System.out.println("Memoria: PID: " + Memoria_Principal.get(i) + "Posicion: " + i);
                int pid = Integer.parseInt(Memoria_Principal.get(i).trim());
                if (Integer.parseInt(Memoria_Principal.get(i + 3)) == pPosicion_Inicial) {
                    return obtener_Datos_BCP(pid);
                }
            }
        }
        return null;
    }

    public List<String> obtener_Lista_Archivos_Proceso(int pPID) {

        List<String> lista_Archivos = new ArrayList<>();

        // 1. Buscar la posicion de la BCP del proceso mediante la PID.
        int posicion_BCP = buscar_Posicion_BCP(pPID);

        if (posicion_BCP == -1) {
            return null; // No se encontro la BCP.
        }

        String archivos = obtener_Instruccion(posicion_BCP + 12);
        // Los archivos estan guardados en este formato en memoria: Arhci1, Archi2,
        // Archi3.

        String[] archivos_Array = archivos.split(",");
        for (String archivo : archivos_Array) {
            lista_Archivos.add(archivo.trim());
        }
        return lista_Archivos;

    }

    // #### Seccion para validaciones del programa. ####
    public Integer validar_Salto_Programa(Integer pPosicion_Destino, int pPID_Actual) {

        // 1. Buscar la posicion de la BCP del proceso mediante la PID.
        int posicion_BCP = buscar_Posicion_BCP(pPID_Actual);

        if (posicion_BCP == -1) {
            return null; // No se encontro la BCP.
        }

        // 2. Obtener los datos de la BCP.
        int mem_init = Integer.parseInt(Memoria_Principal.get(posicion_BCP + 3));
        int mem_end = Integer.parseInt(Memoria_Principal.get(posicion_BCP + 4));

        // 3. Validar que la direccion de destino se encuentre dentro de los limites
        // del programa.
        // Si la direccion de destino es menor a la posicion actual,
        // significa que el programa esta intentando saltar a una posicion anterior.
        if (pPosicion_Destino < mem_init) {
            return 1; // Salto hacia atras.
        }
        // 2. Si la direccion de destino es mayor a la posicion actual,
        // significa que el programa esta intentando saltar a una posicion posterior.
        else if (pPosicion_Destino > mem_end) {
            return 2; // Salto hacia adelante.
        } else {
            return 0; // Salto valido.
        }

    }

    public int validar_Espacio_Disponible_OS(int pEspacio_Necesario) {
        // Se valida si hay espacio disponible en la memoria del sistema operativo.
        if (espacio_Usado_OS + pEspacio_Necesario <= espacio_OS) {
            return 0;
        } else {
            return 1;
        }
    }

    // Creo que para este tambien hay que tener en cuenta el espacio de la memoria
    // virtual.
    public int validar_Espacio_Disponible_Usuario(int pEspacio_Necesario) {
        // Se valida si hay espacio disponible en la memoria del usuario.
        if (espacio_Usado_Usuario + pEspacio_Necesario <= espacio_Usuario) {
            return 0;
        } else {
            return 1;
        }
    }

    public void mostrar_Memoria() {
        System.out.println("Mostrando memoria.\n");
        for (Map.Entry<Integer, String> entry : Memoria_Principal.entrySet()) {
            System.out.println("Posicion: " + entry.getKey() + " -> Instruccion: " + entry.getValue());
        }
    }
}
