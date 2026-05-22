/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author edurg
 */
public class Almacenamiento {

    private Map<Integer, String> Memoria_Secundaria; // -> Estructura que representara toda la memoria secundaria.

    // Valores para los tamaños asignados a cada una de las secciones del
    // alamacenamiento.
    private int tamano_Total = 0;

    private int espacio_Programas = 0;

    private int espacio_Memoria_Virtual = 0;

    private int espacio_Indices = 0;

    // Valores para los espacio utilizados actualmente por cada una de las
    // secciones.

    private int espacio_Usado_Programas = 0;

    private int espacio_Usado_Memoria_Virtual = 0;

    private int espacio_Usado_Indices = 0;

    // Valores para indicar la ultima posicion en la que se agrego un dato nuevo en
    // cada una de las secciones.

    private int posicion_Programas = 0;

    private int posicion_Memoria_Virtual = 0;

    private int posicion_Indices = 0;

    public Almacenamiento(int pTamano_total, int pEspacio_Memoria_Virtual, int pEspacio_Indices) { // Podemos asumir que
                                                                                                   // como minimo el
                                                                                                   // espacio de indices
                                                                                                   // es 10

        this.Memoria_Secundaria = new HashMap<>(pTamano_total); // Crear la memoria secundaria con un tamaño fijo.

        this.tamano_Total = pTamano_total;

        this.espacio_Memoria_Virtual = pEspacio_Memoria_Virtual;

        this.espacio_Indices = pEspacio_Indices;

        this.espacio_Programas = pTamano_total - pEspacio_Memoria_Virtual - pEspacio_Indices;

        this.posicion_Indices = 0;
        this.posicion_Memoria_Virtual = this.espacio_Indices;
        this.posicion_Programas = this.posicion_Memoria_Virtual + this.espacio_Memoria_Virtual;

    }

    public Map<Integer, String> getMemoria_Secundaria() {
        return Memoria_Secundaria;
    }

    public int getTamano_Total() {
        return tamano_Total;
    }

    public int getEspacio_Indices() {
        return espacio_Indices;
    }

    public int getEspacio_Programas() {
        return espacio_Programas;
    }

    public int getEspacio_Memoria_Virtual() {
        return espacio_Memoria_Virtual;
    }

    public int getEspacio_Usado_Programas() {
        return espacio_Usado_Programas;
    }

    public int getEspacio_Usado_Memoria_Virtual() {
        return espacio_Usado_Memoria_Virtual;
    }

    public int getEspacio_Usado_Indices() {
        return espacio_Usado_Indices;
    }

    public int getPosicion_Indices() {
        return posicion_Indices;
    }

    public int getPosicion_Memoria_Virtual() {
        return posicion_Memoria_Virtual;
    }

    public int getPosicion_Programas() {
        return posicion_Programas;
    }

    public void setMemoria_Secundaria(Map<Integer, String> pMemoria_Secundaria) {
        this.Memoria_Secundaria = pMemoria_Secundaria;
    }

    public void setTamano_Total(int pTamano_total) {
        this.tamano_Total = pTamano_total;
    }

    public void setEspacio_Programas(int pEspacio_Programas) {
        this.espacio_Programas = pEspacio_Programas;
    }

    public void setEspacio_Memoria_Virtual(int pEspacio_Memoria_Virtual) {
        this.espacio_Memoria_Virtual = pEspacio_Memoria_Virtual;
    }

    public void setEspacio_Usado_Programas(int pEspacio_Usado_Programas) {
        this.espacio_Usado_Programas = pEspacio_Usado_Programas;
    }

    public void setEspacio_Usado_Memoria_Virtual(int pEspacio_Usado_Memoria_Virtual) {
        this.espacio_Usado_Memoria_Virtual = pEspacio_Usado_Memoria_Virtual;
    }

    public void setEspacio_Usado_Indices(int pEspacio_Usado_Indices) {
        this.espacio_Usado_Indices = pEspacio_Usado_Indices;
    }

