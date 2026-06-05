package model;

import Memoria.Modelo.Pagina;
import Memoria.Modelo.Frame;
import Memoria.Modelo.TablaDePagina;
import java.util.BitSet;
import java.util.List;
import java.util.ArrayList;
import model.Codigo_ASM;
import model.Instruccion;

public class MemoriaPaginada {
    Frame[] frames;
    BitSet bitmap;
    int pageSize;
    int numFrames;
    List<TablaDePagina> tablaDePaginas;
    int contadorIdFrame = 0;
    int numPagina = 1;
    public MemoriaPaginada(int pageSize, int numFrames) {
        this.pageSize = pageSize;
        this.numFrames = numFrames;
        this.frames = new Frame[numFrames];
        this.bitmap = new BitSet(numFrames);
        this.tablaDePaginas = new ArrayList<>();
    }

    public void inicializar() {
        for (int i = 0; i < numFrames; i++) {
            frames[i] = new Frame("Frame" + contadorIdFrame, pageSize);
            frames[i].setNumFrame(contadorIdFrame);
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
        if (!hayFramesDisponibles(cantidadPaginas)) return false;

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

        for (int j = 0; j < cantidadPaginas; j++) {
            int indiceBitLibre = bitmap.nextClearBit(0);
            listaPaginas.get(j).asignarFrame(frames[indiceBitLibre]);
            frames[indiceBitLibre].setProcesoDueno(nombreProceso);
            bitmap.set(indiceBitLibre);
            TablaDePagina tabla = new TablaDePagina(nombreProceso, listaPaginas.get(j).getNumPagina(), frames[indiceBitLibre].getNumFrame(), true);
            tablaDePaginas.add(tabla);
            frames[indiceBitLibre].setPagina(listaPaginas.get(j));
        }
        return true;
    }

    public void liberarProceso(String nombreProceso) {
        for (int i = 0; i < tablaDePaginas.size(); i++) {
            if (nombreProceso.equals(tablaDePaginas.get(i).getNombreProceso())) {
                int numFrame = tablaDePaginas.get(i).getNumeroDeFrame();
                for (int j = 0; j < numFrames; j++) {
                    if (frames[j] != null && frames[j].getNumFrame() == numFrame) {
                        bitmap.set(j, false);
                        frames[j].setProcesoDueno(null);
                        frames[j].setPagina(null);
                    }
                }
                tablaDePaginas.remove(i);
                i--;
            }
        }
    }

    public String obtenerInstruccion(int posicionLogica, int memInit) {
        int offsetRelativo = posicionLogica - memInit;
        int numPagina = offsetRelativo / pageSize;
        int offset = offsetRelativo % pageSize;
        for (TablaDePagina tabla : tablaDePaginas) {
            if (tabla.getNumeroDePagina() == numPagina) {
                int numFrame = tabla.getNumeroDeFrame();
                for (Frame f : frames) {
                    if (f != null && f.getNumFrame() == numFrame && f.getPagina() != null) {
                        return f.getPagina().getContenido().get(offset);
                    }
                }
            }
        }
        return null;
    }

    public Frame[] getFrames() {
        return frames;
    }

    public List<Object[]> obtenerResumenFrames() {
        List<Object[]> resumen = new ArrayList<>();
        for (int i = 0; i < numFrames; i++) {
            Frame frame = frames[i];
            String contenido = "-";
            if (frame != null && frame.getPagina() != null && frame.getPagina().getContenido() != null
                    && !frame.getPagina().getContenido().isEmpty()) {
                contenido = String.join(" ", frame.getPagina().getContenido());
            }
            resumen.add(new Object[] {
                    frame != null ? frame.getNumFrame() : i,
                    bitmap.get(i) ? "Ocupado" : "Libre",
                    frame != null && frame.getProcesoDueno() != null ? frame.getProcesoDueno() : "-",
                    contenido
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
