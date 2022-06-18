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

import static h07.Assertions.assertListContainsAllWithPredicate;
import static h07.Assertions.assertNodePointerAdjacencyMatrixEquals;
import static h07.provider.GraphProvider.MAX_NODE_DISTANCE;
import static h07.provider.AbstractProvider.RANDOM;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestForSubmission("h07")
public class NodePointerAdjacencyMatrixTest extends AdjacencyMatrixPointerTest {

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

        Integer nodeToAdd = existingNodePointers.keySet().iterator().next();
        existingNodePointers.remove(nodeToAdd);
        NodePointerAdjacencyMatrix<Integer, Integer> actualNodePointer = new NodePointerAdjacencyMatrix<>(existingNodePointers,
            existingArcPointers, adjacencyMatrix, nodeToAdd);

        assertTrue(getExistingNodePointersMap(actualNodePointer).containsKey(nodeToAdd) &&
                getExistingNodePointersMap(actualNodePointer).get(nodeToAdd).equals(actualNodePointer),
            "the created nodePointer wasn't added to the existingArcPointersMap");

        assertNodePointerAdjacencyMatrixEquals(existingNodePointers, existingArcPointers, adjacencyMatrix,
            null, null, nodeToAdd, actualNodePointer);
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToAdjacencyMatrixPointerProvider.class)
    public void testDistance(AdjacencyMatrix<Integer> adjacencyMatrix,
                                HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> existingNodePointers,
                                HashMap<Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> existingArcPointers) {

        NodePointerAdjacencyMatrix<Integer, Integer> newNodePointer = new NodePointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, 0);
        int distance = RANDOM.nextInt(0, MAX_NODE_DISTANCE + 1);

        newNodePointer.setDistance(distance);
        assertEquals(distance, newNodePointer.getDistance(), "the methode getDistance() did not return the correct value");
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToAdjacencyMatrixPointerProvider.class)
    public void testPredecessor(AdjacencyMatrix<Integer> adjacencyMatrix,
                                HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> existingNodePointers,
                                HashMap<Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> existingArcPointers) {

        NodePointerAdjacencyMatrix<Integer, Integer> start = new NodePointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, 0);
        NodePointerAdjacencyMatrix<Integer, Integer> destination = new NodePointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, 1);

        destination.setPredecessor(start);
        assertEquals(start, destination.getPredecessor(), "the methode getPredecessor() did not return the correct value");
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
        assertEquals(expectedOutgoingArcs.size(), actualOutgoingArcs.size(), "the method outgoingArcs() did not return the correct amount of arcs if the existingArcPointersMap contains all arcs");
        assertListContainsAllWithPredicate(expectedOutgoingArcs, actualOutgoingArcs,
            (ArcPointerAdjacencyMatrix<Integer, Integer> expected, ArcPointerAdjacencyMatrix<Integer, Integer> actual) -> expected == actual,
            "the method outgoingArcs() did not return the correct elements if the existingArcPointersMap contains all arcs");

        //existingArcsMap does not contain the arcs
        for (ArcPointerAdjacencyMatrix<Integer, Integer> outgoingArc : expectedOutgoingArcs) existingArcPointers.remove(new Pair<>(getRow(outgoingArc), getColumn(outgoingArc)));

        node = new NodePointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, 0);
        actualOutgoingArcs = arcPointerListToAdjacencyMatrixArcList(iteratorToList(node.outgoingArcs()));
        assertEquals(expectedOutgoingArcs.size(), actualOutgoingArcs.size(), "The method outgoingArcs() did not return the correct amount of outgoing arcs");
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
        }, "the method outgoingArcs() did not return the correct elements if the existingArcPointersMap does not contains all arcs");
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
                    assertInstanceOf(ArcPointerAdjacencyMatrix.class, arcPointer, "the elements returned by the the outgoingArcs() method did not have the correct dynamic type");
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

