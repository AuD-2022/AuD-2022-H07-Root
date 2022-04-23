package h07;

import java.util.ArrayList;
import java.util.List;

public class GraphNode<L> {
    private List<GraphArc<L>> outgoingArcs;

    /**
     * Erzeugt einen leeren Knoten.
     */
	public GraphNode() {
        outgoingArcs = new ArrayList<>();
	}

    /**
     * Erzeugt einen Knoten mit den gegebenen Kanten.
     * @param outgoingArcs Die ausgehenden Kanten des Knotens.
     */
	public GraphNode(List<GraphArc<L>> outgoingArcs) {
		this.outgoingArcs = outgoingArcs;
	}

    /**
     * Gibt die ausgehenden Kanten des Knotens zurück.
     * @return Die ausgehenden Kanten des Knotens.
     */
	public List<GraphArc<L>> getOutgoingArcs() {
		return outgoingArcs;
	}

    /**
     * Überschreibt die ausgehenden Kanten des Knotens.
     * @param outgoingArcs Die neuen ausgehenden Kanten des Knotens.
     */
	public void setOutgoingArcs(List<GraphArc<L>> outgoingArcs) {
		this.outgoingArcs = outgoingArcs;
	}
}
