package h07;

import java.util.Iterator;

public interface NodePointer<L, D> {

    /**
     * Gibt die Distanz, vom Startknoten ausgehend, zum Knoten, auf den der Pointer verweist, zurück.
     * @return Die Distanz zum Knoten, auf den der Pointer verweist.
     */
    D getDistance();

    /**
     * Überschreibt die Distanz, vom Startknoten ausgehend, zum Knoten, auf den der Pointer, verweist.
     * @param distance Die neue Distanz zum Knoten, auf den der Pointer verweist.
     */
    void setDistance(D distance);

    /**
     * Der Vorgängerknoten (ausgehend vom Startknoten) durch den der Knoten, auf den der Pointer verweist, erreicht werden kann.
     * @return Der Vorgängerknoten durch den der Knoten, auf den der Pointer verweist, erreicht werden kann.
     */
    NodePointer<L, D> getPredecessor();

    /**
     * Überschreibt den Vorgängerknoten (ausgehend vom Startknoten) durch den der Knoten, auf den der Pointer verweist, erreicht werden kann.
     * @param predecessor Der neue Vorgängerknoten durch den der Knoten, auf den der Pointer verweist, erreicht werden kann.
     */
    void setPredecessor(NodePointer<L, D> predecessor);

    /**
     * Gibt Verweise auf die ausgehenden Kanten des Knotens, auf den der Pointer verweist, zurück.
     * @return Verweise auf die ausgehenden Kanten des Knotens, auf den der Pointer verweist.
     */
    Iterator<ArcPointer<L, D>> outgoingArcs();
}
