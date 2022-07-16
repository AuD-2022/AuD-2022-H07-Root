package h07.h4;

import h07.Dijkstra;
import h07.IPriorityQueue;
import h07.IllegalMethodsCheck;
import h07.NodePointer;
import h07.implementation.*;
import h07.provider.GraphToNodePointerImplProvider;
import h07.transformer.MethodInterceptor;
import kotlin.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static h07.TestConstants.MAX_NODE_COUNT;
import static h07.TestConstants.RANDOM;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static h07.Assertions.*;

@TestForSubmission("h07")
public class DijkstraTest {

    private static final String CONSTRUCTOR_DESCRIPTION = "[[[new Dijkstra(Comparator.reverseOrder(), " +
        "(Integer a, Integer b) -> a + b, " +
        "cmp -> <reference implementation>(cmp)]]]";


    public static final Function<Comparator<NodePointer<Integer, Integer>>, IPriorityQueue<NodePointer<Integer, Integer>>> QUEUE_FACTORY = comparator -> {
        queueFactoryInvoked = true;
        queueFactoryInvokedWith = comparator;
        IPriorityQueue<NodePointer<Integer, Integer>> toReturn = new PriorityQueueHeapImpl<>(comparator, MAX_NODE_COUNT);
        usedQueue = toReturn;
        return toReturn;
    };

