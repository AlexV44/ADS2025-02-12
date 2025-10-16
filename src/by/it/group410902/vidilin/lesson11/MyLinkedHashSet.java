package by.it.group410902.vidilin.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyLinkedHashSet<E> implements Set<E> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    // Узел для хеш-таблицы (цепочки коллизий)
    private static class HashNode<E> {
        E data;
        HashNode<E> next;
        int hash;
        LinkedNode<E> linkedNode; // Ссылка на узел в связном списке порядка

        HashNode(E data, int hash, HashNode<E> next, LinkedNode<E> linkedNode) {
            this.data = data;
            this.hash = hash;
            this.next = next;
            this.linkedNode = linkedNode;
        }
    }

    // Узел для поддержания порядка добавления
    private static class LinkedNode<E> {
        E data;
        LinkedNode<E> prev;
        LinkedNode<E> next;

        LinkedNode(E data) {
            this.data = data;
        }
    }

    private HashNode<E>[] table;
    private LinkedNode<E> head; // Первый добавленный элемент
    private LinkedNode<E> tail; // Последний добавленный элемент
    private int size;
    private final float loadFactor;
    private int threshold;

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {
        this.table = (HashNode<E>[]) new HashNode[DEFAULT_CAPACITY];
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.threshold = (int) (DEFAULT_CAPACITY * DEFAULT_LOAD_FACTOR);
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet(int initialCapacity) {
        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        this.table = (HashNode<E>[]) new HashNode[capacity];
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.threshold = (int) (capacity * DEFAULT_LOAD_FACTOR);
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    // Обязательные методы

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        LinkedNode<E> current = head;
        boolean first = true;

        while (current != null) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(current.data);
            first = false;
            current = current.next;
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
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public boolean contains(Object element) {
        if (element == null) {
            return containsNull();
        }

        int hash = hash(element);
        int index = (table.length - 1) & hash;

        HashNode<E> current = table[index];
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
        HashNode<E> current = table[index];
        while (current != null) {
            if (current.hash == hash &&
                    (element == current.data || element.equals(current.data))) {
                return false; // Элемент уже существует
            }
            current = current.next;
        }

        // Создаем узел для связного списка порядка
        LinkedNode<E> linkedNode = new LinkedNode<>(element);

        // Добавляем в связный список порядка
        if (tail == null) {
            // Первый элемент
            head = linkedNode;
            tail = linkedNode;
        } else {
            tail.next = linkedNode;
            linkedNode.prev = tail;
            tail = linkedNode;
        }

        // Добавляем в хеш-таблицу
        table[index] = new HashNode<>(element, hash, table[index], linkedNode);
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

        HashNode<E> current = table[index];
        HashNode<E> prev = null;

        while (current != null) {
            if (current.hash == hash &&
                    (element == current.data || element.equals(current.data))) {

                // Удаляем из связного списка порядка
                removeLinkedNode(current.linkedNode);

                // Удаляем из хеш-таблицы
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
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object element : c) {
            if (remove(element)) {
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
            E element = it.next();
            if (!c.contains(element)) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    // Вспомогательные методы

    private int hash(Object element) {
        int h = element.hashCode();
        return h ^ (h >>> 16);
    }

    private boolean containsNull() {
        HashNode<E> current = table[0];
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
        HashNode<E> current = table[0];
        while (current != null) {
            if (current.data == null) {
                return false;
            }
            current = current.next;
        }

        // Создаем узел для связного списка порядка
        LinkedNode<E> linkedNode = new LinkedNode<>(null);

        // Добавляем в связный список порядка
        if (tail == null) {
            head = linkedNode;
            tail = linkedNode;
        } else {
            tail.next = linkedNode;
            linkedNode.prev = tail;
            tail = linkedNode;
        }

        // Добавляем в хеш-таблицу
        table[0] = new HashNode<>(null, 0, table[0], linkedNode);
        size++;

        if (size > threshold) {
            resize();
        }

        return true;
    }

    private boolean removeNull() {
        HashNode<E> current = table[0];
        HashNode<E> prev = null;

        while (current != null) {
            if (current.data == null) {
                // Удаляем из связного списка порядка
                removeLinkedNode(current.linkedNode);

                // Удаляем из хеш-таблицы
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

    private void removeLinkedNode(LinkedNode<E> node) {
        if (node.prev == null) {
            // Удаляем голову
            head = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) {
            // Удаляем хвост
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }

        // Очищаем ссылки
        node.prev = null;
        node.next = null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = table.length << 1;
        HashNode<E>[] newTable = (HashNode<E>[]) new HashNode[newCapacity];
        threshold = (int) (newCapacity * loadFactor);

        // Перехеширование всех элементов
        for (HashNode<E> head : table) {
            HashNode<E> current = head;
            while (current != null) {
                HashNode<E> next = current.next;
                int newIndex = (newCapacity - 1) & current.hash;
                current.next = newTable[newIndex];
                newTable[newIndex] = current;
                current = next;
            }
        }

        table = newTable;
    }

    // Остальные методы интерфейса Set

    @Override
    public Iterator<E> iterator() {
        return new LinkedHashSetIterator();
    }

    private class LinkedHashSetIterator implements Iterator<E> {
        private LinkedNode<E> current = head;
        private LinkedNode<E> lastReturned = null;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            lastReturned = current;
            E data = current.data;
            current = current.next;
            return data;
        }

        @Override
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }

            // Удаляем элемент из множества
            MyLinkedHashSet.this.remove(lastReturned.data);
            lastReturned = null;
        }
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
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Set)) {
            return false;
        }

        Set<?> other = (Set<?>) obj;
        if (size != other.size()) {
            return false;
        }

        return containsAll(other);
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (E element : this) {
            if (element != null) {
                hashCode += element.hashCode();
            }
        }
        return hashCode;
    }
}
