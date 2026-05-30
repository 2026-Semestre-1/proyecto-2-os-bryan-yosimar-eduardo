package kernel;
import Memoria.Modelo.Particion;
import model.Codigo_ASM;
import model.Instruccion;
import model.Memoria;

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

    public int asignarProcesoEstatico(Codigo_ASM codigoASM, int pid, String nombre, Memoria memoria) {
        for (Particion particion : particiones) {
            if (particion.procesoAsignado == -1 && particion.tamano >= codigoASM.getContador_Intrucciones()) {
                particion.procesoAsignado = pid;
                // Escribir instrucciones en RAM en posiciones absolutas
                int pos = particion.inicio;
                for (Instruccion inst : codigoASM.getInstrucciones()) {
                    memoria.getMemoria_Principal().put(pos, inst.get_Intruccion_Completa());
                    pos++;
                }
                System.out.println("Proceso " + nombre + " asignado a Partición " + particion.id);
                return particion.id;
            }
        }
        return -1;
    }

    /*
    public String obtenerInstruccionEstaticaIgual(int posicion){
        for ( Particion particion : particiones) {
            if (particion.procesoAsignado != -1 && posicion >= particion.inicio && posicion <= particion.fin) {
                int indiceInstruccion = posicion - particion.inicio;
                return particion.instrucciones.get(indiceInstruccion);
            }
            
        }

    }
    */

    public int liberarProcesoEstatico(int pid, Memoria memoria) {
        for (Particion particion : particiones) {
            if (particion.procesoAsignado == pid) {
                // Limpiar instrucciones en RAM
                for (int i = particion.inicio; i <= particion.fin; i++) {
                    memoria.getMemoria_Principal().put(i, "");
                }
                particion.procesoAsignado = -1;
                System.out.println("Proceso con PID " + pid + " liberado de Partición " + particion.id);
                return particion.id;
            }
        }
        return -1;
    }

    public int getInicioParticion(int idParticion) {
        return particiones.get(idParticion).inicio;
    }

    public int getInicioParticionPorProceso(int pid) {
        for (Particion p : particiones) {
            if (p.procesoAsignado == pid) {
                return p.inicio;
            }
        }
        return -1;
    }


    public int inicializarParticionesFijasIguales(int inicioMemoria, int tamanioMemoria, int tamanioTotalRAM) {
        int inicio = inicioMemoria; // Esto donde inicia el usuario
        int disponible = tamanioMemoria; // Esto seria la memoria disponbible de parte del usuario  
        int numDivision = 4;
        int tamanoParticion = disponible / numDivision; // Aqui lo divido en 4 partes iguales pero realmente podemos poner la que queramos
        for(int i = 0; i < numDivision; i++) {
            int fin = inicio + tamanoParticion - 1;
            if(fin > tamanioTotalRAM) {
                System.out.println("Se sobrepasa el tamaño total de la RAM. No se pueden crear más particiones.");
                break;
            }
            particiones.add(new Particion(i, inicio, fin));
            System.out.println("Part " + i + ": inicio=" + inicio + " fin=" + fin);
            inicio = fin + 1;
        } 
        return particiones.size();     
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
            if (particion.procesoAsignado == -1 && particion.tamano >= tamanoProceso) {
                if(particion.tamano < i) {
                    i = particion.tamano;
                    idParticion = particion.id;
                }
            
            }          
        }
     return idParticion;
    }   

    public boolean hayParticionesEstaticasLibres(int tamanoProceso) {
        for(Particion particion : particiones){
            if (particion.tamano >= tamanoProceso && particion.procesoAsignado == -1){
                return true;
            }
        }
        return false;
    }

}
