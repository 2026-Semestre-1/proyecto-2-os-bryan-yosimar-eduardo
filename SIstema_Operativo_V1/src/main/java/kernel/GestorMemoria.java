package kernel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import Memoria.Modelo.Particion;
import model.Almacenamiento;
import model.BCP;
import model.Codigo_ASM;
import model.Instruccion;
import model.Memoria;
import model.MemoriaPaginada;


public class GestorMemoria {

    private static final int POSICION_INICIO_BCP = 0;
    private static final int TAMANO_BCP = 26;

    private Memoria Memoria_RAM;
    private Almacenamiento Disco;
    private MemoriaPaginada memoriaPaginada;
    private String tipoGestionMemoria; 
    private Controlador_MemoriaParticionada controlador_MemoriaParticionada;
    private Map<Integer, BCP> bcpCache = new HashMap<>();
    private int ultimaPosMV = 0;

    public int obtenerUltimaPosMV() {
        return ultimaPosMV;
    }

    public void guardarBCP(BCP bcp) {
        bcpCache.put(bcp.getPID(), bcp);
    }

    public BCP obtenerBCP(int pid) {
        return bcpCache.get(pid);
    }

    public GestorMemoria(Memoria pMemoria, Almacenamiento pDisco, String pTipoGestionMemoria) {
        this.Memoria_RAM = pMemoria;
        this.Disco = pDisco;
        this.tipoGestionMemoria = pTipoGestionMemoria;
    }

    public GestorMemoria(Memoria pMemoria, Almacenamiento pDisco, MemoriaPaginada pMemoriaPaginada, String
        pTipoGestionMemoria) {
        this.Memoria_RAM = pMemoria;
        this.Disco = pDisco;
        this.memoriaPaginada = pMemoriaPaginada;
        this.tipoGestionMemoria = pTipoGestionMemoria;

    } 
    
    public GestorMemoria(Memoria pMemoria, Almacenamiento pDisco, Controlador_MemoriaParticionada pControlador_MemoriaParticionada, 
        String pTipoGestionMemoria ) {
        this.Memoria_RAM = pMemoria;
        this.Disco = pDisco;  
        this.controlador_MemoriaParticionada =  pControlador_MemoriaParticionada;
        this.tipoGestionMemoria = pTipoGestionMemoria;        
        }

    public void set_Memoria(Memoria pMemoria) {
        this.Memoria_RAM = pMemoria;
    }

    public Memoria get_Memoria() {
        return this.Memoria_RAM;
    }

    public MemoriaPaginada getMemoriaPaginada() {
        return this.memoriaPaginada;
    }

    public String getTipoGestionMemoria() {
        return this.tipoGestionMemoria;
    }

    public int get_Nuevo_PID() {
        return Memoria_RAM.asignar_Nuevo_PID_Proceso();
    }

    public int get_Pos_Actual_MV() {
        return Disco.getPosicion_Memoria_Virtual();
    }

