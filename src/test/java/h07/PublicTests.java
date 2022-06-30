package h07;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * An example JUnit test class.
 */
public class PublicTests {

    private static final Comparator<Integer> CMP = Integer::compare;

    public abstract static class IPriorityQueueTest<Q extends IPriorityQueue<Integer>> {

        protected Q queue;

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
            throw new RuntimeException("This does not make any god darn sense");
            // List<Object> iterable = new ArrayList<>();
            // Collections.addAll(iterable, queue.getInternalHeap());
            // iterable.removeIf(Objects::isNull);
            // return iterable;
        }
    }
}
