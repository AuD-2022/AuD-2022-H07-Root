package h07.h4;

import h07.Dijkstra;
import h07.IPriorityQueue;
import h07.NodePointer;
import h07.implementation.ArcPointerImpl;
import h07.implementation.DijkstraImpl;
import h07.implementation.NodePointerImpl;
import h07.implementation.PriorityQueueImpl;
import h07.provider.GraphToNodePointerImplProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static h07.provider.AbstractProvider.RANDOM;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static h07.Assertions.*;

@TestForSubmission("h07")
public class DijkstraTest {

    public static final Function<Comparator<NodePointer<Integer, Integer>>, IPriorityQueue<NodePointer<Integer, Integer>>> QUEUE_FACTORY = comparator -> {
        queueFactoryInvoked = true;
        queueFactoryInvokedWith = comparator;
        IPriorityQueue<NodePointer<Integer, Integer>> toReturn = new PriorityQueueImpl<>(comparator);
        usedQueue = toReturn;
        return toReturn;
    };
    public static final BiFunction<Integer, Integer, Integer> DISTANCE_FUNCTION = (Integer a, Integer b) -> a == null ? b : a + b;
    public static final Comparator<Integer> CMP = Comparator.naturalOrder();
    public static final Comparator<NodePointer<Integer, Integer>> NODE_POINTER_CMP = (o1, o2) -> CMP.compare(o1.getDistance(), o2.getDistance());

    private static IPriorityQueue<NodePointer<Integer, Integer>> usedQueue = null;
    private static boolean queueFactoryInvoked = false;
    private static Comparator<NodePointer<Integer, Integer>> queueFactoryInvokedWith = null;

    @BeforeEach
    public void reset() {
        NodePointerImpl.resetIds();
        MethodInterceptor.reset();
    }

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        queueFactoryInvoked = false;
        Dijkstra<Integer, Integer> instance = new Dijkstra<>(CMP, DISTANCE_FUNCTION, QUEUE_FACTORY);

        assertTrue(queueFactoryInvoked, "the queueFactory wasn't used to create the queue");
        assertQueueFactoryComparatorCorrect(queueFactoryInvokedWith);

