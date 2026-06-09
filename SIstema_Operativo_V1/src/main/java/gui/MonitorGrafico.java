package gui;

import dto.SnapshotSistema;
import model.BCP;
import model.CPU;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorGrafico extends JPanel {

    private GanttTableModel tableModel;
    private JTable table;

    public MonitorGrafico() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        tableModel = new GanttTableModel(null);
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new GanttCellRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        for (int c = 2; c < table.getColumnCount(); c++) {
            table.getColumnModel().getColumn(c).setPreferredWidth(30);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        add(scrollPane, BorderLayout.CENTER);

        JPanel legendPanel = new JPanel();
        legendPanel.setBackground(Color.WHITE);
        legendPanel.add(createLegendLabel(new Color(76, 175, 80), "Ejecucion"));
        legendPanel.add(createLegendLabel(new Color(33, 150, 243), "Preparado"));
        legendPanel.add(createLegendLabel(new Color(255, 152, 0), "Nuevo"));
        legendPanel.add(createLegendLabel(new Color(158, 158, 158), "Terminado"));
        add(legendPanel, BorderLayout.SOUTH);
    }

    private JPanel createLegendLabel(Color color, String text) {
        JPanel panel = new JPanel(new BorderLayout(4, 0));
        panel.setBackground(Color.WHITE);
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(14, 14));
        panel.add(colorBox, BorderLayout.WEST);
        javax.swing.JLabel label = new javax.swing.JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    public void setSnapshot(SnapshotSistema snap) {
        tableModel.setSnapshot(snap);
        for (int c = 2; c < table.getColumnCount(); c++) {
            if (table.getColumnModel().getColumn(c) != null) {
                table.getColumnModel().getColumn(c).setPreferredWidth(30);
            }
        }
        if (table.getColumnCount() > 0)
            table.getColumnModel().getColumn(0).setPreferredWidth(50);
        if (table.getColumnCount() > 1)
            table.getColumnModel().getColumn(1).setPreferredWidth(130);
    }

    private static class GanttCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof GanttCell) {
                GanttCell cell = (GanttCell) value;
                setText("");
                if (cell.executed) {
                    setBackground(cell.color);
                } else {
                    setBackground(new Color(230, 230, 230));
                }
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
            setBorder(javax.swing.BorderFactory.createLineBorder(new Color(210, 210, 210)));
            return c;
        }
    }

    private static class GanttCell {
        final boolean executed;
        final Color color;
        GanttCell(boolean executed, Color color) {
            this.executed = executed;
            this.color = color;
        }
    }

    private static class GanttTableModel extends AbstractTableModel {
        private List<RowData> rows = new ArrayList<>();
        private int maxTime = 0;
        private int columnCount = 0;

        GanttTableModel(SnapshotSistema snap) {
            setSnapshot(snap);
        }

        void setSnapshot(SnapshotSistema snap) {
            rows.clear();
            maxTime = 1;
            if (snap == null) {
                columnCount = 2;
                fireTableStructureChanged();
                return;
            }

            Map<Integer, Integer> pidToCpu = new HashMap<>();
            if (snap.estadoCPUs != null) {
                for (CPU cpu : snap.estadoCPUs) {
                    int pid = cpu.getPID_Proceso_Actual();
                    if (pid != 0) pidToCpu.put(pid, cpu.getNumero_CPU());
                }
            }

            int accumTime = 0;

            for (BCP bcp : snap.todosLosBCP) {
                String nombre = bcp.getNombre_Programa();
                int pid = bcp.getPID();
                int tiempoEje;
                try { tiempoEje = Integer.parseInt(bcp.getTiempo_Ejecucion()); } catch (NumberFormatException e) { tiempoEje = 0; }
                int rafaga;
                try { rafaga = Integer.parseInt(bcp.getRafaga_Restante()); } catch (NumberFormatException e) { rafaga = tiempoEje; }
                String estado = bcp.getEstado();
                Integer cpuNum = pidToCpu.get(pid);
                String nucleo = (cpuNum != null && "En Ejecucion".equals(estado)) ? String.valueOf(cpuNum) : "-";
                int ejecutados = tiempoEje - rafaga;
                if (ejecutados < 0) ejecutados = 0;
                rows.add(new RowData(nucleo, nombre + " (" + pid + ")", ejecutados, tiempoEje, accumTime, estado));
                accumTime += tiempoEje;
            }

            for (String nombre : snap.pendientes) {
                rows.add(new RowData("-", nombre + " (pendiente)", 0, 0, accumTime, "Nuevo"));
            }

            if (snap.procesosTerminados != null) {
                for (BCP bcp : snap.procesosTerminados) {
                    String nombre = bcp.getNombre_Programa();
                    int pid = bcp.getPID();
                    int tiempoEjeT;
                    try { tiempoEjeT = Integer.parseInt(bcp.getTiempo_Ejecucion()); } catch (NumberFormatException e) { tiempoEjeT = 0; }
                    String estado = bcp.getEstado();
                    rows.add(new RowData("-", nombre + " (" + pid + ")", tiempoEjeT, tiempoEjeT, accumTime, estado));
                    accumTime += tiempoEjeT;
                }
            }

            maxTime = accumTime;
            columnCount = 2 + maxTime;
            fireTableStructureChanged();
        }

        @Override
        public int getRowCount() { return rows.size(); }

        @Override
        public int getColumnCount() { return columnCount; }

        @Override
        public String getColumnName(int column) {
            if (column == 0) return "Nucleo";
            if (column == 1) return "Proceso";
            return String.valueOf(column - 1);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            RowData row = rows.get(rowIndex);
            if (columnIndex == 0) return row.nucleo;
            if (columnIndex == 1) return row.proceso;
            int t = columnIndex - 1;
            int start = row.startTime + 1;
            int end = start + row.duracion - 1;

            if (t < start || t > end || row.duracion == 0) {
                return new GanttCell(false, Color.LIGHT_GRAY);
            }

            int tickInProcess = t - start + 1;
            boolean executed = tickInProcess <= row.ejecutados;
            Color color;
            switch (row.estado) {
                case "En Ejecucion": color = new Color(76, 175, 80); break;
                case "Preparado":     color = new Color(33, 150, 243); break;
                case "Terminado":
                case "Listo":         color = new Color(158, 158, 158); break;
                case "Nuevo":         color = new Color(255, 152, 0); break;
                default:              color = Color.LIGHT_GRAY; break;
            }
            return new GanttCell(executed, color);
        }
    }

    private static class RowData {
        final String nucleo;
        final String proceso;
        final int ejecutados;
        final int duracion;
        final int startTime;
        final String estado;

        RowData(String nucleo, String proceso, int ejecutados, int duracion, int startTime, String estado) {
            this.nucleo = nucleo;
            this.proceso = proceso;
            this.ejecutados = ejecutados;
            this.duracion = duracion;
            this.startTime = startTime;
            this.estado = estado;
        }
    }
}
