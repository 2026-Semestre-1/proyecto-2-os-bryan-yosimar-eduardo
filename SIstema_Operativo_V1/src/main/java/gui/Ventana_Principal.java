package gui;

import kernel.NucleoSO;
import model.Almacenamiento;
import model.BCP;
import model.Memoria;
import model.MemoriaPaginada;
import dto.SnapshotSistema;
import Memoria.Modelo.TablaDePagina;
import Memoria.Modelo.Frame;
import Memoria.Modelo.Particion;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
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

        private JComboBox<String> Selector_Memoria;
        private javax.swing.JLabel Selector_Memoria_Label;
        private javax.swing.JComboBox<String> Selector_Algoritmo;
        private javax.swing.JLabel Selector_Algoritmo_Label;
        private javax.swing.JSpinner Selector_Quantum;
        private javax.swing.JLabel Quantum_Label;
        private javax.swing.JLabel Frames_Label;
        private javax.swing.JLabel Tabla_Paginas_Label;
        private javax.swing.JTable Tabla_Frames;
        private javax.swing.JTable Tabla_Tablas_Paginas;
        private JTabbedPane Seccion_Inferior_Tab;

        public Ventana_Principal() {
                initComponents();
                nucleo = new NucleoSO();
                nucleo.configurarMemoria(modeloGestionMemoria);
                nucleo.configurarAlgoritmoPlanificacion("FCFS");
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
                                                { "Tiempo Finalizacion:", null },
                                                { "Duracion Estimada:", null },
                                                { "Quantum Restante:", null },
                                                { "Tiempo Espera:", null },
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
                                                "PID", "Inicio", "Finalizacion", "Diferencia"
                                }));
                jScrollPane6.setViewportView(Tabla_Estadisticas);

                jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36));
                jLabel1.setText("Sistema Operativo V1");

                Selector_Memoria_Label = new javax.swing.JLabel();
                Selector_Memoria_Label.setText("Gestion de memoria:");

                Selector_Memoria = new JComboBox<>(new String[] { "Normal", "Paginacion", "ParticionIgual",
                                "ParticionIgualDinamica", "Dinamica" });
                Selector_Memoria.setSelectedItem(modeloGestionMemoria);
                Selector_Memoria.addActionListener(evt -> {
                        String seleccion = (String) Selector_Memoria.getSelectedItem();
                        if (seleccion != null && !seleccion.equals(modeloGestionMemoria)) {
                                modeloGestionMemoria = seleccion;
                                nucleo.configurarMemoria(modeloGestionMemoria);
                                iniciar_Contenido_Base_tablas();
                                actualizar_Tablas();
                        }
                });

                Selector_Algoritmo_Label = new javax.swing.JLabel();
                Selector_Algoritmo_Label.setText("Algoritmo:");

                Selector_Algoritmo = new JComboBox<>(new String[] { "FCFS", "SJF", "RR", "SRR", "SRT", "HRRN", "Lottery" });
                Selector_Algoritmo.setSelectedItem("FCFS");

                Quantum_Label = new javax.swing.JLabel();
                Quantum_Label.setText("Quantum:");

                javax.swing.SpinnerNumberModel spinnerModel = new javax.swing.SpinnerNumberModel(3, 1, 99, 1);
                Selector_Quantum = new javax.swing.JSpinner(spinnerModel);

                Quantum_Label.setVisible(false);
                Selector_Quantum.setVisible(false);

                Selector_Algoritmo.addActionListener(evt -> {
                        String seleccion = (String) Selector_Algoritmo.getSelectedItem();
                        if (seleccion != null) {
                                nucleo.configurarAlgoritmoPlanificacion(seleccion);
                                boolean usaQuantum = seleccion.equals("RR") || seleccion.equals("SRR");
                                Quantum_Label.setVisible(usaQuantum);
                                Selector_Quantum.setVisible(usaQuantum);
                                if (usaQuantum) {
                                        int qValue = (int) Selector_Quantum.getValue();
                                        nucleo.configurarQuantum(qValue);
                                }
                                this.revalidate();
                                this.repaint();
                        }
                });

                Selector_Quantum.addChangeListener(evt -> {
                        int qValue = (int) Selector_Quantum.getValue();
                        nucleo.configurarQuantum(qValue);
                });

                Tabla_Frames = new javax.swing.JTable();
                Tabla_Frames.setModel(new DefaultTableModel(
                                new Object[][] {},
                                new String[] { "Frame", "Estado", "Proceso", "Rango Direcciones" }));
                Tabla_Frames.getColumnModel().getColumn(3).setPreferredWidth(120);
                jScrollPane7 = new javax.swing.JScrollPane(Tabla_Frames);

                Tabla_Tablas_Paginas = new javax.swing.JTable();
                Tabla_Tablas_Paginas.setModel(new DefaultTableModel(
                                new Object[][] {},
                                new String[] { "Proceso", "Pagina", "Frame" }));
                jScrollPane8 = new javax.swing.JScrollPane(Tabla_Tablas_Paginas);

                Frames_Label = new javax.swing.JLabel("Frames");
                Tabla_Paginas_Label = new javax.swing.JLabel("Tabla de paginas");

                javax.swing.JPanel panelFrames = new javax.swing.JPanel(new java.awt.BorderLayout());
                panelFrames.add(Frames_Label, java.awt.BorderLayout.NORTH);
                panelFrames.add(jScrollPane7, java.awt.BorderLayout.CENTER);

                javax.swing.JPanel panelPaginas = new javax.swing.JPanel(new java.awt.BorderLayout());
                panelPaginas.add(Tabla_Paginas_Label, java.awt.BorderLayout.NORTH);
                panelPaginas.add(jScrollPane8, java.awt.BorderLayout.CENTER);

                contenido_Text_Area = new javax.swing.JTextArea();
                contenido_Text_Area.setEditable(false);
                contenido_Text_Area.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
                contenido_Text_Area.setRows(8);
                jScrollPane9 = new javax.swing.JScrollPane(contenido_Text_Area);
                Contenido_Label = new javax.swing.JLabel("Contenido de pagina");

                javax.swing.JPanel panelContenido = new javax.swing.JPanel(new java.awt.BorderLayout());
                panelContenido.add(Contenido_Label, java.awt.BorderLayout.NORTH);
                panelContenido.add(jScrollPane9, java.awt.BorderLayout.CENTER);

                javax.swing.JPanel panelPaginacion = new javax.swing.JPanel(new java.awt.GridLayout(1, 3, 12, 0));
                panelPaginacion.add(panelFrames);
                panelPaginacion.add(panelPaginas);
                panelPaginacion.add(panelContenido);

                Tabla_Particiones = new javax.swing.JTable();
                Tabla_Particiones.setModel(new DefaultTableModel(
                                new Object[][] {},
                                new String[] { "Particion", "Inicio", "Fin", "Tamano", "Estado", "Proceso" }));
                jScrollPane10 = new javax.swing.JScrollPane(Tabla_Particiones);
                Particiones_Label = new javax.swing.JLabel("Particiones fijas");

                javax.swing.JPanel panelParticiones = new javax.swing.JPanel(new java.awt.BorderLayout());
                panelParticiones.add(Particiones_Label, java.awt.BorderLayout.NORTH);
                panelParticiones.add(jScrollPane10, java.awt.BorderLayout.CENTER);

                Seccion_Inferior_Tab = new JTabbedPane();
                Seccion_Inferior_Tab.addTab("Estadisticas", jScrollPane6);
                Seccion_Inferior_Tab.addTab("Paginacion", panelPaginacion);
                Seccion_Inferior_Tab.addTab("Particiones", panelParticiones);

                Tabla_Frames.getSelectionModel().addListSelectionListener(e -> {
                        if (!e.getValueIsAdjusting()) {
                                int row = Tabla_Frames.getSelectedRow();
                                if (row >= 0 && nucleo.getMemoriaPaginada() != null) {
                                        int numFrame = (int) Tabla_Frames.getModel().getValueAt(row, 0);
                                        MemoriaPaginada mp = nucleo.getMemoriaPaginada();
                                        Frame[] framesMp = mp.getFrames();
                                        if (framesMp != null && numFrame >= 0 && numFrame < framesMp.length) {
                                                Frame f = framesMp[numFrame];
                                                if (f != null && f.getPagina() != null) {
                                                        contenido_Text_Area.setText(formatearContenido(
                                                                        f.getPagina().getContenido()));
                                                } else {
                                                        contenido_Text_Area.setText("");
                                                }
                                        }
                                }
                        }
                });

                Tabla_Tablas_Paginas.getSelectionModel().addListSelectionListener(e -> {
                        if (!e.getValueIsAdjusting()) {
                                int row = Tabla_Tablas_Paginas.getSelectedRow();
                                if (row >= 0 && nucleo.getMemoriaPaginada() != null) {
                                        String proceso = (String) Tabla_Tablas_Paginas.getModel().getValueAt(row, 0);
                                        int numPagina = Integer.parseInt(
                                                        Tabla_Tablas_Paginas.getModel().getValueAt(row, 1).toString());
                                        MemoriaPaginada mp = nucleo.getMemoriaPaginada();
                                        for (TablaDePagina tp : mp.getTablaDePaginas()) {
                                                if (tp.getNombreProceso().equals(proceso)
                                                                && tp.getNumeroDePagina() == numPagina) {
                                                        int numFrame = tp.getNumeroDeFrame();
                                                        Frame[] framesMp = mp.getFrames();
                                                        if (framesMp != null && numFrame >= 0
                                                                        && numFrame < framesMp.length) {
                                                                Frame f = framesMp[numFrame];
                                                                if (f != null && f.getPagina() != null) {
                                                                        contenido_Text_Area.setText(formatearContenido(
                                                                                        f.getPagina().getContenido()));
                                                                } else {
                                                                        contenido_Text_Area.setText("");
                                                                }
                                                        } else if (tp.getDireccionDisco() != -1
                                                                        && mp.getMemoriaSecundaria() != null) {
                                                                List<String> lineas = new ArrayList<>();
                                                                for (int k = 0; k < mp.getPageSize(); k++) {
                                                                        String inst = mp.getMemoriaSecundaria().get(
                                                                                        tp.getDireccionDisco() + k);
                                                                        if (inst != null && !inst.isEmpty()) {
                                                                                lineas.add(inst);
                                                                        }
                                                                }
                                                                contenido_Text_Area.setText(formatearContenido(lineas));
                                                        } else {
                                                                contenido_Text_Area.setText("");
                                                        }
                                                        break;
                                                }
                                        }
                                }
                        }
                });

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
                                                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                    .addComponent(Cargar_Archivos_BTN)
                                                                                                    .addGap(18, 18, 18)
                                                                                                    .addComponent(Selector_Memoria_Label)
                                                                                                    .addGap(5, 5, 5)
                                                                                                    .addComponent(Selector_Memoria,
                                                                                                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                    120,
                                                                                                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                    .addGap(25, 25, 25)
                                                                                                    .addComponent(Selector_Algoritmo_Label)
                                                                                                    .addGap(5, 5, 5)
                                                                                                    .addComponent(Selector_Algoritmo,
                                                                                                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                    100,
                                                                                                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                    .addGap(25, 25, 25)
                                                                                                    .addComponent(Quantum_Label)
                                                                                                    .addGap(5, 5, 5)
                                                                                                    .addComponent(Selector_Quantum,
                                                                                                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                    50,
                                                                                                                    javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                                                                                                                                .addComponent(Seccion_Inferior_Tab,
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
                                                                .addGroup(layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(Cargar_Archivos_BTN)
                                                                                .addComponent(Selector_Memoria_Label)
                                                                                .addComponent(Selector_Memoria,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(Selector_Algoritmo_Label)
                                                                                .addComponent(Selector_Algoritmo,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(Quantum_Label)
                                                                                .addComponent(Selector_Quantum,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                                                .addComponent(Seccion_Inferior_Tab,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                300,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(80, Short.MAX_VALUE)));

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
                        this.actualizar_Tablas();
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
                actualizar_Tabla_Frames(snap.memoriaPaginada);
                actualizar_Tabla_Paginas(snap.memoriaPaginada);
                actualizar_Tabla_Particiones(snap.particiones);
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
                        limpiar_tabla_BCP();
                        return;
                }

                modeloTabla.setValueAt(datos_BCP.getPID(), 0, 1); // PID
                modeloTabla.setValueAt(datos_BCP.getEstado(), 1, 1); // estado
                modeloTabla.setValueAt(datos_BCP.getPrioridad(), 2, 1); // prioridad
                modeloTabla.setValueAt(datos_BCP.getMem_Init(), 3, 1); // MEM_PTR
                modeloTabla.setValueAt(datos_BCP.getMem_End(), 4, 1); // LIMIT
                modeloTabla.setValueAt(datos_BCP.getPC(), 5, 1); // PC
                modeloTabla.setValueAt(datos_BCP.getIR(), 6, 1); // IR

                modeloTabla.setValueAt(datos_BCP.getAC(), 7, 1); // AC
                modeloTabla.setValueAt(datos_BCP.getAX(), 8, 1); // AX
                modeloTabla.setValueAt(datos_BCP.getBX(), 9, 1); // BX
                modeloTabla.setValueAt(datos_BCP.getCX(), 10, 1); // CX
                modeloTabla.setValueAt(datos_BCP.getDX(), 11, 1); // DX
                modeloTabla.setValueAt(datos_BCP.getAH(), 12, 1); // AH
                modeloTabla.setValueAt(datos_BCP.getAL(), 13, 1); // AL

                modeloTabla.setValueAt(datos_BCP.getIO_STATUS(), 14, 1); // Lista Archivos
                modeloTabla.setValueAt(datos_BCP.getCPU_Asignada(), 15, 1); // CPU_TIME
                modeloTabla.setValueAt(datos_BCP.getTiempo_Llegada(), 16, 1); // Tiempo_Llegada
                modeloTabla.setValueAt(datos_BCP.getTiempo_Inicio(), 17, 1); // Tiempo_Inicio
                modeloTabla.setValueAt(datos_BCP.getTiempo_Finalizacion(), 18, 1); // Tiempo_Finalizacion
                modeloTabla.setValueAt(datos_BCP.getRafaga_Restante(), 19, 1); // Rafaga_Restante
                modeloTabla.setValueAt(datos_BCP.getRafaga_Restante(), 20, 1); // Quantum_Restante
                modeloTabla.setValueAt(datos_BCP.getTiempo_Espera(), 21, 1); // Tiempo_Espera
                modeloTabla.setValueAt(datos_BCP.getProximo_Proceso(), 22, 1); // Proximo_Proceso
                int[] pila = datos_BCP.getPila();
                for (int i = 0; i < pila.length; i++) {
                        modeloTabla.setValueAt(pila[i], i + 23, 1); // Pila
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

        public void actualizar_Tabla_Frames(MemoriaPaginada memoriaPaginada) {
                DefaultTableModel modeloTabla = (DefaultTableModel) Tabla_Frames.getModel();
                modeloTabla.setRowCount(0);
                if (memoriaPaginada == null)
                        return;
                Frame[] frames = memoriaPaginada.getFrames();
                if (frames == null)
                        return;
                for (int i = 0; i < frames.length; i++) {
                        Frame f = frames[i];
                        if (f != null) {
                                boolean libre = memoriaPaginada.isFrameLibre(i);
                                String estado = libre ? "Libre" : "Ocupado";
                                String proceso = "";
                                if (!libre) {
                                        for (TablaDePagina tp : memoriaPaginada.getTablaDePaginas()) {
                                                if (tp.getNumeroDeFrame() == f.getNumFrame()) {
                                                        proceso = tp.getNombreProceso();
                                                        break;
                                                }
                                        }
                                }
                                String rango = f.getDireccionBase() + " - "
                                                + (f.getDireccionBase() + memoriaPaginada.getPageSize() - 1);
                                modeloTabla.addRow(new Object[] { f.getNumFrame(), estado, proceso, rango });
                        } else {
                                modeloTabla.addRow(new Object[] { i, "Libre", "", "" });
                        }
                }
        }

        public void actualizar_Tabla_Paginas(MemoriaPaginada memoriaPaginada) {
                DefaultTableModel modeloTabla = (DefaultTableModel) Tabla_Tablas_Paginas.getModel();
                modeloTabla.setRowCount(0);
                if (memoriaPaginada == null)
                        return;
                List<TablaDePagina> paginas = memoriaPaginada.getTablaDePaginas();
                if (paginas == null)
                        return;
                for (TablaDePagina tp : paginas) {
                        modeloTabla.addRow(new Object[] {
                                        tp.getNombreProceso(),
                                        tp.getNumeroDePagina(),
                                        tp.getNumeroDeFrame()
                        });
                }
        }

        public void actualizar_Tabla_Particiones(List<Particion> particiones) {
                DefaultTableModel modeloTabla = (DefaultTableModel) Tabla_Particiones.getModel();
                modeloTabla.setRowCount(0);
                if (particiones == null)
                        return;
                for (Particion p : particiones) {
                        String estado = (p.procesoAsignado == -1) ? "Libre" : "Ocupado";
                        String proceso = (p.procesoAsignado == -1) ? "" : String.valueOf(p.procesoAsignado);
                        modeloTabla.addRow(new Object[] {
                                        p.id, p.inicio, p.fin, p.tamano, estado, proceso
                        });
                }
        }

        private String formatearContenido(List<String> instrucciones) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < instrucciones.size(); i++) {
                        sb.append(i).append(": ").append(instrucciones.get(i)).append(System.lineSeparator());
                }
                return sb.toString();
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
        private javax.swing.JScrollPane jScrollPane7;
        private javax.swing.JScrollPane jScrollPane8;
        private javax.swing.JScrollPane jScrollPane9;
        private javax.swing.JScrollPane jScrollPane10;
        private javax.swing.JTable Tabla_Particiones;
        private javax.swing.JLabel Particiones_Label;
        private javax.swing.JTextArea terminal_Text_Area;
        private javax.swing.JTextArea contenido_Text_Area;
        private javax.swing.JLabel Contenido_Label;
}
