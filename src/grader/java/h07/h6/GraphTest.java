package h07.h6;

import h07.AdjacencyMatrix;
import h07.Graph;
import h07.IllegalMethodsCheck;
import h07.implementation.GraphAdjacencyMatrixConverter;
import h07.implementation.NodePointerImpl;
import h07.provider.AdjacencyMatrixProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import static h07.Assertions.assertGraphEquals;

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
        assertGraphEquals(expectedGraph, actualGraph);
    }

}
