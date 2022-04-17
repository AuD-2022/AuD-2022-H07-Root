package h07;

import java.util.ArrayList;
import java.util.List;

public class Graph<L> {
    private final List<GraphNode<L>> nodes;

    /**
     * Speichert die gegebenen Knoten als Graphen ab.
     * @param nodes Die Knoten des Graphen.
     */
	public Graph(List<GraphNode<L>> nodes) {
		this.nodes = nodes;
	}

    /**
     * Erzeugt anhand einer Adjazenzmatrix einen Graphen.
     * @param adjacencyMatrix Die Adjazenzmatrix, die zu einem Graphen konvertiert werden soll.
     */
	public Graph(AdjacencyMatrix<L> adjacencyMatrix) {
		L[][] matrix = adjacencyMatrix.getMatrix();
		nodes = new ArrayList<GraphNode<L>>();

		for (int i = 0; i < matrix.length; i++) {
			nodes.add(new GraphNode<L>());
		}

		for (int i = 0; i < matrix.length; i++) {
			List<GraphArc<L>> outgoingArcs = new ArrayList<GraphArc<L>>();
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] != null) {
					outgoingArcs.add(new GraphArc<L>(matrix[i][j], nodes.get(j)));
				}
			}
			nodes.get(i).setOutgoingArcs(outgoingArcs);
		}
	}

    /**
     * Gibt die Knoten des Graphen zurück.
     * @return Die Knoten des Graphen.
     */
	public List<GraphNode<L>> getNodes() {
		return nodes;
	}
}
