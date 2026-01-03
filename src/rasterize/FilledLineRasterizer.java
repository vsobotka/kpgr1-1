package rasterize;

import model.Line;
import raster.RasterBufferedImage;

import java.awt.*;

/* Raterizes line using Bresenham's algorithm

    Pros:
    + fast and efficient
    + integer arithmetic only

    Cons:
    - only straight lines
    - produces aliased lines

 */
public class FilledLineRasterizer extends LineRasterizer {
    public FilledLineRasterizer(RasterBufferedImage raster) {
        super(raster);
    }

    @Override
    public void rasterize(Line line, int color1, int color2, boolean interpolate) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2(), color1, color2, interpolate);
    }


    @Override
    public void rasterize(int x1, int y1, int x2, int y2, int color1, int color2, boolean interpolate) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int err = dx - dy;
        int x = x1;
        int y = y1;

        while (true) {
            int color = interpolate ? getInterpolatedColor(x1, y1, x2, y2, x, y, color1, color2) : color2;
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

    private int getInterpolatedColor(int x1, int y1, int x2, int y2, int x, int y, int color1, int color2) {
        int dx = x - x1;
        int dy = y - y1;
        float distanceFromStart = (float) Math.sqrt(dx * dx + dy * dy);

        int totalDx = x2 - x1;
        int totalDy = y2 - y1;
        float totalDistance = (float) Math.sqrt(totalDx * totalDx + totalDy * totalDy);

        if (totalDistance == 0) {
            return color1;
        }

        float t = distanceFromStart / totalDistance;

        Color c1 = new Color(color1);
        Color c2 = new Color(color2);

        int r = (int) (c1.getRed() + t * (c2.getRed() - c1.getRed()));
        int g = (int) (c1.getGreen() + t * (c2.getGreen() - c1.getGreen()));
        int b = (int) (c1.getBlue() + t * (c2.getBlue() - c1.getBlue()));

        return new Color(r, g, b).getRGB();
    }
}
