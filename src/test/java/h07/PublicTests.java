package h07;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;

import java.util.*;

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
    class DijkstraTest {

        private static final Comparator<Double> CMP = (a, b) ->
            Double.compare(b, a);

        private enum Node {
            A, B, C, D, E, F;


            public NodePointer<Double, Double> pointer() {
                return new NodePointer<Double, Double>() {

                    private double distance = 0;

                    private NodePointer<Double, Double> processor = null;

                    @Override
                    public Double getDistance() {
                        return distance;
                    }

                    @Override
                    public void setDistance(@NotNull Double distance) {
                        this.distance = distance;
                    }

                    @Override
                    public @Nullable NodePointer<Double, Double> getPredecessor() {
                        return processor;
                    }

                    @Override
                    public void setPredecessor(@NotNull NodePointer<Double, Double> predecessor) {
                        this.processor = predecessor;
                    }

                    @Override
                    public Iterator<ArcPointer<Double, Double>> outgoingArcs() {
                        return switch (Node.this) {
                            case A -> arcs(
                                arc(2, B),
                                arc(5, D));
                            case B -> arcs(
                                arc(1, A),
                                arc(4, F));
                            case C -> arcs(
                                arc(3, B));
                            case D -> arcs(
                                arc(8, C));
                            case E -> arcs();
                            case F -> arcs(
                                arc(6, D),
                                arc(7, E));
                        };
                    }

                    private ArcPointer<Double, Double> arc(double length, Node dest) {
                        return new ArcPointer<>() {

                            private final NodePointer<Double, Double> destNode = dest.pointer();

                            @Override
                            public Double getLength() {
                                return length;
                            }

                            @Override
                            public NodePointer<Double, Double> destination() {
                                return destNode;
                            }
                        };
                    }

                    @SafeVarargs
                    public final Iterator<ArcPointer<Double, Double>> arcs(ArcPointer<Double, Double>... arcs) {
                        return List.of(arcs).iterator();
                    }
                };
            }
        }

        private final Dijkstra<Double, Double> dijkstra = new Dijkstra<>(CMP, Double::sum, PriorityQueueList::new);

        @Test
        void testInitialize() {
            dijkstra.initialize(Node.A.pointer());
        }
    }
}
