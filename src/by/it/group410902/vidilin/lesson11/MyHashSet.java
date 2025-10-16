package by.it.group410902.vidilin.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyHashSet<E> implements Set<E> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static class Node<E> {
        E data;
        Node<E> next;
        int hash;

        Node(E data, int hash, Node<E> next) {
            this.data = data;
            this.hash = hash;
            this.next = next;
        }
    }

    private Node<E>[] table;
    private int size;
    private final float loadFactor;
    private int threshold;

    @SuppressWarnings("unchecked")
    public MyHashSet() {
        this.table = (Node<E>[]) new Node[DEFAULT_CAPACITY];
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.threshold = (int) (DEFAULT_CAPACITY * DEFAULT_LOAD_FACTOR);
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    public MyHashSet(int initialCapacity) {
        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        this.table = (Node<E>[]) new Node[capacity];
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.threshold = (int) (capacity * DEFAULT_LOAD_FACTOR);
        this.size = 0;
    }

    // Обязательные методы

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object element) {
        if (element == null) {
            return containsNull();
        }

        int hash = hash(element);
        int index = (table.length - 1) & hash;

        Node<E> current = table[index];
        while (current != null) {
            if (current.hash == hash &&
                    (element == current.data || element.equals(current.data))) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean add(E element) {
        if (element == null) {
            return addNull();
        }

        int hash = hash(element);
        int index = (table.length - 1) & hash;

        // Проверяем, есть ли уже такой элемент
        Node<E> current = table[index];
        while (current != null) {
            if (current.hash == hash &&
                    (element == current.data || element.equals(current.data))) {
                return false; // Элемент уже существует
            }
            current = current.next;
        }

        // Добавляем новый элемент в начало цепочки
        table[index] = new Node<>(element, hash, table[index]);
        size++;

        if (size > threshold) {
            resize();
        }

        return true;
    }

    @Override
    public boolean remove(Object element) {
        if (element == null) {
            return removeNull();
        }

        int hash = hash(element);
        int index = (table.length - 1) & hash;

        Node<E> current = table[index];
        Node<E> prev = null;

        while (current != null) {
            if (current.hash == hash &&
                    (element == current.data || element.equals(current.data))) {

                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (Node<E> node : table) {
            Node<E> current = node;
            while (current != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(current.data);
                first = false;
                current = current.next;
            }
        }

        sb.append("]");
        return sb.toString();
    }

    // Вспомогательные методы

    private int hash(Object element) {
        int h = element.hashCode();
        return h ^ (h >>> 16); // Распределение битов для лучшего хеширования
    }

    private boolean containsNull() {
        Node<E> current = table[0];
        while (current != null) {
            if (current.data == null) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    private boolean addNull() {
        // Проверяем, есть ли уже null
        Node<E> current = table[0];
        while (current != null) {
            if (current.data == null) {
                return false;
            }
            current = current.next;
        }

        // Добавляем null
        table[0] = new Node<>(null, 0, table[0]);
        size++;

        if (size > threshold) {
            resize();
        }

        return true;
    }

    private boolean removeNull() {
        Node<E> current = table[0];
        Node<E> prev = null;

        while (current != null) {
            if (current.data == null) {
                if (prev == null) {
                    table[0] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = table.length << 1; // Удваиваем размер
        Node<E>[] newTable = (Node<E>[]) new Node[newCapacity];
        threshold = (int) (newCapacity * loadFactor);

        // Перехеширование всех элементов
        for (Node<E> head : table) {
            Node<E> current = head;
            while (current != null) {
                Node<E> next = current.next;
                int newIndex = (newCapacity - 1) & current.hash;
                current.next = newTable[newIndex];
                newTable[newIndex] = current;
                current = next;
            }
        }

        table = newTable;
    }

    // Остальные методы интерфейса Set (заглушки)

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int tableIndex = 0;
            private Node<E> current = null;
            private Node<E> next = findNext();

            private Node<E> findNext() {
                // Продолжаем с текущей позиции
                if (current != null && current.next != null) {
                    return current.next;
                }

                // Ищем следующий непустой бакет
                for (; tableIndex < table.length; tableIndex++) {
                    if (table[tableIndex] != null) {
                        return table[tableIndex++];
                    }
                }
                return null;
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                current = next;
                next = findNext();
                return current.data;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int index = 0;
        for (E element : this) {
            array[index++] = element;
        }
        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        }

        int index = 0;
        for (E element : this) {
            a[index++] = (T) element;
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
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
        boolean modified = false;
        for (E element : c) {
            if (add(element)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object element : c) {
            if (remove(element)) {
                modified = true;
            }
        }
        return modified;
    }
}