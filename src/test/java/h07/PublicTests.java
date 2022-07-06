package h07;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * An example JUnit test class.
 */
public class PublicTests {

    private enum Node {
        A, B, C, D, E, F;

        public GraphNode<Integer> graphNode() {
            return graph.getNodes().get(ordinal());
        }

        public NodePointer<Integer, Integer> nodePointer() {
            return new NodePointerGraph<>(
                new HashMap<>(),
                new HashMap<>(),
                graphNode());
        }
    }

    private static final Graph<Integer> graph = MockGraph.graph(Node.class, node ->
        switch (node) {
            case A -> List.of(
                MockGraph.arc(2, Node.B),
                MockGraph.arc(5, Node.D));
            case B -> List.of(
                MockGraph.arc(1, Node.A),
                MockGraph.arc(4, Node.F));
            case C -> List.of(
                MockGraph.arc(3, Node.B));
            case D -> List.of(
                MockGraph.arc(8, Node.C));
            case E -> List.of();
            case F -> List.of(
                MockGraph.arc(6, Node.D),
                MockGraph.arc(7, Node.E));
        }
    );

    private final Integer[][] adjacencyMatrix = {
        //         A     B     C     D     E     F
        /* A */ { null,    2, null,    5, null, null },
        /* B */ {    1, null, null, null, null,    4 },
        /* C */ { null,    3, null, null, null, null },
        /* D */ { null, null,    8, null, null, null },
        /* E */ { null, null, null, null, null, null },
        /* F */ { null, null, null,    6,    7, null }
    };

    public abstract static class AbstractPriorityQueueTest<Q extends IPriorityQueue<Integer>> {

        protected static final Comparator<Integer> CMP = Integer::compare;

        @Test
        void testConstructor() {
            Q queue = newEmptyQueue();
            assertQueue(
                List.of(),
                queue);
        }

        @Test
        void testAdd() {
            Q queue = newEmptyQueue();

            queue.add(2);
            queue.add(3);
            queue.add(5);
            queue.add(0);
            queue.add(4);
            queue.add(1);

            assertQueue(
                List.of(5, 4, 3, 2, 1, 0),
                queue);
        }

        @Test
        void testDelete() {
            Q queue = newEmptyQueue();

            assertNull(queue.delete(69));

            queue.add(2);
            queue.add(3);
            queue.add(5);
            queue.add(0);
            queue.add(4);
            queue.add(1);

            assertEquals(1, queue.delete(1));
            assertEquals(3, queue.delete(3));

            assertQueue(
                List.of(5, 4, 2, 0),
                queue);
        }

        @Test
        void testGetFront() {
            Q queue = newEmptyQueue();

            queue.add(2);
            assertEquals(2, queue.getFront());

            queue.add(3);
            assertEquals(3, queue.getFront());

            queue.add(5);
            assertEquals(5, queue.getFront());

            queue.add(0);
            assertEquals(5, queue.getFront());

            queue.add(4);
            assertEquals(5, queue.getFront());

            queue.add(1);
            assertEquals(5, queue.getFront());
        }

        @Test
        void testDeleteFront() {
            Q queue = newEmptyQueue();

            queue.add(2);
            queue.add(3);
            queue.add(5);

            assertEquals(5, queue.deleteFront());

            queue.add(0);
            assertEquals(3, queue.deleteFront());

            queue.add(4);
            assertEquals(4, queue.deleteFront());

            queue.add(1);
            assertEquals(2, queue.deleteFront());

            assertEquals(1, queue.deleteFront());
            assertEquals(0, queue.deleteFront());

            assertQueue(List.of(), queue);
        }

        @Test
        void testGetPosition() {
            Q queue = newEmptyQueue();

            assertEquals(-1, queue.getPosition(0));

            queue.add(0);
            assertEquals(1, queue.getPosition(0));

            queue.add(4);
            assertEquals(2, queue.getPosition(0));

            queue.add(1);
            assertEquals(1, queue.getPosition(4));

            queue.add(5);
            assertEquals(3, queue.getPosition(1));

            assertEquals(1, queue.getPosition(5));
            assertEquals(-1, queue.getPosition(69));
        }

