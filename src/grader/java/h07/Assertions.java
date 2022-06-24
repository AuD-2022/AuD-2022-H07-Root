package h07;

import h07.implementation.NodePointerImpl;
import h07.implementation.QueueEntry;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static h07.h3.PriorityQueueHeapTest.*;
import static h07.implementation.QueueEntry.CMP;
import static org.junit.jupiter.api.Assertions.*;
import static h07.h8.Point2DPointerTest.*;
import static h07.h7.AdjacencyMatrixPointerTest.*;
import static h07.h6.GraphPointerTest.*;

public class Assertions {

    public static void assertPositionCorrect(List<QueueEntry> entries, QueueEntry entry, int actual) {
        int lowerIndex = (int) entries.stream().filter(j -> CMP.compare(j, entry) > 0 ).count() + 1;
        int upperIndex = (int) (entries.size() - entries.stream().filter(j -> CMP.compare(j, entry) < 0).count());
        assertBetween(lowerIndex, upperIndex, actual,
            "incorrect value for position of %s."
                .formatted(Objects.toString(entry)), lowerIndex == actual + 1);
    }

    public static void assertPositionCorrect(QueueEntry[] entries, QueueEntry entry, int actual, int size) {
        int lowerIndex = (int) Arrays.stream(entries).limit(size).filter(j -> CMP.compare(j, entry) > 0 ).count() + 1;
        int upperIndex = (int) (entries.length - Arrays.stream(entries).limit(size).filter(j -> CMP.compare(j, entry) < 0).count());
        assertBetween(lowerIndex, upperIndex, actual,
            "incorrect value for position of %s."
                .formatted(Objects.toString(entry)), lowerIndex == actual + 1);
    }

    public static void assertPriorityListEquals(List<QueueEntry> expected, List<QueueEntry> actual) {
        assertEquals(expected.size(), actual.size(), "the amount of elements in the queue is not correct");
        assertTrue(expected.containsAll(actual), "the queue does not contain the expected elements");
        assertCorrectOrder(actual);
    }

    public static void assertCorrectOrder(List<QueueEntry> list) {
        if (list.size() < 2) return;

        for (int i = 1; i < list.size(); i++) {
            assertTrue(CMP.compare(list.get(i - 1), list.get(i)) >= 0, "the order of the elements in the queue is not correct");
        }
    }

    public static void assertIndexMapCorrect(QueueEntry[] heap, Map<QueueEntry, Integer> indexMap, int size) {

        assertEquals(indexMap.entrySet().size(), size, "the amount of entries in the indexMap do not match the amount of elements in the queue");

        for (int i = 0; i < size; i++) {
            assertIndexMapCorrect(heap, indexMap, heap[i]);
        }
    }

    public static void assertIndexMapCorrect(QueueEntry[] heap, Map<QueueEntry, Integer> indexMap, QueueEntry key) {

        assertTrue(indexMap.containsKey(key), "the indexMap does not have the entry %s as a key".formatted(key.toString()));
        assertBetween(0, heap.length -1, indexMap.get(key),
            "invalid value for entry %s in IndexMap."
                .formatted(Objects.toString(key)), false);
        assertEquals(key, heap[indexMap.get(key)],
            "the heap array does not contain the expected element at the index specified in the indexMap. Expected %s, actual: %s"
                .formatted(Objects.toString(key), Objects.toString(heap[indexMap.get(key)])));
    }

    public static void assertHeapCorrect(QueueEntry[] heap, List<QueueEntry> elements) {
        QueueEntry[] actualHeap = new QueueEntry[elements.size()];
        System.arraycopy(heap, 0, actualHeap, 0, elements.size());

        for (int i = 0; i < actualHeap.length; i++) {
            assertNotNull(actualHeap[i], "unexpected null element at position %d".formatted(i));
        }

        assertIsPermutation(elements, Arrays.stream(actualHeap).collect(Collectors.toList()), "heap array does not contain the correct elements");

        assertHeapProperty(actualHeap, 0);
    }

    public static void assertHeapProperty(QueueEntry[] heap, int index) {
        if (index >= heap.length) return;

        QueueEntry parent = heap[index];
        int leftChildIndex = index * 2 + 1;
        int rightChildIndex = index * 2 + 2;

        assertHeapPropertyChild(heap, index, parent, leftChildIndex);

        assertHeapPropertyChild(heap, index, parent, rightChildIndex);
    }