    public void setPosicion_Indices(int pPosicion_Indices) {
        this.posicion_Indices = pPosicion_Indices;
    }

    public void setPosicion_Memoria_Virtual(int pPosicion_Memoria_Virtual) {
        this.posicion_Memoria_Virtual = pPosicion_Memoria_Virtual;
    }

    public void setPosicion_Programas(int pPosicion_Programas) {
        this.posicion_Programas = pPosicion_Programas;
    }

    public int agregar_Indice(String pNombre_Archivo, int pPosicion_Inicial, int pPosicion_Final) {// ej. Posicion: 0 ->
                                                                                                   // Programa_1 | 100 -
                                                                                                   // 115

        if (this.espacio_Indices > this.espacio_Usado_Indices) {
            Memoria_Secundaria.put(this.posicion_Indices,
                    pNombre_Archivo + " : " + pPosicion_Inicial + " - " + pPosicion_Final);
            this.posicion_Indices++;
            this.espacio_Usado_Indices++;
            return 1; // Se pudo agregar el indice en el espacio de indices.
        }
        return -1; // El espacio de indices esta lleno por lo que no se pudo crear el nuevo indice.
    }

    public int asignar_Memoria_A_Programa(Codigo_ASM pCodigo_ASM, String pNombre_Archivo) { // ej. Posicion: 100 -> MOV
                                                                                            // AX, 5

        if (this.espacio_Programas > (this.espacio_Usado_Programas + pCodigo_ASM.getContador_Intrucciones())) {
            // Sacar la lista completa de instrucciones.
            List<Instruccion> instrucciones = pCodigo_ASM.getInstrucciones();

            int pos_Inicial = this.posicion_Programas;
            for (Instruccion instruccion_actual : instrucciones) {
                Memoria_Secundaria.put(this.posicion_Programas, instruccion_actual.get_Intruccion_Completa());
                this.posicion_Programas++;
                this.espacio_Usado_Programas++;

            }

            // Registrar el nuevo indice.
            agregar_Indice(pNombre_Archivo, pos_Inicial, this.posicion_Programas - 1);
            return 0; // Si se puede almacenar el programa en el almacenamiento.
        }

        return -1; // el tamaño del programa exede el espacio disponible en el Almacenamiento.

    }

    /**
     * Esta funcion se encarga de modificar el valor de una posicion especifica de
     * la memoria y validar si la posicion ingresada existe.
     * 
     */
    public int modificar_valor_en_memoria(int pPosicion, String pValor) {

        if (Memoria_Secundaria.containsKey(pPosicion)) {
            Memoria_Secundaria.replace(pPosicion, pValor);
            return 0;
        } else {
            return 1;
        }

    }

    /**
     * Funcion: optener_Indices
     * Descripcion: Se encarga de optener todos los indices de los programas
     * guardado en el almacenamiento.
     * 
     * @return List<String> -> Lista de indices de los programas guardados en el
     *         almacenamiento.
     */
    public Map<String, List<Integer>> optener_Indices() {

        System.out.println("Obteniendo los indices de archivos.");

        Map<String, List<Integer>> indices = new HashMap<>();
        for (int i = 0; i < espacio_Indices; i++) {
            String dato = Memoria_Secundaria.get(i);
            if (dato != null) {

                if (dato != "") {
                    // Posicion: 0 -> Programa_1.asm | 100 - 115
                    System.out.println("-> Dato: " + dato);

                    // Separar en seccniones.

                    String[] partes = dato.split(":");
                    if (partes.length < 2) {
                        System.out.println("Formato inválido en índice: " + dato);
                        continue; // saltar este índice
                    }

                    String nombre_archivo = partes[0].trim();
                    String[] pos_Almacenamiento = partes[1].split("-");
                    if (pos_Almacenamiento.length < 2) {
                        System.out.println("Formato inválido en posiciones: " + partes[1]);
                        continue;
                    }

                    int posicion_inicial = Integer.parseInt(pos_Almacenamiento[0].trim());
                    int posicion_final = Integer.parseInt(pos_Almacenamiento[1].trim());

                    List<Integer> posiciones = new ArrayList<>();
                    posiciones.add(posicion_inicial);
                    posiciones.add(posicion_final);

                    indices.put(nombre_archivo, posiciones);

                }

            }
        }
        return indices;
    }

