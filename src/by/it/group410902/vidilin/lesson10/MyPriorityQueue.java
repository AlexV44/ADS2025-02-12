package by.it.group410902.vidilin.lesson10;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class MyPriorityQueue<E> implements Queue<E> {

    private static final int DEFAULT_CAPACITY = 10;
    private Object[] heap;
    private int size;
    private final Comparator<? super E> comparator;

    public MyPriorityQueue() {
        this.heap = new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = null;
    }

    public MyPriorityQueue(Comparator<? super E> comparator) {
        this.heap = new Object[DEFAULT_CAPACITY];
        this.size = 0;
        this.comparator = comparator;
    }

    public MyPriorityQueue(Collection<? extends E> c) {
        this();
        for (E element : c) {
            offer(element);
        }
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(heap[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean add(E element) {
        return offer(element);
    }

    @Override
    public E remove() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return poll();
    }

    @Override
    public boolean remove(Object element) {
        for (int i = 0; i < size; i++) {
            if (element == null ? heap[i] == null : element.equals(heap[i])) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(Object element) {
        for (int i = 0; i < size; i++) {
            if (element == null ? heap[i] == null : element.equals(heap[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean offer(E element) {
        if (element == null) {
            throw new NullPointerException("Cannot add null element");
        }

        ensureCapacity();
        heap[size] = element;
        siftUp(size);
        size++;
        return true;
    }

    @Override
    public E poll() {
        if (isEmpty()) {
            return null;
        }

        E result = getElement(0);
        removeAt(0);
        return result;
    }

    @Override
    public E peek() {
        return isEmpty() ? null : getElement(0);
    }

    @Override
    public E element() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return getElement(0);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object element : c) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == this) {
            throw new IllegalArgumentException("Cannot add collection to itself");
        }

        boolean modified = false;
        for (E element : c) {
            if (offer(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // Создаем временный массив для элементов, которые нужно сохранить
        Object[] newHeap = new Object[heap.length];
        int newSize = 0;
        boolean modified = false;

        for (int i = 0; i < size; i++) {
            E element = getElement(i);
            if (!c.contains(element)) {
                newHeap[newSize++] = element;
            } else {
                modified = true;
            }
        }

        if (modified) {
            this.heap = newHeap;
            this.size = newSize;
            heapify(); // Восстанавливаем свойства кучи
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // Создаем временный массив для элементов, которые нужно сохранить
        Object[] newHeap = new Object[heap.length];
        int newSize = 0;
        boolean modified = false;

        for (int i = 0; i < size; i++) {
            E element = getElement(i);
            if (c.contains(element)) {
                newHeap[newSize++] = element;
            } else {
                modified = true;
            }
        }

        if (modified) {
            this.heap = newHeap;
            this.size = newSize;
            heapify(); // Восстанавливаем свойства кучи
        }

        return modified;
    }

    // Вспомогательные методы для работы с кучей

    @SuppressWarnings("unchecked")
    private E getElement(int index) {
        return (E) heap[index];
    }

    private void ensureCapacity() {
        if (size == heap.length) {
            int newCapacity = heap.length * 2;
            Object[] newHeap = new Object[newCapacity];
            System.arraycopy(heap, 0, newHeap, 0, size);
            heap = newHeap;
        }
    }

    private void siftUp(int index) {
        E element = getElement(index);
        while (index > 0) {
            int parentIndex = (index - 1) >>> 1;
            E parent = getElement(parentIndex);
            if (compare(element, parent) >= 0) {
                break;
            }
            heap[index] = parent;
            index = parentIndex;
        }
        heap[index] = element;
    }

    private void siftDown(int index) {
        E element = getElement(index);
        int half = size >>> 1;
        while (index < half) {
            int childIndex = (index << 1) + 1;
            E child = getElement(childIndex);
            int rightIndex = childIndex + 1;

            if (rightIndex < size && compare(getElement(rightIndex), child) < 0) {
                childIndex = rightIndex;
                child = getElement(childIndex);
            }

            if (compare(element, child) <= 0) {
                break;
            }

            heap[index] = child;
            index = childIndex;
        }
        heap[index] = element;
    }

    @SuppressWarnings("unchecked")
    private int compare(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        } else {
            return ((Comparable<? super E>) a).compareTo(b);
        }
    }

    private void removeAt(int index) {
        size--;
        if (size > index) {
            E moved = getElement(size);
            heap[index] = moved;
            siftDown(index);
            if (heap[index] == moved) {
                siftUp(index);
            }
        }
        heap[size] = null;
    }

    private void heapify() {
        for (int i = (size >>> 1) - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    // Реализация Iterator
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return getElement(currentIndex++);
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        System.arraycopy(heap, 0, array, 0, size);
        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        }
        System.arraycopy(heap, 0, a, 0, size);

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }
}