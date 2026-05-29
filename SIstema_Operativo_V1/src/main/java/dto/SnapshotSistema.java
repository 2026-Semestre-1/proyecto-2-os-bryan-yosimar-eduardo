package dto;

import java.util.List;
import java.util.Map;

import model.BCP;
import model.Memoria;
import model.Almacenamiento;

public class SnapshotSistema {

    public final Memoria memoria;
    public final Almacenamiento almacenamiento;
    public final Map<Integer, String> procesos;
    public final BCP bcpActual;
    public final List<BCP> procesosTerminados;
    public final boolean bloqueoInput;

    public SnapshotSistema(Memoria memoria, Almacenamiento almacenamiento,
            Map<Integer, String> procesos, BCP bcpActual,
            List<BCP> procesosTerminados, boolean bloqueoInput) {
        this.memoria = memoria;
        this.almacenamiento = almacenamiento;
        this.procesos = procesos;
        this.bcpActual = bcpActual;
        this.procesosTerminados = procesosTerminados;
        this.bloqueoInput = bloqueoInput;
    }
}