    public static void assertHeapPropertyChild(QueueEntry[] heap, int index, QueueEntry parent, int ChildIndex) {
        if (ChildIndex < heap.length) {
            QueueEntry leftChild = heap[ChildIndex];

            if (leftChild != null){
                assertTrue(CMP.compare(parent, leftChild) >= 0,
                    "heap array does not satisfy heap property for parent heap[%d] = %d and left child heap[%d] = %d"
                        .formatted(index, parent.value, ChildIndex, leftChild.value));
                assertHeapProperty(heap, ChildIndex);
            }
        }
    }

    public static void assertQueueFactoryComparatorCorrect(Comparator<NodePointer<Integer, Integer>> actual) {
        assertEquals(0, actual.compare(new NodePointerImpl(1), new NodePointerImpl(1)), "The QueueFactory wasn't invoked with a correct Comparator. The Comparator returned an incorrect value for two nodes with same distance");
        assertTrue(actual.compare(new NodePointerImpl(1), new NodePointerImpl(2)) > 0, "The QueueFactory wasn't invoked with a correct Comparator. The Comparator returned an incorrect value when the distance to the first node is shorter than the distance to the second node. Expected a value greater than 0, actual: %d".formatted(actual.compare(new NodePointerImpl(1), new NodePointerImpl(2))));
        assertTrue(actual.compare(new NodePointerImpl(2), new NodePointerImpl(1)) < 0, "The QueueFactory wasn't invoked with a correct Comparator. The Comparator returned an incorrect value when the distance to the first node is longer than the distance to the second node. Expected a value less than 0, actual: %d".formatted(actual.compare(new NodePointerImpl(1), new NodePointerImpl(2))));
    }


    public static void assertBetween(int min, int max, int actual, String message, boolean note) {
        assertTrue(min <= actual && max >= actual,
            message + " Expected value between %d and %d, actual %d".formatted(min, max, actual) + (note ? ". Note that method getPosition() is supposed to start counting from 1 " : ""));
    }

    public static void assertBetween(double min, double max, double actual, String message) {
        assertTrue(min <= actual && max >= actual,
            message + " Expected value between %f and %f, actual %f".formatted(min, max, actual));
    }

    public static <T> void assertArrayContains(T[] array, T element, String message) {
        List<T> list = Arrays.stream(array).toList();
        assertTrue(list.contains(element), message);
    }

    public static <T> void assertArrayDoesNotContains(T[] array, T element, String message) {
        List<T> list = Arrays.stream(array).toList();
        assertFalse(list.contains(element), message);
    }

    public static void assertPriorityQueueCorrect(List<QueueEntry> expectedEntries, PriorityQueueHeap<QueueEntry> actualQueue) throws NoSuchFieldException, IllegalAccessException {
        assertEquals(expectedEntries.size(), getSize(actualQueue), "value of size attribute not correct. Expected %d, actual %d"
                .formatted(expectedEntries.size(), getSize(actualQueue)));
        assertHeapCorrect(getHeap(actualQueue), expectedEntries);
        assertIndexMapCorrect(getHeap(actualQueue), getIndexMap(actualQueue), getSize(actualQueue));
    }

    public static void assertIsPermutation(List<QueueEntry> expected, List<QueueEntry> actual, String message) {
        actual = new LinkedList<>(actual);
        for (QueueEntry i : expected) {
            assertTrue(actual.remove(i), message);
        }
    }

    public static void assertArcPointerGraphEquals(
        HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> expectedExistingNodePointers,
        HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> expectedExistingArcPointers,
        GraphArc<Integer> expectedGraphArc,
        ArcPointerGraph<Integer, Integer> actualArcPointer) throws NoSuchFieldException, IllegalAccessException {

        assertEquals(expectedExistingArcPointers, getExistingArcPointersMap(actualArcPointer), "the existingArcPointersMap does not contain the correct entries");
        assertEquals(expectedExistingNodePointers, getExistingNodePointersMap(actualArcPointer), "the existingNodePointersMap does not contain the correct entries");
        assertNotNull(getGraphArc(actualArcPointer));
        assertEquals(expectedGraphArc.getDestination(), getGraphArc(actualArcPointer).getDestination(), "the graphArc attribute does not contain the correct destination");
        assertEquals(expectedGraphArc.getLength(), getGraphArc(actualArcPointer).getLength(), "the graphArc attribute does not contain the correct length");
    }

