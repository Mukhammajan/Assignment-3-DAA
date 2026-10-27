package connectivity;

import graph.Edge;
import graph.Graph;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public final class TopologicalSort {
    private static long lastOperations;

    private TopologicalSort() { }

    public static List<Integer> sort(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Topological sort requires a directed graph"
            );
        }

        int n = graph.vertexCount();
        int[] indegree = new int[n];
        lastOperations = 0;

        for (Edge edge : graph.edges()) {
            indegree[edge.to]++;
            lastOperations++;
        }

        Queue<Integer> queue = new ArrayDeque<>();

        for (int vertex = 0; vertex < n; vertex++) {
            lastOperations++;

            if (indegree[vertex] == 0) {
                queue.offer(vertex);
            }
        }

        List<Integer> order = new ArrayList<>(n);

        while (!queue.isEmpty()) {
            int vertex = queue.remove();
            order.add(vertex);

            for (Edge edge : graph.neighbors(vertex)) {
                lastOperations++;
                indegree[edge.to]--;

                if (indegree[edge.to] == 0) {
                    queue.offer(edge.to);
                }
            }
        }

        if (order.size() != n) {
            throw new IllegalStateException(
                    "Topological order does not exist: graph contains a cycle"
            );
        }

        return List.copyOf(order);
    }

    public static boolean hasCycle(Graph graph) {
        try {
            sort(graph);
            return false;
        } catch (IllegalStateException cycle) {
            return true;
        }
    }

    public static long getLastOperations() {
        return lastOperations;
    }
}