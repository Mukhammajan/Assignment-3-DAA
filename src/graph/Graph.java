package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Graph {
    private final int vertices;
    private final boolean directed;
    private final List<List<Edge>> adjacency;
    private final List<Edge> edges = new ArrayList<>();

    public Graph(int vertices, boolean directed) {
        if (vertices < 1) {
            throw new IllegalArgumentException(
                    "Graph needs at least one vertex"
            );
        }

        this.vertices = vertices;
        this.directed = directed;
        adjacency = new ArrayList<>(vertices);

        for (int i = 0; i < vertices; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, long weight) {
        checkVertex(from);
        checkVertex(to);

        if (weight < 0) {
            throw new IllegalArgumentException(
                    "Weights must be non-negative"
            );
        }

        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        adjacency.get(from).add(edge);

        if (!directed && from != to) {
            adjacency.get(to).add(new Edge(to, from, weight));
        }
    }

    private void checkVertex(int vertex) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IndexOutOfBoundsException(
                    "Invalid vertex: " + vertex
            );
        }
    }

    public int vertexCount() {
        return vertices;
    }

    public int edgeCount() {
        return edges.size();
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }

    public List<Edge> neighbors(int vertex) {
        checkVertex(vertex);
        return Collections.unmodifiableList(adjacency.get(vertex));
    }
}