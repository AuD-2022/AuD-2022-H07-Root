package h07;

import h07.implementation.NodePointerImpl;
import h07.implementation.PriorityQueueHeapImpl;
import h07.implementation.QueueEntry;

import kotlin.Pair;
import org.opentest4j.AssertionFailedError;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static h07.h3.PriorityQueueHeapTest.*;
import static h07.implementation.QueueEntry.QUEUE_ENTRY_CMP;
import static org.junit.jupiter.api.Assertions.*;
import static h07.h8.Point2DPointerTest.*;
import static h07.h7.AdjacencyMatrixPointerTest.*;
import static h07.h6.GraphPointerTest.*;

public class Assertions {

    public static void assertEqualsTutor(Object expected, Object actual, Supplier<AssertionMessage> message, boolean addExpected) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionFailedError(addExpected ? message.get().appendExpected(Objects.toString(expected))
                .appendActual(Objects.toString(actual)).toString() : message.get().toString());
        }
    }

    public static void assertEqualsTutor(Object expected, Object actual, Supplier<AssertionMessage> message) {
        assertEqualsTutor(expected, actual, message, true);
    }

    public static void assertTrueTutor(boolean actual, Supplier<AssertionMessage> message, boolean addExpected) {
        if (!actual) {
            throw new AssertionFailedError(addExpected ? message.get().appendExpected("true")
                .appendActual("false").toString() : message.get().toString());
        }
    }

    public static void assertTrueTutor(boolean actual, Supplier<AssertionMessage> message) {
        assertTrueTutor(actual, message, true);
    }

    public static void assertFalseTutor(boolean actual, Supplier<AssertionMessage> message) {
        assertTrueTutor(!actual, message, true);
    }

    public static void assertFalseTutor(boolean actual, Supplier<AssertionMessage> message, boolean addExpected) {
        assertTrueTutor(!actual, message, addExpected);
    }

    public static void assertNotNullTutor(Object actual, Supplier<AssertionMessage> message, boolean addExpected) {
        if (actual == null) {
            throw new AssertionFailedError(addExpected ? message.get().appendExpected("not null")
                .appendActual("null").toString() : message.get().toString());
        }
    }

    public static void assertNotNullTutor(Object actual, Supplier<AssertionMessage> message) {
        assertNotNullTutor(actual, message, true);
    }

    public static void assertNullTutor(Object actual, Supplier<AssertionMessage> message, boolean addExpected) {
        if (actual != null) {
            throw new AssertionFailedError(addExpected ? message.get().appendExpected("null")
                .appendActual(Objects.toString(actual)).toString() : message.get().toString());
        }
    }

    public static void assertNullTutor(Object actual, Supplier<AssertionMessage> message) {
        assertNullTutor(actual, message, true);
    }

    public static void assertSameTutor(Object expected, Object actual, Supplier<AssertionMessage> message, boolean addExpected) {
        if (actual != expected) {
            throw new AssertionFailedError(addExpected ? message.get().appendExpected(Objects.toString(expected))
                .appendActual(Objects.toString(actual)).toString() : message.get().toString());
        }
    }

    public static void assertSameTutor(Object expected, Object actual, Supplier<AssertionMessage> message) {
        assertSameTutor(expected, actual, message, true);
    }


    public static void failTutor(AssertionMessage message) {
        throw new AssertionFailedError(message.toString());
    }


    public static void assertPositionCorrect(List<QueueEntry> entries, QueueEntry entry, int actual, Supplier<AssertionMessage> message) {
        int lowerIndex = (int) entries.stream().filter(j -> QUEUE_ENTRY_CMP.compare(j, entry) > 0 ).count() + 1;
        int upperIndex = (int) (entries.size() - entries.stream().filter(j -> QUEUE_ENTRY_CMP.compare(j, entry) < 0).count());
        assertBetween(lowerIndex, upperIndex, actual,
            () -> message.get().appendHead("Incorrect value for position of %s."
                .formatted(Objects.toString(entry))), lowerIndex == actual + 1);
    }

    public static void assertPositionCorrect(QueueEntry[] entries, QueueEntry entry, int actual, int size, Supplier<AssertionMessage> message) {
        int lowerIndex = (int) Arrays.stream(entries).limit(size).filter(j -> QUEUE_ENTRY_CMP.compare(j, entry) > 0 ).count() + 1;
        int upperIndex = (int) (entries.length - Arrays.stream(entries).limit(size).filter(j -> QUEUE_ENTRY_CMP.compare(j, entry) < 0).count());
        assertBetween(lowerIndex, upperIndex, actual,
            () -> message.get().appendHead("Incorrect value for position of %s."
                .formatted(Objects.toString(entry))), lowerIndex == actual + 1);
    }

    public static void assertPriorityListEquals(List<QueueEntry> expected, List<QueueEntry> actual, Supplier<AssertionMessage> message) {
        assertEqualsTutor(expected.size(), actual.size(), () -> message.get().appendHead("The amount of elements in the [[[queue]]] is not correct"));
        assertListContainsAllWithPredicate(expected, actual, Objects::equals, "[[[queue]]]", message);
        assertCorrectOrder(actual, message);
    }

    public static void assertCorrectOrder(List<QueueEntry> list, Supplier<AssertionMessage> message) {
        if (list.size() < 2) return;

        for (int i = 1; i < list.size(); i++) {
            int finalI = i;
            assertTrueTutor(QUEUE_ENTRY_CMP.compare(list.get(i - 1), list.get(i)) >= 0,
                () -> message.get().appendHead("The order of the elements in the [[[queue]]] is not correct at position [[[queue[%d] == %s]]] and [[[queue[%d] == %s]]]"
                    .formatted(finalI - 1, Objects.toString(list.get(finalI - 1)), finalI, Objects.toString(list.get(finalI)))));
        }
    }

    public static void assertIndexMapCorrect(QueueEntry[] heap, Map<QueueEntry, Integer> indexMap, int size, Supplier<AssertionMessage> message) {

        assertEqualsTutor(indexMap.entrySet().size(), size, () -> message.get().appendHead("The amount of entries in the indexMap do not match the amount of elements in the queue"));

        for (int i = 0; i < size; i++) {
            assertIndexMapCorrect(heap, indexMap, heap[i], message);
        }
    }

    public static void assertIndexMapCorrect(QueueEntry[] heap, Map<QueueEntry, Integer> indexMap, QueueEntry key, Supplier<AssertionMessage> message) {

        assertTrueTutor(indexMap.containsKey(key), () -> message.get().appendHead("The indexMap does not have the entry %s as a key".formatted(key.toString())), false);
        assertBetween(0, heap.length -1, indexMap.get(key),
            () -> message.get().appendHead("Invalid value for entry %s in IndexMap"
                .formatted(Objects.toString(key))), false);
        assertEqualsTutor(key, heap[indexMap.get(key)],
            () -> message.get().appendHead("The heap array does not contain the expected element at the index specified in the indexMap"));
    }

    public static void assertPriorityQueueCorrect(PriorityQueueHeapImpl<QueueEntry> expectedQueue, PriorityQueueHeap<QueueEntry> actualQueue, Supplier<AssertionMessage> message) throws NoSuchFieldException, IllegalAccessException {
        assertEqualsTutor(expectedQueue.size(), getSize(actualQueue), () -> message.get().appendHead("The value of attribute [[[size]]] is not correct"));
        assertHeapCorrect(expectedQueue, actualQueue, message);
        assertIndexMapCorrect(getHeap(actualQueue), getIndexMap(actualQueue), expectedQueue.size(), message);
    }

    public static void assertHeapCorrect(PriorityQueueHeapImpl<QueueEntry> expectedQueue, PriorityQueueHeap<QueueEntry> actualQueue, Supplier<AssertionMessage> message) {
        QueueEntry[] actualHeap = new QueueEntry[expectedQueue.size()];
        System.arraycopy(getHeap(actualQueue), 0, actualHeap, 0, expectedQueue.size());

        for (int i = 0; i < actualHeap.length; i++) {
            int finalI = i;
            assertNotNullTutor(actualHeap[i], () -> message.get().appendHead("The [[[heap]]] array contained an unexpected [[[null]]] element at position %d".formatted(finalI)), false);
        }

        assertListContainsAllWithPredicate(expectedQueue.toList(), Arrays.stream(actualHeap).collect(Collectors.toList()), Objects::equals, "[[[heap]]] array", () -> message.get().appendHead("The [[[heap]]] array does not contain the correct elements"));

        assertHeapProperty(actualHeap, 0, message);

        assertCorrectOrder(getHeap(expectedQueue), actualHeap, message);
    }

    public static void assertCorrectOrder(QueueEntry[] expectedHeap, QueueEntry[] actualHeap, Supplier<AssertionMessage> message) {

        for (int i = 0; i < actualHeap.length; i++) {
            int finalI = i;
            assertSameTutor(expectedHeap[i], actualHeap[i], () -> message.get().appendHead(
                "The order of the elements in the [[[heap]]] is not correct at position [[[queue[%d] == %s]]]".formatted(finalI, actualHeap[finalI])
            ));
        }

    }

    public static void assertHeapProperty(QueueEntry[] heap, int index, Supplier<AssertionMessage> message) {
        if (index >= heap.length) return;

        QueueEntry parent = heap[index];
        int leftChildIndex = index * 2 + 1;
        int rightChildIndex = index * 2 + 2;

        assertHeapPropertyChild(heap, index, parent, leftChildIndex, message);

        assertHeapPropertyChild(heap, index, parent, rightChildIndex, message);
    }

    public static void assertHeapPropertyChild(QueueEntry[] heap, int index, QueueEntry parent, int ChildIndex, Supplier<AssertionMessage> message) {
        if (ChildIndex < heap.length) {
            QueueEntry leftChild = heap[ChildIndex];

            if (leftChild != null){
                assertTrueTutor(QUEUE_ENTRY_CMP.compare(parent, leftChild) >= 0,
                    () -> message.get().appendHead("The Heap array does not satisfy the heap property for parent heap[%d] = %d and left child heap[%d] = %d"
                        .formatted(index, parent.value, ChildIndex, leftChild.value)));
                assertHeapProperty(heap, ChildIndex, message);
            }
        }
    }

    public static void assertQueueFactoryComparatorCorrect(Comparator<NodePointer<Integer, Integer>> actual) {
        assertEquals(0, actual.compare(new NodePointerImpl(1), new NodePointerImpl(1)), "The [[[queueFactory]]] wasn't invoked with a correct Comparator. The Comparator returned an incorrect value for two nodes with same distance");
        assertTrue(actual.compare(new NodePointerImpl(1), new NodePointerImpl(2)) > 0, "The [[[queueFactory]]] wasn't invoked with a correct Comparator. The Comparator returned an incorrect value when the distance to the first node is shorter than the distance to the second node. Expected a value greater than 0, actual: %d".formatted(actual.compare(new NodePointerImpl(1), new NodePointerImpl(2))));
        assertTrue(actual.compare(new NodePointerImpl(2), new NodePointerImpl(1)) < 0, "The [[[queueFactory]]] wasn't invoked with a correct Comparator. The Comparator returned an incorrect value when the distance to the first node is longer than the distance to the second node. Expected a value less than 0, actual: %d".formatted(actual.compare(new NodePointerImpl(1), new NodePointerImpl(2))));
    }


    public static void assertBetween(int min, int max, int actual, Supplier<AssertionMessage> message, boolean note) {
        assertTrueTutor(min <= actual && max >= actual,
            () -> message.get().appendHead(" ==> expected: value between %d and %d, but was %d".formatted(min, max, actual) + (note ? ". Note that [[[getPosition()]]] is supposed to start counting from 1 " : "")), false);
    }

    public static void assertBetween(double min, double max, double actual, Supplier<AssertionMessage> message) {
        assertTrueTutor(min <= actual && max >= actual,
            () -> message.get().appendHead(" ==> expected: value between %f and %f but was %f".formatted(min, max, actual)));
    }

    public static <T> void assertArrayContains(T[] array, T element, Supplier<AssertionMessage> message) {
        List<T> list = Arrays.stream(array).toList();
        assertTrueTutor(list.contains(element), () -> message.get().appendHead("The heap array did not contain the element %s".formatted(element.toString())));
    }

    public static <T> void assertArrayDoesNotContains(T[] array, T element, int size, Supplier<AssertionMessage> message) {
        List<T> list = Arrays.stream(array).limit(size).toList();
        assertFalseTutor(list.contains(element), () -> message.get().appendHead("The heap array contained the element %s".formatted(element.toString())));
    }

    public static void assertArcPointerGraphEquals(
        HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> expectedExistingNodePointers,
        HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> expectedExistingArcPointers,
        GraphArc<Integer> expectedGraphArc,
        ArcPointerGraph<Integer, Integer> actualArcPointer,
        Supplier<AssertionMessage> message) throws NoSuchFieldException, IllegalAccessException {

        assertEqualsTutor(expectedExistingArcPointers, getExistingArcPointersMap(actualArcPointer),
            () -> message.get().appendHead("The [[[existingArcPointersMap]]] does not contain the correct entries"));
        assertEqualsTutor(expectedExistingNodePointers, getExistingNodePointersMap(actualArcPointer),
            () -> message.get().appendHead("The [[[existingNodePointersMap]]] does not contain the correct entries"));
        assertNotNullTutor(getGraphArc(actualArcPointer), () -> message.get().appendHead("The returned [[[ArcPointer]]] is null"));
        assertEqualsTutor(expectedGraphArc.getDestination(), getGraphArc(actualArcPointer).getDestination(),
            () -> message.get().appendHead("The [[[graphArc]]] attribute does not contain the correct value"));
        assertEqualsTutor(expectedGraphArc.getLength(), getGraphArc(actualArcPointer).getLength(),
            () -> message.get().appendHead("The [[[graphArc]]] attribute does not contain the correct length"));
    }

    public static void assertNodePointerGraphEquals(
        HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> expectedExistingNodePointers,
        HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> expectedExistingArcPointers,
        GraphNode<Integer> expectedGraphNode,
        Integer expectedDistance,
        NodePointer<Integer, Integer> expectedPredecessor,
        NodePointerGraph<Integer, Integer> actualNodePointer,
        Supplier<AssertionMessage> message) throws NoSuchFieldException, IllegalAccessException {

        assertEqualsTutor(expectedExistingArcPointers, getExistingArcPointersMap(actualNodePointer), () -> message.get().appendHead("The existingArcPointersMap does not contain the correct entries"));
        assertEqualsTutor(expectedExistingNodePointers, getExistingNodePointersMap(actualNodePointer), () -> message.get().appendHead("The existingNodePointersMap does not contain the correct entries"));
        assertEqualsTutor(expectedGraphNode, getGraphNode(actualNodePointer), () -> message.get().appendHead("The graphNode attribute does not contain the correct graphNode"));
        assertEqualsTutor(expectedDistance, getDistance(actualNodePointer), () -> message.get().appendHead("The distance attribute does not contain the correct distance"));
        assertEqualsTutor(expectedPredecessor, getPredecessor(actualNodePointer), () -> message.get().appendHead("The predecessor attribute does not contain the correct predecessor"));
    }

    public static void assertArcPointerAdjacencyMatrixEquals(
        AdjacencyMatrix<Integer> expectedAdjacencyMatrix,
        HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> expectedExistingNodePointers,
        HashMap<h07.Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> expectedExistingArcPointers,
        Integer expectedRow,
        Integer expectedColumn,
        ArcPointerAdjacencyMatrix<Integer, Integer> actualArc,
        Supplier<AssertionMessage> message) throws NoSuchFieldException, IllegalAccessException {

        assertEqualsTutor(expectedAdjacencyMatrix, getAdjacencyMatrix(actualArc), () -> message.get().appendHead("The adjacencyMatrix attribute does not contain the correct adjacencyMatrix"));
        assertEqualsTutor(expectedExistingArcPointers, getExistingArcPointersMap(actualArc), () -> message.get().appendHead("The existingArcPointersMap does not contain the correct entries"));
        assertEqualsTutor(expectedExistingNodePointers, getExistingNodePointersMap(actualArc), () -> message.get().appendHead("The existingNodePointersMap does not contain the correct entries"));
        assertEqualsTutor(expectedRow, getRow(actualArc), () -> message.get().appendHead("The row attribute does not contain the correct value"));
        assertEqualsTutor(expectedColumn, getColumn(actualArc), () -> message.get().appendHead("The column attribute does not contain the correct value"));
    }

    public static void assertNodePointerAdjacencyMatrixEquals(
        HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> expectedExistingNodePointers,
        HashMap<h07.Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> expectedExistingArcPointers,
        AdjacencyMatrix<Integer> expectedAdjacencyMatrix,
        Integer expectedDistance,
        NodePointer<Integer, Integer> expectedPredecessor,
        Integer expectedRow,
        NodePointerAdjacencyMatrix<Integer, Integer> actualNodePointer,
        Supplier<AssertionMessage> message) throws NoSuchFieldException, IllegalAccessException {

        assertEqualsTutor(expectedAdjacencyMatrix, getAdjacencyMatrix(actualNodePointer), () -> message.get().appendHead("The adjacencyMatrix attribute does not contain the correct adjacencyMatrix"));
        assertEqualsTutor(expectedExistingArcPointers, getExistingArcPointersMap(actualNodePointer), () -> message.get().appendHead("The existingArcPointersMap does not contain the correct entries"));
        assertEqualsTutor(expectedExistingNodePointers, getExistingNodePointersMap(actualNodePointer), () -> message.get().appendHead("The existingNodePointersMap does not contain the correct entries"));
        assertEqualsTutor(expectedRow, getRow(actualNodePointer), () -> message.get().appendHead("The row attribute does not contain the correct value"));
        assertEqualsTutor(expectedDistance, getDistance(actualNodePointer), () -> message.get().appendHead("The distance attribute does not contain the correct distance"));
        assertEqualsTutor(expectedPredecessor, getPredecessor(actualNodePointer), () -> message.get().appendHead("The predecessor attribute does not contain the correct predecessor"));
    }

    public static void assertArcPointerPoint2DEquals(
        HashMap<Point2D, NodePointerPoint2D> expectedExistingNodePointers,
        HashMap<h07.Pair<Point2D, Point2D>, ArcPointerPoint2D> expectedExistingArcPointers,
        Point2D expectedSource,
        Point2D expectedDestination,
        Point2DCollection expectedCollection,
        ArcPointerPoint2D actualArcPointer,
        Supplier<AssertionMessage> message) throws NoSuchFieldException, IllegalAccessException {

        assertEqualsTutor(expectedExistingArcPointers, getExistingArcPointersMap(actualArcPointer), () -> message.get().appendHead("The existingArcPointersMap does not contain the correct entries"));
        assertEqualsTutor(expectedExistingNodePointers, getExistingNodePointersMap(actualArcPointer), () -> message.get().appendHead("The existingNodePointersMap does not contain the correct entries"));
        assertEqualsTutor(expectedSource, getSource(actualArcPointer), () -> message.get().appendHead("The source attribute does not contain the correct value"));
        assertEqualsTutor(expectedDestination, getDestination(actualArcPointer), () -> message.get().appendHead("The destination attribute does not contain the correct distance"));
        assertEqualsTutor(expectedCollection, getCollection(actualArcPointer), () -> message.get().appendHead("The collection attribute does not contain the correct predecessor"));
    }

    public static void assertNodePointerPoint2DEquals(
        HashMap<Point2D, NodePointerPoint2D> expectedExistingNodePointers,
        HashMap<h07.Pair<Point2D, Point2D>, ArcPointerPoint2D> expectedExistingArcPointers,
        Point2D expectedPoint,
        Point2DCollection expectedCollection,
        NodePointer<Double, Double> expectedPredecessor,
        Double expectedDistance,
        NodePointerPoint2D actualNodePointer,
        Supplier<AssertionMessage> message) throws NoSuchFieldException, IllegalAccessException {
        assertEqualsTutor(expectedExistingArcPointers, getExistingArcPointersMap(actualNodePointer), () -> message.get().appendHead("The existingArcPointersMap does not contain the correct entries"));
        assertEqualsTutor(expectedExistingNodePointers, getExistingNodePointersMap(actualNodePointer), () -> message.get().appendHead("The existingNodePointersMap does not contain the correct entries"));
        assertEqualsTutor(expectedPoint, getPoint(actualNodePointer), () -> message.get().appendHead("The point attribute does not contain the correct value"));
        assertEqualsTutor(expectedCollection, getCollection(actualNodePointer), () -> message.get().appendHead("The collection attribute does not contain the correct value"));
        assertEqualsTutor(expectedDistance, getDistance(actualNodePointer), () -> message.get().appendHead("The distance attribute does not contain the correct value"));
        assertEqualsTutor(expectedPredecessor, getPredecessor(actualNodePointer), () -> message.get().appendHead("The predecessor attribute does not contain the correct value"));
    }

    public static void assertAdjacencyMatrixEquals(Object[][] expected, Object[][] actual, Supplier<AssertionMessage> message) {
        assertEqualsTutor(expected.length, actual.length, () ->
            message.get().appendHead("Incorrect length for outer array of the adjacencyMatrix"));

        for (int i = 0; i < expected.length; i++) {
            int finalI = i;
            assertEqualsTutor(expected[i].length, actual[i].length, () ->
                message.get().appendHead("incorrect length for inner array at index %d of the adjacencyMatrix".formatted(finalI)));
        }

        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected.length; j++) {
                int finalI = i;
                int finalJ = j;
                assertEqualsTutor(expected[i][j], actual[i][j], () ->
                    message.get().appendHead("incorrect value in the adjacencyMatrix at index [%d,%d]".formatted(finalI, finalJ)));
            }
        }
    }

    public static void assertGraphEquals(Graph<Integer> expected, Graph<Integer> actual, Supplier<AssertionMessage> message) {

        assertEqualsTutor(expected.getNodes().size(), actual.getNodes().size(),
            () -> message.get().appendHead("The amount of nodes is not correct"));

        for (int i = 0; i < expected.getNodes().size(); i++) {
            List<GraphArc<Integer>> expectedOutgoingArcs = expected.getNodes().get(i).getOutgoingArcs();
            Collection<GraphArc<Integer>> actualOutgoingArcs = new ConcurrentLinkedDeque<>(actual.getNodes().get(i).getOutgoingArcs());

            int finalI = i;
            assertEqualsTutor(expectedOutgoingArcs.size(), actualOutgoingArcs.size(), () ->
                message.get().appendHead("The node %d does not have the correct amount of outgoing arcs".formatted(finalI)));

            outerLoop: for (GraphArc<Integer> expectedOutgoingArc : expectedOutgoingArcs) {
                for (GraphArc<Integer> actualOutgoingArc : actualOutgoingArcs) {
                    if (expected.getNodes().indexOf(expectedOutgoingArc.getDestination()) == actual.getNodes().indexOf(actualOutgoingArc.getDestination())) {

                        assertEqualsTutor(expectedOutgoingArc.getLength(), actualOutgoingArc.getLength(), () ->
                            message.get().appendHead("The arc from node %d to node %d does not have the correct length"
                                .formatted(finalI, expected.getNodes().indexOf(expectedOutgoingArc.getDestination())))
                        );

                        actualOutgoingArcs.remove(actualOutgoingArc);
                        continue outerLoop;
                    }
                }
                failTutor(message.get().appendHead("No arc from node %d to node %d found"
                    .formatted(finalI, expected.getNodes().indexOf(expectedOutgoingArc.getDestination()))));
            }
        }
    }

    public static <T> void assertListContainsAllWithPredicate(List<T> expected, List<T> actual, BiPredicate<T,T> predicate, String listName, Supplier<AssertionMessage> message) {
        List<T> actualCopy = new ArrayList<>(actual);
        for (T expectedElement : expected) {
            T matchingElement = null;
            for (T actualElement : actualCopy) {
                if (predicate.test(expectedElement, actualElement)) {
                    matchingElement = actualElement;
                    break;
                }
            }
            assertNotNullTutor(matchingElement, () -> message.get().appendHead("The %s did not contain a matching element for expected element %s"
                .formatted(listName, Objects.toString(expectedElement))), false);
            actualCopy.remove(matchingElement);
        }
    }

    public static void assertDijkstraResultEquals(HashMap<NodePointer<Integer, Integer>, Integer> expectedDistance,
                                                  HashMap<NodePointer<Integer, Integer>, NodePointer<Integer, Integer>> expectedPredecessor,
                                                  List<NodePointer<Integer, Integer>> expected,
                                                  List<NodePointer<Integer, Integer>> actual,
                                                  NodePointer<Integer, Integer> startNode,
                                                  Supplier<AssertionMessage> message) {
        assertFalseTutor(actual.contains(startNode), () -> message.get().appendHead("The returned list contains the startNode"), false);
        assertEqualsTutor(expected.size(), actual.size(), () -> message.get().appendHead("The returned list did not return the correct amount of nodes"), false);
        for (NodePointer<Integer, Integer> node : expected) {
            assertTrueTutor(actual.contains(node), () -> message.get().appendHead(
                "The returned list does not contain the node %s".formatted(Objects.toString(node))), false);
            assertEqualsTutor(expectedDistance.get(node), node.getDistance(), () -> message.get().appendHead(
                "The distance of node %s does not have the correct value".formatted(Objects.toString(node))), false);
            assertEqualsTutor(expectedPredecessor.get(node), node.getPredecessor(), () -> message.get().appendHead(
                "The predecessor of node %s does not have the correct value".formatted(Objects.toString(node))), false);
        }
    }

    public static class AssertionMessage {
        String head;
        String expected;
        String actual;
        List<Pair<String, String>> inputs;

        public AssertionMessage(String head, List<Pair<String, String>> inputs) {
            this.head = head;
            this.expected = "";
            this.actual = "";
            this.inputs = inputs;
        }

        public AssertionMessage(List<Pair<String, String>> inputs) {
            head = "";
            expected = "";
            actual = "";
            this.inputs = inputs;
        }

        public AssertionMessage appendHead(String head) {
            this.head = this.head + (this.head.length() > 0 ? ". " : "") + head;
            return this;
        }

        public AssertionMessage appendActual(String actual) {
            this.actual = this.actual + (this.actual.length() > 0 ? ". " : "") + actual;
            return this;
        }

        public AssertionMessage appendExpected(String expected) {
            this.expected = this.expected + (this.expected.length() > 0 ? ". " : "") + expected;
            return this;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(head);

            if (actual.length() > 0 && expected.length() > 0) {
                builder.append(" ==> expected: <%s> but was <%s>.".formatted(expected, actual));
            }

            if (inputs.size() > 0) {
                builder.append("\nInputs:");
                inputs.forEach(pair -> builder.append("\n").append("%s: %s".formatted(pair.getFirst(), pair.getSecond())));
            }

            return builder.toString();

        }
    }

}
