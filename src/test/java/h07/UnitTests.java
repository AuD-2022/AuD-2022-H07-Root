package h07;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class UnitTests {

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

        queue.delete(8);
        Assertions.assertArrayEquals(new Integer[]{9, 6, null}, heap);
        Assertions.assertNull(queue.delete(10));

        queue.add(8);
        Assertions.assertArrayEquals(new Integer[]{9, 6, 8}, heap);
        Assertions.assertEquals(1, queue.getPosition(9));
        Assertions.assertEquals(3, queue.getPosition(6));
        Assertions.assertEquals(2, queue.getPosition(8));
        Assertions.assertEquals(-1, queue.getPosition(10));

        queue.delete(6);
        Assertions.assertArrayEquals(new Integer[]{9, 8, null}, heap);

        Assertions.assertEquals(9, queue.deleteFront());
        Assertions.assertArrayEquals(new Integer[]{8, null, null}, heap);
        Assertions.assertEquals(8, queue.getFront());

        queue.deleteFront();
        Assertions.assertNull(queue.deleteFront());
        Assertions.assertNull(queue.delete(10));
        Assertions.assertArrayEquals(new Integer[]{null, null, null}, heap);
    }

    @Test
    public void priorityQueueList() {
        PriorityQueueList<Integer> queue = new PriorityQueueList<Integer>(Comparator.naturalOrder());
        List<Integer> list = queue.getInternalList();

        queue.add(7);
        Assertions.assertArrayEquals(new Integer[]{7}, list.toArray());

        queue.add(6);
        Assertions.assertArrayEquals(new Integer[]{7, 6}, list.toArray());

        queue.delete(7);
        Assertions.assertArrayEquals(new Integer[]{6}, list.toArray());

        queue.add(9);
        Assertions.assertArrayEquals(new Integer[]{9, 6}, list.toArray());

        queue.add(8);
        Assertions.assertArrayEquals(new Integer[]{9, 8, 6}, list.toArray());

        queue.delete(8);
        Assertions.assertArrayEquals(new Integer[]{9, 6}, list.toArray());
        Assertions.assertNull(queue.delete(10));

        queue.add(8);
        Assertions.assertArrayEquals(new Integer[]{9, 8, 6}, list.toArray());
        Assertions.assertEquals(1, queue.getPosition(9));
        Assertions.assertEquals(2, queue.getPosition(8));
        Assertions.assertEquals(3, queue.getPosition(6));
        Assertions.assertEquals(-1, queue.getPosition(10));

        queue.delete(6);
        Assertions.assertArrayEquals(new Integer[]{9, 8}, list.toArray());

        Assertions.assertEquals(9, queue.deleteFront());
        Assertions.assertArrayEquals(new Integer[]{8}, list.toArray());
        Assertions.assertEquals(8, queue.getFront());

        queue.deleteFront();
        Assertions.assertNull(queue.deleteFront());
        Assertions.assertNull(queue.delete(10));
        Assertions.assertArrayEquals(new Integer[]{}, list.toArray());
    }

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

    @Test
    public void graph() {
        Double[][] matrix = {
            {1d, 0.5d, null},
            {null, null, 1.5d},
            {1d, null, null},
        };
        AdjacencyMatrix<Double> adjacencyMatrix = new AdjacencyMatrix<Double>(matrix);
        Graph<Double> graph = new Graph<Double>(adjacencyMatrix);

        NodePointerGraph<Double, Double> nodePointer = new NodePointerGraph<Double, Double>(new HashMap<>(), new HashMap<>(), graph.getNodes().get(0));
        nodePointer.setDistance(42d);
        Iterator<ArcPointer<Double, Double>> arcPointerIterator1 = nodePointer.outgoingArcs();

        ArcPointer<Double, Double> arcNode1 = arcPointerIterator1.next();
        Assertions.assertEquals(1d, arcNode1.getLength());
        Assertions.assertEquals(42d, arcNode1.destination().getDistance());

        ArcPointer<Double, Double> arcNode2 = arcPointerIterator1.next();
        Assertions.assertEquals(0.5d, arcNode2.getLength());

        Assertions.assertFalse(arcPointerIterator1.hasNext());
    }

    @Test
    public void pointCollection() {
        Point2DCollection collection = new Point2DCollection(100, new Point2D(0, 50), new Point2D(50, 100), 10);
        Assertions.assertEquals(10, collection.getMaxArcLength());
        Assertions.assertEquals(100, collection.getPoints().size());
        for (int i = 0; i < 100; i++) {
            double x = collection.getPoints().get(i).getX();
            double y = collection.getPoints().get(i).getY();
            Assertions.assertTrue(x >= 0 && x < 50);
            Assertions.assertTrue(y >= 50 && y < 100);
        }
    }

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

    @Test
    public void dijkstra2() {
        Point2DCollection collection = new Point2DCollection(100, new Point2D(0, 50), new Point2D(50, 100), 10);
        NodePointerPoint2D nodePointer = new NodePointerPoint2D(new HashMap<>(), new HashMap<>(), collection.getPoints().get(0), collection);
        nodePointer.setDistance(0.0);

        Dijkstra<Double, Double> dijkstra = new Dijkstra<Double, Double>((a, b) -> -Double.compare(a,b),
            Double::sum, nodePointerComparator -> new PriorityQueueHeap<>(nodePointerComparator, 100));

        dijkstra.initialize(nodePointer);
        List<NodePointer<Double, Double>> nodes = dijkstra.run();
    }

    @Test
    public void pathfinder() {
        Point2DCollection collection = new Point2DCollection(100, new Point2D(0, 50), new Point2D(50, 100), 10);
        NodePointerPoint2D nodePointer = new NodePointerPoint2D(new HashMap<>(), new HashMap<>(), collection.getPoints().get(0), collection);
        nodePointer.setDistance(0.0);

        Dijkstra<Double, Double> dijkstra = new Dijkstra<Double, Double>((a, b) -> -Double.compare(a,b),
            Double::sum, nodePointerComparator -> new PriorityQueueHeap<>(nodePointerComparator, 100));

        dijkstra.initialize(nodePointer);
        List<NodePointer<Double, Double>> nodes = dijkstra.run();

        PathFinder<Double, Double> finder = new PathFinder<>();
        List<NodePointer<Double, Double>> path = finder.apply(nodes.get(nodes.size()-1));

        System.out.println("Hurra");
    }
}
