package h07.h7;

import h07.*;
import h07.provider.GraphToAdjacencyMatrixPointerProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.util.*;
import java.util.stream.Collectors;

import static h07.Assertions.*;
import static h07.TestConstants.MAX_NODE_DISTANCE;
import static h07.TestConstants.RANDOM;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestForSubmission("h07")
public class NodePointerAdjacencyMatrixTest extends AdjacencyMatrixPointerTest {

    private static final kotlin.Pair<String, String> CONSTRUCTOR_DESCRIPTION =
        new kotlin.Pair<>("[[[this]]]", "[[[new NodePointerAdjacencyMatrix(existingNodePointers, existingArcPointers, row)]]]");

    private static final kotlin.Pair<String, String> MAP_DESCRIPTION =
        new kotlin.Pair<>("[[[existingArcPointersMap]]] and [[[existingNodePointersMap]]]",
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
    @ArgumentsSource(GraphToAdjacencyMatrixPointerProvider.class)
    public void testConstructor(AdjacencyMatrix<Integer> adjacencyMatrix,
                                HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> existingNodePointers,
                                HashMap<Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> existingArcPointers) throws NoSuchFieldException, IllegalAccessException {

        Integer row = existingNodePointers.keySet().iterator().next();
        existingNodePointers.remove(row);
        NodePointerAdjacencyMatrix<Integer, Integer> actualNodePointer = new NodePointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, row);

        assertTrueTutor(getExistingNodePointersMap(actualNodePointer).containsKey(row) &&
                getExistingNodePointersMap(actualNodePointer).get(row).equals(actualNodePointer),
            () -> new AssertionMessage("the created [[[nodePointer]]] wasn't added to the [[[existingNodePointersMap]]] after invoking the constructor",
                List.of(CONSTRUCTOR_DESCRIPTION)));

        assertNodePointerAdjacencyMatrixEquals(existingNodePointers, existingArcPointers, adjacencyMatrix,
            null, null, row, actualNodePointer,
            () -> new AssertionMessage("the [[[nodePointer]]] created by the constructor does not have the expected properties",
                List.of(CONSTRUCTOR_DESCRIPTION)));
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToAdjacencyMatrixPointerProvider.class)
    public void testDistance(AdjacencyMatrix<Integer> adjacencyMatrix,
                                HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> existingNodePointers,
                                HashMap<Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> existingArcPointers) {

        NodePointerAdjacencyMatrix<Integer, Integer> newNodePointer = new NodePointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, 0);
        int distance = RANDOM.nextInt(0, MAX_NODE_DISTANCE + 1);

