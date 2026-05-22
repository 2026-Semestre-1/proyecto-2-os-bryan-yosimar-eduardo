/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Memoria;
import Modelo.Almacenamiento;
import Modelo.BCP;
import Modelo.CPU;
import Modelo.Codigo_ASM;
import Modelo.Configuracion;
import Controlador.Controlador_Archivos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author edurg
 */
public class Controlador_Principal {
    private Memoria memoria = null; // Memoria princiapl del programa.
    private Almacenamiento almacenamiento = null; // Almacenamiento de los programas.
    private CPU cpu1 = null; // CPU 1.
    // private Codigo_ASM codigo = null;

    private Controlador_Planificador planificador = null;

    private Controlador_Memoria controlador_Memoria = null;

    private int contador_ciclos = 0;

    private boolean programa_Iniciado = false;

    private boolean hay_interrupcion = false;

    /**
     * Constructor de la clase Control_Programa.
     */
    public Controlador_Principal() {

        // Ocupamos que cuando se inicie esta clase se creen todos los almacenamientos y
        // se cargue el archivo de configuracion.
        this.planificador = new Controlador_Planificador();

        cargar_configuracion();

    }

    public int cargar_configuracion() {
        Configuracion config = Controlador_Archivos
                .cargarConfiguracion(
                        "C:\\Users\\edurg\\OneDrive\\Escritorio\\Proyecto_1\\PC\\proyecto-1-Eduardo1105rg\\SIstema_Operativo_V1\\src\\main\\java\\Config\\Config_Mem.json");// SIstema_Operativo_V1/src/main/java/Config/Config_Mem.json
        // System.out.println(config.toString());

        if (config == null) {
            return 1; // Hubo un error
        }

        crear_almacenamiento(config.getAlmacenamiento(), config.getMemoria_Virtual(), 20);
        crear_memoria(config.getMemoria());

        // Creacion de los CPU.

        // En esta parte para trabajar con mas CPU se debe de agregar un for que
        // cree la cantidad de CPU que diga el archivo de configuracion.

        crear_CPU(config.getCant_CPU());

        return 0;
    }

    /**
     * Crea la memoria, se encarga de crear la memoria y asignarla a la CPU.
     * 
     * @param tamano_memoria El tamaño de la memoria.
     */
    public void crear_memoria(int tamano_memoria) {
        this.memoria = new Memoria(tamano_memoria);

        // Incializar el controlador de memoria.
        this.controlador_Memoria = new Controlador_Memoria(memoria, almacenamiento);

        // Pasarselo al planificador.
        this.planificador.setControlador_Memoria(controlador_Memoria);
    }

    public void crear_almacenamiento(int pTamano_total, int pTamano_memoria_virtual, int pTamano_indices) {
        this.almacenamiento = new Almacenamiento(pTamano_total, pTamano_memoria_virtual, pTamano_indices);
    }

    public void crear_CPU(int cant_CPU) {
        this.cpu1 = new CPU(1, memoria, controlador_Memoria);
    }

    /**
     * Obtiene la memoria.
     * 
     * @return La memoria.
     */
    public Memoria getMemoria() {
        return memoria;
    }

    /**
     * Obtiene el almacenamiento.
     * 
     * @return El almacenamiento.
     */
    public Almacenamiento getAlmacenamiento() {
        return almacenamiento;
    }

