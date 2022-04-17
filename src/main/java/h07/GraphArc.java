package h07;

public class GraphArc<L> {
    private final L length;
    private final GraphNode<L> destination;

    /**
     * Erzeugt eine Kante eines Graphen.
     * @param length Die Länge der Kante.
     * @param destination Das Ziel der Kante.
     */
	public GraphArc(L length, GraphNode<L> destination) {
		this.destination = destination;
		this.length = length;
	}

    /**
     * Gibt die Länge der Kante zurück.
     * @return Die Länge der Kante.
     */
	public L getLength() {
		return length;
	}

    /**
     * Gibt das Ziel der Kante zurück.
     * @return Das Ziel der Kante.
     */
	public GraphNode<L> getDestination() {
		return destination;
	}

}
