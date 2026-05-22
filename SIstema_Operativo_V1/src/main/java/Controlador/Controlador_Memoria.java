/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Modelo.Almacenamiento;
import Modelo.BCP;
import Modelo.Codigo_ASM;
import Modelo.Instruccion;
import Modelo.Memoria;

/**
 *
 * @author edurg
 */
public class Controlador_Memoria {

    private static final int POSICION_INICIO_BCP = 0;
    private static final int TAMANO_BCP = 26;

    private Memoria Memoria_RAM;

    private Almacenamiento Disco;

    public Controlador_Memoria(Memoria pMemoria, Almacenamiento pDisco) {
        this.Memoria_RAM = pMemoria;
        this.Disco = pDisco;
    }

    public void set_Memoria(Memoria pMemoria) {
        this.Memoria_RAM = pMemoria;
    }

    public Memoria get_Memoria() {
        return this.Memoria_RAM;
    }

    public int get_Nuevo_PID() {
        return Memoria_RAM.asignar_Nuevo_PID_Proceso();
    }

    public int get_Pos_Actual_MV() {
        return Disco.getPosicion_Memoria_Virtual();
    }

    // #### Seccion para el manejo del registro de programas en la memoria y el
    // disco.

    // -> Cargar los programas tanto en memoria como en memoria virtual.

    /**
     * Asigna memoria a un programa. Si no hay espacio en RAM, se pasa a memoria
     * virtual.
     * 
     * @param codigoASM Programa con instrucciones.
     * @return 0 = éxito en RAM, 1 = éxito en Virtual, -1 = error.
     */
    public int asignar_Memoria_Programa(Codigo_ASM codigoASM) {
        int tamano = codigoASM.getContador_Intrucciones();

        // Validar espacio en RAM
        if (this.Memoria_RAM.getEspacio_Usado_Usuario() + tamano <= this.Memoria_RAM.getEspacio_Usuario()) {
            // Cargar en RAM
            int pos = this.Memoria_RAM.getPosicion_Actual_Usuario();
            for (Instruccion instruccion_Actual : codigoASM.getInstrucciones()) {
                this.Memoria_RAM.getMemoria_Principal().put(pos, instruccion_Actual.get_Intruccion_Completa());
                pos++;
            }
            this.Memoria_RAM.setPosicion_Actual_Usuario(pos);
            this.Memoria_RAM.setEspacio_Usado_Usuario(this.Memoria_RAM.getEspacio_Usado_Usuario() + tamano);
            return 0;

        } else {
            // Cargar en Virtual
            int pos = this.Disco.getPosicion_Memoria_Virtual();
            for (Instruccion instruccion_Actual : codigoASM.getInstrucciones()) {
                this.Disco.getMemoria_Secundaria().put(pos, instruccion_Actual.get_Intruccion_Completa());
                pos++;
            }
            this.Disco.setPosicion_Memoria_Virtual(pos);
            this.Disco.setEspacio_Usado_Memoria_Virtual(
                    this.Disco.getEspacio_Usado_Memoria_Virtual() + tamano);
            return 1;
        }
    }

    // -> Limpiar datos de BCPs y programas tanto de memoria como de memoria
    // virtual..
    // ##### Seccion para la eliminacion de la memoria asignada a un proceso ###
    public void limpiar_Memoria_Proceso(int pPID, int pPosicion_Final) {

        // 1. Liberamos la memoria.
        List<Integer> posciones = this.liberar_Memoria_Proceso(pPID);
        int pos_Inicial_Programa = posciones.get(0);
        int pos_Final_Programa = posciones.get(1);

        // 2. Iniciamos compactacion de la memoria hacia la izquierda.
        System.out.println("Controlador Memoria: Iniciando compactacion de la memoria hacia la izquierda.");

        // compactar_SO();

        System.out.println("Controlador Memoria: Inciando compactacion del usuario...");

        int espacio_Total = this.Memoria_RAM.getEspacio_Total(); // + this.Disco.getEspacio_Memoria_Virtual();

        // compactar_Usuario_Desde(pos_Final_Programa, espacio_Total);
    }