    /**
     * Carga el archivo ASM, se encarga de leer el archivo.
     * 
     * @param ruta La ruta del archivo ASM.
     * @return 0: Si todo fue un exito | 1: Si hubo un error.
     */
    public List<String> cargar_archivo(String ruta, String pNombre_Archivo) {

        List<String> errores = new ArrayList<>();
        // De deberia de recibir el nombre y la ruta,
        // Para de esta manera verificar que no se esten cargando dos programas con el
        // mismo nombre.
        // 1. Validar que el nombre del archivo no haya sido registrado anteriormente.
        if (almacenamiento.existe_Archivo(pNombre_Archivo)) {
            errores.add("1");
            errores.add("Error: Ya existe un archivo con ese nombre.");
            return errores; // Existe un archivo con ese nombre.
        }
        // 2. Leer el contenido del archivo.
        Codigo_ASM codigo = Controlador_Archivos.Cargar_Archivo_ASM(ruta);

        // 3. Validar que se pudo abrir y leer el archivo.
        if (codigo.isHay_errores()) {
            errores.add("2");
            errores.add("Error:" + codigo.getErrores().toString());
            return errores;
        }

        if (codigo.getContador_Intrucciones() == 0) {
            errores.add("3");
            errores.add("Error: El Archvivo esta vacio");
            return errores;
        }

        // 4. Asignar el espacio en almacenamiento y registrar el programa.
        // Estos tiene que cambiarse de lugar despues.
        asignar_Almacenamiento(codigo, pNombre_Archivo);

        // 5. Inicar la parte de cargar los archivos nos procesados en el planificador.
        // System.out.println("-> Se van a extraer los programas del almacenamiento..");
        this.planificador.extraer_Programas_Almacenamiento(almacenamiento);

        // 6. Iniciar el planificador con FCFS.
        this.planificador.FSFS_Planificador(memoria, almacenamiento,
                cpu1.getTiempo_CPU());

        //
        this.planificador.cambiar_Estado_Proceso_Nuevo();

        // System.out.println("Controlador Principal: Fin de la carga del archivo.");
        // 7. Comprobar si el programa ya fue iniciado.
        if (programa_Iniciado == false) {
            int inicio = this.planificador.get_PID_Primer_Proceso_Nuevo();
            if (inicio != -1) {

                // Iniciamos el despachador.
                iniciar_Despachador(inicio);

            } else {
                errores.add("4");
                errores.add("Error: No hay procesos nuevos para ejecutar.");
                return errores;
            }

            programa_Iniciado = true;

        }

        errores.add("0");
        errores.add("Exito al leer el archivo.");
        return errores;
    }

    public int asignar_Almacenamiento(Codigo_ASM codigo, String pNombre_Archivo) {

        // 1. Realizar la validacion del espacio de indices.
        if (this.almacenamiento.espacio_Disponible_Indices() == 0) {
            return 1; // No hay espacio en el espacio de indices.
        }

        // 2. Realiazar la validacion del espacio de programas.
        if (this.almacenamiento.espacio_Disponible_Programas() < codigo.getContador_Intrucciones()) {
            return 2; // No hay espacio en el espacio de programas.
        }

        return this.almacenamiento.asignar_Memoria_A_Programa(codigo, pNombre_Archivo);
    }

    /**
     * Asigna memoria, se encarga de asignar memoria al programa.
     * 
     * @return memoria del programa.
     */
    public int asignar_memoria(Codigo_ASM pCodigo) {
        return this.memoria.asignar_memoria_a_programa(pCodigo);
    }

    public void iniciar_Despachador(int pPID) {
        Control_Despachador.despachador(this.cpu1, this.memoria, pPID);

        // Una ves pasado por el despachador se puede cambiar su estado a en ejecucion.
        this.memoria.actualizar_Estado_BCP(pPID, "En Ejecucion");

        // Seteamos el PID del proceso actual en la CPU.
        this.cpu1.setPID_Proceso_Actual(pPID);
    }

    public boolean comprobar_Finalizacion_Proceso() {

        // 1. Optenemos el PID del proceso actual.
        int PID_actual = this.cpu1.getPID_Proceso_Actual();

        // 2. Iniciamos la comprobacion para ver si en el siguiente ciclo el proceso
        // va a tener un PC mayor al tamaño del espacio del programa.
        int proceso_Finalizado = this.controlador_Memoria.comprobar_Finalizacion_Proceso(PID_actual);

        if (proceso_Finalizado == 1) {
            // 3. El proceso ha finalizado.
            return true;

        } else if (proceso_Finalizado == -1) {
            // 4. No se encontro el proceso con el PID indicado.
            System.out.println("-> Controlador Principal: No se encontro el proceso con el PID indicado.");
            return true;
        } else {
            // 5. El proceso no ha finalizado.
            System.out.println("-> Controlador Principal: El proceso no ha finalizado.");
            return false;
        }

    }