    public int asignar_Memoria_Programa(Codigo_ASM codigoASM, String nombreProceso, int pID) {
        switch(tipoGestionMemoria) {

            case "Normal":
                int tamano = codigoASM.getContador_Intrucciones();
                if (this.Memoria_RAM.getEspacio_Usado_Usuario() + tamano <= this.Memoria_RAM.getEspacio_Usuario()) {
                    int pos = this.Memoria_RAM.getPosicion_Actual_Usuario();
                    for (Instruccion instruccion_Actual : codigoASM.getInstrucciones()) {
                        this.Memoria_RAM.getMemoria_Principal().put(pos, instruccion_Actual.get_Intruccion_Completa());
                        pos++;
                    }
                    this.Memoria_RAM.setPosicion_Actual_Usuario(pos);
                    this.Memoria_RAM.setEspacio_Usado_Usuario(this.Memoria_RAM.getEspacio_Usado_Usuario() + tamano);
                    return 0;
                } else {
                    int pos = this.Disco.getPosicion_Memoria_Virtual();
                    for (Instruccion instruccion_Actual : codigoASM.getInstrucciones()) {
                        this.Disco.getMemoria_Secundaria().put(pos, instruccion_Actual.get_Intruccion_Completa());
                        pos++;
                    }
                    this.Disco.setPosicion_Memoria_Virtual(pos);
                    this.Disco.setEspacio_Usado_Memoria_Virtual(this.Disco.getEspacio_Usado_Memoria_Virtual() + tamano);
                    return 1;
                }

            case "Paginacion":
                int pageSize = memoriaPaginada.getPageSize();
                int cantidadPaginas = (int) Math.ceil((double)codigoASM.getContador_Intrucciones() / pageSize);
                if (memoriaPaginada.hayFramesDisponibles(cantidadPaginas)) {
                    memoriaPaginada.asignarProceso(codigoASM, nombreProceso);
                    return 0;
                } else {
                    System.out.println("Gestor Memoria: No hay frames disponibles para asignar el proceso.");
                    return 1;
                }
            case "ParticionIgual":
                int tamanoproceso = codigoASM.getContador_Intrucciones();
                controlador_MemoriaParticionada.asignarProcesoEstatico(
                    codigoASM, pID, nombreProceso, Memoria_RAM);
                break;

            case "ParticionIgualDinamica":
                controlador_MemoriaParticionada.asignarProcesoEstaticoDinamico(
                    codigoASM, pID, nombreProceso, Memoria_RAM);               
                break;

            case "Dinamica":
                int tamanoTotalProceso = codigoASM.getContador_Intrucciones();
                int idParticion = controlador_MemoriaParticionada.bestFitMemoriaDinamica(tamanoTotalProceso);
                if (idParticion == -1) {
                    int espacioUtilizable;
                    if (controlador_MemoriaParticionada.getParticiones().isEmpty()) {
                        espacioUtilizable = this.Memoria_RAM.getPosicion_Actual_Usuario();
                    } else {
                        List<Particion> parts = controlador_MemoriaParticionada.getParticiones();
                        espacioUtilizable = parts.get(parts.size() - 1).fin + 1;
                    }
                    try {
                        idParticion = controlador_MemoriaParticionada.crearParticionDinamica(
                            tamanoTotalProceso, espacioUtilizable, this.Memoria_RAM.getEspacio_Total());
                    } catch (Exception e) {
                        System.out.println("Error al crear particion dinamica: " + e.getMessage());
                        break;
                    }
                }
                cargarInstruccionesEnParticionDinamica(idParticion, codigoASM, pID);
                break;

        }
        return 0;


    }


    public void cargarInstruccionesEnParticionDinamica(int idParticion, Codigo_ASM codigoASM, int pid) {
        Particion p = controlador_MemoriaParticionada.getParticiones().get(idParticion);
        int pos = p.inicio;
        for (Instruccion inst : codigoASM.getInstrucciones()) {
            Memoria_RAM.getMemoria_Principal().put(pos, inst.get_Intruccion_Completa());
            pos++;
        }
        p.procesoAsignado = pid;
    }  


    public void limpiar_Memoria_Proceso(int pPID, int pPosicion_Final, String nombreProceso) {
        switch(tipoGestionMemoria) {
            case "Normal":
                List<Integer> posciones = this.liberar_Memoria_Proceso(pPID);
                int pos_Inicial_Programa = posciones.get(0);
                int pos_Final_Programa = posciones.get(1);
                System.out.println("Controlador Memoria: Iniciando compactacion de la memoria hacia la izquierda.");
                System.out.println("Controlador Memoria: Inciando compactacion del usuario...");
                int espacio_Total = this.Memoria_RAM.getEspacio_Total();
                break;

            case "Paginacion":
                memoriaPaginada.liberarProceso(nombreProceso);
                int posBCP = this.Memoria_RAM.buscar_Posicion_BCP(pPID);
                if (posBCP != -1) {
                    liberar_Memoria_BCP(posBCP);
                }
                break;

            case "ParticionIgual":
                controlador_MemoriaParticionada.liberarProcesoEstatico(pPID, Memoria_RAM);
                int posBCP2 = this.Memoria_RAM.buscar_Posicion_BCP(pPID);
                if (posBCP2 != -1) {
                    liberar_Memoria_BCP(posBCP2);
                }
                break;

            case "ParticionIgualDinamica":
                controlador_MemoriaParticionada.liberarProcesoEstaticoDinamico(pPID, Memoria_RAM);
                int posBCP3 = this.Memoria_RAM.buscar_Posicion_BCP(pPID);
                if (posBCP3 != -1) {
                    liberar_Memoria_BCP(posBCP3);
                }
                break;         
                
            case "Dinamica":     
                controlador_MemoriaParticionada.liberarMemoriaDinamica(pPID);
                controlador_MemoriaParticionada.moverProcesosRamDinamica(Memoria_RAM);
                controlador_MemoriaParticionada.compactacionMemoriaDinamica();
                int posBCP4 = this.Memoria_RAM.buscar_Posicion_BCP(pPID);
                if (posBCP4 != -1) {
                    liberar_Memoria_BCP(posBCP4);
                }
                break;
        }

    }

