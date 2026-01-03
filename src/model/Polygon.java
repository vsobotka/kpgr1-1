package model;

import java.util.ArrayList;

public class Polygon {
    private final ArrayList<Point> points;

    public Polygon() {
        this.points = new ArrayList<>();
    }

    public void addPoint(Point p) {
        this.points.add(p);
    }

    public void replaceLastPoint(Point p) {
        this.points.set(points.size() - 1, p);
    }

    public int getSize() {
        return points.size();
    }

    public Point getPoint(int index) {
        return points.get(index);
    }

    public void setPoint(Point p, int index) {
        points.set(index, p);
    }

    public void insertPoint(Point p, int index) {
        points.add(index, p);
    }
}