    public void sincronizar_Datos_CPU_Memoria_BCP() {

        // 1. Optenemos el PID del proceso actual.
        int PID_actual = this.cpu1.getPID_Proceso_Actual();

        // 2. Actualizamos los datos de la BCP.
        memoria.actualizar_Registros_BCP(PID_actual, cpu1);

    }

    public int finalizacion_Proceso_FCFS(int pPID_Actual) {

        // 1. Sincronizamos por ultima vez los datos del proceso actual que esta
        // finalizando.
        cpu1.modificar_PC(-1);

        // int pid_Anterior = cpu1.getPID_Proceso_Actual();

        // Sincronizar los datos del la CPU y el BCP.
        this.sincronizar_Datos_CPU_Memoria_BCP();

        // 2. Finalizamos el proceso actual y traemos el siguiente de la cola.
        this.planificador.finalizacion_Procesos(memoria, pPID_Actual, this.cpu1.getTiempo_CPU());

        // 3. Inciamos el algoritmos de planificacion para volver a acomodar la lista.
        // Esta parte tendra que cambiar para cuando hayan mas algoritmos de
        // planificacion.
        this.planificador.FSFS_Planificador(memoria, almacenamiento, cpu1.getTiempo_CPU());

        // Reiniciar datos de la CPU.
        this.cpu1.reiniciar_Datos_CPU();

        if (this.planificador.hay_Procesos_Nuevos()) {
            System.out.println("--> Controlador Principal: Hay procesos nuevos");

            // 4. Obtenermos el PID del proximo proceso en la cola.
            int PID_siguiente = this.planificador.get_PID_Primer_Proceso_Nuevo();

            if (PID_siguiente != -1) {
                // 5. Iniciamos el despachador con el nuevo PID.
                iniciar_Despachador(PID_siguiente);
                this.programa_Iniciado = true;
                return 0;

            } else {

                // No hay procesos nuevos.
                this.programa_Iniciado = false;
                return 1;
            }
        } else {
            // 4. No hay procesos nuevos.
            this.programa_Iniciado = false;
            return 1;
        }

        // return 0; // 0:
    }

    // #### Seccion para la ejecucion de la CPU. ######
    /**
     * Ejecuta el programa, realiza una ejecucion completa de todas las
     * instrucciones.
     */
    public List<String> ejecutar() {

        if (!this.programa_Iniciado) {
            return null;
        }

        // List<String> salida = new ArrayList<>();

        if (!this.hay_interrupcion) {
            this.cpu1.reiniciar_Interrupciones();
            this.cpu1.ejecutar_Siguiente_Instruccion();
            this.sincronizar_Datos_CPU_Memoria_BCP();
            return this.procesar_Interrupciones(cpu1.getPID_Proceso_Actual());

        } else {
            return null;
        }

        // return salida;
    }

    /**
     * Ejecuta el programa, realiza una ejecucion paso a paso de las instrucciones.
     */
    public List<String> paso_a_paso() {
        // Este se ejecuta una sola vez por cada vez que se precione el boton.
        if (!this.programa_Iniciado) {
            return null;
        }

        // Comprobar si el proceso actual no ha finalizado
        if (!this.comprobar_Finalizacion_Proceso()) {

            if (!this.hay_interrupcion) {
                this.cpu1.reiniciar_Interrupciones();
                this.cpu1.ejecutar_Siguiente_Instruccion();
                this.sincronizar_Datos_CPU_Memoria_BCP();
                return this.procesar_Interrupciones(cpu1.getPID_Proceso_Actual());
            }

            return null;

        } else {

            // Finalizamos el proceso.
            finalizacion_Proceso_FCFS(cpu1.getPID_Proceso_Actual());

            // Reiniciar los datos de la CPU.
            // this.cpu1.reiniciar_Datos_CPU();

            return null;

        }

    }

