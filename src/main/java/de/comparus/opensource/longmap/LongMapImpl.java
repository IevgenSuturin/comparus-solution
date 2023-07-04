package de.comparus.opensource.longmap;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LongMapImpl<V> implements LongMap<V> {

    private final static String NOT_FOUND_EXCEPTION_MESSAGE = "Element not found";

    private int capacity;
    private final double loadFactor;
    private int numberNotEmptyBuckets = 0;

    private List<Node<V>>[] table;

    public LongMapImpl(int capacity, double loadFactor) {
        this.capacity = capacity;
        this.loadFactor = loadFactor;
        this.table = new LinkedList[capacity];
    }
    public V put(long key, V value) {
        if (this.numberNotEmptyBuckets >= this.capacity * loadFactor) {
            resize();
        }

        int bucketIndex = getBucketIndexByKey(key);
        if (table[bucketIndex] == null) {
            this.numberNotEmptyBuckets++;
            table[bucketIndex] = new LinkedList<>();
            table[bucketIndex].add(new Node<>(key, value));
        } else {
            locateNode(table[bucketIndex], node -> node.key.equals(key))
                    .ifPresentOrElse(
                            node -> node.value = value,
                            () ->table[bucketIndex].add(new Node<>(key, value))
                    );
        }
        return value;
    }

    @Override
    public V get(long key) {
        int bucketIndex = getBucketIndexByKey(key);

        if (Objects.isNull(table[bucketIndex])) {
            throw new IllegalArgumentException(NOT_FOUND_EXCEPTION_MESSAGE);
        }

        Optional<Node<V>> locatedNode = locateNode(table[bucketIndex], node -> node.key.equals(key));

        if (locatedNode.isPresent()) {
            return locatedNode.get().getValue();
        }

        throw new IllegalArgumentException(NOT_FOUND_EXCEPTION_MESSAGE);
    }

    @Override
    public V remove(long key) {
        int bucketIndex = getBucketIndexByKey(key);

        Optional<Node<V>> locateNode = locateNode(table[bucketIndex], node -> node.key.equals(key));
        if (locateNode.isPresent()) {
            table[bucketIndex].remove(locateNode.get());
            return locateNode.get().getValue();
        }

        throw new IllegalArgumentException(NOT_FOUND_EXCEPTION_MESSAGE);
    }

    public boolean isEmpty() {
        return Arrays.stream(table).noneMatch(list -> Objects.nonNull(list) && !list.isEmpty());
    }

    @Override
    public boolean containsKey(long key) {
        int bucketIndex = getBucketIndexByKey(key);
        if (Objects.isNull(table[bucketIndex])) {
            return false;
        }

        return locateNode(table[bucketIndex], node -> node.key.equals(key)).isPresent();
    }

    @Override
    public boolean containsValue(V value) {
        return Arrays.stream(table)
                .filter(Objects::nonNull)
                .anyMatch(
                list -> locateNode(list, node -> node.value.equals(value)).isPresent()
        );
    }

    @Override
    public long[] keys() {
        return Arrays.stream(table)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(Node::getKey)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .toArray();
    }

    @Override
    public List<V> values() {
        return Arrays.stream(table)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .map(Node::getValue)
                .distinct()
                .collect(Collectors.toList());
    }

    public long size() {
        return Arrays.stream(table)
                .filter(Objects::nonNull)
                .mapToLong(List::size)
                .sum();
    }

    public void clear() {
        Arrays.stream(table).filter(Objects::nonNull).forEach(List::clear);
    }

    private Optional<Node<V>> locateNode(List<Node<V>> list,
                                         Predicate<Node<V>> predicate) {
        return list.stream().filter(predicate).findFirst();
    }

    private int getBucketIndexByKey(long key) {
        return (int)((capacity - 1) & (key ^ key>>>16));
    }

    private void resize() {
        List<Node<V>>[] oldTable = this.table;
        this.capacity = capacity * 2;
        this.table = new LinkedList[capacity];

        Arrays.stream(oldTable)
                .flatMap(List::stream)
                .forEach(node -> put(node.getKey(), node.getValue()));
    }

    private static class Node<V> {
        V value;
        Long key;

        public Node(Long key, V value) {
            this.value = value;
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public Long getKey() {
            return key;
        }
    }
}