    public int actualizarIndice(String nombreArchivo, int nuevoInicio, int nuevoFin) {
        for (int i = 0; i < espacio_Indices; i++) {
            String dato = Memoria_Secundaria.get(i);
            if (dato != null && !dato.trim().isEmpty()) {
                String[] partes = dato.split(":");
                String nombre = partes[0].trim();
                if (nombre.equals(nombreArchivo)) {
                    Memoria_Secundaria.put(i, nombreArchivo + " : " + nuevoInicio + " - " + nuevoFin);
                    return 0;
                }
            }
        }
        return -1; // no encontrado
    }

    public int eliminarIndice(String nombreArchivo) {
        for (int i = 0; i < espacio_Indices; i++) {
            String dato = Memoria_Secundaria.get(i);
            if (dato != null && dato.trim().startsWith(nombreArchivo + " :")) {
                Memoria_Secundaria.put(i, "");
                this.espacio_Usado_Indices--;
                return 0;
            }
        }
        return -1;
    }

    /**
     * Funcion: optener_Programa
     * Descripcion: Se encarga de optener todos los programas guardados en el
     * almacenamiento. Esto mediante
     * la posicion inicial y final del programa en el almacenamiento.
     * 
     * @param pPosicion_Inicial -> Posicion inicial del programa en el
     *                          almacenamiento.
     * @param pPosicion_Final   -> Posicion final del programa en el almacenamiento.
     * @return Codigo_ASM -> Codigo ASM de los programas guardados en el
     *         almacenamiento.
     */
    public Codigo_ASM optener_Programa(int pPosicion_Inicial, int pPosicion_Final) {

        Codigo_ASM codigo_ASM = new Codigo_ASM();

        for (int i = pPosicion_Inicial; i <= pPosicion_Final; i++) {

            Instruccion intruc = new Instruccion(Memoria_Secundaria.get(i));

            codigo_ASM.agregar_Intruccion(intruc);
        }

        return codigo_ASM;
    }

    public String optener_Instruccion(int pPosicion) {
        return Memoria_Secundaria.get(pPosicion);
    }

    // ########## Seccion para la validacion de espacios disponibles. ##########

    /**
     * Funcion: existe_Archivo
     * Descripcion: Se encarga de verificar si un archivo existe en el
     * almacenamiento, validando si el nombre existe en el espacio de indices.
     * 
     * @param pNombre_Archivo -> Nombre del archivo a verificar.
     * @return boolean -> True si el archivo existe, false en caso contrario.
     */
    public boolean existe_Archivo(String pNombre_Archivo) {
        return optener_Indices().containsKey(pNombre_Archivo);
    }

    public int espacio_Disponible_Indices() {
        return this.espacio_Indices - this.espacio_Usado_Indices;
    }

    public int espacio_Disponible_Programas() {
        return this.espacio_Programas - this.espacio_Usado_Programas;
    }

    public int espacio_Disponible_Memoria_Virtual() {
        return this.espacio_Memoria_Virtual - this.espacio_Usado_Memoria_Virtual;
    }

    /**
     * Funcion: mostrar_Memoria
     * Descripcion: Se encarga de mostrar todos los programas guardados en el
     * almacenamiento.
     */
    public void mostrar_Memoria() {
        System.out.println("Mostrando memoria.\n");

        for (int i = 0; i < tamano_Total; i++) {

            String datos = Memoria_Secundaria.get(i) != null ? Memoria_Secundaria.get(i) : " ";

            System.out.println("Clave: " + i + ", Valor: " + datos);
        }

        System.out.println("Memoria mostrada.\n");
    }
}