        @Test
        void testContains() {
            Q queue = newEmptyQueue();

            assertFalse(queue.contains(69));

            queue.add(2);
            queue.add(3);
            queue.add(5);
            queue.add(0);
            queue.add(4);
            queue.add(1);

            assertTrue(queue.contains(1));
            assertTrue(queue.contains(3));
        }

        @Test
        void testClear() {
            Q queue = newEmptyQueue();

            queue.add(2);
            queue.add(3);
            queue.add(5);
            queue.add(0);
            queue.add(4);
            queue.add(1);

            queue.clear();
            assertQueue(
                List.of(),
                queue);
        }

        protected void assertQueue(Iterable<Integer> expected, Q actual) {
            assertIterableEquals(
                expected,
                queueToIterable(actual));
        }

        protected abstract Q newEmptyQueue();
        protected abstract Iterable<Integer> queueToIterable(Q queue);
    }

    @Nested
    class PriorityQueueListTest extends AbstractPriorityQueueTest<PriorityQueueList<Integer>> {

        @Override
        protected PriorityQueueList<Integer> newEmptyQueue() {
            return new PriorityQueueList<>(CMP);
        }

        @Override
        protected Iterable<Integer> queueToIterable(PriorityQueueList<Integer> queue) {
            return new ArrayList<>(queue.getInternalList());
        }
    }

    @Nested
    class PriorityQueueHeapTest extends AbstractPriorityQueueTest<PriorityQueueHeap<Integer>> {

        @Override
        protected PriorityQueueHeap<Integer> newEmptyQueue() {
            return new PriorityQueueHeap<>(CMP, 32);
        }

        @Override
        protected Iterable<Integer> queueToIterable(PriorityQueueHeap<Integer> queue) {
            return () ->
                new HeapIterator<>(queue);
        }
    }

    private static class HeapIterator<T> implements Iterator<T> {

        private final PriorityQueueHeap<T> heap;

        private HeapIterator(PriorityQueueHeap<T> heap) {
            this.heap = heap;
        }

        @Override
        public boolean hasNext() {
            return heap.getFront() != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return heap.deleteFront();
        }
    }

    @Nested
    class AdjacencyMatrixTest {

        @Test
        void testConstructorAndGetMatrix() {
            var matrix = new AdjacencyMatrix<>(graph);
            Object[][] actual = matrix.getMatrix();
            assertArrayEquals(adjacencyMatrix, actual);
        }
    }

    public abstract static class AbstractArcPointerTest {

        @Test
        void testConstructorAndGetters() {
            var arcFromDtoC = getArcFromCtoD();
            assertArcFromDToC(arcFromDtoC);
        }

        protected abstract ArcPointer<Integer, Integer> getArcFromCtoD();
    }

    public abstract static class AbstractNodePointerTest {

        @Test
        void testOutgoingArcs() {
            var nodeD = getNodeD();
            var arcs = nodeD.outgoingArcs();
            assertArcFromDToC(arcs.next());
            assertFalse(arcs.hasNext());
        }

        protected abstract NodePointer<Integer, Integer> getNodeD();
    }

    private static void assertArcFromDToC(ArcPointer<Integer, Integer> arc) {
        assertEquals(8, arc.getLength());

        var dest = arc.destination();
        assertNull(dest.getPredecessor());

        var arcs = dest.outgoingArcs();

        assertEquals(3, arcs.next().getLength());
        assertFalse(arcs.hasNext());
    }

    @Nested
    class ArcPointerAdjacencyMatrixTest extends AbstractArcPointerTest {

        @Override
        protected ArcPointer<Integer, Integer> getArcFromCtoD() {
            return new ArcPointerAdjacencyMatrix<>(
                new HashMap<>(),
                new HashMap<>(),
                new AdjacencyMatrix<>(adjacencyMatrix),
                Node.D.ordinal(), Node.C.ordinal());
        }
    }

    @Nested
    class NodePointerAdjacencyMatrixTest extends AbstractNodePointerTest {

