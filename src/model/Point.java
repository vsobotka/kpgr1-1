package model;

public class Point {
    private final int x, y, color;
    private final boolean interpolate;

    public Point(int x, int y, int color, boolean interpolate) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.interpolate = interpolate;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    public boolean isInterpolate() {
        return interpolate;
    }
}
