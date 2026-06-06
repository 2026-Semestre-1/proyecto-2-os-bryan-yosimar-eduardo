package Memoria.Modelo;

public class TablaDePagina {
    String nombreProceso;
    int numeroDePagina;
    int offsetPagina;    
    int numeroDeFrame;
    boolean paginaFueModificada = false; // Falso si no fue modifica, true para lo contrario
    String bitsDeProteccion; // R, W, X
    boolean permiteCache; // 0 para no permitir cache y 1 para permitir cache 
    boolean bitPresencia; // true si la página está presente en memoria, false si esta virtual
    int direccionDisco;



    public TablaDePagina(String nombreProceso, int numeroDePagina, int numeroDeFrame, boolean bitPresencia) {
        this.nombreProceso = nombreProceso;
        this.numeroDePagina = numeroDePagina;
        this.numeroDeFrame = numeroDeFrame;
        this.bitPresencia = bitPresencia;
        this.direccionDisco = -1;
    }

    public String getNombreProceso() {
        return this.nombreProceso;
    }

    public int getNumeroDePagina() {
        return this.numeroDePagina;
    }
    
    public int getNumeroDeFrame() {
        return this.numeroDeFrame;
    }    

    public int getDireccionDisco(){
        return direccionDisco;
    }
    
     public int setDireccionDisco(int direccion){
        direccionDisco = direccion;
        return direccionDisco;
    }   
     
    public boolean setBitPresencia(boolean bit) {
        bitPresencia = bit;
        return bit;
    }
    
    
    public boolean hayBitPresencia() {
        return bitPresencia;
    }
    
    public int setNumeroDeFrame(int num){
        numeroDeFrame = num;
        return numeroDeFrame;
    }

    
    
}