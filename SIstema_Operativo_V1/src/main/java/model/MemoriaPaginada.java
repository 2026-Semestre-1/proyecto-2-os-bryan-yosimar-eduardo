package model;

import Memoria.Modelo.Pagina;
import Memoria.Modelo.Frame;
import Memoria.Modelo.TablaDePagina;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import model.Codigo_ASM;
import model.Instruccion;

public class MemoriaPaginada {
    Frame[] frames;
    BitSet bitmap;
    int pageSize;
    int numFrames;
    int espacioOS;
    Map<Integer, String> memoriaPrincipal;
    List<TablaDePagina> tablaDePaginas;
    int contadorIdFrame = 0;
    int contadorCarga = 0;
    Map<Integer, String> memoriaSecundaria;
    int posicionMV;

    public void setMemoriaPrincipal(Map<Integer, String> memoriaPrincipal) {
        this.memoriaPrincipal = memoriaPrincipal;
    }
    
    public void setMemoriaSecundaria(Map<Integer, String> memoriaSecundaria) {
        this.memoriaSecundaria = memoriaSecundaria;
    }
    
    public Map<Integer, String> getMemoriaSecundaria() {
        return memoriaSecundaria;
    }
    
    public int getPosicionMV(){
        return posicionMV;
    }
    
    public int setPosicionMV(int pos){
       posicionMV = pos;
       return posicionMV;
    }

    
    public int getContadorCarga(){
        return contadorCarga;
    }
    
    public int setContadorCarga(int cont){
        contadorCarga = cont;
        return contadorCarga;
    }    
    

    public MemoriaPaginada(int pageSize, int numFrames) {
        this.pageSize = pageSize;
        this.numFrames = numFrames;
        this.frames = new Frame[numFrames];
        this.bitmap = new BitSet(numFrames);
        this.tablaDePaginas = new ArrayList<>();
    }

    public void inicializar(int espacioOS) {
        this.espacioOS = espacioOS;
        for (int i = 0; i < numFrames; i++) {
            frames[i] = new Frame("Frame" + contadorIdFrame, pageSize);
            frames[i].setNumFrame(contadorIdFrame);
            frames[i].setDireccionBase(espacioOS + (i * pageSize)); // El i es mi número de frame
            contadorIdFrame++;
        }
    }

    public boolean hayFramesDisponibles(int cantidadPaginas) {
        int disponibles = 0;
        for (int i = 0; i < numFrames; i++) {
            if (!bitmap.get(i)) disponibles++;
        }
        return disponibles >= cantidadPaginas;
    }

public boolean asignarProceso(Codigo_ASM codigoASM, String nombreProceso) {
    int numPagina = 0;
    int cantidadPaginas = (int) Math.ceil((double)codigoASM.getContador_Intrucciones() / pageSize);

    ArrayList<Pagina> listaPaginas = new ArrayList<>();
    int x = 0;
    List<String> todas = new ArrayList<>();
    while(x < codigoASM.getContador_Intrucciones()){
        todas.add(codigoASM.getInstrucciones().get(x).get_Intruccion_Completa());  
        x++;
    }
    int total = todas.size();
    for (int i = 0; i < total; i += pageSize) {
        int fin = Math.min(i + pageSize, total);
        List<String> grupo = todas.subList(i, fin);
        Pagina pagina = new Pagina(grupo, pageSize, numPagina);
        listaPaginas.add(pagina);
        numPagina++;
    }

    // Aqui asignamos en ram o disco dependiendo de si pasa la condicion
    for (int j = 0; j < cantidadPaginas; j++) {
        int indiceBitLibre = bitmap.nextClearBit(0);
        if (indiceBitLibre < numFrames) {
            // En este caso si los frames alcanzan va para ram 
            Frame frame = frames[indiceBitLibre];
            Pagina pagina = listaPaginas.get(j);
            pagina.asignarFrame(frame);
            frame.setProcesoDueno(nombreProceso);
            bitmap.set(indiceBitLibre);
            frame.setTiempoCarga(contadorCarga++);
            frame.setPagina(pagina);

            TablaDePagina tabla = new TablaDePagina(nombreProceso, pagina.getNumPagina(), frame.getNumFrame(), true);
            tablaDePaginas.add(tabla);

            List<String> contenido = pagina.getContenido();
            for (int k = 0; k < contenido.size(); k++) {
                memoriaPrincipal.put(frame.getDireccionBase() + k, contenido.get(k));
            }
        } else {
            //Sino alcanza pues va pa disco
            Pagina pagina = listaPaginas.get(j);
            TablaDePagina tabla = new TablaDePagina(nombreProceso, pagina.getNumPagina(), -1, false);
            tabla.setDireccionDisco(posicionMV);
            tablaDePaginas.add(tabla);

            List<String> contenido = pagina.getContenido();
            for (int k = 0; k < contenido.size(); k++) {
                memoriaSecundaria.put(posicionMV + k, contenido.get(k));
            }
            posicionMV += pageSize;
        }
    }
    return true;
}

