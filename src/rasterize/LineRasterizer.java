package rasterize;

import model.Line;
import raster.RasterBufferedImage;

public abstract class LineRasterizer {
    protected RasterBufferedImage raster;

    public LineRasterizer(RasterBufferedImage raster) {
        this.raster = raster;
    }

    public void rasterize(Line line, int color1, int color2, boolean interpolate) {

    }

    public void rasterize(int x1, int y1, int x2, int y2, int color1, int color2, boolean interpolate) {

    }
}
