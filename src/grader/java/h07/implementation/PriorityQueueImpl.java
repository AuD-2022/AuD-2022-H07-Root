package h07.implementation;

import h07.IPriorityQueue;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PriorityQueueImpl<T> implements IPriorityQueue<T> {

    public final Comparator<T> priorityComparator;
    public final List<T> queue;

    /**
     * Erstellt eine Priority Queue basierend auf einer Liste, mit durch priorityComparator induzierter Ordnung.
     * @param priorityComparator Die auf die Priority Queue induzierte Ordnung.
     */
    public PriorityQueueImpl(Comparator<T> priorityComparator) {
        this.priorityComparator = priorityComparator;
        this.queue = new LinkedList<T>();
    }

    @Override
    public void add(T item) {
        Iterator<T> iterator = queue.iterator();

        int position = 0;
        while (iterator.hasNext() && priorityComparator.compare(item, iterator.next()) <= 0) {
            position++;
        }
        queue.add(position, item);
    }

    @Override
    public @Nullable T delete(T item) {
        if (queue.remove(item)) {
            return item;
        }
        return null;
    }

    @Override
    public @Nullable T getFront() {
        if (queue.size() > 0) {
            return queue.get(0);
        }
        return null;
    }

    @Override
    public @Nullable T deleteFront() {
        if (queue.size() > 0) {
            return queue.remove(0);
        }
        return null;
    }

    @Override
    public int getPosition(T item) {
        Iterator<T> iterator = queue.iterator();

        int position = 1;
        while (iterator.hasNext()) {
            if (iterator.next().equals(item)) {
                return position;
            }
            position++;
        }
        return -1;
    }

    @Override
    public boolean contains(T item) {
        for (T currentElement : queue) {
            if (item.equals(currentElement)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Comparator<T> getPriorityComparator() {
        return priorityComparator;
    }

    @Override
    public void clear() {
        queue.clear();
    }

}