    public static void assertNodePointerGraphEquals(
        HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> expectedExistingNodePointers,
        HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> expectedExistingArcPointers,
        GraphNode<Integer> expectedGraphNode,
        Integer expectedDistance,
        NodePointer<Integer, Integer> expectedPredecessor,
        NodePointerGraph<Integer, Integer> actualNodePointer) throws NoSuchFieldException, IllegalAccessException {

        assertEquals(expectedExistingArcPointers, getExistingArcPointersMap(actualNodePointer), "the existingArcPointersMap does not contain the correct entries");
        assertEquals(expectedExistingNodePointers, getExistingNodePointersMap(actualNodePointer), "the existingNodePointersMap does not contain the correct entries");
        assertEquals(expectedGraphNode, getGraphNode(actualNodePointer), "the graphNode attribute does not contain the correct graphNode");
        assertEquals(expectedDistance, getDistance(actualNodePointer), "the distance attribute does not contain the correct distance");
        assertEquals(expectedPredecessor, getPredecessor(actualNodePointer), "the predecessor attribute does not contain the correct predecessor");
    }

    public static void assertArcPointerAdjacencyMatrixEquals(
        AdjacencyMatrix<Integer> expectedAdjacencyMatrix,
        HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> expectedExistingNodePointers,
        HashMap<Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> expectedExistingArcPointers,
        Integer expectedRow,
        Integer expectedColumn,
        ArcPointerAdjacencyMatrix<Integer, Integer> actualArc) throws NoSuchFieldException, IllegalAccessException {

        assertEquals(expectedAdjacencyMatrix, getAdjacencyMatrix(actualArc), "the adjacencyMatrix attribute does not contain the correct adjacencyMatrix");
        assertEquals(expectedExistingArcPointers, getExistingArcPointersMap(actualArc), "the existingArcPointersMap does not contain the correct entries");
        assertEquals(expectedExistingNodePointers, getExistingNodePointersMap(actualArc), "the existingNodePointersMap does not contain the correct entries");
        assertEquals(expectedRow, getRow(actualArc), "the row attribute does not contain the correct value");
        assertEquals(expectedColumn, getColumn(actualArc), "the column attribute does not contain the correct value");
    }

    public static void assertNodePointerAdjacencyMatrixEquals(
        HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> expectedExistingNodePointers,
        HashMap<Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> expectedExistingArcPointers,
        AdjacencyMatrix<Integer> expectedAdjacencyMatrix,
        Integer expectedDistance,
        NodePointer<Integer, Integer> expectedPredecessor,
        Integer expectedRow,
        NodePointerAdjacencyMatrix<Integer, Integer> actualNodePointer) throws NoSuchFieldException, IllegalAccessException {

        assertEquals(expectedAdjacencyMatrix, getAdjacencyMatrix(actualNodePointer), "the adjacencyMatrix attribute does not contain the correct adjacencyMatrix");
        assertEquals(expectedExistingArcPointers, getExistingArcPointersMap(actualNodePointer), "the existingArcPointersMap does not contain the correct entries");
        assertEquals(expectedExistingNodePointers, getExistingNodePointersMap(actualNodePointer), "the existingNodePointersMap does not contain the correct entries");
        assertEquals(expectedRow, getRow(actualNodePointer), "the row attribute does not contain the correct value");
        assertEquals(expectedDistance, getDistance(actualNodePointer), "the distance attribute does not contain the correct distance");
        assertEquals(expectedPredecessor, getPredecessor(actualNodePointer), "the predecessor attribute does not contain the correct predecessor");
    }

    public static void assertArcPointerPoint2DEquals(
        HashMap<Point2D, NodePointerPoint2D> expectedExistingNodePointers,
        HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> expectedExistingArcPointers,
        Point2D expectedSource,
        Point2D expectedDestination,
        Point2DCollection expectedCollection,
        ArcPointerPoint2D actualArcPointer
    ) throws NoSuchFieldException, IllegalAccessException {
        assertEquals(expectedExistingArcPointers, getExistingArcPointersMap(actualArcPointer), "the existingArcPointersMap does not contain the correct entries");
        assertEquals(expectedExistingNodePointers, getExistingNodePointersMap(actualArcPointer), "the existingNodePointersMap does not contain the correct entries");
        assertEquals(expectedSource, getSource(actualArcPointer), "the source attribute does not contain the correct value");
        assertEquals(expectedDestination, getDestination(actualArcPointer), "the destination attribute does not contain the correct distance");
        assertEquals(expectedCollection, getCollection(actualArcPointer), "the collection attribute does not contain the correct predecessor");
    }

