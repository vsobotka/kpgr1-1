package rasterize;

import model.Line;
import raster.RasterBufferedImage;

// Raterizes line using Bresenham's algorithm
public class FilledLineRasterizer extends LineRasterizer {
    public FilledLineRasterizer(RasterBufferedImage raster) {
        super(raster);
    }

    @Override
    public void rasterize(Line line, int color) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2(), color);
    }


    @Override
    public void rasterize(int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int err = dx - dy;
        int x = x1;
        int y = y1;

        while (true) {
            raster.setPixel(x, y, color);

            if (x == x2 && y == y2) {
                break;
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }

            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }
}