        newNodePointer.setDistance(distance);
        assertEqualsTutor(distance, newNodePointer.getDistance(),
            () -> new AssertionMessage("[[[getDistance()]]] did not return the correct value",
                List.of(CONSTRUCTOR_DESCRIPTION,
                    new kotlin.Pair<>("[[[distance]]]", "The [[[distance]]] of the [[[nodePointer]]] has been set using [[[setDistance(%d)]]]".formatted(distance))))
        );
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToAdjacencyMatrixPointerProvider.class)
    public void testPredecessor(AdjacencyMatrix<Integer> adjacencyMatrix,
                                HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> existingNodePointers,
                                HashMap<Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> existingArcPointers) {

        Iterator<Integer> nodeIterator = existingNodePointers.keySet().iterator();
        NodePointer<Integer, Integer> start = new NodePointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, nodeIterator.next());
        NodePointer<Integer, Integer> predecessor = new NodePointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, nodeIterator.next());

        start.setPredecessor(predecessor);
        assertEqualsTutor(predecessor, start.getPredecessor(),
            () -> new AssertionMessage("[[[getPredecessor()]]] did not return the correct value",
            List.of(CONSTRUCTOR_DESCRIPTION,
                new kotlin.Pair<>("[[[predecessor]]]", "The [[[predecessor]]] of the [[[nodePointer]]] has been set using [[[setPredecessor(%s)]]]".formatted(predecessor))))
        );
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToAdjacencyMatrixPointerProvider.class)
    public void testOutgoingArcs(AdjacencyMatrix<Integer> adjacencyMatrix,
                                HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> existingNodePointers,
                                HashMap<Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> existingArcPointers) throws NoSuchFieldException, IllegalAccessException {

        List<ArcPointerAdjacencyMatrix<Integer, Integer>> expectedOutgoingArcs = getOutgoingArcs(adjacencyMatrix.getMatrix()[0]).stream()
            .map(destination -> existingArcPointers.get(new Pair<>(0, destination)))
            .collect(Collectors.toList());

        //existingArcsMap contains all arcs
        NodePointerAdjacencyMatrix<Integer, Integer> node = new NodePointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, 0);
        List<ArcPointerAdjacencyMatrix<Integer, Integer>> actualOutgoingArcs = arcPointerListToAdjacencyMatrixArcList(iteratorToList(node.outgoingArcs()));

        assertEqualsTutor(expectedOutgoingArcs.size(), actualOutgoingArcs.size(),
            () -> new AssertionMessage("[[[outgoingArcs()]]] did not return the correct amount of arcs",
        List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
            new kotlin.Pair<>("[[[row]]]", "the index of a node whose outgoing arcs are keys of the [[[existingArcPointers]]] map"))));


        assertListContainsAllWithPredicate(expectedOutgoingArcs, actualOutgoingArcs,
            (ArcPointerAdjacencyMatrix<Integer, Integer> expected, ArcPointerAdjacencyMatrix<Integer, Integer> actual) -> expected == actual,
            "the list returned by outgoingArcs()", () -> new AssertionMessage("[[[outgoingArcs()]]] did not return the correct arcs",
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new kotlin.Pair<>("[[[row]]]", "the index of a node whose outgoing arcs are keys of the [[[existingArcPointers]]] map")))
        );

        //existingArcsMap does not contain the arcs
        for (ArcPointerAdjacencyMatrix<Integer, Integer> outgoingArc : expectedOutgoingArcs) existingArcPointers.remove(new Pair<>(getRow(outgoingArc), getColumn(outgoingArc)));

        node = new NodePointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, 0);
        actualOutgoingArcs = arcPointerListToAdjacencyMatrixArcList(iteratorToList(node.outgoingArcs()));

        assertEqualsTutor(expectedOutgoingArcs.size(), actualOutgoingArcs.size(), () -> new AssertionMessage("[[[outgoingArcs()]]] did not return the correct amount of arcs",
            List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                new kotlin.Pair<>("[[[row]]]", "the index of a node whose outgoing arcs are not keys of the [[[existingArcPointers]]] map")))
        );

        assertListContainsAllWithPredicate(expectedOutgoingArcs, actualOutgoingArcs,
            (ArcPointerAdjacencyMatrix<Integer, Integer> expected, ArcPointerAdjacencyMatrix<Integer, Integer> actual) -> {
            try {
                return getExistingNodePointersMap(expected) == getExistingNodePointersMap(actual) &&
                    getExistingArcPointersMap(expected) == getExistingArcPointersMap(actual) &&
                    getAdjacencyMatrix(expected) == getAdjacencyMatrix(actual) &&
                    Objects.equals(getRow(expected), getRow(actual)) &&
                    Objects.equals(getColumn(expected), getColumn(actual));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                fail("could not read fields of class ArcPointerAdjacencyMatrix"); //shouldn't happen
                return false;
            }
        }, "the list returned by outgoingArcs()",
            () -> new AssertionMessage("[[[outgoingArcs()]]] did not return the correct arcs",
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new kotlin.Pair<>("[[[row]]]", "the index of a node whose outgoing arcs are not keys of the [[[existingArcPointers]]] map")))
        );
    }

    private List<Integer> getOutgoingArcs(Integer[] destinations) {

        List<Integer> outGoingArcs = new ArrayList<>();

        for (int i = 0; i < destinations.length; i++) {
            if (destinations[i] != null) outGoingArcs.add(i);
        }

        return outGoingArcs;
    }

    private List<ArcPointerAdjacencyMatrix<Integer, Integer>> arcPointerListToAdjacencyMatrixArcList(List<ArcPointer<Integer, Integer>> outgoingArcs) {
        return outgoingArcs.stream()
            .map((ArcPointer<Integer, Integer> arcPointer) -> {
                    assertInstanceOf(ArcPointerAdjacencyMatrix.class, arcPointer, "the elements returned by [[[outgoingArcs()]]] did not have the correct dynamic type");
                    return ((ArcPointerAdjacencyMatrix<Integer, Integer>) arcPointer);
                }
            ).collect(Collectors.toList());
    }

    private <T> List<T> iteratorToList(Iterator<T> iterator) {
        List<T> list = new ArrayList<>();
        while (iterator.hasNext()) list.add(iterator.next());
        return list;
    }

}

