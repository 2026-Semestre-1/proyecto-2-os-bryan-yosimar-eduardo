package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Memoria {

    private Map<Integer, String> Memoria_Principal;
    private int espacio_Total = 0;
    private int espacio_OS = 0;
    private int espacio_Usuario = 0;
    private int espacio_Usado_OS = 0;
    private int espacio_Usado_Usuario = 0;
    private int posicion_Actual_OS = 0;
    private int posicion_Actual_Usuario = 0;
    private static final int TAMANO_BCP = 26;

    public Memoria(int pTamano_Memoria) {
        this.Memoria_Principal = new HashMap<>(pTamano_Memoria);
        this.espacio_Total = pTamano_Memoria;
        int minOS = 5 * TAMANO_BCP;
        this.espacio_OS = Math.max((int) (pTamano_Memoria * 0.2), minOS);
        this.espacio_Usuario = pTamano_Memoria - this.espacio_OS; this.posicion_Actual_Usuario = this.espacio_OS;
    }

    public Map<Integer, String> getMemoria_Principal() { return Memoria_Principal; }
    public int getEspacio_Total() { return espacio_Total; }
    public int getEspacio_OS() { return espacio_OS; }
    public int getEspacio_Usuario() { return espacio_Usuario; }
    public int getEspacio_Usado_OS() { return espacio_Usado_OS; }
    public int getEspacio_Usado_Usuario() { return espacio_Usado_Usuario; }
    public int getPosicion_Actual_OS() { return posicion_Actual_OS; }
    public int getPosicion_Actual_Usuario() { return posicion_Actual_Usuario; }
    public void setPosicion_Actual_Usuario(int pPosicion) { this.posicion_Actual_Usuario = pPosicion; }
    public void setEspacio_Usado_Usuario(int pEspacio) { this.espacio_Usado_Usuario = pEspacio; }
    public void setPosicion_Actual_OS(int pPosicion) { this.posicion_Actual_OS = pPosicion; }
    public void setEspacio_Usado_OS(int pEspacio) { this.espacio_Usado_OS = pEspacio; }

    public String obtener_Instruccion(int pPosicion) { return Memoria_Principal.get(pPosicion); }
    public void agregar_Instruccion_OS(int pPosicion, String pInstruccion) { Memoria_Principal.put(pPosicion, pInstruccion); }
    public void agregar_Instruccion_Usuario(int pPosicion, String pInstruccion) { Memoria_Principal.put(pPosicion, pInstruccion); }

    public void iniciar_Memoria_Registros() {
        Memoria_Principal.put(0, ((Integer) espacio_OS).toString()); Memoria_Principal.put(1, "0000 0000 00000000");
        Memoria_Principal.put(2, "0"); Memoria_Principal.put(3, "0"); Memoria_Principal.put(4, "0");
        Memoria_Principal.put(5, "0"); Memoria_Principal.put(6, "0");
        posicion_Actual_OS += 7; espacio_Usado_OS += 7;
    }

    public int asignar_Nuevo_PID_Proceso() {
        int pid = 0;
        for (int i = 0; i < posicion_Actual_OS; i += TAMANO_BCP) {
            if (Memoria_Principal.get(i) != null && !Memoria_Principal.get(i).equals("")) { pid = Integer.parseInt(Memoria_Principal.get(i)); }
        }
        return pid + 1;
    }

    public int iniciar_Memoria_BCP(int pTamano_Proceso, int pPrioridad, int pProximo_Proceso, int pCPU_Asignada,
            int pMomento_Llegada, int pDuracion_Estimada, int pos_Actual_MV) {
        int pid = asignar_Nuevo_PID_Proceso();
        Memoria_Principal.put(posicion_Actual_OS, ((Integer) pid).toString());
        Memoria_Principal.put(posicion_Actual_OS + 1, "NEW");
        Memoria_Principal.put(posicion_Actual_OS + 2, ((Integer) pPrioridad).toString());
        if (this.validar_Espacio_Disponible_Usuario(pTamano_Proceso) == 0) {
            Memoria_Principal.put(posicion_Actual_OS + 3, ((Integer) posicion_Actual_Usuario).toString());
            Memoria_Principal.put(posicion_Actual_OS + 4, ((Integer) (posicion_Actual_Usuario + pTamano_Proceso - 1)).toString());
            Memoria_Principal.put(posicion_Actual_OS + 5, ((Integer) posicion_Actual_Usuario).toString());
        } else {
            Memoria_Principal.put(posicion_Actual_OS + 3, ((Integer) (this.getEspacio_Total() + pos_Actual_MV)).toString());
            Memoria_Principal.put(posicion_Actual_OS + 4, ((Integer) (this.getEspacio_Total() + pos_Actual_MV + pTamano_Proceso - 1)).toString());
            Memoria_Principal.put(posicion_Actual_OS + 5, ((Integer) (this.getEspacio_Total() + pos_Actual_MV)).toString());
        }
        Memoria_Principal.put(posicion_Actual_OS + 6, "0000 0000 00000000");
        Memoria_Principal.put(posicion_Actual_OS + 7, "0"); Memoria_Principal.put(posicion_Actual_OS + 8, "0");
        Memoria_Principal.put(posicion_Actual_OS + 9, "0"); Memoria_Principal.put(posicion_Actual_OS + 10, "0");
        Memoria_Principal.put(posicion_Actual_OS + 11, "0"); Memoria_Principal.put(posicion_Actual_OS + 12, "NONE");
        Memoria_Principal.put(posicion_Actual_OS + 13, "1");
        Memoria_Principal.put(posicion_Actual_OS + 14, ((Integer) pMomento_Llegada).toString());
        Memoria_Principal.put(posicion_Actual_OS + 15, "-1"); Memoria_Principal.put(posicion_Actual_OS + 16, "0");
        Memoria_Principal.put(posicion_Actual_OS + 17, ((Integer) pDuracion_Estimada).toString());
        Memoria_Principal.put(posicion_Actual_OS + 18, (pProximo_Proceso == -1) ? "NONE" : String.valueOf(pProximo_Proceso));
        for (int i = 0; i < 5; i++) { Memoria_Principal.put(posicion_Actual_OS + 19 + i, ""); }
        Memoria_Principal.put(posicion_Actual_OS + 24, "0"); Memoria_Principal.put(posicion_Actual_OS + 25, "0");
        this.posicion_Actual_OS += 26; this.espacio_Usado_OS += 26;
        System.out.println("[DEBUG BCP] PID=" + pid + " creado | pos_OS=" + (posicion_Actual_OS - 26)
                + " usado_OS=" + espacio_Usado_OS + " max_OS=" + espacio_OS
                + " pos_User=" + posicion_Actual_Usuario + " usado_User=" + espacio_Usado_Usuario);
        return pid;
    }

    public int asignar_memoria_a_programa(Codigo_ASM pCodigo_ASM) {
        int pos_inicial = posicion_Actual_Usuario; int tamano_codigo = pCodigo_ASM.getContador_Intrucciones();
        if (pos_inicial + tamano_codigo <= espacio_Total) {
            List<Instruccion> instrucciones = pCodigo_ASM.getInstrucciones();
            for (Instruccion instruccion_actual : instrucciones) { Memoria_Principal.put(pos_inicial, instruccion_actual.get_Intruccion_Completa()); pos_inicial++; }
            this.posicion_Actual_Usuario += tamano_codigo; this.espacio_Usado_Usuario += tamano_codigo; return 0;
        } else { return 1; }
    }

    public int modificar_valor_en_memoria(int pPosicion, String pValor) {
        if (Memoria_Principal.containsKey(pPosicion)) { Memoria_Principal.replace(pPosicion, pValor); return 0; } else { return 1; }
    }

    public int actualizar_Registros_BCP(int pPID, CPU pCPU) {
        int posicion_BCP = buscar_Posicion_BCP(pPID); if (posicion_BCP == -1) { return 1; }
        Memoria_Principal.put(posicion_BCP + 5, String.valueOf(pCPU.getPC()));
        Memoria_Principal.put(posicion_BCP + 6, pCPU.getIR());
        Memoria_Principal.put(posicion_BCP + 7, String.valueOf(pCPU.getAC()));
        Memoria_Principal.put(posicion_BCP + 8, String.valueOf(pCPU.getAX()));
        Memoria_Principal.put(posicion_BCP + 9, String.valueOf(pCPU.getBX()));
        Memoria_Principal.put(posicion_BCP + 10, String.valueOf(pCPU.getCX()));
        Memoria_Principal.put(posicion_BCP + 11, pCPU.getDX());
        Memoria_Principal.put(posicion_BCP + 24, pCPU.getAH());
        Memoria_Principal.put(posicion_BCP + 25, pCPU.getAL());
        return 0;
    }

    public int actualizar_Estado_BCP(int pPID, String pEstado) { int posicion_BCP = buscar_Posicion_BCP(pPID); if (posicion_BCP == -1) { return 1; } Memoria_Principal.put(posicion_BCP + 1, pEstado); return 0; }
    public int actualizar_Priodidad_BCP(int pPID, int pPriodidad) { int posicion_BCP = buscar_Posicion_BCP(pPID); if (posicion_BCP == -1) { return 1; } Memoria_Principal.put(posicion_BCP + 2, String.valueOf(pPriodidad)); return 0; }

    public int modificar_Lista_Archivos_BCP(int pPID, String pNuevo_Archivo) {
        int posicion_BCP = buscar_Posicion_BCP(pPID); if (posicion_BCP == -1) { return 1; }
        String lista_Archivos = Memoria_Principal.get(posicion_BCP + 12);
        if (lista_Archivos.equals("NONE")) { lista_Archivos = pNuevo_Archivo; } else { lista_Archivos += ", " + pNuevo_Archivo; }
        Memoria_Principal.put(posicion_BCP + 12, lista_Archivos); return 0;
    }

    public int modificar_Tiempo_Inicio_BCP(int pPID, int pTiempo_Inicio) { int posicion_BCP = buscar_Posicion_BCP(pPID); if (posicion_BCP == -1) { return 1; } Memoria_Principal.put(posicion_BCP + 15, String.valueOf(pTiempo_Inicio)); return 0; }
    public int modificar_Tiempo_Finalizacion_BCP(int pPID, int pTiempo_Finalizacion) { int posicion_BCP = buscar_Posicion_BCP(pPID); if (posicion_BCP == -1) { return 1; } Memoria_Principal.put(posicion_BCP + 16, String.valueOf(pTiempo_Finalizacion)); return 0; }
    public int modificar_Enlace_Siguiente_BCP(int pPID, int pProximo_Proceso) { int posicion_BCP = buscar_Posicion_BCP(pPID); if (posicion_BCP == -1) { return 1; } Memoria_Principal.put(posicion_BCP + 18, String.valueOf(pProximo_Proceso)); return 0; }

    public int liberar_Memoria(int pDireccion) { if (pDireccion < espacio_OS) { return 1; } if (pDireccion < espacio_Total) { getMemoria_Principal().remove(pDireccion); return 0; } else { return 2; } }

    public int liberar_Memoria_Proceso(int pPID) {
        int posicion_BCP = buscar_Posicion_BCP(pPID); if (posicion_BCP == -1) { return -1; }
        int posicion_Inicial = Integer.parseInt(Memoria_Principal.get(posicion_BCP + 3).trim());
        int posicion_Final = Integer.parseInt(Memoria_Principal.get(posicion_BCP + 4).trim());
        for (int i = posicion_Inicial; i <= posicion_Final; i++) { Memoria_Principal.put(i, ""); }
        for (int i = posicion_BCP; i < posicion_BCP + TAMANO_BCP; i++) { Memoria_Principal.put(i, ""); }
        System.out.println("Memoria: Memoria liberada desde la posicion " + posicion_Inicial + " hasta la posicion " + posicion_Final + ".");
        return posicion_Final;
    }

    public int agregar_Dato_Pila(int pPID, String pDato) {
        int posicion_BCP = buscar_Posicion_BCP(pPID); if (posicion_BCP == -1) { return 1; }
        for (int i = posicion_BCP + 19; i <= posicion_BCP + 23; i++) { if (Memoria_Principal.get(i) == null || Memoria_Principal.get(i).equals("")) { Memoria_Principal.put(i, pDato); return 0; } }
        return 2;
    }

    public Integer obtener_Dato_Pila(int pPID) {
        int posicion_BCP = buscar_Posicion_BCP(pPID); if (posicion_BCP == -1) { return null; }
        for (int i = posicion_BCP + 23; i >= posicion_BCP + 19; i--) { if (Memoria_Principal.get(i) != null && !Memoria_Principal.get(i).equals("")) { String dato_Obtenido = Memoria_Principal.get(i); Memoria_Principal.put(i, ""); return Integer.parseInt(dato_Obtenido.trim()); } }
        return null;
    }

    public int buscar_Posicion_BCP(int pPID) { for (int i = 0; i < espacio_OS; i += TAMANO_BCP) { if (Memoria_Principal.get(i) != null && !Memoria_Principal.get(i).equals("")) { int pid = Integer.parseInt(Memoria_Principal.get(i).trim()); if (pid == pPID) { return i; } } } return -1; }

    public BCP obtener_Datos_BCP(int pPID) {
        int posicion_BCP = buscar_Posicion_BCP(pPID); if (posicion_BCP == -1) { return null; }
        int pid = Integer.parseInt(Memoria_Principal.get(posicion_BCP));
        String estado = Memoria_Principal.get(posicion_BCP + 1); String prioridad = Memoria_Principal.get(posicion_BCP + 2);
        String mem_init = Memoria_Principal.get(posicion_BCP + 3); String mem_end = Memoria_Principal.get(posicion_BCP + 4);
        String pc = Memoria_Principal.get(posicion_BCP + 5); String ir = Memoria_Principal.get(posicion_BCP + 6);
        String ac = Memoria_Principal.get(posicion_BCP + 7); String ax = Memoria_Principal.get(posicion_BCP + 8);
        String bx = Memoria_Principal.get(posicion_BCP + 9); String cx = Memoria_Principal.get(posicion_BCP + 10);
        String dx = Memoria_Principal.get(posicion_BCP + 11); String io_status = Memoria_Principal.get(posicion_BCP + 12);
        String cpu_asignada = Memoria_Principal.get(posicion_BCP + 13); String tiempo_llegada = Memoria_Principal.get(posicion_BCP + 14);
        String tiempo_inicio = Memoria_Principal.get(posicion_BCP + 15); String tiempo_finalizacion = Memoria_Principal.get(posicion_BCP + 16);
        String tiempo_ejecucion = Memoria_Principal.get(posicion_BCP + 17); String proximo_proceso = Memoria_Principal.get(posicion_BCP + 18);
        int[] pila = new int[5]; for (int i = 0; i < 5; i++) { String dato = Memoria_Principal.get(posicion_BCP + 19 + i); if (dato == null || dato.equals("")) { pila[i] = 0; } else { pila[i] = Integer.parseInt(dato); } }
        String ah = Memoria_Principal.get(posicion_BCP + 24); String al = Memoria_Principal.get(posicion_BCP + 25);
        return new BCP(pid, estado, prioridad, mem_init, mem_end, pc, ir, ac, ax, bx, cx, dx, ah, al, io_status, cpu_asignada, tiempo_llegada, tiempo_inicio, tiempo_finalizacion, tiempo_ejecucion, proximo_proceso, pila);
    }

    public BCP obtener_Datos_BCP_Pos_Inicio(int pPosicion_Inicial) { for (int i = 0; i < espacio_OS; i += TAMANO_BCP) { if (Memoria_Principal.get(i) != null && !Memoria_Principal.get(i).equals("")) { System.out.println("Memoria: PID: " + Memoria_Principal.get(i) + "Posicion: " + i); int pid = Integer.parseInt(Memoria_Principal.get(i).trim()); if (Integer.parseInt(Memoria_Principal.get(i + 3)) == pPosicion_Inicial) { return obtener_Datos_BCP(pid); } } } return null; }

    public List<String> obtener_Lista_Archivos_Proceso(int pPID) { List<String> lista_Archivos = new ArrayList<>(); int posicion_BCP = buscar_Posicion_BCP(pPID); if (posicion_BCP == -1) { return null; } String archivos = obtener_Instruccion(posicion_BCP + 12); String[] archivos_Array = archivos.split(","); for (String archivo : archivos_Array) { lista_Archivos.add(archivo.trim()); } return lista_Archivos; }

    public Integer validar_Salto_Programa(Integer pPosicion_Destino, int pPID_Actual) { int posicion_BCP = buscar_Posicion_BCP(pPID_Actual); if (posicion_BCP == -1) { return null; } int mem_init = Integer.parseInt(Memoria_Principal.get(posicion_BCP + 3)); int mem_end = Integer.parseInt(Memoria_Principal.get(posicion_BCP + 4)); if (pPosicion_Destino < mem_init) { return 1; } else if (pPosicion_Destino > mem_end) { return 2; } else { return 0; } }

    public int validar_Espacio_Disponible_OS(int pEspacio_Necesario) {
        int resultado = (espacio_Usado_OS + pEspacio_Necesario <= espacio_OS) ? 0 : 1;
        System.out.println("[DEBUG OS] usado=" + espacio_Usado_OS + " necesita=" + pEspacio_Necesario
                + " max=" + espacio_OS + " => " + (resultado == 0 ? "OK" : "FULL"));
        return resultado;
    }
    public int validar_Espacio_Disponible_Usuario(int pEspacio_Necesario) { if (espacio_Usado_Usuario + pEspacio_Necesario <= espacio_Usuario) { return 0; } else { return 1; } }

    public void mostrar_Memoria() { System.out.println("Mostrando memoria.\n"); for (Map.Entry<Integer, String> entry : Memoria_Principal.entrySet()) { System.out.println("Posicion: " + entry.getKey() + " -> Instruccion: " + entry.getValue()); } }
}
