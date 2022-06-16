package h07.h7;

import h07.AdjacencyMatrix;
import h07.Graph;
import h07.implementation.GraphAdjacencyMatrixConverter;
import h07.provider.GraphProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import static h07.Assertions.assertAdjacencyMatrixEquals;

@TestForSubmission("h07")
public class AdjacencyMatrixTest {

    @BeforeEach
    public void reset() {
        MethodInterceptor.reset();
    }

    @ParameterizedTest
    @ArgumentsSource(GraphProvider.class)
    public void testConstructor(Graph<Integer> graph) {
        Integer[][] expectedMatrix = GraphAdjacencyMatrixConverter.graphToAdjacencyMatrix(graph);
        Object[][] actualMatrix = new AdjacencyMatrix<>(graph).getMatrix();
        assertAdjacencyMatrixEquals(expectedMatrix, actualMatrix);
    }

}
