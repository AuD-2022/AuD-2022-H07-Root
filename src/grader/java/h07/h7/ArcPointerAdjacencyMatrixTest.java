package h07.h7;

import h07.*;
import h07.implementation.NodePointerImpl;
import h07.provider.GraphToAdjacencyMatrixPointerProvider;
import h07.transformer.MethodInterceptor;
import kotlin.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import static h07.Assertions.assertArcPointerAdjacencyMatrixEquals;
import static h07.Assertions.assertNodePointerAdjacencyMatrixEquals;
import static h07.provider.GraphProvider.MAX_NODE_DISTANCE;
import static h07.provider.AbstractProvider.RANDOM;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Iterator;

@TestForSubmission("h07")
public class ArcPointerAdjacencyMatrixTest extends AdjacencyMatrixPointerTest {

    @BeforeEach
    public void reset() {
        MethodInterceptor.reset();
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

        assertTrue(getExistingArcPointersMap(actualArcPointer).containsKey(arcToAdd) &&
                getExistingArcPointersMap(actualArcPointer).get(arcToAdd).equals(actualArcPointer),
            "the created arcPointer wasn't added to the existingArcPointersMap");

        assertArcPointerAdjacencyMatrixEquals(adjacencyMatrix, existingNodePointers, existingArcPointers,
            arcToAdd.getElement1(), arcToAdd.getElement2(), actualArcPointer);
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

        assertEquals(length, newArcPointer.getLength(), "the method getLength() did not return the correct value.");

        //test no connection
        adjacencyMatrix.getMatrix()[row][column] = null;
        assertNull(newArcPointer.getLength(), "the method getLength() did not return the correct value if there is no connection between two nodes");
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
        NodePointer<Integer, Integer> actualNodePointer = new ArcPointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, start, destination).destination();
        assertInstanceOf(NodePointerAdjacencyMatrix.class, actualNodePointer, "the NodePointer returned by the destination() method does not have the correct dynamic type");
        assertSame(existingNodePointers.get(destination), actualNodePointer, "the methode destination() did not return the correct value if the existingNodePointers map contains the destination node.");

        //existingNodes does not contain destination node
        NodePointer<Integer, Integer> expectedNodePointer = existingNodePointers.remove(destination);

        actualNodePointer =new ArcPointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, start, destination).destination();
        assertInstanceOf(NodePointerAdjacencyMatrix.class, actualNodePointer, "the NodePointer returned by the destination() method does not have the correct dynamic type");
        assertNodePointerAdjacencyMatrixEquals(existingNodePointers, existingArcPointers, adjacencyMatrix,
            null, null, destination, (NodePointerAdjacencyMatrix<Integer, Integer>) actualNodePointer);
    }

}
