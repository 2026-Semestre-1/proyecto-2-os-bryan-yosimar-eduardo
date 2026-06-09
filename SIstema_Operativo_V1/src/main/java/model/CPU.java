package model;

import java.util.List;

import kernel.GestorMemoria;
import util.Separar_Instrucciones;

public class CPU {

    private int tiempo_CPU = 0;

    private static int rafaga_actual = 0;

    private int numero_CPU;
    private int PC;
    private String IR;
    private int AC;
    private int AX;
    private int BX;
    private int CX;
    private String DX;
    private String AH;
    private String AL;
    private Memoria memoria_Principal;
    private GestorMemoria controlador_Memoria;
    private int PID_Proceso_Actual = 0;
    private int flag_CMP;
    private int IO_STATUS;
    private String ruta_Archivo;
    private boolean proceso_Finalizado = false;
    private boolean error_Encontrado = false;
    private String descripcion_Error = "";
    private boolean imprimir_Pantalla = false;
    private boolean leer_Teclado = false;

    public CPU(int pNumero_CPU, Memoria pMemoria_Principal, GestorMemoria pControlador_Memoria) {
        this.tiempo_CPU = 0;
        this.numero_CPU = pNumero_CPU;
        this.memoria_Principal = pMemoria_Principal;
        this.controlador_Memoria = pControlador_Memoria;
        this.PC = 0;
        this.IR = "";
        this.AC = this.AX = this.BX = this.CX = 0;
        this.DX = "";
        this.flag_CMP = 0;
    }

    public int getNumero_CPU() {
        return numero_CPU;
    }

    public int getTiempo_CPU() {
        return tiempo_CPU;
    }

    public void incrementar_Tiempo_CPU() {
        this.tiempo_CPU++;
    }

    public boolean isProcesoFinalizado() {
        return proceso_Finalizado;
    }

    public static void incrementar_rafaga_actual() {
        rafaga_actual++;
    }

    public static void reiniciar_rafaga() {
        rafaga_actual = 0;
    }

    public static int get_rafaga_actual() {
        return rafaga_actual;
    }

    public void setPC(int nuevaPC) {
        this.PC = nuevaPC;
    }

    public int getPC() {
        return this.PC;
    }

    public void modificar_PC(int pIncremento) {
        this.PC = this.PC + pIncremento;
    }

    public String getIR() {
        return this.IR;
    }

    public void setIR(String nuevaIR) {
        this.IR = nuevaIR;
    }

    public int getAC() {
        return this.AC;
    }

    public void setAC(int nuevaAC) {
        this.AC = nuevaAC;
    }

    public int getAX() {
        return this.AX;
    }

    public void setAX(int nuevaAX) {
        this.AX = nuevaAX;
    }

    public int getBX() {
        return this.BX;
    }

    public void setBX(int nuevaBX) {
        this.BX = nuevaBX;
    }

    public int getCX() {
        return this.CX;
    }

    public void setCX(int nuevaCX) {
        this.CX = nuevaCX;
    }

    public String getDX() {
        return this.DX;
    }

    public void setDX(String nuevaDX) {
        this.DX = nuevaDX;
    }

    public String getAH() {
        return this.AH;
    }

    public void setAH(String nuevaAH) {
        this.AH = nuevaAH;
    }

    public String getAL() {
        return this.AL;
    }

    public void setAL(String nuevaAL) {
        this.AL = nuevaAL;
    }

    public int getIO_STATUS() {
        return this.IO_STATUS;
    }

    public void setIO_STATUS(int nuevaIO_STATUS) {
        this.IO_STATUS = nuevaIO_STATUS;
    }

    public int getFlag_CMP() {
        return this.flag_CMP;
    }

    public void setFlag_CMP(int nuevaFlag_CMP) {
        this.flag_CMP = nuevaFlag_CMP;
    }

    public int getPID_Proceso_Actual() {
        return PID_Proceso_Actual;
    }

    public void setPID_Proceso_Actual(int PID_Proceso_Actual) {
        this.PID_Proceso_Actual = PID_Proceso_Actual;
    }

