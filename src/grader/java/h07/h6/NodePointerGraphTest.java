package h07.h6;

import h07.*;
import h07.provider.GraphToGraphPointerMapsProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.util.*;

import static h07.Assertions.assertListContainsAllWithPredicate;
import static h07.Assertions.assertNodePointerGraphEquals;
import static h07.provider.GraphProvider.MAX_NODE_DISTANCE;
import static h07.provider.AbstractProvider.RANDOM;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestForSubmission("h07")
public class NodePointerGraphTest extends GraphPointerTest {

    @BeforeEach
    public void reset() {
        MethodInterceptor.reset();
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToGraphPointerMapsProvider.class)
    public void testConstructor(HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers,
                                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers) throws NoSuchFieldException, IllegalAccessException {

        GraphNode<Integer> nodeToAdd = existingNodePointers.keySet().iterator().next();
        existingNodePointers.remove(nodeToAdd);
        NodePointerGraph<Integer, Integer> actualNodePointer = new NodePointerGraph<>(existingNodePointers, existingArcPointers, nodeToAdd);

        assertTrue(getExistingNodePointersMap(actualNodePointer).containsKey(nodeToAdd) && getExistingNodePointersMap(actualNodePointer).get(nodeToAdd).equals(actualNodePointer),
            "the created nodePointer wasn't added to the existingNodePointersMap");

        assertNodePointerGraphEquals(existingNodePointers, existingArcPointers, nodeToAdd, null, null, actualNodePointer);
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToGraphPointerMapsProvider.class)
    public void testDistance(HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers,
                                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers) {

        NodePointerGraph<Integer, Integer> nodePointer = new NodePointerGraph<>(existingNodePointers, existingArcPointers, existingNodePointers.keySet().iterator().next());
        int distance = RANDOM.nextInt(0, MAX_NODE_DISTANCE + 1);

        nodePointer.setDistance(distance);
        assertEquals(distance, nodePointer.getDistance(), "the methode getDistance() did not return the correct value");
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToGraphPointerMapsProvider.class)
    public void testPredecessor(HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers,
                                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers) {

        Iterator<GraphNode<Integer>> nodeIterator = existingNodePointers.keySet().iterator();
        NodePointerGraph<Integer, Integer> start = new NodePointerGraph<>(existingNodePointers, existingArcPointers, nodeIterator.next());
        NodePointer<Integer, Integer> predecessor = new NodePointerGraph<>(existingNodePointers, existingArcPointers, nodeIterator.next());

        start.setPredecessor(predecessor);
        assertEquals(predecessor, start.getPredecessor(), "the methode getPredecessor() did not return the correct value");
    }


    @ParameterizedTest
    @ArgumentsSource(GraphToGraphPointerMapsProvider.class)
    public void testOutgoingArcs(HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers,
                                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers) {

        GraphNode<Integer> node = existingNodePointers.keySet().iterator().next();
        List<ArcPointerGraph<Integer, Integer>> expectedOutgoingArcs = node.getOutgoingArcs().stream().map(existingArcPointers::get).toList();
        NodePointer<Integer, Integer> actualNodePointer = new NodePointerGraph<>(existingNodePointers, existingArcPointers, node);

        //existingArcPointers contains all arcs
        List<ArcPointerGraph<Integer, Integer>> actualOutgoingArcs = arcPointerListToArcPointerGraphList(iteratorToList(actualNodePointer.outgoingArcs()));
        assertEquals(expectedOutgoingArcs.size(), actualOutgoingArcs.size(), "the method outgoingArcs() did not return the correct amount of arcs if the existingArcPointersMap contains all arcs");
        assertListContainsAllWithPredicate(expectedOutgoingArcs, actualOutgoingArcs, (ArcPointerGraph<Integer, Integer> expected, ArcPointerGraph<Integer, Integer> actual) -> expected == actual, "the method outgoingArcs() did not return the correct elements if the existingArcPointersMap contains all arcs");

        //existingArcPointers does not contain all arcs
        for (GraphArc<Integer> arc : node.getOutgoingArcs()) existingArcPointers.remove(arc);

        actualNodePointer = new NodePointerGraph<>(existingNodePointers, existingArcPointers, node);
        actualOutgoingArcs = arcPointerListToArcPointerGraphList(iteratorToList(actualNodePointer.outgoingArcs()));
        assertEquals(expectedOutgoingArcs.size(), actualOutgoingArcs.size(), "The method outgoingArcs() did not return the correct amount of outgoing arcs if the existingArcPointersMap does not contain all elements");
        assertListContainsAllWithPredicate(expectedOutgoingArcs, actualOutgoingArcs, (ArcPointerGraph<Integer, Integer> expected, ArcPointerGraph<Integer, Integer> actual) -> {
            try {
                return getExistingNodePointersMap(expected) == getExistingNodePointersMap(actual) &&
                    getExistingArcPointersMap(expected) == getExistingArcPointersMap(actual) &&
                    getGraphArc(expected) == getGraphArc(actual);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                fail("could not read fields of class ArcPointerGrapg"); //shouldn't happen
                return false;
            }
        }, "the method outgoingArcs() did not return the correct elements if the existingArcPointersMap does not contains all arcs");
    }


    private List<ArcPointerGraph<Integer, Integer>> arcPointerListToArcPointerGraphList(List<ArcPointer<Integer, Integer>> outgoingArcs) {
        return outgoingArcs.stream()
            .map((ArcPointer<Integer, Integer> arcPointer) -> {
                    assertInstanceOf(ArcPointerGraph.class, arcPointer, "the elements returned by the the outgoingArcs() method did not have the correct dynamic type");
                    return (ArcPointerGraph<Integer, Integer>) arcPointer;
                }
            ).toList();
    }

    private <T> List<T> iteratorToList(Iterator<T> iterator) {
        List<T> list = new ArrayList<>();
        while (iterator.hasNext()) list.add(iterator.next());
        return list;
    }


}
