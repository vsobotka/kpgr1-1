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
    private boolean editMode = false;
    private int highlightedPointIndex = -1;
    private Point previewPoint = null;
    private int previewLineIndex = -1;

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
                    clearPreviewPoint();
                    clearHighlightedPoint();
                    polygon = new Polygon();
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    colorIndex = (colorIndex + 1) % colors.length;
                    color = colors[colorIndex];
                    if (editMode && highlightedPointIndex >= 0) {
                        Point oldPoint = polygon.getPoint(highlightedPointIndex);
                        polygon.setPoint(new Point(oldPoint.getX(), oldPoint.getY(), color, oldPoint.isInterpolate()), highlightedPointIndex);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    if (editMode) return;

                    snap = !snap;
                } else if (e.getKeyCode() == KeyEvent.VK_I) {
                    interpolate = !interpolate;
                    if (editMode && highlightedPointIndex >= 0) {
                        Point oldPoint = polygon.getPoint(highlightedPointIndex);
                        polygon.setPoint(new Point(oldPoint.getX(), oldPoint.getY(), oldPoint.getColor(), interpolate), highlightedPointIndex);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_E) {
                    editMode = !editMode;
                }

                drawScene();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (editMode) {
                    if (previewPoint != null) {
                        polygon.insertPoint(previewPoint, previewLineIndex + 1);
                        clearHighlightedPoint();
                        clearPreviewPoint();
                        checkPointProximity(e.getX(), e.getY());
                        drawScene();
                    }

                    return;
                }

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
                if (editMode) {
                    if (highlightedPointIndex >= 0) {
                        Point previousPoint = polygon.getPoint(highlightedPointIndex);
                        polygon.setPoint(new Point(e.getX(), e.getY(), previousPoint.getColor(), previousPoint.isInterpolate()), highlightedPointIndex);
                    }

                    drawScene();
                    return;
                }

                if (snap) {
                    Point previousPoint = polygon.getPoint(polygon.getSize() - 2);
                    polygon.replaceLastPoint(createPointWithAdjustedPosition(previousPoint.getX(), previousPoint.getY(), e.getX(), e.getY()));
                } else {
                    polygon.replaceLastPoint(new Point(e.getX(), e.getY(), color, interpolate));
                }

                drawScene();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (!editMode) return;

                if (checkPointProximity(e.getX(), e.getY())) return;

                clearPreviewPoint();

                for (int i = 0; i < polygon.getSize(); i++) {
                    Point p1 = polygon.getPoint(i);
                    Point p2 = polygon.getPoint(i + 1 == polygon.getSize() ? 0 : i + 1);

                    int steps = Math.max(Math.abs(p2.getX() - p1.getX()), Math.abs(p2.getY() - p1.getY()));

                    for (int step = 0; step <= steps; step++) {
                        int px = p1.getX() + (p2.getX() - p1.getX()) * step / steps;
                        int py = p1.getY() + (p2.getY() - p1.getY()) * step / steps;

                        if (Math.abs(px - e.getX()) < 10 && Math.abs(py - e.getY()) < 10) {
                            previewPoint = new Point(px, py, color, interpolate);
                            previewLineIndex = i;
                            clearHighlightedPoint();
                            break;
                        }
                    }

                    if (previewPoint != null) break;
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

        if (interpolate) {
            g.setColor(new Color(colors[0]));
            g.drawString("[I] Interpolate ON", 10, 80);
            g.setColor(Color.WHITE);
        } else {
            g.drawString("[I] Interpolate OFF", 10, 80);
        }

        if (editMode) {
            g.setColor(new Color(colors[0]));
            g.drawString("[E] Edit mode ON", 10, 100);
            g.setColor(Color.WHITE);
        } else {
            g.drawString("[E] Edit mode OFF", 10, 100);
        }

        if (!editMode) {
            if (snap) {
                g.setColor(new Color(colors[0]));
                g.drawString("[SHIFT] Snap ON", 10, 120);
                g.setColor(Color.WHITE);
            } else {
                g.drawString("[SHIFT] Snap OFF", 10, 120);
            }
        }

        if (editMode && highlightedPointIndex >= 0) {
            g.setColor(Color.RED);
            g.drawRect(polygon.getPoint(highlightedPointIndex).getX() - 5, polygon.getPoint(highlightedPointIndex).getY() - 5, 10, 10);
        }

        if (editMode && previewPoint != null) {
            g.setColor(Color.YELLOW);
            g.fillOval(previewPoint.getX() - 5, previewPoint.getY() - 5, 10, 10);
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
            // dx is significantly higher than dy, vertical
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

    private boolean checkPointProximity(int x, int y) {
        for (int i = 0; i < polygon.getSize(); i++) {
            Point point = polygon.getPoint(i);
            int dx = Math.abs(point.getX() - x);
            int dy = Math.abs(point.getY() - y);

            if (dx < 10 && dy < 10) {
                highlightedPointIndex = i;
                interpolate = point.isInterpolate();
                color = point.getColor();
                clearPreviewPoint();
                drawScene();
                return true;
            }
        }

        return false;
    }

    private void clearPreviewPoint() {
        previewPoint = null;
        previewLineIndex = -1;
    }

    private void clearHighlightedPoint() {
        highlightedPointIndex = -1;
    }
}
