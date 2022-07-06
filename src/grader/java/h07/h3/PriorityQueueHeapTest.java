package h07.h3;

import h07.IllegalMethodsCheck;
import h07.PriorityQueueHeap;
import h07.implementation.QueueEntry;
import h07.provider.QueueEntryHeapProvider;
import h07.transformer.MethodInterceptor;
import kotlin.Pair;
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

    private static final String STANDARD_INITIALIZE_STRING =
        "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value %% 10), %d)]]], ".formatted(HEAP_CAPACITY) +
            "the [[[heap]]], [[[indexMap]]] and [[[size]]] have been modified to simulate %d calls to [[[add(T)]]], ".formatted(HEAP_CAPACITY / 2) +
            "the [[[priorityComparator]]] has been set manually";

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
            int finalI = i;

            assertIndexMapCorrect(getHeap(queue), getIndexMap(queue), nextElement, () -> new AssertionMessage(
                "[[[add(T)]]] did not have the expected effect after adding %d items".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value %% 10), %d)]]], ".formatted(HEAP_CAPACITY) +
                    "the [[[priorityComparator]]] has been set manually"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertArrayContains(getHeap(queue), nextElement, () -> new AssertionMessage(
                "[[[add(T)]]] did not have the expected after adding %d items".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value %% 10), %d)]]], ".formatted(HEAP_CAPACITY) +
                    "the [[[priorityComparator]]] has been set manually"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));
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
            int finalI = i;

            assertPriorityQueueCorrect(inserted, queue, () -> new AssertionMessage(
                "[[[add(T)]]] did not have the expected effect after adding %d items".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testDeleteSimple(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).collect(Collectors.toList());

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(getHeap(queue), entries.size());
            int finalI = i;
            entries.remove(nextElement);

            assertEqualsTutor(nextElement, queue.delete(nextElement), () -> new AssertionMessage(
                "[[[delete(T)]]] did not return the expected value after removing %d items".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertArrayDoesNotContains(getHeap(queue), nextElement, entries.size(), () -> new AssertionMessage(
                "[[[delete(T)]]] did not have the expected effect after removing %d items".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertFalseTutor(getIndexMap(queue).containsKey(nextElement), () -> new AssertionMessage(
                "[[[delete(T)]]] did not have the expected effect after removing %d items. The [[[indexMap]]] contains the removed item".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));
        }

    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testDeleteComplex(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        QueueEntry[] initialHeap = new QueueEntry[heap.length];
        System.arraycopy(heap, 0, initialHeap, 0, heap.length);

        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).collect(Collectors.toList());

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(getHeap(queue), entries.size());
            int finalI = i;
            entries.remove(nextElement);

            assertEqualsTutor(nextElement, queue.delete(nextElement), () -> new AssertionMessage(
                "[[[delete(T)]]] did not return the expected value after removing %d items".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPriorityQueueCorrect(entries, queue, () -> new AssertionMessage(
                "[[[delete(T)]]] did not have the expected effect after removing %d items".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));
        }

        queue = initializeQueue(initialHeap);
        entries = Arrays.stream(initialHeap).filter(Objects::nonNull).collect(Collectors.toList());
        assertNullTutor(queue.delete(QueueEntry.UNUSED_ENTRY), () -> new AssertionMessage(
            "[[[delete(T)]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]" , "[[[<unusedEntry>]]]"))
        ));

        assertPriorityQueueCorrect(entries, queue, () -> new AssertionMessage(
            "[[[delete(T)]]] did not have the expected effect when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]" , "[[[<unusedEntry>]]]"))
        ));

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

        assertEqualsTutor(toDelete, queue.delete(toDelete), () -> new AssertionMessage(
            "[[[delete(T)]]] did not return the expected value",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", toDelete.toString()))
        ));

        assertPriorityQueueCorrect(entries, queue, () -> new AssertionMessage(
            "[[[delete(T)]]] did not have the expected effect when an upwards correction inside the heap is required",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", toDelete.toString()))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testGetFront(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).collect(Collectors.toList());

        assertEqualsTutor(heap[0], queue.getFront(), () -> new AssertionMessage(
            "[[[getFront()]]] did not return the correct value",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));

        assertPriorityQueueCorrect(entries, queue, () -> new AssertionMessage(
            "[[[getFront(T)]]] did not have the expected effect",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));

        assertNullTutor(initializeQueue(new QueueEntry[HEAP_CAPACITY]).getFront(), () -> new AssertionMessage(
            "[[[getFront(T)]]] did not return the correct value when the queue is empty",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]], " +
                "the attributes have been set to simulate an empty heap. The priorityComparator has been set manually"))
        ));

    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testDeleteFront(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).collect(Collectors.toList());
        entries.remove(0);

        assertEqualsTutor(heap[0], queue.deleteFront(), () -> new AssertionMessage(
            "[[[deleteFront()]]] did not return the correct value",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));

        assertPriorityQueueCorrect(entries, queue, () -> new AssertionMessage(
            "[[[deleteFront(T)]]] did not have the expected effect",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));

        assertNullTutor(initializeQueue(new QueueEntry[HEAP_CAPACITY]).deleteFront(), () -> new AssertionMessage(
            "[[[deleteFront()]]] did not return the correct value when the queue is empty",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]], " +
                "the attributes have been set to simulate an empty heap, the priorityComparator has been set manually"))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testGetPosition(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).toList();

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(heap, entries.size());

            assertPositionCorrect(getHeap(queue), nextElement, queue.getPosition(nextElement), entries.size(), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPriorityQueueCorrect(entries, queue, () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not have the expected effect",
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));
        }

        assertEqualsTutor(-1, queue.getPosition(QueueEntry.UNUSED_ENTRY), () -> new AssertionMessage(
            "[[[getPosition(T)]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", "[[[<unusedEntry>]]]"))
        ));

        assertPriorityQueueCorrect(entries, queue, () -> new AssertionMessage(
            "[[[getPosition(T)]]] did not have the expected effect when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", "[[[<unusedEntry>]]]"))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testContains(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);
        List<QueueEntry> entries = Arrays.stream(heap).filter(Objects::nonNull).toList();

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(heap, entries.size());

            assertTrueTutor(queue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));
        }

        assertFalseTutor(queue.contains(QueueEntry.UNUSED_ENTRY), () -> new AssertionMessage(
            "[[[contains(T)]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", "[[[<unusedEntry>]]]"))
        ));

        assertPriorityQueueCorrect(entries, queue, () -> new AssertionMessage(
            "[[[contains(T)]]] did not have the expected effect",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", "[[[<unusedEntry>]]]"))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testClear(QueueEntry[] heap) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(heap);

        queue.clear();

        assertEqualsTutor(0, getSize(queue), () -> new AssertionMessage(
            "the attribute [[[size]]] does not have the correct value after [[[clear()]]] was called",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));

        assertEqualsTutor(0, getIndexMap(queue).size(), () -> new AssertionMessage(
            "the attribute [[[indexMap]]] does not contain the correct amount of items after [[[clear()]]] was called",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));
    }

    @Test
    public void testAll() throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = new PriorityQueueHeap<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);

        assertFalseTutor(queue.contains(QueueEntry.UNUSED_ENTRY), () -> new AssertionMessage(
            "[[[contains(T)]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                new Pair<>("Argument #1 - [[[item]]]", "[[[unusedEntry]]]"))
        ));

        assertEqualsTutor(-1, queue.getPosition(QueueEntry.UNUSED_ENTRY), () -> new AssertionMessage(
            "[[[getPosition(T)]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                new Pair<>("Argument #1 - [[[item]]]", "[[[<unusedEntry>]]]"))
        ));

        assertNullTutor(queue.getFront(), () -> new AssertionMessage(
            "[[[getFront()]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"))
        ));

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

            assertFalseTutor(queue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value when called with an item that was not in the queue",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertEqualsTutor(-1, queue.getPosition(nextElement), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value when called with an item that was not in the queue",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            queue.add(nextElement);
            inserted.add(nextElement);

            assertTrueTutor(queue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPositionCorrect(getHeap(queue), nextElement, queue.getPosition(nextElement), inserted.size(), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPriorityQueueCorrect(inserted, queue, () -> new AssertionMessage(
                "[[[add(T)]]] did not have the expect effect",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))));
        }

        return inserted;
    }

    private void testDeleteAll(PriorityQueueHeap<QueueEntry> queue, List<QueueEntry> inserted) throws NoSuchFieldException, IllegalAccessException {
        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = inserted.remove(RANDOM.nextInt(inserted.size()));

            assertTrueTutor(queue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            if (queue.getPosition(nextElement) == 23) {
                System.out.println("a");
            }

            assertPositionCorrect(getHeap(queue), nextElement, queue.getPosition(nextElement), inserted.size(), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertEqualsTutor(nextElement, queue.delete(nextElement), () -> new AssertionMessage(
                "[[[delete(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertFalseTutor(queue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value when called with an item that was removed from the queue",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertEqualsTutor(-1, queue.getPosition(nextElement), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value when called with an item that was removed from the queue",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPriorityQueueCorrect(inserted, queue, () -> new AssertionMessage(
                "[[[delete(T)]]] did not have the expect effect",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))));
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
