package h07;

public interface ArcPointer<L, D> {

    /**
     * Gibt die Länge der Kante, auf die der Pointer verweist, zurück.
     * @return Die Länge der Kante, auf die der Pointer verweist.
     */
	public L getLength();

    /**
     * Gibt das Ziel der Kante, auf das der Pointer verweist, zurück.
     * @return Das Ziel der Kante, auf das der Pointer verweist.
     */
	public NodePointer<L, D> destination();
}
