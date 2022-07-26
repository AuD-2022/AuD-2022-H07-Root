package h07.provider;

import h07.Graph;
import h07.GraphArc;
import h07.GraphNode;
import h07.implementation.ArcPointerImpl;
import h07.implementation.NodePointerImpl;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GraphToNodePointerImplProvider implements ArgumentsProvider {

    @SuppressWarnings("unchecked")
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return new GraphProvider().provideArguments(context)
            .map(arguments -> (Graph<Integer>) arguments.get()[0])
            .map(graph -> {
                HashMap<GraphNode<Integer>, NodePointerImpl> existingNodePointers = new HashMap<>();

                for (GraphNode<Integer> node : graph.getNodes()) existingNodePointers.put(node, new NodePointerImpl());
                for (GraphNode<Integer> node : graph.getNodes()) {
                    for (GraphArc<Integer> arc : node.getOutgoingArcs()) {
                        ArcPointerImpl arcPointer = new ArcPointerImpl();
                        arcPointer.length = arc.getLength();
                        arcPointer.destination = existingNodePointers.get(arc.getDestination());
                        existingNodePointers.get(node).addOutgoingArc(arcPointer);
                    }
                }

                return graph.getNodes().stream().map(existingNodePointers::get).collect(Collectors.toList());//use the same order
            })
            .map(Arguments::of);
    }
}