    public List<Integer> liberar_Memoria_Proceso(int pid) {
        int pos_BCP = this.Memoria_RAM.buscar_Posicion_BCP(pid);
        int pos_Inicial_Programa = Integer.parseInt(this.Memoria_RAM.obtener_Instruccion(pos_BCP + 3));
        int pos_Final_Programa = Integer.parseInt(this.Memoria_RAM.obtener_Instruccion(pos_BCP + 4));
        List<Integer> posciones = new ArrayList<>();
        posciones.add(pos_Inicial_Programa);
        posciones.add(pos_Final_Programa);

        if (!(pos_Inicial_Programa >= this.Memoria_RAM.getEspacio_Total())) {
            System.out.println("Controlador Memoria: El programa se encuentra en RAM");
            for (int i = pos_Inicial_Programa; i <= pos_Final_Programa; i++) {
                this.Memoria_RAM.getMemoria_Principal().put(i, "");
            }
            this.liberar_Memoria_BCP(pos_BCP);
            return posciones;
        } else {
            System.out.println("Controlador Memoria: El programa se encuentra en Memoria Virtual");
            int tamano_Total_Ram = this.Memoria_RAM.getEspacio_Total();
            int pos_Ini_MV = this.Disco.getEspacio_Indices();
            int pos_Inicial_Real = pos_Inicial_Programa - tamano_Total_Ram;
            int pos_Final_Real = pos_Final_Programa - tamano_Total_Ram;
            for (int i = pos_Inicial_Real; i <= pos_Final_Real; i++) {
                this.Disco.getMemoria_Secundaria().put(i, "");
            }
            this.liberar_Memoria_BCP(pos_BCP);
            return posciones;
        }
    }

    public void liberar_Memoria_BCP(int posicion_BCP) {
        System.out.println("Controlador Memoria: Limpiando memoria del BCP en la posicion: " + posicion_BCP);
        System.out.println("Controlador Memoria: Tamano del BCP: " + (posicion_BCP + TAMANO_BCP));
        for (int i = posicion_BCP; i < posicion_BCP + TAMANO_BCP; i++) {
            Memoria_RAM.getMemoria_Principal().put(i, "");
        }
    }

    public int validar_Espacio_Disponible_Usuario(int tamano, String nombreProceso, Memoria memoria) {
        switch (this.tipoGestionMemoria) {
            case "Normal":
                if (this.Memoria_RAM.getEspacio_Usado_Usuario() + tamano <= this.Memoria_RAM.getEspacio_Usuario()) {
                    return 1;
                } else if (this.Disco.getEspacio_Usado_Memoria_Virtual() + tamano <= this.Disco.getEspacio_Memoria_Virtual()) {
                    return 2;
                }
                return 0;

            case "Paginacion":
                int cantidadPaginas = (int) Math.ceil((double) tamano / memoriaPaginada.getPageSize());
                if (memoriaPaginada.hayFramesDisponibles(cantidadPaginas)) {
                    return 1;
                } else {
                    System.out.println("Gestor Memoria: No hay frames disponibles para asignar el proceso.");
                    return 0;
                }

            case "ParticionIgual":
                if (controlador_MemoriaParticionada.hayParticionesEstaticasLibres(tamano)){
                    return 1;
                }
                return 3;
            
            case "ParticionIgualDinamica": 
                if (controlador_MemoriaParticionada.hayParticionesEstaticasDinamicasLibres(tamano)){
                    return 1;
                }
                return 3;

            case "Dinamica": 
            if (controlador_MemoriaParticionada.hayParticionesDinamicasLibres(tamano, memoria)) {
                return 1;
            }
            return 0;
            

        }
        return 0;
    }

