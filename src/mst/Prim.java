package mst;

import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public final class Prim {
    private record Candidate(int from, int to, long weight) { }

    public static MSTResult findMST(Graph graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Prim requires an undirected graph"
            );
        }

        int n = graph.vertexCount();
        boolean[] visited = new boolean[n];
        List<Edge> selected = new ArrayList<>();

        PriorityQueue<Candidate> queue =
                new PriorityQueue<>(
                        Comparator.comparingLong(Candidate::weight)
                );

        long totalWeight = 0;
        long examinedEdges = 0;
        long heapRemovals = 0;

        visited[0] = true;
        examinedEdges += addFrontier(graph, 0, visited, queue);

        while (!queue.isEmpty() && selected.size() < n - 1) {
            Candidate candidate = queue.poll();
            heapRemovals++;

            if (visited[candidate.to()]) {
                continue;
            }

            visited[candidate.to()] = true;
            selected.add(new Edge(
                    candidate.from(),
                    candidate.to(),
                    candidate.weight()
            ));
            totalWeight += candidate.weight();

            examinedEdges += addFrontier(
                    graph,
                    candidate.to(),
                    visited,
                    queue
            );
        }

        boolean connected = selected.size() == n - 1;

        return new MSTResult(
                selected,
                totalWeight,
                connected,
                examinedEdges + heapRemovals
        );
    }

    private static long addFrontier(
            Graph graph,
            int vertex,
            boolean[] visited,
            PriorityQueue<Candidate> queue
    ) {
        long examined = 0;

        for (Edge edge : graph.neighbors(vertex)) {
            examined++;

            if (!visited[edge.to]) {
                queue.add(new Candidate(
                        vertex,
                        edge.to,
                        edge.weight
                ));
            }
        }

        return examined;
    }
}