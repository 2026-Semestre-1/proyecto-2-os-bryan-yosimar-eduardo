package model;

import java.time.LocalTime;

public class BCP {

    private int PID;
    private String Estado;
    private String Prioridad;
    private String Mem_Init;
    private String Mem_End;
    private String PC;
    private String IR;
    private String AC;
    private String AX;
    private String BX;
    private String CX;
    private String DX;
    private String AH;
    private String AL;
    private String IO_STATUS;
    private String CPU_Asignada;
    private String Tiempo_Llegada;
    private String Tiempo_Inicio;
    private String Tiempo_Finalizacion;
    private String Tiempo_Ejecucion;
    private String Proximo_Proceso;
    private int[] Pila;
    private String nombre_Programa;
    private int espacio_Total_Programa;
    private LocalTime momento_creacion;
    private LocalTime momento_finalizacion;

    public BCP(int pPID, String pEstado, String pPrioridad, String pMem_Init, String pMem_End, String pPC, String pIR,
            String pAC, String pAX, String pBX, String pCX, String pDX, String pAH, String pAL, String pIO_STATUS,
            String pCPU_Asignada, String pTiempo_Llegada, String pTiempo_Inicio, String pTiempo_Finalizacion,
            String pTiempo_Ejecucion, String pProximo_Proceso, int[] pPila) {
        this.PID = pPID;
        this.Estado = pEstado;
        this.Prioridad = pPrioridad;
        this.Mem_Init = pMem_Init;
        this.Mem_End = pMem_End;
        this.PC = pPC;
        this.IR = pIR;
        this.AC = pAC;
        this.AX = pAX;
        this.BX = pBX;
        this.CX = pCX;
        this.DX = pDX;
        this.AH = pAH;
        this.AL = pAL;
        this.IO_STATUS = pIO_STATUS;
        this.CPU_Asignada = pCPU_Asignada;
        this.Tiempo_Llegada = pTiempo_Llegada;
        this.Tiempo_Inicio = pTiempo_Inicio;
        this.Tiempo_Finalizacion = pTiempo_Finalizacion;
        this.Tiempo_Ejecucion = pTiempo_Ejecucion;
        this.Proximo_Proceso = pProximo_Proceso;
        this.Pila = pPila;
    }

    public BCP(int pPID, String pPrioridad, String pCPU_Asignada, String pTiempo_Llegada, String pTiempo_Estimado,
            String pNombre_Programa, int pEspacio_Total_Programa) {
        this.PID = pPID;
        this.Estado = "Nuevo";
        this.Prioridad = pPrioridad;
        this.Mem_Init = "0";
        this.Mem_End = "0";
        this.PC = "0";
        this.IR = "0";
        this.AC = "0";
        this.AX = "0";
        this.BX = "0";
        this.CX = "0";
        this.DX = "0";
        this.AH = "0";
        this.AL = "0";
        this.IO_STATUS = "";
        this.CPU_Asignada = pCPU_Asignada;
        this.Tiempo_Llegada = pTiempo_Llegada;
        this.Tiempo_Inicio = "";
        this.Tiempo_Finalizacion = "";
        this.Tiempo_Ejecucion = pTiempo_Estimado;
        this.Proximo_Proceso = "-1";
        this.Pila = new int[0];
        this.nombre_Programa = pNombre_Programa;
        this.espacio_Total_Programa = pEspacio_Total_Programa;
    }

    public int getPID() {
        return PID;
    }

    public void setPID(int pPID) {
        PID = pPID;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String pEstado) {
        Estado = pEstado;
    }

    public String getPrioridad() {
        return Prioridad;
    }

    public void setPrioridad(String pPrioridad) {
        Prioridad = pPrioridad;
    }

    public String getMem_Init() {
        return Mem_Init;
    }

    public void setMem_Init(String pMem_Init) {
        Mem_Init = pMem_Init;
    }

    public String getMem_End() {
        return Mem_End;
    }

    public void setMem_End(String pMem_End) {
        Mem_End = pMem_End;
    }

    public String getPC() {
        return PC;
    }

    public void setPC(String pPC) {
        PC = pPC;
    }

    public String getIR() {
        return IR;
    }

    public void setIR(String pIR) {
        IR = pIR;
    }

    public String getAC() {
        return AC;
    }

    public void setAC(String pAC) {
        AC = pAC;
    }

    public String getAX() {
        return AX;
    }

    public void setAX(String pAX) {
        AX = pAX;
    }

    public String getBX() {
        return BX;
    }