    public static final BiFunction<Integer, Integer, Integer> DISTANCE_FUNCTION = (Integer a, Integer b) -> {
        if (a == null || b == null) failTutor(new AssertionMessage("a value passed to the distance function was null",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION))));
        return a + b;
    };

    public static final Comparator<Integer> NODE_CMP = (i1, i2) -> {
        if (i1 == null || i2 == null) failTutor(new AssertionMessage("a value passed to the comparator was null",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION))));
        return Comparator.<Integer>reverseOrder().compare(i1, i2);
    };

    private static IPriorityQueue<NodePointer<Integer, Integer>> usedQueue = null;
    private static boolean queueFactoryInvoked = false;
    private static Comparator<NodePointer<Integer, Integer>> queueFactoryInvokedWith = null;

    @BeforeEach
    public void reset() {
        NodePointerImpl.resetIds();
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods(
            "^java/util/Comparator.+",
            "^java/util/Iterator.+",
            "^java/util/LinkedList.+",
            "^java/util/ArrayList.+",
            "^java/util/BiFunction.+",
            "^java/util/Function.+",
            "^java/util/Predicate.+");
    }

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        queueFactoryInvoked = false;
        Dijkstra<Integer, Integer> instance = new Dijkstra<>(NODE_CMP, DISTANCE_FUNCTION, QUEUE_FACTORY);

        assertTrue(queueFactoryInvoked, "the [[[queueFactory]]] wasn't used to create the queue");
        assertQueueFactoryComparatorCorrect(queueFactoryInvokedWith);

        assertSame(NODE_CMP, getComparator(instance), "the attribute [[[comparator]]] does not have the correct value");
        assertSame(DISTANCE_FUNCTION, getDistanceFunction(instance), "the attribute [[[distanceFunction]]] does not have the correct value");
        assertSame(usedQueue, getPriorityQueue(instance), "the attribute [[priorityQueue]]] does not have the correct value");
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToNodePointerImplProvider.class)
    public void testInitialize(List<NodePointerImpl> nodePointers) throws NoSuchFieldException, IllegalAccessException {

        Dijkstra<Integer, Integer> dijkstra = createInstance();

        //item to be cleared
        getPriorityQueue(dijkstra).add(new NodePointerImpl(10).setName("otherNode"));

        NodePointerImpl startNode = nodePointers.get(0).setName("startNode");
        startNode.setDistance(10);

        dijkstra.initialize(startNode);

        assertEqualsTutor(1, getPriorityQueue(dijkstra).size(), () -> new AssertionMessage(
            "the [[[priorityQueue]]] does not contain the correct amount of items after calling [[[initialize(startNode)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". One other node has been added to the [[[queue]]] before calling the method"),
                new Pair<>("Argument #1 - [[[startNode]]]", startNode.toString()))
        ));

        assertTrueTutor(getPriorityQueue(dijkstra).contains(startNode), () -> new AssertionMessage(
            "the priorityQueue does not contain the [[[startNode]]] after calling [[[initialize(startNode)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". One other node has been added to the [[[queue]]] before calling the method"),
                new Pair<>("Argument #1 - [[[startNode]]]", startNode.toString()))
        ), false);
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToNodePointerImplProvider.class)
    public void testInitializeWithPredicate(List<NodePointerImpl> nodePointers) throws NoSuchFieldException, IllegalAccessException {

        Dijkstra<Integer, Integer> dijkstra = createInstance();

        //item to be cleared
        getPriorityQueue(dijkstra).add(new NodePointerImpl(10).setName("otherNode"));

        NodePointerImpl startNode = nodePointers.get(0).setName("startNode");
        startNode.setDistance(10);

        Predicate<NodePointer<Integer, Integer>> predicate = node -> node == startNode;

        dijkstra.initialize(startNode, predicate);

        assertEqualsTutor(1, getPriorityQueue(dijkstra).size(), () -> new AssertionMessage(
            "the [[[priorityQueue]]] does not contain the correct amount of items after calling [[[initialize(startNode, node -> node == startNode)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". One other node has been added to the [[[queue]]] before calling the method"),
                new Pair<>("Argument #1 - [[[startNode]]]", startNode.toString()))
        ));

        assertTrueTutor(getPriorityQueue(dijkstra).contains(startNode), () -> new AssertionMessage(
            "the priorityQueue does not contain the [[[startNode]]] after calling [[[initialize(startNode, node -> node == startNode)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". One other node has been added to the [[[queue]]] before calling the method"),
                new Pair<>("Argument #1 - [[[startNode]]]", startNode.toString()))
        ), false);

        assertSameTutor(predicate, getPredicate(dijkstra), () -> new AssertionMessage(
            "the attribute [[[predicate]]] does not contain the correct value after calling [[[initialize(startNode, node -> node == startNode)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". One other node has been added to the [[[queue]]] before calling the method"),
                new Pair<>("Argument #1 - [[[startNode]]]", startNode.toString()))
        ), false);
    }

    @Test
    public void testUnvisitedNode() throws NoSuchFieldException, IllegalAccessException {
        Dijkstra<Integer, Integer> dijkstra = createInstance();

        NodePointerImpl currentNode = new NodePointerImpl(5).setName("currentNode");
        NodePointerImpl unvisitedNode = new NodePointerImpl().setName("unvisitedNode");

        currentNode.addOutgoingArc(new ArcPointerImpl(10, unvisitedNode));

        //For assertion messages
        NodePointerImpl originalCurrentNode = currentNode.clone();
        NodePointerImpl originalUnvisitedNode = unvisitedNode.clone();


        getPriorityQueue(dijkstra).add(currentNode);

        dijkstra.expandNode(currentNode);

        assertSameTutor(currentNode, unvisitedNode.getPredecessor(), () -> new AssertionMessage(
            "the attribute predecessor of [[[unvisitedNode]]] does not have the correct value after calling [[[expandNode(NodePointer<L,D>)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". The [[[currentNode]]] has been added to the queue"),
                new Pair<>("Argument #1 - [[[currentNode]]]", originalCurrentNode.toString()),
                new Pair<>("[[[unvisitedNode]]]", originalUnvisitedNode.toString()))
        ));

        assertEqualsTutor(15, unvisitedNode.getDistance(), () -> new AssertionMessage(
            "the attribute distance of [[[unvisitedNode]]] does not have the correct value after calling [[[expandNode(NodePointer<L,D>)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". The [[[currentNode]]] has been added to the queue"),
                new Pair<>("Argument #1 - [[[currentNode]]]", originalCurrentNode.toString()),
                new Pair<>("[[[unvisitedNode]]]", originalUnvisitedNode.toString()))
        ));

        assertTrueTutor(getPriorityQueue(dijkstra).contains(unvisitedNode), () -> new AssertionMessage(
            "[[[unvisitedNode]]] wasn't added to the queue after calling [[[expandNode(NodePointer<L,D>)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". The [[[currentNode]]] has been added to the queue"),
                new Pair<>("Argument #1 - [[[currentNode]]]", originalCurrentNode.toString()),
                new Pair<>("[[[unvisitedNode]]]", originalUnvisitedNode.toString()))
        ));
    }

    @Test
    public void testUpdateVisitedNode() throws NoSuchFieldException, IllegalAccessException {
        //test update distance
        Dijkstra<Integer, Integer> dijkstra = createInstance();

        NodePointerImpl currentNode = new NodePointerImpl(5).setName("currentNode");
        NodePointerImpl visitedNode = new NodePointerImpl(20).setName("visitedNode");
        NodePointerImpl otherNode = new NodePointerImpl(15).setName("otherNode");
        NodePointerImpl unrelatedNode = new NodePointerImpl(18).setName("unrelatedNode");

        currentNode.addOutgoingArc(new ArcPointerImpl(10, visitedNode));
        visitedNode.setPredecessor(otherNode);
        otherNode.addOutgoingArc(new ArcPointerImpl(5, visitedNode));

        //For assertion messages
        NodePointerImpl originalCurrentNode = currentNode.clone();
        NodePointerImpl originalVisitedNode = visitedNode.clone();
        NodePointerImpl originalOtherNode = otherNode.clone();
        NodePointerImpl originalUnrelatedNode = unrelatedNode.clone();

        getPriorityQueue(dijkstra).add(visitedNode);
        getPriorityQueue(dijkstra).add(unrelatedNode);

        dijkstra.expandNode(currentNode);

        assertSameTutor(currentNode, visitedNode.getPredecessor(), () -> new AssertionMessage(
            "the attribute predecessor of [[[visitedNode]]] does not have the correct value after calling [[[expandNode(NodePointer<L,D>)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". The [[[visitedNode]]] and [[[unrelatedNode]]] have been added to the queue"),
                new Pair<>("Argument #1 - [[[currentNode]]]", originalCurrentNode.toString()),
                new Pair<>("[[[visitedNode]]]", originalVisitedNode.toString()),
                new Pair<>("[[[otherNode]]]", originalOtherNode.toString()),
                new Pair<>("[[[unrelatedNode]]]", originalUnrelatedNode.toString()))
        ));

        assertEqualsTutor(15, visitedNode.getDistance(), () -> new AssertionMessage(
            "the attribute distance of [[[visitedNode]]] does not have the correct value after calling [[[expandNode(NodePointer<L,D>)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". The [[[visitedNode]]] and [[[unrelatedNode]]] have been added to the queue"),
                new Pair<>("Argument #1 - [[[currentNode]]]", originalCurrentNode.toString()),
                new Pair<>("[[[visitedNode]]]", originalVisitedNode.toString()),
                new Pair<>("[[[otherNode]]]", originalOtherNode.toString()),
                new Pair<>("[[[unrelatedNode]]]", originalUnrelatedNode.toString()))
        ));

        assertTrueTutor(getPriorityQueue(dijkstra).contains(visitedNode), () -> new AssertionMessage(
            "the queue does not contain [[[visitedNode]]] after calling [[[expandNode(NodePointer<L,D>)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". The [[[visitedNode]]] and [[[unrelatedNode]]] have been added to the queue"),
                new Pair<>("Argument #1 - [[[currentNode]]]", originalCurrentNode.toString()),
                new Pair<>("[[[visitedNode]]]", originalVisitedNode.toString()),
                new Pair<>("[[[otherNode]]]", originalOtherNode.toString()),
                new Pair<>("[[[unrelatedNode]]]", originalUnrelatedNode.toString()))
        ));

        assertEqualsTutor(0, getPriorityQueue(dijkstra).getIndexMap().get(visitedNode), () -> new AssertionMessage(
            "the position of [[[visitedNode]]] in the queue hasn't been updated after calling [[[expandNode(NodePointer<L,D>)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". The [[[visitedNode]]] and [[[unrelatedNode]]] have been added to the queue"),
                new Pair<>("Argument #1 - [[[currentNode]]]", originalCurrentNode.toString()),
                new Pair<>("[[[visitedNode]]]", originalVisitedNode.toString()),
                new Pair<>("[[[otherNode]]]", originalOtherNode.toString()),
                new Pair<>("[[[unrelatedNode]]]", originalUnrelatedNode.toString()))
        ));

        //test do not update distance
        dijkstra = createInstance();

        currentNode = new NodePointerImpl(5);
        visitedNode = new NodePointerImpl(1);
        otherNode = new NodePointerImpl(0);

        currentNode.addOutgoingArc(new ArcPointerImpl(10, visitedNode));
        visitedNode.setPredecessor(otherNode);
        otherNode.addOutgoingArc(new ArcPointerImpl(1, visitedNode));

        getPriorityQueue(dijkstra).add(visitedNode);
        getPriorityQueue(dijkstra).add(currentNode);

        dijkstra.expandNode(currentNode);

        assertSameTutor(otherNode, visitedNode.getPredecessor(), () -> new AssertionMessage(
            "the attribute predecessor of [[[visitedNode]]] does not have the correct value after calling [[[expandNode(NodePointer<L,D>)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". The [[[visitedNode]]] has been added to the queue"),
                new Pair<>("Argument #1 - [[[currentNode]]]", originalCurrentNode.toString()),
                new Pair<>("[[[visitedNode]]]", originalVisitedNode.toString()),
                new Pair<>("[[[otherNode]]]", originalOtherNode.toString()))
        ));

        assertEqualsTutor(1, visitedNode.getDistance(), () -> new AssertionMessage(
            "the attribute distance of [[[visitedNode]]] does not have the correct value after calling [[[expandNode(NodePointer<L,D>)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". The [[[visitedNode]]] has been added to the queue"),
                new Pair<>("Argument #1 - [[[currentNode]]]", originalCurrentNode.toString()),
                new Pair<>("[[[visitedNode]]]", originalVisitedNode.toString()),
                new Pair<>("[[[otherNode]]]", originalOtherNode.toString()))
        ));

        assertTrueTutor(getPriorityQueue(dijkstra).contains(visitedNode), () -> new AssertionMessage(
            "the attribute distance of [[[visitedNode]]] does not have the correct value after calling [[[expandNode(NodePointer<L,D>)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + ". The [[[visitedNode]]] has been added to the queue"),
                new Pair<>("Argument #1 - [[[currentNode]]]", originalCurrentNode.toString()),
                new Pair<>("[[[visitedNode]]]", originalVisitedNode.toString()),
                new Pair<>("[[[otherNode]]]", originalOtherNode.toString()))
        ));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTerminate() throws NoSuchFieldException, IllegalAccessException {

        Dijkstra<Integer, Integer> dijkstra = createInstance();

        doReturn(true).when(dijkstra).finished(any(NodePointer.class));

        NodePointerImpl startNode = new NodePointerImpl();
        startNode.setDistance(10);
        getPriorityQueue(dijkstra).add(startNode);

        dijkstra.run();

        try {
            verify(dijkstra, never()).expandNode(any(NodePointer.class));
        } catch (MockitoAssertionError error) {
            fail("Expected no call to [[[expandNode(NodePointer<L,D>)]]] after calling [[[run()]]] when [[[finished((NodePointer<L, D>)]]] always returns false but received at least one");
        }
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @ArgumentsSource(GraphToNodePointerImplProvider.class)
    public void testWithoutPredicate(List<NodePointerImpl> nodePointers) throws NoSuchFieldException, IllegalAccessException {

        Dijkstra<Integer, Integer> dijkstra = createInstance();

        NodePointerImpl startNode = nodePointers.get(0);
        startNode.setDistance(10);

        HashMap<NodePointer<Integer, Integer>, Integer> expectedDistance = new HashMap<>();
        HashMap<NodePointer<Integer, Integer>, NodePointer<Integer, Integer>> expectedPredecessor = new HashMap<>();
        List<NodePointer<Integer, Integer>> expected = getExpectedResult(expectedDistance, expectedPredecessor, startNode, nodePointers, null);

        doAnswer(invocation -> invocation.getArgument(0) == null).when(dijkstra).finished(any(NodePointer.class));

        dijkstra.initialize(startNode);
        List<NodePointer<Integer, Integer>> actual = dijkstra.run();

        assertDijkstraResultEquals(expectedDistance, expectedPredecessor, expected, actual, startNode, () -> new AssertionMessage(
            "[[[run()]]] did not return the correct value after calling [[[initialize(startNode)]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION + "[[[finished((NodePointer<L, D>)]]] has been overwritten with a reference implementation"),
                new Pair<>("[[[startNode]]]", "a node of a graph with %d nodes. The [[[distance]]] of [[[startNode]]] is [[[10]]]".formatted(nodePointers.size())))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToNodePointerImplProvider.class)
    public void testDijkstra(List<NodePointerImpl> nodePointers) {

        Dijkstra<Integer, Integer> dijkstra = new Dijkstra<>(NODE_CMP, DISTANCE_FUNCTION, QUEUE_FACTORY);

        NodePointerImpl startNode = nodePointers.get(0);
        startNode.setDistance(10);
        NodePointerImpl endNode = nodePointers.get(RANDOM.nextInt(0, nodePointers.size()));

        Predicate<NodePointer<Integer, Integer>> predicate = node -> node == endNode;
        HashMap<NodePointer<Integer, Integer>, Integer> expectedDistance = new HashMap<>();
        HashMap<NodePointer<Integer, Integer>, NodePointer<Integer, Integer>> expectedPredecessor = new HashMap<>();
        List<NodePointer<Integer, Integer>> expected = getExpectedResult(expectedDistance, expectedPredecessor, startNode, nodePointers, predicate);

        dijkstra.initialize(startNode, predicate);
        List<NodePointer<Integer, Integer>> actual = dijkstra.run();

        assertDijkstraResultEquals(expectedDistance, expectedPredecessor, expected, actual, startNode, () -> new AssertionMessage(
            "[[[run()]]] did not return the correct value after calling [[[initialize(startNode, node -> node == endNode))]]]",
            List.of(new Pair<>("[[[this]]]", CONSTRUCTOR_DESCRIPTION),
                new Pair<>("[[[startNode]]] and [[[endNode]]]", "nodes of a graph with %d nodes. The [[[distance]]] of [[[startNode]]] is [[[10]]]"
                    .formatted(nodePointers.size())))
        ));
    }

    private Dijkstra<Integer, Integer> createInstance() throws NoSuchFieldException, IllegalAccessException {
        Dijkstra<Integer, Integer> dijkstra = spy(new Dijkstra<>(NODE_CMP, DISTANCE_FUNCTION, QUEUE_FACTORY));

        Field queue = Dijkstra.class.getDeclaredField("queue");
        queue.setAccessible(true);
        queue.set(dijkstra, QUEUE_FACTORY.apply((o1, o2) -> NODE_CMP.compare(o1.getDistance(), o2.getDistance())));

        return dijkstra;
}

    private List<NodePointer<Integer, Integer>> getExpectedResult(HashMap<NodePointer<Integer, Integer>, Integer> expectedDistance,
                                                                  HashMap<NodePointer<Integer, Integer>, NodePointer<Integer, Integer>> expectedPredecessor,
                                                                  NodePointerImpl startNode,
                                                                  List<NodePointerImpl> nodePointers,
                                                                  Predicate<NodePointer<Integer, Integer>> predicate) {
        //get expected solution
        DijkstraImpl solution = new DijkstraImpl(NODE_CMP, DISTANCE_FUNCTION, QUEUE_FACTORY);
        if (predicate == null) solution.initialize(startNode);
        else solution.initialize(startNode, predicate);
        List<NodePointer<Integer, Integer>> expected = solution.run();

        //save expected solution and reset nodes
        for (NodePointerImpl node : nodePointers) {
            expectedDistance.put(node, node.getDistance());
            expectedPredecessor.put(node, node.getPredecessor());
            node.reset();
        }

        startNode.setDistance(10);

        return expected;
    }

    @SuppressWarnings("unchecked")
    private BiFunction<Integer, Integer, Integer> getDistanceFunction(Dijkstra<Integer, Integer> dijkstra) throws IllegalAccessException, NoSuchFieldException {
        Field distanceFunction = Dijkstra.class.getDeclaredField("distanceFunction");
        distanceFunction.setAccessible(true);
        return (BiFunction<Integer, Integer, Integer>) distanceFunction.get(dijkstra);
    }

    @SuppressWarnings("unchecked")
    private Comparator<Integer> getComparator(Dijkstra<Integer, Integer> dijkstra) throws IllegalAccessException, NoSuchFieldException {
        Field comparator = Dijkstra.class.getDeclaredField("comparator");
        comparator.setAccessible(true);
        return (Comparator<Integer>) comparator.get(dijkstra);
    }

    @SuppressWarnings("unchecked")
    private PriorityQueueHeapImpl<NodePointer<Integer, Integer>> getPriorityQueue(Dijkstra<Integer, Integer> dijkstra) throws IllegalAccessException, NoSuchFieldException {
        Field queue = Dijkstra.class.getDeclaredField("queue");
        queue.setAccessible(true);
        return (PriorityQueueHeapImpl<NodePointer<Integer, Integer>>) queue.get(dijkstra);
    }

    @SuppressWarnings("unchecked")
    private Predicate<NodePointer<Integer, Integer>> getPredicate(Dijkstra<Integer, Integer> dijkstra) throws IllegalAccessException, NoSuchFieldException {
        Field predicate = Dijkstra.class.getDeclaredField("predicate");
        predicate.setAccessible(true);
        return (Predicate<NodePointer<Integer, Integer>>) predicate.get(dijkstra);
    }

}
