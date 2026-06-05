package Memoria.Modelo;

public class Frame {
    String id;
    int tamano;
    String procesoDueno = null;  // Aqui seria para el ID del proceso como tal 
    int numFrame; // Este numero de frame es para saber a que frame se asigna la pagina, es decir, el numero del frame en la memoria fisica
    private Pagina pagina;


    public Frame (String id, int tamano){
        this.id = id;
        this.tamano = tamano;

    }

    public int getNumFrame() {
        return numFrame;
    }

    public void setNumFrame(int numFrame) {
        this.numFrame = numFrame;
    }

    public String getProcesoDueno() {
        return procesoDueno;
    }

    public String setProcesoDueno(String proceso) {
        return this.procesoDueno = proceso;
    }

    public void setPagina(Pagina pagina) {
        this.pagina = pagina;
    }

    public Pagina getPagina() {
        return pagina;
    }
 }
