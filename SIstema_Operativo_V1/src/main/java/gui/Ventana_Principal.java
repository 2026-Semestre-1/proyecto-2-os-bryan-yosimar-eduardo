package gui;

import kernel.NucleoSO;
import model.Almacenamiento;
import model.BCP;
import model.Memoria;
import dto.SnapshotSistema;

import java.io.File;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class Ventana_Principal extends javax.swing.JFrame {

        private static final java.util.logging.Logger logger = java.util.logging.Logger
                        .getLogger(Ventana_Principal.class.getName());

        private static NucleoSO nucleo;

        private String modeloGestionMemoria = "Paginacion";

        private Boolean bloqueo = false;

        public Ventana_Principal() {
                initComponents();
                nucleo = new NucleoSO();
                nucleo.configurarMemoria(modeloGestionMemoria);
                iniciar_Contenido_Base_tablas();
        }

        @SuppressWarnings("unchecked")
        private void initComponents() {

                Ejecutar_BTN = new javax.swing.JButton();
                Paso_A_Paso_BTN = new javax.swing.JButton();
                Limpiar_BTN = new javax.swing.JButton();
                Estadisticas_BTN = new javax.swing.JButton();
                Cargar_Archivos_BTN = new javax.swing.JButton();
                jScrollPane1 = new javax.swing.JScrollPane();
                Tabla_Procesos = new javax.swing.JTable();
                jScrollPane2 = new javax.swing.JScrollPane();
                Tabla_BCP = new javax.swing.JTable();
                jScrollPane3 = new javax.swing.JScrollPane();
                Tabla_Memoria = new javax.swing.JTable();
                jScrollPane4 = new javax.swing.JScrollPane();
                Tabla_Almacenamiento = new javax.swing.JTable();
                Lista_Procesos_Label = new javax.swing.JLabel();
                BCP_Label = new javax.swing.JLabel();
                Memoria_Label = new javax.swing.JLabel();
                Almacenamiento_Label = new javax.swing.JLabel();
                jScrollPane5 = new javax.swing.JScrollPane();
                terminal_Text_Area = new javax.swing.JTextArea();
                Terminal_Text_Area_Label = new javax.swing.JLabel();
                jScrollPane6 = new javax.swing.JScrollPane();
                Tabla_Estadisticas = new javax.swing.JTable();
                jLabel1 = new javax.swing.JLabel();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                setBackground(new java.awt.Color(102, 102, 102));

                Ejecutar_BTN.setText("Ejecutar");
                Ejecutar_BTN.addActionListener(this::Ejecutar_BTNActionPerformed);

                Paso_A_Paso_BTN.setText("Paso a paso");
                Paso_A_Paso_BTN.addActionListener(this::Paso_A_Paso_BTNActionPerformed);

                Limpiar_BTN.setText("Limpiar");
                Limpiar_BTN.addActionListener(this::Limpiar_BTNActionPerformed);

                Estadisticas_BTN.setText("Estadisticas");
                Estadisticas_BTN.addActionListener(this::Estadisticas_BTNActionPerformed);

                Cargar_Archivos_BTN.setText("Cargar Archivos");
                Cargar_Archivos_BTN.addActionListener(this::Cargar_Archivos_BTNActionPerformed);

                Tabla_Procesos.setModel(new javax.swing.table.DefaultTableModel(
                                new Object[][] {

                                },
                                new String[] {
                                                "Proceso", "Estado"
                                }) {
                        Class[] types = new Class[] {
                                        java.lang.String.class, java.lang.String.class
                        };

                        public Class getColumnClass(int columnIndex) {
                                return types[columnIndex];
                        }
                });
                jScrollPane1.setViewportView(Tabla_Procesos);

                Tabla_BCP.setModel(new javax.swing.table.DefaultTableModel(
                                new Object[][] {
                                                { "PID:", null },
                                                { "Estado:", null },
                                                { "Prioridad:", null },
                                                { "Inicio Memoria:", null },
                                                { "Final Memoria:", null },
                                                { "PC:", null },
                                                { "IR:", null },
                                                { "AC:", null },
                                                { "AX:", null },
                                                { "BX", null },
                                                { "CX:", null },
                                                { "DX:", null },
                                                { "AH:", null },
                                                { "AL:", null },
                                                { "Lista Archivos:", null },
                                                { "CPU Asignada:", null },
                                                { "Tiempo Llegada:", null },
                                                { "Tiempo Inicio:", null },
                                                { "Tiempo Finalizaci\u00f3n:", null },
                                                { "Duraci\u00f3n Estimada:", null },
                                                { "Proximo Proceso:", null },
                                                { "Pila (0):", null },
                                                { "Pila (1):", null },
                                                { "Pila (2):", null },
                                                { "Pila (3):", null },
                                                { "Pila (4):", null }
                                },
                                new String[] {
                                                "Dato", "Valor"
                                }));
                jScrollPane2.setViewportView(Tabla_BCP);

                Tabla_Memoria.setModel(new javax.swing.table.DefaultTableModel(
                                new Object[][] {

                                },
                                new String[] {
                                                "Posicion", "Valor"
                                }) {
                        Class[] types = new Class[] {
                                        java.lang.String.class, java.lang.String.class
                        };

                        public Class getColumnClass(int columnIndex) {
                                return types[columnIndex];
                        }
                });
                jScrollPane3.setViewportView(Tabla_Memoria);

                Tabla_Almacenamiento.setModel(new javax.swing.table.DefaultTableModel(
                                new Object[][] {

                                },
                                new String[] {
                                                "Posicion", "Valor"
                                }));
                jScrollPane4.setViewportView(Tabla_Almacenamiento);

                Lista_Procesos_Label.setText("Lista de procesos");

                BCP_Label.setText("BCP");

                Memoria_Label.setText("Memoria");

                Almacenamiento_Label.setText("Almacenamiento");

                terminal_Text_Area.setColumns(20);
                terminal_Text_Area.setRows(5);
                jScrollPane5.setViewportView(terminal_Text_Area);

                Terminal_Text_Area_Label.setText("Terminal");

                Tabla_Estadisticas.setModel(new javax.swing.table.DefaultTableModel(
                                new Object[][] {

                                },
                                new String[] {
                                                "PID", "Inicio", "Finalizaci\u00f3n", "Diferencia"
                                }));
                jScrollPane6.setViewportView(Tabla_Estadisticas);

                jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36));
                jLabel1.setText("Sistema Operativo V1");

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                .addGap(14, 14, 14)
                                                                                                .addGroup(layout.createParallelGroup(
                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                                                .addComponent(jScrollPane1,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                207,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addGap(37, 37, 37)
                                                                                                                                .addComponent(jScrollPane2,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                201,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addGap(30, 30, 30)
                                                                                                                                .addComponent(jScrollPane3,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                198,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addGap(32, 32, 32)
                                                                                                                                .addComponent(jScrollPane4,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                203,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addGap(51, 51, 51)
                                                                                                                                .addComponent(jScrollPane5,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                .addComponent(Cargar_Archivos_BTN)
                                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                                                .addGap(55, 55, 55)
                                                                                                                                .addComponent(Lista_Procesos_Label)
                                                                                                                                .addGap(177, 177,
                                                                                                                                                177)
                                                                                                                                .addComponent(BCP_Label)
                                                                                                                                .addGap(199, 199,
                                                                                                                                                199)
                                                                                                                                .addComponent(Memoria_Label)
                                                                                                                                .addGap(170, 170,
                                                                                                                                                170)
                                                                                                                                .addComponent(Almacenamiento_Label)
                                                                                                                                .addGap(187, 187,
                                                                                                                                                187)
                                                                                                                                .addComponent(Terminal_Text_Area_Label))
                                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                                                .addComponent(Ejecutar_BTN)
                                                                                                                                .addGap(32, 32, 32)
                                                                                                                                .addComponent(Paso_A_Paso_BTN)
                                                                                                                                .addGap(18, 18, 18)
                                                                                                                                .addGroup(layout.createParallelGroup(
                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                                                                                .addComponent(Limpiar_BTN)
                                                                                                                                                                .addGap(18, 18, 18)
                                                                                                                                                                .addComponent(Estadisticas_BTN))
                                                                                                                                                .addComponent(jScrollPane6,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                771,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                .addGap(421, 421, 421)
                                                                                                .addComponent(jLabel1)))
                                                                .addContainerGap(52, Short.MAX_VALUE)));
                layout.setVerticalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addGap(21, 21, 21)
                                                                .addComponent(jLabel1)
                                                                .addGap(35, 35, 35)
                                                                .addGroup(layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(Ejecutar_BTN)
                                                                                .addComponent(Paso_A_Paso_BTN)
                                                                                .addComponent(Limpiar_BTN)
                                                                                .addComponent(Estadisticas_BTN))
                                                                .addGap(28, 28, 28)
                                                                .addComponent(Cargar_Archivos_BTN)
                                                                .addGap(29, 29, 29)
                                                                .addGroup(layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(Lista_Procesos_Label)
                                                                                .addComponent(BCP_Label)
                                                                                .addComponent(Memoria_Label)
                                                                                .addComponent(Almacenamiento_Label)
                                                                                .addComponent(Terminal_Text_Area_Label))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jScrollPane5,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                247,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGroup(layout.createParallelGroup(
                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                false)
                                                                                                .addComponent(jScrollPane4,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                289,
                                                                                                                Short.MAX_VALUE)
                                                                                                .addComponent(jScrollPane3,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                289,
                                                                                                                Short.MAX_VALUE)
                                                                                                .addComponent(jScrollPane2,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                0,
                                                                                                                Short.MAX_VALUE)
                                                                                                .addComponent(jScrollPane1,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                0,
                                                                                                                Short.MAX_VALUE)))
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jScrollPane6,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                244,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(157, Short.MAX_VALUE)));

                pack();
        }

        private void Ejecutar_BTNActionPerformed(java.awt.event.ActionEvent evt) {

                if (this.bloqueo)
                        return;

                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                                while (nucleo.hay_Procesos_Nuevos()) {
                                        while (!nucleo.comprobar_Finalizacion_Proceso()) {
                                                if (bloqueo)
                                                        return null;

                                                List<String> estado_Ejecucion = nucleo.paso_a_paso();
                                                if (estado_Ejecucion == null) {
                                                        SwingUtilities.invokeLater(() -> {
                                                                limpiar_tabla_BCP();
                                                                limpiar_terminal();
                                                        });
                                                        return null;
                                                }

                                                String lineaActual = estado_Ejecucion.get(0);
                                                switch (lineaActual) {
                                                        case "1":
                                                                JOptionPane.showMessageDialog(null,
                                                                                "Error: " + estado_Ejecucion.get(1));
                                                                break;
                                                        case "2":
                                                                bloqueo = true;
                                                                entrada_Por_Teclado();
                                                                break;
                                                        case "3":
                                                                imprimiar_En_Pantalla(estado_Ejecucion.get(1));
                                                                break;
                                                        case "4":
                                                                limpiar_tabla_BCP();
                                                                limpiar_terminal();
                                                                break;
                                                }

                                                nucleo.activar_Espera();

                                                SwingUtilities.invokeLater(() -> actualizar_Tablas());
                                        }
                                        nucleo.procesar_Finalizacion_Proceso();
                                }
                                return null;
                        }
                };
                worker.execute();

        }

        private void Paso_A_Paso_BTNActionPerformed(java.awt.event.ActionEvent evt) {

                if (this.bloqueo == true) {
                        return;
                }

                List<String> estado_Ejecucion = this.nucleo.paso_a_paso();

                if (estado_Ejecucion == null) {
                        this.limpiar_tabla_BCP();
                        this.limpiar_terminal();
                        return;
                }

                String lineaActual = estado_Ejecucion.get(0);

                if (lineaActual.equals("1")) {
                        JOptionPane.showMessageDialog(null, "Error: " + estado_Ejecucion.get(1));

                } else if (lineaActual.equals("2")) {
                        this.bloqueo = true;
                        this.entrada_Por_Teclado();

                } else if (lineaActual.equals("3")) {
                        this.imprimiar_En_Pantalla(estado_Ejecucion.get(1));

                } else if (lineaActual.equals("4")) {
                        limpiar_tabla_BCP();
                        limpiar_terminal();

                }

                this.actualizar_Tablas();

        }

        private void Limpiar_BTNActionPerformed(java.awt.event.ActionEvent evt) {

                this.nucleo.reiniciar_programa();
                this.iniciar_Contenido_Base_tablas();
                this.bloqueo = false;

        }

        private void Estadisticas_BTNActionPerformed(java.awt.event.ActionEvent evt) {

                if (this.bloqueo == true) {
                        return;
                }

                List<BCP> estadisticas = this.nucleo.getLista_Procesos_Terminados();

                actualizar_Tabla_Estadisticas(estadisticas);

        }

        private void Cargar_Archivos_BTNActionPerformed(java.awt.event.ActionEvent evt) {

                JFileChooser seleccion_Archivos = new JFileChooser();

                FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos ASM", "asm");

                seleccion_Archivos.setFileFilter(filtro);

                seleccion_Archivos.setAcceptAllFileFilterUsed(false);

                seleccion_Archivos.setMultiSelectionEnabled(true);

                int opcion = seleccion_Archivos.showOpenDialog(this);

                if (opcion == JFileChooser.APPROVE_OPTION) {
                        File[] archivosASM = seleccion_Archivos.getSelectedFiles();

                        for (File archivo : archivosASM) {
                                if (archivo != null) {
                                        String ruta_Archivo = archivo.getAbsolutePath();
                                        List<String> resultado = nucleo.cargar_archivo(ruta_Archivo,
                                                        archivo.getName());

                                        if (!resultado.get(0).equals("0")) {
                                                JOptionPane.showMessageDialog(null, resultado.get(1));
                                        }
                                }
                        }
                }

                SnapshotSistema snap = nucleo.tomarSnapshot();
                actualizar_Desde_Snapshot(snap);

        }

        public void actualizar_Tablas() {
                SnapshotSistema snap = nucleo.tomarSnapshot();
                actualizar_Desde_Snapshot(snap);
        }

        private void actualizar_Desde_Snapshot(SnapshotSistema snap) {
                actualizar_tabla_Almacenamiento(snap.almacenamiento);
                actualizar_tabla_Memoria(snap.memoria);
                actualizar_Tabla_Procesos(snap.procesos);
                actualizar_Tabla_BCP(snap.bcpActual);
        }

        public void iniciar_Contenido_Base_tablas() {

                DefaultTableModel modelo_Tabla_Procesos = (DefaultTableModel) Tabla_Procesos.getModel();

                modelo_Tabla_Procesos.setRowCount(0);

                for (int i = 0; i < 20; i++) {
                        modelo_Tabla_Procesos.addRow(new Object[] { " ", " " });
                }

                SnapshotSistema snap = nucleo.tomarSnapshot();
                actualizar_tabla_Memoria(snap.memoria);
                actualizar_tabla_Almacenamiento(snap.almacenamiento);
        }

        public void actualizar_Tabla_Procesos(Map<Integer, String> lista_Procesos) {
                DefaultTableModel modeloTabla = (DefaultTableModel) Tabla_Procesos.getModel();
                modeloTabla.setRowCount(0);

                for (Map.Entry<Integer, String> entry : lista_Procesos.entrySet()) {
                        int pid = entry.getKey();
                        String estado = entry.getValue();
                        modeloTabla.addRow(new Object[] { pid, estado });
                }
        }

        public void actualizar_Tabla_BCP(BCP datos_BCP) {
                DefaultTableModel modeloTabla = (DefaultTableModel) Tabla_BCP.getModel();

                if (datos_BCP == null) {
                        return;
                }

                modeloTabla.setValueAt(datos_BCP.getPID(), 0, 1);
                modeloTabla.setValueAt(datos_BCP.getEstado(), 1, 1);
                modeloTabla.setValueAt(datos_BCP.getPrioridad(), 2, 1);
                modeloTabla.setValueAt(datos_BCP.getMem_Init(), 3, 1);
                modeloTabla.setValueAt(datos_BCP.getMem_End(), 4, 1);
                modeloTabla.setValueAt(datos_BCP.getPC(), 5, 1);
                modeloTabla.setValueAt(datos_BCP.getIR(), 6, 1);

                modeloTabla.setValueAt(datos_BCP.getAC(), 7, 1);
                modeloTabla.setValueAt(datos_BCP.getAX(), 8, 1);
                modeloTabla.setValueAt(datos_BCP.getBX(), 9, 1);
                modeloTabla.setValueAt(datos_BCP.getCX(), 10, 1);
                modeloTabla.setValueAt(datos_BCP.getDX(), 11, 1);
                modeloTabla.setValueAt(datos_BCP.getAH(), 12, 1);
                modeloTabla.setValueAt(datos_BCP.getAL(), 13, 1);

                modeloTabla.setValueAt(datos_BCP.getIO_STATUS(), 14, 1);
                modeloTabla.setValueAt(datos_BCP.getCPU_Asignada(), 15, 1);
                modeloTabla.setValueAt(datos_BCP.getTiempo_Llegada(), 16, 1);
                modeloTabla.setValueAt(datos_BCP.getTiempo_Inicio(), 17, 1);
                modeloTabla.setValueAt(datos_BCP.getTiempo_Finalizacion(), 18, 1);
                modeloTabla.setValueAt(datos_BCP.getTiempo_Ejecucion(), 19, 1);
                modeloTabla.setValueAt(datos_BCP.getProximo_Proceso(), 20, 1);
                int[] pila = datos_BCP.getPila();
                for (int i = 0; i < pila.length; i++) {
                        modeloTabla.setValueAt(pila[i], i + 21, 1);
                }
        }

        public void actualizar_tabla_Memoria(Memoria pMemoria) {

                if (pMemoria == null) {
                        return;
                }

                DefaultTableModel modeloTabla = (DefaultTableModel) Tabla_Memoria.getModel();

                modeloTabla.setRowCount(0);

                Map<Integer, String> memoria_Actual = pMemoria.getMemoria_Principal();

                for (int i = 0; i < pMemoria.getEspacio_Total(); i++) {
                        modeloTabla.addRow(new Object[] { i, memoria_Actual.get(i) });
                }
        }

        public void actualizar_tabla_Almacenamiento(Almacenamiento pAlmacenamiento) {

                if (pAlmacenamiento == null) {
                        return;
                }

                DefaultTableModel modeloTabla = (DefaultTableModel) Tabla_Almacenamiento.getModel();

                modeloTabla.setRowCount(0);

                Map<Integer, String> almacenamiento_Actual = pAlmacenamiento.getMemoria_Secundaria();

                for (int i = 0; i < pAlmacenamiento.getTamano_Total(); i++) {
                        modeloTabla.addRow(new Object[] { i, almacenamiento_Actual.get(i) });
                }
        }

        public void limpiar_terminal() {
                terminal_Text_Area.setText("");
        }

        public void limpiar_tabla_BCP() {

                DefaultTableModel modeloTabla = (DefaultTableModel) Tabla_BCP.getModel();
                modeloTabla.setValueAt("", 0, 1);
                modeloTabla.setValueAt("", 1, 1);
                modeloTabla.setValueAt("", 2, 1);
                modeloTabla.setValueAt("", 3, 1);
                modeloTabla.setValueAt("", 4, 1);
                modeloTabla.setValueAt("", 5, 1);
                modeloTabla.setValueAt("", 6, 1);

                modeloTabla.setValueAt("", 7, 1);
                modeloTabla.setValueAt("", 8, 1);
                modeloTabla.setValueAt("", 9, 1);
                modeloTabla.setValueAt("", 10, 1);
                modeloTabla.setValueAt("", 11, 1);
                modeloTabla.setValueAt("", 12, 1);
                modeloTabla.setValueAt("", 13, 1);

                modeloTabla.setValueAt("", 14, 1);
                modeloTabla.setValueAt("", 15, 1);
                modeloTabla.setValueAt("", 16, 1);
                modeloTabla.setValueAt("", 17, 1);
                modeloTabla.setValueAt("", 18, 1);
                modeloTabla.setValueAt("", 19, 1);
                modeloTabla.setValueAt("", 20, 1);

                for (int i = 0; i < 5; i++) {
                        modeloTabla.setValueAt("", i + 21, 1);
                }

        }

        public void limpiar_Tabla_Procesos() {
                DefaultTableModel modeloTabla = (DefaultTableModel) Tabla_Procesos.getModel();
                modeloTabla.setRowCount(0);
        }

        public void actualizar_Tabla_Estadisticas(List<BCP> pLista_BCP) {
                DefaultTableModel modeloTabla = (DefaultTableModel) Tabla_Estadisticas.getModel();
                modeloTabla.setRowCount(0);

                for (BCP bcp : pLista_BCP) {

                        int pid = bcp.getPID();

                        LocalTime inicio = bcp.get_momento_creacion();
                        LocalTime fin = bcp.get_momento_finalizacion();

                        long diferencia = 0;
                        if (inicio != null && fin != null) {
                                diferencia = java.time.Duration.between(inicio, fin).getSeconds();
                        }

                        Object[] row = new Object[] {
                                        pid,
                                        inicio != null ? inicio.toString() : "-",
                                        fin != null ? fin.toString() : "-",
                                        diferencia
                        };

                        modeloTabla.addRow(row);
                }
        }

        public void imprimiar_En_Pantalla(String pMensaje) {

                String mensajeAnterior = terminal_Text_Area.getText();

                if (mensajeAnterior != null && !mensajeAnterior.isEmpty()) {
                        terminal_Text_Area.setText(mensajeAnterior + "\n" + "> Print:" + pMensaje);
                } else {
                        terminal_Text_Area.setText(pMensaje);
                }
        }

        public void entrada_Por_Teclado() {
                String mensajeAnterior = terminal_Text_Area.getText();

                if (mensajeAnterior != null && !mensajeAnterior.isEmpty()) {
                        terminal_Text_Area.setText(mensajeAnterior + "\n" + "> Input: ");
                } else {
                        terminal_Text_Area.setText("> Input: ");
                }
                setupInputListener();
        }

        public void setupInputListener() {
                terminal_Text_Area.addKeyListener(new java.awt.event.KeyAdapter() {
                        @Override
                        public void keyPressed(java.awt.event.KeyEvent evt) {

                                if (bloqueo == false) {
                                        return;
                                }

                                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                                        evt.consume();

                                        String texto = terminal_Text_Area.getText();
                                        int idx = texto.lastIndexOf("> Input:");
                                        if (idx != -1) {
                                                String entrada = texto.substring(idx + 8).trim();
                                                try {
                                                        int valor = Integer.parseInt(entrada);
                                                        if (valor >= 0 && valor <= 255) {
                                                                nucleo.leer_Teclado(valor);
                                                                terminal_Text_Area.append("\nValor capturado: "
                                                                                + valor + "\n");

                                                                bloqueo = false;

                                                                actualizar_Tablas();

                                                        } else {
                                                                terminal_Text_Area.append(
                                                                                "\nError: valor fuera de rango (0-255)\n");
                                                        }
                                                } catch (NumberFormatException e) {
                                                        terminal_Text_Area.append(
                                                                        "\nError: entrada invalida, debe ser numerica\n");
                                                }
                                        }
                                }
                        }
                });
        }

        public static void main(String args[]) {
                try {
                        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                                        .getInstalledLookAndFeels()) {
                                if ("Nimbus".equals(info.getName())) {
                                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                                        break;
                                }
                        }
                } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
                        logger.log(java.util.logging.Level.SEVERE, null, ex);
                }

                java.awt.EventQueue.invokeLater(() -> new Ventana_Principal().setVisible(true));
        }

        private javax.swing.JLabel Almacenamiento_Label;
        private javax.swing.JLabel BCP_Label;
        private javax.swing.JButton Cargar_Archivos_BTN;
        private javax.swing.JButton Ejecutar_BTN;
        private javax.swing.JButton Estadisticas_BTN;
        private javax.swing.JButton Limpiar_BTN;
        private javax.swing.JLabel Lista_Procesos_Label;
        private javax.swing.JLabel Memoria_Label;
        private javax.swing.JButton Paso_A_Paso_BTN;
        private javax.swing.JTable Tabla_Almacenamiento;
        private javax.swing.JTable Tabla_BCP;
        private javax.swing.JTable Tabla_Estadisticas;
        private javax.swing.JTable Tabla_Memoria;
        private javax.swing.JTable Tabla_Procesos;
        private javax.swing.JLabel Terminal_Text_Area_Label;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JScrollPane jScrollPane1;
        private javax.swing.JScrollPane jScrollPane2;
        private javax.swing.JScrollPane jScrollPane3;
        private javax.swing.JScrollPane jScrollPane4;
        private javax.swing.JScrollPane jScrollPane5;
        private javax.swing.JScrollPane jScrollPane6;
        private javax.swing.JTextArea terminal_Text_Area;
}
