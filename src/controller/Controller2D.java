package controller;

import model.Point;
import model.Polygon;
import rasterize.FilledLineRasterizer;
import rasterize.LineRasterizer;
import rasterize.PolygonRasterizer;
import view.Panel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller2D {
    private final Panel panel;
    private final int[] colors = {
            0x00FFFF,  // Cyan
            0xFF6600,  // Vivid Orange
            0xFF00FF,  // Magenta
            0xCCFF00,  // Electric Lime
            0xFF1493   // Deep Pink
    };

    private Polygon polygon;
    private int colorIndex = 0;
    private int color = colors[colorIndex];

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.polygon = new Polygon();

        initListeners();
        drawScene();
    }

    private void initListeners() {
        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    polygon = new Polygon();
                    drawScene();
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    colorIndex = (colorIndex + 1) % colors.length;
                    color = colors[colorIndex];
                    drawScene();
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point currentPoint = new Point(e.getX(), e.getY(), color);
                if (polygon.getSize() == 0) {
                    polygon.addPoint(currentPoint);
                    polygon.addPoint(currentPoint);
                } else {
                    polygon.addPoint(currentPoint);
                }

                drawScene();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                polygon.replaceLastPoint(new Point(e.getX(), e.getY(), color));

                drawScene();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                polygon.replaceLastPoint(new Point(e.getX(), e.getY(), color));

                drawScene();
            }
        });
    }

    private void drawScene() {
        panel.getRaster().clear();

        LineRasterizer lineRasterizer = new FilledLineRasterizer(panel.getRaster());
        PolygonRasterizer polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        polygonRasterizer.rasterize(polygon);
        renderUI();

        panel.repaint();
    }

    private void renderUI() {
        Graphics g = panel.getRaster().getImage().getGraphics();
        g.setColor(Color.WHITE);

        g.drawString("Polygon size: " + polygon.getSize(), 10, 20);
        g.drawString("[C] Clear screen", 10, 40);

        g.setColor(new Color(color));
        g.drawString("[R] Change color", 10, 60);
        g.setColor(Color.WHITE);

        g.dispose();
    }
}
