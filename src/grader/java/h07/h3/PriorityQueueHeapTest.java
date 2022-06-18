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
import static h07.implementation.QueueEntry.CMP;
import static h07.implementation.QueueEntry.createRandomEntry;
import static h07.provider.AbstractProvider.RANDOM;
import static h07.provider.QueueEntryHeapProvider.HEAP_CAPACITY;
import static org.junit.jupiter.api.Assertions.*;

@TestForSubmission("h07")
public class PriorityQueueHeapTest {

    private static final int TEST_ITERATIONS = 5;

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
        PriorityQueueHeap<QueueEntry> queue = new PriorityQueueHeap<>(CMP, HEAP_CAPACITY);
        assertEquals(CMP, queue.getPriorityComparator());
        assertEquals(HEAP_CAPACITY, queue.getInternalHeap().length);
        assertEquals(HEAP_CAPACITY, Arrays.stream(queue.getInternalHeap()).filter(Objects::isNull).count());
        assertEquals(0, getIndexMap(queue).size());
    }

    @Test
    public void testAddSimple() throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = new PriorityQueueHeap<>(CMP, HEAP_CAPACITY);

        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = createRandomEntry();
            queue.add(nextElement);
            assertIndexMapCorrect(getHeap(queue), getIndexMap(queue), nextElement);
            assertArrayContains(getHeap(queue), nextElement, "the heap array does not contain the inserted element");
        }
    }

    @Test
    public void testAddComplex() throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = new PriorityQueueHeap<>(CMP, HEAP_CAPACITY);
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
            assertEquals(nextElement, queue.delete(nextElement));
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
            assertEquals(nextElement, queue.delete(nextElement));
            entries.remove(nextElement);
            assertPriorityQueueCorrect(entries, queue);
        }

        assertNull(queue.delete(QueueEntry.UNUSED_ENTRY));
        assertPriorityQueueCorrect(entries, queue);
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

        assertEquals(toDelete, queue.delete(toDelete));
        assertPriorityQueueCorrect(entries, queue);
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testGetFront(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        assertEquals(heap[0], queue.getFront());

        assertNull(initializeQueue(new QueueEntry[HEAP_CAPACITY]).getFront());

    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testDeleteFront(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).collect(Collectors.toList());

        assertEquals(heap[0], queue.deleteFront());
        entries.remove(0);
        assertPriorityQueueCorrect(entries, queue);

        assertNull(initializeQueue(new QueueEntry[HEAP_CAPACITY]).deleteFront());
        assertPriorityQueueCorrect(entries, queue);
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testGetPosition(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).toList();

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(heap, entries.size());
            assertPositionCorrect(entries, nextElement, queue.getPosition(nextElement) - 1);
        }

        assertEquals(-1, queue.getPosition(QueueEntry.UNUSED_ENTRY));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testContains(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).toList();

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(heap, entries.size());
            assertTrue(queue.contains(nextElement));
        }

        assertFalse(queue.contains(QueueEntry.UNUSED_ENTRY));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testClear(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);

        queue.clear();

        //assertHeapCorrect(getHeap(queue), new ArrayList<>());
        assertIndexMapCorrect(getHeap(queue), getIndexMap(queue), getSize(queue));
    }

    @Test
    public void testAll() throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = new PriorityQueueHeap<>(CMP, HEAP_CAPACITY);

        assertFalse(queue.contains(QueueEntry.UNUSED_ENTRY), "return value of contains not correct");
        assertEquals(-1, queue.getPosition(QueueEntry.UNUSED_ENTRY), "return value of getPosition not correct");
        assertNull(queue.getFront(), "return value of getFront not correct");

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
            assertFalse(queue.contains(nextElement), "return value of contains not correct");
            assertEquals(-1, queue.getPosition(nextElement), "return value of getPosition not correct");
            queue.add(nextElement);
            inserted.add(nextElement);
            assertTrue(queue.contains(nextElement), "return value of contains not correct");
            assertPositionCorrect(getHeap(queue), nextElement, queue.getPosition(nextElement) - 1, inserted.size());
            assertPriorityQueueCorrect(inserted, queue);
        }

        return inserted;
    }

    private void testDeleteAll(PriorityQueueHeap<QueueEntry> queue, List<QueueEntry> inserted) throws NoSuchFieldException, IllegalAccessException {
        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = inserted.remove(RANDOM.nextInt(inserted.size()));
            assertTrue(queue.contains(nextElement), "return value of contains not correct");
            assertPositionCorrect(getHeap(queue), nextElement, queue.getPosition(nextElement) - 1, inserted.size());
            queue.delete(nextElement);
            assertFalse(queue.contains(nextElement), "return value of contains not correct");
            assertEquals(-1, queue.getPosition(nextElement), "return value of getPosition not correct");
            assertPriorityQueueCorrect(inserted, queue);
        }
    }

    public PriorityQueueHeap<QueueEntry> initializeQueue(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = new PriorityQueueHeap<>(CMP, HEAP_CAPACITY);

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
