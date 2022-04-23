package h07;

import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public interface IPriorityQueue<T> {
	/**
	 * Fügt ein neues Element (Parameter item) in die Queue ein.
     * Beachtet die durch den Comparator<T> induzierte Ordnung.
	 * @param item Das einzufügende Element.
	 */
	void add(T item);

	/**
	 * Löscht ein bestehendes Element (Parameter item) aus der Queue und gibt es zurück.
     * Die Elemente dahinter rücken entsprechend in der Reihenfolge um eins vor.
     * Die Methode hat keine Auswirkungen und gibt null zurück, falls das Element nicht in der Queue sein sollte.
	 * @param item Das zu löschende Element.
	 * @return Das gelöschte Element. Null, falls kein Element gelöscht wurde.
	 */
    @Nullable T delete(T item);

	/**
	 * Gibt das vorderste Element aus der Queue zurück.
     * Die Methode hat keine Auswirkungen und gibt null zurück, falls das Element nicht in der Queue sein sollte.
	 * @return Das vorderste Element der Queue, oder null, falls kein Element in der Queue sein sollte.
	 */
    @Nullable T getFront();

	/**
	 * Gibt das vorderste Element aus der Queue zurück und löscht es dabei.
     * Die Elemente dahinter rücken entsprechend in der Reihenfolge um eins vor.
     * Die Methode hat keine Auswirkungen und gibt null zurück, falls kein Element in der Queue sein sollte.
	 * @return Das gelöschte vorderste Element der Queue, oder null, falls kein Element in der Queue sein sollte.
	 */
    @Nullable T deleteFront();

	/**
	 * Gibt die Position des als Parameter übergebenen Elements (item) in der Priority Queue zurück.
     * Diese Position ist durch die durch den Comparator<T> induzierte Ordnung vorgegeben.
     * Die Methode hat keine Auswirkungen und gibt -1 zurück, falls das Element nicht in der Queue sein sollte.
	 * @param item Das Element für das die Position bestimmt werden soll.
	 * @return Die Position des Parameters item, oder null, falls item nicht in der Queue sein sollte.
	 */
	int getPosition(T item);

    /**
     * Gibt true zurück, falls sich das als Parameter übergebene Element (item) in der Priority Queue befindet.
     * @param item Das Element für das überprüft werden soll, ob es sich in der Queue befindet.
     * @return True, falls sich das als Parameter übergebene Element (item) in der Priority Queue befindet.
     */
    boolean contains(T item);

    /**
	 * Entfernt alle Elemente aus der Queue.
	 */
	void clear();

	/**
	 * Gibt den die Ordnung induzierenden Comparator<T> zurück.
	 * @return Den die Ordnung induzierenden Comparator<T>.
	 */
	Comparator<T> getPriorityComparator();
}
