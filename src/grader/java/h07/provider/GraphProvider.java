package h07.provider;

import h07.Graph;
import h07.GraphArc;
import h07.GraphNode;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static h07.TestConstants.*;

public class GraphProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        List<Graph<Integer>> arguments = new ArrayList<>(ARGUMENT_COUNT);

        for (int i = 0; i < ARGUMENT_COUNT; i++) {

            int nodeCount = RANDOM.nextInt(MIN_NODE_COUNT, MAX_NODE_COUNT + 1) - 1;
            List<GraphNode<Integer>> nodes = new ArrayList<>(nodeCount + 1);

            for (int j = 0; j < nodeCount; j++) {
                nodes.add(new GraphNode<>());
            }

            for (GraphNode<Integer> node : nodes) {
                int arcCount = RANDOM.nextInt(MAX_OUTGOING_ARC_COUNT + 1);

                for (int j = 0; j < arcCount; j++) {
                    GraphNode<Integer> randomNode = nodes.get(RANDOM.nextInt(nodeCount));
                    while (randomNode == node) randomNode = nodes.get(RANDOM.nextInt(nodeCount));

                    GraphNode<Integer> finalRandomNode = randomNode;
                    if (node.getOutgoingArcs().stream().noneMatch(existingArc -> existingArc.getDestination() == finalRandomNode))
                        node.getOutgoingArcs().add(new GraphArc<>(RANDOM.nextInt(MAX_NODE_DISTANCE + 1), randomNode));
                }
            }

            //ensure the graph is connected
            List<Boolean> connected = new ArrayList<>();
            for (int j = 0; j < nodeCount; j++) connected.add(false);

            checkConnected(nodes, nodes.get(0), connected);
            //connect every unconnected node with the first node
            for (int j = 0; j < nodeCount; j++) {
                if (!connected.get(j)) {
                    GraphNode<Integer> unconnectedNode = nodes.get(j);
                    nodes.get(0).getOutgoingArcs().add(new GraphArc<>(RANDOM.nextInt(MAX_NODE_DISTANCE + 1), unconnectedNode));
                }
            }

            //add a node with no outgoing arcs
            GraphNode<Integer> node = new GraphNode<>();
            nodes.get(0).getOutgoingArcs().add(new GraphArc<>(10, node));
            nodes.add(node);

            //add an arc that points to node itself
            nodes.get(0).getOutgoingArcs().add(new GraphArc<>(10, nodes.get(0)));

            arguments.add(new Graph<>(nodes));
        }

        return arguments.stream().map(Arguments::of);
    }

    private void checkConnected(List<GraphNode<Integer>> nodes, GraphNode<Integer> currentNode, List<Boolean> connected) {
        connected.set(nodes.indexOf(currentNode), true);

        for (GraphArc<Integer> arc : currentNode.getOutgoingArcs()) {
            if (!connected.get(nodes.indexOf(arc.getDestination()))) {
                checkConnected(nodes, arc.getDestination(), connected);
            }
        }
    }
}
