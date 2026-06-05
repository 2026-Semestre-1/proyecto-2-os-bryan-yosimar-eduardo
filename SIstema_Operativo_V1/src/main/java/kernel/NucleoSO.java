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
import Config.ConfigKernel;
import kernel.planificacion.AlgoritmoFCFS;

public class NucleoSO {
    private Memoria memoria = null;
    private Almacenamiento almacenamiento = null;
    private CPU cpu1 = null;
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
        int cantidadFrames = (int) Math.ceil((double) tamano_memoria / 16);
        this.memoriaPaginada = new MemoriaPaginada(16, cantidadFrames);
        this.memoriaPaginada.inicializar();
        this.memoria = new Memoria(tamano_memoria);
        this.memoria.soloKernel();
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
        this.cpu1 = new CPU(1, memoria, controlador_Memoria);
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
        this.planificador.cargarLote(memoria, almacenamiento, cpu1.getTiempo_CPU());
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
        Despachador.despachador(this.cpu1, this.memoria, pPID);
        this.memoria.actualizar_Estado_BCP(pPID, "En Ejecucion");
        this.cpu1.setPID_Proceso_Actual(pPID);
    }

    public boolean comprobar_Finalizacion_Proceso() {
        int PID_actual = this.cpu1.getPID_Proceso_Actual();
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
        int PID_actual = this.cpu1.getPID_Proceso_Actual();
        memoria.actualizar_Registros_BCP(PID_actual, cpu1);
    }

    public int finalizacion_Proceso_FCFS(int pPID_Actual) {
        cpu1.modificar_PC(-1);
        this.sincronizar_Datos_CPU_Memoria_BCP();
        this.planificador.finalizacion_Procesos(memoria, pPID_Actual, this.cpu1.getTiempo_CPU());
        this.planificador.cargarLote(memoria, almacenamiento, cpu1.getTiempo_CPU());
        this.planificador.cambiar_Estado_Proceso_Nuevo();
        this.cpu1.reiniciar_Datos_CPU();
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
        if (!this.programa_Iniciado) {
            return null;
        }
        if (!this.hay_interrupcion) {
            this.cpu1.reiniciar_Interrupciones();
            this.cpu1.ejecutar_Siguiente_Instruccion();
            this.sincronizar_Datos_CPU_Memoria_BCP();
            return this.procesar_Interrupciones(cpu1.getPID_Proceso_Actual());
        } else {
            return null;
        }
    }

    public List<String> paso_a_paso() {
        if (!this.programa_Iniciado) {
            return null;
        }
        if (!this.comprobar_Finalizacion_Proceso()) {
            if (!this.hay_interrupcion) {
                int pid = this.cpu1.getPID_Proceso_Actual();
                int pc = this.cpu1.getPC();
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
                        this.cpu1.setPC(particionInicio);
                    }
                }
                this.cpu1.reiniciar_Interrupciones();
                this.cpu1.ejecutar_Siguiente_Instruccion();
                this.sincronizar_Datos_CPU_Memoria_BCP();
                return this.procesar_Interrupciones(pid);
            }
            return null;
        } else {
            finalizacion_Proceso_FCFS(cpu1.getPID_Proceso_Actual());
            return null;
        }
    }

    public List<String> procesar_Interrupciones(int pPID_Actual) {
        List<String> salida = new ArrayList<>();
        if (cpu1.isError_Encontrado()) {
            salida.add(String.valueOf(1));
            salida.add(this.cpu1.getDescripcion_Error());
            this.cpu1.reiniciar_Datos_CPU();
            this.finalizacion_Proceso_FCFS(pPID_Actual);
            return salida;
        } else if (cpu1.isLeer_Teclado()) {
            salida.add(String.valueOf(2));
            this.hay_interrupcion = true;
            return salida;
        } else if (cpu1.isImprimir_Pantalla()) {
            salida.add(String.valueOf(3));
            salida.add(this.cpu1.getDX());
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

    public void leer_Teclado(int pDato) {
        System.out.println("Dato: " + pDato);
        this.cpu1.leer_Entrada_Teclado(pDato);
        this.hay_interrupcion = false;
        this.sincronizar_Datos_CPU_Memoria_BCP();
    }

    public void procesar_Finalizacion_Proceso() {
        finalizacion_Proceso_FCFS(cpu1.getPID_Proceso_Actual());
    }

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

    public boolean hay_Interrupcion() {
        return this.hay_interrupcion;
    }

    public boolean hay_Procesos_Nuevos() {
        return this.planificador.hay_Procesos_Nuevos();
    }

    public void limpiar() {
        this.memoria = null;
        this.almacenamiento = null;
        this.cpu1 = null;
        this.controlador_Memoria = null;
        this.planificador = null;
        this.contador_ciclos = 0;
        this.programa_Iniciado = false;
    }

    public void reiniciar_programa() {
        this.memoria = null;
        this.cpu1 = null;
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
        this.cpu1.set_Espera(3);
    }

    public SnapshotSistema tomarSnapshot() {
        MemoriaPaginada mp = (controlador_Memoria != null) ? controlador_Memoria.getMemoriaPaginada() : null;
        List<Particion> particiones = (controlador_MemoriaParticionada != null) ? controlador_MemoriaParticionada.getParticiones() : null;
        if (cpu1 == null || memoria == null) {
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
