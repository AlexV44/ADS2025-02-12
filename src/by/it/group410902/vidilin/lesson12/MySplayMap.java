package by.it.group410902.vidilin.lesson12;

import java.util.*;

public class MySplayMap implements NavigableMap<Integer, String> {

    private static class Node {
        Integer key;
        String value;
        Node left, right, parent;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size;

    public MySplayMap() {
        root = null;
        size = 0;
    }

    // Вспомогательные методы для splay-дерева

    private void rotateLeft(Node x) {
        Node y = x.right;
        if (y != null) {
            x.right = y.left;
            if (y.left != null) {
                y.left.parent = x;
            }
            y.parent = x.parent;
        }

        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        if (y != null) {
            y.left = x;
        }
        x.parent = y;
    }

    private void rotateRight(Node x) {
        Node y = x.left;
        if (y != null) {
            x.left = y.right;
            if (y.right != null) {
                y.right.parent = x;
            }
            y.parent = x.parent;
        }

        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }

        if (y != null) {
            y.right = x;
        }
        x.parent = y;
    }

    private void splay(Node x) {
        while (x.parent != null) {
            if (x.parent.parent == null) {
                if (x.parent.left == x) {
                    rotateRight(x.parent);
                } else {
                    rotateLeft(x.parent);
                }
            } else if (x.parent.left == x && x.parent.parent.left == x.parent) {
                rotateRight(x.parent.parent);
                rotateRight(x.parent);
            } else if (x.parent.right == x && x.parent.parent.right == x.parent) {
                rotateLeft(x.parent.parent);
                rotateLeft(x.parent);
            } else if (x.parent.left == x && x.parent.parent.right == x.parent) {
                rotateRight(x.parent);
                rotateLeft(x.parent);
            } else {
                rotateLeft(x.parent);
                rotateRight(x.parent);
            }
        }
    }

    private Node findNode(Integer key) {
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                splay(current);
                return current;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }

