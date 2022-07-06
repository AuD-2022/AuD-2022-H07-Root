package h07.h2;

import h07.IllegalMethodsCheck;
import h07.PriorityQueueList;
import h07.implementation.QueueEntry;
import h07.provider.QueueEntryListProvider;
import h07.transformer.MethodInterceptor;
import kotlin.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import static h07.Assertions.*;
import static h07.implementation.QueueEntry.QUEUE_ENTRY_CMP;
import static org.junit.jupiter.api.Assertions.*;
import static h07.TestConstants.*;

import java.lang.reflect.Field;
import java.util.*;

@TestForSubmission("h07")
public class PriorityQueueListTest {

    private static final String STANDARD_INITIALIZE_STRING = "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]], " +
        "%d items have been added to the [[[queue]]] to simulate calls to [[[add(T)]]], ".formatted(LIST_SIZE) +
        "the [[[priorityComparator]]] has been set manually";

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
        PriorityQueueList<QueueEntry> queue = new PriorityQueueList<>(QUEUE_ENTRY_CMP);

        assertEquals(QUEUE_ENTRY_CMP, queue.getPriorityComparator(), "the priorityComparator attribute does not have the correct value");
        assertInstanceOf(LinkedList.class, queue.getInternalList(), "the queue attribute does not have the correct dynamic type");
        assertEquals(0, queue.getInternalList().size(), "the queue is not empty");
    }

    @Test
    public void testAdd() throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue = initializeQueue(new LinkedList<>());

        List<QueueEntry> inserted = new LinkedList<>();

        for (int i = 0; i < LIST_SIZE; i++) {
            QueueEntry item = QueueEntry.createRandomEntry();
            queue.add(item);
            inserted.add(item);
            int finalI = i;
            assertPriorityListEquals(inserted, queue.getInternalList(), () -> new AssertionMessage(
                "[[[add(T)]]] did not have the expected effect after adding %d items".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]], " +
                    "[[[queue]]] has been set to an initially empty [[[LinkedList]]], " +
                        "the [[[priorityComparator]]] has been set manually"),
                    new Pair<>("Argument #1 - [[[item]]]", item.toString()))));
        }

    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryListProvider.class)
    public void testDelete(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        List<QueueEntry> originalList = new ArrayList<>(list);
        PriorityQueueList<QueueEntry> queue= initializeQueue(list);

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(list);
            list.remove(nextElement);

            int finalI = i;
            assertEqualsTutor(nextElement, queue.delete(nextElement), () -> new AssertionMessage(
                "[[[delete(T)]]] did not return the correct value after deleting %d items".formatted(finalI + 1),
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]" , nextElement.toString()))
            ));

            int finalI1 = i;
            assertPriorityListEquals(list, queue.getInternalList(), () -> new AssertionMessage(
                "[[[delete(T)]]] did not have the expected effect after deleting %d items".formatted(finalI1 + 1),
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                    new Pair<>("Argument #1 - [[[item]]]" , nextElement.toString()))
            ));
        }

        queue = initializeQueue(originalList);
        assertNullTutor(queue.delete(QueueEntry.UNUSED_ENTRY), () -> new AssertionMessage(
            "[[[delete(T)]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]" , "[[[<unusedEntry>]]]"))
        ));

        assertPriorityListEquals(originalList, queue.getInternalList(), () -> new AssertionMessage(
            "[[[delete(T)]]] did not have the expected effect when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]" , "[[[<unusedEntry>]]]"))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryListProvider.class)
    public void testGetFront(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue= initializeQueue(list);

        assertEqualsTutor(list.get(0), queue.getFront(), () -> new AssertionMessage(
            "[[[getFront()]]] did not return the correct value",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));

        assertPriorityListEquals(list, queue.getInternalList(), () -> new AssertionMessage(
            "[[[getFront(T)]]] did not have the expected effect",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));

        assertNullTutor(initializeQueue(new LinkedList<>()).getFront(), () -> new AssertionMessage(
            "[[[getFront(T)]]] did not return the correct value when the queue is empty",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]], " +
                "[[[queue]]] has been set to an empty [[[LinkedList]]], the priorityComparator has been set manually"))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryListProvider.class)
    public void testDeleteFront(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue= initializeQueue(list);

        assertEqualsTutor(list.remove(0), queue.deleteFront(), () -> new AssertionMessage(
            "[[[deleteFront()]]] did not return the correct value",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));

        assertPriorityListEquals(list, queue.getInternalList(), () -> new AssertionMessage(
            "[[[deleteFront()]]] did not have the expected effect",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));

        assertNullTutor(initializeQueue(new LinkedList<>()).deleteFront(), () -> new AssertionMessage(
            "[[[deleteFront()]]] did not return the correct value when the queue is empty",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]], " +
                "[[[queue]]] has been set to an empty [[[LinkedList]]], the priorityComparator has been set manually"))
            ));

    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryListProvider.class)
    public void testGetPosition(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue= initializeQueue(list);

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(list);

            assertPositionCorrect(list, nextElement, queue.getPosition(nextElement), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPriorityListEquals(list, queue.getInternalList(), () -> new AssertionMessage(
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

        assertPriorityListEquals(list, queue.getInternalList(), () -> new AssertionMessage(
            "[[[getPosition(T)]]] did not have the expected effect when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING),
                new Pair<>("Argument #1 - [[[item]]]", "[[[<unusedEntry>]]]"))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryListProvider.class)
    public void testContains(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue= initializeQueue(list);

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            QueueEntry nextElement = getRandomElement(list);

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

        assertPriorityListEquals(list, queue.getInternalList(), () -> new AssertionMessage(
            "[[[contains(T)]]] did not have the expected effect",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(QueueEntryListProvider.class)
    public void testClear(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue= initializeQueue(list);

        queue.clear();

        assertEqualsTutor(0, queue.getInternalList().size(), () -> new AssertionMessage(
            "the queue is not empty after [[[clear()]]] was called",
            List.of(new Pair<>("[[[this]]]", STANDARD_INITIALIZE_STRING))
        ));
    }

    @Test
    public void testAll() {
        PriorityQueueList<QueueEntry> queue = new PriorityQueueList<>(QUEUE_ENTRY_CMP);

        assertFalseTutor(queue.contains(QueueEntry.UNUSED_ENTRY), () -> new AssertionMessage(
            "[[[contains(T)]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                new Pair<>("Argument #1 - [[[item]]]", "[[[unusedEntry]]]"))
        ));

        assertEqualsTutor(-1, queue.getPosition(QueueEntry.UNUSED_ENTRY), () -> new AssertionMessage(
            "[[[getPosition(T)]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                new Pair<>("Argument #1 - [[[item]]]", "[[[<unusedEntry>]]]"))
        ));

        assertNullTutor(queue.getFront(), () -> new AssertionMessage(
            "[[[getFront()]]] did not return the correct value when called with an item that was not in the queue",
            List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"))
        ));

        testDeleteAll(queue, testAddAll(queue));
        testAddAll(queue);
        queue.clear();
        testAddAll(queue);
    }

    private List<QueueEntry> testAddAll(PriorityQueueList<QueueEntry> queue) {
        List<QueueEntry> inserted = new ArrayList<>();
        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = QueueEntry.createRandomEntry();

            assertFalseTutor(queue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value when called with an item that was not in the queue",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertEqualsTutor(-1, queue.getPosition(nextElement), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value when called with an item that was not in the queue",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            queue.add(nextElement);
            inserted.add(nextElement);

            assertTrueTutor(queue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPositionCorrect(queue.getInternalList(), nextElement, queue.getPosition(nextElement), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPriorityListEquals(inserted, queue.getInternalList(), () -> new AssertionMessage(
                "[[[add(T)]]] did not have the expect effect",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))));
        }

        return inserted;
    }

    private void testDeleteAll(PriorityQueueList<QueueEntry> queue, List<QueueEntry> inserted) {
        for (int i = 0; i < HEAP_CAPACITY; i++) {
            QueueEntry nextElement = inserted.remove(RANDOM.nextInt(inserted.size()));

            assertTrueTutor(queue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPositionCorrect(queue.getInternalList(), nextElement, queue.getPosition(nextElement), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertEqualsTutor(nextElement, queue.delete(nextElement), () -> new AssertionMessage(
                "[[[delete(T)]]] did not return the correct value",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertFalseTutor(queue.contains(nextElement), () -> new AssertionMessage(
                "[[[contains(T)]]] did not return the correct value when called with an item that was removed from the queue",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertEqualsTutor(-1, queue.getPosition(nextElement), () -> new AssertionMessage(
                "[[[getPosition(T)]]] did not return the correct value when called with an item that was removed from the queue",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))
            ));

            assertPriorityListEquals(inserted, queue.getInternalList(), () -> new AssertionMessage(
                "[[[delete(T)]]] did not have the expect effect",
                List.of(new Pair<>("[[[this]]]", "[[[new PriorityQueueList<>(Comparator.comparingInt(queueEntry -> queueEntry.value % 10))]]]"),
                    new Pair<>("Argument #1 - [[[item]]]", nextElement.toString()))));
        }
    }

    private void setQueueList(PriorityQueueList<QueueEntry> queue, List<QueueEntry> list) throws IllegalAccessException, NoSuchFieldException {
        Field queueList = PriorityQueueList.class.getDeclaredField("queue");
        queueList.setAccessible(true);
        queueList.set(queue, list);
    }

    private void setComparator(PriorityQueueList<QueueEntry> queue) throws IllegalAccessException, NoSuchFieldException {
        Field comparatorField = PriorityQueueList.class.getDeclaredField("priorityComparator");
        comparatorField.setAccessible(true);
        comparatorField.set(queue, QueueEntry.QUEUE_ENTRY_CMP);
    }

    private PriorityQueueList<QueueEntry> initializeQueue(List<QueueEntry> list) throws NoSuchFieldException, IllegalAccessException {
        PriorityQueueList<QueueEntry> queue = new PriorityQueueList<>(QUEUE_ENTRY_CMP);
        setQueueList(queue, new LinkedList<>(list));
        setComparator(queue);
        return queue;
    }

    private QueueEntry getRandomElement(List<QueueEntry> list) {
        return list.get(RANDOM.nextInt(list.size()));
    }

}