    public boolean isError_Encontrado() {
        return error_Encontrado;
    }

    public String getDescripcion_Error() {
        return descripcion_Error;
    }

    public void setDescripcion_Error(String descripcion_Error) {
        this.descripcion_Error = descripcion_Error;
    }

    public void setError_Encontrado(boolean error_Encontrado) {
        this.error_Encontrado = error_Encontrado;
    }

    public boolean isImprimir_Pantalla() {
        return imprimir_Pantalla;
    }

    public void setImprimir_Pantalla(boolean imprimir_Pantalla) {
        this.imprimir_Pantalla = imprimir_Pantalla;
    }

    public boolean isLeer_Teclado() {
        return leer_Teclado;
    }

    public void setLeer_Teclado(boolean leer_Teclado) {
        this.leer_Teclado = leer_Teclado;
    }

    public boolean isProceso_Finalizado() {
        return proceso_Finalizado;
    }

    public void setProceso_Finalizado(boolean proceso_Finalizado) {
        this.proceso_Finalizado = proceso_Finalizado;
    }

    public boolean ejecutar_Siguiente_Instruccion() {
        System.out.println("[DEBUG CPU] Leyendo línea en PC = " + PC);
        String instruccion = leer_Memoria(PC);
        System.out.println("[DEBUG CPU] Contenido crudo obtenido: '" + instruccion + "'");
        if (instruccion == null || instruccion.trim().isEmpty()) {
            return false;
        }
        IR = instruccion.trim();
        List<String> instr = Separar_Instrucciones.separar_Instrucciones(IR);
        String opcode = instr.get(0);
        String operando1 = null;
        String operando2 = null;
        String operando3 = null;
        if (instr.size() == 2) {
            operando1 = instr.get(1);
        }
        if (instr.size() == 3) {
            operando1 = instr.get(1);
            operando2 = instr.get(2);
        }
        if (instr.size() == 4) {
            operando1 = instr.get(1);
            operando2 = instr.get(2);
            operando3 = instr.get(3);
        }
        System.out.println("[DEBUG EXECUTE] Evaluando Opcode: '" + opcode + "' con args: '" + operando1 + "', '"
                + operando2 + "', '" + operando3 + "'");
        switch (opcode.toUpperCase()) {
            case "LOAD":
                ejecutar_LOAD(operando1);
                this.PC++;
                break;
            case "STORE":
                ejecutar_STORE(operando1);
                this.PC++;
                break;
            case "MOV":
                ejecutar_MOV(operando1, operando2);
                this.PC++;
                break;
            case "ADD":
                ejecutar_ADD(operando1);
                this.PC++;
                break;
            case "SUB":
                ejecutar_SUB(operando1);
                this.PC++;
                break;
            case "INC":
                ejecutar_INC(operando1);
                this.PC++;
                break;
            case "DEC":
                ejecutar_DEC(operando1);
                this.PC++;
                break;
            case "SWAP":
                ejecutar_SWAP(operando1, operando2);
                this.PC++;
                break;
            case "INT":
                ejecutar_INT(operando1);
                this.PC++;
                break;
            case "JMP":
                ejecutar_JMP(operando1);
                break;
            case "CMP":
                ejecutar_CMP(operando1, operando2);
                this.PC++;
                break;
            case "JE":
                ejecutar_JE(operando1);
                break;
            case "JNE":
                ejecutar_JNE(operando1);
                break;
            case "PARAM":
                ejecutar_PARAM(operando1, operando2, operando3);
                this.PC++;
                break;
            case "PUSH":
                ejecutar_PUSH(operando1);
                this.PC++;
                break;
            case "POP":
                ejecutar_POP(operando1);
                this.PC++;
                break;
            default:
                System.out.println("Instruccion desconocida: " + IR);
                break;
        }
        mostrar_Datos_Registros();
        return true;
    }