    /**
     * Libera memoria de un proceso, ya sea en RAM o Virtual.
     */
    public List<Integer> liberar_Memoria_Proceso(int pid) {

        // Obtener la posicion de la BCP.
        int pos_BCP = this.Memoria_RAM.buscar_Posicion_BCP(pid);

        int pos_Inicial_Programa = Integer.parseInt(this.Memoria_RAM.obtener_Instruccion(pos_BCP + 3));
        int pos_Final_Programa = Integer.parseInt(this.Memoria_RAM.obtener_Instruccion(pos_BCP + 4));

        List<Integer> posciones = new ArrayList<>();
        posciones.add(pos_Inicial_Programa);
        posciones.add(pos_Final_Programa);

        if (!(pos_Inicial_Programa >= this.Memoria_RAM.getEspacio_Total())) {

            System.out.println("Controlador Memoria: El programa se encuentra en RAM");

            // Liberar en RAM
            // int posInicial = this.Memoria_RAM.buscar_Posicion_BCP(pid); // ejemplo
            // int posFinal = this.Memoria_RAM.buscar_Posicion_BCP(pid);

            for (int i = pos_Inicial_Programa; i <= pos_Final_Programa; i++) {
                this.Memoria_RAM.getMemoria_Principal().put(i, "");
            }

            this.liberar_Memoria_BCP(pos_BCP);

            return posciones;
        } else {

            System.out.println("Controlador Memoria: El programa se encuentra en Memoria Virtual");

            // Liberar en Virtual
            // int posInicial = this.Memoria_RAM.buscar_Posicion_BCP(pid);
            // int posFinal = this.Memoria_RAM.buscar_Posicion_BCP(pid);

            // procesar los tamaños para que pueda entrar a la memoria virtual.
            // Si estan en memoria virtual, su valor en BCP, es mas grande que
            // el espacio total de la memoria principal.

            // Asi que optenemos la diferencia entre el tamaño total de la ram y la pos que
            // indica la BCP.

            // Despues a la posicion en donde inicia la memoria virtual.
            // le sumamos la diferencia optenido y ahi estarian las instrucciones de ese
            // programa.
            int tamano_Total_Ram = this.Memoria_RAM.getEspacio_Total();

            int pos_Ini_MV = this.Disco.getEspacio_Indices();

            int pos_Inicial_Real = pos_Inicial_Programa - tamano_Total_Ram;

            int pos_Final_Real = pos_Final_Programa - tamano_Total_Ram; // pos_Ini_MV + (

            for (int i = pos_Inicial_Real; i <= pos_Final_Real; i++) {
                this.Disco.getMemoria_Secundaria().put(i, "");
            }

            this.liberar_Memoria_BCP(pos_BCP);

            return posciones;
        }
    }

    public void liberar_Memoria_BCP(int posicion_BCP) {
        // 4. Limpiar la memoria del bloque BCP (espacio del SO).
        System.out.println("Controlador Memoria: Limpiando memoria del BCP en la posicion: " + posicion_BCP);
        System.out.println("Controlador Memoria: Tamano del BCP: " + (posicion_BCP + TAMANO_BCP));

        for (int i = posicion_BCP; i < posicion_BCP + TAMANO_BCP; i++) {
            Memoria_RAM.getMemoria_Principal().put(i, "");
        }
    }

    // -> Validaciones.

    public int validar_Espacio_Disponible_Usuario(int tamano) {
        if (this.Memoria_RAM.getEspacio_Usado_Usuario() + tamano <= this.Memoria_RAM.getEspacio_Usuario()) {
            return 1; // hay espacio en RAM
        } else if (this.Disco.getEspacio_Usado_Memoria_Virtual() + tamano <= this.Disco.getEspacio_Memoria_Virtual()) {
            return 2; // hay espacio en Virtual
        }
        return 0;
    }

    // ######## Seccion para la compresion hacia la izquierda en espacio libres de
    // memoria.
    // Helper: copia un bloque de memoria a una lista temporal (evita solapamientos)
    private List<String> copiarBloque(int inicio, int longitud) {
        List<String> temp = new ArrayList<>(longitud);
        for (int i = 0; i < longitud; i++) {
            String v = Memoria_RAM.getMemoria_Principal().get(inicio + i);
            temp.add(v == null ? "" : v);
        }
        return temp;
    }

