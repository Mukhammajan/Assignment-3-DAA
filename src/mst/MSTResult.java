package mst;

import graph.Edge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MSTResult {
    private final List<Edge> edges;
    private final long totalWeight;
    private final boolean connected;
    private final long operations;

    public MSTResult(
            List<Edge> edges,
            long totalWeight,
            boolean connected,
            long operations
    ) {
        this.edges = new ArrayList<>(edges);
        this.totalWeight = totalWeight;
        this.connected = connected;
        this.operations = operations;
    }

    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public long getTotalWeight() {
        return totalWeight;
    }

    public boolean isConnected() {
        return connected;
    }

    public long getOperations() {
        return operations;
    }
}