    private void ejecutar_LOAD(String operandos) {
        set_Espera(1);
        System.out.println("CPU -> Ejecutando LOAD con operando: " + operandos);
        if (isRegister(operandos)) {
            System.out.println("CPU -> El operando es un registro");
            Integer val = get_Valor_Registros(operandos);
            if (val == null) {
                this.error_Encontrado = true;
                this.descripcion_Error = "Error: No se pudo obtener el valor del registro " + operandos;
                return;
            }
            AC = val;
        }
    }

    private void ejecutar_STORE(String pOperando1) {
        set_Espera(1);
        System.out.println("CPU -> Ejecutando STORE con operando: " + pOperando1);
        if (isRegister(pOperando1)) {
            System.out.println("CPU -> El operando es un registro");
            set_Valor_Registros(pOperando1, AC);
        }
    }

    private void ejecutar_MOV(String pOperando1, String pOperando2) {
        set_Espera(1);
        System.out.println("CPU -> Ejecutando MOV con operando: " + pOperando1 + ", " + pOperando2);
        if (isRegister(pOperando2)) {
            System.out.println("CPU -> El operando 2 es un registro");
            System.out.println("CPU -> El operando 1 es otro registro");
            if (pOperando1.toUpperCase().contains("AL")) {
                System.out.println("CPU -> El operando 1 es AL");
                this.AL = get_Valor_Registros_String(pOperando2);
                return;
            } else if (pOperando1.toUpperCase().contains("AH")) {
                System.out.println("CPU -> El operando 1 es AH");
                this.AH = get_Valor_Registros_String(pOperando2);
                return;
            } else if (pOperando1.toUpperCase().contains("DX")) {
                System.out.println("CPU -> El operando 1 es DX");
                this.DX = get_Valor_Registros_String(pOperando2);
                return;
            } else {
                int val = get_Valor_Registros(pOperando2);
                set_Valor_Registros(pOperando1, val);
            }
        } else {
            System.out.println("CPU -> El operando 2 es un numero");
            if (pOperando1.toUpperCase().contains("AH")) {
                System.out.println("CPU -> El operando 1 es AH");
                this.AH = pOperando2;
                return;
            } else if (pOperando1.toUpperCase().contains("AL")) {
                System.out.println("CPU -> El operando 1 es AL");
                this.AL = pOperando2;
                return;
            } else if (pOperando1.toUpperCase().contains("DX")) {
                System.out.println("CPU -> El operando 1 es DX");
                this.DX = pOperando2;
                return;
            } else {
                try {
                    int val2 = Integer.parseInt(pOperando2);
                    System.out.println("CPU -> El valor 2 es: " + val2);
                    set_Valor_Registros(pOperando1, val2);
                } catch (NumberFormatException e) {
                    this.error_Encontrado = true;
                    this.descripcion_Error = "Error: Movimiento (MOV) invalido entre " + pOperando1 + " y "
                            + pOperando2;
                }
            }
        }
    }

    private void ejecutar_ADD(String operandos) {
        set_Espera(1);
        if (operandos.toUpperCase().equals("AC")) {
            this.error_Encontrado = true;
            this.descripcion_Error = "Error: El operando no puede ser el acumulador.";
            return;
        } else {
            Integer val = get_Valor_Registros(operandos);
            if (val == null) {
                this.error_Encontrado = true;
                this.descripcion_Error = "Error: No se pudo obtener el valor del registro " + operandos;
                return;
            }
            AC += val;
        }
    }

    private void ejecutar_SUB(String operandos) {
        set_Espera(1);
        if (operandos.toUpperCase().equals("AC")) {
            this.error_Encontrado = true;
            this.descripcion_Error = "Error: El operando no puede ser el acumulador.";
            return;
        } else {
            Integer val = get_Valor_Registros(operandos);
            if (val == null) {
                this.error_Encontrado = true;
                this.descripcion_Error = "Error: No se pudo obtener el valor del registro " + operandos;
                return;
            }
            AC -= val;
        }
    }

