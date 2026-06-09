package kernel;

import java.util.ArrayList;
import java.util.List;

import Memoria.Modelo.BloqueMemoria;
import Memoria.Modelo.Particion;
import model.Codigo_ASM;
import model.Instruccion;
import model.Memoria;

public class ControladorMemoriaBuddy {
    private List<BloqueMemoria> bloques = new ArrayList<>();
    private int inicioMemoria;

    public List<BloqueMemoria> getBloques() {
        return bloques;
    }

    public List<Particion> getParticiones() {
        List<Particion> resultado = new ArrayList<>();
        for (BloqueMemoria b : bloques) {
            Particion p = new Particion(b.id, b.inicio, b.fin);
            p.procesoAsignado = b.procesoAsignado;
            resultado.add(p);
        }
        return resultado;
    }

    private int mayorPotencia2(int n) {
        int pot = 1;
        while (pot * 2 <= n) {
            pot *= 2;
        }
        return pot;
    }

    private int redondearPotencia2(int n) {
        int pot = 1;
        while (pot < n) {
            pot *= 2;
        }
        return pot;
    }

    public void inicializar(int pInicioMemoria, int espacioUsuario, int tamanoTotalRAM) {
        this.inicioMemoria = pInicioMemoria;
        bloques.clear();
        int tamanoGestionado = mayorPotencia2(espacioUsuario);
        int fin = pInicioMemoria + tamanoGestionado - 1;
        if (fin >= tamanoTotalRAM) {
            fin = tamanoTotalRAM - 1;
        }
        bloques.add(new BloqueMemoria(0, pInicioMemoria, fin));
        System.out.println("Buddy: bloque inicial inicio=" + pInicioMemoria + " fin=" + fin + " tamano=" + (fin - pInicioMemoria + 1));
    }

    public boolean hayEspacioLibre(int tamanoProceso) {
        int necesario = redondearPotencia2(tamanoProceso);
        for (BloqueMemoria b : bloques) {
            if (b.procesoAsignado == -1 && b.tamano >= necesario) {
                return true;
            }
        }
        return false;
    }

    public int asignarProceso(Codigo_ASM codigoASM, int pid, String nombre, Memoria memoria) {
        int necesario = redondearPotencia2(codigoASM.getContador_Intrucciones());

        int mejorIdx = -1;
        int mejorTamano = Integer.MAX_VALUE;
        for (int i = 0; i < bloques.size(); i++) {
            BloqueMemoria b = bloques.get(i);
            if (b.procesoAsignado == -1 && b.tamano >= necesario && b.tamano < mejorTamano) {
                mejorTamano = b.tamano;
                mejorIdx = i;
            }
        }

        if (mejorIdx == -1) {
            return -1;
        }

        BloqueMemoria bloque = bloques.get(mejorIdx);

        while (bloque.tamano > necesario) {
            int mitad = bloque.tamano / 2;
            int mitadInicio = bloque.inicio + mitad;
            BloqueMemoria nuevo = new BloqueMemoria(bloques.size(), mitadInicio, bloque.fin);
            bloque.fin = bloque.inicio + mitad - 1;
            bloque.tamano = mitad;
            bloques.add(mejorIdx + 1, nuevo);
            if (bloques.indexOf(bloque) != mejorIdx) {
                mejorIdx = bloques.indexOf(bloque);
            }
        }

        bloque = bloques.get(mejorIdx);
        bloque.procesoAsignado = pid;

        int pos = bloque.inicio;
        for (Instruccion inst : codigoASM.getInstrucciones()) {
            memoria.getMemoria_Principal().put(pos, inst.get_Intruccion_Completa());
            pos++;
        }

        System.out.println("Buddy: proceso " + nombre + " PID=" + pid
                + " asignado a inicio=" + bloque.inicio + " fin=" + (bloque.inicio + necesario - 1)
                + " tamano=" + necesario);
        return bloque.id;
    }

    public int liberarProceso(int pid, Memoria memoria) {
        for (int i = 0; i < bloques.size(); i++) {
            BloqueMemoria b = bloques.get(i);
            if (b.procesoAsignado == pid) {

                for (int j = b.inicio; j <= b.fin; j++) {
                    memoria.getMemoria_Principal().put(j, "");
                }

                b.procesoAsignado = -1;
                System.out.println("Buddy: liberado PID=" + pid + " bloque inicio=" + b.inicio + " fin=" + b.fin);

                fusionar(i);
                return i;
            }
        }
        return -1;
    }

    private void fusionar(int idx) {
        if (idx < 0 || idx >= bloques.size()) return;
        BloqueMemoria actual = bloques.get(idx);
        if (actual.procesoAsignado != -1) return;

        int buddyInicio = actual.inicio ^ actual.tamano;

        for (int i = 0; i < bloques.size(); i++) {
            if (i == idx) continue;
            BloqueMemoria otro = bloques.get(i);
            if (otro.procesoAsignado == -1 && otro.tamano == actual.tamano && otro.inicio == buddyInicio) {

                int nuevoInicio = Math.min(actual.inicio, otro.inicio);
                int nuevoFin = Math.max(actual.fin, otro.fin);

                BloqueMemoria fusionado = new BloqueMemoria(bloques.size(), nuevoInicio, nuevoFin);

                int minIdx = Math.min(idx, i);
                int maxIdx = Math.max(idx, i);
                bloques.remove(maxIdx);
                bloques.remove(minIdx);
                bloques.add(minIdx, fusionado);

                fusionar(minIdx);
                return;
            }
        }
    }

    public int getInicioParticionPorProceso(int pid) {
        for (BloqueMemoria b : bloques) {
            if (b.procesoAsignado == pid) {
                return b.inicio;
            }
        }
        return -1;
    }

    public int getTamanoParticionPorProceso(int pid) {
        for (BloqueMemoria b : bloques) {
            if (b.procesoAsignado == pid) {
                return b.tamano;
            }
        }
        return -1;
    }
}
