package h07.provider;

import h07.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GraphToGraphPointerMapsProvider implements ArgumentsProvider {

    @SuppressWarnings("unchecked")
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        return new GraphProvider().provideArguments(context)
            .map(arguments -> arguments.get()[0])
            .map(object -> (Graph<Integer>) object)
            .map(graph -> {
                HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers = new HashMap<>();
                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers = new HashMap<>();

                //create mocked NodePointer instances
                for (GraphNode<Integer> node : graph.getNodes()) {
                    existingNodePointers.put(node, spy(new NodePointerGraph<>(existingNodePointers, existingArcPointers, node)));
                }

                //create mocked arcPointer instances
                for (GraphNode<Integer> node : graph.getNodes()) {
                    for (GraphArc<Integer> arc : node.getOutgoingArcs()) {
                        ArcPointerGraph<Integer, Integer> mock = spy(new ArcPointerGraph<>(existingNodePointers, existingArcPointers, arc));
                        when(mock.getLength()).thenReturn(arc.getLength());
                        when(mock.destination()).thenReturn(existingNodePointers.get(arc.getDestination()));
                        existingArcPointers.put(arc, mock);
                    }
                }

                //mock outgoingArcs method
                for (GraphNode<Integer> node : graph.getNodes()) {
                    when(existingNodePointers.get(node).outgoingArcs()).thenReturn(
                        new ArrayList<ArcPointer<Integer, Integer>>(
                            node.getOutgoingArcs().stream()
                                .map(existingArcPointers::get)
                                .toList())
                            .iterator());
                }

                //set fields of mocked nodePointers
                for (Map.Entry<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> entry : existingNodePointers.entrySet()) {
                    try {
                        Field existingNodePointersField = NodePointerGraph.class.getDeclaredField("existingNodePointers");
                        existingNodePointersField.setAccessible(true);
                        existingNodePointersField.set(entry.getValue(), existingNodePointers);

                        Field existingArcPointersField = NodePointerGraph.class.getDeclaredField("existingArcPointers");
                        existingArcPointersField.setAccessible(true);
                        existingArcPointersField.set(entry.getValue(), existingArcPointers);

                        Field graphNode = NodePointerGraph.class.getDeclaredField("graphNode");
                        graphNode.setAccessible(true);
                        graphNode.set(entry.getValue(), entry.getKey());
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                        throw new RuntimeException("unable to set fields of class NodePointerGraph");
                    }
                }

                //set fields of mocked arcPointers
                for (Map.Entry<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> entry : existingArcPointers.entrySet()) {
                    try {
                        Field existingNodePointersField = ArcPointerGraph.class.getDeclaredField("existingNodePointers");
                        existingNodePointersField.setAccessible(true);
                        existingNodePointersField.set(entry.getValue(), existingNodePointers);

                        Field existingArcPointersField = ArcPointerGraph.class.getDeclaredField("existingArcPointers");
                        existingArcPointersField.setAccessible(true);
                        existingArcPointersField.set(entry.getValue(), existingArcPointers);

                        Field graphArc = ArcPointerGraph.class.getDeclaredField("graphArc");
                        graphArc.setAccessible(true);
                        graphArc.set(entry.getValue(), entry.getKey());
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                        throw new RuntimeException("unable to set fields of class ArcPointerGraph");
                    }
                }
                return new Object[]{existingNodePointers, existingArcPointers};
            })
            .map(Arguments::of);
    }

}
