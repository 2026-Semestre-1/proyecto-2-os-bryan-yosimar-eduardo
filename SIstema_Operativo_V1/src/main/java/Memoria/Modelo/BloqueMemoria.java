package Memoria.Modelo;

public class BloqueMemoria {
    public int id;
    public int inicio;
    public int fin;
    public int tamano;
    public int procesoAsignado;

    public BloqueMemoria(int id, int inicio, int fin) {
        this.id = id;
        this.inicio = inicio;
        this.fin = fin;
        this.tamano = fin - inicio + 1;
        this.procesoAsignado = -1;
    }
}
