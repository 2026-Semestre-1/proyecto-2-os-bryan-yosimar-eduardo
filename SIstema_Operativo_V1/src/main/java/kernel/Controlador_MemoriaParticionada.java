package kernel;
import Memoria.Modelo.Particion;
import model.Codigo_ASM;
import model.Instruccion;
import model.Memoria;
import java.util.ArrayList;
import java.util.List;
import Memoria.Modelo.Overlay;

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

    public int liberarProcesoEstaticoDinamico(int pid, Memoria memoria) {
        for (Particion particion : particiones) {
            if (particion.procesoAsignado == pid) {
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

    public int getTamanoParticionPorProceso(int pid) {
        for (Particion p : particiones) {
            if (p.procesoAsignado == pid) {
                return p.tamano;
            }
        }
        return -1;
    }


    public int inicializarParticionesFijasIguales(int inicioMemoria, int tamanioTotalRAM, int tamanoParticion) {
        particiones.clear();
        int inicio = inicioMemoria;
        int i = 0;
        while (true) {
            int fin = inicio + tamanoParticion - 1;
            if (fin >= tamanioTotalRAM) break;
            particiones.add(new Particion(i, inicio, fin));
            System.out.println("Part " + i + ": inicio=" + inicio + " fin=" + fin);
            inicio = fin + 1;
            i++;
        }
        return particiones.size();
    }


    public int inicializarParticionesFijasDistribucion(int inicioMemoria, int tamanioMemoria, int tamanioTotalRAM, List<Integer> porcentajes) throws Exception{
        particiones.clear();
        int inicio = inicioMemoria;
        for (int i = 0; i < porcentajes.size(); i++) {
            int tam = (int)(tamanioMemoria * (porcentajes.get(i) / 100.0));
            int fin = inicio + tam - 1;
            if (fin >= tamanioTotalRAM) {
                throw new Exception("Se sobrepasa el tamaño total de la RAM. No se pueden crear más particiones.");
            }
            particiones.add(new Particion(i, inicio, fin));
            System.out.println("Part " + i + ": inicio=" + inicio + " fin=" + fin + " (" + porcentajes.get(i) + "%)");
            inicio = fin + 1;
        }
        return particiones.size();
    }  

    public int asignarProcesoEstaticoDinamico(Codigo_ASM codigoASM, int pid, String nombre, Memoria memoria) {
        for (Particion particion : particiones) {
            if (particion.procesoAsignado == -1 && particion.tamano >= codigoASM.getContador_Intrucciones()) {
                particion.procesoAsignado = pid;
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

    public int moverProcesosRamDinamica(Memoria memoria){
        int i = 0;
        while (i < particiones.size() - 1) {
            if (particiones.get(i).procesoAsignado == -1 &&
                particiones.get(i + 1).procesoAsignado != -1) {

                Particion libre = particiones.get(i);
                Particion ocupada = particiones.get(i + 1);
                int tam = ocupada.tamano;
                int destino = libre.inicio;
                int origen = ocupada.inicio;

                for (int j = 0; j < tam; j++) {
                    String instr = memoria.getMemoria_Principal().get(origen + j);
                    memoria.getMemoria_Principal().put(destino + j, instr);
                    memoria.getMemoria_Principal().put(origen + j, "");
                }

                int origenMovido = particiones.get(i + 1).inicio;
                particiones.remove(i);
                Particion movida = particiones.get(i);
                movida.inicio = destino;
                movida.fin = destino + tam - 1;
                movida.tamano = tam;

                int pidMovido = movida.procesoAsignado;
                int posBCP = memoria.buscar_Posicion_BCP(pidMovido);
                if (posBCP != -1) {
                    int pcActual = Integer.parseInt(memoria.getMemoria_Principal().get(posBCP + 5));
                    int mov = destino - origenMovido;
                    memoria.getMemoria_Principal().put(posBCP + 3, String.valueOf(movida.inicio));
                    memoria.getMemoria_Principal().put(posBCP + 4, String.valueOf(movida.fin));
                    memoria.getMemoria_Principal().put(posBCP + 5, String.valueOf(pcActual + mov));
                }

                particiones.add(i + 1, new Particion(-1, origenMovido, origenMovido + tam - 1));
                reasignacionIdsMemoriaDinamica();

            } else {
                i++;
            }
        }
        return 0;
    }


    public int bestFitMemoriaDinamica(int tamanoProceso) {
        int mejorTamano = Integer.MAX_VALUE;
        int idParticion = -1;
        for (int j = 0; j < particiones.size(); j++) {
            Particion particion = particiones.get(j);
            if (particion.procesoAsignado == -1 && particion.tamano >= tamanoProceso) {
                if (particion.tamano < mejorTamano) {
                    mejorTamano = particion.tamano;
                    idParticion = particion.id;
                }
            }          
        }
        return idParticion;
    }  
    
    public int crearParticionDinamica(int tamanoProceso, int espacioUtilizable, int tamanioTotalRAM) throws Exception {
        int fin = espacioUtilizable + tamanoProceso - 1;
        if (fin > tamanioTotalRAM) {
            throw new Exception("Se sobrepasa el tamaño total de la RAM. No se pueden crear más particiones.");
        }
        particiones.add(new Particion(particiones.size(), espacioUtilizable, fin));
        System.out.println("Part " + (particiones.size() - 1) + ": inicio=" + espacioUtilizable + " fin=" + fin);
        return particiones.size() - 1;
    }

    public boolean hayParticionesEstaticasLibres(int tamanoProceso) {
        for(Particion particion : particiones){
            if (particion.tamano >= tamanoProceso && particion.procesoAsignado == -1){
                return true;
            }
        }
        return false;
    }


    public boolean hayParticionesDinamicasLibres(int tamanoProceso, Memoria memoria) {
        for (Particion p : particiones) {
            if (p.procesoAsignado == -1 && p.tamano >= tamanoProceso) {
                return true;
            }
        }
        int espacioLibre;
        if (particiones.isEmpty()) {
            espacioLibre = memoria.getEspacio_Usuario();
        } else {
            int ultimoFin = particiones.get(particiones.size() - 1).fin;
            espacioLibre = (memoria.getEspacio_Total() - 1) - ultimoFin;
        }
        return espacioLibre >= tamanoProceso;
    }


    public boolean hayParticionesEstaticasDinamicasLibres(int tamanoProceso) {
        for(Particion particion : particiones){
            if (particion.tamano >= tamanoProceso && particion.procesoAsignado == -1){
                return true;
            }
        }
        return false;
    }    

    public int crearOverlays(int cantidadOverlays, int particionIndex) {
        Particion p = particiones.get(particionIndex);
        for (int x = 0; x < cantidadOverlays; x++){
            p.overlays.add(new Overlay(x + 1));
        }
        return cantidadOverlays;
    }

}