    // Funcion para el procesamiento de interrupciones.
    public List<String> procesar_Interrupciones(int pPID_Actual) {
        List<String> salida = new ArrayList<>();

        // 0: Sin interrupcion
        // 1: Error Encontrado
        // 2: Leer Teclado
        // 3: Imprimir Pantalla
        // 4: Salir -> Por la instruccion INT 20H

        if (cpu1.isError_Encontrado()) {
            // Mostrar el mensaje de error y pasamos al siguiente proceso.
            salida.add(String.valueOf(1));
            salida.add(this.cpu1.getDescripcion_Error());
            this.cpu1.reiniciar_Datos_CPU();
            this.finalizacion_Proceso_FCFS(pPID_Actual);
            // this.hay_interrupcion = true;
            return salida;

        } else if (cpu1.isLeer_Teclado()) {
            //
            salida.add(String.valueOf(2));
            this.hay_interrupcion = true;
            return salida;

        } else if (cpu1.isImprimir_Pantalla()) {
            //
            salida.add(String.valueOf(3));
            salida.add(this.cpu1.getDX());
            // this.hay_interrupcion = true;
            return salida;

        } else if (cpu1.isProceso_Finalizado()) {
            salida.add(String.valueOf(4));
            this.finalizacion_Proceso_FCFS(pPID_Actual);
            return salida;

        } else {

            String valor = String.valueOf(0);
            salida.add(valor);

            return salida;
        }

    }

    // ####### Funciones para el manejo de las interrupciones #######
    public void leer_Teclado(int pDato) {

        System.out.println("Dato: " + pDato);
        // Inicia la lectura del teclado.
        this.cpu1.leer_Entrada_Teclado(pDato);
        this.hay_interrupcion = false;

        this.sincronizar_Datos_CPU_Memoria_BCP();
    }

    public void procesar_Finalizacion_Proceso() {

        // Finalizamos el proceso.
        finalizacion_Proceso_FCFS(cpu1.getPID_Proceso_Actual());

        // Reiniciar los datos de la CPU.
        // this.cpu1.reiniciar_Datos_CPU();

    }

    // ######## Seccion para la ontencion de datos desde la interfaz #######

    public BCP obtener_Datos_BCP_Actual() {
        int pid_ProcesoActual = this.cpu1.getPID_Proceso_Actual();
        System.out.println("-> PID Proceso Actual: " + pid_ProcesoActual);
        return this.memoria.obtener_Datos_BCP(pid_ProcesoActual);
    }

    public Map<Integer, String> getLista_Proceso() {
        return this.planificador.obtener_Estado_5_Procesos();
    }

    public List<BCP> getLista_Procesos_Terminados() {
        return this.planificador.getCola_Procesos_Terminados();
    }

    // ###### Seccion de validaciones ######

    public boolean hay_Interrupcion() {
        return this.hay_interrupcion;
    }

    public boolean hay_Procesos_Nuevos() {
        return this.planificador.hay_Procesos_Nuevos();
    }

    /**
     * Limpia el programa, elimina los datos almacenados actualmente.
     */
    public void limpiar() {
        this.memoria = null;
        this.almacenamiento = null;
        this.cpu1 = null;
        this.controlador_Memoria = null;
        this.planificador = null;
        this.contador_ciclos = 0;
        this.programa_Iniciado = false;
    }

    /**
     * Reinicia el programa, se encarga de reiniciar el programa.
     */
    public void reiniciar_programa() {
        this.memoria = null;
        this.cpu1 = null;
        this.almacenamiento = null;
        this.controlador_Memoria = null;
        this.planificador = null;
        this.contador_ciclos = 0;
        this.programa_Iniciado = false;
        this.hay_interrupcion = false;

        // Iniciamos la carga de la configuracion inicial.
        this.planificador = new Controlador_Planificador();

        cargar_configuracion();
    }

    public void activar_Espera() {
        this.cpu1.set_Espera(3);
    }

}
