package hash;

import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HashMap8<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable {
    private static final long serialVersionUID = 6598324644919853216L;
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    static final int MAXIMUM_CAPACITY = 1 << 30;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int TREEIFY_THRESHOLD = 8;
    static final int UNTREEIFY_THRESHOLD = 6;
    static final int MIN_TREEIFY_CAPACITY = 64;
    transient Node<K, V>[] table;
    transient Set<Map.Entry<K, V>> entrySet;
    transient int size;
    transient int modCount;
    int threshold;
    final float loadFactor;


    public HashMap8(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                    initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                    loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    public HashMap8(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashMap8() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }


    static class Node<K, V> implements Entry<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public Node<K, V> getNext() {
            return next;
        }

        public void setNext(Node<K, V> next) {
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
        public V setValue(V value) {
            V oldValue = value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?, ?> node = ( Node<?, ?> ) o;
            return hash == node.hash &&
                    Objects.equals(key, node.key) &&
                    Objects.equals(value, node.value) &&
                    Objects.equals(next, node.next);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hash, key, value, next);
        }
    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16  );
    }

    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }


    final Node<K, V>[] resize() {
        Node<K, V>[] oldTab = table;
        int oldCap = table == null ? 0 : table.length;
        int oldTr = threshold;
        int newCap, newTr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = MAXIMUM_CAPACITY;
                return table;
            } else if ((newCap = oldCap << 2) < MAXIMUM_CAPACITY && oldCap > DEFAULT_INITIAL_CAPACITY)
                newTr = oldTr << 2;
        } else if (oldTr > 0) {
            newCap = oldTr;
        } else {
            newCap = DEFAULT_INITIAL_CAPACITY;
            newTr = ( int ) (newCap * DEFAULT_LOAD_FACTOR);
        }
        if (newTr == 0) {
            float ft = ( float ) newCap * loadFactor;
            newTr = (newCap < MAXIMUM_CAPACITY && ft < ( float ) MAXIMUM_CAPACITY ?
                    ( int ) ft : Integer.MAX_VALUE);
        }
        threshold = newTr;
        table = new Node[newCap];
        if (oldCap > 0) {
            for (int j = 0; j < oldTab.length; ++j) {
                Node<K, V> n = oldTab[j];
                if (n != null) {
                    oldTab[j] = null;
                    if (n.next == null)
                        table[n.hash & (newCap - 1)] = n;
                    else if (n instanceof TreeNode)
                        (( TreeNode<K, V> ) n).split(this, table, j, oldCap);
                    else {
                        Node<K, V> loTail = null, loHead = null;
                        Node<K, V> hiTail = null, hiHead = null;
                        Node<K, V> next;
                        do {
                            next = n.next;
                            if ((n.hash & oldCap) == 0) {
                                if (loHead == null)
                                    loHead = n;
                                else
                                    loTail.next = n;
                                loTail = n;
                            } else {
                                if (hiHead == null)
                                    hiHead = n;
                                else
                                    hiTail.next = n;
                                hiTail = n;
                            }
                        } while ((n = next) != null);
                        if (loHead != null) {
                            loTail.next = null;
                            table[j] = loHead;
                        }
                        if (hiHead != null) {
                            hiTail.next = null;
                            table[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return table;
    }


    final V putNode(int hash, K k, V v, boolean onlyIfAbsent,
                    boolean evict) {
        int n, i;
        Node<K, V> p, e;
        if (table == null || (n = table.length) == 0)
            n = resize().length;
        if ((e = table[i = (hash & (n - 1))]) == null) {
            table[i] = new Node<>(hash, k, v, null);
        } else {
            if (e.hash == hash && (e.key == k || (e.key != null && e.key.equals(k)))) {
                p = e;
            } else if (e instanceof TreeNode)
                p = (( TreeNode<K, V> ) e).putTreeVal(this, table, hash, k, v);
            else {
                for (int count = 0; ; ++count) {
                    if ((p = e.next) == null) {
                        e.next = new Node<>(hash, k, v, null);
                        if (count >= TREEIFY_THRESHOLD - 1)
                            treeifyBin(table, hash);
                        break;
                    } else if (e.hash == hash && (e.key == k || (e.key != null && e.key.equals(k)))) {
                        p = e;
                        break;
                    }
                    e = p;
                }

            }
            if (p != null) {
                V oldValue = p.value;
                if (!onlyIfAbsent || oldValue == null)
                    p.value = v;
                afterNodeAccess(p);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }


    private void treeifyBin(Node<K, V>[] table, int hash) {
        int l = 0;
        Node<K, V> e;
        TreeNode<K, V> loTail = null, loHead = null;

        if (table == null || (l = table.length) < MIN_TREEIFY_CAPACITY)
            resize();
        if ((e = table[hash & (l - 1)]) != null) {
            do {
                TreeNode<K, V> p = parseNodeToTreeNode(e);
                if (loTail == null)
                    loHead = p;
                else {
                    loTail.next = p;
                    p.prev = loTail;
                }
                loTail = p;
            } while ((e = e.next) != null);
            if ((table[hash & (l - 1)] = loHead) != null) {
                loHead.treeify(table);
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
    public Set<Entry<K, V>> entrySet() {
        return null;
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
    public V put(K key, V value) {
        return putNode(hash(key), key, value, false, false);
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

    void afterNodeAccess(Node<K, V> p) {
    }

    void afterNodeInsertion(boolean evict) {
    }

    TreeNode<K, V> parseNodeToTreeNode(Node<K, V> node) {
        return new TreeNode<>(node.hash, node.key, node.value, null);
    }

    static int tieBreakOrder(Object a, Object b) {
        int d;
        if (a == null || b == null || (d = a.getClass().getName().compareTo(b.getClass().getName())) == 0)
            d = System.identityHashCode(a) > System.identityHashCode(b) ? 1 : -1;
        return d;
    }

    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c;
            Type[] ts, as;
            Type t;
            ParameterizedType p;
            if ((c = x.getClass()) == String.class) // bypass checks
                return c;
            if ((ts = c.getGenericInterfaces()) != null) {
                for (int i = 0; i < ts.length; ++i) {
                    if (((t = ts[i]) instanceof ParameterizedType) &&
                            ((p = ( ParameterizedType ) t).getRawType() ==
                                    Comparable.class) &&
                            (as = p.getActualTypeArguments()) != null &&
                            as.length == 1 && as[0] == c) // type arg is c
                        return c;
                }
            }
        }
        return null;
    }

    static int compareComparables(Class<?> kc, Object k, Object x) {

        return k == null || x.getClass() != kc ? 0 : (( Comparable ) k).compareTo(x);

    }


    static final class TreeNode<K, V> extends HashMap8.Node<K, V> {
        TreeNode<K, V> parent;  // red-black tree links
        TreeNode<K, V> left;
        TreeNode<K, V> right;
        TreeNode<K, V> prev;    // needed to unlink next upon deletion
        boolean red;

        TreeNode(int hash, K key, V value, Node<K, V> next) {
            super(hash, key, value, next);
        }

        final TreeNode<K, V> root() {
            for (TreeNode<K, V> r = this, p; ; ) {
                if ((p = r.parent) == null)
                    return r;
                r = p;
            }
        }


        public Node<K, V> putTreeVal(HashMap8<K, V> kvHashMap8, Node<K, V>[] table, int hash, K k, V v) {
            Class kc = null;
            TreeNode<K, V> t;
            int idr;
            TreeNode<K, V> root = (parent != null) ? root() : this;
            for (TreeNode<K, V> n = root; ; ) {
                if (hash < n.hash) {
                    idr = -1;
                } else if (hash > n.hash) {
                    idr = 1;
                } else if (k == n.key || (k != null && k.equals(n.key)))
                    return n;
                else if ((kc == null && (kc = comparableClassFor(n)) == null)
                        || (idr = (compareComparables(kc, k, n.key))) == 0)
                    idr = tieBreakOrder(k, n.key);
                TreeNode<K, V> r = n;
                if ((n = idr > 0 ? n.right : n.left) == null) {
                    Node<K, V> nn = r.next;
                    t = new TreeNode<>(hash, k, v, nn);
                    if (idr > 0)
                        r.right = t;
                    else
                        r.left = t;
                    t.parent = t.prev = r;
                    r.next = t;
                    if (nn != null)
                        (( TreeNode<K, V> ) nn).prev = t;
                    moveRootToFront(table, balanceInsertion(root, t));
                    return null;
                }
            }
        }


        public void split(HashMap8<K, V> map, Node<K, V>[] tab, int index, int oldCap) {
            TreeNode<K, V> b = this;
            // Relink into lo and hi lists, preserving order
            TreeNode<K, V> loHead = null, loTail = null;
            TreeNode<K, V> hiHead = null, hiTail = null;
            int lc = 0, hc = 0;
            for (TreeNode<K, V> e = b, next; e != null; e = next) {
                next = ( TreeNode<K, V> ) e.next;
                e.next = null;
                if ((e.hash & oldCap) == 0) {
                    if ((e.prev = loTail) == null)
                        loHead = e;
                    else
                        loTail.next = e;
                    loTail = e;
                    ++lc;
                } else {
                    if ((e.prev = hiTail) == null)
                        hiHead = e;
                    else
                        hiTail.next = e;
                    hiTail = e;
                    ++hc;
                }
            }

            if (loHead != null) {
                if (lc <= UNTREEIFY_THRESHOLD)
                    tab[index] = loHead.untreeify(map);
                else {
                    tab[index] = loHead;
                    if (hiHead != null) // (else is already treeified)
                        loHead.treeify(tab);
                }
            }
            if (hiHead != null) {
                if (hc <= UNTREEIFY_THRESHOLD)
                    tab[index + oldCap] = hiHead.untreeify(map);
                else {
                    tab[index + oldCap] = hiHead;
                    if (loHead != null)
                        hiHead.treeify(tab);
                }
            }

        }

        final Node<K, V> untreeify(HashMap8<K, V> map) {
            Node<K, V> hd = null, tl = null;
            for (Node<K, V> q = this; q != null; q = q.next) {
                Node<K, V> p = map.replacementNode(q, null);
                if (tl == null)
                    hd = p;
                else
                    tl.next = p;
                tl = p;
            }
            return hd;
        }

        public void treeify(Node<K, V>[] table) {
            TreeNode<K, V> root = null;
            for (TreeNode<K, V> t = this, next; t != null; t = next) {
                next = ( TreeNode<K, V> ) t.next;
                t.left = t.right = null;
                if (root == null) {
                    t.parent = null;
                    t.red = false;
                    root = t;
                } else {
                    K k = t.key;
                    int h = t.hash, idr;
                    Class<?> kc = null;
                    for (TreeNode<K, V> p = root; ; ) {
                        if (h < p.hash)
                            idr = -1;
                        else if (h > p.hash)
                            idr = 1;
                        else if ((kc == null && (kc = comparableClassFor(p)) == null) ||
                                (idr = compareComparables(kc, k, p.key)) == 0)
                            idr = tieBreakOrder(k, p.key);
                        TreeNode<K, V> xp = p;
                        if ((p = (idr < 0) ? p.left : p.right) == null) {
                            t.parent = xp;
                            if (idr < 0)
                                xp.left = t;
                            else
                                xp.right = t;
                            root = balanceInsertion(root, t);
                            break;
                        }
                    }
                }
            }
            moveRootToFront(table, root);
        }

        private static <K, V> void moveRootToFront(Node<K, V>[] table, TreeNode<K, V> root) {
            int n, index;
            TreeNode<K, V> fp, fn;
            if (table != null && root != null & (n = table.length) > 0) {
                int h = root.hash;
                TreeNode<K, V> first = ( TreeNode<K, V> ) table[(index = h & (n - 1))];
                if (first != root) {
                    table[index] = root;
                    fn = ( TreeNode<K, V> ) root.next;
                    if ((fp = root.prev) != null)
                        fp.next = fn;
                    if (fn != null)
                        fn.prev = fp;
                    if (first != null)
                        first.prev = root;
                    root.next = first;
                    root.prev = null;
                }
                assert checkInvariants(root);
            }

        }

        //检查红黑树的正确性
        private static <K, V> boolean checkInvariants(TreeNode<K, V> t) {
            TreeNode<K, V> tp = t.parent, tl = t.left, tr = t.right,
                    tb = t.prev, tn = ( TreeNode<K, V> ) t.next;
            if (tb != null && tb.next != t)
                return false;
            if (tn != null && tn.prev != t)
                return false;
            if (tp != null && t != tp.left && t != tp.right)
                return false;
            if (tl != null && (tl.parent != t || tl.hash > t.hash))
                return false;
            if (tr != null && (tr.parent != t || tr.hash < t.hash))
                return false;
            if (t.red && tl != null && tl.red && tr != null && tr.red)
                return false;
            if (tl != null && !checkInvariants(tl))
                return false;
            if (tr != null && !checkInvariants(tr))
                return false;
            return true;
        }

        /*插入后自旋已达到平衡分为以下几种情况
          1.插入后父节点为空       当前节点为root 将其变黑色
          2.插入后父节点为黑色或者父节点为NULL  无需变色
          3.父节点为红色,且叔父节点为红色    将父节点和叔父节点变黑色,祖父节点变红色
          4.当前节点为父节点的左子节点且其为红色,且叔父节点为黑色  右旋
          5.当前节点为父节点的右子节点且其为红色,且叔父节点为黑色  左旋
         */
        private static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> root, TreeNode<K, V> e) {
            e.red = true;
            for (TreeNode<K, V> ep, epp, epl, epr; ; ) {
                if ((ep = e.parent) == null) {
                    e.red = false;
                    return e;
                } else if (!ep.red || (epp = ep.parent) == null)
                    return root;
                if (ep == (epl = epp.left)) {//父节点为祖父节点的左节点
                    if ((epr = epp.right) != null && epr.red) {
                        ep.red = false;
                        epr.red = false;
                        epp.red = true;
                        e = epp;
                    } else {
                        if (e == ep.right) {
                            root = rotateLeft(root, e = ep);
                            epp = (ep = e.parent) == null ? null : ep.parent;
                        }
                        if (ep != null) {
                            ep.red = false;
                            if (epp != null) {
                                epp.red = true;
                                root = rotateRight(root, epp);
                            }
                        }
                    }
                } else {//父节点为祖父节点的右节点或者
                    if (epl != null && epl.red) {
                        ep.red = false;
                        epl.red = false;
                        epp.red = true;
                        e = epp;
                    } else {
                        if (e == ep.left) {
                            root = rotateRight(root, e = ep);
                            epp = (ep = e.parent) == null ? null : ep.parent;
                        }
                        if (ep != null) {
                            ep.red = false;
                            if (epp != null) {
                                epp.red = true;
                                root = rotateLeft(root, epp);
                            }
                        }
                    }
                }
            }
        }

        /*   右旋
                    X                      Y
                  /  \                   /  \
                Y     Z     ===>       F    X
              /  \                        /  \
            F    W                      W     Z
        */
        private static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> root, TreeNode<K, V> p) {
            TreeNode<K, V> pl, pp, pr, rl;
            if (p != null && (pl = p.left) != null) {
                if ((rl = p.left = pl.right) != null)
                    rl.parent = p;
                if ((pp = pl.parent = p.parent) != null) {
                    if (pp.left == p)
                        pp.left = pl;
                    else
                        pp.right = pl;

                } else {
                    (root = pl).red = false;
                }
                p.parent = pl;
                pl.right = p;
            }
            return root;
        }

        /*   左旋
                X                      Z
              /  \                   /  \
            Y     Z     ===>       X     F
                /  \             /  \
               W   F            Y    W
         */
        private static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> root, TreeNode<K, V> p) {
            TreeNode<K, V> pr, pp, pl, lr;
            if (p != null && (pr = p.right) != null) {
                if ((lr = p.right = pr.left) != null)
                    lr.parent = p;
                if ((pp = pr.parent = p.parent) != null) {
                    if (p == pp.right)
                        pp.right = pr;
                    else
                        pp.left = pr;
                } else
                    (root = pr).red = false;
                p.parent = pr;
                pr.left = p;
            }
            return root;
        }
    }

    private Node<K,V> replacementNode(Node<K, V> p, Node<K, V> next) {
         return new Node<K,V>(p.hash, p.key, p.value, next);
    }

}