    public int crearOverlay(int cantidadInstrucciones, Codigo_ASM codigo, int pid){
        for(int i = 0; i <controlador_MemoriaParticionada.getParticiones().size(); i++) {
            Particion p = controlador_MemoriaParticionada.getParticiones().get(i);
            if(p.procesoAsignado == -1){
                p.procesoAsignado = pid;
                int tamanoParticion = p.tamano;
                int lugar = p.inicio;
                for (int j = 0; j < p.tamano && j < cantidadInstrucciones; j++){
                    String ins = codigo.getInstrucciones().get(j).getInstruccion_Completa_Original();
                    this.Memoria_RAM.agregar_Instruccion_Usuario(lugar, ins);
                    lugar++;
                }
                int instruccionesRestantes = cantidadInstrucciones - tamanoParticion;
                int CantidadOverlays = 0;
                if (instruccionesRestantes > 0) {
                    CantidadOverlays = (int) Math.ceil((double)instruccionesRestantes / tamanoParticion);
                    controlador_MemoriaParticionada.crearOverlays(CantidadOverlays, i);
                    this.ultimaPosMV = this.Disco.getPosicion_Memoria_Virtual();
                    for (int j = tamanoParticion; j < cantidadInstrucciones; j++) {
                        String ins = codigo.getInstrucciones().get(j).getInstruccion_Completa_Original();
                        this.Disco.getMemoria_Secundaria().put(this.ultimaPosMV + (j - tamanoParticion), ins);
                    }
                }
                return CantidadOverlays;
            }
        }
        return 0;
    }

    public void swapOverlay(BCP bcp) {
        int pid = bcp.getPID();
        if (!bcp.isTieneOverlay() || bcp.getOverlayActual() >= bcp.getTotalOverlays()) return;

        int particionInicio = controlador_MemoriaParticionada.getInicioParticionPorProceso(pid);
        if (particionInicio == -1) return;
        int particionTamano = 0;
        int posInicioMV = bcp.getPosInicioOverlayMV();
        for (Particion p : controlador_MemoriaParticionada.getParticiones()) {
            if (p.procesoAsignado == pid) {
                particionTamano = p.tamano;
                break;
            }
        }

        for (int i = 0; i < particionTamano; i++) {
            Memoria_RAM.getMemoria_Principal().put(particionInicio + i, "");
        }

        int offset = bcp.getOverlayActual() * particionTamano;
        for (int i = 0; i < particionTamano; i++) {
            String ins = this.Disco.getMemoria_Secundaria().get(posInicioMV + offset + i);
            if (ins == null) break;
            Memoria_RAM.getMemoria_Principal().put(particionInicio + i, ins);
            this.Disco.getMemoria_Secundaria().put(posInicioMV + offset + i, "");
        }

        bcp.setOverlayActual(bcp.getOverlayActual() + 1);
    }

    

    private List<String> copiarBloque(int inicio, int longitud) {
        List<String> temp = new ArrayList<>(longitud);
        for (int i = 0; i < longitud; i++) {
            String v = Memoria_RAM.getMemoria_Principal().get(inicio + i);
            temp.add(v == null ? "" : v);
        }
        return temp;
    }

    private void escribirBloque(int inicioDestino, List<String> datos) {
        for (int i = 0; i < datos.size(); i++) {
            Memoria_RAM.getMemoria_Principal().put(inicioDestino + i, datos.get(i));
        }
    }

    private void limpiarRango(int inicio, int fin) {
        for (int i = inicio; i <= fin; i++) {
            Memoria_RAM.getMemoria_Principal().put(i, "");
        }
    }

    public void compactar_SO() {
        int writePos = POSICION_INICIO_BCP;
        for (int readPos = POSICION_INICIO_BCP; readPos < Memoria_RAM.getEspacio_OS(); readPos += TAMANO_BCP) {
            System.out.println("Controlador Memoria: Posicion de lectura: " + readPos);
            System.out.println("Controlador Memoria: Posicion de escritura: " + writePos);
            String posiblePID = this.obtener_intruccion_Proceso(readPos);
            boolean bloqueVacio = (posiblePID == null || posiblePID.trim().isEmpty());
            if (!bloqueVacio) {
                if (readPos != writePos) {
                    List<String> bloque = copiarBloque(readPos, TAMANO_BCP);
                    escribirBloque(writePos, bloque);
                    limpiarRango(readPos, readPos + TAMANO_BCP);
                }
                writePos += TAMANO_BCP;
            }
        }
        if (writePos < Memoria_RAM.getEspacio_OS()) {
            limpiarRango(writePos, Memoria_RAM.getEspacio_OS() - 1);
        }
        int bcpCount = contarBCPValidos();
        Memoria_RAM.setEspacio_Usado_OS(bcpCount * TAMANO_BCP);
        Memoria_RAM.setPosicion_Actual_OS(POSICION_INICIO_BCP + Memoria_RAM.getEspacio_Usado_OS());
    }

