package h07;

import java.util.HashMap;

public class ArcPointerPoint2D implements ArcPointer<Double, Double> {
    private final HashMap<Point2D, NodePointerPoint2D> existingNodePointers;
    private final HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers;
    private final Point2DCollection collection;
    private final Point2D source;
    private final Point2D destination;

    /**
     * Erzeugt einen Pointer auf eine Kante, für eine gegebene Punktsammlung.
     * @param existingNodePointers Die bereits bestehenden NodePointer.
     * @param existingArcPointers Die bereits bestehenden ArcPointer.
     * @param source Die Quelle der Kante.
     * @param destination Das Ziel der Kante.
     * @param collection Die Punktsammlung.
     */
    public ArcPointerPoint2D(HashMap<Point2D, NodePointerPoint2D> existingNodePointers,
                             HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers,
                             Point2D source, Point2D destination, Point2DCollection collection) {
        this.existingNodePointers = existingNodePointers;
        this.existingArcPointers = existingArcPointers;
        this.source = source;
        this.destination = destination;
        this.collection = collection;

        existingArcPointers.put(new Pair<>(source, destination), this);
    }

    @Override
    public Double getLength() {
        return Math.sqrt(Math.pow(destination.getX() - source.getX(), 2)
            + Math.pow(destination.getY() - source.getY(), 2));
    }

    @Override
    public NodePointer<Double, Double> destination() {
        if (existingNodePointers.containsKey(destination)) {
            return existingNodePointers.get(destination);
        }
        return new NodePointerPoint2D(existingNodePointers, existingArcPointers, destination, collection);
    }
}