    private void ejecutar_INC(String operandos) {
        set_Espera(1);
        if (operandos == null || operandos.isEmpty()) {
            AC++;
        } else if (isRegister(operandos)) {
            int val = get_Valor_Registros(operandos);
            set_Valor_Registros(operandos, val + 1);
        } else {
            this.error_Encontrado = true;
            this.descripcion_Error = "Error: No se pudo obtener el valor del registro " + operandos;
        }
    }

    private void ejecutar_DEC(String operandos) {
        set_Espera(1);
        if (operandos == null || operandos.isEmpty()) {
            AC--;
        } else if (isRegister(operandos)) {
            int val = get_Valor_Registros(operandos);
            set_Valor_Registros(operandos, val - 1);
        } else {
            setError_Encontrado(true);
            setDescripcion_Error("Error: No se pudo obtener el valor del registro " + operandos);
        }
    }

    private void ejecutar_SWAP(String operando1, String operando2) {
        set_Espera(1);
        if (isRegister(operando1) && isRegister(operando2)) {
            int val1 = get_Valor_Registros(operando1);
            int val2 = get_Valor_Registros(operando2);
            set_Valor_Registros(operando1, val2);
            set_Valor_Registros(operando2, val1);
        } else {
            setError_Encontrado(true);
            setDescripcion_Error(
                    "Error: No se pudo obtener el valor de los registros " + operando1 + " y " + operando2);
        }
    }

    private void ejecutar_INT(String operandos) {
        String op = operandos.toUpperCase();
        switch (op) {
            case "20H":
                set_Espera(1);
                proceso_Finalizado = true;
                System.out.println("INT 20H: finalizar proceso.");
                break;
            case "10H":
                set_Espera(1);
                imprimir_Pantalla = true;
                System.out.println("INT 10H: SALIDA -> " + DX);
                break;
            case "09H":
                set_Espera(1);
                leer_Teclado = true;
                break;
            case "21H":
                set_Espera(1);
                ejecutar_INT_21H(AH, DX);
                break;
            default:
                System.out.println("INT desconocida: " + operandos);
                break;
        }
    }

    private void ejecutar_INT_21H(String pAH, String pDX) {
        switch (pAH.toLowerCase()) {
            case "3ch":
                this.controlador_Memoria.crear_Archivo(this.getPID_Proceso_Actual(), pDX);
                break;
            case "3dh":
                controlador_Memoria.abrir_Archivo(this.getPID_Proceso_Actual(), pDX);
                break;
            case "4dh":
                String lectura = controlador_Memoria.leer_Archivo(this.getPID_Proceso_Actual(), pDX);
                if (lectura != null) {
                    AL = lectura;
                }
                break;
            case "40h":
                this.controlador_Memoria.escribir_Archivo(this.getPID_Proceso_Actual(), pDX, AL);
                break;
            case "41h":
                this.controlador_Memoria.eliminar_Archivo(this.getPID_Proceso_Actual(), pDX);
                break;
            default:
                System.out.println("INT 21H: AH desconocido: " + pAH);
                break;
        }
    }

    private Integer ejecutar_JMP(String pOperando1) {
        set_Espera(1);
        String operando = pOperando1.trim();
        try {
            Integer offset = Integer.parseInt(operando);
            System.out.println("PC actual: " + PC);
            System.out.println("Salto de: " + offset);
            Integer resultado = this.memoria_Principal.validar_Salto_Programa(PC + offset, getPID_Proceso_Actual());
            if (resultado == 0) {
                PC += offset;
                return offset;
            } else if (resultado == 1) {
                setError_Encontrado(true);
                setDescripcion_Error(
                        "Error: El programa intenta saltar por debajo del limite inferior de la posicion del programa.");
                return null;
            } else if (resultado == 2) {
                setError_Encontrado(true);
                setDescripcion_Error(
                        "Error: El programa intenta realizar un salto por encima del limite superior de la posicion del programa.");
                return null;
            }
        } catch (NumberFormatException e) {
            setError_Encontrado(true);
            setDescripcion_Error("Error: No se pudo obtener el valor del registro " + pOperando1);
            return null;
        }
        return null;
    }

