package kernel;

import model.Memoria;
import model.MemoriaPaginada;
import model.Almacenamiento;
import model.BCP;
import model.CPU;
import model.Codigo_ASM;
import dto.SnapshotSistema;

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
import kernel.planificacion.AlgoritmoFCFS;

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

    public NucleoSO() {
        inicializarPlanificador();
        this.controlador_MemoriaParticionada = new Controlador_MemoriaParticionada();
    }

    private void inicializarPlanificador() {
        this.planificador = new Planificador();
        this.planificador.setAlgoritmoPlanificacion(new AlgoritmoFCFS());
    }

    public void configurarMemoria(String tipoMemoria) {
        inicializarPlanificador();
        this.programa_Iniciado = false;
        this.hay_interrupcion = false;
        this.contador_ciclos = 0;
        switch(tipoMemoria) {
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
                this.controlador_Memoria = new GestorMemoria(memoria, almacenamiento, controlador_MemoriaParticionada, "ParticionIgual");
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
                this.controlador_Memoria = new GestorMemoria(memoria, almacenamiento, controlador_MemoriaParticionada, "ParticionIgualDinamica");
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
                crear_memoriaDinamica(config4.getMemoria()); // ← solo crea RAM vacía
                this.controlador_Memoria = new GestorMemoria(memoria, almacenamiento, 
                    controlador_MemoriaParticionada, "Dinamica");
                this.planificador.setControlador_Memoria(controlador_Memoria);
                crear_CPU(config4.getCant_CPU());
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
            List<Integer> porcentajes = (configPart != null) ? configPart.getDinamica() : Arrays.asList(8, 12, 17, 25, 38);
            controlador_MemoriaParticionada.inicializarParticionesFijasDistribucion(posInicioUsuario, espacioUsuario, tamanoMemoria, porcentajes);
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
            if (cpu.getPID_Proceso_Actual() == 0) {
                return cpu;
            }
        }
        return null;
    }

    public List<String> cargar_archivo(String ruta, String pNombre_Archivo) {
        List<String> errores = new ArrayList<>();
        if (almacenamiento.existe_Archivo(pNombre_Archivo)) {
            errores.add("1");
            errores.add("Error: Ya existe un archivo con ese nombre.");
            return errores;
        }
        Codigo_ASM codigo = GestorArchivos.Cargar_Archivo_ASM(ruta);
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
        asignar_Almacenamiento(codigo, pNombre_Archivo);
        this.planificador.extraer_Programas_Almacenamiento(almacenamiento);
        this.planificador.cargarLote(memoria, almacenamiento, cpus.isEmpty() ? 0 : cpus.get(0).getTiempo_CPU());
        this.planificador.cambiar_Estado_Proceso_Nuevo();
        System.out.println("[DEBUG CARGA] Archivo=" + pNombre_Archivo
                + " | cola_Nuevos=" + this.planificador.getCola_Procesos_Nuevos().size()
                + " | pendientes=" + this.planificador.getCola_Programas_Pendientes().size()
                + " | OS usado=" + memoria.getEspacio_Usado_OS()
                + "/" + memoria.getEspacio_OS());
        if (programa_Iniciado == false) {
            int inicio = this.planificador.seleccionarSiguiente();
            if (inicio != -1) {
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

    public void iniciar_Despachador(int pPID) {
        CPU cpu = obtenerCpuLibre();
        if (cpu == null) {
            cpu = this.cpus.get(0);
        }
        Despachador.despachador(cpu, this.memoria, pPID);
        this.memoria.actualizar_Estado_BCP(pPID, "En Ejecucion");
        cpu.setPID_Proceso_Actual(pPID);
    }

    public boolean comprobar_Finalizacion_Proceso() {
        if (this.cpus.isEmpty()) return false;
        int PID_actual = this.cpus.get(0).getPID_Proceso_Actual();
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

    public void sincronizar_Datos_CPU_Memoria_BCP() {
        if (this.cpus.isEmpty()) return;
        int PID_actual = this.cpus.get(0).getPID_Proceso_Actual();
        memoria.actualizar_Registros_BCP(PID_actual, this.cpus.get(0));
    }

    public int finalizacion_Proceso_FCFS(int pPID_Actual) {
        CPU cpu = obtenerCpuPorPID(pPID_Actual);
        if (cpu == null && !this.cpus.isEmpty()) {
            cpu = this.cpus.get(0);
        }
        if (cpu == null) return 1;
        cpu.modificar_PC(-1);
        this.sincronizar_Datos_CPU_Memoria_BCP();
        this.planificador.finalizacion_Procesos(memoria, pPID_Actual, cpu.getTiempo_CPU());
        this.planificador.cargarLote(memoria, almacenamiento, cpu.getTiempo_CPU());
        this.planificador.cambiar_Estado_Proceso_Nuevo();
        cpu.reiniciar_Datos_CPU();
        if (this.planificador.hay_Procesos_Nuevos()) {
            System.out.println("--> Controlador Principal: Hay procesos nuevos");
            int PID_siguiente = this.planificador.seleccionarSiguiente();
            if (PID_siguiente != -1) {
                iniciar_Despachador(PID_siguiente);
                this.programa_Iniciado = true;
                return 0;
            } else {
                this.programa_Iniciado = false;
                return 1;
            }
        } else {
            this.programa_Iniciado = false;
            return 1;
        }
    }

    public List<String> ejecutar() {
        if (!this.programa_Iniciado || this.cpus.isEmpty()) {
            return null;
        }
        CPU cpu = this.cpus.get(0);
        if (!this.hay_interrupcion) {
            cpu.reiniciar_Interrupciones();
            cpu.ejecutar_Siguiente_Instruccion();
            this.sincronizar_Datos_CPU_Memoria_BCP();
            return this.procesar_Interrupciones(cpu.getPID_Proceso_Actual());
        } else {
            return null;
        }
    }

    public List<String> paso_a_paso() {
        if (!this.programa_Iniciado || this.cpus.isEmpty()) {
            return null;
        }
        CPU cpu = this.cpus.get(0);
        if (!this.comprobar_Finalizacion_Proceso()) {
            if (!this.hay_interrupcion) {
                int pid = cpu.getPID_Proceso_Actual();
                int pc = cpu.getPC();
                BCP bcp = this.controlador_Memoria.obtenerBCP(pid);
                if (bcp != null && bcp.isTieneOverlay() && bcp.getOverlayActual() < bcp.getTotalOverlays()) {
                    int particionInicio = controlador_MemoriaParticionada.getInicioParticionPorProceso(pid);
                    int particionFin = particionInicio;
                    for (Particion p : controlador_MemoriaParticionada.getParticiones()) {
                        if (p.procesoAsignado == pid) {
                            particionFin = p.fin;
                            break;
                        }
                    }
                    if (pc > particionFin) {
                        this.controlador_Memoria.swapOverlay(bcp);
                        cpu.setPC(particionInicio);
                    }
                }
                cpu.reiniciar_Interrupciones();
                cpu.ejecutar_Siguiente_Instruccion();
                this.sincronizar_Datos_CPU_Memoria_BCP();
                return this.procesar_Interrupciones(pid);
            }
            return null;
        } else {
            finalizacion_Proceso_FCFS(cpu.getPID_Proceso_Actual());
            return null;
        }
    }

    public List<String> procesar_Interrupciones(int pPID_Actual) {
        List<String> salida = new ArrayList<>();
        CPU cpu = this.cpus.isEmpty() ? null : this.cpus.get(0);
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
        if (this.cpus.isEmpty()) return;
        System.out.println("Dato: " + pDato);
        this.cpus.get(0).leer_Entrada_Teclado(pDato);
        this.hay_interrupcion = false;
        this.sincronizar_Datos_CPU_Memoria_BCP();
    }

    public void procesar_Finalizacion_Proceso() {
        if (this.cpus.isEmpty()) return;
        finalizacion_Proceso_FCFS(this.cpus.get(0).getPID_Proceso_Actual());
    }

    public BCP obtener_Datos_BCP_Actual() {
        if (this.cpus.isEmpty()) return null;
        int pid_ProcesoActual = this.cpus.get(0).getPID_Proceso_Actual();
        System.out.println("-> PID Proceso Actual: " + pid_ProcesoActual);
        return this.memoria.obtener_Datos_BCP(pid_ProcesoActual);
    }

    public Map<Integer, String> getLista_Proceso() {
        return this.planificador.obtener_Estado_5_Procesos();
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
        this.contador_ciclos = 0;
        this.programa_Iniciado = false;
    }

    public void reiniciar_programa() {
        this.memoria = null;
        this.cpus.clear();
        this.almacenamiento = null;
        this.controlador_Memoria = null;
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
        List<Particion> particiones = (controlador_MemoriaParticionada != null) ? controlador_MemoriaParticionada.getParticiones() : null;
        if (this.cpus.isEmpty() || memoria == null) {
            return new SnapshotSistema(
                    memoria,
                    almacenamiento,
                    new java.util.HashMap<>(),
                    null,
                    new java.util.ArrayList<>(),
                    this.hay_interrupcion,
                    mp,
                    particiones);
        }
        return new SnapshotSistema(
                memoria,
                almacenamiento,
                this.planificador.obtener_Estado_5_Procesos(),
                obtener_Datos_BCP_Actual(),
                this.planificador.getCola_Procesos_Terminados(),
                this.hay_interrupcion,
                mp,
                particiones);
    }
}
