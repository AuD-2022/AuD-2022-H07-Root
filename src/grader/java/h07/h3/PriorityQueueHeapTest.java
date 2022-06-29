package h07.h3;

import h07.IllegalMethodsCheck;
import h07.PriorityQueueHeap;
import h07.implementation.QueueEntry;
import h07.provider.QueueEntryHeapProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static h07.Assertions.*;
import static h07.implementation.QueueEntry.QUEUE_ENTRY_CMP;
import static h07.implementation.QueueEntry.createRandomEntry;
import static h07.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@TestForSubmission("h07")
public class PriorityQueueHeapTest {

    @BeforeEach
    public void reset() {
        QueueEntry.reset();
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods("^java/util/Comparator.+", "^java/util/HashMap.+");
    }

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = new PriorityQueueHeap<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);
        assertEquals(QUEUE_ENTRY_CMP, queue.getPriorityComparator(), "the priorityComparator attribute does not have the correct value");
        assertNotNull(queue.getInternalHeap(), "the heap array is null");
        assertEquals(HEAP_CAPACITY, queue.getInternalHeap().length, "the heap array does not have the correct value");
        assertEquals(HEAP_CAPACITY, Arrays.stream(queue.getInternalHeap()).filter(Objects::isNull).count(), "the heap array is not empty");
        assertNotNull(getIndexMap(queue), "the indexMap is null");
        assertEquals(0, getIndexMap(queue).size(), "the indexMap is not empty");
    }

    @Test
    public void testAddSimple() throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = new PriorityQueueHeap<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);

        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = createRandomEntry();
            queue.add(nextElement);
            assertIndexMapCorrect(getHeap(queue), getIndexMap(queue), nextElement);
            assertArrayContains(getHeap(queue), nextElement, "the heap array does not contain the inserted element");
        }
    }

    @Test
    public void testAddComplex() throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = new PriorityQueueHeap<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);
        List<QueueEntry> inserted = new LinkedList<>();

        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = createRandomEntry();
            queue.add(nextElement);
            inserted.add(nextElement);
            assertPriorityQueueCorrect(inserted, queue);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testDeleteSimple(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).collect(Collectors.toList());

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(getHeap(queue), entries.size());
            assertEquals(nextElement, queue.delete(nextElement), "the method delete(T) did not return the correct value");
            entries.remove(nextElement);
            assertArrayDoesNotContains(getHeap(queue), nextElement, "the heap array contains the removed element");
            assertFalse(getIndexMap(queue).containsKey(nextElement), "the indexMap contains an entry for the removed element");
        }

    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testDeleteComplex(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).collect(Collectors.toList());

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(getHeap(queue), entries.size());
            assertEquals(nextElement, queue.delete(nextElement), "the method delete(T) did not return the correct value");
            entries.remove(nextElement);
            assertPriorityQueueCorrect(entries, queue);
        }

        assertNull(queue.delete(QueueEntry.UNUSED_ENTRY), "the method delete(T) did not return the correct value");
        assertPriorityQueueCorrect(entries, queue);
        testDeleteUpwardCorrectionEdgeCase();
    }

    @Test
    public void testDeleteUpwardCorrectionEdgeCase() throws NoSuchFieldException, IllegalAccessException {
        //special edgeCase where upward correction is required
        QueueEntry toDelete = new QueueEntry(2);
        QueueEntry[] heap = new QueueEntry[]{new QueueEntry(9),
            new QueueEntry(3),
            new QueueEntry(7),
            toDelete,
            new QueueEntry(1),
            new QueueEntry(6),
            new QueueEntry(5)};

        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).collect(Collectors.toList());
        entries.remove(toDelete);

        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);

        assertEquals(toDelete, queue.delete(toDelete), "the method delete(T) did not return the correct value");
        assertPriorityQueueCorrect(entries, queue);
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testGetFront(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).collect(Collectors.toList());

        assertEquals(heap[0], queue.getFront(), "the method getFront() did not return the correct value");
        assertNull(initializeQueue(new QueueEntry[HEAP_CAPACITY]).getFront());
        assertPriorityQueueCorrect(entries, queue);
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testDeleteFront(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).collect(Collectors.toList());

        assertEquals(heap[0], queue.deleteFront(), "the method deleteFront() did not return the correct value");
        entries.remove(0);
        assertPriorityQueueCorrect(entries, queue);

        assertNull(initializeQueue(new QueueEntry[HEAP_CAPACITY]).deleteFront(), "the method deleteFront() did not return the correct value");
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testGetPosition(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).toList();

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(heap, entries.size());
            assertPositionCorrect(entries, nextElement, queue.getPosition(nextElement));
        }

        assertEquals(-1, queue.getPosition(QueueEntry.UNUSED_ENTRY), "the method getPosition(T) did not return the correct value");
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testContains(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).toList();

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(heap, entries.size());
            assertTrue(queue.contains(nextElement), "the method contains(T) did not return the correct value");
        }

        assertFalse(queue.contains(QueueEntry.UNUSED_ENTRY), "the method contains(T) did not return the correct value");
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testClear(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);

        queue.clear();

        assertEquals(0, getSize(queue), "the attribute size does not have the correct value");
        assertIndexMapCorrect(getHeap(queue), getIndexMap(queue), getSize(queue));
    }

    @Test
    public void testAll() throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = new PriorityQueueHeap<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);

        assertFalse(queue.contains(QueueEntry.UNUSED_ENTRY), "the method contains(T) did not return the correct value");
        assertEquals(-1, queue.getPosition(QueueEntry.UNUSED_ENTRY), "the method getPosition(T) did not return the correct value");
        assertNull(queue.getFront(), "the method getFront() did not return the correct value");

        testDeleteAll(queue, testAddAll(queue));
        testAddAll(queue);
        queue.clear();
        testAddAll(queue);

        testDeleteUpwardCorrectionEdgeCase();
    }

    private List<QueueEntry> testAddAll(PriorityQueueHeap<QueueEntry> queue) throws NoSuchFieldException, IllegalAccessException {
        List<QueueEntry> inserted = new ArrayList<>();
        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = QueueEntry.createRandomEntry();
            assertFalse(queue.contains(nextElement), "the method contains(T) did not return the correct value");
            assertEquals(-1, queue.getPosition(nextElement), "the method getPosition(T) did not return the correct value");
            queue.add(nextElement);
            inserted.add(nextElement);
            assertTrue(queue.contains(nextElement), "the method contains(T) did not return the correct value");
            assertPositionCorrect(getHeap(queue), nextElement, queue.getPosition(nextElement), inserted.size());
            assertPriorityQueueCorrect(inserted, queue);
        }

        return inserted;
    }

    private void testDeleteAll(PriorityQueueHeap<QueueEntry> queue, List<QueueEntry> inserted) throws NoSuchFieldException, IllegalAccessException {
        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = inserted.remove(RANDOM.nextInt(inserted.size()));
            assertTrue(queue.contains(nextElement), "the method contains(T) did not return the correct value");
            assertPositionCorrect(getHeap(queue), nextElement, queue.getPosition(nextElement), inserted.size());
            queue.delete(nextElement);
            assertFalse(queue.contains(nextElement), "the method contains(T) did not return the correct value");
            assertEquals(-1, queue.getPosition(nextElement), "the method getPosition(T) did not return the correct value");
            assertPriorityQueueCorrect(inserted, queue);
        }
    }

    public PriorityQueueHeap<QueueEntry> initializeQueue(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = new PriorityQueueHeap<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);

        setHeap(queue, heap);

        int elementCount = (int) Arrays.stream(heap).filter(Objects::nonNull).count();
        setSize(queue, elementCount);

        Map<QueueEntry, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < elementCount; i++) {
            indexMap.put(heap[i], i);
        }
        setIndexMap(queue, indexMap);

        return queue;
    }

    public static int getSize(PriorityQueueHeap<QueueEntry> queue) throws NoSuchFieldException, IllegalAccessException {
        Field field = PriorityQueueHeap.class.getDeclaredField("size");
        field.setAccessible(true);
        return (int) field.get(queue);
    }

    public static void setSize(PriorityQueueHeap<QueueEntry> queue, int size) throws NoSuchFieldException, IllegalAccessException {
        Field field = PriorityQueueHeap.class.getDeclaredField("size");
        field.setAccessible(true);
        field.set(queue, size);
    }

    @SuppressWarnings("unchecked")
    public static Map<QueueEntry, Integer> getIndexMap(PriorityQueueHeap<QueueEntry> queue) throws NoSuchFieldException, IllegalAccessException {
        Field field = PriorityQueueHeap.class.getDeclaredField("indexMap");
        field.setAccessible(true);
        return (Map<QueueEntry, Integer>) field.get(queue);
    }

    public static void setIndexMap(PriorityQueueHeap<QueueEntry> queue, Map<QueueEntry, Integer> indexMap) throws NoSuchFieldException, IllegalAccessException {
        Field field = PriorityQueueHeap.class.getDeclaredField("indexMap");
        field.setAccessible(true);
        field.set(queue, indexMap);
    }

    public static QueueEntry[] getHeap(PriorityQueueHeap<QueueEntry> queue) {
        return ObjectToQueueEntryArray(queue.getInternalHeap());
    }

    public static void setHeap(PriorityQueueHeap<QueueEntry> queue, QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        Field field = PriorityQueueHeap.class.getDeclaredField("heap");
        field.setAccessible(true);
        field.set(queue, heap);
    }

    public static QueueEntry[] ObjectToQueueEntryArray(Object[] array) {
        QueueEntry[] arr = new QueueEntry[array.length];
        for (int i = 0; i < array.length; i++) {
            arr[i] = (QueueEntry) array[i];
        }
        return arr;
    }

    public QueueEntry getRandomElement(QueueEntry[] heap, int size) {
        return heap[RANDOM.nextInt(size)];
    }

}
