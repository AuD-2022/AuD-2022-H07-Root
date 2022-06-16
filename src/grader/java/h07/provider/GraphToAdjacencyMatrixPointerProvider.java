package h07.provider;

import h07.*;
import h07.implementation.GraphAdjacencyMatrixConverter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class GraphToAdjacencyMatrixPointerProvider extends AbstractProvider {

    @SuppressWarnings("unchecked")
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return new GraphProvider().provideArguments(context)
            .map(argument -> argument.get()[0])
            .map(object -> (Graph<Integer>) object)
            .map(graph -> {
                AdjacencyMatrix<Integer> adjacencyMatrix = new AdjacencyMatrix<>(GraphAdjacencyMatrixConverter.graphToAdjacencyMatrix(graph));
                Integer[][] matrix = adjacencyMatrix.getMatrix();

                HashMap<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> existingNodePointers = new HashMap<>();
                HashMap<Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> existingArcPointers = new HashMap<>();

                //create mocked NodePointer instances
                for (int node = 0; node < matrix.length; node++) {
                    existingNodePointers.put(node, spy(new NodePointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, node)));
                }

                //create mocked arcPointer instances
                for (int node = 0; node < matrix.length; node++) {
                    for (int destination = 0; destination < matrix[node].length; destination++) {
                        if (matrix[node][destination] == null) continue;
                        ArcPointerAdjacencyMatrix<Integer, Integer> mock = spy(new ArcPointerAdjacencyMatrix<>(existingNodePointers, existingArcPointers, adjacencyMatrix, node, destination));
                        when(mock.getLength()).thenReturn(matrix[node][destination]);
                        when(mock.destination()).thenReturn(existingNodePointers.get(node));
                        existingArcPointers.put(new Pair<>(node, destination), mock);
                    }
                }

                //mock outgoingArcs method
                for (int node = 0; node < matrix.length; node++) {
                    NodePointerAdjacencyMatrix<Integer, Integer> mock = existingNodePointers.get(node);
                    int finalNode = node;
                    when(mock.outgoingArcs()).thenReturn(Arrays.stream(matrix[node])
                        .filter(Objects::nonNull)
                        .map(destination -> (ArcPointer<Integer, Integer>) existingArcPointers.get(new Pair<>(finalNode, destination)))
                        .toList()
                        .iterator());
                }


                //set fields of mocked nodePointers
                for (Map.Entry<Integer, NodePointerAdjacencyMatrix<Integer, Integer>> entry : existingNodePointers.entrySet()) {
                    try {
                        Field existingNodePointersField = NodePointerAdjacencyMatrix.class.getDeclaredField("existingNodePointers");
                        existingNodePointersField.setAccessible(true);
                        existingNodePointersField.set(entry.getValue(), existingNodePointers);

                        Field existingArcPointersField = NodePointerAdjacencyMatrix.class.getDeclaredField("existingArcPointers");
                        existingArcPointersField.setAccessible(true);
                        existingArcPointersField.set(entry.getValue(), existingArcPointers);

                        Field adjacencyMatrixField = NodePointerAdjacencyMatrix.class.getDeclaredField("adjacencyMatrix");
                        adjacencyMatrixField.setAccessible(true);
                        adjacencyMatrixField.set(entry.getValue(), adjacencyMatrix);

                        Field row = NodePointerAdjacencyMatrix.class.getDeclaredField("row");
                        row.setAccessible(true);
                        row.set(entry.getValue(), entry.getKey());

                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                        throw new RuntimeException("unable to set fields of class NodePointerAdjacencyMatrix");
                    }
                }

                //set fields of mocked arcPointers
                for (Map.Entry<Pair<Integer, Integer>, ArcPointerAdjacencyMatrix<Integer, Integer>> entry : existingArcPointers.entrySet()) {
                    try {
                        Field existingNodePointersField = ArcPointerAdjacencyMatrix.class.getDeclaredField("existingNodePointers");
                        existingNodePointersField.setAccessible(true);
                        existingNodePointersField.set(entry.getValue(), existingNodePointers);

                        Field existingArcPointersField = ArcPointerAdjacencyMatrix.class.getDeclaredField("existingArcPointers");
                        existingArcPointersField.setAccessible(true);
                        existingArcPointersField.set(entry.getValue(), existingArcPointers);

                        Field adjacencyMatrixField = ArcPointerAdjacencyMatrix.class.getDeclaredField("adjacencyMatrix");
                        adjacencyMatrixField.setAccessible(true);
                        adjacencyMatrixField.set(entry.getValue(), adjacencyMatrix);

                        Field row = ArcPointerAdjacencyMatrix.class.getDeclaredField("row");
                        row.setAccessible(true);
                        row.set(entry.getValue(), entry.getKey().getElement1());

                        Field column = ArcPointerAdjacencyMatrix.class.getDeclaredField("column");
                        column.setAccessible(true);
                        column.set(entry.getValue(), entry.getKey().getElement2());
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                        throw new RuntimeException("unable to set fields of class ArcPointerAdjacencyMatrix");
                    }
                }

                return new Object[]{adjacencyMatrix, existingNodePointers, existingArcPointers};
            })
            .map(Arguments::of);
    }

}
