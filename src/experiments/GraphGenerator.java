package experiments;

import graph.Graph;
import java.util.Random;

public final class GraphGenerator {
    private GraphGenerator() { }

    public static Graph connectedUndirected(
            int n,
            int targetEdges,
            long seed
    ) {
        long maxEdges = (long) n * (n - 1) / 2;

        if (n < 1 || targetEdges < n - 1 || targetEdges > maxEdges) {
            throw new IllegalArgumentException(
                    "Invalid connected graph size"
            );
        }

        Graph graph = new Graph(n, false);
        boolean[][] used = new boolean[n][n];
        Random random = new Random(seed);

        for (int vertex = 1; vertex < n; vertex++) {
            int parent = random.nextInt(vertex);
            add(graph, used, parent, vertex, 1 + random.nextInt(1000));
        }

        while (graph.edgeCount() < targetEdges) {
            int from = random.nextInt(n);
            int to = random.nextInt(n);

            if (from != to && !used[from][to]) {
                add(graph, used, from, to, 1 + random.nextInt(1000));
            }
        }

        return graph;
    }

    public static Graph directedDag(int n, int edgeCount, long seed) {
        long maxEdges = (long) n * (n - 1) / 2;

        if (n < 1 || edgeCount < 0 || edgeCount > maxEdges) {
            throw new IllegalArgumentException("Invalid DAG size");
        }

        Graph graph = new Graph(n, true);
        boolean[][] used = new boolean[n][n];
        Random random = new Random(seed);

        while (graph.edgeCount() < edgeCount) {
            int from = random.nextInt(n);
            int to = random.nextInt(n);

            if (from < to && !used[from][to]) {
                add(graph, used, from, to, 1 + random.nextInt(1000));
            }
        }

        return graph;
    }

    public static Graph directedCyclic(
            int n,
            int edgeCount,
            long seed
    ) {
        long maxEdges = (long) n * (n - 1) / 2 + 1;

        if (n < 2 || edgeCount < n || edgeCount > maxEdges) {
            throw new IllegalArgumentException(
                    "Invalid cyclic graph size"
            );
        }

        Graph graph = directedDag(n, edgeCount - 1, seed);
        graph.addEdge(n - 1, 0, 1);
        return graph;
    }

    private static void add(
            Graph graph,
            boolean[][] used,
            int from,
            int to,
            long weight
    ) {
        graph.addEdge(from, to, weight);
        used[from][to] = true;

        if (!graph.isDirected()) {
            used[to][from] = true;
        }
    }
}