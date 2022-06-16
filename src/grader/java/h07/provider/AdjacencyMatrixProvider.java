package h07.provider;

import h07.AdjacencyMatrix;
import h07.Graph;
import h07.implementation.GraphAdjacencyMatrixConverter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class AdjacencyMatrixProvider extends AbstractProvider {

    @Override
    @SuppressWarnings("unchecked")
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return new GraphProvider().provideArguments(context)
                .map(arguments -> (Graph<Integer>) arguments.get()[0])
                .map(GraphAdjacencyMatrixConverter::graphToAdjacencyMatrix)
                .map(AdjacencyMatrix::new)
                .map(Arguments::of);
    }
}