    public static void assertNodePointerPoint2DEquals(
        HashMap<Point2D, NodePointerPoint2D> expectedExistingNodePointers,
        HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> expectedExistingArcPointers,
        Point2D expectedPoint,
        Point2DCollection expectedCollection,
        NodePointer<Double, Double> expectedPredecessor,
        Double expectedDistance,
        NodePointerPoint2D actualNodePointer
    ) throws NoSuchFieldException, IllegalAccessException {
        assertEquals(expectedExistingArcPointers, getExistingArcPointersMap(actualNodePointer), "the existingArcPointersMap does not contain the correct entries");
        assertEquals(expectedExistingNodePointers, getExistingNodePointersMap(actualNodePointer), "the existingNodePointersMap does not contain the correct entries");
        assertEquals(expectedPoint, getPoint(actualNodePointer), "the point attribute does not contain the correct value");
        assertEquals(expectedCollection, getCollection(actualNodePointer), "the collection attribute does not contain the correct value");
        assertEquals(expectedDistance, getDistance(actualNodePointer), "the distance attribute does not contain the correct value");
        assertEquals(expectedPredecessor, getPredecessor(actualNodePointer), "the predecessor attribute does not contain the correct value");
    }

    public static void assertAdjacencyMatrixEquals(Object[][] expected, Object[][] actual) {
        assertEquals(expected.length, actual.length, "incorrect length for outer array of the adjacencyMatrix");

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].length, actual[i].length,
                "incorrect length for inner array at index %d of the adjacencyMatrix".formatted(i));
        }

        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected.length; j++) {
                assertEquals(expected[i][j], actual[i][j],
                    "incorrect value in the adjacencyMatrix at index [%d,%d]".formatted(i,j));
            }
        }
    }

    public static void assertGraphEquals(Graph<Integer> expected, Graph<Integer> actual) {

        assertEquals(expected.getNodes().size(), actual.getNodes().size(),
            "incorrect amount of nodes");

        for (int i = 0; i < expected.getNodes().size(); i++) {
            List<GraphArc<Integer>> expectedOutgoingArcs = expected.getNodes().get(i).getOutgoingArcs();
            Collection<GraphArc<Integer>> actualOutgoingArcs = new ConcurrentLinkedDeque<>(actual.getNodes().get(i).getOutgoingArcs());

            assertEquals(expectedOutgoingArcs.size(), actualOutgoingArcs.size(),
                "incorrect amount of outgoing arcs for node %d".formatted(i));

            outerLoop: for (GraphArc<Integer> expectedOutgoingArc : expectedOutgoingArcs) {
                for (GraphArc<Integer> actualOutgoingArc : actualOutgoingArcs) {
                    if (expected.getNodes().indexOf(expectedOutgoingArc.getDestination()) ==
                        actual.getNodes().indexOf(actualOutgoingArc.getDestination())) {
                        assertEquals(expectedOutgoingArc.getLength(), actualOutgoingArc.getLength(),
                            "incorrect length of arc from node %d to node %d"
                                .formatted(i, expected.getNodes().indexOf(expectedOutgoingArc.getDestination())));
                        actualOutgoingArcs.remove(actualOutgoingArc);
                        continue outerLoop;
                    }
                }
                fail("no arc found from node %d to node %d found.".formatted(i, expected.getNodes().indexOf(expectedOutgoingArc.getDestination())));
            }
        }
    }

    public static <T> void assertListContainsAllWithPredicate(List<T> expected, List<T> actual, BiPredicate<T,T> predicate, String message) {
        List<T> actualCopy = new ArrayList<>(actual);
        for (T expectedElement : expected) {
            T matchingElement = null;
            for (T actualElement : actualCopy) {
                if (predicate.test(expectedElement, actualElement)) {
                    matchingElement = actualElement;
                    break;
                }
            }
            if (matchingElement == null) fail(message);
            actualCopy.remove(matchingElement);
        }
    }

    public static void assertDijkstraResultEquals(HashMap<NodePointer<Integer, Integer>, Integer> expectedDistance,
                                           HashMap<NodePointer<Integer, Integer>, NodePointer<Integer, Integer>> expectedPredecessor,
                                           List<NodePointer<Integer, Integer>> expected,
                                           List<NodePointer<Integer, Integer>> actual) {
        assertEquals(expected.size(), actual.size(), "the method run() did not return the correct amount of nodes");
        for (NodePointer<Integer, Integer> node : expected) {
            assertTrue(actual.contains(node), "the method run() did not return a node that matches the node %s"
                .formatted(Objects.toString(node)));
            assertEquals(expectedDistance.get(node), node.getDistance(),
                "the distance of node %s does not have the correct value".formatted(Objects.toString(node)));
            assertEquals(expectedPredecessor.get(node), node.getPredecessor(),
                "the predecessor of node %s does not have the correct value".formatted(Objects.toString(node)));
        }
    }

}