        assertSame(CMP, getComparator(instance), "the attribute comparator does not have the correct value");
        assertSame(DISTANCE_FUNCTION, getDistanceFunction(instance), "the attribute distanceFunction does not have the correct value");
        assertSame(usedQueue, getPriorityQueue(instance), "the attribute queue does not have the correct value");
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToNodePointerImplProvider.class)
    public void testInitialize(List<NodePointerImpl> nodePointers) throws NoSuchFieldException, IllegalAccessException {

        Dijkstra<Integer, Integer> dijkstra = createInstance();

        getPriorityQueue(dijkstra).add(new NodePointerImpl());
        NodePointerImpl startNode = nodePointers.get(0);
        dijkstra.initialize(startNode);
        assertEquals(1, getPriorityQueue(dijkstra).queue.size(), "the priorityQueue does not contain the correct amount of items");
        assertTrue(getPriorityQueue(dijkstra).queue.contains(startNode), "the priorityQueue does not contain the startNode");
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToNodePointerImplProvider.class)
    public void testInitializeWithPredicate(List<NodePointerImpl> nodePointers) throws NoSuchFieldException, IllegalAccessException {

        Dijkstra<Integer, Integer> dijkstra = createInstance();

        getPriorityQueue(dijkstra).add(new NodePointerImpl());
        NodePointerImpl startNode = nodePointers.get(0);
        Predicate<NodePointer<Integer, Integer>> predicate = node -> node == startNode;
        dijkstra.initialize(startNode, predicate);
        assertEquals(1, getPriorityQueue(dijkstra).queue.size(), "the priorityQueue does not contain the correct amount of items");
        assertTrue(getPriorityQueue(dijkstra).contains(startNode), "the priorityQueue does not contain the startNode");
        assertSame(predicate, getPredicate(dijkstra), "the predicate attribute does not contain the correct value");
    }

    @Test
    public void testUnvisitedNode() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Dijkstra<Integer, Integer> dijkstra = new Dijkstra<>(CMP, DISTANCE_FUNCTION, QUEUE_FACTORY);

        NodePointerImpl startNode = new NodePointerImpl();
        startNode.setDistance(5);
        NodePointer<Integer, Integer> unvisitedNode = new NodePointerImpl();
        startNode.addOutgoingArc(new ArcPointerImpl(10, unvisitedNode));
        getPriorityQueue(dijkstra).add(startNode);

        Method method = Dijkstra.class.getDeclaredMethod("expandNode", NodePointer.class);
        method.setAccessible(true);
        method.invoke(dijkstra, startNode);

        assertSame(startNode, unvisitedNode.getPredecessor(), "the attribute predecessor of the visited node does not have the correct value");
        assertEquals(15, unvisitedNode.getDistance(), "the attribute distance of the visited node does not have the correct value");
        assertTrue(getPriorityQueue(dijkstra).contains(unvisitedNode), "the visited node wasn't added to the queue");
    }

    @Test
    public void testUpdateVisitedNode() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //test update distance
        Dijkstra<Integer, Integer> dijkstra = new Dijkstra<>(CMP, DISTANCE_FUNCTION, QUEUE_FACTORY);

        NodePointerImpl startNode = new NodePointerImpl();
        startNode.setDistance(5);
        NodePointer<Integer, Integer> visitedNode = new NodePointerImpl();
        startNode.addOutgoingArc(new ArcPointerImpl(10, visitedNode));
        NodePointerImpl otherNode = new NodePointerImpl();
        otherNode.setDistance(0);
        visitedNode.setPredecessor(otherNode);
        visitedNode.setDistance(20);

        getPriorityQueue(dijkstra).add(visitedNode);
        getPriorityQueue(dijkstra).add(startNode);


        Method method = Dijkstra.class.getDeclaredMethod("expandNode", NodePointer.class);
        method.setAccessible(true);
        method.invoke(dijkstra, startNode);

        assertSame(startNode, visitedNode.getPredecessor(), "the attribute predecessor of the visited node does not have the correct value");
        assertEquals(15, visitedNode.getDistance(), "the attribute distance of the visited node does not have the correct value");
        assertTrue(getPriorityQueue(dijkstra).contains(visitedNode), "the queue does not contain the visited node");

        //test do not update distance
        dijkstra = new Dijkstra<>(CMP, DISTANCE_FUNCTION, QUEUE_FACTORY);

        startNode = new NodePointerImpl();
        startNode.setDistance(5);
        visitedNode = new NodePointerImpl();
        startNode.addOutgoingArc(new ArcPointerImpl(10, visitedNode));
        otherNode = new NodePointerImpl();
        otherNode.setDistance(0);
        visitedNode.setPredecessor(otherNode);
        visitedNode.setDistance(1);

        getPriorityQueue(dijkstra).add(visitedNode);
        getPriorityQueue(dijkstra).add(startNode);

        method.invoke(dijkstra, startNode);

        assertSame(otherNode, visitedNode.getPredecessor(), "the attribute predecessor of the visited node does not have the correct value");
        assertEquals(1, visitedNode.getDistance(), "the attribute distance of the visited node does not have the correct value");
        assertTrue(getPriorityQueue(dijkstra).contains(visitedNode), "the queue does not contain the visited node");
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @ArgumentsSource(GraphToNodePointerImplProvider.class)
    public void testTerminate(List<NodePointerImpl> nodePointers) throws NoSuchFieldException, IllegalAccessException {

        Dijkstra<Integer, Integer> dijkstra = createInstance();

        when(dijkstra.finished(any(NodePointer.class))).thenReturn(true);
        dijkstra.initialize(nodePointers.get(0));
        dijkstra.run();
        verify(dijkstra, never().description("Expected no call to method expandNode but received at least one")).expandNode(any(NodePointer.class));
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @ArgumentsSource(GraphToNodePointerImplProvider.class)
    public void testWithoutPredicate(List<NodePointerImpl> nodePointers) throws NoSuchFieldException, IllegalAccessException {

        Dijkstra<Integer, Integer> dijkstra = createInstance();

        NodePointerImpl startNode = nodePointers.get(0);
        startNode.setDistance(0);

        HashMap<NodePointer<Integer, Integer>, Integer> expectedDistance = new HashMap<>();
        HashMap<NodePointer<Integer, Integer>, NodePointer<Integer, Integer>> expectedPredecessor = new HashMap<>();
        List<NodePointer<Integer, Integer>> expected = getExpectedResult(expectedDistance, expectedPredecessor, startNode, nodePointers, null);

        when(dijkstra.finished(any(NodePointer.class))).thenReturn(false);

        dijkstra.initialize(startNode);
        List<NodePointer<Integer, Integer>> actual = dijkstra.run();

        assertDijkstraResultEquals(expectedDistance, expectedPredecessor, expected, actual);
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToNodePointerImplProvider.class)
    public void testDijkstra(List<NodePointerImpl> nodePointers) throws NoSuchFieldException, IllegalAccessException {

        Dijkstra<Integer, Integer> dijkstra = createInstance();

        NodePointerImpl startNode = nodePointers.get(0);
        startNode.setDistance(0);
        NodePointerImpl endNode = nodePointers.get(RANDOM.nextInt(0, nodePointers.size()));

        Predicate<NodePointer<Integer, Integer>> predicate = node -> node == endNode;
        HashMap<NodePointer<Integer, Integer>, Integer> expectedDistance = new HashMap<>();
        HashMap<NodePointer<Integer, Integer>, NodePointer<Integer, Integer>> expectedPredecessor = new HashMap<>();
        List<NodePointer<Integer, Integer>> expected = getExpectedResult(expectedDistance, expectedPredecessor, startNode, nodePointers, predicate);

        dijkstra.initialize(startNode, predicate);
        List<NodePointer<Integer, Integer>> actual = dijkstra.run();

        assertDijkstraResultEquals(expectedDistance, expectedPredecessor, expected, actual);
    }

    private Dijkstra<Integer, Integer> createInstance() throws NoSuchFieldException, IllegalAccessException {
        Dijkstra<Integer, Integer> dijkstra = spy(new Dijkstra<>(CMP, DISTANCE_FUNCTION, QUEUE_FACTORY));

        Field queue = Dijkstra.class.getDeclaredField("queue");
        queue.setAccessible(true);
        queue.set(dijkstra, new PriorityQueueImpl<>(NODE_POINTER_CMP));

        Field comparator = Dijkstra.class.getDeclaredField("comparator");
        comparator.setAccessible(true);
        comparator.set(dijkstra, CMP);

        Field distanceFunction = Dijkstra.class.getDeclaredField("distanceFunction");
        distanceFunction.setAccessible(true);
        distanceFunction.set(dijkstra, DISTANCE_FUNCTION);

        return dijkstra;
}

    private List<NodePointer<Integer, Integer>> getExpectedResult(HashMap<NodePointer<Integer, Integer>, Integer> expectedDistance,
                                                                  HashMap<NodePointer<Integer, Integer>, NodePointer<Integer, Integer>> expectedPredecessor,
                                                                  NodePointerImpl startNode,
                                                                  List<NodePointerImpl> nodePointers,
                                                                  Predicate<NodePointer<Integer, Integer>> predicate) {
        //get expected solution
        DijkstraImpl solution = new DijkstraImpl(CMP, DISTANCE_FUNCTION, QUEUE_FACTORY);
        if (predicate == null) solution.initialize(startNode);
        else solution.initialize(startNode, predicate);
        List<NodePointer<Integer, Integer>> expected = solution.run();

        //save expected solution and reset nodes
        for (NodePointerImpl node : nodePointers) {
            expectedDistance.put(node, node.getDistance());
            expectedPredecessor.put(node, node.getPredecessor());
            node.reset();
        }
        startNode.setDistance(0);

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
    private PriorityQueueImpl<NodePointer<Integer, Integer>> getPriorityQueue(Dijkstra<Integer, Integer> dijkstra) throws IllegalAccessException, NoSuchFieldException {
        Field queue = Dijkstra.class.getDeclaredField("queue");
        queue.setAccessible(true);
        return (PriorityQueueImpl<NodePointer<Integer, Integer>>) queue.get(dijkstra);
    }

    @SuppressWarnings("unchecked")
    private Predicate<NodePointer<Integer, Integer>> getPredicate(Dijkstra<Integer, Integer> dijkstra) throws IllegalAccessException, NoSuchFieldException {
        Field predicate = Dijkstra.class.getDeclaredField("predicate");
        predicate.setAccessible(true);
        return (Predicate<NodePointer<Integer, Integer>>) predicate.get(dijkstra);
    }

}