    // Helper: escribe una lista en memoria a partir de inicioDestino
    private void escribirBloque(int inicioDestino, List<String> datos) {
        for (int i = 0; i < datos.size(); i++) {
            Memoria_RAM.getMemoria_Principal().put(inicioDestino + i, datos.get(i));
        }
    }

    // Helper: limpiar rango (poner "")
    private void limpiarRango(int inicio, int fin) {
        for (int i = inicio; i <= fin; i++) {
            Memoria_RAM.getMemoria_Principal().put(i, "");
        }
    }

    /**
     * Compacta la sección OS: mueve todos los BCPs hacia la izquierda para eliminar
     * huecos.
     * Mantiene el orden relativo de los BCPs.
     */
    public void compactar_SO() {
        int writePos = POSICION_INICIO_BCP; // donde debe quedar el siguiente BCP válido

        // Recorremos todos los posibles bloques BCP dentro del espacio OS
        for (int readPos = POSICION_INICIO_BCP; readPos < Memoria_RAM.getEspacio_OS(); readPos += TAMANO_BCP) {
            System.out.println("Controlador Memoria: Posicion de lectura: " + readPos);
            System.out.println("Controlador Memoria: Posicion de escritura: " + writePos);
            // System.out.println("Controlador Memoria: Buscando en la posicion: " +
            // readPos);

            // String posiblePID = Memoria_RAM.getMemoria_Principal().get(readPos);
            String posiblePID = this.obtener_intruccion_Proceso(readPos);

            boolean bloqueVacio = (posiblePID == null || posiblePID.trim().isEmpty());
            if (!bloqueVacio) {

                // Si readPos != writePos, mover el bloque completo
                if (readPos != writePos) {
                    List<String> bloque = copiarBloque(readPos, TAMANO_BCP);
                    escribirBloque(writePos, bloque);
                    // limpiar las antiguas posiciones
                    limpiarRango(readPos, readPos + TAMANO_BCP);
                }
                writePos += TAMANO_BCP;
            }
            // si bloque vacío, simplemente avanzamos readPos; writePos se queda para
            // recibir el siguiente bloque
        }

        // Limpiar el resto del espacio OS desde writePos hasta espacio_OS-1
        if (writePos < Memoria_RAM.getEspacio_OS()) {
            limpiarRango(writePos, Memoria_RAM.getEspacio_OS() - 1);
        }

        // Para OS
        int bcpCount = contarBCPValidos(); // recorrer OS y contar bloques con PID válido
        Memoria_RAM.setEspacio_Usado_OS(bcpCount * TAMANO_BCP);
        Memoria_RAM.setPosicion_Actual_OS(POSICION_INICIO_BCP + Memoria_RAM.getEspacio_Usado_OS());
    }

