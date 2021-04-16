package node;

public class SingleNode<K,V> {
    K key;
    V value;
    SingleNode<K,V> next;

    public SingleNode(K key, V value, SingleNode<K, V> next) {
        this.key = key;
        this.value = value;
        this.next = next;

    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public SingleNode<K, V> getNext() {
        return next;
    }

    public void setNext(SingleNode<K, V> next) {
        this.next = next;
    }
}
