package h07;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * An example JUnit test class.
 */
public class PublicTests {

    public abstract static class IPriorityQueueTest<Q extends IPriorityQueue<Integer>> {

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

        protected abstract Q newEmptyQueue();

        protected void assertQueue(Iterable<Object> expected, Q actual) {
            assertIterableEquals(
                expected,
                queueToIterable(actual));
        }

        protected abstract Iterable<Object> queueToIterable(Q queue);
    }

    @Nested
    class PriorityQueueListTest extends IPriorityQueueTest<PriorityQueueList<Integer>> {

        @Override
        protected PriorityQueueList<Integer> newEmptyQueue() {
            return new PriorityQueueList<>(CMP);
        }

        @Override
        protected Iterable<Object> queueToIterable(PriorityQueueList<Integer> queue) {
            return new ArrayList<>(queue.getInternalList());
        }
    }

    @Nested
    class PriorityQueueHeapTest extends IPriorityQueueTest<PriorityQueueHeap<Integer>> {

        @Override
        protected PriorityQueueHeap<Integer> newEmptyQueue() {
            return new PriorityQueueHeap<>(CMP, 32);
        }

        @Override
        protected Iterable<Object> queueToIterable(PriorityQueueHeap<Integer> queue) {
            return () ->
                new HeapIterator<>(queue);
        }
    }

    private static class HeapIterator<T> implements Iterator<Object> {

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

        private enum Node { A, B, C, D, E, F }

        private final Graph<Double> graph = MockGraph.graph(Node.class, node ->
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

        @Test
        void testConstructor() {
            System.out.println(graph);
        }
    }

    @Nested
    class DijkstraTest {

        private static final Comparator<Double> CMP = (a, b) ->
            Double.compare(b, a);

        private final Dijkstra<Double, Double> dijkstra = new Dijkstra<>(CMP, Double::sum, PriorityQueueList::new);

        @Test
        void testInitialize() {
            throw new RuntimeException("Impl me");
            // dijkstra.initialize(Node.A.pointer());
        }
    }

    private abstract static class AbstractNodePointer<L, D> implements NodePointer<L, D> {

        private D distance;
        private NodePointer<L, D> predecessor = null;

        @Override
        public D getDistance() {
            return distance;
        }

        @Override
        public void setDistance(@NotNull D distance) {
            this.distance = distance;
        }

        @Override
        public @Nullable NodePointer<L, D> getPredecessor() {
            return predecessor;
        }

        @Override
        public void setPredecessor(@NotNull NodePointer<L, D> predecessor) {
            this.predecessor = predecessor;
        }

        @Override
        public abstract Iterator<ArcPointer<L, D>> outgoingArcs();
    }

    private static class MockGraph {

        public static <E extends Enum<E>> Graph<Double> graph(Class<E> clazz, Function<E, List<Pair<Double, E>>> getArcs) {
            Map<E, GraphNode<Double>> nodes = new EnumMap<>(clazz);

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

        public static <T> Pair<Double, T> arc(double length, T dest) {
            return new Pair<>(length, dest);
        }
    }
}