        @Override
        protected NodePointer<Integer, Integer> getNodeD() {
            return new NodePointerAdjacencyMatrix<>(
                new HashMap<>(),
                new HashMap<>(),
                new AdjacencyMatrix<>(adjacencyMatrix),
                Node.D.ordinal());
        }
    }

    @Nested
    class ArcPointerGraphTest extends AbstractArcPointerTest {

        @Override
        protected ArcPointer<Integer, Integer> getArcFromCtoD() {
            var arc =
                Node.D.graphNode()
                .getOutgoingArcs()
                .get(0);

            return new ArcPointerGraph<>(
                new HashMap<>(),
                new HashMap<>(),
                arc);
        }
    }

    @Nested
    class NodePointerGraphTest extends AbstractNodePointerTest {

        @Override
        protected NodePointer<Integer, Integer> getNodeD() {
            return Node.D.nodePointer();
        }
    }

    @Nested
    class GraphTest {

        @Test
        void testConstructorAndGetNodes() {
            var actual = new Graph<>(new AdjacencyMatrix<>(adjacencyMatrix));
            assertNodesAndArcsMatch(graph.getNodes(), actual.getNodes());
        }

        private void assertNodesAndArcsMatch(List<GraphNode<Integer>> expected, List<GraphNode<Integer>> actual) {
            assertEquals(expected.size(), actual.size());

            for (int i = 0; i < expected.size(); i++) {
                assertArcsMatch(
                    expected.get(i).getOutgoingArcs(),
                    actual.get(i).getOutgoingArcs());
            }
        }

        private void assertArcsMatch(List<GraphArc<Integer>> expected, List<GraphArc<Integer>> actual) {
            assertEquals(expected.size(), actual.size());

            for (int i = 0; i < expected.size(); i++) {
                assertEquals(
                    expected.get(i).getLength(),
                    actual.get(i).getLength());
            }
        }
    }

    @Nested
    class DijkstraTest {

        private static final Comparator<Integer> CMP = (a, b) ->
            Integer.compare(b, a);

        private final Dijkstra<Integer, Integer> dijkstra = new Dijkstra<>(CMP, Integer::sum, PriorityQueueList::new);

        private final NodePointer<Integer, Integer> startNode = Node.A.nodePointer();

        @Test
        void testInitializeAndRun() {
            startNode.setDistance(0);
            dijkstra.initialize(startNode);

            var nodes = dijkstra.run();
            assertStartNode();
            assertNodes(nodes, 5);
        }

        @Test
        void testInitializeWithPredAndRun() {
            startNode.setDistance(0);
            dijkstra.initialize(startNode, node ->
                Objects.equals(node.getDistance(), 6));

            var nodes = dijkstra.run();
            assertStartNode();
            assertNodes(nodes, 3);
        }

        private void assertNodes(List<NodePointer<Integer, Integer>> nodes, int maxNodes) {
            assertEquals(maxNodes, nodes.size());
            assertDistances(nodes, maxNodes);
            assertPredecessors(nodes, maxNodes);
        }

        private void assertPredecessors(List<NodePointer<Integer, Integer>> nodes, int maxNodes) {
            var predecessors = new NodePointer[]{
                startNode,
                startNode,
                nodes.get(0),
                nodes.get(1),
                nodes.get(2),
            };
            Objects.checkIndex(maxNodes, predecessors.length+1);

            for (int i = 0; i < maxNodes; i++) {
                assertEquals(predecessors[i], nodes.get(i).getPredecessor());
            }
        }

        private void assertDistances(List<NodePointer<Integer, Integer>> nodes, int maxNodes) {
            int[] distances = {2, 5, 6, 13, 13};
            Objects.checkIndex(maxNodes, distances.length+1);

            for (int i = 0; i < maxNodes; i++) {
                assertEquals(distances[i], nodes.get(i).getDistance());
            }
        }

        private void assertStartNode() {
            assertEquals(0, startNode.getDistance());
            assertNull(startNode.getPredecessor());
        }
    }

    @Nested
    class PathFinderTest {

        List<NodePointer<Integer, Integer>> expectedPath = new ArrayList<>();

