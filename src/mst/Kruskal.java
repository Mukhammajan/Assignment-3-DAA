package mst;

import dsu.DSU;
import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class Kruskal {
    public static MSTResult findMST(Graph graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kruskal requires an undirected graph"
            );
        }

        AtomicLong comparisons = new AtomicLong();
        List<Edge> sorted = new ArrayList<>(graph.edges());

        sorted.sort((a, b) -> {
            comparisons.incrementAndGet();
            return Long.compare(a.weight, b.weight);
        });

        DSU dsu = new DSU(graph.vertexCount());
        List<Edge> selected = new ArrayList<>();

        long totalWeight = 0;
        long examinedEdges = 0;

        for (Edge edge : sorted) {
            examinedEdges++;

            if (dsu.union(edge.from, edge.to)) {
                selected.add(edge);
                totalWeight += edge.weight;

                if (selected.size() == graph.vertexCount() - 1) {
                    break;
                }
            }
        }

        long operations = comparisons.get()
                + examinedEdges
                + dsu.findCalls()
                + dsu.unionCalls();

        return new MSTResult(
                selected,
                totalWeight,
                dsu.components() == 1,
                operations
        );
    }
}