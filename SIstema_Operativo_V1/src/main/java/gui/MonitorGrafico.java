package gui;

import dto.SnapshotSistema;
import model.BCP;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class MonitorGrafico extends JPanel {

    private SnapshotSistema snapshot;
    private static final int BAR_HEIGHT = 22;
    private static final int LABEL_WIDTH = 150;
    private static final int PADDING = 10;
    private static final int ROW_GAP = 4;
    private static final int LEGEND_HEIGHT = 30;

    public MonitorGrafico() {
        setPreferredSize(new Dimension(600, 300));
        setBackground(Color.WHITE);
    }

    public void setSnapshot(SnapshotSistema snap) {
        this.snapshot = snap;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int panelHeight = getHeight();
        int panelWidth = getWidth();

        if (snapshot == null) {
            g2.setColor(Color.GRAY);
            g2.drawString("Sin datos de procesos", PADDING, 30);
            drawLegend(g2, panelWidth, panelHeight);
            return;
        }

        List<ProcesoBar> barras = new ArrayList<>();

        for (BCP bcp : snapshot.todosLosBCP) {
            String nombre = bcp.getNombre_Programa();
            int pid = bcp.getPID();
            int rafagaRestante;
            try { rafagaRestante = Integer.parseInt(bcp.getRafaga_Restante()); } catch (NumberFormatException e) { rafagaRestante = 0; }
            int duracion;
            try { duracion = Integer.parseInt(bcp.getTiempo_Ejecucion()); } catch (NumberFormatException e) { duracion = 0; }
            String estado = bcp.getEstado();
            barras.add(new ProcesoBar(nombre + " (" + pid + ")", rafagaRestante, duracion, estado));
        }

        for (String p : snapshot.pendientes) {
            barras.add(new ProcesoBar(p + " (pendiente)", 0, 0, "Nuevo"));
        }

        if (!barras.isEmpty()) {

            int maxDuracion = 1;
            for (ProcesoBar b : barras) {
                if (b.duracion > maxDuracion) maxDuracion = b.duracion;
            }

            int chartWidth = panelWidth - LABEL_WIDTH - PADDING * 2;
            int chartHeight = panelHeight - LEGEND_HEIGHT - PADDING * 2;
            int startY = PADDING + 5;

            int availableRows = Math.max(1, chartHeight / (BAR_HEIGHT + ROW_GAP));
            int shownRows = Math.min(availableRows, barras.size());

            for (int i = 0; i < shownRows; i++) {
                ProcesoBar bar = barras.get(i);
                int y = startY + i * (BAR_HEIGHT + ROW_GAP);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
                String label = truncate(bar.nombre, LABEL_WIDTH / 7);
                g2.drawString(label, PADDING, y + BAR_HEIGHT - 6);

                int barX = LABEL_WIDTH + PADDING;
                int barW = maxDuracion > 0 ? (int) ((double) bar.rafaga / maxDuracion * chartWidth) : 0;
                if (barW < 1 && bar.rafaga > 0) barW = 1;

                Color color;
                switch (bar.estado) {
                    case "En Ejecucion": color = new Color(76, 175, 80); break;
                    case "Preparado":     color = new Color(33, 150, 243); break;
                    case "Terminado":
                    case "Listo":         color = new Color(158, 158, 158); break;
                    case "Nuevo":         color = new Color(255, 152, 0); break;
                    default:              color = Color.LIGHT_GRAY; break;
                }

                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 200));
                g2.fillRect(barX, y, barW, BAR_HEIGHT);

                g2.setColor(Color.DARK_GRAY);
                g2.drawRect(barX, y, barW, BAR_HEIGHT);

                g2.setFont(new Font("Monospaced", Font.BOLD, 10));
                g2.setColor(Color.BLACK);
                String info = bar.rafaga + "/" + bar.duracion + " " + bar.estado;
                g2.drawString(info, barX + 4, y + BAR_HEIGHT - 6);
            }

            if (shownRows < barras.size()) {
                g2.setColor(Color.GRAY);
                g2.drawString("... +" + (barras.size() - shownRows) + " mas",
                        PADDING, startY + shownRows * (BAR_HEIGHT + ROW_GAP) + BAR_HEIGHT - 6);
            }

            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
            g2.setColor(Color.LIGHT_GRAY);
            int gridX = LABEL_WIDTH + PADDING;
            for (int t = 0; t <= maxDuracion; t += Math.max(1, maxDuracion / 10)) {
                int x = gridX + (int) ((double) t / maxDuracion * chartWidth);
                g2.drawLine(x, startY, x, startY + shownRows * (BAR_HEIGHT + ROW_GAP) - ROW_GAP);
                g2.setStroke(new BasicStroke(1));
                g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
                g2.drawString(String.valueOf(t), x - 6, startY - 2);
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
            }
        }

        drawLegend(g2, panelWidth, panelHeight);
    }

    private void drawLegend(Graphics2D g2, int panelWidth, int panelHeight) {
        int y = panelHeight - LEGEND_HEIGHT + 5;
        int x = PADDING;
        Font legendFont = new Font("SansSerif", Font.PLAIN, 10);
        g2.setFont(legendFont);

        drawLegendItem(g2, x, y, new Color(76, 175, 80), "Ejecucion"); x += 70;
        drawLegendItem(g2, x, y, new Color(33, 150, 243), "Preparado"); x += 70;
        drawLegendItem(g2, x, y, new Color(255, 152, 0), "Nuevo"); x += 70;
        drawLegendItem(g2, x, y, new Color(158, 158, 158), "Terminado"); x += 70;
    }

    private void drawLegendItem(Graphics2D g2, int x, int y, Color color, String label) {
        g2.setColor(color);
        g2.fillRect(x, y, 10, 10);
        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(x, y, 10, 10);
        g2.setColor(Color.BLACK);
        g2.drawString(label, x + 14, y + 10);
    }

    private String truncate(String s, int maxLen) {
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 2) + "..";
    }

    private static class ProcesoBar {
        final String nombre;
        final int rafaga;
        final int duracion;
        final String estado;

        ProcesoBar(String nombre, int rafaga, int duracion, String estado) {
            this.nombre = nombre;
            this.rafaga = rafaga;
            this.duracion = duracion;
            this.estado = estado;
        }
    }
}
