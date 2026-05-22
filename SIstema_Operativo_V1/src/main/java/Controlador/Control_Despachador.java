/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.BCP;
import Modelo.CPU;
import Modelo.Memoria;

/**
 *
 * @author edurg
 */
public class Control_Despachador {

    public static void despachador(CPU pCPU_Actual, Memoria pMemoria_Principal, int PID) {

        // Mediante esta funcion se realizara la captura de los datos de la BCP
        // del proceso que se va a ejecutar y se los carga a la CPU.
        // Este funcionamiento supone que la CPU que se pasa es una referencia a la CPU
        // con la que se esta trabajando.

        // 1. Obtenemos los datos de la BCP del proceso mediante el PID.

        BCP bcp_Proceso = pMemoria_Principal.obtener_Datos_BCP(PID);

        // 2. Actualizamos los registros de la CPU con los datos de la BCP.
        pCPU_Actual.setPC(Integer.valueOf(bcp_Proceso.getPC()));
        pCPU_Actual.setIR(bcp_Proceso.getIR());
        pCPU_Actual.setAC(Integer.valueOf(bcp_Proceso.getAC()));
        pCPU_Actual.setAX(Integer.valueOf(bcp_Proceso.getAX()));
        pCPU_Actual.setBX(Integer.valueOf(bcp_Proceso.getBX()));
        pCPU_Actual.setCX(Integer.valueOf(bcp_Proceso.getCX()));
        pCPU_Actual.setDX(bcp_Proceso.getDX());
        pCPU_Actual.setAH(bcp_Proceso.getAH());
        pCPU_Actual.setAL(bcp_Proceso.getAL());

        // Estas me parecen que quedan pendientes para el siguiente proyecto.
        // pCPU_Actual.setIO_STATUS(bcp_Proceso.getIO_STATUS());
        // pCPU_Actual.setFlag_CMP(Integer.valueOf(bcp_Proceso.getFlag_CMP()));

    }

    public static void actualizar_Datos_BCP_Ejecucion(CPU pCPU_Actual, Memoria pMemoria_Principal, int PID) {

        BCP bcp_Proceso = pMemoria_Principal.obtener_Datos_BCP(PID);

        bcp_Proceso.setPC(String.valueOf(pCPU_Actual.getPC()));
        bcp_Proceso.setIR(pCPU_Actual.getIR());
        bcp_Proceso.setAC(String.valueOf(pCPU_Actual.getAC()));
        bcp_Proceso.setAX(String.valueOf(pCPU_Actual.getAX()));
        bcp_Proceso.setBX(String.valueOf(pCPU_Actual.getBX()));
        bcp_Proceso.setCX(String.valueOf(pCPU_Actual.getCX()));
        bcp_Proceso.setDX(pCPU_Actual.getDX());
        bcp_Proceso.setAH(String.valueOf(pCPU_Actual.getAH()));
        bcp_Proceso.setAL(pCPU_Actual.getAL());
    }

}
