package h07.h7;

import h07.*;
import h07.provider.GraphToAdjacencyMatrixPointerProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import static h07.Assertions.*;
import static h07.TestConstants.MAX_NODE_DISTANCE;
import static h07.TestConstants.RANDOM;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@TestForSubmission("h07")
public class ArcPointerAdjacencyMatrixTest extends AdjacencyMatrixPointerTest {

    private static final kotlin.Pair<String, String> CONSTRUCTOR_DESCRIPTION =
        new kotlin.Pair<>("[[[this]]]", "[[[new ArcPointerAdjacencyMatrix(existingNodePointers, existingArcPointers, row, column)]]]");

    private static final kotlin.Pair<String, String> MAP_DESCRIPTION =
        new kotlin.Pair<>("[[[existingArcPointersMap]]] and [[[existingNodePointersMap]]]",
            "the fields and the methods [[[getLength()]]], [[[destination()]]] and [[[outgoingArcs()]]] of the values of the maps have been overwritten to return the expected values");

    @BeforeEach
    public void reset() {
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods("^java/util/HashMap.+");
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToAdjacencyMatrixPointerProvider.class)
    public void testConstructor(AdjacencyMatrix<Integer> adjacencyMatrix,
                                HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> existingNodePointers,
                                HashMap<Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> existingArcPointers)
        throws NoSuchFieldException, IllegalAccessException {

        Pair<Integer, Integer> arcToAdd = existingArcPointers.keySet().iterator().next();
        existingArcPointers.remove(arcToAdd);
        ArcPointerAdjacencyMatrix<Integer, Integer> actualArcPointer = new ArcPointerAdjacencyMatrix<>(existingNodePointers,
            existingArcPointers, adjacencyMatrix, arcToAdd.getElement1(), arcToAdd.getElement2());

        assertTrueTutor(getExistingArcPointersMap(actualArcPointer).containsKey(arcToAdd) &&
                getExistingArcPointersMap(actualArcPointer).get(arcToAdd).equals(actualArcPointer),
            () -> new AssertionMessage("the created [[[arcPointer]]] wasn't added to the [[[existingArcPointersMap]]] after invoking the constructor",
                List.of(CONSTRUCTOR_DESCRIPTION)));

        assertArcPointerAdjacencyMatrixEquals(adjacencyMatrix, existingNodePointers, existingArcPointers,
            arcToAdd.getElement1(), arcToAdd.getElement2(), actualArcPointer,
            () -> new AssertionMessage("the [[[arcPointer]]] created by the constructor does not have the expected properties",
                List.of(CONSTRUCTOR_DESCRIPTION)));
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToAdjacencyMatrixPointerProvider.class)
    public void testGetLength(AdjacencyMatrix<Integer> adjacencyMatrix,
                                HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> existingNodePointers,
                                HashMap<Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> existingArcPointers) {

        int length = RANDOM.nextInt(0, MAX_NODE_DISTANCE + 1);
        int row = RANDOM.nextInt(0, adjacencyMatrix.getMatrix().length);
        int column = RANDOM.nextInt(0, adjacencyMatrix.getMatrix().length);
        adjacencyMatrix.getMatrix()[row][column] = length;

        ArcPointerAdjacencyMatrix<Integer, Integer> newArcPointer = new ArcPointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, row, column);

        assertEqualsTutor(length, newArcPointer.getLength(), () -> new AssertionMessage("[[[getLength()]]] did not return the correct value",
            List.of(CONSTRUCTOR_DESCRIPTION, new kotlin.Pair<>("[[[row, column]]]", "the indices of a arc with [[[matrix[row][column] == %d]]]".formatted(length)))
        ));

        //test no connection
        adjacencyMatrix.getMatrix()[row][column] = null;

        assertNullTutor(newArcPointer.getLength(), () -> new AssertionMessage(
            "[[[getLength()]] did not return the correct value if there is no connection between two nodes",
            List.of(CONSTRUCTOR_DESCRIPTION, new kotlin.Pair<>("[[[row, column]]]", "the indices of a arc with [[[matrix[row][column] == null]]]"))
        ));
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToAdjacencyMatrixPointerProvider.class)
    public void testGetDestination(AdjacencyMatrix<Integer> adjacencyMatrix,
                                HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> existingNodePointers,
                                HashMap<Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> existingArcPointers) throws NoSuchFieldException, IllegalAccessException {

        Iterator<Integer> nodeIterator = existingNodePointers.keySet().iterator();
        int start = nodeIterator.next();
        int destination = nodeIterator.next();

        //existingNodes contains destination node
        NodePointer<Integer, Integer> actualDestination = new ArcPointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, start, destination).destination();

        assertInstanceOf(NodePointerAdjacencyMatrix.class, actualDestination,
            "the [[[nodePointer]]] returned by [[[destination()]]] does not have the correct dynamic type if the [[[existingNodePointers]]] map contains the destination");

        assertSameTutor(existingNodePointers.get(destination), actualDestination,
            () -> new AssertionMessage("[[[destination()]]] did not return the correct [[[NodePointer]]]",
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new kotlin.Pair<>("[[[row, column]]]", "the indices of a arc whose destination is a key of the [[[existingNodePointers]]] map")))
        );

        //existingNodes does not contain destination node
        existingNodePointers.remove(destination);

        actualDestination =new ArcPointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, start, destination).destination();

        assertInstanceOf(NodePointerAdjacencyMatrix.class, actualDestination,
            "the [[[nodePointer]]] returned by [[[destination()]]] does not have the correct dynamic type if the [[[existingNodePointers]]] map does not contain the destination");

        assertNodePointerAdjacencyMatrixEquals(existingNodePointers, existingArcPointers, adjacencyMatrix,
            null, null, destination, (NodePointerAdjacencyMatrix<Integer, Integer>) actualDestination,
            () -> new AssertionMessage("[[[destination()]]] did not return the correct [[[nodePointer]]]",
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new kotlin.Pair<>("[[[row, column]]]", "the indices of a arc whose destination is not a key of the [[[existingNodePointers]]] map")))
        );
    }

}
