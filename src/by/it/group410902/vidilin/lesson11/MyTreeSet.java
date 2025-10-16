package by.it.group410902.vidilin.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyTreeSet<E extends Comparable<E>> implements Set<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int size;

    public MyTreeSet() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public MyTreeSet(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
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
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        // Бинарный поиск для нахождения позиции элемента
        int low = 0;
        int high = size - 1;
        int index = -1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            E midVal = (E) elements[mid];
            int cmp = element.compareTo(midVal);

            if (cmp < 0) {
                high = mid - 1;
            } else if (cmp > 0) {
                low = mid + 1;
            } else {
                // Элемент уже существует
                return false;
            }
        }

        index = low; // Позиция для вставки

        // Увеличиваем массив при необходимости
        ensureCapacity(size + 1);

        // Сдвигаем элементы вправо для освобождения места
        System.arraycopy(elements, index, elements, index + 1, size - index);

        // Вставляем новый элемент
        elements[index] = element;
        size++;
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        int index = binarySearch((E) element);
        if (index >= 0) {
            // Сдвигаем элементы влево для удаления
            int numMoved = size - index - 1;
            if (numMoved > 0) {
                System.arraycopy(elements, index + 1, elements, index, numMoved);
            }
            elements[--size] = null; // Помогаем сборщику мусора
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }
        return binarySearch((E) element) >= 0;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object element : collection) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean modified = false;
        for (E element : collection) {
            if (add(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean modified = false;
        for (Object element : collection) {
            if (remove(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;
        for (int i = size - 1; i >= 0; i--) {
            @SuppressWarnings("unchecked")
            E element = (E) elements[i];
            if (!collection.contains(element)) {
                remove(element);
                modified = true;
            }
        }
        return modified;
    }

    // Вспомогательные методы

    /**
     * Бинарный поиск элемента в массиве
     * @return индекс элемента или -(insertion point) - 1 если не найден
     */
    @SuppressWarnings("unchecked")
    private int binarySearch(E element) {
        int low = 0;
        int high = size - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            E midVal = (E) elements[mid];
            int cmp = element.compareTo(midVal);

            if (cmp < 0) {
                high = mid - 1;
            } else if (cmp > 0) {
                low = mid + 1;
            } else {
                return mid; // Элемент найден
            }
        }
        return -(low + 1); // Элемент не найден
    }

    /**
     * Увеличивает емкость массива при необходимости
     */
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = Math.max(elements.length * 2, minCapacity);
            Object[] newElements = new Object[newCapacity];
            System.arraycopy(elements, 0, newElements, 0, size);
            elements = newElements;
        }
    }

    // Методы интерфейса Set, которые не требуются по заданию
    // но должны быть реализованы

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Iterator not implemented");
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("toArray not implemented");
    }

    @Override
    public <T> T[] toArray(T[] array) {
        throw new UnsupportedOperationException("toArray not implemented");
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("equals not implemented");
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("hashCode not implemented");
    }
}