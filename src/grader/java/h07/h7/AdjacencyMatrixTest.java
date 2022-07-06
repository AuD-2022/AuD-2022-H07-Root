package h07.h7;

import h07.AdjacencyMatrix;
import h07.Graph;
import h07.IllegalMethodsCheck;
import h07.implementation.GraphAdjacencyMatrixConverter;
import h07.provider.GraphProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.util.List;

import static h07.Assertions.*;

@TestForSubmission("h07")
public class AdjacencyMatrixTest {

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
    @ArgumentsSource(GraphProvider.class)
    public void testConstructor(Graph<Integer> graph) {
        Integer[][] expectedMatrix = GraphAdjacencyMatrixConverter.graphToAdjacencyMatrix(graph);
        Object[][] actualMatrix = new AdjacencyMatrix<>(graph).getMatrix();
        assertAdjacencyMatrixEquals(expectedMatrix, actualMatrix, () ->
            new AssertionMessage("the [[[adjacencyMatrix]]] returned by [[[AdjacencyMatrix(Graph)]]] is not correct", List.of()));
    }

}
