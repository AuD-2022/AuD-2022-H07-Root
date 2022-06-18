package h07.h2;

import h07.IllegalMethodsCheck;
import h07.PriorityQueueList;
import h07.implementation.QueueEntry;
import h07.provider.QueueEntryListProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import static h07.Assertions.*;
import static h07.implementation.QueueEntry.CMP;
import static h07.provider.QueueEntryHeapProvider.HEAP_CAPACITY;
import static org.junit.jupiter.api.Assertions.*;
import static h07.provider.AbstractProvider.RANDOM;

import java.lang.reflect.Field;
import java.util.*;

@TestForSubmission("h07")
public class PriorityQueueListTest {

    private static final int TEST_ITERATIONS = 5;

    @BeforeEach
    public void reset() {
        QueueEntry.reset();
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods(
            "^java/util/LinkedList.+",
            "^java/util/Comparator.+",
            "^java/util/Iterator.+");
    }

    @Test
    public void testConstructor() {
        PriorityQueueList<QueueEntry> queue = new PriorityQueueList<>(CMP);

        assertEquals(CMP, queue.getPriorityComparator());
        assertInstanceOf(LinkedList.class, queue.getInternalList());
        assertEquals(0, queue.getInternalList().size());
    }

    @Test
    public void testAdd() throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue = new PriorityQueueList<>(CMP);
        setQueueList(queue, new LinkedList<>());

        List<QueueEntry> inserted = new LinkedList<>();

        for (int i = 0; i < QueueEntryListProvider.LIST_SIZE; i++) {
            QueueEntry item = QueueEntry.createRandomEntry();
            queue.add(item);
            inserted.add(item);
            assertPriorityListEquals(inserted, queue.getInternalList());
        }

    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryListProvider.class)
    public void testDelete(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue= initializeQueue(list);

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(list);
            list.remove(nextElement);
            assertEquals(nextElement, queue.delete(nextElement));
            assertPriorityListEquals(list, queue.getInternalList());
        }

        assertNull(queue.delete(QueueEntry.UNUSED_ENTRY));
        assertPriorityListEquals(list, queue.getInternalList());
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryListProvider.class)
    public void testGetFront(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue= initializeQueue(list);

        assertEquals(list.get(0), queue.getFront());
        assertNull(initializeQueue(new LinkedList<>()).deleteFront());
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryListProvider.class)
    public void testDeleteFront(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue= initializeQueue(list);

        assertEquals(list.remove(0), queue.deleteFront());
        assertPriorityListEquals(list, queue.getInternalList());

        assertNull(initializeQueue(new LinkedList<>()).deleteFront());
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryListProvider.class)
    public void testGetPosition(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue= initializeQueue(list);

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(list);

            assertPositionCorrect(list, nextElement, queue.getPosition(nextElement) - 1);
            assertPriorityListEquals(list, queue.getInternalList());
        }

        assertEquals(-1, queue.getPosition(QueueEntry.UNUSED_ENTRY));

    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryListProvider.class)
    public void testContains(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue= initializeQueue(list);

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            assertTrue(queue.contains(getRandomElement(list)));
        }

        assertFalse(queue.contains(QueueEntry.UNUSED_ENTRY));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryListProvider.class)
    public void testClear(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue= initializeQueue(list);

        queue.clear();
        assertEquals(0, queue.getInternalList().size());
    }

    @Test
    public void testAll() throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue = new PriorityQueueList<>(CMP);

        assertFalse(queue.contains(QueueEntry.UNUSED_ENTRY), "return value of contains not correct");
        assertEquals(-1, queue.getPosition(QueueEntry.UNUSED_ENTRY), "return value of getPosition not correct");
        assertNull(queue.getFront(), "return value of getFront not correct");

        testDeleteAll(queue, testAddAll(queue));
        testAddAll(queue);
        queue.clear();
        testAddAll(queue);
    }

    private List<QueueEntry> testAddAll(PriorityQueueList<QueueEntry> queue) {
        List<QueueEntry> inserted = new ArrayList<>();
        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = QueueEntry.createRandomEntry();
            assertFalse(queue.contains(nextElement), "return value of contains not correct");
            assertEquals(-1, queue.getPosition(nextElement), "return value of getPosition not correct");
            queue.add(nextElement);
            inserted.add(nextElement);
            assertTrue(queue.contains(nextElement), "return value of contains not correct");
            assertPositionCorrect(queue.getInternalList(), nextElement, queue.getPosition(nextElement) - 1);
            assertPriorityListEquals(inserted, queue.getInternalList());
        }

        return inserted;
    }

    private void testDeleteAll(PriorityQueueList<QueueEntry> queue, List<QueueEntry> inserted) throws NoSuchFieldException, IllegalAccessException {
        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = inserted.remove(RANDOM.nextInt(inserted.size()));
            assertTrue(queue.contains(nextElement), "return value of contains not correct");
            assertPositionCorrect(queue.getInternalList(), nextElement, queue.getPosition(nextElement) - 1);
            queue.delete(nextElement);
            assertFalse(queue.contains(nextElement), "return value of contains not correct");
            assertEquals(-1, queue.getPosition(nextElement), "return value of getPosition not correct");
            assertPriorityListEquals(inserted, queue.getInternalList());
        }
    }

    private void setQueueList(PriorityQueueList<QueueEntry> queue, List<QueueEntry> list) throws IllegalAccessException, NoSuchFieldException {
        Field queueList = PriorityQueueList.class.getDeclaredField("queue");
        queueList.setAccessible(true);
        queueList.set(queue, list);
    }

    private PriorityQueueList<QueueEntry> initializeQueue(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue = new PriorityQueueList<>(CMP);
        setQueueList(queue, new ArrayList<>(list));
        return queue;
    }

    private QueueEntry getRandomElement(List<QueueEntry> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }

}
