package model;

import Memoria.Modelo.Pagina;
import Memoria.Modelo.Frame;
import Memoria.Modelo.TablaDePagina;
import java.util.BitSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import model.Codigo_ASM;
import model.Instruccion;

public class MemoriaPaginada {
    Frame[] frames;
    BitSet bitmap;
    int pageSize;
    int numFrames;
    List<TablaDePagina> tablaDePaginas;
    int contadorIdFrame = 1;
    int numPagina = 1;
    Map<Integer, Pagina> paginasPorFrame = new HashMap<>();

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
            todas.add(codigoASM.getInstrucciones().get(x).toString());  
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
            bitmap.set(indiceBitLibre);
            TablaDePagina tabla = new TablaDePagina(nombreProceso, listaPaginas.get(j).getNumPagina(), frames[indiceBitLibre].getNumFrame(), true);
            tablaDePaginas.add(tabla);
            paginasPorFrame.put(indiceBitLibre, listaPaginas.get(j));
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
                    }
                }
                tablaDePaginas.remove(i);
                paginasPorFrame.remove(numFrame);
                i--;
            }
        }
    }

    public String obtenerInstruccion(int posicionLogica) {
        int pagina = posicionLogica / pageSize;
        int offset = posicionLogica % pageSize;
        for (TablaDePagina tabla : tablaDePaginas) {
            if (tabla.getNumeroDePagina() == pagina) {
                int frame = tabla.getNumeroDeFrame();
                Pagina p = paginasPorFrame.get(frame);
                return p.getContenido().get(offset);
            }
        }
        return null;
    }

    public Frame[] getFrames() {
        return frames;
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
}
