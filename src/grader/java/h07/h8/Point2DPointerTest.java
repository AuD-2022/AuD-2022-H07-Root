package h07.h8;

import h07.*;

import java.lang.reflect.Field;
import java.util.HashMap;

public abstract class Point2DPointerTest {

    @SuppressWarnings("unchecked")
    public static HashMap<Pair<Point2D,Point2D>, ArcPointerPoint2D> getExistingArcPointersMap(ArcPointerPoint2D arcPointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = ArcPointerPoint2D.class.getDeclaredField("existingArcPointers");
        field.setAccessible(true);
        return (HashMap<Pair<Point2D,Point2D>, ArcPointerPoint2D>) field.get(arcPointer);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<Pair<Point2D,Point2D>, ArcPointerPoint2D> getExistingArcPointersMap(NodePointerPoint2D nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerPoint2D.class.getDeclaredField("existingArcPointers");
        field.setAccessible(true);
        return (HashMap<Pair<Point2D,Point2D>, ArcPointerPoint2D>) field.get(nodePointer);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<Point2D, NodePointerPoint2D> getExistingNodePointersMap(ArcPointerPoint2D arcPointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = ArcPointerPoint2D.class.getDeclaredField("existingNodePointers");
        field.setAccessible(true);
        return (HashMap<Point2D, NodePointerPoint2D>) field.get(arcPointer);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<Point2D, NodePointerPoint2D> getExistingNodePointersMap(NodePointerPoint2D nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerPoint2D.class.getDeclaredField("existingNodePointers");
        field.setAccessible(true);
        return (HashMap<Point2D, NodePointerPoint2D>) field.get(nodePointer);
    }

    public static Point2DCollection getCollection(ArcPointerPoint2D arcPointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = ArcPointerPoint2D.class.getDeclaredField("collection");
        field.setAccessible(true);
        return (Point2DCollection) field.get(arcPointer);
    }

    public static Point2DCollection getCollection(NodePointerPoint2D nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerPoint2D.class.getDeclaredField("collection");
        field.setAccessible(true);
        return (Point2DCollection) field.get(nodePointer);
    }

    public static Point2D getPoint(NodePointerPoint2D nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerPoint2D.class.getDeclaredField("point");
        field.setAccessible(true);
        return (Point2D) field.get(nodePointer);
    }

    public static Point2D getSource(ArcPointerPoint2D arcPointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = ArcPointerPoint2D.class.getDeclaredField("source");
        field.setAccessible(true);
        return (Point2D) field.get(arcPointer);
    }

    public static Point2D getDestination(ArcPointerPoint2D arcPointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = ArcPointerPoint2D.class.getDeclaredField("destination");
        field.setAccessible(true);
        return (Point2D) field.get(arcPointer);
    }

    @SuppressWarnings("unchecked")
    public static NodePointer<Double, Double> getPredecessor(NodePointerPoint2D nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerPoint2D.class.getDeclaredField("predecessor");
        field.setAccessible(true);
        return (NodePointer<Double, Double>) field.get(nodePointer);
    }

    public static Double getDistance(NodePointerPoint2D nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerPoint2D.class.getDeclaredField("distance");
        field.setAccessible(true);
        return (Double) field.get(nodePointer);
    }

}
