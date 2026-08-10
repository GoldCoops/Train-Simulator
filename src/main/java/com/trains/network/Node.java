package com.trains.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private final GridPos pos;
    private final ArrayList<PathwaySegment> connections = new ArrayList<>();


    public Node(GridPos pos) {
        this.pos = pos;
    }
    /**
     * Gets all Pathways connected to this Node
     * @return List of PathwaySegments
     */
    public List<PathwaySegment> getConnections() {
        return Collections.unmodifiableList(connections);
    }

    /**
     * Adds a connection
     * @param segment the PathwaySegment to be added
     */
    void addConnection(PathwaySegment segment) {
        connections.add(segment);
    }

    /**
     * Removes a connection
     * @param segment segment to be removed
     */
    void removeConnection(PathwaySegment segment) {
        connections.remove(segment);
    }
}
