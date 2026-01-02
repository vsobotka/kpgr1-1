package rasterize;

import model.Point;
import model.Polygon;

public class PolygonRasterizer {
    private final LineRasterizer lineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void rasterize(Polygon polygon) {
        if (polygon.getSize() < 3) return;

        for (int i = 0; i < polygon.getSize(); i++) {
            Point a = polygon.getPoint(i);
            Point b = i + 1 == polygon.getSize() ? polygon.getPoint(0) : polygon.getPoint(i + 1);

            lineRasterizer.rasterize(a.getX(), a.getY(), b.getX(), b.getY());
        }
    }
}
