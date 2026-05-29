package Modelo;
import Memoria.Modelo.Pagina;
import Memoria.Modelo.Frame;
import Memoria.Modelo.TablaDePagina;
import java.util.BitSet;
import java.util.List;
import java.util.ArrayList;

public class MemoriaPaginada {
    Frame[] frames;
    BitSet bitmap;
    int pageSize;
    int numFrames;
    List<TablaDePagina> tablaDePaginas;
    int contadorIdFrame = 1;
    int numPagina = 1;
    int listaNumPagina = 1;


    // Aqui numFrames ya viene dado entonces con esto me refiero
    // a que ya hicimos RamUsuario/16  
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
            frames[i].setNumFrame(contadorIdFrame); // Asignar el número de frame al objeto Frame
            contadorIdFrame++;     
        }
    }

    public boolean asignarProceso(BCP bcp, List<String> instrucciones) {
       ArrayList<Pagina> listaPaginas = new ArrayList<>();
        int tamanoProceso = bcp.getTamanoProceso(); // Falta crearlo en el bcp
        int cantidadPaginas =  (int)Math.ceil((double)tamanoProceso/pageSize);
        int cantidadPaginas2 = cantidadPaginas;
        int cantidadPaginas3 = cantidadPaginas;
        int i = 0;
        // Aqui creare la página 
        while(cantidadPaginas2 != 0) {
            String contenidoProceso = instrucciones.get(i); // Aqui tengo que ver de donde saco la instruccion porque no la trae jsjsjs
            Pagina pagina = new Pagina(contenidoProceso,pageSize,numPagina);
            listaPaginas.add(pagina);
            
            numPagina++;
            cantidadPaginas2--;
        }

        if (cantidadPaginas == 0) {
            return false;
        }

        
        for(int j = 0; j < cantidadPaginas3; j++){
            int indiceBitLibre = bitmap.nextClearBit(0);
            listaPaginas.get(j).asignarFrame(frames[indiceBitLibre]);
            bitmap.set(indiceBitLibre);
            cantidadPaginas--;
            TablaDePagina tabla = new TablaDePagina(bcp.getNombre_Programa(),listaPaginas.get(j).getNumPagina(),frames[indiceBitLibre].getNumFrame(), true);
            tablaDePaginas.add(tabla);

        }
        return true; 
    }


    public boolean liberarProceso(BCP bcp) {
        if(tablaDePaginas.isEmpty()) {
            return false;
        }
        for(int i = 0; i < tablaDePaginas.size(); i++){
            if(bcp.getNombre_Programa().equals(tablaDePaginas.get(i).getNombreProceso())) {
                int numFrame = tablaDePaginas.get(i).getNumeroDeFrame();
                for (int j = 0; j < bitmap.size(); j++) {
                    if (frames[j].getNumFrame() == numFrame) {
                        bitmap.set(j, false);
                        frames[j].setProcesoDueno(null);
                    }
                }
                tablaDePaginas.remove(i);
                i--;
            }   
        }
        return true;
    }










}
