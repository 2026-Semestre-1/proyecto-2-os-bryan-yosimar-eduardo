# Arquitectura del Sistema Operativo V1

**Versión:** 1.0 — Post-refactor  
**Última actualización:** 2026-05-28  
**Stack:** Java 25, Maven, Jackson 2.17, Swing GUI

---

## Índice

1. [Estructura del proyecto](#1-estructura-del-proyecto)
2. [Diagrama de dependencias entre paquetes](#2-diagrama-de-dependencias-entre-paquetes)
3. [Mapa de módulos](#3-mapa-de-módulos)
4. [Flujo principal: ciclo de vida de un proceso](#4-flujo-principal-ciclo-de-vida-de-un-proceso)
5. [Flujo de ejecución de instrucciones (CPU)](#5-flujo-de-ejecución-de-instrucciones-cpu)
6. [Flujo de la GUI](#6-flujo-de-la-gui)
7. [Gestión de memoria](#7-gestión-de-memoria)
8. [Planificación FCFS](#8-planificación-fcfs)
9. [Módulos de modificación futura](#9-módulos-de-modificación-futura)
10. [Guía de modificación](#10-guía-de-modificación)
11. [Métodos críticos por flujo](#11-métodos-críticos-por-flujo)

---

## 1. Estructura del proyecto

```
SIstema_Operativo_V1/
├── pom.xml
├── docs/
│   ├── DEBUG_PLAN_PROCESOS.md        ← Plan de debug (carga de procesos)
│   └── ARQUITECTURA.md               ← Este documento
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── SIstema_Operativo_V1.java         ← Main legacy (no usado)
    │   │   ├── config/
    │   │   │   ├── Configuracion.java            ← POJO de config JSON
    │   │   │   └── Config_Mem.json               ← Config duplicada (legacy)
    │   │   ├── dto/
    │   │   │   └── SnapshotSistema.java          ← DTO de snapshot para GUI
    │   │   ├── gui/
    │   │   │   └── Ventana_Principal.java        ← GUI Swing + entry point
    │   │   ├── kernel/
    │   │   │   ├── NucleoSO.java                ← Fachada del sistema
    │   │   │   ├── Planificador.java            ← Planificador FCFS
    │   │   │   ├── Despachador.java             ← Context switch
    │   │   │   ├── GestorMemoria.java           ← Gestión de RAM + MV
    │   │   │   └── GestorArchivos.java          ← Carga de .asm + config
    │   │   ├── model/
    │   │   │   ├── CPU.java                     ← CPU con registros + ISA
    │   │   │   ├── Memoria.java                 ← RAM (mapa de palabras)
    │   │   │   ├── Almacenamiento.java          ← Disco (índices + MV + programas)
    │   │   │   ├── BCP.java                     ← Bloque de Control de Proceso
    │   │   │   ├── Codigo_ASM.java              ← Contenedor de instrucciones
    │   │   │   └── Instruccion.java             ← Una instrucción ASM parseada
    │   │   └── util/
    │   │       ├── Separar_Instrucciones.java   ← Tokenizador de línea ASM
    │   │       ├── Validar_Formato_ASM.java     ← Validador sintáctico de ASM
    │   │       ├── Calcular_Tiempo_Estimado_Programa.java  ← Pesos de instrucciones
    │   │       └── Procesar_en_Binario.java     ← Conversión a binario (no usado en ciclo principal)
    │   └── resources/
    │       └── Config/
    │           └── Config_Mem.json              ← Config única (usada por classpath)
    └── test/                                    ← (pendiente)
```

---

## 2. Diagrama de dependencias entre paquetes

```
gui (Ventana_Principal)
  │
  └──→ dto (SnapshotSistema) ←── kernel (NucleoSO)
                                        │
                          ┌─────────────┼──────────────────┐
                          │             │                  │
                    Planificador    GestorMemoria   GestorArchivos
                          │             │                  │
                          └──────┬──────┘                  │
                                 │                         │
                            ┌────▼────┐              ┌─────▼──────┐
                            │  model  │              │    util    │
                            │ Memoria │              │ Separar_   │
                            │ BCP     │              │ Instrucc.  │
                            │ CPU     │              │ Validar_   │
                            │ Almacen.│              │ Formato    │
                            │ Codigo  │              └────────────┘
                            │ ASM     │
                            │ Instruc.│
                            └─────────┘
```

**Regla de dependencia:** Las flechas apuntan en dirección de la dependencia. Un paquete **nunca** debe depender de un paquete superior. Ej: `model` no importa `kernel`, `gui` no importa `model` directamente (solo vía `dto`).

---

## 3. Mapa de módulos

### 3.1 `config/` — Configuración del sistema

| Archivo | Rol | ¿Modificable? |
|---|---|---|
| `Configuracion.java` | POJO con getters/setters para Jackson. Campos: `memoria`, `almacenamiento`, `memoria_Virtual`, `cant_CPU` | Solo si se agregan campos al JSON |
| `Config_Mem.json` (/resources) | Archivo de configuración único que el sistema lee vía classpath | **Sí** — cambiar valores aquí |

**Validaciones en `GestorArchivos.cargarConfiguracion()`:**  
`memoria >= 512`, `almacenamiento >= 512`, `memoria_Virtual >= 64`, `cant_CPU >= 1`.

### 3.2 `dto/` — Data Transfer Objects

| Archivo | Rol |
|---|---|
| `SnapshotSistema.java` | Inmutable con 6 campos: `memoria`, `almacenamiento`, `procesos` (Map PID→Estado), `bcpActual`, `procesosTerminados`, `bloqueoInput`. Puente entre kernel y GUI. |

### 3.3 `kernel/` — Lógica del sistema (fachada)

| Archivo | Rol | Dependencias |
|---|---|---|
| `NucleoSO.java` | Fachada principal. Orquesta configuración, carga, planificación, ejecución. Expone `tomarSnapshot()` para la GUI. | `model.*`, `config.*`, `dto.*`, `kernel.*` |
| `Planificador.java` | Planificación FCFS. Administra colas: `pendientes`, `nuevos`, `terminados`. | `model.Memoria`, `model.Almacenamiento`, `GestorMemoria` |
| `Despachador.java` | Context switch: copia BCP ↔ CPU registros. Solo métodos `static`. | `model.CPU`, `model.Memoria`, `model.BCP` |
| `GestorMemoria.java` | Asigna/libera memoria RAM y virtual. Compactación. Operaciones de archivos (crear, abrir, leer, escribir, eliminar). | `model.Memoria`, `model.Almacenamiento` |
| `GestorArchivos.java` | Carga archivos `.asm` desde disco y configuración JSON. | `util.*`, `model.*`, `config.*` |

### 3.4 `model/` — Modelo de datos del sistema

| Archivo | Rol | Campos clave |
|---|---|---|
| `CPU.java` | CPU virtual con registros y ciclo FETCH-DECODE-EXECUTE. ISA de 16 instrucciones. | `PC, IR, AC, AX, BX, CX, DX, AH, AL, flag_CMP, PID_Proceso_Actual, tiempo_CPU` |
| `Memoria.java` | RAM: `Map<Integer,String>`. Área OS (20% mínimo 130) + área usuario. BCPs de 26 slots. | `espacio_Total, espacio_OS, espacio_Usado_OS, TAMANO_BCP=26` |
| `Almacenamiento.java` | Disco: `Map<Integer,String>`. 3 zonas: índices, MV, programas. | `tamano_Total, espacio_Indices, espacio_Memoria_Virtual, espacio_Programas` |
| `BCP.java` | Bloque de Control de Proceso con 22 campos + pila de 5. | `PID, Estado, Mem_Init/End, PC, registros (AC..AL), tiempos, nombre_Programa` |
| `Codigo_ASM.java` | Lista de `Instruccion` + flag de error. | `Instrucciones[], contador_Intrucciones` |
| `Instruccion.java` | Línea ASM parseada: opcode + 3 operandos. | `Instruccion_ASM, Registro_Destino, Operando1, Operando2` |

### 3.5 `util/` — Utilidades sin estado

| Archivo | Rol |
|---|---|
| `Separar_Instrucciones.java` | Tokeniza una línea ASM usando regex `\"[^\"]*\"|\\S+`. Devuelve `List<String>`. |
| `Validar_Formato_ASM.java` | Valida sintaxis instrucción por instrucción. 16 opcodes + chequeo de operandos. |
| `Calcular_Tiempo_Estimado_Programa.java` | Suma duraciones fijas por instrucción. |
| `Procesar_en_Binario.java` | Convierte opcode + registro + valor a binario de 4/4/8 bits. **No usado en ciclo principal.** |

---

## 4. Flujo principal: ciclo de vida de un proceso

```
USUARIO (GUI)
    │
    ▼
[Cargar_Archivos_BTN]             Ventana_Principal.java:382
    │
    ▼
nucleo.cargar_archivo(ruta, nombre)   NucleoSO.java:57
    │
    ├── 1. GestorArchivos.Cargar_Archivo_ASM(ruta)     ← Lee .asm, filtra ; y vacías
    │       └── Validar_Formato_ASM.validacion_Completa()  ← Valida cada línea
    │       └── new Instruccion(linea)                     ← Crea objeto
    │       └── Codigo_ASM.agregar_Intruccion()            ← Acumula
    │
    ├── 2. almacenamiento.asignar_Memoria_A_Programa()  ← Guarda en Disco (zona programas)
    │       └── agregar_Indice()                         ← Crea índice nombre→[inicio,fin]
    │
    ├── 3. planificador.extraer_Programas_Almacenamiento() ← Escanea índices del disco
    │       └── cola_Programas_Pendientes.add(nombre)      ← Agrega los que no están en proceso
    │
    ├── 4. planificador.FSFS_Planificador()              ← FCFS: carga pendientes → RAM
    │       └── for (i = size; i < 5; i++)
    │           ├── Validar OS space (BCP de 26 slots)
    │           ├── Validar User space (instrucciones)
    │           ├── memoria.iniciar_Memoria_BCP()        ← Crea BCP en RAM
    │           ├── controlador_Memoria.asignar_Memoria_Programa() ← Copia código a RAM o MV
    │           └── cola_Procesos_Nuevos.put(pid, bcp)
    │
    ├── 5. planificador.cambiar_Estado_Proceso_Nuevo()   ← Todos "Preparado" (1° "En Ejecuccion")
    │
    └── 6. Si es primer archivo:
            └── iniciar_Despachador(pid)                 ← Carga CPU con BCP del primer proceso
                  └── Despachador.despachador()           ← Copia registros BCP → CPU
```

### Estados de proceso

```
Nuevo ──→ Preparado ──→ En Ejecuccion ──→ Terminado
                 ↑              │
                 └── (Vuelve a Preparado si hay quantum/interrupción)
```

**Actualmente solo FCFS no-expulsivo**: un proceso corre hasta `INT 20H` (finalización) o error.

---

## 5. Flujo de ejecución de instrucciones (CPU)

### 5.1 Ciclo FETCH-DECODE-EXECUTE

```
NucleoSO.ejecutar() / paso_a_paso()
    │
    └── cpu1.ejecutar_Siguiente_Instruccion()        CPU.java:79
            │
            ├── FETCH:  leer_Memoria(PC)              → String cruda desde RAM/MV
            │         via controlador_Memoria.obtener_intruccion_Proceso()
            │
            ├── DECODE: Separar_Instrucciones(IR)     → List<String> [opcode, args...]
            │
            └── EXECUTE: switch(opcode)
                    ├── LOAD, STORE, MOV, ADD, SUB, INC, DEC, SWAP
                    ├── INT (20H=exit, 10H=print, 09H=readkey, 21H=fileIO)
                    ├── JMP, CMP, JE, JNE             ← Saltos condicionales/incondicionales
                    ├── PARAM, PUSH, POP              ← Pila de 5 slots en BCP
                    └── default: "Instruccion desconocida"
```

### 5.2 ISA completa

| Opcode | Args | Efecto | Tiempo |
|---|---|---|---|
| `LOAD` | `reg` | AC ← valor(reg) | 2 |
| `STORE` | `reg` | reg ← AC | 2 |
| `MOV` | `dest, src` | dest ← src (reg o num) | 1 |
| `ADD` | `reg` | AC ← AC + valor(reg) | 3 |
| `SUB` | `reg` | AC ← AC - valor(reg) | 3 |
| `INC` | `[reg]` | reg++ o AC++ | 1 |
| `DEC` | `[reg]` | reg-- o AC-- | 1 |
| `SWAP` | `r1, r2` | intercambia r1↔r2 | 1 |
| `INT` | `20H/10H/09H/21H` | ver §5.3 | 2-5 |
| `JMP` | `offset` | PC += offset | 2 |
| `CMP` | `r1, r2` | flag_CMP = 0/1/2 | 2 |
| `JE` | `offset` | JMP si flag_CMP==0 | 2 |
| `JNE` | `offset` | JMP si flag_CMP!=0 | 2 |
| `PARAM` | `v1[, v2[, v3]]` | Push hasta 3 valores | 3×PUSH |
| `PUSH` | `reg|num` | Apila en BCP | 1 |
| `POP` | `reg` | Desapila a reg | 1 |

### 5.3 Interrupciones INT

| INT | AH | Efecto | Mecanismo |
|---|---|---|---|
| `20H` | — | Finalizar proceso | `proceso_Finalizado = true` |
| `10H` | — | Imprimir DX | `imprimir_Pantalla = true` |
| `09H` | — | Leer teclado | `leer_Teclado = true` → bloquea GUI |
| `21H` | `3Ch` | Crear archivo `nombre=[DX]` | Llama `GestorMemoria.crear_Archivo()` |
| `21H` | `3Dh` | Abrir archivo `nombre=[DX]` | Llama `GestorMemoria.abrir_Archivo()` |
| `21H` | `4Dh` | Leer archivo → AL | Llama `GestorMemoria.leer_Archivo()` |
| `21H` | `40h` | Escribir AL en archivo | Llama `GestorMemoria.escribir_Archivo()` |
| `21H` | `41h` | Eliminar archivo | Llama `GestorMemoria.eliminar_Archivo()` |

### 5.4 Post-ejecución

```java
NucleoSO.paso_a_paso()
    ├── cpu1.ejecutar_Siguiente_Instruccion()
    ├── sincronizar_Datos_CPU_Memoria_BCP()          ← CPU → BCP en RAM
    └── procesar_Interrupciones()
        ├── "1" → Error (finaliza proceso)
        ├── "2" → Input (bloquea GUI)
        ├── "3" → Print (muestra DX)
        ├── "4" → Proceso finalizado (FCFS next)
        └── "0" → OK, continuar
```

---

## 6. Flujo de la GUI

### 6.1 Arquitectura MVC pasivo

La GUI **no accede directamente** a modelos del kernel. Usa `SnapshotSistema` como DTO inmutable:

```
Ventana_Principal
    │
    ├── nucleo.tomarSnapshot()         → SnapshotSistema
    │                                     ├── memoria (referencia para tabla)
    │                                     ├── almacenamiento (referencia para tabla)
    │                                     ├── procesos (Map PID→Estado)
    │                                     ├── bcpActual (BCP o null)
    │                                     ├── procesosTerminados
    │                                     └── bloqueoInput
    │
    └── actualizar_Desde_Snapshot(snap)
        ├── actualizar_tabla_Almacenamiento(snap.almacenamiento)
        ├── actualizar_tabla_Memoria(snap.memoria)
        ├── actualizar_Tabla_Procesos(snap.procesos)
        └── actualizar_Tabla_BCP(snap.bcpActual)
```

### 6.2 Botones principales

| Botón | Método | Qué hace |
|---|---|---|
| **Cargar Archivos** | `Cargar_Archivos_BTNActionPerformed` | JFileChooser multi-selección → `nucleo.cargar_archivo()` por cada archivo → `actualizar_Desde_Snapshot()` |
| **Ejecutar** | `Ejecutar_BTNActionPerformed` | `SwingWorker` que itera `paso_a_paso()` hasta que no hay procesos nuevos. Maneja interrupciones (input, print, error). |
| **Paso a paso** | `Paso_A_Paso_BTNActionPerformed` | Un solo ciclo `paso_a_paso()`. |
| **Limpiar** | `Limpiar_BTNActionPerformed` | `nucleo.reiniciar_programa()` + limpia tablas. |
| **Estadísticas** | `Estadisticas_BTNActionPerformed` | `nucleo.getLista_Procesos_Terminados()` → tabla de PID/inicio/fin/diferencia. |

### 6.3 Manejo de input de teclado (INT 09H)

```
SwingWorker detecta estado "2"
  → bloqueo = true
  → entrada_Por_Teclado()
      → agrega KeyListener al terminal_Text_Area
      → al presionar Enter:
          parsea int → nucleo.leer_Teclado(valor) → bloqueo = false
```

---

## 7. Gestión de memoria

### 7.1 Layout de RAM (`Memoria.java`)

```
Posición 0                          ─┐
  ...                                │
  Posición (espacio_OS - 1)          │  Área OS (BCPs)
  ─────────────────────────────────  ─┤
  Posición espacio_OS                │
  ...                                │  Área Usuario (instrucciones)
  Posición (espacio_Total - 1)      ─┘
```

- `espacio_OS = Math.max((int)(total * 0.2), 5 * TAMANO_BCP)` → mínimo 130 para 5 BCPs.
- Cada BCP = 26 slots (`TAMANO_BCP`). Posiciones relativas:

| Offset | Campo |
|---|---|
| +0 | PID |
| +1 | Estado |
| +2 | Prioridad |
| +3 | Mem_Init |
| +4 | Mem_End |
| +5 | PC |
| +6 | IR |
| +7 | AC |
| +8 | AX |
| +9 | BX |
| +10 | CX |
| +11 | DX |
| +12 | IO_STATUS (lista archivos) |
| +13 | CPU_Asignada |
| +14 | Tiempo_Llegada |
| +15 | Tiempo_Inicio |
| +16 | Tiempo_Finalización |
| +17 | Duración_Estimada |
| +18 | Próximo_Proceso |
| +19..+23 | Pila (5 slots) |
| +24 | AH |
| +25 | AL |

### 7.2 Layout de Disco (`Almacenamiento.java`)

```
Posición 0                          ─┐
  ...                                │  Índices (nombre : inicio - fin)
  Posición (espacio_Indices - 1)    ─┤
  Posición espacio_Indices          ─┤
  ...                                │  Memoria Virtual (cuando RAM llena)
  Posición (MV_start + MV_size -1) ─┤
  Posición (programas_start)        ─┤
  ...                                │  Programas almacenados (código sin BCP)
  Posición (tamano_Total - 1)       ─┘
```

### 7.3 Asignación de programa

```
GestorMemoria.asignar_Memoria_Programa(codigo)
    ├── if (RAM_USADO + tamano <= RAM_USER):  → RAM
    └── else:                                  → MV (Disco)
```

### 7.4 Compactación

`GestorMemoria.compactar_SO()` y `compactar_Usuario_Desde()` reordenan BCPs e instrucciones para eliminar huecos. Se invocan desde `liberar_Memoria_Proceso`/`limpiar_Memoria_Proceso`.

---

## 8. Planificación FCFS

### 8.1 Estructura (`Planificador.java`)

```
cola_Programas_Pendientes  ← Programas en disco aún no cargados a RAM
cola_Procesos_Nuevos       ← Procesos con BCP en RAM (PID → BCP)
cola_Procesos_Terminados   ← Procesos que finalizaron
```

### 8.2 Algoritmo FSFS_Planificador

```java
for (i = cola_Procesos_Nuevos.size(); i < 5; i++) {
    // Toma el primero de pendientes
    // Valida espacio OS (BCP de 26 slots)
    // Valida espacio usuario (instrucciones)
    // Si hay espacio: crea BCP, asigna memoria, agrega a cola_Procesos_Nuevos
    // Si no: break
}
```

- **Límite 5 procesos simultáneos** (hardcodeado en el `for`).
- No-expulsivo: un proceso corre hasta `INT 20H` o error.
- `cambiar_Estado_Proceso_Nuevo()` pone todos "Preparado" (primero "En Ejecuccion").

---

## 9. Módulos de modificación futura

### 9.1 Agregar una nueva instrucción al CPU

**Archivos a tocar:**

| Archivo | Cambio |
|---|---|
| `util/Validar_Formato_ASM.java:7` | Agregar opcode a `instruccionesPermitidas` |
| `util/Validar_Formato_ASM.java:60` | Agregar `case "NUEVA":` en `validacion_Completa()` |
| `util/Validar_Formato_ASM.java` | Crear método `validarNUEVA()` |
| `util/Calcular_Tiempo_Estimado_Programa.java:27` | Agregar `case "NUEVA": return N;` |
| `model/CPU.java:89` | Agregar `case "NUEVA":` en switch de `ejecutar_Siguiente_Instruccion()` |
| `model/CPU.java` | Crear `private void ejecutar_NUEVA(...)` |

**Ejemplo** — agregar instrucción `MULT`:

```java
// Validar_Formato_ASM.java
private final Set<String> instruccionesPermitidas = Set.of(..., "MULT");

// validacion_Completa switch
case "MULT": return validarADD_SUB(elementos, "MULT");  // misma firma que ADD

// Calcular_Tiempo_Estimado_Programa.java
case "MULT": return 4;

// CPU.java switch
case "MULT": ejecutar_MULT(operando1); this.PC++; break;

// CPU.java
private void ejecutar_MULT(String operandos) {
    set_Espera(4);
    Integer val = get_Valor_Registros(operandos);
    if (val == null) { error... return; }
    AC *= val;
}
```

### 9.2 Cambiar el planificador (FCFS → Round Robin, SJF, etc.)

| Archivo | Cambio |
|---|---|
| `kernel/Planificador.java` | Reemplazar `FSFS_Planificador()` con nuevo algoritmo |
| `kernel/Planificador.java` | Agregar colas auxiliares si necesario (ej: cola_listos para RR) |
| `kernel/NucleoSO.java` | Si el nuevo planificador requiere quantum, modificar `ejecutar()` y `paso_a_paso()` para desalojo |
| `model/CPU.java` | Agregar flag `quantum_expirado` si se necesita desalojo por tiempo |

**Puntos de enganche para Round Robin:**

- `NucleoSO.ejecutar()`: tras N instrucciones, forzar desalojo → guardar contexto → planificar siguiente.
- `Planificador`: agregar `cola_Procesos_Listos` separada de `cola_Procesos_Nuevos` (que pasa a ser la cola de "admitidos").
- `Despachador`: ya hace context-switch completo.

### 9.3 Agregar multiprocesamiento (varios CPUs)

| Archivo | Cambio |
|---|---|
| `config/Config_Mem.json` | `cant_CPU` ya existe |
| `kernel/NucleoSO.java` | Cambiar `CPU cpu1` por `List<CPU> CPUs`. `crear_CPU()` ya recibe `cant_CPU` |
| `kernel/NucleoSO.java` | `iniciar_Despachador()` debe seleccionar CPU libre |
| `kernel/Planificador.java` | Asignar procesos a CPUs disponibles |
| `model/CPU.java` | Sin cambios (ya es independiente por instancia) |

**Ejemplo de cambio en NucleoSO:**

```java
private List<CPU> CPUs = new ArrayList<>();

public void crear_CPU(int cant_CPU) {
    for (int i = 0; i < cant_CPU; i++) {
        CPUs.add(new CPU(i + 1, memoria, controlador_Memoria));
    }
}

public void iniciar_Despachador(int pPID) {
    CPU cpuLibre = CPUs.stream()
        .filter(c -> c.getPID_Proceso_Actual() == 0)
        .findFirst().orElse(CPUs.get(0));
    Despachador.despachador(cpuLibre, memoria, pPID);
    memoria.actualizar_Estado_BCP(pPID, "En Ejecucion");
    cpuLibre.setPID_Proceso_Actual(pPID);
}
```

### 9.4 Agregar memoria virtual por paginación

| Archivo | Cambio |
|---|---|
| `kernel/GestorMemoria.java` | Reemplazar asignación contigua por tabla de páginas |
| `model/Memoria.java` | Agregar `Map<Integer, Integer>` página→marco |
| `model/CPU.java` | `leer_Memoria()` debe traducir dirección virtual a física con tabla de páginas |
| `kernel/Planificador.java` | `iniciar_Memoria_BCP()` debe crear tabla de páginas en lugar de Mem_Init/Mem_End |

### 9.5 Agregar colas de I/O o dispositivos

| Archivo | Cambio |
|---|---|
| `kernel/NucleoSO.java` | Agregar cola de procesos bloqueados por I/O |
| `model/CPU.java` | `ejecutar_INT()` con nueva interrupción de I/O → bloquea proceso |
| `kernel/Planificador.java` | Mover proceso a cola_bloqueados cuando hace I/O, devolver a listos cuando I/O completa |

---

## 10. Guía de modificación

### 10.1 Conventions

- **Sangría:** El proyecto usa mezcla de tabs de 4 espacios y espacios sueltos. Para cambios nuevos, usa **4 espacios** consistente.
- **Nombres:** `snake_case` para variables locales y métodos (legacy). Los nuevos cambios pueden usar `camelCase` pero es preferible mantener `snake_case` para consistencia con el código existente en el mismo archivo.
- **Strings de estado:** mayúscula inicial ("Nuevo", "Preparado", "En Ejecuccion", "Terminado"). **Atención:** "Ejecuccion" tiene doble 'c' en el legacy — no corregir sin avisar al equipo.
- **Logs:** Los `System.out.println` son el mecanismo de logging. Prefijo `[DEBUG MODULO]` para logs de rastreo temporales.
- **Dependencias:** Nunca importar de `kernel` en `model` o `util`. `gui` solo habla con `kernel` vía `NucleoSO` y recibe datos vía `SnapshotSistema`.

### 10.2 Proceso para hacer cambios

1. Identificar qué flujo se afecta (ver [§11](#11-métodos-críticos-por-flujo)).
2. Modificar `model/` primero si el cambio es estructural.
3. Modificar `kernel/` después (la lógica de negocio).
4. Modificar `util/` si se agregan validaciones o transformaciones.
5. Modificar `gui/` solo si el cambio requiere nuevos datos visuales (agregar campo a `SnapshotSistema` y a `actualizar_Desde_Snapshot`).
6. Compilar: `mvn clean compile`.

### 10.3 Pruebas manuales post-cambio

| Cambio | Verificación |
|---|---|
| Nueva instrucción | Cargar .asm con la instrucción → debe ejecutarse sin error |
| Nuevo planificador | 5 procesos deben mostrar correctamente sus estados |
| Multiprocesamiento | Todos los CPUs deben mostrar procesos distintos |
| Memoria virtual | Programas que exceden RAM deben ejecutarse desde MV sin error |

---

## 11. Métodos críticos por flujo

### Flujo de carga de archivos

```mermaid
NucleoSO.cargar_archivo()
  → GestorArchivos.Cargar_Archivo_ASM()        [Entrada: ruta archivo .asm]
  → Almacenamiento.asignar_Memoria_A_Programa() [Entrada: Codigo_ASM, nombre]
  → Planificador.extraer_Programas_Almacenamiento() [Entrada: Almacenamiento]
  → Planificador.FSFS_Planificador()            [Entrada: Memoria, Almacenamiento, tiempo_CPU]
  → Planificador.cambiar_Estado_Proceso_Nuevo() [Salida: estados actualizados]
```

### Flujo de ejecución

```mermaid
NucleoSO.paso_a_paso()
  → CPU.ejecutar_Siguiente_Instruccion()        [FETCH → DECODE → EXECUTE]
  → NucleoSO.sincronizar_Datos_CPU_Memoria_BCP() [CPU → RAM]
  → NucleoSO.procesar_Interrupciones()          [Salida: código de estado]
  → (si finalizó) NucleoSO.finalizacion_Proceso_FCFS()
  → Planificador.FSFS_Planificador()            [Cargar siguiente]
  → Planificador.hay_Procesos_Nuevos()          [¿Quedan procesos?]
```

### Flujo de finalización

```mermaid
CPU.ejecutar_INT("20H")
  → CPU.setProceso_Finalizado(true)
  → NucleoSO.paso_a_paso() detecta flag "4"
  → NucleoSO.finalizacion_Proceso_FCFS(PID)
    → Planificador.finalizacion_Procesos()      [Estado → "Terminado", compacta memoria]
    → Planificador.FSFS_Planificador()           [Carga siguiente si hay]
    → CPU.reiniciar_Datos_CPU()
    → Despachador.despachador(siguiente)         [Context switch]
```

### Métodos hoja (sin llamados a otros módulos kernel)

| Método | Archivo | Hace |
|---|---|---|
| `memoria.iniciar_Memoria_BCP()` | `Memoria.java:58` | Escribe 26 slots del BCP en RAM |
| `memoria.obtener_Datos_BCP()` | `Memoria.java:155` | Lee 26 slots y construye `BCP` |
| `memoria.buscar_Posicion_BCP()` | `Memoria.java:153` | Escanea área OS buscando PID |
| `memoria.validar_Espacio_Disponible_OS()` | `Memoria.java:178` | ¿Cabe un BCP más? |
| `Separar_Instrucciones.separar_Instrucciones()` | `Separar_Instrucciones.java:10` | Tokeniza línea ASM |
| `Validar_Formato_ASM.validacion_Completa()` | `Validar_Formato_ASM.java:28` | Valida sintaxis |
| `Calcular_Tiempo_Estimado_Programa.calcular_Tiempo_Estimado()` | `Calcular_Tiempo_Estimado_Programa.java:10` | Suma pesos de todas las instrucciones |

### Cadena de llamados críticos (quién llama a quién)

```
Ventana_Principal
  → NucleoSO.cargar_archivo()
    → GestorArchivos.Cargar_Archivo_ASM()
      → Validar_Formato_ASM.validacion_Completa()
      → Separar_Instrucciones.separar_Instrucciones()
      → Instruccion constructor
    → Almacenamiento.asignar_Memoria_A_Programa()
    → Planificador.extraer_Programas_Almacenamiento()
    → Planificador.FSFS_Planificador()
      → GestorMemoria.validar_Espacio_Disponible_Usuario()
      → Memoria.validar_Espacio_Disponible_OS()
      → Memoria.iniciar_Memoria_BCP()
      → GestorMemoria.asignar_Memoria_Programa()
      → Planificador.agregar_Proceso_Nuevo()
    → Planificador.cambiar_Estado_Proceso_Nuevo()
      → BCP.setEstado()
      → GestorMemoria.actualizar_Estado_BCP()

  → NucleoSO.paso_a_paso()
    → CPU.ejecutar_Siguiente_Instruccion()
      → GestorMemoria.obtener_intruccion_Proceso()   ← FETCH
      → Separar_Instrucciones.separar_Instrucciones() ← DECODE
      → [ejecutar_LOAD | ejecutar_MOV | ... ]         ← EXECUTE
        → [get_Valor_Registros | set_Valor_Registros]
        → (INT 21H) GestorMemoria.crear_Archivo() ...
    → Memoria.actualizar_Registros_BCP()               ← sincronizar
    → NucleoSO.procesar_Interrupciones()
    → (si finalizó) NucleoSO.finalizacion_Proceso_FCFS()
      → Planificador.finalizacion_Procesos()
      → Planificador.FSFS_Planificador()
      → CPU.reiniciar_Datos_CPU()
      → Despachador.despachador()

  → NucleoSO.tomarSnapshot()
    → Planificador.obtener_Estado_5_Procesos()
    → NucleoSO.obtener_Datos_BCP_Actual()
```

---

## Apéndice A: Config_Mem.json

```json
{
    "memoria": 512,
    "almacenamiento": 512,
    "memoria_Virtual": 64,
    "cant_CPU": 1
}
```

El archivo se lee desde `src/main/resources/Config/Config_Mem.json` (classpath).

Validaciones mínimas en `GestorArchivos.cargarConfiguracion()`:
- `memoria >= 512`
- `almacenamiento >= 512`
- `memoria_Virtual >= 64`
- `cant_CPU >= 1`

Si no cumple, la configuración se rechaza y el sistema no arranca correctamente.

---

## Apéndice B: Archivos legacy eliminados durante el refactor

| Archivo original | Destino |
|---|---|
| `Modelo/BCP.java` | → `model/BCP.java` |
| `Modelo/Memoria.java` | → `model/Memoria.java` |
| `Modelo/Almacenamiento.java` | → `model/Almacenamiento.java` |
| `Modelo/Codigo_ASM.java` | → `model/Codigo_ASM.java` |
| `Modelo/Instruccion.java` | → `model/Instruccion.java` |
| `Modelo/CPU.java` | → `model/CPU.java` |
| `Controlador/Controlador_Planificador.java` | → `kernel/Planificador.java` |
| `Controlador/Controlador_Memoria.java` | → `kernel/GestorMemoria.java` |
| `Controlador/Controlador_Archivos.java` | → `kernel/GestorArchivos.java` |
| `Controlador/Controlador_Principal.java` | → `kernel/NucleoSO.java` |
| `Vista/Ventana_Principal.java` | → `gui/Ventana_Principal.java` |
| `Controlador/Despachador.java` | → `kernel/Despachador.java` |
| `util/Separar_Instrucciones.java` | Sin cambios de paquete |
| `util/Validar_Formato_ASM.java` | Sin cambios de paquete |
| `util/Calcular_Tiempo_Estimado_Programa.java` | Sin cambios de paquete |
| `util/Procesar_en_Binario.java` | Sin cambios de paquete |
| `cr/` | Eliminado (experimental) |
