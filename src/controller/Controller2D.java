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
    private boolean snap = false;
    private boolean interpolate = false;

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
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    colorIndex = (colorIndex + 1) % colors.length;
                    color = colors[colorIndex];
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    snap = !snap;
                } else if (e.getKeyCode() == KeyEvent.VK_I) {
                    interpolate = !interpolate;
                }

                drawScene();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (polygon.getSize() == 0) {
                    Point currentPoint = new Point(e.getX(), e.getY(), color, interpolate);
                    polygon.addPoint(currentPoint);
                    polygon.addPoint(currentPoint);
                } else {
                    Point previousPoint = polygon.getPoint(polygon.getSize() - 1);
                    polygon.addPoint(createPointWithAdjustedPosition(previousPoint.getX(), previousPoint.getY(), e.getX(), e.getY()));
                }

                drawScene();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (snap) {
                    Point previousPoint = polygon.getPoint(polygon.getSize() - 2);
                    polygon.replaceLastPoint(createPointWithAdjustedPosition(previousPoint.getX(), previousPoint.getY(), e.getX(), e.getY()));
                } else {
                    polygon.replaceLastPoint(new Point(e.getX(), e.getY(), color, interpolate));
                }

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
        g.drawString("[C] Clear", 10, 40);

        g.setColor(new Color(color));
        g.drawString("[R] Color", 10, 60);
        g.setColor(Color.WHITE);

        if (snap) {
            g.setColor(new Color(colors[0]));
            g.drawString("[SHIFT] Snap ON", 10, 80);
            g.setColor(Color.WHITE);
        } else {
            g.drawString("[SHIFT] Snap OFF", 10, 80);
        }

        if (interpolate) {
            g.setColor(new Color(colors[0]));
            g.drawString("[I] Interpolate ON", 10, 100);
            g.setColor(Color.WHITE);
        } else {
            g.drawString("[I] Interpolate OFF", 10, 100);
        }

        g.dispose();
    }

    private Point createPointWithAdjustedPosition(int prevX, int prevY, int x, int y) {
        if (!snap) {
            return new Point(x, y, color, interpolate);
        }

        int dX = Math.abs(x - prevX);
        int dY = Math.abs(y - prevY);

        if (dX > dY * 2) {
            // dx is significantly higher than dy, horizontal
            return new Point(x, prevY, color, interpolate);
        } else if (dY > dX * 2) {
            // dy is significantly higher than dx, horizontal
            return new Point(prevX, y, color, interpolate);
        } else {
            // diagonal, insignificant difference
            int distance = Math.max(dX, dY);
            int signX = x > prevX ? 1 : -1;
            int signY = y > prevY ? 1 : -1;
            return new Point(prevX + signX * distance, prevY + signY * distance, color, interpolate);
        }
    }
}
