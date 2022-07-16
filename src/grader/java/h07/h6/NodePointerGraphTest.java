package h07.h6;

import h07.*;
import h07.provider.GraphToGraphPointerMapsProvider;
import h07.transformer.MethodInterceptor;
import kotlin.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.util.*;

import static h07.Assertions.*;
import static h07.TestConstants.MAX_NODE_DISTANCE;
import static h07.TestConstants.RANDOM;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestForSubmission("h07")
public class NodePointerGraphTest extends GraphPointerTest {

    private static final Pair<String, String> CONSTRUCTOR_DESCRIPTION =
        new Pair<>("[[[this]]]", "[[[new NodePointerGraph(existingNodePointers, existingArcPointers, newGraphNode)]]]");

    private static final Pair<String, String> MAP_DESCRIPTION =
        new Pair<>("[[[existingArcPointersMap]]] and [[[existingNodePointersMap]]]",
            "the fields and the methods [[[getLength()]]], [[[destination()]]] and [[[outgoingArcs()]]] of the values of the maps have been overwritten to return the expected values");

    @BeforeEach
    public void reset() {
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods(
            "^java/util/HashMap.+",
            "^java/util/Iterator.+",
            "^java/util/LinkedList.+",
            "^java/util/ArrayList.+");
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToGraphPointerMapsProvider.class)
    public void testConstructor(HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers,
                                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers) throws NoSuchFieldException, IllegalAccessException {

        GraphNode<Integer> newGraphNode = existingNodePointers.keySet().iterator().next();
        existingNodePointers.remove(newGraphNode);
        NodePointerGraph<Integer, Integer> actualNodePointer = new NodePointerGraph<>(existingNodePointers, existingArcPointers, newGraphNode);

        assertTrueTutor(getExistingNodePointersMap(actualNodePointer).containsKey(newGraphNode) && getExistingNodePointersMap(actualNodePointer).get(newGraphNode).equals(actualNodePointer),
            () -> new AssertionMessage("the created [[[nodePointer]]] wasn't added to the [[[existingNodePointersMap]]] after invoking the constructor",
                List.of(CONSTRUCTOR_DESCRIPTION)));

        assertNodePointerGraphEquals(existingNodePointers, existingArcPointers, newGraphNode, null, null, actualNodePointer,
            () -> new AssertionMessage("the [[[nodePointer]]] created by the constructor does not have the expected properties",
                List.of(CONSTRUCTOR_DESCRIPTION)));
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToGraphPointerMapsProvider.class)
    public void testDistance(HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers,
                                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers) {

        NodePointerGraph<Integer, Integer> nodePointer = new NodePointerGraph<>(existingNodePointers, existingArcPointers, existingNodePointers.keySet().iterator().next());
        int distance = RANDOM.nextInt(0, MAX_NODE_DISTANCE + 1);

        nodePointer.setDistance(distance);
        assertEqualsTutor(distance, nodePointer.getDistance(),
            () -> new AssertionMessage("[[[getDistance()]]] did not return the correct value",
            List.of(CONSTRUCTOR_DESCRIPTION,
                new Pair<>("[[[distance]]]", "The [[[distance]]] of the [[[nodePointer]]] has been set using [[[setDistance(%d)]]]".formatted(distance))))
        );
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToGraphPointerMapsProvider.class)
    public void testPredecessor(HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers,
                                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers) {

        Iterator<GraphNode<Integer>> nodeIterator = existingNodePointers.keySet().iterator();
        NodePointer<Integer, Integer> start = new NodePointerGraph<>(existingNodePointers, existingArcPointers, nodeIterator.next());
        NodePointer<Integer, Integer> predecessor = new NodePointerGraph<>(existingNodePointers, existingArcPointers, nodeIterator.next());

        start.setPredecessor(predecessor);
        assertEqualsTutor(predecessor, start.getPredecessor(),
            () -> new AssertionMessage("[[[getPredecessor()]]] did not return the correct value",
                List.of(CONSTRUCTOR_DESCRIPTION,
                    new Pair<>("[[[predecessor]]]", "The [[[predecessor]]] of the [[[nodePointer]]] has been set using [[[setPredecessor(%s)]]]".formatted(predecessor))))
        );
    }


