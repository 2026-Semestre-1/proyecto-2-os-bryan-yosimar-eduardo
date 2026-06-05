package Memoria.Modelo;
import java.util.List;


public class Pagina {
    List<String> contenido;
    int tamano;
    Frame frameAsignado;
    int numPagina;
    


    public Pagina (List<String> contenido, int tamano, int numPagina){
        this.contenido = contenido;
        this.tamano = tamano;
        this.numPagina = numPagina;
        this.frameAsignado = null;
    }

    public void asignarFrame(Frame frameAsignado) {
        this.frameAsignado = frameAsignado;
    }

    public int getNumPagina() {
        return numPagina;
    }

    public List<String> getContenido() {
        return contenido;
    }

    public Frame getFrameAsignado() {
        return frameAsignado;
    }

    public int getTamano() {
        return tamano;
    }    
}
