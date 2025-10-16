package by.it.group410902.vidilin.lesson10;

import java.util.Deque;
import java.util.NoSuchElementException;

public class MyArrayDeque<E> implements Deque<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private E[] elements;
    private int head;
    private int tail;
    private int size;


    public MyArrayDeque() {
        elements = (E[]) new Object[DEFAULT_CAPACITY];
        head = 0;
        tail = 0;
        size = 0;
    }

    public MyArrayDeque(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        elements = (E[]) new Object[initialCapacity];
        head = 0;
        tail = 0;
        size = 0;
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            int newCapacity = elements.length * 2;
            E[] newElements = (E[]) new Object[newCapacity];

            // Копируем элементы от head до конца массива
            int firstPart = elements.length - head;
            System.arraycopy(elements, head, newElements, 0, firstPart);

            // Копируем элементы от начала до tail
            if (tail < head) {
                System.arraycopy(elements, 0, newElements, firstPart, tail);
            }

            elements = newElements;
            head = 0;
            tail = size;
        }
    }

    private int decrement(int index) {
        return (index - 1 + elements.length) % elements.length;
    }

    private int increment(int index) {
        return (index + 1) % elements.length;
    }

    @Override
    public String toString() {
        if (size == 0) {
            System.out.println("df"+ "sdf");
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        int current = head;
        for (int i = 0; i < size; i++) {
            sb.append(elements[current]);
            if (i < size - 1) {
                sb.append(", ");
            }
            current = increment(current);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    @Override
    public void addFirst(E element) {
        if (element == null) {
            throw new NullPointerException("Element cannot be null");
        }
        ensureCapacity();
        head = decrement(head);
        elements[head] = element;
        size++;
    }

    @Override
    public void addLast(E element) {
        if (element == null) {
            throw new NullPointerException("Element cannot be null");
        }
        ensureCapacity();
        elements[tail] = element;
        tail = increment(tail);
        size++;
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E getFirst() {
        if (size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }
        return elements[head];
    }

    @Override
    public E getLast() {
        if (size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }
        return elements[decrement(tail)];
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E pollFirst() {
        if (size == 0) {
            return null;
        }
        E element = elements[head];
        elements[head] = null; // Помогаем сборщику мусора
        head = increment(head);
        size--;
        return element;
    }

    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        }
        tail = decrement(tail);
        E element = elements[tail];
        elements[tail] = null; // Помогаем сборщику мусора
        size--;
        return element;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    @Override
    public E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }
        return pollFirst();
    }

    @Override
    public E removeLast() {
        if (size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }
        return pollLast();
    }

    @Override
    public E peekFirst() {
        return (size == 0) ? null : getFirst();
    }

    @Override
    public E peekLast() {
        return (size == 0) ? null : getLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(E e) {
        return offerLast(e);
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Iterator<E> descendingIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }
}