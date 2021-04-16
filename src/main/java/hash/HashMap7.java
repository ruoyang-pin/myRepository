package hash;


import org.apache.commons.collections.functors.InvokerTransformer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HashMap7<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable {

    private static final int MAXINUM_CAPACITY = 1 << 30;

    private static final long serialVersionUID = 5612913506965932873L;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_CAPACITY = 10;
    static final Entry<?, ?>[] EMPTY_TABLE = {};
    transient Entry<K, V>[] table = ( Entry<K, V>[] ) EMPTY_TABLE;
    transient int size;
    int threshold;
    final float loadFactor;
    transient int modCount;
    static final int ALTERNATIVE_HASHING_THRESHOLD_DEFAULT = Integer.MAX_VALUE;
    transient int hashSeed = 0;
    private Set<K> keySet;
    private Set<V> valueSet;
    private Set<Map.Entry<K, V>> entrySet;


    private static class Holder {
        /**
         * Table capacity above which to switch to use alternative hashing.
         */
        static final int ALTERNATIVE_HASHING_THRESHOLD;

        static {
            String altThreshold = java.security.AccessController.doPrivileged(
                    new sun.security.action.GetPropertyAction(
                            "jdk.map.althashing.threshold"));

            int threshold;
            try {
                threshold = (null != altThreshold)
                        ? Integer.parseInt(altThreshold)
                        : ALTERNATIVE_HASHING_THRESHOLD_DEFAULT;

                // disable alternative hashing if -1
                if (threshold == -1) {
                    threshold = Integer.MAX_VALUE;
                }

                if (threshold < 0) {
                    throw new IllegalArgumentException("value must be positive integer.");
                }
            } catch (IllegalArgumentException failed) {
                throw new Error("Illegal value for 'jdk.map.althashing.threshold'", failed);
            }

            ALTERNATIVE_HASHING_THRESHOLD = threshold;
        }
    }

    public HashMap7(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("illegal  Initial  Capacity:" + initialCapacity);
        if (initialCapacity > MAXINUM_CAPACITY)
            initialCapacity = MAXINUM_CAPACITY;
        if (loadFactor < 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("illegal Load  Factor:" + loadFactor);
        this.threshold = initialCapacity;
        this.loadFactor = loadFactor;
    }

    public HashMap7(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashMap7() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }


    final int hash(Object k) {
        int h = hashSeed;
        h ^= k.hashCode();
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    static int indexFor(int h, int length) {
        return h & (length - 1);
    }

    public static class Entry<K, V> implements Map.Entry<K, V> {
        final K key;
        V value;
        int hash;
        Entry<K, V> next;

        public Entry(K key, V value, int hash, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V v) {
            V oldValue = value;
            value = v;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry entry = ( Map.Entry ) o;
            Object key = entry.getKey();
            if (this.key == key || (key != null && key.equals(this.key))) {
                Object value = entry.getValue();
                if (this.value == value || (value != null && value.equals(this.value))) {
                    return true;
                }
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(key, value);
        }

        //当hash表中有记录被替换时调用此方法
        void recordAccess(HashMap7<K, V> m) {
        }

        // 当hash表中有记录被删除时调用此方法
        void recordRemoval(HashMap7<K, V> m) {
        }

    }


    void addEntry(int h, K k, V v, int bucketIndex) {
        if (size > threshold && (table[bucketIndex] != null)) {
            reSize(table.length << 1);
            h = k == null ? 0 : hash(k);
            bucketIndex = indexFor(h, table.length);
        }
        creatEntry(h, k, v, bucketIndex);
    }

    void creatEntry(int h, K k, V v, int bucketIndex) {
        Entry<K, V> entry = table[bucketIndex];
        table[bucketIndex] = new Entry<>(k, v, h, entry);
        size++;
    }

    Entry<K, V> removeEntry(Object key) {
        if (size == 0)
            return null;
        int h = key == null ? 0 : hash(key);
        int i = indexFor(h, table.length);
        Entry<K, V> prev = null;
        for (Entry<K, V> e = table[i]; e != null; e = e.next) {
            if (e.hash == h && (e.key == key || (e.key.equals(key)))) {
                modCount++;
                size--;
                if (prev == null) {
                    table[i] = e.next;
                } else {
                    prev.next = e.next;
                }
                e.recordRemoval(this);
                return e;
            }
            prev = e;
        }
        return null;
    }


    void reSize(int newCapacity) {
        if (threshold == MAXINUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }
        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable, true);
        table = newTable;
        threshold = ( int ) Math.min(newCapacity * loadFactor, MAXINUM_CAPACITY + 1);
    }

    void transfer(Entry<K, V>[] newTable, boolean rehash) {
        int newCapacity = newTable.length;
        for (Entry<K, V> entry : table) {
            while (entry != null) {
                Entry<K, V> next = entry.next;
                if (rehash) {
                    entry.hash = entry.key == null ? 0 : hash(entry.key);
                }
                int index = indexFor(entry.hash, newCapacity);
                entry.next = newTable[index];
                newTable[index] = entry;
                entry = next;
            }
        }
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
    public boolean containsKey(Object key) {
        V v = get(key);
        return v != null;
    }

    @Override
    public boolean containsValue(Object value) {
        if (size == 0)
            return false;
        Entry[] t = table;
        for (Entry<K, V> e : table) {
            for (Entry<K, V> el = e; el != null; el = el.next) {
                if (value == el.value || (value != null && value.equals(el.value))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        if (table.length == 0)
            return null;
        if (key == null)
            return getForNull(key);
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        for (Entry<K, V> e = table[i]; e != null; e = e.next) {
            if (e.hash == hash && (e.key == key || (key.equals(e.key)))) {
                return e.value;
            }
        }
        return null;
    }

    private V getForNull(Object key) {
        if (table.length == 0)
            return null;
        for (Entry<K, V> e = table[0]; e != null; e = e.next) {
            if (e.key == null) {
                return e.value;
            }
        }
        return null;
    }


    @Override
    public V put(K key, V value) {
        if (table == EMPTY_TABLE) {
            initTable(threshold);
        }
        if (key == null) {
            return putForNull(value);
        }
        int h = hash(key);
        int index = indexFor(h, table.length);
        for (Entry<K, V> e = table[index]; e != null; e = e.next) {
            if (e.hash == h && (e.key == key || (key.equals(e.key)))) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }
        modCount++;
        addEntry(h, key, value, index);
        return null;
    }

    private V putForNull(V value) {
        for (Entry<K, V> e = table[0]; e != null; e = e.next) {
            if (e.key == null) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }
        modCount++;
        addEntry(0, null, value, 0);
        return null;
    }

    private void initTable(int toSize) {
        int capacity = roundUpToPowerOf2(toSize);
        threshold = ( int ) Math.min(capacity * loadFactor, MAXINUM_CAPACITY + 1);
        table = new Entry[capacity];
    }

    private int roundUpToPowerOf2(int toSize) {
        return toSize > MAXINUM_CAPACITY ? MAXINUM_CAPACITY : toSize > 1 ? Integer.highestOneBit((toSize - 1) << 1) : 1;
    }

    @Override
    public V remove(Object key) {
        Entry<K, V> e = removeEntry(key);
        return e == null ? null : e.value;
    }


    private abstract class HashIterator<E> implements Iterator<E> {

        private int expectedModcount;
        private Entry<K, V> next;
        private Entry<K, V> current;
        private int index;

        HashIterator() {
            expectedModcount = modCount;
            if (size > 0) {
                Entry[] t = table;
                while (index < table.length && ((next = t[index++]) == null))//find the first not null Node
                    ;
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        public Entry<K, V> nextEntry() {
            if (expectedModcount != modCount)
                throw new ConcurrentModificationException();
            if (next == null) {
                throw new NoSuchElementException();
            }
            Entry<K, V> e = next;
            if ((next = e.next) == null) {
                Entry[] t = table;
                while (index < table.length && ((next = t[index++]) == null))//find the first not null Node
                    ;
            }
            current = e;
            return e;
        }

        @Override
        public void remove() {
            if (current == null) {
                throw new IllegalArgumentException();
            }
            if (modCount != expectedModcount) {
                throw new ConcurrentModificationException();
            }
            K k = current.key;
            current = null;
            HashMap7.this.remove(k);
            expectedModcount = modCount;
        }
    }

    private final class KeyIterator extends HashIterator<K> {
        @Override
        public K next() {
            return nextEntry().key;
        }
    }

    private final class ValueIterator extends HashIterator<V> {
        @Override
        public V next() {
            return nextEntry().value;
        }
    }

    private final class EntryIterator extends HashIterator<Map.Entry<K, V>> {
        @Override
        public Entry<K, V> next() {
            return nextEntry();
        }
    }


    private final class KeySet extends AbstractSet<K> {

        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return size;
        }

        public boolean contains(Object o) {
            return containsKey(o);
        }

        public boolean remove(Object o) {
            return removeEntry(o) != null;
        }

        public void clear() {
            HashMap7.this.clear();
        }
    }

    private final class ValueSet extends AbstractSet<V> {

        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public int size() {
            return size;
        }

        public boolean contains(Object o) {
            return containsValue(o);
        }

        public void clear() {
            HashMap7.this.clear();
        }
    }

    private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public int size() {
            return size;
        }

        public void clear() {
            HashMap7.this.clear();
        }
    }


    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {
        modCount++;
        Arrays.fill(table, null);
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        if (this.keySet == null)
            this.keySet = new KeySet();
        return this.keySet;
    }

    @Override
    public Collection<V> values() {

        if (this.valueSet == null)
            this.valueSet = new ValueSet();
        return this.valueSet;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null)
            this.entrySet = new EntrySet();
        return this.entrySet;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {

    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {

    }

    @Override
    public V putIfAbsent(K key, V value) {
        return null;
    }

    @Override
    public boolean remove(Object key, Object value) {
        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    @Override
    public V replace(K key, V value) {
        return null;
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return null;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return null;
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return null;
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return null;
    }

    private void writeObject(ObjectOutputStream os) throws IOException {
        os.defaultWriteObject();
    }

    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
        is.defaultReadObject();
    }
}
