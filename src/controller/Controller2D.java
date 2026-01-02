package controller;

import model.Point;
import model.Polygon;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerBresenham;
import rasterize.PolygonRasterizer;
import view.Panel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Controller2D {
    private final Panel panel;
    private Polygon polygon;

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

        LineRasterizer lineRasterizer = new LineRasterizerBresenham(panel.getRaster());
        PolygonRasterizer polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        polygonRasterizer.rasterize(polygon);

        panel.repaint();
    }
}