    public void compactar_Usuario_Desde(int posicionLiberada, int finUsuario) {
        int pos = posicionLiberada + 1;
        while (pos <= finUsuario) {
            String valor = Memoria_RAM.getMemoria_Principal().get(pos);
            if (valor == null || valor.trim().isEmpty()) {
                break;
            }
            BCP bcp = Memoria_RAM.obtener_Datos_BCP_Pos_Inicio(pos);
            if (bcp == null) {
                pos++;
                continue;
            }
            int memInit = Integer.parseInt(bcp.getMem_Init());
            int memEnd = Integer.parseInt(bcp.getMem_End());
            int tam = memEnd - memInit + 1;
            if (memInit < Memoria_RAM.getEspacio_Total()) {
                int newStart = posicionLiberada + 1;
                if (newStart != memInit) {
                    List<String> bloque = copiarBloque(memInit, tam);
                    escribirBloque(newStart, bloque);
                    limpiarRango(memInit, memEnd);
                }
                int posBCP = Memoria_RAM.buscar_Posicion_BCP(bcp.getPID());
                if (posBCP != -1) {
                    Memoria_RAM.getMemoria_Principal().put(posBCP + 3, String.valueOf(newStart));
                    Memoria_RAM.getMemoria_Principal().put(posBCP + 4, String.valueOf(newStart + tam - 1));
                    Memoria_RAM.getMemoria_Principal().put(posBCP + 5, String.valueOf(newStart));
                }
                posicionLiberada = newStart + tam - 1;
                pos = posicionLiberada + 1;
            } else {
                int tamanoTotalRam = Memoria_RAM.getEspacio_Total();
                int posIniMV = Disco.getEspacio_Indices();
                int posRealIni = posIniMV + (memInit - tamanoTotalRam);
                int posRealFin = posIniMV + (memEnd - tamanoTotalRam);
                if (Memoria_RAM.getEspacio_Usado_Usuario() + tam <= Memoria_RAM.getEspacio_Usuario()) {
                    int newStart = posicionLiberada + 1;
                    List<String> bloque = new ArrayList<>();
                    for (int i = posRealIni; i <= posRealFin; i++) {
                        bloque.add(Disco.getMemoria_Secundaria().get(i));
                    }
                    escribirBloque(newStart, bloque);
                    limpiarRango(posRealIni, posRealFin);
                    int posBCP = Memoria_RAM.buscar_Posicion_BCP(bcp.getPID());
                    if (posBCP != -1) {
                        Memoria_RAM.getMemoria_Principal().put(posBCP + 3, String.valueOf(newStart));
                        Memoria_RAM.getMemoria_Principal().put(posBCP + 4, String.valueOf(newStart + tam - 1));
                        Memoria_RAM.getMemoria_Principal().put(posBCP + 5, String.valueOf(newStart));
                    }
                    posicionLiberada = newStart + tam - 1;
                    pos = posicionLiberada + 1;
                } else {
                    pos = memEnd + 1;
                }
            }
        }
        int totalInstr = contarInstruccionesValidas();
        Memoria_RAM.setEspacio_Usado_Usuario(totalInstr);
        Memoria_RAM.setPosicion_Actual_Usuario(Memoria_RAM.getEspacio_OS() + totalInstr);
        int totalVirtual = contarInstruccionesVirtuales();
        Disco.setEspacio_Usado_Memoria_Virtual(totalVirtual);
        Disco.setPosicion_Memoria_Virtual(Disco.getEspacio_Indices() + totalVirtual);
    }

