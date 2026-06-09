package kernel;

import model.Memoria;
import model.MemoriaPaginada;
import model.Almacenamiento;
import model.BCP;
import model.CPU;
import model.Codigo_ASM;
import dto.SnapshotSistema;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import kernel.Controlador_MemoriaParticionada;
import Memoria.Modelo.*;

import Config.Configuracion;
import Config.ConfigParticion;
import Config.ConfigPaginacion;
import Config.ConfigKernel;
import kernel.planificacion.*;

public class NucleoSO {
    private Memoria memoria = null;
    private Almacenamiento almacenamiento = null;
    private List<CPU> cpus = new ArrayList<>();
    private Planificador planificador = null;
    private GestorMemoria controlador_Memoria = null;
    private MemoriaPaginada memoriaPaginada = null;
    private int contador_ciclos = 0;
    private boolean programa_Iniciado = false;
    private boolean hay_interrupcion = false;
    private Controlador_MemoriaParticionada controlador_MemoriaParticionada;
    private ControladorMemoriaBuddy controladorBuddy;

    public NucleoSO() {
        inicializarPlanificador();
        this.controlador_MemoriaParticionada = new Controlador_MemoriaParticionada();
    }

    private void inicializarPlanificador() {
        this.planificador = new Planificador();

        // Esta árte se pasa a la funcionalidad de abajo para que el
        // usuario pueda elegir el algoritmo de planificacion.
        // this.planificador.setAlgoritmoPlanificacion(new AlgoritmoFCFS());
    }

    // Seccion para la configuracion de los algoritmos de planificacion y el quantum
    // establecido.

    /**
     * Configura el algoritmo de planificación en el planificador.
     * 
     * @param nombreAlgoritmo Nombre del algoritmo seleccionado ("FCFS", "SJF",
     *                        etc.)
     */
    public void configurarAlgoritmoPlanificacion(String nombreAlgoritmo) {
        if (this.planificador == null)
            return;

        IAlgoritmoPlanificacion alg;
        switch (nombreAlgoritmo) {
            case "FCFS":
                alg = new AlgoritmoFCFS();
                break;
            case "SJF":
                alg = new AlgoritmoSJF();
                break;
            case "RR":
                alg = new AlgoritmoRR();
                break;
            case "SRR":
                alg = new AlgoritmoSRR();
                break;
            case "SRT":
                alg = new AlgoritmoSRT();
                break;
            case "HRRN":
                alg = new AlgoritmoHRRN();
                break;
            case "Lottery":
                alg = new AlgoritmoLottery();
                break;
            default:
                alg = new AlgoritmoFCFS();
                break;
        }
        this.planificador.setAlgoritmoPlanificacion(alg);
        System.out.println("[PLANIFICADOR] Algoritmo cambiado a: " + nombreAlgoritmo);
    }

    /**
     * Configura el valor del Quantum en el planificador.
     * 
     * @param quantum Valor del quantum
     */
    public void configurarQuantum(int quantum) {
        if (this.planificador != null) {
            this.planificador.setQuantum(quantum);
            System.out.println("[PLANIFICADOR] Quantum configurado a: " + quantum);
        }
    }

