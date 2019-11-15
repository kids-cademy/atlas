package com.kidscademy.atlas.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import js.lang.BugError;

/**
 * This immutable tree map uses a single char as key and is optimized for value search by a sequence of chars.
 * A value is identified by a sequence of chars that should be unique.
 * <p>
 * A sequence of chars can be used to locate a value. Provides constant
 * complexity regarding dictionary getObjectsCount, O(m * log n) where m is char sequence length and n is alphabet getObjectsCount.
 *
 * @param <V> the type of mapped values.
 * @author Iulian Rotaru
 */
public class CharTree<V extends Keyword> {
    private Node root;

    public CharTree() {
        root = new Node();
    }

    public CharTree(List<V> values) {
        this();
        for (V value : values) {
            if (value.getKeyword().isEmpty()) {
                throw new BugError("Empty value keyword.");
            }
            loadIndex(root, value, 0);
        }
    }

    private void loadIndex(Node node, V value, int keywordIndex) {
        if (keywordIndex >= value.getKeyword().length()) {
            throw new BugError("Value keyword overflow.");
        }
        // current key is the character from value keyword in current iteration
        final char key = value.getKeyword().charAt(keywordIndex);

        // value is always stored on a child node of the current iteration node
        // but only if one of the next condition is met:
        // 1. child node is not yet created for current key
        // 2. value keyword is fully traversed, that is, current key is the last char from keyword

        // get child node for current key; if there is no such node create it as value node
        Node child = node.getChild(key);
        if (child == null) {
            node.putChildValue(key, value);
            return;
        }

        // at this point child node exists

        if (child.hasValue()) {
            V childValue = child.getValue();
            if (keywordIndex < childValue.getKeyword().length() - 1) {
                child.moveValueToChild(keywordIndex + 1);
            }
        }

        // check if value keyword is completely traversed, that is, this iteration is on last keyword char
        // if so, store value on current node but takes care to move inner value, if one exists
        if (value.getKeyword().length() - 1 == keywordIndex) {
            child.putValue(value);
            return;
        }

        loadIndex(child, value, keywordIndex + 1);
    }

    public List<V> find(String prefix) {
        return find(prefix, 10);
    }

    public List<V> find(String prefix, int count) {
        List<V> indices = new ArrayList<>();
        if (prefix.isEmpty()) {
            return indices;
        }
        prefix = prefix.toLowerCase();

        Node node = root;
        for (int i = 0; i < prefix.length(); ++i) {
            Node child = node.getChild(prefix.charAt(i));

            if (child == null) {
                if (node.hasValue(prefix)) {
                    indices.add(node.getValue());
                    return indices;
                }
                return Collections.emptyList();
            }

            if (child.hasValue(prefix)) {
                indices.add(child.getValue());
                return indices;
            }

            node = child;
        }

        // loadIndex value nodes from subtree of which located node is the root

        loadIndices(node, indices, count);
        return indices;
    }

    /**
     * Load index values from subtree.
     *
     * @param node    subtree root node,
     * @param indices indices repository,
     * @param count   maximum indices count.
     * @return true if recursive iteration should continue, false to interrupt recursive iteration
     */
    private boolean loadIndices(Node node, List<V> indices, int count) {
        // TODO: add guard for infinite loop

        if (!node.hasChildren()) {
            return true;
        }

        // because map implementation is tree map values - child nodes, are iterated in ascending order
        for (Node child : node.getChildren()) {
            if (child.hasValue()) {
                indices.add(child.getValue());
                if (indices.size() == count) {
                    return false;
                }
            }
            if (!loadIndices(child, indices, count)) {
                return false;
            }
        }
        return true;
    }

    public void dump() {
        System.out.println("root");
        dump('.', root, 0);
    }

    private void dump(char key, Node node, int indent) {
        for (int i = 0; i < indent; ++i) {
            System.out.print(" ");
        }
        System.out.print(key);
        if (node.value != null) {
            System.out.print(": ");
            System.out.print(node.value.getKeyword());
        }
        System.out.println();

        if (node.children == null) {
            return;
        }
        for (TreeMap.Entry<Character, Node> child : node.children.entrySet()) {
            dump(child.getKey(), child.getValue(), indent + 1);
        }
    }

    private class Node {
        /**
         * Optional value, default to null.
         */
        private V value;

        /**
         * Optional children mapped by character key, default to null.
         */
        private TreeMap<Character, Node> children;

        Node() {
            children = new TreeMap<>();
        }

        Node(V value) {
            this.value = value;
        }

        boolean hasChildren() {
            return children != null;
        }

        Collection<Node> getChildren() {
            assert children != null;
            return children.values();
        }

        Node getChild(char key) {
            return children != null ? children.get(key) : null;
        }

        void putValue(V value) {
            this.value = value;
        }

        boolean hasValue() {
            return value != null;
        }

        boolean hasValue(String prefix) {
            return value != null && value.getKeyword().startsWith(prefix);
        }

        V getValue() {
            assert value != null;
            return value;
        }

        void putChildValue(char key, V value) {
            if (children == null) {
                children = new TreeMap<>();
            }
            children.put(key, new Node(value));
        }

        void moveValueToChild(int keyIndex) {
            assert value != null;
            if (value.getKeyword().length() <= keyIndex) {
                throw new BugError("Keyword overflow.");
            }
            char key = value.getKeyword().charAt(keyIndex);
            assert children == null;
            children = new TreeMap<>();
            putChildValue(key, value);
            this.value = null;
        }
    }
}