    public void setBX(String pBX) {
        BX = pBX;
    }

    public String getCX() {
        return CX;
    }

    public void setCX(String pCX) {
        CX = pCX;
    }

    public String getDX() {
        return DX;
    }

    public void setDX(String pDX) {
        DX = pDX;
    }

    public String getAH() {
        return AH;
    }

    public void setAH(String pAH) {
        AH = pAH;
    }

    public String getAL() {
        return AL;
    }

    public void setAL(String pAL) {
        AL = pAL;
    }

    public String getIO_STATUS() {
        return IO_STATUS;
    }

    public void setIO_STATUS(String pIO_STATUS) {
        IO_STATUS = pIO_STATUS;
    }

    public String getCPU_Asignada() {
        return CPU_Asignada;
    }

    public void setCPU_Asignada(String pCPU_Asignada) {
        CPU_Asignada = pCPU_Asignada;
    }

    public String getTiempo_Llegada() {
        return Tiempo_Llegada;
    }

    public void setTiempo_Llegada(String pTiempo_Llegada) {
        Tiempo_Llegada = pTiempo_Llegada;
    }

    public String getTiempo_Inicio() {
        return Tiempo_Inicio;
    }

    public void setTiempo_Inicio(String pTiempo_Inicio) {
        Tiempo_Inicio = pTiempo_Inicio;
    }

    public String getTiempo_Finalizacion() {
        return Tiempo_Finalizacion;
    }

    public void setTiempo_Finalizacion(String pTiempo_Finalizacion) {
        Tiempo_Finalizacion = pTiempo_Finalizacion;
    }

    public String getProximo_Proceso() {
        return Proximo_Proceso;
    }

    public void setProximo_Proceso(String pProximo_Proceso) {
        Proximo_Proceso = pProximo_Proceso;
    }

    public int[] getPila() {
        return Pila;
    }

    public void setPila(int[] pPila) {
        Pila = pPila;
    }

    public String getNombre_Programa() {
        return nombre_Programa;
    }

    public int getTamanoProceso() {
        return espacio_Total_Programa;
    }

    public void setNombre_Programa(String nombre_Programa) {
        this.nombre_Programa = nombre_Programa;
    }

    public String getTiempo_Ejecucion() {
        return Tiempo_Ejecucion;
    }

    public void setTiempo_Ejecucion(String pTiempo_Ejecucion) {
        Tiempo_Ejecucion = pTiempo_Ejecucion;
    }

    public void set_momento_creacion(LocalTime pMomento_Creacion) {
        this.momento_creacion = pMomento_Creacion;
    }

    public void set_momento_finalizacion(LocalTime pMomento_Finalizacion) {
        this.momento_finalizacion = pMomento_Finalizacion;
    }

    public LocalTime get_momento_creacion() {
        return momento_creacion;
    }

    public LocalTime get_momento_finalizacion() {
        return momento_finalizacion;
    }

    public void mostrar_datos_bcp() {
        System.out.println("-> PID: " + this.PID);
        System.out.println("-> Estado: " + this.Estado);
        System.out.println("-> Prioridad: " + this.Prioridad);
        System.out.println("-> Mem_Init: " + this.Mem_Init);
        System.out.println("-> Mem_End: " + this.Mem_End);
        System.out.println("-> PC: " + this.PC);
        System.out.println("-> IR: " + this.IR);
        System.out.println("-> AC: " + this.AC);
        System.out.println("-> AX: " + this.AX);
        System.out.println("-> BX: " + this.BX);
        System.out.println("-> CX: " + this.CX);
        System.out.println("-> DX: " + this.DX);
        System.out.println("-> IO_STATUS: " + this.IO_STATUS);
        System.out.println("-> CPU_Asignada: " + this.CPU_Asignada);
        System.out.println("-> Tiempo_Llegada: " + this.Tiempo_Llegada);
        System.out.println("-> Tiempo_Inicio: " + this.Tiempo_Inicio);
        System.out.println("-> Tiempo_Finalizacion: " + this.Tiempo_Finalizacion);
        System.out.println("-> Tiempo_Ejecucion: " + this.Tiempo_Ejecucion);
        System.out.println("-> Proximo_Proceso: " + this.Proximo_Proceso);
        System.out.println("-> Pila: " + this.Pila);
        System.out.println("-> Nombre_Programa: " + this.nombre_Programa);
        System.out.println("-> Espacio_Total_Programa: " + this.espacio_Total_Programa);
    }
}
