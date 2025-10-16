package by.it.group410902.vidilin.lesson12;

import java.util.Map;
import java.util.NoSuchElementException;

public class MyAvlMap implements Map<Integer, String> {

    private static class AvlNode {
        Integer key;
        String value;
        AvlNode left;
        AvlNode right;
        int height;

        AvlNode(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private AvlNode root;
    private int size;

    public MyAvlMap() {
        root = null;
        size = 0;
    }

    // Обязательные методы

    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        inOrderTraversal(root, sb);
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2); // Удаляем последнюю запятую и пробел
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        String[] oldValue = new String[1];
        root = put(root, key, value, oldValue);
        if (oldValue[0] == null) {
            size++;
        }
        return oldValue[0];
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) {
            return null;
        }

        String[] removedValue = new String[1];
        root = remove(root, (Integer) key, removedValue);
        if (removedValue[0] != null) {
            size--;
        }
        return removedValue[0];
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) {
            return null;
        }
        return get(root, (Integer) key);
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Integer)) {
            return false;
        }
        return get(root, (Integer) key) != null;
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

    // Вспомогательные методы АВЛ-дерева

    private int height(AvlNode node) {
        return node == null ? 0 : node.height;
    }

    private int balanceFactor(AvlNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private void updateHeight(AvlNode node) {
        if (node != null) {
            node.height = Math.max(height(node.left), height(node.right)) + 1;
        }
    }

    private AvlNode rotateRight(AvlNode y) {
        AvlNode x = y.left;
        AvlNode T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private AvlNode rotateLeft(AvlNode x) {
        AvlNode y = x.right;
        AvlNode T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    private AvlNode balance(AvlNode node) {
        if (node == null) {
            return null;
        }

        updateHeight(node);
        int balance = balanceFactor(node);

        // Left Left Case
        if (balance > 1 && balanceFactor(node.left) >= 0) {
            return rotateRight(node);
        }

        // Left Right Case
        if (balance > 1 && balanceFactor(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right Right Case
        if (balance < -1 && balanceFactor(node.right) <= 0) {
            return rotateLeft(node);
        }

        // Right Left Case
        if (balance < -1 && balanceFactor(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private AvlNode put(AvlNode node, Integer key, String value, String[] oldValue) {
        if (node == null) {
            return new AvlNode(key, value);
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = put(node.left, key, value, oldValue);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value, oldValue);
        } else {
            // Ключ уже существует
            oldValue[0] = node.value;
            node.value = value;
            return node;
        }

        return balance(node);
    }

    private AvlNode remove(AvlNode node, Integer key, String[] removedValue) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = remove(node.left, key, removedValue);
        } else if (cmp > 0) {
            node.right = remove(node.right, key, removedValue);
        } else {
            // Найден узел для удаления
            removedValue[0] = node.value;

            // Узел с одним или нулем потомков
            if (node.left == null || node.right == null) {
                AvlNode temp = (node.left != null) ? node.left : node.right;

                // Нет потомков
                if (temp == null) {
                    return null;
                } else {
                    // Один потомок
                    return temp;
                }
            } else {
                // Узел с двумя потомками
                AvlNode temp = findMin(node.right);
                node.key = temp.key;
                node.value = temp.value;
                node.right = remove(node.right, temp.key, new String[1]);
            }
        }

        return balance(node);
    }

    private AvlNode findMin(AvlNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private String get(AvlNode node, Integer key) {
        if (node == null) {
            return null;
        }

        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            return get(node.left, key);
        } else if (cmp > 0) {
            return get(node.right, key);
        } else {
            return node.value;
        }
    }

    private void inOrderTraversal(AvlNode node, StringBuilder sb) {
        if (node != null) {
            inOrderTraversal(node.left, sb);
            sb.append(node.key).append("=").append(node.value).append(", ");
            inOrderTraversal(node.right, sb);
        }
    }

    // Остальные методы интерфейса Map (заглушки)

    @Override
    public boolean containsValue(Object value) {
        return containsValue(root, value);
    }

    private boolean containsValue(AvlNode node, Object value) {
        if (node == null) {
            return false;
        }

        if (value == null ? node.value == null : value.equals(node.value)) {
            return true;
        }

        return containsValue(node.left, value) || containsValue(node.right, value);
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        for (Entry<? extends Integer, ? extends String> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public java.util.Set<Integer> keySet() {
        java.util.Set<Integer> keys = new java.util.HashSet<>();
        keySet(root, keys);
        return keys;
    }

    private void keySet(AvlNode node, java.util.Set<Integer> keys) {
        if (node != null) {
            keySet(node.left, keys);
            keys.add(node.key);
            keySet(node.right, keys);
        }
    }

    @Override
    public java.util.Collection<String> values() {
        java.util.Collection<String> values = new java.util.ArrayList<>();
        values(root, values);
        return values;
    }

    private void values(AvlNode node, java.util.Collection<String> values) {
        if (node != null) {
            values(node.left, values);
            values.add(node.value);
            values(node.right, values);
        }
    }

    @Override
    public java.util.Set<Entry<Integer, String>> entrySet() {
        java.util.Set<Entry<Integer, String>> entries = new java.util.HashSet<>();
        entrySet(root, entries);
        return entries;
    }

    private void entrySet(AvlNode node, java.util.Set<Entry<Integer, String>> entries) {
        if (node != null) {
            entrySet(node.left, entries);
            entries.add(new SimpleEntry(node.key, node.value));
            entrySet(node.right, entries);
        }
    }

    // Простая реализация Entry для совместимости
    private static class SimpleEntry implements Entry<Integer, String> {
        private final Integer key;
        private String value;

        SimpleEntry(Integer key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            String oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry<?, ?> other = (Entry<?, ?>) obj;
            return (key == null ? other.getKey() == null : key.equals(other.getKey())) &&
                    (value == null ? other.getValue() == null : value.equals(other.getValue()));
        }

        @Override
        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }
    }

    // Дополнительные методы для тестирования балансировки

    /**
     * Проверяет, является ли дерево сбалансированным АВЛ-деревом
     */
    public boolean isBalanced() {
        return isBalanced(root);
    }

    private boolean isBalanced(AvlNode node) {
        if (node == null) {
            return true;
        }

        int balance = balanceFactor(node);
        if (Math.abs(balance) > 1) {
            return false;
        }

        return isBalanced(node.left) && isBalanced(node.right);
    }

    /**
     * Возвращает высоту дерева
     */
    public int height() {
        return height(root);
    }
}