package dto;

import java.util.List;
import java.util.Map;

import model.BCP;
import model.CPU;
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
    public final List<BCP> todosLosBCP;
    public final List<String> pendientes;
    public final List<CPU> estadoCPUs;

    public SnapshotSistema(Memoria memoria, Almacenamiento almacenamiento,
            Map<Integer, String> procesos, BCP bcpActual,
            List<BCP> procesosTerminados, boolean bloqueoInput,
            MemoriaPaginada memoriaPaginada) {
        this(memoria, almacenamiento, procesos, bcpActual,
            procesosTerminados, bloqueoInput, memoriaPaginada,
            null, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            new java.util.ArrayList<>());
    }

    public SnapshotSistema(Memoria memoria, Almacenamiento almacenamiento,
            Map<Integer, String> procesos, BCP bcpActual,
            List<BCP> procesosTerminados, boolean bloqueoInput,
            MemoriaPaginada memoriaPaginada, List<Particion> particiones) {
        this(memoria, almacenamiento, procesos, bcpActual,
            procesosTerminados, bloqueoInput, memoriaPaginada,
            particiones, new java.util.ArrayList<>(), new java.util.ArrayList<>(),
            new java.util.ArrayList<>());
    }

    public SnapshotSistema(Memoria memoria, Almacenamiento almacenamiento,
            Map<Integer, String> procesos, BCP bcpActual,
            List<BCP> procesosTerminados, boolean bloqueoInput,
            MemoriaPaginada memoriaPaginada, List<Particion> particiones,
            List<BCP> todosLosBCP, List<String> pendientes,
            List<CPU> estadoCPUs) {
        this.memoria = memoria;
        this.almacenamiento = almacenamiento;
        this.procesos = procesos;
        this.bcpActual = bcpActual;
        this.procesosTerminados = procesosTerminados;
        this.bloqueoInput = bloqueoInput;
        this.memoriaPaginada = memoriaPaginada;
        this.particiones = particiones;
        this.todosLosBCP = todosLosBCP;
        this.pendientes = pendientes;
        this.estadoCPUs = estadoCPUs;
    }
}
