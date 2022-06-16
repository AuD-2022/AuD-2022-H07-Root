package h07.h6;

import h07.*;

import java.lang.reflect.Field;
import java.util.HashMap;


public abstract class GraphPointerTest {


    @SuppressWarnings("unchecked")
    public static HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> getExistingArcPointersMap(ArcPointerGraph<Integer, Integer> arcPointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = ArcPointerGraph.class.getDeclaredField("existingArcPointers");
        field.setAccessible(true);
        return (HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>>) field.get(arcPointer);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> getExistingArcPointersMap(NodePointerGraph<Integer, Integer> nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerGraph.class.getDeclaredField("existingArcPointers");
        field.setAccessible(true);
        return (HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>>) field.get(nodePointer);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> getExistingNodePointersMap(ArcPointerGraph<Integer, Integer> arcPointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = ArcPointerGraph.class.getDeclaredField("existingNodePointers");
        field.setAccessible(true);
        return (HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>>) field.get(arcPointer);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> getExistingNodePointersMap(NodePointerGraph<Integer, Integer> nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerGraph.class.getDeclaredField("existingNodePointers");
        field.setAccessible(true);
        return (HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>>) field.get(nodePointer);
    }

    @SuppressWarnings("unchecked")
    public static GraphArc<Integer> getGraphArc(ArcPointerGraph<Integer, Integer> arcPointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = ArcPointerGraph.class.getDeclaredField("graphArc");
        field.setAccessible(true);
        return (GraphArc<Integer>) field.get(arcPointer);
    }

    @SuppressWarnings("unchecked")
    public static GraphNode<Integer> getGraphNode(NodePointerGraph<Integer, Integer> nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerGraph.class.getDeclaredField("graphNode");
        field.setAccessible(true);
        return (GraphNode<Integer>) field.get(nodePointer);
    }

    public static Integer getDistance(NodePointerGraph<Integer, Integer> nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerGraph.class.getDeclaredField("distance");
        field.setAccessible(true);
        return (Integer) field.get(nodePointer);
    }

    @SuppressWarnings("unchecked")
    public static NodePointer<Integer, Integer> getPredecessor(NodePointerGraph<Integer, Integer> nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerGraph.class.getDeclaredField("predecessor");
        field.setAccessible(true);
        return (NodePointer<Integer, Integer>) field.get(nodePointer);
    }
}
