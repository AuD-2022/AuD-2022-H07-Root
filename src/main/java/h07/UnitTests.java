package h07;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class UnitTests {

    /**
     * Mindestens 4 Testfälle zur Heap Priority Queue.
     */
    @Test
    public void priorityQueueHeap() {
        PriorityQueueHeap<Integer> queue = new PriorityQueueHeap<Integer>(Comparator.naturalOrder(), 3);
        Object[] heap = queue.getInternalHeap();
        queue.add(7);
        Assertions.assertArrayEquals(new Integer[]{7, null, null}, heap);
        queue.add(6);
        Assertions.assertArrayEquals(new Integer[]{7, 6, null}, heap);
        queue.delete(7);
        Assertions.assertArrayEquals(new Integer[]{6, null, null}, heap);
        queue.add(9);
        Assertions.assertArrayEquals(new Integer[]{9, 6, null}, heap);
        queue.add(8);
        Assertions.assertArrayEquals(new Integer[]{9, 6, 8}, heap);
    }

    /**
     * Mindestens 4 Testfälle zur AdjacencyMatrix und zugehörigen Pointerklassen.
     */
    @Test
    public void adjacencyMatrix() {
        Double[][] matrix = {
            {1d, 0.5d, null},
            {null, null, 1.5d},
            {1d, null, null},
        };
        AdjacencyMatrix<Double> adjacencyMatrix = new AdjacencyMatrix<Double>(matrix);

        NodePointerAdjacencyMatrix<Double, Double> nodePointer = new NodePointerAdjacencyMatrix<>(new HashMap<>(), new HashMap<>(), adjacencyMatrix, 0);
        nodePointer.setDistance(42d);
        Iterator<ArcPointer<Double, Double>> arcPointerIterator1 = nodePointer.outgoingArcs();

        ArcPointer<Double, Double> arcNode1 = arcPointerIterator1.next();
        Assertions.assertEquals(1d, arcNode1.getLength());
        Assertions.assertEquals(42d, arcNode1.destination().getDistance());

        ArcPointer<Double, Double> arcNode2 = arcPointerIterator1.next();
        Assertions.assertEquals(0.5d, arcNode2.getLength());

        Assertions.assertFalse(arcPointerIterator1.hasNext());
    }

    /**
     * Mindestens 4 Testfälle zur Dijkstra.
     */
    @Test
    public void dijkstra() {
        Double[][] matrix = {
            {1d, 0.5d, null},
            {null, null, 1.5d},
            {1d, null, null},
        };
        AdjacencyMatrix<Double> adjacencyMatrix = new AdjacencyMatrix<Double>(matrix);
        NodePointerAdjacencyMatrix<Double, Double> nodePointer = new NodePointerAdjacencyMatrix<>(new HashMap<>(), new HashMap<>(), adjacencyMatrix, 0);
        nodePointer.setDistance(0d);

        Dijkstra<Double, Double> dijkstra = new Dijkstra<Double, Double>((a, b) -> -Double.compare(a,b),
            Double::sum, nodePointerComparator -> new PriorityQueueHeap<>(nodePointerComparator, 100));

        dijkstra.initialize(nodePointer);
        List<NodePointer<Double, Double>> nodes = dijkstra.run();
        Assertions.assertEquals(2, nodes.size());
        Assertions.assertEquals(0.5, nodes.get(0).getDistance());
        Assertions.assertEquals(2, nodes.get(1).getDistance());
        Assertions.assertEquals(nodePointer, nodes.get(0).getPredecessor());
        Assertions.assertEquals(nodes.get(0), nodes.get(1).getPredecessor());
    }
}
