package kernel;

import model.Memoria;
import model.MemoriaPaginada;
import model.Almacenamiento;
import model.BCP;
import model.CPU;
import model.Codigo_ASM;
import dto.SnapshotSistema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Config.Configuracion;

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

    public NucleoSO() {
        this.planificador = new Planificador();
    }

    public void configurarMemoria(String tipoMemoria) {
        this.planificador = new Planificador();
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
        }
    }

    public void crear_memoriaParticionada(int tamano_memoria) {
        int cantidadFrames = (int) Math.ceil((double) tamano_memoria / 16);
        this.memoriaPaginada = new MemoriaPaginada(16, cantidadFrames);
        this.memoriaPaginada.inicializar();
        this.memoria = new Memoria(tamano_memoria);
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
        this.planificador.FSFS_Planificador(memoria, almacenamiento, cpu1.getTiempo_CPU());
        this.planificador.cambiar_Estado_Proceso_Nuevo();
        System.out.println("[DEBUG CARGA] Archivo=" + pNombre_Archivo
                + " | cola_Nuevos=" + this.planificador.getCola_Procesos_Nuevos().size()
                + " | pendientes=" + this.planificador.getCola_Programas_Pendientes().size()
                + " | OS usado=" + memoria.getEspacio_Usado_OS()
                + "/" + memoria.getEspacio_OS());
        if (programa_Iniciado == false) {
            int inicio = this.planificador.get_PID_Primer_Proceso_Nuevo();
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
        this.planificador.FSFS_Planificador(memoria, almacenamiento, cpu1.getTiempo_CPU());
        this.cpu1.reiniciar_Datos_CPU();
        if (this.planificador.hay_Procesos_Nuevos()) {
            System.out.println("--> Controlador Principal: Hay procesos nuevos");
            int PID_siguiente = this.planificador.get_PID_Primer_Proceso_Nuevo();
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
                this.cpu1.reiniciar_Interrupciones();
                this.cpu1.ejecutar_Siguiente_Instruccion();
                this.sincronizar_Datos_CPU_Memoria_BCP();
                return this.procesar_Interrupciones(cpu1.getPID_Proceso_Actual());
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
        this.planificador = new Planificador();
        cargar_configuracion();
    }

    public void activar_Espera() {
        this.cpu1.set_Espera(3);
    }

    public SnapshotSistema tomarSnapshot() {
        MemoriaPaginada mp = (controlador_Memoria != null) ? controlador_Memoria.getMemoriaPaginada() : null;
        if (cpu1 == null || memoria == null) {
            return new SnapshotSistema(
                    memoria,
                    almacenamiento,
                    new java.util.HashMap<>(),
                    null,
                    new java.util.ArrayList<>(),
                    this.hay_interrupcion,
                    mp);
        }
        return new SnapshotSistema(
                memoria,
                almacenamiento,
                this.planificador.obtener_Estado_5_Procesos(),
                obtener_Datos_BCP_Actual(),
                this.planificador.getCola_Procesos_Terminados(),
                this.hay_interrupcion,
                mp);
    }
}