    public void liberarProceso(String nombreProceso) {
        for (int i = 0; i < tablaDePaginas.size(); i++) {
            if (nombreProceso.equals(tablaDePaginas.get(i).getNombreProceso())) {
                TablaDePagina pagina = tablaDePaginas.get(i);
                int numFrame = pagina.getNumeroDeFrame();
                if (numFrame != -1) {
                    for (int j = 0; j < numFrames; j++) {
                        if (frames[j] != null && frames[j].getNumFrame() == numFrame) {
                            Frame f = frames[j];
                            for (int k = 0; k < pageSize; k++) {
                                memoriaPrincipal.remove(f.getDireccionBase() + k);
                            }
                            bitmap.set(j, false);
                            frames[j].setProcesoDueno(null);
                            frames[j].setPagina(null);
                        }
                    }
                }
                if (pagina.getDireccionDisco() != -1) {
                    for (int k = 0; k < pageSize; k++) {
                        memoriaSecundaria.remove(pagina.getDireccionDisco() + k);
                    }
                }
                tablaDePaginas.remove(i);
                i--;
            }
        }
    }

    public String obtenerInstruccion(int posicionLogica, int memInit, String nombreProceso) {
        int offsetRelativo = posicionLogica - memInit;
        int numPagina = offsetRelativo / pageSize;
        int offset = offsetRelativo % pageSize;
        for (TablaDePagina paginaFaltante : tablaDePaginas) {
            if (paginaFaltante.getNumeroDePagina() == numPagina && paginaFaltante.getNombreProceso().equals(nombreProceso)) {
                if (paginaFaltante.hayBitPresencia()) {
                    int numFrame = paginaFaltante.getNumeroDeFrame();
                    for (Frame f : frames) {
                        if (f != null && f.getNumFrame() == numFrame) {
                            int dirFisica = f.getDireccionBase() + offset;
                            return memoriaPrincipal.get(dirFisica);
                        }
                    }
                } else {
                    // parte del page fault
                    Frame cambio = frames[0];
                    for (Frame f : frames) {
                        if (f.getTiempoCarga() < cambio.getTiempoCarga()) {
                            cambio = f;
                        }
                    }

                    // Guardar página a cambiar a disco si no tiene copia
                    for (TablaDePagina paginaCambio : tablaDePaginas) {
                        if (paginaCambio.getNumeroDeFrame() == cambio.getNumFrame()) {
                            if (paginaCambio.getDireccionDisco() == -1) {
                                for (int k = 0; k < pageSize; k++) {
                                    String dato = memoriaPrincipal.get(cambio.getDireccionBase() + k);
                                    if (dato != null) {
                                        memoriaSecundaria.put(posicionMV + k, dato);
                                    }
                                    memoriaPrincipal.remove(cambio.getDireccionBase() + k);
                                }
                                paginaCambio.setDireccionDisco(posicionMV);
                                posicionMV += pageSize;
                            } else {
                                for (int k = 0; k < pageSize; k++) {
                                    memoriaPrincipal.remove(cambio.getDireccionBase() + k);
                                }
                            }
                            paginaCambio.setBitPresencia(false);
                            paginaCambio.setNumeroDeFrame(-1);
                            break;
                        }
                    }

                    // Cargar página faltante desde disco al frame
                    for (int k = 0; k < pageSize; k++) {
                        String inst = memoriaSecundaria.get(paginaFaltante.getDireccionDisco() + k);
                        if (inst != null) {
                            memoriaPrincipal.put(cambio.getDireccionBase() + k, inst);
                        }
                        memoriaSecundaria.remove(paginaFaltante.getDireccionDisco() + k);
                    }

                    // Actualizar tabla de página faltante y frame
                    paginaFaltante.setBitPresencia(true);
                    paginaFaltante.setNumeroDeFrame(cambio.getNumFrame());
                    paginaFaltante.setDireccionDisco(-1);
                    cambio.setTiempoCarga(contadorCarga++);
                    cambio.setProcesoDueno(paginaFaltante.getNombreProceso());

                    int dirFisica = cambio.getDireccionBase() + offset;
                    return memoriaPrincipal.get(dirFisica);
                }
            }
        }
        return null;
    }

    public Frame[] getFrames() {
        return frames;
    }

    
    // Esto es pa ver la info de los frames
    public List<Object[]> obtenerResumenFrames() {
        List<Object[]> resumen = new ArrayList<>();
        for (int i = 0; i < numFrames; i++) {
            Frame frame = frames[i];
            String contenido = "-";
            if (frame != null && bitmap.get(i)) {
                List<String> insts = new ArrayList<>();
                for (int k = 0; k < pageSize; k++) {
                    String val = memoriaPrincipal.get(frame.getDireccionBase() + k);
                    if (val != null && !val.trim().isEmpty()) insts.add(val);
                }
                if (!insts.isEmpty()) contenido = String.join(" ", insts);
            }
            String rango = (frame != null) ? (frame.getDireccionBase() + " - " + (frame.getDireccionBase() + pageSize - 1)) : "-";
            resumen.add(new Object[] {
                    frame != null ? frame.getNumFrame() : i,
                    bitmap.get(i) ? "Ocupado" : "Libre",
                    frame != null && frame.getProcesoDueno() != null ? frame.getProcesoDueno() : "-",
                    contenido,
                    rango
            });
        }
        return resumen;
    }

    public List<Object[]> obtenerResumenTablaPaginas() {
        List<Object[]> resumen = new ArrayList<>();
        for (TablaDePagina tabla : tablaDePaginas) {
            resumen.add(new Object[] {
                    tabla.getNombreProceso(),
                    tabla.getNumeroDePagina(),
                    tabla.getNumeroDeFrame()
            });
        }
        return resumen;
    }

    public List<TablaDePagina> getTablaDePaginas() {
        return tablaDePaginas;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getNumFrames() {
        return numFrames;
    }

    public boolean isFrameLibre(int indice) {
        if (indice < 0 || indice >= numFrames) return true;
        return !bitmap.get(indice);
    }
}
