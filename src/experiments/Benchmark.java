package experiments;
import connectivity.SCC;
import connectivity.TopologicalSort;
import graph.Graph;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;
import shortestpath.Dijkstra;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public final class Benchmark {
    private static final int REPEATS = 5;
    private static final long SEED = 20260317L;
    private record Size(String name, int vertices, int edges) { }
    private record Result(
            String size,
            int vertices,
            int edges,
            String algorithm,
            long averageNanos,
            long averageOperations
    ){}
    private interface Operation {
        long run();
    }
    private Benchmark() { }
    public static void main(String[] args) throws IOException {
        Path root = projectRoot();
        Path tables = root.resolve("results/tables");
        Path plots = root.resolve("results/plots");
        Files.createDirectories(tables);
        Files.createDirectories(plots);
        List<Result> results = new ArrayList<>();
        List<Size> sizes = List.of(
                new Size("Small", 10, 30),
                new Size("Medium", 100, 500),
                new Size("Large", 1000, 5000)
        );

        for (Size size : sizes) {
            runAlgorithms(size, results);
        }

        Path csv = tables.resolve("benchmark_results.csv");
        writeCsv(csv, results);
        PlotGenerator.createPlots(csv, plots);

        System.out.println("Benchmark table saved to: " + csv);
        System.out.println("Plots saved to: " + plots);
    }

    private static void runAlgorithms(
            Size size,
            List<Result> results
    ) {
        Graph undirected = GraphGenerator.connectedUndirected(
                size.vertices(),
                size.edges(),
                SEED + size.vertices()
        );

        run(results, size, "Kruskal",
                () -> Kruskal.findMST(undirected).getOperations());

        run(results, size, "Prim",
                () -> Prim.findMST(undirected).getOperations());

        MSTResult kruskal = Kruskal.findMST(undirected);
        MSTResult prim = Prim.findMST(undirected);

        if (!kruskal.isConnected()
                || !prim.isConnected()
                || kruskal.getTotalWeight() != prim.getTotalWeight()) {
            throw new IllegalStateException(
                    "Kruskal and Prim results do not match"
            );
        }

        Graph dag = GraphGenerator.directedDag(
                size.vertices(),
                size.edges(),
                SEED + 10_000 + size.vertices()
        );

        run(results, size, "Kosaraju-SCC", () -> {
            SCC.find(dag);
            return SCC.getLastOperations();
        });

        run(results, size, "Topological-DAG", () -> {
            TopologicalSort.sort(dag);
            return TopologicalSort.getLastOperations();
        });

        run(results, size, "Dijkstra-DAG",
                () -> Dijkstra.shortestPaths(dag, 0).getOperations());

        Graph cyclic = GraphGenerator.directedCyclic(
                size.vertices(),
                size.edges(),
                SEED + 20_000 + size.vertices()
        );

        run(results, size, "Topological-cycle", () -> {
            try {
                TopologicalSort.sort(cyclic);
            } catch (IllegalStateException expected) {
                return TopologicalSort.getLastOperations();
            }

            throw new IllegalStateException(
                    "Cycle was not detected"
            );
        });
    }

    private static void run(
            List<Result> results,
            Size size,
            String algorithm,
            Operation operation
    ) {
        operation.run();
        operation.run();

        long totalNanos = 0;
        long totalOperations = 0;

        for (int i = 0; i < REPEATS; i++) {
            long start = System.nanoTime();
            long operations = operation.run();

            totalNanos += System.nanoTime() - start;
            totalOperations += operations;
        }

        results.add(new Result(
                size.name(),
                size.vertices(),
                size.edges(),
                algorithm,
                totalNanos / REPEATS,
                totalOperations / REPEATS
        ));
    }

    private static void writeCsv(
            Path path,
            List<Result> results
    ) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(
                "size,vertices,edges,algorithm,"
                        + "average_time_ns,average_operations"
        );

        for (Result result : results) {
            lines.add(String.format(
                    Locale.ROOT,
                    "%s,%d,%d,%s,%d,%d",
                    result.size(),
                    result.vertices(),
                    result.edges(),
                    result.algorithm(),
                    result.averageNanos(),
                    result.averageOperations()
            ));
        }
        Files.write(path, lines);
    }
    private static Path projectRoot() {
        Path current = Path.of("").toAbsolutePath();

        for (Path path = current;
             path != null;
             path = path.getParent()) {
            if (Files.isDirectory(path.resolve("src"))
                    && Files.isDirectory(path.resolve("results"))) {
                return path;
            }
        }
        return current;
    }
}