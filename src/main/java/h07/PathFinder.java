package h07;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class PathFinder<L, D> implements Function<NodePointer<L, D>, List<NodePointer<L, D>>> {

    /**
     * Rekonstruiert den Pfad vom Endkonten zum Startknoten.
     * @param endNode Der Endkonten.
     * @return Den Pfad vom Endknoten zum Startknoten.
     */
	@Override
	public List<NodePointer<L, D>> apply(NodePointer<L, D> endNode) {
		NodePointer<L, D> currentNode = endNode;
		List<NodePointer<L, D>> path = new LinkedList<NodePointer<L, D>>();

		while (currentNode != null) {
			path.add(0, currentNode);
			currentNode = currentNode.getPredecessor();
		}
		return path;
	}
}
