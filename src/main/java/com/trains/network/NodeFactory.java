package com.trains.network;

import com.trains.utils.GridPos;

/**
 * A recipe for building a {@link Node} at a given position.
 * @param <T> the type of node this factory builds
 */
@FunctionalInterface
public interface NodeFactory<T extends Node> {

    /**
     * Builds a node at the given position. Must honour the position it is
     handed.
     * @param pos the position to build at
     * @return the created node
     */
    T create(GridPos pos);

    /**
     * Returns a factory for a plain {@link Node}
     *  @return a factory for plain junction {@link Node}s
     */
    static NodeFactory<Node> node() {
        return Node::new;
    }

    /**
     * Returns a factory for a {@link Station} with the capacity supplied
     * @param capacity the capacity every station built by this factory gets
     * @return a factory for unnamed {@link Station}s
     */
    static NodeFactory<Station> station(int capacity) {
        return pos -> new Station(pos, capacity);
    }

    /**
     * Returns a factory for a {@link Station} with the capacity and name supplied
     * @param capacity the capacity every station built by this factory gets
     * @param name the name every station built by this factory gets
     * @return a factory for named {@link Station}s
     */
    static NodeFactory<Station> station(int capacity, String name) {
        return pos -> new Station(pos, capacity, name);
    }
}
