package h07.h6;

import h07.AdjacencyMatrix;
import h07.Assertions;
import h07.Graph;
import h07.IllegalMethodsCheck;
import h07.implementation.GraphAdjacencyMatrixConverter;
import h07.provider.AdjacencyMatrixProvider;
import h07.transformer.MethodInterceptor;
import kotlin.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.util.ArrayList;
import java.util.List;

import static h07.Assertions.*;

@TestForSubmission("h07")
public class GraphTest {

    @BeforeEach
    public void reset() {
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods(
            "^java/util/ArrayList.+",
            "^java/util/LinkedList.+",
            "^java/util/Iterator.+");
    }

    @ParameterizedTest
    @ArgumentsSource(AdjacencyMatrixProvider.class)
    public void testConstructor(AdjacencyMatrix<Integer> adjacencyMatrix) {
        Graph<Integer> expectedGraph = GraphAdjacencyMatrixConverter.adjacencyMatrixToGraph(adjacencyMatrix.getMatrix());
        Graph<Integer> actualGraph = new Graph<>(adjacencyMatrix);
        assertGraphEquals(expectedGraph, actualGraph, () ->
            new AssertionMessage("the [[[graph]]] returned by [[[Graph(AdjacencyMatrix)]]] is not correct", List.of()));
    }

    @Test
    public void testConstructorEmpty() {
        //test with empty adjacencyMatrix
        assertGraphEquals(new Graph<>(new ArrayList<>()), new Graph<>(new AdjacencyMatrix<>(new Integer[0][0])),
            () -> new AssertionMessage("the [[[graph]]] returned by [[[Graph(AdjacencyMatrix)]]] is not correct",
                List.of(new Pair<>("Argument #1 - [[[adjacencyMatrix]]]", "an empty adjacencyMatrix"))));
    }

}
