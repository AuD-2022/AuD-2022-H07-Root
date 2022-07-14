package h07.h3;

import h07.IllegalMethodsCheck;
import h07.PriorityQueueHeap;
import h07.implementation.PriorityQueueHeapImpl;
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

import static h07.Assertions.*;
import static h07.implementation.QueueEntry.QUEUE_ENTRY_CMP;
import static h07.implementation.QueueEntry.createRandomEntry;
import static h07.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@TestForSubmission("h07")
public class PriorityQueueHeapTest {

    private static final String STANDARD_INITIALIZE_STRING =
        "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value %% 10), %d)]]], ".formatted(HEAP_CAPACITY) +
            "the [[[heap]]], [[[indexMap]]] and [[[size]]] have been modified to simulate %d calls to [[[add(T)]]], ".formatted(HEAP_CAPACITY / 2);

    @BeforeEach
    public void reset() {
        QueueEntry.reset();
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods(
            "^java/util/Comparator.+",
            "^java/util/HashMap.+",
            "^java/util/Arrays.+"
        );
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
        PriorityQueueHeapImpl<QueueEntry> expectedQueue = new PriorityQueueHeapImpl<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(expectedQueue);

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
        PriorityQueueHeapImpl<QueueEntry> expectedQueue = new PriorityQueueHeapImpl<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);
        PriorityQueueHeap<QueueEntry> actualQueue = initializeQueue(expectedQueue);

        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = createRandomEntry();
            actualQueue.add(nextElement);
            expectedQueue.add(nextElement);
            int finalI = i;

            assertPriorityQueueCorrect(expectedQueue, actualQueue, () -> new AssertionMessage(
                "[[[add(T)]]] did not have the expected effect after adding %d items".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testDeleteSimple(PriorityQueueHeapImpl<QueueEntry> expectedQueue) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = initializeQueue(expectedQueue);

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(expectedQueue);
            int finalI = i;
            expectedQueue.delete(nextElement);

            assertEqualsTutor(nextElement, queue.delete(nextElement), () -> new AssertionMessage(
                "[[[delete(T)]]] did not return the expected value after removing %d items".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertArrayDoesNotContains(getHeap(queue), nextElement, expectedQueue.size(), () -> new AssertionMessage(
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
    public void testDeleteComplex(PriorityQueueHeapImpl<QueueEntry> expectedQueue) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> actualQueue = initializeQueue(expectedQueue);

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(expectedQueue);
            int finalI = i;
            expectedQueue.delete(nextElement);

            assertEqualsTutor(nextElement, actualQueue.delete(nextElement), () -> new AssertionMessage(
                "[[[delete(T)]]] did not return the expected value after removing %d items".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPriorityQueueCorrect(expectedQueue, actualQueue, () -> new AssertionMessage(
                "[[[delete(T)]]] did not have the expected effect after removing %d items".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));
        }

        actualQueue = initializeQueue(expectedQueue);

        assertNullTutor(actualQueue.delete(QueueEntry.UNUSED_ENTRY), () -> new AssertionMessage(
            "[[[delete(T)]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]" , "[[[<unusedEntry>]]]"))
        ));

        assertPriorityQueueCorrect(expectedQueue, actualQueue, () -> new AssertionMessage(
            "[[[delete(T)]]] did not have the expected effect when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]" , "[[[<unusedEntry>]]]"))
        ));
    }

    @Test
    public void testDeleteUpwardCorrectionEdgeCase() throws NoSuchFieldException, IllegalAccessException {
        //special edgeCase where upward correction is required
        PriorityQueueHeapImpl<QueueEntry> expectedQueue = new PriorityQueueHeapImpl<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);

        QueueEntry toDelete = new QueueEntry(2);
        expectedQueue.add(new QueueEntry(9));
        expectedQueue.add(new QueueEntry(3));
        expectedQueue.add(new QueueEntry(7));
        expectedQueue.add(toDelete);
        expectedQueue.add(new QueueEntry(1));
        expectedQueue.add(new QueueEntry(6));
        expectedQueue.add(new QueueEntry(5));

        PriorityQueueHeap<QueueEntry> actualQueue = initializeQueue(expectedQueue);

        expectedQueue.delete(toDelete);

        assertEqualsTutor(toDelete, actualQueue.delete(toDelete), () -> new AssertionMessage(
            "[[[delete(T)]]] did not return the expected value",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", toDelete.toString()))
        ));

        assertPriorityQueueCorrect(expectedQueue, actualQueue, () -> new AssertionMessage(
            "[[[delete(T)]]] did not have the expected effect when an upwards correction inside the [[[heap]]] is required",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", toDelete.toString()))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testGetFront(PriorityQueueHeapImpl<QueueEntry> expectedQueue) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> actualQueue = initializeQueue(expectedQueue);

        assertEqualsTutor(expectedQueue.getFront(), actualQueue.getFront(), () -> new AssertionMessage(
            "[[[getFront()]]] did not return the correct value",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));

        assertPriorityQueueCorrect(expectedQueue, actualQueue, () -> new AssertionMessage(
            "[[[getFront(T)]]] did not have the expected effect",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));

        assertNullTutor(createEmptyQueue().getFront(), () -> new AssertionMessage(
            "[[[getFront(T)]]] did not return the correct value when the queue is empty",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]], " +
                "the attributes have been set to simulate an empty heap. The priorityComparator has been set manually"))
        ));

    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testDeleteFront(PriorityQueueHeapImpl<QueueEntry> expectedQueue) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> actualQueue = initializeQueue(expectedQueue);



