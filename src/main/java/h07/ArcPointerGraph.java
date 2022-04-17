package h07;

import java.util.HashMap;

public class ArcPointerGraph<L, D> implements ArcPointer<L, D> {
    private final HashMap<GraphNode<L>, NodePointerGraph<L, D>> existingNodePointers;
    private final HashMap<GraphArc<L>, ArcPointerGraph<L, D>> existingArcPointers;
	private final GraphArc<L> graphArc;

    /**
     * Erzeugt einen Pointer auf eine Kante, für einen gegebenen Graphen.
     * @param existingNodePointers Die bereits bestehenden NodePointer.
     * @param existingArcPointers Die bereits bestehenden ArcPointer.
     * @param graphArc Die Kante des Graphen, auf die der Pointer verweist.
     */
	public ArcPointerGraph(HashMap<GraphNode<L>, NodePointerGraph<L, D>> existingNodePointers,
                           HashMap<GraphArc<L>, ArcPointerGraph<L, D>> existingArcPointers,
                           GraphArc<L> graphArc) {
		this.graphArc = graphArc;
        this.existingNodePointers = existingNodePointers;
        this.existingArcPointers = existingArcPointers;

        existingArcPointers.put(graphArc, this);
    }

	@Override
	public L getLength() {
		return graphArc.getLength();
	}

	@Override
	public NodePointer<L, D> destination() {
        if (existingNodePointers.containsKey(graphArc.getDestination())) {
            return existingNodePointers.get(graphArc.getDestination());
        }
		return new NodePointerGraph<L,D>(existingNodePointers, existingArcPointers, graphArc.getDestination());
	}

}