    /**
     * Compacta la sección de usuario a partir de una posición liberada.
     * - posicionLiberada: última posición que quedó vacía (por ejemplo, al borrar
     * un programa).
     * - finUsuario: índice de la última posición válida del área de usuario.
     *
     * La función mueve hacia la izquierda los programas contiguos que estén a la
     * derecha del hueco,
     * actualiza en la BCP (en la sección OS) los campos Mem_Init, Mem_End y PC.
     */
    public void compactar_Usuario_Desde(int posicionLiberada, int finUsuario) {
        int pos = posicionLiberada + 1;

        while (pos <= finUsuario) {
            String valor = Memoria_RAM.getMemoria_Principal().get(pos);
            if (valor == null || valor.trim().isEmpty()) {
                break; // hueco vacío
            }

            BCP bcp = Memoria_RAM.obtener_Datos_BCP_Pos_Inicio(pos);
            if (bcp == null) {
                pos++;
                continue;
            }

            int memInit = Integer.parseInt(bcp.getMem_Init());
            int memEnd = Integer.parseInt(bcp.getMem_End());
            int tam = memEnd - memInit + 1;

            if (memInit < Memoria_RAM.getEspacio_Total()) {
                // Programa en RAM → mover hacia la izquierda
                int newStart = posicionLiberada + 1;
                if (newStart != memInit) {
                    List<String> bloque = copiarBloque(memInit, tam);
                    escribirBloque(newStart, bloque);
                    limpiarRango(memInit, memEnd);
                }

                // Actualizar BCP
                int posBCP = Memoria_RAM.buscar_Posicion_BCP(bcp.getPID());
                if (posBCP != -1) {
                    Memoria_RAM.getMemoria_Principal().put(posBCP + 3, String.valueOf(newStart));
                    Memoria_RAM.getMemoria_Principal().put(posBCP + 4, String.valueOf(newStart + tam - 1));
                    Memoria_RAM.getMemoria_Principal().put(posBCP + 5, String.valueOf(newStart)); // PC
                }

                posicionLiberada = newStart + tam - 1;
                pos = posicionLiberada + 1;

            } else {
                // Programa en Virtual → intentar mover a RAM si hay espacio
                int tamanoTotalRam = Memoria_RAM.getEspacio_Total();
                int posIniMV = Disco.getEspacio_Indices();
                int posRealIni = posIniMV + (memInit - tamanoTotalRam);
                int posRealFin = posIniMV + (memEnd - tamanoTotalRam);

                // Validar espacio en RAM
                if (Memoria_RAM.getEspacio_Usado_Usuario() + tam <= Memoria_RAM.getEspacio_Usuario()) {
                    int newStart = posicionLiberada + 1;

                    // Copiar desde disco a RAM
                    List<String> bloque = new ArrayList<>();
                    for (int i = posRealIni; i <= posRealFin; i++) {
                        bloque.add(Disco.getMemoria_Secundaria().get(i));
                    }
                    escribirBloque(newStart, bloque);

                    // Limpiar en disco
                    limpiarRango(posRealIni, posRealFin);

                    // Actualizar BCP
                    int posBCP = Memoria_RAM.buscar_Posicion_BCP(bcp.getPID());
                    if (posBCP != -1) {
                        Memoria_RAM.getMemoria_Principal().put(posBCP + 3, String.valueOf(newStart));
                        Memoria_RAM.getMemoria_Principal().put(posBCP + 4, String.valueOf(newStart + tam - 1));
                        Memoria_RAM.getMemoria_Principal().put(posBCP + 5, String.valueOf(newStart));
                    }

                    posicionLiberada = newStart + tam - 1;
                    pos = posicionLiberada + 1;
                } else {
                    // No hay espacio en RAM → dejar en Virtual
                    pos = memEnd + 1;
                }
            }
        }

        // Actualizar punteros de RAM
        int totalInstr = contarInstruccionesValidas();
        Memoria_RAM.setEspacio_Usado_Usuario(totalInstr);
        Memoria_RAM.setPosicion_Actual_Usuario(Memoria_RAM.getEspacio_OS() + totalInstr);

        // Actualizar punteros de Virtual
        int totalVirtual = contarInstruccionesVirtuales();
        Disco.setEspacio_Usado_Memoria_Virtual(totalVirtual);
        Disco.setPosicion_Memoria_Virtual(Disco.getEspacio_Indices() + totalVirtual);
    }

    /**
     * Cuenta cuántos BCP válidos existen en la memoria RAM.
     * Un BCP válido se detecta si al menos una de sus posiciones contiene datos
     * distintos de "".
     * Normalmente se valida con el PID en la primera celda del bloque.
     *
     * @return Número de BCP válidos.
     */
    public int contarBCPValidos() {
        int contador = 0;

        // Recorremos todos los posibles bloques BCP dentro del espacio OS
        for (int pos = POSICION_INICIO_BCP; pos < Memoria_RAM.getEspacio_OS(); pos += TAMANO_BCP) {
            boolean bloqueVacio = true;

            // Validar todo el bloque para mayor seguridad
            for (int j = 0; j < TAMANO_BCP; j++) {
                String val = Memoria_RAM.getMemoria_Principal().get(pos + j);
                if (val != null && !val.trim().isEmpty()) {
                    bloqueVacio = false;
                    break;
                }
            }

            if (!bloqueVacio) {
                contador++;
            }
        }

        return contador;
    }

