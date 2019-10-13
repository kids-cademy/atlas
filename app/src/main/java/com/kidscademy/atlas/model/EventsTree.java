package com.kidscademy.atlas.model;

/**
 * A tree with nodes that could implement an interface registered to tree root. Implementation has a
 * specific set of interfaces that its node could register. It is considered a bug if a node attempts
 * to register an interface not recognized by root.
 * <p>
 * Tree root has the possibility send events to registered nodes, that is, to invoke methods from interface.
 * Having registered node instance reference, root is able to optimally invoked interface avoid the need
 * to traverse the tree in order to locate nodes.
 *
 * @author Iulian Rotaru
 */
public interface EventsTree {
    <I> void registerListener(Class<I> type, I listener);

    <I> Iterable<I> getListeners(Class<I> type);
}