    // Основные методы Map

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        inOrderTraversal(root, sb);
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2); // Remove last ", "
        }
        sb.append("}");
        return sb.toString();
    }

    private void inOrderTraversal(Node node, StringBuilder sb) {
        if (node != null) {
            inOrderTraversal(node.left, sb);
            sb.append(node.key).append("=").append(node.value).append(", ");
            inOrderTraversal(node.right, sb);
        }
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException("Key cannot be null");

        Node current = root;
        Node parent = null;

        while (current != null) {
            parent = current;
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                String oldValue = current.value;
                current.value = value;
                splay(current);
                return oldValue;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        Node newNode = new Node(key, value);
        newNode.parent = parent;

        if (parent == null) {
            root = newNode;
        } else if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        splay(newNode);
        size++;
        return null;
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) return null;
        Integer k = (Integer) key;

        Node node = findNode(k);
        if (node == null) return null;

        String removedValue = node.value;

        if (node.left == null) {
            transplant(node, node.right);
        } else if (node.right == null) {
            transplant(node, node.left);
        } else {
            Node min = minimum(node.right);
            if (min.parent != node) {
                transplant(min, min.right);
                min.right = node.right;
                min.right.parent = min;
            }
            transplant(node, min);
            min.left = node.left;
            min.left.parent = min;
        }

        size--;
        return removedValue;
    }

    private void transplant(Node u, Node v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != null) {
            v.parent = u.parent;
        }
    }

    private Node minimum(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) return null;
        Node node = findNode((Integer) key);
        return node != null ? node.value : null;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Integer)) return false;
        return findNode((Integer) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof String)) return false;
        return containsValueRecursive(root, (String) value);
    }

    private boolean containsValueRecursive(Node node, String value) {
        if (node == null) return false;
        if (value.equals(node.value)) return true;
        return containsValueRecursive(node.left, value) ||
                containsValueRecursive(node.right, value);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // NavigableMap methods

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        return new SubMap(null, toKey, false);
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        return new SubMap(fromKey, null, true);
    }

    @Override
    public Integer firstKey() {
        if (root == null) throw new NoSuchElementException();
        Node min = minimum(root);
        splay(min);
        return min.key;
    }

    @Override
    public Integer lastKey() {
        if (root == null) throw new NoSuchElementException();
        Node max = maximum(root);
        splay(max);
        return max.key;
    }

    private Node maximum(Node node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    @Override
    public Integer lowerKey(Integer key) {
        Node result = lowerNode(key);
        if (result != null) {
            splay(result);
            return result.key;
        }
        return null;
    }

    private Node lowerNode(Integer key) {
        Node current = root;
        Node result = null;

        while (current != null) {
            if (current.key.compareTo(key) < 0) {
                result = current;
                current = current.right;
            } else {
                current = current.left;
            }
        }
        return result;
    }

    @Override
    public Integer floorKey(Integer key) {
        Node result = floorNode(key);
        if (result != null) {
            splay(result);
            return result.key;
        }
        return null;
    }

    private Node floorNode(Integer key) {
        Node current = root;
        Node result = null;

        while (current != null) {
            int cmp = current.key.compareTo(key);
            if (cmp == 0) {
                return current;
            } else if (cmp < 0) {
                result = current;
                current = current.right;
            } else {
                current = current.left;
            }
        }
        return result;
    }

    @Override
    public Integer ceilingKey(Integer key) {
        Node result = ceilingNode(key);
        if (result != null) {
            splay(result);
            return result.key;
        }
        return null;
    }

    private Node ceilingNode(Integer key) {
        Node current = root;
        Node result = null;

        while (current != null) {
            int cmp = current.key.compareTo(key);
            if (cmp == 0) {
                return current;
            } else if (cmp > 0) {
                result = current;
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return result;
    }

    @Override
    public Integer higherKey(Integer key) {
        Node result = higherNode(key);
        if (result != null) {
            splay(result);
            return result.key;
        }
        return null;
    }

    private Node higherNode(Integer key) {
        Node current = root;
        Node result = null;

        while (current != null) {
            if (current.key.compareTo(key) > 0) {
                result = current;
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return result;
    }

    // Реализация SubMap для headMap и tailMap
    private class SubMap extends AbstractMap<Integer, String> implements SortedMap<Integer, String> {
        private final Integer fromKey;
        private final Integer toKey;
        private final boolean fromInclusive;

        SubMap(Integer fromKey, Integer toKey, boolean fromInclusive) {
            this.fromKey = fromKey;
            this.toKey = toKey;
            this.fromInclusive = fromInclusive;
        }

        @Override
        public Set<Entry<Integer, String>> entrySet() {
            Set<Entry<Integer, String>> entries = new TreeSet<>(Comparator.comparing(Entry::getKey));
            addEntries(root, entries);
            return entries;
        }

        private void addEntries(Node node, Set<Entry<Integer, String>> entries) {
            if (node == null) return;

            addEntries(node.left, entries);

            // Проверяем, попадает ли ключ в диапазон
            boolean inRange = true;
            if (fromKey != null) {
                if (fromInclusive) {
                    inRange = node.key >= fromKey;
                } else {
                    inRange = node.key > fromKey;
                }
            }
            if (toKey != null) {
                inRange = inRange && node.key < toKey;
            }

            if (inRange) {
                entries.add(new AbstractMap.SimpleEntry<>(node.key, node.value));
            }

            addEntries(node.right, entries);
        }

        @Override
        public Comparator<? super Integer> comparator() {
            return null;
        }

        @Override
        public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
            return new SubMap(fromKey, toKey, true);
        }

        @Override
        public SortedMap<Integer, String> headMap(Integer toKey) {
            return new SubMap(null, toKey, true);
        }

        @Override
        public SortedMap<Integer, String> tailMap(Integer fromKey) {
            return new SubMap(fromKey, null, true);
        }

        @Override
        public Integer firstKey() {
            for (Entry<Integer, String> entry : entrySet()) {
                return entry.getKey();
            }
            throw new NoSuchElementException();
        }

        @Override
        public Integer lastKey() {
            Integer last = null;
            for (Entry<Integer, String> entry : entrySet()) {
                last = entry.getKey();
            }
            if (last == null) throw new NoSuchElementException();
            return last;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            boolean first = true;
            for (Entry<Integer, String> entry : entrySet()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
    }

    // Не реализованные методы (для упрощения)

    @Override
    public Entry<Integer, String> lowerEntry(Integer key) { return null; }

    @Override
    public Entry<Integer, String> floorEntry(Integer key) { return null; }

    @Override
    public Entry<Integer, String> ceilingEntry(Integer key) { return null; }

    @Override
    public Entry<Integer, String> higherEntry(Integer key) { return null; }

    @Override
    public Entry<Integer, String> firstEntry() { return null; }

    @Override
    public Entry<Integer, String> lastEntry() { return null; }

    @Override
    public Entry<Integer, String> pollFirstEntry() { return null; }

    @Override
    public Entry<Integer, String> pollLastEntry() { return null; }

    @Override
    public NavigableMap<Integer, String> descendingMap() { return null; }

    @Override
    public NavigableSet<Integer> navigableKeySet() { return null; }

    @Override
    public NavigableSet<Integer> descendingKeySet() { return null; }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive, Integer toKey, boolean toInclusive) { return null; }

    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) { return null; }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) { return null; }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) { return null; }

    @Override
    public Comparator<? super Integer> comparator() { return null; }

    @Override
    public Set<Integer> keySet() { return null; }

    @Override
    public Collection<String> values() { return null; }

    @Override
    public Set<Entry<Integer, String>> entrySet() { return null; }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) { }
}