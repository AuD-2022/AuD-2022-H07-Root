package h07.implementation;

import h07.ArcPointer;
import h07.IPriorityQueue;
import h07.NodePointer;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class DijkstraImpl {

    private final Comparator<Integer> comparator;
    private final BiFunction<Integer, Integer, Integer> distanceFunction;
    private final IPriorityQueue<NodePointer<Integer, Integer>> queue;

    private @Nullable Predicate<NodePointer<Integer, Integer>> predicate;

    /**
     * Erzeugt eine Instanz von Dijkstra, welche den Dijkstra Algorithmus ausführt.
     * @param comparator Ein Vergleichsoperator, welcher die Priorität, in der die Knoten in Dijkstra abgearbeitet werden, vorgibt.
     * @param distanceFunction Eine Integeristanzfunktion, welche für eine gegebene Quellknotendistanz und eine Kantenlänge die Zielknotendistanz ermittelt.
     * @param queueFactory Erzeugt für einen gegebenen Vergleichsoperator eine PriorityQueue, welche nach diesem Vergleichskriterium die Knoten sortiert.
     */
    public DijkstraImpl(Comparator<Integer> comparator, BiFunction<Integer, Integer, Integer> distanceFunction,
                    Function<Comparator<NodePointer<Integer,Integer>>, IPriorityQueue<NodePointer<Integer, Integer>>> queueFactory) {

        queue = queueFactory.apply((o1, o2) -> comparator.compare(o1.getDistance(), o2.getDistance()));

        this.comparator = comparator;
        this.distanceFunction = distanceFunction;
    }

    /**
     * Initialisiert den Algorithmus von Dijkstra.
     * @param startNode Der Startknoten, von dem der Algorithmus aus die Suche startet.
     */
    public void initialize(NodePointer<Integer, Integer> startNode) {
        queue.clear();
        queue.add(startNode);
    }

    /**
     * Initialisiert den Algorithmus von Dijkstra, erhält zusätzlich ein Prädikat, welches beim Eintreffen die Suche vorzeitig beendet.
     * @param startNode Der Startknoten, von dem der Algorithmus aus die Suche startet.
     * @param predicate Das Prädikat, welches beim Eintreffen die Suche vorzeitig beendet.
     */
    public void initialize(NodePointer<Integer, Integer> startNode, Predicate<NodePointer<Integer, Integer>> predicate) {
        this.predicate = predicate;
        initialize(startNode);
    }

    /**
     * Startet den Algorithms von Dijkstra.
     * @return Alle ermittelten Knoten, ausgenommen den Startknoten.
     */
    public List<NodePointer<Integer, Integer>> run() {
        List<NodePointer<Integer, Integer>> knownNodes = new LinkedList<>();
        NodePointer<Integer, Integer> currentNode = queue.deleteFront();

        while (!finished(currentNode)) {
            expandNode(currentNode);
            currentNode = queue.deleteFront();
            if (currentNode != null) {
                knownNodes.add(currentNode);
            }
        }
        return knownNodes;
    }

    /**
     * Expandiert den aktuellen Knoten, wie aus Dijkstra bekannt.
     * @param currentNode Zu expandierender Knoten.
     */
    public void expandNode(NodePointer<Integer, Integer> currentNode) {
        Iterator<ArcPointer<Integer, Integer>> outgoingArcs = currentNode.outgoingArcs();

        while (outgoingArcs.hasNext()) {
            ArcPointer<Integer, Integer> outgoingArc = outgoingArcs.next();

            NodePointer<Integer, Integer> destination = outgoingArc.destination();
            Integer destinationDistance = distanceFunction.apply(
                currentNode.getDistance(),
                outgoingArc.getLength());

            if (destination.getDistance() == null) {
                destination.setDistance(destinationDistance);
                destination.setPredecessor(currentNode);
                queue.add(destination);
            }
            else if (queue.contains(destination) && comparator.compare(destinationDistance, destination.getDistance()) < 0) {
                destination = queue.delete(destination);
                assert destination != null;
                destination.setDistance(destinationDistance);
                destination.setPredecessor(currentNode);
                queue.add(destination);
            }
        }
    }

    /**
     * Überprüft, ob der Algorithmus von Dijkstra terminiert.
     * @param currentNode Der Knoten, anhand dessen überprüft wird, ob der Algorithmus terminiert.
     * @return true, falls der Algorithmus terminiert.
     */
    public boolean finished(NodePointer<Integer, Integer> currentNode) {
        return currentNode == null || (predicate != null && predicate.test(currentNode));
    }

}