        assertEqualsTutor(expectedQueue.deleteFront(), actualQueue.deleteFront(), () -> new AssertionMessage(
            "[[[deleteFront()]]] did not return the correct value",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));

        assertPriorityQueueCorrect(expectedQueue, actualQueue, () -> new AssertionMessage(
            "[[[deleteFront(T)]]] did not have the expected effect",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));

        assertNullTutor(createEmptyQueue().deleteFront(), () -> new AssertionMessage(
            "[[[deleteFront()]]] did not return the correct value when the queue is empty",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]], " +
                "the attributes have been set to simulate an empty heap, the priorityComparator has been set manually"))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testGetPosition(PriorityQueueHeapImpl<QueueEntry> expectedQueue) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> actualQueue = initializeQueue(expectedQueue);

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(expectedQueue);

            assertPositionCorrect(getHeap(actualQueue), nextElement, actualQueue.getPosition(nextElement), expectedQueue.size(), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPriorityQueueCorrect(expectedQueue, actualQueue, () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not have the expected effect",
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));
        }

        assertEqualsTutor(-1, actualQueue.getPosition(QueueEntry.UNUSED_ENTRY), () -> new AssertionMessage(
            "[[[getPosition(T)]]] did not return the correct value when called with an item that was not in the actualQueue",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", "[[[<unusedEntry>]]]"))
        ));

        assertPriorityQueueCorrect(expectedQueue, actualQueue, () -> new AssertionMessage(
            "[[[getPosition(T)]]] did not have the expected effect when called with an item that was not in the actualQueue",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", "[[[<unusedEntry>]]]"))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testContains(PriorityQueueHeapImpl<QueueEntry> expectedQueue) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> actualQueue = initializeQueue(expectedQueue);

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(expectedQueue);

            assertTrueTutor(actualQueue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));
        }

        assertFalseTutor(actualQueue.contains(QueueEntry.UNUSED_ENTRY), () -> new AssertionMessage(
            "[[[contains(T)]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", "[[[<unusedEntry>]]]"))
        ));

        assertPriorityQueueCorrect(expectedQueue, actualQueue, () -> new AssertionMessage(
            "[[[contains(T)]]] did not have the expected effect",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", "[[[<unusedEntry>]]]"))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryHeapProvider.class)
    public void testClear(PriorityQueueHeapImpl<QueueEntry> expectedQueue) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> actualQueue = initializeQueue(expectedQueue);

        actualQueue.clear();

        assertEqualsTutor(0, getIndexMap(actualQueue).size(), () -> new AssertionMessage(
            "the attribute [[[indexMap]]] does not contain the correct amount of items after [[[clear()]]] was called",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));
    }

    @Test
    public void testAll() throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> actualQueue = new PriorityQueueHeap<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);

        assertFalseTutor(actualQueue.contains(QueueEntry.UNUSED_ENTRY), () -> new AssertionMessage(
            "[[[contains(T)]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                new Pair<>("Argument #1 - [[[item]]]", "[[[unusedEntry]]]"))
        ));

        assertEqualsTutor(-1, actualQueue.getPosition(QueueEntry.UNUSED_ENTRY), () -> new AssertionMessage(
            "[[[getPosition(T)]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                new Pair<>("Argument #1 - [[[item]]]", "[[[<unusedEntry>]]]"))
        ));

        assertNullTutor(actualQueue.getFront(), () -> new AssertionMessage(
            "[[[getFront()]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"))
        ));

        testDeleteAll(actualQueue, testAddAll(actualQueue));
        testAddAll(actualQueue);
        actualQueue.clear();
        testAddAll(actualQueue);

        testDeleteUpwardCorrectionEdgeCase();
    }

    private PriorityQueueHeapImpl<QueueEntry> testAddAll(PriorityQueueHeap<QueueEntry> actualQueue) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeapImpl<QueueEntry> expectedQueue = new PriorityQueueHeapImpl<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);

        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = QueueEntry.createRandomEntry();

            assertFalseTutor(actualQueue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value when called with an item that was not in the queue",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertEqualsTutor(-1, actualQueue.getPosition(nextElement), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value when called with an item that was not in the queue",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            actualQueue.add(nextElement);
            expectedQueue.add(nextElement);

            assertTrueTutor(actualQueue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPositionCorrect(getHeap(actualQueue), nextElement, actualQueue.getPosition(nextElement), expectedQueue.size(), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPriorityQueueCorrect(expectedQueue, actualQueue, () -> new AssertionMessage(
                "[[[add(T)]]] did not have the expect effect",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))));
        }

        return expectedQueue;
    }

    private void testDeleteAll(PriorityQueueHeap<QueueEntry> actualQueue, PriorityQueueHeapImpl<QueueEntry> expectedQueue) throws NoSuchFieldException, IllegalAccessException {
        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = getRandomElement(expectedQueue);

            assertTrueTutor(actualQueue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPositionCorrect(getHeap(actualQueue), nextElement, actualQueue.getPosition(nextElement), expectedQueue.size(), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertEqualsTutor(expectedQueue.delete(nextElement), actualQueue.delete(nextElement), () -> new AssertionMessage(
                "[[[delete(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertFalseTutor(actualQueue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value when called with an item that was removed from the queue",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertEqualsTutor(-1, actualQueue.getPosition(nextElement), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value when called with an item that was removed from the queue",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPriorityQueueCorrect(expectedQueue, actualQueue, () -> new AssertionMessage(
                "[[[delete(T)]]] did not have the expect effect",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueHeap<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))));
        }
    }

    public static PriorityQueueHeap<QueueEntry> initializeQueue(PriorityQueueHeapImpl<QueueEntry> expectedQueue) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = new PriorityQueueHeap<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);

        QueueEntry[] actualHeap = new QueueEntry[HEAP_CAPACITY];
        System.arraycopy(getHeap(expectedQueue), 0, actualHeap, 0, HEAP_CAPACITY);

        setHeap(queue, actualHeap);
        setSize(queue, expectedQueue.size());
        setIndexMap(queue, new HashMap<>(expectedQueue.getIndexMap()));

        return queue;
    }

    public static PriorityQueueHeap<QueueEntry> createEmptyQueue() throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueHeap<QueueEntry> queue = new PriorityQueueHeap<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);

        setHeap(queue, new QueueEntry[HEAP_CAPACITY]);
        setSize(queue, 0);
        setIndexMap(queue, new HashMap<>());

        return queue;
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

    public static QueueEntry[] getHeap(PriorityQueueHeapImpl<QueueEntry> queue) {
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

    public QueueEntry getRandomElement(PriorityQueueHeapImpl<QueueEntry> queue) {
        return (QueueEntry) queue.getInternalHeap()[RANDOM.nextInt(queue.size())];
    }

}
