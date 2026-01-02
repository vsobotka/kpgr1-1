package controller;

import model.Line;
import model.Point;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.PolygonRasterizer;
import view.Panel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller2D {
    private final Panel panel;

    private Polygon polygon;
    private Line line;

    public Controller2D(Panel panel) {
        this.panel = panel;
        this.polygon = new Polygon();

        initListeners();
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
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                polygon.addPoint(new Point(e.getX(), e.getY()));

                drawScene();
            }
        });
    }

    private void drawScene() {
        panel.getRaster().clear();

        LineRasterizer lineRasterizer = new LineRasterizerGraphics(panel.getRaster());
        PolygonRasterizer polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        polygonRasterizer.rasterize(polygon);

        panel.repaint();
    }
}
