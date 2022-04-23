package h07;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Pair<T1, T2> {
    private T1 element1;
    private T2 element2;

    /**
     * Erzeugt ein Paar aus zwei zusammengehörigen Elementen.
     * @param element1 Das erste Element.
     * @param element2 Das zweite Element.
     */
    public Pair(T1 element1, T2 element2) {
        this.element1 = element1;
        this.element2 = element2;
    }

    /**
     * Überschreibt das erste Element.
     * @param element1 Das neue erste Element.
     */
    public void setElement1(T1 element1) {
        this.element1 = element1;
    }

    /**
     * Überschreibt das zweite Element.
     * @param element2 Das neue zweite Element.
     */
    public void setElement2(T2 element2) {
        this.element2 = element2;
    }

    /**
     * Gibt das erste Element zurück.
     * @return Das erste Element.
     */
    public T1 getElement1() {
        return element1;
    }

    /**
     * Gibt das erste Element zurück.
     * @return Das erste Element.
     */
    public T2 getElement2() {
        return element2;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return element1.equals(pair.getElement1()) && element2.equals(pair.getElement2());
    }

    @Override
    public int hashCode() {
        return Objects.hash(element1, element2);
    }
}
