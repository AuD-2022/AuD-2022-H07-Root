package h07.h7;

import h07.*;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.lang.reflect.Field;
import java.util.HashMap;

public abstract class AdjacencyMatrixPointerTest {

    @SuppressWarnings("unchecked")
    public static HashMap<Pair<Integer,Integer>, ArcPointerAdjacencyMatrix<Integer,Integer>> getExistingArcPointersMap(ArcPointerAdjacencyMatrix<Integer,Integer> arcPointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = ArcPointerAdjacencyMatrix.class.getDeclaredField("existingArcPointers");
        field.setAccessible(true);
        return (HashMap<Pair<Integer,Integer>, ArcPointerAdjacencyMatrix<Integer,Integer>>) field.get(arcPointer);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<Pair<Integer,Integer>, ArcPointerAdjacencyMatrix<Integer,Integer>> getExistingArcPointersMap(NodePointerAdjacencyMatrix<Integer,Integer> nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerAdjacencyMatrix.class.getDeclaredField("existingArcPointers");
        field.setAccessible(true);
        return (HashMap<Pair<Integer,Integer>, ArcPointerAdjacencyMatrix<Integer,Integer>>) field.get(nodePointer);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> getExistingNodePointersMap(ArcPointerAdjacencyMatrix<Integer,Integer> arcPointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = ArcPointerAdjacencyMatrix.class.getDeclaredField("existingNodePointers");
        field.setAccessible(true);
        return (HashMap<Integer, NodePointerAdjacencyMatrix<Integer,Integer>>) field.get(arcPointer);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<Integer, NodePointerAdjacencyMatrix<Integer,Integer>> getExistingNodePointersMap(NodePointerAdjacencyMatrix<Integer,Integer> nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerAdjacencyMatrix.class.getDeclaredField("existingNodePointers");
        field.setAccessible(true);
        return (HashMap<Integer, NodePointerAdjacencyMatrix<Integer,Integer>>) field.get(nodePointer);
    }

    @SuppressWarnings("unchecked")
    public static AdjacencyMatrix<Integer> getAdjacencyMatrix(ArcPointerAdjacencyMatrix<Integer,Integer> arcPointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = ArcPointerAdjacencyMatrix.class.getDeclaredField("adjacencyMatrix");
        field.setAccessible(true);
        return (AdjacencyMatrix<Integer>) field.get(arcPointer);
    }

    @SuppressWarnings("unchecked")
    public static AdjacencyMatrix<Integer> getAdjacencyMatrix(NodePointerAdjacencyMatrix<Integer,Integer> nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerAdjacencyMatrix.class.getDeclaredField("adjacencyMatrix");
        field.setAccessible(true);
        return (AdjacencyMatrix<Integer>) field.get(nodePointer);
    }

    public static Integer getRow(NodePointerAdjacencyMatrix<Integer,Integer> nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerAdjacencyMatrix.class.getDeclaredField("row");
        field.setAccessible(true);
        return (Integer) field.get(nodePointer);
    }

    public static Integer getRow(ArcPointerAdjacencyMatrix<Integer,Integer> arcPointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = ArcPointerAdjacencyMatrix.class.getDeclaredField("row");
        field.setAccessible(true);
        return (Integer) field.get(arcPointer);
    }

    public static Integer getColumn(ArcPointerAdjacencyMatrix<Integer,Integer> arcPointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = ArcPointerAdjacencyMatrix.class.getDeclaredField("column");
        field.setAccessible(true);
        return (Integer) field.get(arcPointer);
    }

    @SuppressWarnings("unchecked")
    public static NodePointer<Integer, Integer> getPredecessor(NodePointerAdjacencyMatrix<Integer,Integer> nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerAdjacencyMatrix.class.getDeclaredField("predecessor");
        field.setAccessible(true);
        return (NodePointer<Integer, Integer>) field.get(nodePointer);
    }

    public static Integer getDistance(NodePointerAdjacencyMatrix<Integer,Integer> nodePointer) throws IllegalAccessException, NoSuchFieldException {
        Field field = NodePointerAdjacencyMatrix.class.getDeclaredField("distance");
        field.setAccessible(true);
        return (Integer) field.get(nodePointer);
    }

}
