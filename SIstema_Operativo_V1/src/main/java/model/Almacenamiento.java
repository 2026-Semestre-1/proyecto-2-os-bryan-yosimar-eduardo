package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Almacenamiento {

    private Map<Integer, String> Memoria_Secundaria;
    private int tamano_Total = 0;
    private int espacio_Programas = 0;
    private int espacio_Memoria_Virtual = 0;
    private int espacio_Indices = 0;
    private int espacio_Usado_Programas = 0;
    private int espacio_Usado_Memoria_Virtual = 0;
    private int espacio_Usado_Indices = 0;
    private int posicion_Programas = 0;
    private int posicion_Memoria_Virtual = 0;
    private int posicion_Indices = 0;

    public Almacenamiento(int pTamano_total, int pEspacio_Memoria_Virtual, int pEspacio_Indices) {
        this.Memoria_Secundaria = new HashMap<>(pTamano_total);
        this.tamano_Total = pTamano_total; this.espacio_Memoria_Virtual = pEspacio_Memoria_Virtual;
        this.espacio_Indices = pEspacio_Indices; this.espacio_Programas = pTamano_total - pEspacio_Memoria_Virtual - pEspacio_Indices;
        this.posicion_Indices = 0; this.posicion_Memoria_Virtual = this.espacio_Indices;
        this.posicion_Programas = this.posicion_Memoria_Virtual + this.espacio_Memoria_Virtual;
    }

    public Map<Integer, String> getMemoria_Secundaria() { return Memoria_Secundaria; }
    public int getTamano_Total() { return tamano_Total; }
    public int getEspacio_Indices() { return espacio_Indices; }
    public int getEspacio_Programas() { return espacio_Programas; }
    public int getEspacio_Memoria_Virtual() { return espacio_Memoria_Virtual; }
    public int getEspacio_Usado_Programas() { return espacio_Usado_Programas; }
    public int getEspacio_Usado_Memoria_Virtual() { return espacio_Usado_Memoria_Virtual; }
    public int getEspacio_Usado_Indices() { return espacio_Usado_Indices; }
    public int getPosicion_Indices() { return posicion_Indices; }
    public int getPosicion_Memoria_Virtual() { return posicion_Memoria_Virtual; }
    public int getPosicion_Programas() { return posicion_Programas; }
    public void setMemoria_Secundaria(Map<Integer, String> pMemoria_Secundaria) { this.Memoria_Secundaria = pMemoria_Secundaria; }
    public void setTamano_Total(int pTamano_total) { this.tamano_Total = pTamano_total; }
    public void setEspacio_Programas(int pEspacio_Programas) { this.espacio_Programas = pEspacio_Programas; }
    public void setEspacio_Memoria_Virtual(int pEspacio_Memoria_Virtual) { this.espacio_Memoria_Virtual = pEspacio_Memoria_Virtual; }
    public void setEspacio_Usado_Programas(int pEspacio_Usado_Programas) { this.espacio_Usado_Programas = pEspacio_Usado_Programas; }
    public void setEspacio_Usado_Memoria_Virtual(int pEspacio_Usado_Memoria_Virtual) { this.espacio_Usado_Memoria_Virtual = pEspacio_Usado_Memoria_Virtual; }
    public void setEspacio_Usado_Indices(int pEspacio_Usado_Indices) { this.espacio_Usado_Indices = pEspacio_Usado_Indices; }
    public void setPosicion_Indices(int pPosicion_Indices) { this.posicion_Indices = pPosicion_Indices; }
    public void setPosicion_Memoria_Virtual(int pPosicion_Memoria_Virtual) { this.posicion_Memoria_Virtual = pPosicion_Memoria_Virtual; }
    public void setPosicion_Programas(int pPosicion_Programas) { this.posicion_Programas = pPosicion_Programas; }

    public int agregar_Indice(String pNombre_Archivo, int pPosicion_Inicial, int pPosicion_Final) {
        if (this.espacio_Indices > this.espacio_Usado_Indices) {
            Memoria_Secundaria.put(this.posicion_Indices, pNombre_Archivo + " : " + pPosicion_Inicial + " - " + pPosicion_Final);
            this.posicion_Indices++; this.espacio_Usado_Indices++; return 1;
        }
        return -1;
    }

    public int asignar_Memoria_A_Programa(Codigo_ASM pCodigo_ASM, String pNombre_Archivo) {
        if (this.espacio_Programas > (this.espacio_Usado_Programas + pCodigo_ASM.getContador_Intrucciones())) {
            List<Instruccion> instrucciones = pCodigo_ASM.getInstrucciones();
            int pos_Inicial = this.posicion_Programas;
            for (Instruccion instruccion_actual : instrucciones) {
                Memoria_Secundaria.put(this.posicion_Programas, instruccion_actual.get_Intruccion_Completa());
                this.posicion_Programas++; this.espacio_Usado_Programas++;
            }
            agregar_Indice(pNombre_Archivo, pos_Inicial, this.posicion_Programas - 1); return 0;
        }
        return -1;
    }

    public int modificar_valor_en_memoria(int pPosicion, String pValor) {
        if (Memoria_Secundaria.containsKey(pPosicion)) { Memoria_Secundaria.replace(pPosicion, pValor); return 0; }
        else { return 1; }
    }

    public Map<String, List<Integer>> optener_Indices() {
        System.out.println("Obteniendo los indices de archivos.");
        Map<String, List<Integer>> indices = new HashMap<>();
        for (int i = 0; i < espacio_Indices; i++) {
            String dato = Memoria_Secundaria.get(i);
            if (dato != null && dato != "") {
                System.out.println("-> Dato: " + dato);
                String[] partes = dato.split(":");
                if (partes.length < 2) { System.out.println("Formato inv\u00e1lido en \u00edndice: " + dato); continue; }
                String nombre_archivo = partes[0].trim();
                String[] pos_Almacenamiento = partes[1].split("-");
                if (pos_Almacenamiento.length < 2) { System.out.println("Formato inv\u00e1lido en posiciones: " + partes[1]); continue; }
                int posicion_inicial = Integer.parseInt(pos_Almacenamiento[0].trim());
                int posicion_final = Integer.parseInt(pos_Almacenamiento[1].trim());
                List<Integer> posiciones = new ArrayList<>(); posiciones.add(posicion_inicial); posiciones.add(posicion_final);
                indices.put(nombre_archivo, posiciones);
            }
        }
        return indices;
    }

    public int actualizarIndice(String nombreArchivo, int nuevoInicio, int nuevoFin) {
        for (int i = 0; i < espacio_Indices; i++) {
            String dato = Memoria_Secundaria.get(i);
            if (dato != null && !dato.trim().isEmpty()) {
                String[] partes = dato.split(":"); String nombre = partes[0].trim();
                if (nombre.equals(nombreArchivo)) { Memoria_Secundaria.put(i, nombreArchivo + " : " + nuevoInicio + " - " + nuevoFin); return 0; }
            }
        }
        return -1;
    }

    public int eliminarIndice(String nombreArchivo) {
        for (int i = 0; i < espacio_Indices; i++) {
            String dato = Memoria_Secundaria.get(i);
            if (dato != null && dato.trim().startsWith(nombreArchivo + " :")) { Memoria_Secundaria.put(i, ""); this.espacio_Usado_Indices--; return 0; }
        }
        return -1;
    }

    public Codigo_ASM optener_Programa(int pPosicion_Inicial, int pPosicion_Final) {
        Codigo_ASM codigo_ASM = new Codigo_ASM();
        for (int i = pPosicion_Inicial; i <= pPosicion_Final; i++) { Instruccion intruc = new Instruccion(Memoria_Secundaria.get(i)); codigo_ASM.agregar_Intruccion(intruc); }
        return codigo_ASM;
    }

    public String optener_Instruccion(int pPosicion) { return Memoria_Secundaria.get(pPosicion); }
    public boolean existe_Archivo(String pNombre_Archivo) { return optener_Indices().containsKey(pNombre_Archivo); }
    public int espacio_Disponible_Indices() { return this.espacio_Indices - this.espacio_Usado_Indices; }
    public int espacio_Disponible_Programas() { return this.espacio_Programas - this.espacio_Usado_Programas; }
    public int espacio_Disponible_Memoria_Virtual() { return this.espacio_Memoria_Virtual - this.espacio_Usado_Memoria_Virtual; }

    public void mostrar_Memoria() {
        System.out.println("Mostrando memoria.\n");
        for (int i = 0; i < tamano_Total; i++) { String datos = Memoria_Secundaria.get(i) != null ? Memoria_Secundaria.get(i) : " "; System.out.println("Clave: " + i + ", Valor: " + datos); }
        System.out.println("Memoria mostrada.\n");
    }
}