    public int contarBCPValidos() {
        int contador = 0;
        for (int pos = POSICION_INICIO_BCP; pos < Memoria_RAM.getEspacio_OS(); pos += TAMANO_BCP) {
            boolean bloqueVacio = true;
            for (int j = 0; j < TAMANO_BCP; j++) {
                String val = Memoria_RAM.getMemoria_Principal().get(pos + j);
                if (val != null && !val.trim().isEmpty()) {
                    bloqueVacio = false;
                    break;
                }
            }
            if (!bloqueVacio) {
                contador++;
            }
        }
        return contador;
    }

    public int contarInstruccionesValidas() {
        int contador = 0;
        int inicioUsuario = Memoria_RAM.getEspacio_OS();
        int finUsuario = Memoria_RAM.getEspacio_Total();
        for (int i = inicioUsuario; i < finUsuario; i++) {
            String val = Memoria_RAM.getMemoria_Principal().get(i);
            if (val != null && !val.trim().isEmpty()) {
                contador++;
            }
        }
        return contador;
    }

    public int contarInstruccionesVirtuales() {
        int contador = 0;
        int inicioVirtual = Disco.getEspacio_Indices();
        int finVirtual = inicioVirtual + Disco.getEspacio_Memoria_Virtual();
        for (int i = inicioVirtual; i < finVirtual; i++) {
            String val = Disco.getMemoria_Secundaria().get(i);
            if (val != null && !val.trim().isEmpty()) {
                contador++;
            }
        }
        return contador;
    }

    public int comprobar_Finalizacion_Proceso(int pPID) {
        BCP bcp = Memoria_RAM.obtener_Datos_BCP(pPID);
        if (bcp == null) {
            return -1;
        }
        BCP cachedBcp = obtenerBCP(pPID);
        if (cachedBcp != null && cachedBcp.isTieneOverlay() && cachedBcp.getOverlayActual() < cachedBcp.getTotalOverlays()) {
            return 0;
        }
        int pc = Integer.parseInt(bcp.getPC()) - 1;
        int tam = Integer.parseInt(bcp.getMem_End());
        if (pc == tam) {
            return 1;
        }
        return 0;
    }

    public void actualizar_Proceso_Siguiente(int pPID_Actual, int pPID_Siguiente) {
        Memoria_RAM.modificar_Enlace_Siguiente_BCP(pPID_Actual, pPID_Siguiente);
    }

    public void actualizar_Estado_BCP(int pPID, String pEstado) {
        Memoria_RAM.actualizar_Estado_BCP(pPID, pEstado);
    }

    public String obtener_intruccion_Proceso(int pPosicion) {
        switch(tipoGestionMemoria) {
            case("Normal"):
                if (pPosicion < Memoria_RAM.getEspacio_Total()) {
                    return Memoria_RAM.getMemoria_Principal().get(pPosicion);
                }
                int tamano_Total_Ram = this.Memoria_RAM.getEspacio_Total();
                int pos_Inicial_Real = pPosicion - tamano_Total_Ram;
                return this.Disco.optener_Instruccion(pos_Inicial_Real);

            case("Paginacion"):
                int memInit = encontrarMemInit(pPosicion);
                if (memInit == -1) return null;
                return memoriaPaginada.obtenerInstruccion(pPosicion, memInit);

            case("ParticionIgual"):
                return Memoria_RAM.getMemoria_Principal().get(pPosicion);

            case("ParticionIgualDinamica"):
                return Memoria_RAM.getMemoria_Principal().get(pPosicion);

            case("Dinamica"):
                return Memoria_RAM.getMemoria_Principal().get(pPosicion);

        }

        return null;

    }


    public int getInicioParticionProceso(int pid) {
        return controlador_MemoriaParticionada.getInicioParticionPorProceso(pid);
    }

    public int getTamanoParticionProceso(int pid) {
        return controlador_MemoriaParticionada.getTamanoParticionPorProceso(pid);
    }