    private void ejecutar_CMP(String operando1, String operando2) {
        set_Espera(1);
        operando1.trim();
        operando2.trim();
        if (isRegister(operando1) && isRegister(operando2)) {
            Integer valor1 = get_Valor_Registros(operando1);
            Integer valor2 = get_Valor_Registros(operando2);
            if (valor1 == null || valor2 == null) {
                setError_Encontrado(true);
                setDescripcion_Error(
                        "Error: No se pudo obtener el valor de los registros " + operando1 + " y " + operando2);
                return;
            }
            if (valor1 == valor2)
                flag_CMP = 0;
            else if (valor1 > valor2)
                flag_CMP = 1;
            else
                flag_CMP = 2;
        } else {
            setError_Encontrado(true);
            setDescripcion_Error(
                    "Error: No se pudo obtener el valor de los registros " + operando1 + " y " + operando2);
        }
    }

    private void ejecutar_JE(String pOperando1) {
        set_Espera(1);
        if (flag_CMP == 0) {
            Integer resultado = ejecutar_JMP(pOperando1);
            if (resultado == null) {
                setError_Encontrado(true);
                setDescripcion_Error("Error: No se pudo realizar el salto JE");
            }
        } else {
            PC++;
        }
    }

    private void ejecutar_JNE(String pOperando1) {
        set_Espera(1);
        if (flag_CMP != 0) {
            Integer resultado = ejecutar_JMP(pOperando1);
            if (resultado == null) {
                setError_Encontrado(true);
                setDescripcion_Error("Error: No se pudo realizar el salto JNE");
            }
        } else {
            PC++;
        }
    }

    private void ejecutar_PARAM(String pOperando1, String pOperando2, String pOperando3) {
        set_Espera(1);
        if (pOperando1 != null) {
            pOperando1.trim();
            ejecutar_PUSH(pOperando1);
        }
        if (pOperando2 != null) {
            pOperando2.trim();
            ejecutar_PUSH(pOperando2);
        }
        if (pOperando3 != null) {
            pOperando3.trim();
            ejecutar_PUSH(pOperando3);
        }
    }

    private void ejecutar_PUSH(String pOperando1) {
        set_Espera(1);
        if (isRegister(pOperando1)) {
            Integer val = get_Valor_Registros(pOperando1);
            if (val != null) {
                push_A_Pila_Proceso(val);
            } else {
                setError_Encontrado(true);
                setDescripcion_Error("Error: No se pudo obtener el valor del registro " + pOperando1);
            }
        } else {
            Integer val = parser_String_To_Integer(pOperando1);
            if (val != null) {
                push_A_Pila_Proceso(val);
            }
        }
    }

    private void ejecutar_POP(String operandos) {
        set_Espera(1);
        Integer val = pop_De_Pila_Proceso();
        if (val == null) {
            System.out.println("POP: pila vacia");
            return;
        }
        if (isRegister(operandos)) {
            set_Valor_Registros(operandos, val);
        } else {
            setError_Encontrado(true);
            setDescripcion_Error("Error: No se pudo obtener el valor del registro " + operandos);
        }
    }

    private boolean isRegister(String token) {
        if (token == null)
            return false;
        String t = token.toUpperCase();
        return t.equals("AC") || t.equals("AX") || t.equals("BX") || t.equals("CX") || t.equals("DX") || t.equals("AH")
                || t.equals("AL");
    }