    public void configurarMemoria(String tipoMemoria) {
        inicializarPlanificador();
        this.programa_Iniciado = false;
        this.hay_interrupcion = false;
        this.contador_ciclos = 0;
        switch (tipoMemoria) {
            case "Paginacion":
                Configuracion config = GestorArchivos.cargarConfiguracion();
                if (config == null) {
                    System.out.println("Error: No se pudo cargar la configuracion.");
                    return;
                }
                crear_almacenamiento(config.getAlmacenamiento(), config.getMemoria_Virtual(), 20);
                crear_memoriaParticionada(config.getMemoria());
                this.controlador_Memoria = new GestorMemoria(memoria, almacenamiento, memoriaPaginada, "Paginacion");
                this.planificador.setControlador_Memoria(controlador_Memoria);
                crear_CPU(config.getCant_CPU());
                break;
            case "Normal":
                this.memoriaPaginada = null;
                cargar_configuracion();
                break;

            case "ParticionIgual":
                this.memoriaPaginada = null;
                Configuracion config2 = GestorArchivos.cargarConfiguracion();
                if (config2 == null) {
                    System.out.println("Error: No se pudo cargar la configuracion.");
                    return;
                }
                crear_almacenamiento(config2.getAlmacenamiento(), config2.getMemoria_Virtual(), 20);
                crear_memoriaParticionFijajIgual(config2.getMemoria());
                this.controlador_Memoria = new GestorMemoria(memoria, almacenamiento, controlador_MemoriaParticionada,
                        "ParticionIgual");
                this.planificador.setControlador_Memoria(controlador_Memoria);
                crear_CPU(config2.getCant_CPU());
                break;

            case "ParticionIgualDinamica":
                this.memoriaPaginada = null;
                Configuracion config3 = GestorArchivos.cargarConfiguracion();
                if (config3 == null) {
                    System.out.println("Error: No se pudo cargar la configuracion.");
                    return;
                }
                crear_almacenamiento(config3.getAlmacenamiento(), config3.getMemoria_Virtual(), 20);
                crear_memoriaParticionFijaTamanosDimanicos(config3.getMemoria());
                this.controlador_Memoria = new GestorMemoria(memoria, almacenamiento, controlador_MemoriaParticionada,
                        "ParticionIgualDinamica");
                this.planificador.setControlador_Memoria(controlador_Memoria);
                crear_CPU(config3.getCant_CPU());
                break;

            case "Dinamica":
                this.memoriaPaginada = null;
                Configuracion config4 = GestorArchivos.cargarConfiguracion();
                if (config4 == null) {
                    System.out.println("Error: No se pudo cargar la configuracion.");
                    return;
                }
                crear_almacenamiento(config4.getAlmacenamiento(), config4.getMemoria_Virtual(), 20);
                crear_memoriaDinamica(config4.getMemoria());
                this.controlador_Memoria = new GestorMemoria(memoria, almacenamiento,
                        controlador_MemoriaParticionada, "Dinamica");
                this.planificador.setControlador_Memoria(controlador_Memoria);
                crear_CPU(config4.getCant_CPU());
                break;

            case "Buddy":
                this.memoriaPaginada = null;
                Configuracion config5 = GestorArchivos.cargarConfiguracion();
                if (config5 == null) {
                    System.out.println("Error: No se pudo cargar la configuracion.");
                    return;
                }
                crear_almacenamiento(config5.getAlmacenamiento(), config5.getMemoria_Virtual(), 20);
                this.memoria = new Memoria(config5.getMemoria());
                this.memoria.soloKernel();
                this.controlador_Memoria = new GestorMemoria(memoria, almacenamiento,
                        controlador_MemoriaParticionada, "Buddy");
                controladorBuddy = new ControladorMemoriaBuddy();
                controladorBuddy.inicializar(memoria.getPosicion_Actual_Usuario(),
                        memoria.getEspacio_Usuario(), config5.getMemoria());
                this.controlador_Memoria.setControladorBuddy(controladorBuddy);
                this.planificador.setControlador_Memoria(controlador_Memoria);
                crear_CPU(config5.getCant_CPU());
                break;

        }
    }

    public void crear_memoriaParticionada(int tamano_memoria) {
        this.memoria = new Memoria(tamano_memoria);
        this.memoria.soloKernel();
        ConfigPaginacion configPaginacion = GestorArchivos.cargarConfigPaginacion();
        int pageSize = (configPaginacion != null) ? configPaginacion.getPaginacion() : 16;
        int cantidadFrames = memoria.getEspacio_Usuario() / pageSize;
        this.memoriaPaginada = new MemoriaPaginada(pageSize, cantidadFrames);
        this.memoriaPaginada.inicializar(memoria.getEspacio_OS());
        this.memoriaPaginada.setMemoriaPrincipal(memoria.getMemoria_Principal());
        this.memoriaPaginada.setMemoriaSecundaria(almacenamiento.getMemoria_Secundaria());
        this.memoriaPaginada.setPosicionMV(almacenamiento.getPosicion_Memoria_Virtual());
    }

