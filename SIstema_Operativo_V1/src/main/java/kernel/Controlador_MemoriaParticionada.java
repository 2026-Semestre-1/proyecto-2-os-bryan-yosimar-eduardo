package Controlador;
import Memoria.Modelo.Particion;

import java.util.ArrayList;
import java.util.List;

public class Controlador_MemoriaParticionada {
    List<Particion> particiones = new ArrayList<>();






    public List<Particion> getParticiones() {
        return particiones;
    }


    public int inicializarParticionesEstaticas(int tamanioMemoria, int cantidadProcesos, int inicioMemoria, int tamanioTotalRAM) throws Exception{
        int bloques = 0;
        int inicio = inicioMemoria;
        bloques = (tamanioMemoria - inicioMemoria) / cantidadProcesos;
        for(int i = 0; i < cantidadProcesos; i++) {
            int fin = inicio + bloques - 1;
            if(fin > tamanioTotalRAM) {
                throw new Exception("Se sobrepasa el tamaño total de la RAM. No se pueden crear más particiones.");
            }
            particiones.add(new Particion(i, inicio, fin));
            inicio = fin + 1;
            System.out.println("Part " + i + ": inicio=" + inicio + " fin=" + fin);
        }

        return particiones.size();
    }

    public int asignarProcesoEstatico(int pid, int tamanioProceso, String nombre) {
        for (Particion particion : particiones) {
            if (particion.procesoAsignado == -1 && particion.tamano >= tamanioProceso) {
                particion.procesoAsignado = pid;
                System.out.println("Proceso " + nombre + " asignado a Partición " + particion.id);
                return particion.id;
            }
        }
        System.out.println("No se pudo asignar el proceso " + nombre + ". No hay partición disponible.");
        return -1; // No se pudo asignar
    }

    public int liberarProcesoEstatico(int pid) {
        for (Particion particion : particiones) {
            if (particion.procesoAsignado == pid) {
                System.out.println("Proceso con PID " + pid + " liberado de Partición " + particion.id);
                particion.procesoAsignado = -1;
                return particion.id;
            }
        }
        System.out.println("No se pudo liberar el proceso con PID " + pid + ". No se encontró en ninguna partición.");
        return -1; // No se pudo liberar
    }




    public int inicializarParticionesFijasDistribucion(int inicioMemoria, int tamanioMemoria, int tamanioTotalRAM) throws Exception{
        int inicio = inicioMemoria; // Esto donde inicia el usuario
        int disponible = tamanioMemoria; // Esto seria la memoria disponbible de parte del usuario
        int particion1 = (int)(disponible * 0.08);
        int particion2 = (int)(disponible * 0.12);
        int particion3 = (int)(disponible * 0.17);
        int particion4 = (int)(disponible * 0.25);
        int particion5 = disponible - particion1 - particion2 - particion3 - particion4;
        List<Integer> particiones2 = new ArrayList<>();
        particiones2.add(particion1);
        particiones2.add(particion2);
        particiones2.add(particion3);
        particiones2.add(particion4);
        particiones2.add(particion5);
        for(int i = 0; i < 5; i++) {
            int fin = inicio + particiones2.get(i) - 1;
            if(fin > tamanioTotalRAM) {
                throw new Exception("Se sobrepasa el tamaño total de la RAM. No se pueden crear más particiones.");
            }
            particiones.add(new Particion(i, inicio, fin));
            System.out.println("Part " + i + ": inicio=" + inicio + " fin=" + fin);
            inicio = fin + 1;
        }         
        return particiones.size();
    }  
    
    
    public int crearMemoriaDinamica(int posInicial, int tamanioProceso, int tamanioTotalRAM, int espacioUtilizable) throws Exception {
        int inicio = espacioUtilizable; // Esto es el fin del proceso anterior + 1 para empezar correctamente
        int fin = inicio + tamanioProceso - 1;    
        if(fin > tamanioTotalRAM) {
            throw new Exception("Se sobrepasa el tamaño total de la RAM. No se pueden crear más particiones.");
        }   
        particiones.add(new Particion(particiones.size(),inicio, fin));
        System.out.println("Part " + (particiones.size() - 1) + ": inicio=" + inicio + " fin=" + fin);
        return particiones.size();

    } 

    // Me FALTA liberar memoria dinamica bueno compactación 

    public int liberarMemoriaDinamica(int pid) {
        for (Particion particion : particiones) {
            if (particion.procesoAsignado == pid) {
                System.out.println("Proceso con PID " + pid + " liberado de Partición " + particion.id);
                particion.procesoAsignado = -1;
                return particion.id;
            }
        }
        System.out.println("No se pudo liberar el proceso con PID " + pid + ". No se encontró en ninguna partición.");
        return -1; // No se pudo liberar
    }


    public int reasignacionIdsMemoriaDinamica() {
        // Los devolvemos a todos igual a cero 
        int i = 0;
        for (Particion particion : particiones) {
            particion.id = i;
            i++;
        }    
        return 0;
    }

    public int compactacionMemoriaDinamica() {
        int i = 0;
        while(i < particiones.size() - 1) {
            Particion actual = particiones.get(i);
            Particion siguiente = particiones.get(i + 1);
            if (actual.procesoAsignado == -1 && siguiente.procesoAsignado == -1) {
                int ini = actual.inicio; 
                int fin = siguiente.fin;  
                Particion nueva = new Particion(particiones.size(), ini, fin);
                particiones.remove(i+1);
                particiones.set(i, nueva);  
                reasignacionIdsMemoriaDinamica();              
            }
            else {
                i++;
            }
        }
        return 0;
    }

    public int bestFitMemoriaDinamica(int tamanoProceso) {
        int i = 10000; // doy un valor alto
        int idParticion = -1; // En caso de que no encuentre devuelvo -1 
        for (int j = 0; j < particiones.size(); j++) {
            Particion particion = particiones.get(j);
            Particion particion2 = particiones.get(j+1);
            if (particion.procesoAsignado == -1 && particion.tamano >= tamanoProceso) {
                if(particion.tamano < i) {
                    i = particion.tamano;
                    idParticion = particion.id;
                }
            
            }          
        }
     return idParticion;
    }   

}
