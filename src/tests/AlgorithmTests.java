package tests;

import connectivity.SCC;
import connectivity.TopologicalSort;
import experiments.GraphGenerator;
import graph.Edge;
import graph.Graph;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;
import shortestpath.Dijkstra;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AlgorithmTests {
    public static void main(String[] args) {
        testMstAndDuplicateWeights();
        testDisconnectedAndSingleVertex();
        testTopologicalAndCycle();
        testSccPartition();
        testDijkstraAndUnreachable();
        testGenerators();

        System.out.println("All correctness checks passed.");
    }

    private static void testMstAndDuplicateWeights() {
        Graph graph = new Graph(4, false);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 3);
        graph.addEdge(0, 2, 2);
        graph.addEdge(0, 3, 6);

        MSTResult kruskal = Kruskal.findMST(graph);
        MSTResult prim = Prim.findMST(graph);

        check(
                kruskal.isConnected() && prim.isConnected(),
                "MST graph is connected"
        );

        check(
                kruskal.getTotalWeight() == prim.getTotalWeight()
                        && kruskal.getTotalWeight() == 6,
                "MST weights match, including duplicate weights"
        );
    }

    private static void testDisconnectedAndSingleVertex() {
        Graph disconnected = new Graph(3, false);
        disconnected.addEdge(0, 1, 5);

        check(
                !Kruskal.findMST(disconnected).isConnected(),
                "Kruskal detects a disconnected graph"
        );

        check(
                !Prim.findMST(disconnected).isConnected(),
                "Prim detects a disconnected graph"
        );

        Graph oneVertex = new Graph(1, false);

        check(
                Kruskal.findMST(oneVertex).isConnected()
                        && Prim.findMST(oneVertex).isConnected(),
                "single-vertex graph"
        );
    }

    private static void testTopologicalAndCycle() {
        Graph dag = new Graph(4, true);
        dag.addEdge(0, 1, 1);
        dag.addEdge(0, 2, 1);
        dag.addEdge(1, 3, 1);
        dag.addEdge(2, 3, 1);

        List<Integer> order = TopologicalSort.sort(dag);
        int[] position = new int[4];

        for (int i = 0; i < order.size(); i++) {
            position[order.get(i)] = i;
        }

        for (Edge edge : dag.edges()) {
            check(
                    position[edge.from] < position[edge.to],
                    "topological order respects every edge"
            );
        }

        dag.addEdge(3, 0, 1);

        check(
                TopologicalSort.hasCycle(dag),
                "cycle detection"
        );

        try {
            TopologicalSort.sort(dag);
            throw new AssertionError("Expected a cycle exception");
        } catch (IllegalStateException expected) {
            // Expected when the graph contains a cycle.
        }
    }

    private static void testSccPartition() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 2, 1);

        List<List<Integer>> components = SCC.find(graph);
        Set<Integer> vertices = new HashSet<>();

        for (List<Integer> component : components) {
            for (int vertex : component) {
                check(
                        vertices.add(vertex),
                        "each vertex appears in one SCC"
                );
            }
        }

        check(
                vertices.size() == 5 && components.size() == 3,
                "SCCs cover all vertices"
        );
    }

    private static void testDijkstraAndUnreachable() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 1);
        graph.addEdge(2, 1, 2);
        graph.addEdge(1, 3, 1);

        Dijkstra.Result result =
                Dijkstra.shortestPaths(graph, 0);

        check(
                result.distanceTo(3) == 4,
                "Dijkstra finds the shortest distance"
        );

        check(
                result.pathTo(3).equals(List.of(0, 2, 1, 3)),
                "Dijkstra reconstructs the shortest path"
        );

        check(
                result.distanceTo(4) == Long.MAX_VALUE
                        && result.pathTo(4).isEmpty(),
                "unreachable vertex"
        );
    }

    private static void testGenerators() {
        Graph undirected =
                GraphGenerator.connectedUndirected(10, 30, 7);

        Graph dag =
                GraphGenerator.directedDag(10, 30, 8);

        Graph cyclic =
                GraphGenerator.directedCyclic(10, 30, 9);

        check(
                undirected.edgeCount() == 30
                        && Kruskal.findMST(undirected).isConnected(),
                "connected graph generator"
        );

        check(
                dag.edgeCount() == 30
                        && !TopologicalSort.hasCycle(dag),
                "DAG generator"
        );

        check(
                cyclic.edgeCount() == 30
                        && TopologicalSort.hasCycle(cyclic),
                "cyclic graph generator"
        );
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("FAILED: " + message);
        }
    }
}