    private int encontrarMemInit(int posicion) {
        for (int i = POSICION_INICIO_BCP; i < Memoria_RAM.getEspacio_OS(); i += TAMANO_BCP) {
            String pidStr = Memoria_RAM.obtener_Instruccion(i);
            if (pidStr != null && !pidStr.trim().isEmpty()) {
                String memInitStr = Memoria_RAM.obtener_Instruccion(i + 3);
                String memEndStr = Memoria_RAM.obtener_Instruccion(i + 4);
                if (memInitStr != null && memEndStr != null) {
                    try {
                        int memInit = Integer.parseInt(memInitStr.trim());
                        int memEnd = Integer.parseInt(memEndStr.trim());
                        if (posicion >= memInit && posicion <= memEnd) {
                            return memInit;
                        }
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }
        }
        return -1;
    }

    public int crear_Archivo(int pid, String nombreArchivo) {
        System.out.println("Controlador Memoria: Creando archivo: " + nombreArchivo);
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty())
            return -1;
        nombreArchivo = nombreArchivo.trim();
        System.out.println("Control memoria: Pass 1");
        Map<String, List<Integer>> indices = Disco.optener_Indices();
        if (indices.containsKey(nombreArchivo))
            return -1;
        System.out.println("Control memoria: Pass 2");
        if (Disco.espacio_Disponible_Programas() == 0)
            return -1;
        System.out.println("Control memoria: Pass 3");
        int inicio = Disco.getPosicion_Programas();
        Disco.getMemoria_Secundaria().put(inicio, "");
        Disco.setPosicion_Programas(inicio + 1);
        Disco.setEspacio_Usado_Programas(Disco.getEspacio_Usado_Programas() + 1);
        System.out.println("Control memoria: Pass 4");
        Disco.agregar_Indice(nombreArchivo, inicio, inicio);
        System.out.println("Control memoria: Pass 5");
        Memoria_RAM.modificar_Lista_Archivos_BCP(pid, nombreArchivo);
        System.out.println("Control memoria: Pass 6");
        return 0;
    }

    public int abrir_Archivo(int pid, String nombreArchivo) {
        if (nombreArchivo == null)
            return -1;
        Map<String, List<Integer>> indices = Disco.optener_Indices();
        if (!indices.containsKey(nombreArchivo))
            return -1;
        List<String> lista = Memoria_RAM.obtener_Lista_Archivos_Proceso(pid);
        if (lista == null || !lista.contains(nombreArchivo)) {
            Memoria_RAM.modificar_Lista_Archivos_BCP(pid, nombreArchivo);
        }
        return 0;
    }

    public String leer_Archivo(int pid, String nombreArchivo) {
        Map<String, List<Integer>> indices = Disco.optener_Indices();
        if (!indices.containsKey(nombreArchivo))
            return null;
        List<Integer> pos = indices.get(nombreArchivo);
        int inicio = pos.get(0);
        String valor = Disco.optener_Instruccion(inicio);
        return valor == null ? "" : valor;
    }

    public int escribir_Archivo(int pid, String nombreArchivo, String dato) {
        if (dato == null)
            dato = "";
        Map<String, List<Integer>> indices = Disco.optener_Indices();
        if (!indices.containsKey(nombreArchivo))
            return -1;
        List<Integer> pos = indices.get(nombreArchivo);
        int inicio = pos.get(0);
        String actual = Disco.optener_Instruccion(inicio);
        if (actual == null)
            actual = "";
        String nuevo = actual + dato;
        Disco.modificar_valor_en_memoria(inicio, nuevo);
        return 0;
    }

    public int eliminar_Archivo(int pid, String nombreArchivo) {
        Map<String, List<Integer>> indices = Disco.optener_Indices();
        if (!indices.containsKey(nombreArchivo))
            return -1;
        List<Integer> pos = indices.get(nombreArchivo);
        int inicio = pos.get(0);
        Disco.getMemoria_Secundaria().put(inicio, "");
        Disco.eliminarIndice(nombreArchivo);
        quitarArchivoDeBCP(pid, nombreArchivo);
        return 0;
    }

    public int quitarArchivoDeBCP(int pid, String nombreArchivo) {
        int posBCP = Memoria_RAM.buscar_Posicion_BCP(pid);
        if (posBCP == -1)
            return -1;
        String lista = Memoria_RAM.obtener_Instruccion(posBCP + 12);
        if (lista == null || lista.equals("NONE"))
            return -1;
        String[] partes = lista.split(",");
        StringBuilder sb = new StringBuilder();
        boolean encontrado = false;
        for (String p : partes) {
            String t = p.trim();
            if (t.equals(nombreArchivo)) {
                encontrado = true;
                continue;
            }
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(t);
        }
        String nueva = sb.length() == 0 ? "NONE" : sb.toString();
        Memoria_RAM.getMemoria_Principal().put(posBCP + 12, nueva);
        return encontrado ? 0 : -1;
    }
}