    public void crear_memoriaParticionFijajIgual(int tamanoMemoria) {
        this.memoria = new Memoria(tamanoMemoria);
        int posInicioUsuario = memoria.getPosicion_Actual_Usuario();
        int espacioUsuario = memoria.getEspacio_Usuario();
        this.memoria.soloKernel();
        ConfigParticion configPart = GestorArchivos.cargarConfigParticion();
        int tamanoFijo = (configPart != null) ? configPart.getEstatica() : espacioUsuario / 4;
        controlador_MemoriaParticionada.inicializarParticionesFijasIguales(posInicioUsuario, tamanoMemoria, tamanoFijo);
    }

    public void crear_memoriaParticionFijaTamanosDimanicos(int tamanoMemoria) {
        this.memoria = new Memoria(tamanoMemoria);
        int posInicioUsuario = memoria.getPosicion_Actual_Usuario();
        int espacioUsuario = memoria.getEspacio_Usuario();
        this.memoria.soloKernel();
        try {
            ConfigParticion configPart = GestorArchivos.cargarConfigParticion();
            List<Integer> porcentajes = (configPart != null) ? configPart.getDinamica()
                    : Arrays.asList(8, 12, 17, 25, 38);
            controlador_MemoriaParticionada.inicializarParticionesFijasDistribucion(posInicioUsuario, espacioUsuario,
                    tamanoMemoria, porcentajes);
        } catch (Exception e) {
            System.out.println("Error al inicializar particiones fijas dinamicas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void crear_memoriaDinamica(int tamanoMemoria) {
        this.memoria = new Memoria(tamanoMemoria);
        this.memoria.soloKernel();
    }

    public int cargar_configuracion() {
        Configuracion config = GestorArchivos.cargarConfiguracion();
        if (config == null) {
            return 1;
        }
        crear_almacenamiento(config.getAlmacenamiento(), config.getMemoria_Virtual(), 20);
        crear_memoria(config.getMemoria());
        crear_CPU(config.getCant_CPU());
        return 0;
    }

    public void crear_memoria(int tamano_memoria) {
        this.memoria = new Memoria(tamano_memoria);
        this.controlador_Memoria = new GestorMemoria(memoria, almacenamiento, "Normal");
        this.planificador.setControlador_Memoria(controlador_Memoria);
    }

    public void crear_almacenamiento(int pTamano_total, int pTamano_memoria_virtual, int pTamano_indices) {
        this.almacenamiento = new Almacenamiento(pTamano_total, pTamano_memoria_virtual, pTamano_indices);
    }

    public void crear_CPU(int cant_CPU) {
        this.cpus.clear();
        for (int i = 1; i <= cant_CPU; i++) {
            this.cpus.add(new CPU(i, memoria, controlador_Memoria));
        }
        this.planificador.setMaxProcesosSimultaneos(cant_CPU * 5);
    }

    public Memoria getMemoria() {
        return memoria;
    }

    public MemoriaPaginada getMemoriaPaginada() {
        return memoriaPaginada;
    }

    public Almacenamiento getAlmacenamiento() {
        return almacenamiento;
    }

    private CPU obtenerCpuPorPID(int pid) {
        for (CPU cpu : this.cpus) {
            if (cpu.getPID_Proceso_Actual() == pid) {
                return cpu;
            }
        }
        return null;
    }

    private CPU obtenerCpuLibre() {
        for (CPU cpu : this.cpus) {
            if (cpu.getPID_Proceso_Actual() <= 0) {
                return cpu;
            }
        }
        return null;
    }

    public List<String> cargar_archivo(String ruta, String pNombre_Archivo) {
        List<String> errores = new ArrayList<>();
        if (almacenamiento.existe_Archivo(pNombre_Archivo)) {
            errores.add("0");
            errores.add("El archivo ya existe en almacenamiento.");
            return errores;
        }
        Codigo_ASM codigo = GestorArchivos.Cargar_Archivo_ASM(ruta);
        if (codigo == null) {
            errores.add("5");
            errores.add("Error: No se pudo leer el archivo.");
            return errores;
        }
        if (codigo.isHay_errores()) {
            errores.add("2");
            errores.add("Error:" + codigo.getErrores().toString());
            return errores;
        }
        if (codigo.getContador_Intrucciones() == 0) {
            errores.add("3");
            errores.add("Error: El archivo esta vacio");
            return errores;
        }
        int res = asignar_Almacenamiento(codigo, pNombre_Archivo);
        if (res != 0) {
            errores.add("6");
            errores.add("Error: No hay espacio en almacenamiento.");
            return errores;
        }
        System.out.println("[DEBUG UPLOAD] Archivo=" + pNombre_Archivo
                + " almacenado con " + codigo.getContador_Intrucciones() + " instrucciones.");
        errores.add("0");
        errores.add("Exito al almacenar el archivo.");
        return errores;
    }

    public List<String> listarArchivos() {
        if (almacenamiento == null)
            return new ArrayList<>();
        return almacenamiento.obtenerNombresArchivos();
    }

    public Codigo_ASM obtenerPrograma(String nombreArchivo) {
        if (almacenamiento == null || planificador == null)
            return null;
        return this.planificador.obtener_Programa_Almacenamiento(almacenamiento, nombreArchivo);
    }

    public int crearProceso(String nombreArchivo) {
        if (memoria == null || almacenamiento == null || controlador_Memoria == null) {
            return -1;
        }
        Codigo_ASM codigo = this.planificador.obtener_Programa_Almacenamiento(almacenamiento, nombreArchivo);
        if (codigo == null || codigo.getContador_Intrucciones() == 0) {
            return -2;
        }
        String nombreInstancia = this.planificador.getSiguienteNombreInstancia(nombreArchivo);
        int tiempoCPU = cpus.isEmpty() ? 0 : cpus.get(0).getTiempo_CPU();
        boolean creado = this.planificador.crearProcesoEnMemoria(nombreInstancia, codigo,
                memoria, controlador_Memoria, tiempoCPU);
        if (!creado) {
            this.planificador.agregar_Programa_Pendiente(nombreArchivo);
            return 0;
        }
        // this.planificador.cambiar_Estado_Proceso_Nuevo();
        // if (!programa_Iniciado) {

        // // Aqui se esta asignando solo cuando se empieza el programa
        // int inicio = this.planificador.seleccionarSiguiente();
        // if (inicio != -1) {
        // iniciar_Despachador(inicio);
        // programa_Iniciado = true;
        // }
        // }
        CPU cpu_libre = obtenerCpuLibre();
        if (cpu_libre != null) {
            int pid = this.planificador.seleccionarSiguiente();
            if (pid != -1) {
                System.out.println("-> Kernel: Asignando CPU " + cpu_libre.getNumero_CPU() + " al proceso " + pid);

                iniciar_Despachador(pid, cpu_libre); // versión sobrecargada que recibe CPU destino

                programa_Iniciado = true;
            }
        }
        return 0;
    }

    public int asignar_Almacenamiento(Codigo_ASM codigo, String pNombre_Archivo) {
        if (this.almacenamiento.espacio_Disponible_Indices() == 0) {
            return 1;
        }
        if (this.almacenamiento.espacio_Disponible_Programas() < codigo.getContador_Intrucciones()) {
            return 2;
        }
        return this.almacenamiento.asignar_Memoria_A_Programa(codigo, pNombre_Archivo);
    }

    public int asignar_memoria(Codigo_ASM pCodigo) {
        return this.memoria.asignar_memoria_a_programa(pCodigo);
    }

    public void iniciar_Despachador(int pPID, CPU pCPU) {
        // CPU cpu = obtenerCpuLibre();
        // if (cpu == null) {
        // cpu = this.cpus.get(0);
        // }
        Despachador.despachador(pCPU, this.memoria, pPID);
        this.memoria.actualizar_Estado_BCP(pPID, "En Ejecucion");
        pCPU.setPID_Proceso_Actual(pPID);
        sincronizar_Datos_Memoria_BCP_Planificador(pPID);
    }

    public boolean comprobar_Finalizacion_Proceso_CPU(CPU pCpu) {
        if (pCpu == null)
            return false;

        int PID_actual = pCpu.getPID_Proceso_Actual();
        int proceso_Finalizado = this.controlador_Memoria.comprobar_Finalizacion_Proceso(PID_actual);
        if (proceso_Finalizado == 1) {
            return true;
        } else if (proceso_Finalizado == -1) {
            System.out.println("-> Controlador Principal: No se encontro el proceso con el PID indicado.");
            return true;
        } else {
            System.out.println("-> Controlador Principal: El proceso no ha finalizado.");
            return false;
        }
    }

    public void sincronizar_Datos_CPU_Memoria_BCP(CPU pCPU) {
        if (this.cpus.isEmpty() || pCPU == null)
            return;
        int PID_actual = pCPU.getPID_Proceso_Actual();
        if (PID_actual == 0)
            return;
        memoria.actualizar_Registros_BCP(PID_actual, pCPU);

        // Aqui se procedera a sincronizar los datos de la BPC guardada en el
        // planificador con
        // la BCP guardada en memoria.
        sincronizar_Datos_Memoria_BCP_Planificador(PID_actual);

    }

    public void sincronizar_Datos_Memoria_BCP_Planificador(int pPID) {

        // Se obtiene los datos de la BCP.
        BCP bcp_memoria = this.memoria.obtener_Datos_BCP(pPID);

        // Se obtiene la lista de procesos preparados del planificador.
        Map<Integer, BCP> procesos_preparados = this.planificador.getCola_Procesos_Nuevos();

        // Obtener del diccionario el BCP con el PID indicado.
        BCP bcp_proceso = procesos_preparados.get(pPID);
        if (bcp_memoria == null || bcp_proceso == null) {
            return;
        }

        // Sincronizar los datos de ambas BCP.
        // Asignar a la BCP de la memoria, el nombre del archivo y los tiempo.
        bcp_memoria.setNombre_Programa(bcp_proceso.getNombre_Programa());
        bcp_memoria.setTamanoProceso(bcp_proceso.getTamanoProceso());
        bcp_memoria.set_momento_creacion(bcp_proceso.get_momento_creacion());
        bcp_memoria.set_momento_finalizacion(bcp_proceso.get_momento_finalizacion());
        bcp_memoria.setOverlayActual(bcp_proceso.getOverlayActual());
        bcp_memoria.setTotalOverlays(bcp_proceso.getTotalOverlays());
        bcp_memoria.setTieneOverlay(bcp_proceso.isTieneOverlay());
        bcp_memoria.setPosInicioOverlayMV(bcp_proceso.getPosInicioOverlayMV());

        // Asignar la BCP de la memoria al planificador.
        procesos_preparados.put(pPID, bcp_memoria);

    }

    public int finalizacion_Proceso_FCFS(int pPID_Actual) {
        CPU cpu = obtenerCpuPorPID(pPID_Actual);
        if (cpu == null && !this.cpus.isEmpty()) {
            cpu = this.cpus.get(0);
        }
        if (cpu == null)
            return 1;

        int tiempo_actual = cpu.getTiempo_CPU();
        this.memoria.actualizar_Estado_BCP(pPID_Actual, "Terminado");
        cpu.modificar_PC(-1);
        this.sincronizar_Datos_CPU_Memoria_BCP(cpu);
        this.planificador.finalizacion_Procesos(memoria, pPID_Actual, tiempo_actual);

        this.planificador.cargarLote(memoria, almacenamiento, tiempo_actual);

        this.planificador.cambiar_Estado_Proceso_Nuevo();
        cpu.reiniciar_Datos_CPU();

        if (this.planificador.hay_Procesos_Nuevos()) {
            System.out.println("--> Controlador Principal: Hay procesos nuevos");
            int PID_siguiente = this.planificador.seleccionarSiguiente();
            if (PID_siguiente != -1) {
                iniciar_Despachador(PID_siguiente, cpu);
                this.programa_Iniciado = true;
                return 0;
            } else {
                this.programa_Iniciado = false;
                return 1;
            }
        } else {
            this.programa_Iniciado = false;

            // Vaciar los datos de ese CPU.
            cpu.setPID_Proceso_Actual(0);

            return 1;
        }
    }

    // ############ Seccion para la parte de ejecucion #################

    public List<String> ejecutar() {
        if (!this.programa_Iniciado || this.cpus.isEmpty()) {
            return null;
        }
        CPU cpu = this.cpus.get(0);
        if (!this.hay_interrupcion) {
            cpu.reiniciar_Interrupciones();
            cpu.ejecutar_Siguiente_Instruccion();
            this.sincronizar_Datos_CPU_Memoria_BCP(cpu);
            return this.procesar_Interrupciones(cpu.getPID_Proceso_Actual());
        } else {
            return null;
        }
    }

    public List<String> paso_a_paso() {
        if (this.cpus.isEmpty()) {
            return null;
        }

        List<String> resultados = new ArrayList<>();

        // 1. Ejecutar una instrucción en cada CPU
        for (CPU cpu : this.cpus) {
            int pid = cpu.getPID_Proceso_Actual();
            if (pid == 0)
                continue;

            if (!this.comprobar_Finalizacion_Proceso_CPU(cpu)) {
                if (!this.hay_interrupcion) {
                    // --- Manejo de overlays (igual que tu código original) ---
                    int pc = cpu.getPC();
                    BCP bcp = this.controlador_Memoria.obtenerBCP(pid);
                    if (bcp != null && bcp.isTieneOverlay() && bcp.getOverlayActual() < bcp.getTotalOverlays()) {
                        int particionInicio = controlador_MemoriaParticionada.getInicioParticionPorProceso(pid);
                        int particionFin = particionInicio;
                        boolean encontrada = false;
                        for (Particion p : controlador_MemoriaParticionada.getParticiones()) {
                            if (p.procesoAsignado == pid) {
                                particionFin = p.fin;
                                encontrada = true;
                                break;
                            }
                        }
                        if (encontrada && pc > particionFin) {
                            this.controlador_Memoria.swapOverlay(bcp);
                            cpu.setPC(particionInicio);
                        }
                    }

                    // --- Ejecución de instrucción ---
                    cpu.reiniciar_Interrupciones();
                    cpu.ejecutar_Siguiente_Instruccion();
                    this.sincronizar_Datos_CPU_Memoria_BCP(cpu);
                    sincronizar_Datos_Memoria_BCP_Planificador(pid);

                    // Modificar los tiempos de espera de los procesos cuyo estado sea listo.
                    this.memoria.modificar_Tiempo_Espera_Procesos_Listos(this.planificador.getCola_Procesos_Nuevos());

                    // Modificar la rafaga restante del proceso actual.
                    this.memoria.modificar_Tiempo_Restante_BCP(pid);

                    // Guardar resultado de interrupciones
                    resultados.addAll(this.procesar_Interrupciones(pid));
                }
            } else {
                // Proceso finalizado
                finalizacion_Proceso_FCFS(pid);
            }
        }

        // 2. Fase de planificacion (según algoritmo)
        String nombreAlgoritmo = planificador.getNombreAlgoritmo();

        switch (nombreAlgoritmo) {

            case "SRT":
                for (CPU cpu : this.cpus) {
                    int actualPid = cpu.getPID_Proceso_Actual();
                    int candidato = planificador.seleccionarSiguiente();
                    // Se debe poner el estado del PID actual de este CPU en "Listo", para que se
                    // puede calcular con ese tambien.
                    this.memoria.actualizar_Estado_BCP(actualPid, "Listo");
                    sincronizar_Datos_Memoria_BCP_Planificador(actualPid);
                    if (candidato != -1 && candidato != actualPid) {
                        Despachador.despachador(cpu, memoria, candidato);
                        memoria.actualizar_Estado_BCP(candidato, "En Ejecuccion");
                        sincronizar_Datos_Memoria_BCP_Planificador(candidato);
                    }
                }
                break;

            case "RR": {
                int candidato = planificador.seleccionarSiguiente();
                if (candidato == -1)
                    break;
                boolean yaAsignado = false;
                for (CPU cpu : this.cpus) {
                    if (cpu.getPID_Proceso_Actual() == candidato) {
                        yaAsignado = true;
                        break;
                    }
                }
                if (yaAsignado)
                    break;
                CPU cpuObjetivo = null;
                int peorRafaga = -1;
                for (CPU cpu : this.cpus) {
                    int pid = cpu.getPID_Proceso_Actual();
                    if (pid == 0) {
                        cpuObjetivo = cpu;
                        break;
                    }
                    BCP bcp = this.memoria.obtener_Datos_BCP(pid);
                    if (bcp != null) {
                        int r = 0;
                        try {
                            r = Integer.parseInt(bcp.getRafaga_Restante());
                        } catch (NumberFormatException e) {
                        }
                        if (r > peorRafaga) {
                            peorRafaga = r;
                            cpuObjetivo = cpu;
                        }
                    }
                }
                if (cpuObjetivo != null && cpuObjetivo.getPID_Proceso_Actual() != candidato) {
                    int actualPid = cpuObjetivo.getPID_Proceso_Actual();
                    if (actualPid != 0) {
                        this.memoria.actualizar_Estado_BCP(actualPid, "Listo");
                    }
                    Despachador.despachador(cpuObjetivo, memoria, candidato);
                    this.memoria.actualizar_Estado_BCP(candidato, "En Ejecucion");
                    cpuObjetivo.setPID_Proceso_Actual(candidato);
                }
                break;
            }

            case "FCFS":
            case "SJF":
            case "HRRN":
            case "Lottery": {
                for (CPU cpu : this.cpus) {
                    if (cpu.getPID_Proceso_Actual() != 0)
                        continue;
                    int candidato = planificador.seleccionarSiguiente();
                    if (candidato == -1)
                        break;
                    iniciar_Despachador(candidato, cpu);
                    BCP bcpPlan = planificador.getCola_Procesos_Nuevos().get(candidato);
                    if (bcpPlan != null) {
                        bcpPlan.setEstado("En Ejecucion");
                    }
                }
                break;
            }

            default:
                break;
        }

        return resultados;
    }

    // ############ Fin de la seccion de ejecucion #################

    // ############ Seccion para funciones auxiliares del proceso de ejecucion
    public List<String> procesar_Interrupciones(int pPID_Actual) {
        List<String> salida = new ArrayList<>();
        CPU cpu = obtenerCpuPorPID(pPID_Actual);
        if (cpu == null && !this.cpus.isEmpty()) {
            cpu = this.cpus.get(0);
        }
        if (cpu == null) {
            salida.add("0");
            return salida;
        }
        if (cpu.isError_Encontrado()) {
            salida.add(String.valueOf(1));
            salida.add(cpu.getDescripcion_Error());
            cpu.reiniciar_Datos_CPU();
            this.finalizacion_Proceso_FCFS(pPID_Actual);
            return salida;
        } else if (cpu.isLeer_Teclado()) {
            salida.add(String.valueOf(2));
            this.hay_interrupcion = true;
            return salida;
        } else if (cpu.isImprimir_Pantalla()) {
            salida.add(String.valueOf(3));
            salida.add(cpu.getDX());
            return salida;
        } else if (cpu.isProceso_Finalizado()) {
            salida.add(String.valueOf(4));
            this.finalizacion_Proceso_FCFS(pPID_Actual);
            return salida;
        } else {
            String valor = String.valueOf(0);
            salida.add(valor);
            return salida;
        }
    }

    public void leer_Teclado(int pDato) {
        if (this.cpus.isEmpty())
            return;
        System.out.println("Dato: " + pDato);
        this.cpus.get(0).leer_Entrada_Teclado(pDato);
        this.hay_interrupcion = false;
        this.sincronizar_Datos_CPU_Memoria_BCP(this.cpus.get(0));
    }

    public void procesar_Finalizacion_Proceso(CPU pCPU_Actual) {
        finalizacion_Proceso_FCFS(pCPU_Actual.getPID_Proceso_Actual());
    }

    public BCP obtener_Datos_BCP_Actual() {
        if (this.cpus.isEmpty())
            return null;
        int pid_ProcesoActual = this.cpus.get(0).getPID_Proceso_Actual();
        System.out.println("-> PID Proceso Actual: " + pid_ProcesoActual);
        return this.memoria.obtener_Datos_BCP(pid_ProcesoActual);
    }

    public Map<Integer, String> getLista_Proceso() {
        return this.planificador.obtener_Estado_5_Procesos();
    }

    public void cambiarSoloNuevosAPreparados() {
        if (planificador == null || controlador_Memoria == null)
            return;
        for (BCP bcp : planificador.getCola_Procesos_Nuevos().values()) {
            if ("Nuevo".equals(bcp.getEstado())) {
                bcp.setEstado("Preparado");
                controlador_Memoria.actualizar_Estado_BCP(bcp.getPID(), "Preparado");
            }
        }
    }

    public List<BCP> getLista_Procesos_Terminados() {
        return this.planificador.getCola_Procesos_Terminados();
    }

    public boolean hay_Interrupcion() {
        return this.hay_interrupcion;
    }

    public boolean hay_Procesos_Nuevos() {
        return this.planificador.hay_Procesos_Nuevos();
    }

    public void limpiar() {
        this.memoria = null;
        this.almacenamiento = null;
        this.cpus.clear();
        this.controlador_Memoria = null;
        this.planificador = null;
        this.controladorBuddy = null;
        // this.contador_ciclos = 0;
        this.programa_Iniciado = false;
    }

    public void reiniciar_programa() {
        this.memoria = null;
        this.cpus.clear();
        this.almacenamiento = null;
        this.controlador_Memoria = null;
        this.controladorBuddy = null;
        this.planificador = null;
        this.contador_ciclos = 0;
        this.programa_Iniciado = false;
        this.hay_interrupcion = false;
        inicializarPlanificador();
        cargar_configuracion();
    }

    public void activar_Espera() {
        if (!this.cpus.isEmpty()) {
            this.cpus.get(0).set_Espera(3);
        }
    }

    public SnapshotSistema tomarSnapshot() {
        MemoriaPaginada mp = (controlador_Memoria != null) ? controlador_Memoria.getMemoriaPaginada() : null;
        List<Particion> particiones = null;
        if (controladorBuddy != null) {
            particiones = controladorBuddy.getParticiones();
        } else if (controlador_MemoriaParticionada != null) {
            particiones = controlador_MemoriaParticionada.getParticiones();
        }
        List<BCP> todosLosBCP = (planificador != null)
                ? new java.util.ArrayList<>(planificador.getCola_Procesos_Nuevos().values())
                : new java.util.ArrayList<>();
        List<String> pendientes = (planificador != null)
                ? new java.util.ArrayList<>(planificador.getCola_Programas_Pendientes())
                : new java.util.ArrayList<>();
        if (this.cpus.isEmpty() || memoria == null) {
            return new SnapshotSistema(
                    memoria,
                    almacenamiento,
                    new java.util.HashMap<>(),
                    null,
                    new java.util.ArrayList<>(),
                    this.hay_interrupcion,
                    mp,
                    particiones,
                    todosLosBCP,
                    pendientes,
                    new java.util.ArrayList<>(this.cpus));
        }
        return new SnapshotSistema(
                memoria,
                almacenamiento,
                this.planificador.obtener_Estado_5_Procesos(),
                obtener_Datos_BCP_Actual(),
                this.planificador.getCola_Procesos_Terminados(),
                this.hay_interrupcion,
                mp,
                particiones,
                todosLosBCP,
                pendientes,
                new java.util.ArrayList<>(this.cpus));
    }

    public List<CPU> getCpus() {
        return this.cpus;
    }
}
