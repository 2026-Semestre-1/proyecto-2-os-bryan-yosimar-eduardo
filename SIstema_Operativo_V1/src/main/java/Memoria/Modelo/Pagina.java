package Memoria.Modelo;

public class Pagina {
    String contenido;
    int tamano;
    Frame frameAsignado;
    int numPagina;
    


    public Pagina (String contenido, int tamano, int numPagina){
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
}
