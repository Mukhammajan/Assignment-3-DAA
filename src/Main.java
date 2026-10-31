import experiments.Benchmark;
import graph.Graph;
import mst.Kruskal;

public final class Main {
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("--benchmark")) {
            Benchmark.main(new String[0]);
            return;
        }

        if (args.length > 0 && args[0].equals("--test")) {
            tests.AlgorithmTests.main(new String[0]);
            return;
        }

        Graph graph = new Graph(4, false);
        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 2, 6);
        graph.addEdge(0, 3, 5);
        graph.addEdge(1, 3, 15);
        graph.addEdge(2, 3, 4);

        System.out.println(
                "MST weight: " + Kruskal.findMST(graph).getTotalWeight()
        );
        System.out.println(
                "Run with --benchmark to generate results."
        );
    }
}