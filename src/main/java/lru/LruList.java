package lru;

import com.google.common.base.Preconditions;

import java.util.*;
import java.util.function.UnaryOperator;

public class LruList<E> implements List<E> {
    private Node<E> head;

    private Node<E> tail;

    private volatile int size;

    private int cacheThreshold;

    @Override
    public String toString() {

        StringJoiner stringJoiner = new StringJoiner(",","[","]");

        for(Node node=head;node!=null;node=node.next){

            stringJoiner.add(String.valueOf(node.val));

        }

        return stringJoiner.toString();
    }

    private class Node<E> {
        private E val;

        private Node next;

        private Node Previous;

        public Node(E val, Node next, Node previous) {
            this.val = val;
            this.next = next;
            Previous = previous;
        }
    }


    private LruList(int cacheThreshold){
        this.cacheThreshold=cacheThreshold;
    }

    public static LruList getNewList(int cacheThreshold){
        Preconditions.checkArgument(cacheThreshold>0, String.format("cacheThreshold more than the 0  actual is %s",cacheThreshold));

        return new LruList(cacheThreshold);
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
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(E e) {
        if (size >= cacheThreshold) {
            remove(head);
        }
        if (head == null) {
            tail = head = new Node<>(e, null, null);
        } else {
            Node oldTail = tail;
            Node<E> node = new Node<>(e, null, oldTail);
            oldTail.next = node;
            tail = node;
        }
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Node node;

        if (!(o instanceof Node)) {
            return false;
        }

        node = ( Node ) o;

        E e = removeNode(node);

        return e != null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {

    }

    @Override
    public void sort(Comparator<? super E> c) {

    }

    @Override
    public void clear() {

    }

    @Override
    public E get(int index) {
        if(index>=size)
            return null;

        Node<E> result = head, pre, next;

        while (index > 0) {
            index--;

            result = result.next;
        }

        if (result != tail) {
            removeNode(result);

            add(result.val);
        }
        return result.val;
    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E element) {

    }

    @Override
    public E remove(int index) {
        Node node = head;

        while (index > 0) {
            index--;
            node = node.next;
        }
        return removeNode(node);
    }

    public E removeNode(Node node) {
        Node pre= node.Previous, next= node.next;
        node.Previous=null;
        node.next=null;
        if (pre != null) {

            pre.next = next;

        } else {

            head = next;

        }
        if (next  != null) {

            next.Previous = pre;

        } else {

            tail = pre;

        }
        size--;

        return ( E ) node.val;
    }


    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return null;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return null;
    }

    @Override
    public Spliterator<E> spliterator() {
        return null;
    }
}
