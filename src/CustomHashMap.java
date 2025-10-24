import java.util.Objects;

public class CustomHashMap<K, V> implements CustomHashMapInterface<K, V> {

    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Node<K, V>[] buckets;

    private int size = 0;
    private int threshold = (int) (INITIAL_CAPACITY * LOAD_FACTOR);

    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        this.buckets = (Node<K, V>[]) new Node[INITIAL_CAPACITY];
    }

    private int hash(Object key) {
        return key == null ? 0 : key.hashCode() & (buckets.length - 1);
    }

    private void resize() {
        Node<K, V>[] oldBuckets = buckets;
        @SuppressWarnings("unchecked")
        Node<K, V>[] newBuckets = (Node<K, V>[]) new Node[oldBuckets.length * 2];
        this.buckets = newBuckets;
        this.threshold = (int) (newBuckets.length * LOAD_FACTOR);
        this.size = 0;

        for (Node<K, V> head : oldBuckets) {
            while (head != null) {
                put(head.key, head.value);
                head = head.next;
            }
        }
    }

    private boolean hasNextElement(int currentBucketIndex, Node<K, V> currentNode) {
        if (currentNode.next != null) {
            return true;
        }
        for (int i = currentBucketIndex + 1; i < buckets.length; i++) {
            if (buckets[i] != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void put(K key, V value) {
        if (size >= threshold) {
            resize();
        }
        int index = hash(key);
        Node<K, V> node = buckets[index];

        if (node == null) {
            buckets[index] = new Node<>(key, value);
            size++;
            return;
        }

        while (node != null) {
            if (Objects.equals(key, node.key)) {
                node.value = value;
                return;
            }
            if (node.next == null) {
                break;
            }
            node = node.next;
        }
        node.next = new Node<>(key, value);
        size++;
    }

    @Override
    public V get(K key) {
        int index = hash(key);
        Node<K, V> node = buckets[index];

        while (node != null) {
            if (Objects.equals(key, node.key)) {
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    @Override
    public void remove(K key) {
        int index = hash(key);
        Node<K, V> node = buckets[index];
        Node<K, V> prev = null;

        while (node != null) {
            if (Objects.equals(key, node.key)) {
                if (prev == null) {
                    buckets[index] = node.next;
                } else {
                    prev.next = node.next;
                }
                size--;
                return;
            }
            prev = node;
            node = node.next;
        }
    }

    @Override
    public boolean containsKey(K key) {
        int index = hash(key);
        Node<K, V> node = buckets[index];

        while (node != null) {
            if (Objects.equals(key, node.key)) {
                return true;
            }
            node = node.next;
        }
        return false;
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
    public String toString() {
        if (size == 0) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        for (int i = 0; i < buckets.length; i++) {
            Node<K, V> node = buckets[i];
            while (node != null) {
                sb.append(node.key);
                sb.append("=");
                sb.append(node.value);

                if (hasNextElement(i, node)) {
                    sb.append(", ");
                }

                node = node.next;
            }
        }

        sb.append("}");

        return sb.toString();
    }

    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}