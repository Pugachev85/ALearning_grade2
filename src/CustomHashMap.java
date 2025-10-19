import java.util.Objects;

public class CustomHashMap<K, V> {
    // Начальный размер массива корзин
    private static final int INITIAL_CAPACITY = 16;
    // Коэффициент загрузки (при заполнении > 75% произойдёт resize)
    private static final float LOAD_FACTOR = 0.75f;

    // Внутренний массив корзин
    private Node[] buckets;
    // Текущее число элементов
    private int size = 0;
    // Пороговое значение для resize
    private int threshold = (int) (INITIAL_CAPACITY * LOAD_FACTOR);

    // Конструктор
    public CustomHashMap() {
        buckets = new Node[INITIAL_CAPACITY];
    }

    // Вспомогательный класс узла
    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    // Хеш-функция (упрощённая)
    private int hash(Object key) {
        return key == null ? 0 : key.hashCode() & (buckets.length - 1);
    }

    // Добавление элемента
    public void put(K key, V value) {
        if (size >= threshold) {
            resize();
        }
        int index = hash(key);
        Node<K, V> node = buckets[index];

        // Если корзина пуста — добавляем новый узел
        if (node == null) {
            buckets[index] = new Node<>(key, value);
            size++;
            return;
        }

        // Иначе ищем узел с таким же ключом в цепочке
        while (node != null) {
            if (Objects.equals(key, node.key)) {
                node.value = value; // Обновляем значение
                return;
            }
            if (node.next == null) {
                break;
            }
            node = node.next;
        }
        // Добавляем новый узел в конец цепочки
        node.next = new Node<>(key, value);
        size++;
    }

    // Получение значения по ключу
    public V get(K key) {
        int index = hash(key);
        Node<K, V> node = buckets[index];

        while (node != null) {
            if (Objects.equals(key, node.key)) {
                return node.value;
            }
            node = node.next;
        }
        return null; // Ключ не найден
    }

    // Удаление элемента
    public void remove(K key) {
        int index = hash(key);
        Node<K, V> node = buckets[index];
        Node<K, V> prev = null;

        while (node != null) {
            if (Objects.equals(key, node.key)) {
                if (prev == null) {
                    buckets[index] = node.next; // Удаляем первый узел
                } else {
                    prev.next = node.next; // Удаляем промежуточный узел
                }
                size--;
                return;
            }
            prev = node;
            node = node.next;
        }
    }

    // Расширение массива (resize)
    private void resize() {
        Node<K, V>[] oldBuckets = buckets;
        buckets = new Node[oldBuckets.length * 2];
        threshold = (int) (buckets.length * LOAD_FACTOR);
        size = 0;

        // Перехешируем все элементы
        for (Node<K, V> head : oldBuckets) {
            while (head != null) {
                put(head.key, head.value);
                head = head.next;
            }
        }
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

                // Добавляем запятую, если есть ещё элементы
                if (hasNextElement(i, node)) {
                    sb.append(", ");
                }

                node = node.next;
            }
        }

        sb.append("}");
        return sb.toString();
    }

    // Вспомогательный метод: проверяет, есть ли ещё элементы после текущего
    private boolean hasNextElement(int currentBucketIndex, Node<K, V> currentNode) {
        // Проверяем текущий список — есть ли следующий узел
        if (currentNode.next != null) {
            return true;
        }

        // Проверяем следующие корзины
        for (int i = currentBucketIndex + 1; i < buckets.length; i++) {
            if (buckets[i] != null) {
                return true;
            }
        }
        return false;
    }

    // Вспомогательные методы (для отладки)
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
