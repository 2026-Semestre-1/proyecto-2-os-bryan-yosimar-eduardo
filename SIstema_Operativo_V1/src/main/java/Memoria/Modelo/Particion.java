package Memoria.Modelo;

import java.util.List;
import java.util.*;

public class Particion {
    public int id;
    public int inicio;
    public int fin;
    public int tamano;
    public int procesoAsignado; // si esta libre -1 >= 0 si hay algo

    public Particion(int id, int inicio, int fin) {
        this.id = id;
        this.inicio = inicio;
        this.fin = fin;
        this.tamano = fin - inicio + 1;
        this.procesoAsignado = -1;
    }




}


