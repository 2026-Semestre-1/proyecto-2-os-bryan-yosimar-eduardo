/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author edurg
 */
public class Configuracion {

    private int memoria;
    private int almacenamiento;
    private int memoria_Virtual;
    private int cant_CPU;

    // public Configuracion(int memoria, int almacenamiento, int memoria_virtual,
    // int cant_cpu) {
    // this.Memoria = memoria;
    // this.Almacenamiento = almacenamiento;
    // this.Memoria_Virtual = memoria_virtual;
    // this.Cant_CPU = cant_cpu;
    // }

    public int getMemoria() {
        return memoria;
    }

    public int getAlmacenamiento() {
        return almacenamiento;
    }

    public int getMemoria_Virtual() {
        return memoria_Virtual;
    }

    public int getCant_CPU() {
        return cant_CPU;
    }

    public void setMemoria(int memoria) {
        this.memoria = memoria;
    }

    public void setAlmacenamiento(int almacenamiento) {
        this.almacenamiento = almacenamiento;
    }

    public void setMemoria_Virtual(int memoria_virtual) {
        this.memoria_Virtual = memoria_virtual;
    }

    public void setCant_CPU(int cant_cpu) {
        this.cant_CPU = cant_cpu;
    }

    @Override
    public String toString() {
        return "Configuracion [Memoria=" + memoria + ", Almacenamiento=" + almacenamiento
                + ", Memoria_Virtual=" + memoria_Virtual + ", Cant_CPU=" + cant_CPU + "]";
    }

}