    /**
     * Cuenta cuántas instrucciones válidas existen en la memoria de usuario (RAM).
     * Una instrucción válida es cualquier celda no nula y no vacía.
     *
     * @return Número de instrucciones válidas en RAM.
     */
    public int contarInstruccionesValidas() {
        int contador = 0;
        int inicioUsuario = Memoria_RAM.getEspacio_OS();
        int finUsuario = Memoria_RAM.getEspacio_Total();

        for (int i = inicioUsuario; i < finUsuario; i++) {
            String val = Memoria_RAM.getMemoria_Principal().get(i);
            if (val != null && !val.trim().isEmpty()) {
                contador++;
            }
        }
        return contador;
    }

    /**
     * Cuenta cuántas instrucciones válidas existen en la memoria virtual (Disco).
     * Una instrucción válida es cualquier celda no nula y no vacía.
     *
     * @return Número de instrucciones válidas en memoria virtual.
     */
    public int contarInstruccionesVirtuales() {
        int contador = 0;
        int inicioVirtual = Disco.getEspacio_Indices();
        int finVirtual = inicioVirtual + Disco.getEspacio_Memoria_Virtual();

        for (int i = inicioVirtual; i < finVirtual; i++) {
            String val = Disco.getMemoria_Secundaria().get(i);
            if (val != null && !val.trim().isEmpty()) {
                contador++;
            }
        }
        return contador;
    }

    // ####### Seccion para la finalizacion de procesos #########
    public int comprobar_Finalizacion_Proceso(int pPID) {
        // 1. Obtener la BCP del proceso
        BCP bcp = Memoria_RAM.obtener_Datos_BCP(pPID);
        if (bcp == null) {
            // Inconsistencia: no encontramos BCP para este PID.
            return -1; // error
        }
        // 2. Obtener el PC del proceso
        int pc = Integer.parseInt(bcp.getPC()) - 1;

        // 3. Obtener el tamano del proceso
        int tam = Integer.parseInt(bcp.getMem_End()); // - Integer.parseInt(bcp.getMem_Init()) + 1

        // 4. Comprobar si el PC es mayor al tamaño del proceso
        if (pc == tam) {
            // El proceso ha finalizado
            return 1; // proceso finalizado
        }
        // 5. El proceso no ha finalizado
        return 0; // proceso no finalizado
    }

    // ##### Seccion para las actualizaciones de los datos de la BCP #####
    public void actualizar_Proceso_Siguiente(int pPID_Actual, int pPID_Siguiente) {

        // 1. Llamar a la funcionalidad encargada de la modificacion.
        Memoria_RAM.modificar_Enlace_Siguiente_BCP(pPID_Actual, pPID_Siguiente);

    }

    public void actualizar_Estado_BCP(int pPID, String pEstado) {
        // 1. Llamar a la funcionalidad encargada de la modificacion.
        Memoria_RAM.actualizar_Estado_BCP(pPID, pEstado);
    }

    public String obtener_intruccion_Proceso(int pPosicion) {
        // 1. Comprobar si esta en memoria RAM o en memoria Virtual.
        // System.out.println("Controlador Memoria: Posicion: " + pPosicion);
        // System.out.println("Controlador Memoria: Espacio Total RAM: " +
        // this.Memoria_RAM.getEspacio_Total());
        if (pPosicion < Memoria_RAM.getEspacio_Total()) {
            // Esta en memoria RAM.
            return Memoria_RAM.getMemoria_Principal().get(pPosicion);
        }

        int tamano_Total_Ram = this.Memoria_RAM.getEspacio_Total();

        // int pos_Ini_MV = this.Disco.getEspacio_Indices();

        int pos_Inicial_Real = pPosicion - tamano_Total_Ram;
        // System.out.println("Controlador Memoria: Posicion Real: " +
        // pos_Inicial_Real);

        return this.Disco.optener_Instruccion(pos_Inicial_Real);
    }

    // ####### Seccion para el manejo de archivos ########

    public int crear_Archivo(int pid, String nombreArchivo) {
        System.out.println("Controlador Memoria: Creando archivo: " + nombreArchivo);
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty())
            return -1;
        nombreArchivo = nombreArchivo.trim();

        System.out.println("Control memoria: Pass 1");

        // Evitar duplicados en índices
        Map<String, List<Integer>> indices = Disco.optener_Indices();
        if (indices.containsKey(nombreArchivo))
            return -1;

        System.out.println("Control memoria: Pass 2");

