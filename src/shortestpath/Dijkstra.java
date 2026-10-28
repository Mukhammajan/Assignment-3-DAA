package shortestpath;

import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public final class Dijkstra {
    public static final class Result {
        private final long[] distances;
        private final int[] previous;
        private final long operations;

        private Result(
                long[] distances,
                int[] previous,
                long operations
        ) {
            this.distances = distances.clone();
            this.previous = previous.clone();
            this.operations = operations;
        }

        public long distanceTo(int vertex) {
            return distances[vertex];
        }

        public long[] getDistances() {
            return distances.clone();
        }

        public long getOperations() {
            return operations;
        }

        public List<Integer> pathTo(int vertex) {
            if (distances[vertex] == Long.MAX_VALUE) {
                return List.of();
            }

            List<Integer> path = new ArrayList<>();

            for (int v = vertex; v != -1; v = previous[v]) {
                path.add(v);
            }

            Collections.reverse(path);
            return List.copyOf(path);
        }
    }

    private record Node(int vertex, long distance) { }

    private Dijkstra() { }

    public static Result shortestPaths(Graph graph, int source) {
        int n = graph.vertexCount();

        if (source < 0 || source >= n) {
            throw new IndexOutOfBoundsException(
                    "Invalid source vertex"
            );
        }

        long[] distance = new long[n];
        int[] previous = new int[n];

        Arrays.fill(distance, Long.MAX_VALUE);
        Arrays.fill(previous, -1);

        PriorityQueue<Node> queue =
                new PriorityQueue<>(
                        Comparator.comparingLong(Node::distance)
                );

        distance[source] = 0;
        queue.offer(new Node(source, 0));

        long operations = 0;

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            operations++;

            if (current.distance() != distance[current.vertex()]) {
                continue;
            }

            for (Edge edge : graph.neighbors(current.vertex())) {
                operations++;

                if (current.distance() <= Long.MAX_VALUE - edge.weight) {
                    long candidate = current.distance() + edge.weight;

                    if (candidate < distance[edge.to]) {
                        distance[edge.to] = candidate;
                        previous[edge.to] = current.vertex();
                        queue.offer(new Node(edge.to, candidate));
                    }
                }
            }
        }

        return new Result(distance, previous, operations);
    }
}