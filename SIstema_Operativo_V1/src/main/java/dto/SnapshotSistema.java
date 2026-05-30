package dto;

import java.util.List;
import java.util.Map;

import model.BCP;
import model.Memoria;
import model.MemoriaPaginada;
import model.Almacenamiento;
import Memoria.Modelo.Particion;

public class SnapshotSistema {

    public final Memoria memoria;
    public final Almacenamiento almacenamiento;
    public final Map<Integer, String> procesos;
    public final BCP bcpActual;
    public final List<BCP> procesosTerminados;
    public final boolean bloqueoInput;
    public final MemoriaPaginada memoriaPaginada;
    public final List<Particion> particiones;

    public SnapshotSistema(Memoria memoria, Almacenamiento almacenamiento,
            Map<Integer, String> procesos, BCP bcpActual,
            List<BCP> procesosTerminados, boolean bloqueoInput,
            MemoriaPaginada memoriaPaginada) {
        this.memoria = memoria;
        this.almacenamiento = almacenamiento;
        this.procesos = procesos;
        this.bcpActual = bcpActual;
        this.procesosTerminados = procesosTerminados;
        this.bloqueoInput = bloqueoInput;
        this.memoriaPaginada = memoriaPaginada;
        this.particiones = null;
    }

    public SnapshotSistema(Memoria memoria, Almacenamiento almacenamiento,
            Map<Integer, String> procesos, BCP bcpActual,
            List<BCP> procesosTerminados, boolean bloqueoInput,
            MemoriaPaginada memoriaPaginada, List<Particion> particiones) {
        this.memoria = memoria;
        this.almacenamiento = almacenamiento;
        this.procesos = procesos;
        this.bcpActual = bcpActual;
        this.procesosTerminados = procesosTerminados;
        this.bloqueoInput = bloqueoInput;
        this.memoriaPaginada = memoriaPaginada;
        this.particiones = particiones;
    }
}