        // Reservar 1 celda en la zona de Programas
        if (Disco.espacio_Disponible_Programas() == 0)
            return -1;

        System.out.println("Control memoria: Pass 3");

        int inicio = Disco.getPosicion_Programas();
        Disco.getMemoria_Secundaria().put(inicio, ""); // celda reservada (vacía)
        Disco.setPosicion_Programas(inicio + 1);
        Disco.setEspacio_Usado_Programas(Disco.getEspacio_Usado_Programas() + 1);

        System.out.println("Control memoria: Pass 4");

        // Registrar índice en las primeras posiciones
        Disco.agregar_Indice(nombreArchivo, inicio, inicio); // inicio == fin por ahora

        System.out.println("Control memoria: Pass 5");

        // Actualizar BCP del proceso creador
        Memoria_RAM.modificar_Lista_Archivos_BCP(pid, nombreArchivo);

        System.out.println("Control memoria: Pass 6");

        return 0;
    }

    // Abrir archivo: validar existencia
    public int abrir_Archivo(int pid, String nombreArchivo) {
        if (nombreArchivo == null)
            return -1;
        Map<String, List<Integer>> indices = Disco.optener_Indices();
        if (!indices.containsKey(nombreArchivo))
            return -1;

        // Añadir a la lista de archivos del BCP si no existe ya
        List<String> lista = Memoria_RAM.obtener_Lista_Archivos_Proceso(pid);
        if (lista == null || !lista.contains(nombreArchivo)) {
            Memoria_RAM.modificar_Lista_Archivos_BCP(pid, nombreArchivo);
        }
        return 0;
    }

    // Leer archivo: devuelve todo el contenido como cadena (o null si no existe)
    public String leer_Archivo(int pid, String nombreArchivo) {
        Map<String, List<Integer>> indices = Disco.optener_Indices();
        if (!indices.containsKey(nombreArchivo))
            return null;
        List<Integer> pos = indices.get(nombreArchivo);
        int inicio = pos.get(0);
        String valor = Disco.optener_Instruccion(inicio);
        return valor == null ? "" : valor;
    }

    // Escribir: agrega el contenido de 'dato' (AL) al final del archivo (append)
    public int escribir_Archivo(int pid, String nombreArchivo, String dato) {
        if (dato == null)
            dato = "";
        Map<String, List<Integer>> indices = Disco.optener_Indices();
        if (!indices.containsKey(nombreArchivo))
            return -1;

        List<Integer> pos = indices.get(nombreArchivo);
        int inicio = pos.get(0);

        String actual = Disco.optener_Instruccion(inicio);
        if (actual == null)
            actual = "";

        String nuevo = actual + dato;
        Disco.modificar_valor_en_memoria(inicio, nuevo);

        // No cambia índice porque sigue en la misma celda (inicio == fin)
        return 0;
    }

    // Eliminar archivo: borra índice y limpia contenido
    public int eliminar_Archivo(int pid, String nombreArchivo) {
        Map<String, List<Integer>> indices = Disco.optener_Indices();
        if (!indices.containsKey(nombreArchivo))
            return -1;

        List<Integer> pos = indices.get(nombreArchivo);
        int inicio = pos.get(0);

        Disco.getMemoria_Secundaria().put(inicio, "");
        Disco.eliminarIndice(nombreArchivo);

        quitarArchivoDeBCP(pid, nombreArchivo);
        return 0;
    }

    public int quitarArchivoDeBCP(int pid, String nombreArchivo) {
        int posBCP = Memoria_RAM.buscar_Posicion_BCP(pid);
        if (posBCP == -1)
            return -1;

        String lista = Memoria_RAM.obtener_Instruccion(posBCP + 12);
        if (lista == null || lista.equals("NONE"))
            return -1;

        String[] partes = lista.split(",");
        StringBuilder sb = new StringBuilder();
        boolean encontrado = false;
        for (String p : partes) {
            String t = p.trim();
            if (t.equals(nombreArchivo)) {
                encontrado = true;
                continue;
            }
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(t);
        }
        String nueva = sb.length() == 0 ? "NONE" : sb.toString();
        Memoria_RAM.getMemoria_Principal().put(posBCP + 12, nueva);
        return encontrado ? 0 : -1;
    }

}
