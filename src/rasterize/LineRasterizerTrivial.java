package rasterize;

import model.Line;
import raster.RasterBufferedImage;

public class LineRasterizerTrivial extends LineRasterizer {
    public LineRasterizerTrivial(RasterBufferedImage raster) {
        super(raster);
    }

    @Override
    public void rasterize(Line line) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }


    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {
        // y = kx + q
        float k = (float) (y2 - y1) / (x2 - x1);
    }
}
