package h07;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PriorityQueueList<T> implements IPriorityQueue<T> {
	final Comparator<T> priorityComparator;
	final List<T> queue;

	/**
	 * Erstellt eine Priority Queue basierend auf einer Liste, mit durch priorityComparator induzierter Ordnung.
	 * @param priorityComparator Die auf die Priority Queue induzierte Ordnung.
	 */
	public PriorityQueueList(Comparator<T> priorityComparator) {
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
	public T delete(T item) {
		if (queue.remove(item)) {
			return item;
		}
		return null;
	}

	@Override
	public T getFront() {
		if (queue.size() > 0) {
			return queue.get(0);
		}
		return null;
	}

	@Override
	public T deleteFront() {
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

	/**
	 * Gibt die zur Realisierung der Priority Queue genutzte Liste zurück.
	 * @return Die Liste, die zur Realisierung der Priority Queue genutzt wird.
	 */
	public List<T> getInternalList() {
		return queue;
	}

}
