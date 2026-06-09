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

        JPanel legendPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 2));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.add(createLegendLabel(getCpuColor(1), "CPU 1"));
        legendPanel.add(createLegendLabel(getCpuColor(2), "CPU 2"));
        legendPanel.add(createLegendLabel(getCpuColor(3), "CPU 3"));
        legendPanel.add(createLegendLabel(getCpuColor(4), "CPU 4"));
        legendPanel.add(createLegendLabel(new Color(245, 245, 245), "Libre"));
        add(legendPanel, BorderLayout.SOUTH);
    }

    private static Color getCpuColor(int cpuNum) {
        switch (cpuNum) {
            case 1: return new Color(255, 193, 7);
            case 2: return new Color(33, 150, 243);
            case 3: return new Color(244, 67, 54);
            case 4: return new Color(156, 39, 176);
            default: return Color.LIGHT_GRAY;
        }
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
                setBackground(cell.color);
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
            setBorder(javax.swing.BorderFactory.createLineBorder(new Color(210, 210, 210)));
            return c;
        }
    }

    private static class GanttCell {
        final Color color;
        GanttCell(Color color) { this.color = color; }
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

            List<RowData> tempRows = new ArrayList<>();
            int accumTime = 0;

            for (BCP bcp : snap.todosLosBCP) {
                int pid = bcp.getPID();
                int tiempoEje;
                try { tiempoEje = Integer.parseInt(bcp.getTiempo_Ejecucion()); } catch (NumberFormatException e) { tiempoEje = 0; }
                int rafaga;
                try { rafaga = Integer.parseInt(bcp.getRafaga_Restante()); } catch (NumberFormatException e) { rafaga = tiempoEje; }
                Integer cpuNum = pidToCpu.get(pid);
                if (cpuNum == null && bcp.getCPU_Asignada() != null && !bcp.getCPU_Asignada().isEmpty()
                        && !"NONE".equals(bcp.getCPU_Asignada())) {
                    try { cpuNum = Integer.parseInt(bcp.getCPU_Asignada()); } catch (NumberFormatException ignored) {}
                }
                if (cpuNum == null) cpuNum = 0;
                String nucleo = cpuNum != 0 ? String.valueOf(cpuNum) : "-";
                int ejecutados = tiempoEje - rafaga;
                if (ejecutados < 0) ejecutados = 0;
                tempRows.add(new RowData(nucleo, bcp.getNombre_Programa() + " (" + pid + ")",
                        ejecutados, tiempoEje, accumTime, cpuNum));
                accumTime += tiempoEje;
            }

            for (String nombre : snap.pendientes) {
                tempRows.add(new RowData("-", nombre + " (pendiente)", 0, 0, accumTime, 0));
            }

            if (snap.procesosTerminados != null) {
                for (BCP bcp : snap.procesosTerminados) {
                    int pid = bcp.getPID();
                    int tiempoEjeT;
                    try { tiempoEjeT = Integer.parseInt(bcp.getTiempo_Ejecucion()); } catch (NumberFormatException e) { tiempoEjeT = 0; }
                    int cpuNumT = 0;
                    if (bcp.getCPU_Asignada() != null && !bcp.getCPU_Asignada().isEmpty()
                            && !"NONE".equals(bcp.getCPU_Asignada())) {
                        try { cpuNumT = Integer.parseInt(bcp.getCPU_Asignada()); } catch (NumberFormatException ignored) {}
                    }
                    tempRows.add(new RowData("-", bcp.getNombre_Programa() + " (" + pid + ")",
                            tiempoEjeT, tiempoEjeT, accumTime, cpuNumT));
                    accumTime += tiempoEjeT;
                }
            }

            // Natural order: first process at top
            rows.addAll(tempRows);

            maxTime = accumTime;
            columnCount = 2 + maxTime;
            fireTableStructureChanged();
        }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return columnCount; }

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
                return new GanttCell(new Color(245, 245, 245));
            }

            int tickInProcess = t - start + 1;
            boolean executed = tickInProcess <= row.ejecutados;
            if (executed && row.cpuColorIndex > 0) {
                return new GanttCell(getCpuColor(row.cpuColorIndex));
            }
            return new GanttCell(new Color(220, 220, 220));
        }
    }

    private static class RowData {
        final String nucleo;
        final String proceso;
        final int ejecutados;
        final int duracion;
        final int startTime;
        final int cpuColorIndex;

        RowData(String nucleo, String proceso, int ejecutados, int duracion, int startTime, int cpuColorIndex) {
            this.nucleo = nucleo;
            this.proceso = proceso;
            this.ejecutados = ejecutados;
            this.duracion = duracion;
            this.startTime = startTime;
            this.cpuColorIndex = cpuColorIndex;
        }
    }
}