    @ParameterizedTest
    @ArgumentsSource(GraphToGraphPointerMapsProvider.class)
    public void testOutgoingArcs(HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers,
                                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers) {

        GraphNode<Integer> newGraphNode = existingNodePointers.keySet().iterator().next();
        List<ArcPointerGraph<Integer, Integer>> expectedOutgoingArcs = newGraphNode.getOutgoingArcs().stream().map(existingArcPointers::get).toList();
        NodePointer<Integer, Integer> actualNodePointer = new NodePointerGraph<>(existingNodePointers, existingArcPointers, newGraphNode);

        //existingArcPointers contains all arcs
        List<ArcPointerGraph<Integer, Integer>> actualOutgoingArcs = arcPointerListToArcPointerGraphList(iteratorToList(actualNodePointer.outgoingArcs()));

        assertEqualsTutor(expectedOutgoingArcs.size(), actualOutgoingArcs.size(),
            () -> new AssertionMessage("[[[outgoingArcs()]]] did not return the correct amount of arcs",
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new Pair<>("[[[newGraphNode]]]", "a [[[GraphNode]]] whose outgoing arcs are keys of the [[[existingArcPointers]]] map")))
        );

        assertListContainsAllWithPredicate(expectedOutgoingArcs, actualOutgoingArcs,
            (ArcPointerGraph<Integer, Integer> expected, ArcPointerGraph<Integer, Integer> actual) -> expected == actual,
            "iterator returned by [[[outgoingArcs()]]]",
            () -> new AssertionMessage("[[[outgoingArcs()]]] did not return the correct arcs",
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new Pair<>("[[[newGraphNode]]]", "a [[[GraphNode]]] whose outgoing arcs are keys of the [[[existingArcPointers]]] map")))
        );

        //existingArcPointers does not contain all arcs
        for (GraphArc<Integer> arc : newGraphNode.getOutgoingArcs()) existingArcPointers.remove(arc);

        actualNodePointer = new NodePointerGraph<>(existingNodePointers, existingArcPointers, newGraphNode);
        actualOutgoingArcs = arcPointerListToArcPointerGraphList(iteratorToList(actualNodePointer.outgoingArcs()));

        assertEqualsTutor(expectedOutgoingArcs.size(), actualOutgoingArcs.size(),
            () -> new AssertionMessage("[[[outgoingArcs()]]] did not return the correct amount of arcs",
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new Pair<>("[[[newGraphNode]]]", "a [[[GraphNode]]] whose outgoing arcs are not keys of the [[[existingArcPointers]]] map")))
        );

        assertListContainsAllWithPredicate(expectedOutgoingArcs, actualOutgoingArcs, (ArcPointerGraph<Integer, Integer> expected, ArcPointerGraph<Integer, Integer> actual) -> {
            try {
                return getExistingNodePointersMap(expected) == getExistingNodePointersMap(actual) &&
                    getExistingArcPointersMap(expected) == getExistingArcPointersMap(actual) &&
                    getGraphArc(expected) == getGraphArc(actual);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                fail("could not read fields of class ArcPointerGraph"); //shouldn't happen
                return false;
            }
        }, "the list returned by [[[outgoingArcs()]]]",
            () -> new AssertionMessage("[[[outgoingArcs()]]] did not return the correct arcs",
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new Pair<>("[[[newGraphNode]]]", "a [[[GraphNode]]] whose outgoing arcs are not keys of the [[[existingArcPointers]]] map")))
        );
    }


    private List<ArcPointerGraph<Integer, Integer>> arcPointerListToArcPointerGraphList(List<ArcPointer<Integer, Integer>> outgoingArcs) {
        return outgoingArcs.stream()
            .map((ArcPointer<Integer, Integer> arcPointer) -> {
                    assertInstanceOf(ArcPointerGraph.class, arcPointer, "the elements returned by [[[outgoingArcs()]]] did not have the correct dynamic type");
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
