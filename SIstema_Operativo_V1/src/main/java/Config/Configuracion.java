package Config;

public class Configuracion {

    private int memoria;
    private int almacenamiento;
    private int memoria_Virtual;
    private int cant_CPU;

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
        return "Configuracion [Memoria=" + memoria + ", Almacenamiento=" + almacenamiento + ", Memoria_Virtual="
                + memoria_Virtual + ", Cant_CPU=" + cant_CPU + "]";
    }
}
