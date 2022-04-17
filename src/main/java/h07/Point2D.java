package h07;

import java.util.Objects;

public class Point2D {
    private final double x;
    private final double y;

    /**
     * Erzeugt einen 2D-Punkt bestehende aus x und y Wert.
     * @param x Der x Wert.
     * @param y Der y Wert.
     */
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gibt den x Wert des Punkts zurück.
     * @return Der x Wert des Punkts.
     */
    public double getX() {
        return x;
    }

    /**
     * Gibt den y Wert des Punkts zurück.
     * @return Der y Wert des Punkts.
     */
    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point2D point2D = (Point2D) o;
        return Double.compare(point2D.getX(), x) == 0 && Double.compare(point2D.getY(), y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
