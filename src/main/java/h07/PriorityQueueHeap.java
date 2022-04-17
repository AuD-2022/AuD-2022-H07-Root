package h07;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class PriorityQueueHeap<T> implements IPriorityQueue<T> {
    private final Comparator<T> priorityComparator;
    private final HashMap<T, Integer> indexMap;
    private T[] heap;
    private int size;

    /**
     * Erstellt eine Priority Queue basierend auf einem Heap, mit durch priorityComparator induzierter Ordnung.
     * @param priorityComparator Die auf die Priority Queue induzierte Ordnung.
     * @param capacity Die Kapazität der Queue.
     */
	public PriorityQueueHeap(Comparator<T> priorityComparator, int capacity) {
		this.priorityComparator = priorityComparator;
        this.indexMap = new HashMap<>();
        heap = (T[])new Object[capacity];
        size = 0;
	}

    @Override
	public void add(T item) {
        heap[size] = item;
        indexMap.put(item, size);
        int currentIndex = size;
        int parentIndex = (size - 1) / 2;
        size++;

        // swap element upwards to correct position
        while (parentIndex >= 0 && priorityComparator.compare(heap[currentIndex], heap[parentIndex]) > 0) {
            swap(currentIndex, parentIndex);
            currentIndex = parentIndex;
            parentIndex = (parentIndex - 1) / 2;
        }
	}

	@Override
	public T delete(T item) {
        if (indexMap.containsKey(item)) {
            size--;
            int index = indexMap.get(item);
            indexMap.remove(item);
            if (index == size) {
                heap[index] = null;
                return item;
            }
            heap[index] = heap[size];
            heap[size] = null;
            indexMap.put(heap[index], index);

            int currentIndex = index;

            // swap element downward to correct position
            while (true) {
                int leftChildIndex = currentIndex * 2 + 1;
                int rightChildIndex = currentIndex * 2 + 2;
                int biggestChildIndex;

                // any existing children?
                if (leftChildIndex < size) {
                    // only left child existing?
                    if (rightChildIndex >= size) {
                        biggestChildIndex = leftChildIndex;
                    } else {
                        biggestChildIndex = priorityComparator.compare(heap[leftChildIndex], heap[rightChildIndex]) >= 0 ?
                            leftChildIndex : rightChildIndex;
                    }
                    if (priorityComparator.compare(heap[currentIndex], heap[biggestChildIndex]) < 0) {
                        swap(currentIndex, biggestChildIndex);
                        currentIndex = biggestChildIndex;
                        continue;
                    }
                }
                return item;
            }
        }
        return null;
	}

    /**
     * Tauscht die Position der Elemente
     * @param index0 Die Position des ersten Elements.
     * @param index1 Die Position des zweiten Elements.
     */
	private void swap(int index0, int index1) {
        T store = heap[index0];
        heap[index0] = heap[index1];
        heap[index1] = store;
        indexMap.put(heap[index0], index0);
        indexMap.put(heap[index1], index1);
	}

	@Override
	public T getFront() {
		if (size > 0) {
            return heap[0];
        }
        return null;
	}

	@Override
	public T deleteFront() {
		return delete(heap[0]);
	}

	@Override
	public int getPosition(T item) {
        int position = 0;
        for (int i = 0; i < size; i++) {
            if (priorityComparator.compare(item, heap[i]) < 0) {
                position++;
            }
        }
        return position;
	}

    @Override
    public boolean contains(T item) {
        return indexMap.containsKey(item);
    }

    @Override
	public void clear() {
        size = 0;
	}

	@Override
	public Comparator<T> getPriorityComparator() {
		return priorityComparator;
	}

    /**
     * Gibt die zugrundeliegende Heapstruktur zurück.
     * @return Die zugrundeliegende Heapstruktur.
     */
    public Object[] getInternalHeap() {
        return heap;
    }
}
