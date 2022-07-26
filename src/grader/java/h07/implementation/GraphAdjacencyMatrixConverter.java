package h07.implementation;

import h07.Graph;
import h07.GraphArc;
import h07.GraphNode;

import java.util.ArrayList;
import java.util.List;

public class GraphAdjacencyMatrixConverter {

    public static Integer[][] graphToAdjacencyMatrix(Graph<Integer> graph) {

        List<GraphNode<Integer>> nodes = graph.getNodes();
        Integer[][] matrix = new Integer[nodes.size()][nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            List<GraphArc<Integer>> outgoingArcs = nodes.get(i).getOutgoingArcs();
            for (int j = 0; j < outgoingArcs.size(); j++) {
                GraphNode<Integer> destination = outgoingArcs.get(j).getDestination();
                Integer length = outgoingArcs.get(j).getLength();
                for (int k = 0; k < nodes.size(); k++) {
                    if (nodes.get(k).equals(destination)) {
                        matrix[i][k] = length;
                        break;
                    }
                }
            }
        }

        return matrix;
    }

    public static Graph<Integer> adjacencyMatrixToGraph(Integer[][] matrix) {
        List<GraphNode<Integer>> nodes = new ArrayList<GraphNode<Integer>>();

        for (int i = 0; i < matrix.length; i++) {
            nodes.add(new GraphNode<>());
        }

        for (int i = 0; i < matrix.length; i++) {
            List<GraphArc<Integer>> outgoingArcs = new ArrayList<>();
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != null) {
                    outgoingArcs.add(new GraphArc<Integer>(matrix[i][j], nodes.get(j)));
                }
            }
            nodes.get(i).setOutgoingArcs(outgoingArcs);
        }

        return new Graph<>(nodes);
    }
}
