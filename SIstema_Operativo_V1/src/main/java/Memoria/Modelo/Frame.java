package Memoria.Modelo;

public class Frame {
    String id;
    int tamano;
    String procesoDueno = null;
    int numFrame;
    int direccionBase;
    private Pagina pagina;
    int tiempoCarga;


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

    public int getDireccionBase() {
        return direccionBase;
    }

    public void setDireccionBase(int direccionBase) {
        this.direccionBase = direccionBase;
    }
    
    public int getTiempoCarga(){
        return tiempoCarga;
    }
    
     public int setTiempoCarga(int tiempo){
        tiempoCarga = tiempo;
        return tiempoCarga;
    }       
}