        @Test
        void testPathFinder() {
            createPath();

            var pathFinder = new PathFinder<Integer, Integer>();
            var actual = pathFinder.apply(expectedPath.get(expectedPath.size()-1));

            assertEquals(expectedPath.size(), actual.size());
            for (int i = 0; i < expectedPath.size(); i++) {
                assertSame(expectedPath.get(i), actual.get(i));
            }
        }

        private void createPath() {
            var node = Node.A.nodePointer();

            while (node != null) {
                expectedPath.add(node);

                var next = getNextNode(node);
                if (next != null) {
                    next.setPredecessor(node);
                }

                node = next;
            }
        }

        private @Nullable NodePointer<Integer, Integer> getNextNode(NodePointer<Integer, Integer> node) {
            var arcs = node.outgoingArcs();
            while (arcs.hasNext()) {
                var dest = arcs.next().destination();

                if (!expectedPath.contains(dest)) {
                    return dest;
                }
            }

            return null;
        }
    }

    @Nested
    class Point2DCollectionTest {

        void testConstructorAndGetPoints() {
            var point2DCollection = new Point2DCollection(
                20,
                new Point2D(-5, -5),
                new Point2D(5, 5),
                1);

            assertEquals(1, point2DCollection.getMaxArcLength());

            var points = point2DCollection.getPoints();
            assertEquals(20, points.size());

            for (var point : points) {
                assertTrue(point.getX() >= -5);
                assertTrue(point.getX() <=  5);
                assertTrue(point.getY() >= -5);
                assertTrue(point.getY() <=  5);
            }
        }
    }

    @Nested
    class NodePointerPoint2DTest {

        private static final Point2DCollection point2DCollection = new Point2DCollection(getPoints(), 1);

        private static List<Point2D> getPoints() {
            var points = new ArrayList<Point2D>();

            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    points.add(new Point2D(x, y));
                }
            }

            return points;
        }

        private final NodePointerPoint2D pointer = new NodePointerPoint2D(
            new HashMap<>(),
            new HashMap<>(),
            point2DCollection.getPoints().get(0),
            point2DCollection);

        @Test
        void testConstructorAndOutgoingArcs() {
            var arcs = pointer.outgoingArcs();

            ArcPointerPoint2DTest.assertArcs(arcs, 2);
        }
    }

    @Nested
    class ArcPointerPoint2DTest {

        private final ArcPointerPoint2D pointer = new ArcPointerPoint2D(
            new HashMap<>(),
            new HashMap<>(),
            NodePointerPoint2DTest.point2DCollection.getPoints().get(0),
            NodePointerPoint2DTest.point2DCollection.getPoints().get(1),
            NodePointerPoint2DTest.point2DCollection);

        @Test
        void testConstructorAndGetLength() {
            assertEquals(1, pointer.getLength());
        }

        @Test
        void testDestination() {
            var dest = pointer.destination();

            var arcs = dest.outgoingArcs();

            assertArcs(arcs, 3);
        }

        private static void assertArcs(Iterator<ArcPointer<Double, Double>> arcs, int numberOfArcs) {
            for (int i = 0; i < numberOfArcs; i++) {
                var arc = arcs.next();
                assertEquals(1, arc.getLength());
            }

            assertFalse(arcs.hasNext());
        }
    }

    private static class MockGraph {

        public static <E extends Enum<E>> Graph<Integer> graph(Class<E> clazz, Function<E, List<Pair<Integer, E>>> getArcs) {
            Map<E, GraphNode<Integer>> nodes = new EnumMap<>(clazz);

            for (E node : clazz.getEnumConstants()) {
                nodes.put(node, new GraphNode<>());
            }

            for (E node : clazz.getEnumConstants()) {
                for (var arc : getArcs.apply(node)) {
                    var from = nodes.get(node);
                    var length = arc.getElement1();
                    var dest = nodes.get(arc.getElement2());
                    from.getOutgoingArcs().add(new GraphArc<>(length, dest));
                }
            }

            return new Graph<>(new ArrayList<>(nodes.values()));
        }

        public static <T> Pair<Integer, T> arc(int length, T dest) {
            return new Pair<>(length, dest);
        }
    }
}
