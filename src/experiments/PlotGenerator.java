package experiments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PlotGenerator {
    private record Row(
            int vertices,
            int edges,
            String algorithm,
            long time
    ) { }

    private static final String[] ALGORITHMS = {
            "Kruskal",
            "Prim",
            "Kosaraju-SCC",
            "Topological-DAG",
            "Dijkstra-DAG",
            "Topological-cycle"
    };

    private static final String[] COLORS = {
            "#c0392b",
            "#2471a3",
            "#148f77",
            "#7d3c98",
            "#d68910",
            "#555555"
    };

    private PlotGenerator() { }

    public static void createPlots(
            Path csvFile,
            Path outputDirectory
    ) throws IOException {
        Files.createDirectories(outputDirectory);
        List<Row> rows = readCsv(csvFile);

        createPlot(
                rows,
                outputDirectory.resolve("time_vs_vertices.svg"),
                true
        );

        createPlot(
                rows,
                outputDirectory.resolve("time_vs_edges.svg"),
                false
        );
    }

    private static List<Row> readCsv(Path csvFile) throws IOException {
        List<String> lines = Files.readAllLines(csvFile);
        List<Row> rows = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String[] columns = lines.get(i).split(",");

            rows.add(new Row(
                    Integer.parseInt(columns[1]),
                    Integer.parseInt(columns[2]),
                    columns[3],
                    Long.parseLong(columns[4])
            ));
        }

        return rows;
    }

    private static void createPlot(
            List<Row> rows,
            Path outputFile,
            boolean useVertices
    ) throws IOException {
        int width = 900;
        int height = 520;
        int left = 90;
        int right = 35;
        int top = 50;
        int bottom = 80;
        int plotWidth = width - left - right;
        int plotHeight = height - top - bottom;

        long maxX = rows.stream()
                .mapToLong(row -> useVertices
                        ? row.vertices()
                        : row.edges())
                .max()
                .orElse(1);

        long maxY = rows.stream()
                .mapToLong(Row::time)
                .max()
                .orElse(1);

        String xLabel = useVertices
                ? "Number of vertices"
                : "Number of edges";

        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns='http://www.w3.org/2000/svg' ")
                .append("width='900' height='520'>");
        svg.append("<rect width='100%' height='100%' fill='white'/>");

        svg.append("<text x='450' y='25' text-anchor='middle' ")
                .append("font-family='Arial' font-size='19'>")
                .append("Execution time vs. ")
                .append(xLabel)
                .append("</text>");

        drawAxes(
                svg,
                left,
                top,
                plotWidth,
                plotHeight,
                maxY,
                xLabel
        );

        for (int i = 0; i < ALGORITHMS.length; i++) {
            drawAlgorithm(
                    svg,
                    rows,
                    ALGORITHMS[i],
                    COLORS[i],
                    i,
                    useVertices,
                    maxX,
                    maxY,
                    left,
                    top,
                    plotWidth,
                    plotHeight,
                    height
            );
        }

        svg.append("</svg>");
        Files.writeString(outputFile, svg.toString());
    }

    private static void drawAxes(
            StringBuilder svg,
            int left,
            int top,
            int plotWidth,
            int plotHeight,
            long maxY,
            String xLabel
    ) {
        for (int tick = 0; tick <= 5; tick++) {
            int y = top + plotHeight - tick * plotHeight / 5;
            double milliseconds =
                    (double) maxY * tick / 5 / 1_000_000;

            svg.append("<line x1='").append(left)
                    .append("' y1='").append(y)
                    .append("' x2='").append(left + plotWidth)
                    .append("' y2='").append(y)
                    .append("' stroke='#dddddd'/>");

            svg.append("<text x='").append(left - 8)
                    .append("' y='").append(y + 4)
                    .append("' text-anchor='end' font-family='Arial' ")
                    .append("font-size='11'>")
                    .append(String.format("%.2f ms", milliseconds))
                    .append("</text>");
        }
        svg.append("<text x='450' y='500' text-anchor='middle' ")
                .append("font-family='Arial'>")
                .append(xLabel)
                .append("</text>");

        svg.append("<text transform='translate(18 280) rotate(-90)' ")
                .append("text-anchor='middle' font-family='Arial'>")
                .append("Average execution time")
                .append("</text>");
    }

    private static void drawAlgorithm(
            StringBuilder svg,
            List<Row> rows,
            String algorithm,
            String color,
            int index,
            boolean useVertices,
            long maxX,
            long maxY,
            int left,
            int top,
            int plotWidth,
            int plotHeight,
            int height
    ) {
        List<Row> selected = rows.stream()
                .filter(row -> row.algorithm().equals(algorithm))
                .sorted(Comparator.comparingInt(row -> useVertices
                        ? row.vertices()
                        : row.edges()))
                .toList();

        StringBuilder points = new StringBuilder();

        for (Row row : selected) {
            long xValue = useVertices
                    ? row.vertices()
                    : row.edges();

            int x = left + (int) (xValue * plotWidth / maxX);
            int y = top + plotHeight
                    - (int) (row.time() * plotHeight / maxY);

            points.append(x).append(',').append(y).append(' ');

            svg.append("<circle cx='").append(x)
                    .append("' cy='").append(y)
                    .append("' r='4' fill='")
                    .append(color)
                    .append("'/>");
        }

        if (!selected.isEmpty()) {
            svg.append("<polyline points='")
                    .append(points)
                    .append("' fill='none' stroke='")
                    .append(color)
                    .append("' stroke-width='2'/>");
        }

        int legendX = 100 + (index % 3) * 240;
        int legendY = height - 42 + (index / 3) * 18;

        svg.append("<line x1='").append(legendX)
                .append("' y1='").append(legendY)
                .append("' x2='").append(legendX + 20)
                .append("' y2='").append(legendY)
                .append("' stroke='").append(color)
                .append("' stroke-width='3'/>");

        svg.append("<text x='").append(legendX + 25)
                .append("' y='").append(legendY + 4)
                .append("' font-family='Arial' font-size='11'>")
                .append(algorithm)
                .append("</text>");
    }
}