    private Integer get_Valor_Registros(String reg) {
        String r = reg.toUpperCase();
        switch (r) {
            case "AC":
                return AC;
            case "AX":
                return AX;
            case "BX":
                return BX;
            case "CX":
                return CX;
            case "DX":
                try {
                    return Integer.parseInt(DX);
                } catch (NumberFormatException e) {
                    return null;
                }
            case "AH":
                try {
                    return Integer.parseInt(AH);
                } catch (NumberFormatException e) {
                    return null;
                }
            case "AL":
                try {
                    return Integer.parseInt(AL);
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private String get_Valor_Registros_String(String reg) {
        String r = reg.toUpperCase();
        switch (r) {
            case "AC":
                return String.valueOf(AC);
            case "AX":
                return String.valueOf(AX);
            case "BX":
                return String.valueOf(BX);
            case "CX":
                return String.valueOf(CX);
            case "DX":
                return DX;
            case "AH":
                return AH;
            case "AL":
                return AL;
            default:
                return null;
        }
    }

    private void set_Valor_Registros(String reg, int valor) {
        String r = reg.toUpperCase();
        switch (r) {
            case "AC":
                this.AC = valor;
                break;
            case "AX":
                this.AX = valor;
                break;
            case "BX":
                this.BX = valor;
                break;
            case "CX":
                this.CX = valor;
                break;
            case "DX":
                this.DX = String.valueOf(valor);
                break;
            case "AH":
                this.AH = String.valueOf(valor);
                break;
            case "AL":
                this.AL = String.valueOf(valor);
                break;
        }
    }

    private Integer parser_String_To_Integer(String pValor_String) {
        try {
            return Integer.parseInt(pValor_String.trim());
        } catch (Exception e) {
            setError_Encontrado(true);
            setDescripcion_Error("Error: No se pudo pudo pasar el valor de " + pValor_String + " a integer.");
            return null;
        }
    }

    private String leer_Memoria(int posicion) {
        return this.controlador_Memoria.obtener_intruccion_Proceso(posicion);
    }

    private void escribir_Memoria(int posicion, String valor) {
        memoria_Principal.modificar_valor_en_memoria(posicion, valor);
    }

    private void push_A_Pila_Proceso(int valor) {
        Integer val = memoria_Principal.agregar_Dato_Pila(this.getPID_Proceso_Actual(), String.valueOf(valor));
        if (val == 1) {
            setError_Encontrado(true);
            setDescripcion_Error("Error: No se encontro la BCP del proceso actual. " + this.getPID_Proceso_Actual());
        } else if (val == 2) {
            setError_Encontrado(true);
            setDescripcion_Error("Error: No hay espacio en la pila, se salen de los limites superios.");
        }
    }

    private Integer pop_De_Pila_Proceso() {
        Integer val = memoria_Principal.obtener_Dato_Pila(this.getPID_Proceso_Actual());
        if (val == null) {
            setError_Encontrado(true);
            setDescripcion_Error("Error: No hay datos en la pila. Se salen de los limites inferiores.");
            return null;
        } else {
            return val;
        }
    }

    public int leer_Entrada_Teclado(int pDato) {
        set_Valor_Registros("DX", pDato);
        return pDato;
    }

    public void set_Espera(int pEspera) {
        try {
            this.tiempo_CPU += pEspera;
            Thread.sleep(pEspera * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void mostrar_Datos_Registros() {
        System.out.println("Registros:");
        System.out.println("PC: " + this.PC);
        System.out.println("IR: " + this.IR);
        System.out.println("AC: " + this.AC);
        System.out.println("AX: " + this.AX);
        System.out.println("BX: " + this.BX);
        System.out.println("CX: " + this.CX);
        System.out.println("DX: " + this.DX);
        System.out.println("AH: " + this.AH);
        System.out.println("AL: " + this.AL);
    }

    public void reiniciar_Datos_CPU() {
        this.PC = 0;
        this.IR = "";
        this.AC = this.AX = this.BX = this.CX = 0;
        this.DX = "";
        this.AH = "";
        this.AL = "";
        this.flag_CMP = 0;
        this.IO_STATUS = 0;
        this.PID_Proceso_Actual = 0;
        this.ruta_Archivo = "";
        this.proceso_Finalizado = false;
        this.error_Encontrado = false;
        this.descripcion_Error = "";
        this.imprimir_Pantalla = false;
        this.leer_Teclado = false;
    }

    public void reiniciar_Interrupciones() {
        this.proceso_Finalizado = false;
        this.error_Encontrado = false;
        this.imprimir_Pantalla = false;
        this.leer_Teclado = false;
    }
}
