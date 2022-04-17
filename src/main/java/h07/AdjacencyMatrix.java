package h07;

import java.util.List;

public class AdjacencyMatrix<L> {
    private final L[][] matrix;

    /**
     * Initialisiert die Adjazenzmatrix über die gegebene Matrix.
     * @param matrix Die Matrix, die als Adjazenzmatrix genutzt werden soll.
     */
	public AdjacencyMatrix(L[][] matrix) {
		this.matrix = matrix;
	}

    /**
     * Erzeugt aus dem gegebenen Graph eine Adjazenzmatrix.
     * @param graph Der zu konvertierende Graph.
     */
	@SuppressWarnings("unchecked")
	public AdjacencyMatrix(Graph<L> graph) {
		List<GraphNode<L>> nodes = graph.getNodes();
		matrix = (L[][])new Object[nodes.size()][nodes.size()];

		for (int i = 0; i < nodes.size(); i++) {
			List<GraphArc<L>> outgoingArcs = nodes.get(i).getOutgoingArcs();
			for (int j = 0; j < outgoingArcs.size(); j++) {
				GraphNode<L> destination = outgoingArcs.get(j).getDestination();
				L length = outgoingArcs.get(j).getLength();
				for (int k = 0; k < nodes.size(); k++) {
					if (nodes.get(k).equals(destination)) {
						matrix[i][k] = length;
						break;
					}
				}
			}
		}
	}

    /**
     * Gibt die Adjazenzmatrix zurück.
     * @return Die Adjazenzmatrix.
     */
	public L[][] getMatrix() {
		return matrix;
	}
}
