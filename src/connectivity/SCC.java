package connectivity;

import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SCC {
    private static long lastOperations;

    private SCC() { }

    public static List<List<Integer>> find(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "SCC requires a directed graph"
            );
        }

        int n = graph.vertexCount();
        boolean[] visited = new boolean[n];
        List<Integer> order = new ArrayList<>(n);
        lastOperations = 0;

        for (int vertex = 0; vertex < n; vertex++) {
            lastOperations++;

            if (!visited[vertex]) {
                dfsOrder(graph, vertex, visited, order);
            }
        }

        List<List<Integer>> reversed = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            reversed.add(new ArrayList<>());
        }

        for (Edge edge : graph.edges()) {
            reversed.get(edge.to).add(edge.from);
            lastOperations++;
        }

        Arrays.fill(visited, false);
        Collections.reverse(order);

        List<List<Integer>> components = new ArrayList<>();

        for (int vertex : order) {
            lastOperations++;

            if (!visited[vertex]) {
                List<Integer> component = new ArrayList<>();
                dfsReverse(vertex, reversed, visited, component);
                components.add(List.copyOf(component));
            }
        }

        return List.copyOf(components);
    }

    private static void dfsOrder(
            Graph graph,
            int vertex,
            boolean[] visited,
            List<Integer> order
    ) {
        visited[vertex] = true;

        for (Edge edge : graph.neighbors(vertex)) {
            lastOperations++;

            if (!visited[edge.to]) {
                dfsOrder(graph, edge.to, visited, order);
            }
        }

        order.add(vertex);
    }

    private static void dfsReverse(
            int vertex,
            List<List<Integer>> reversed,
            boolean[] visited,
            List<Integer> component
    ) {
        visited[vertex] = true;
        component.add(vertex);

        for (int neighbor : reversed.get(vertex)) {
            lastOperations++;

            if (!visited[neighbor]) {
                dfsReverse(neighbor, reversed, visited, component);
            }
        }
    }

    public static long getLastOperations() {
        return lastOperations;
    }
}