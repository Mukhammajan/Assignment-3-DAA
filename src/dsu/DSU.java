package dsu;

public final class DSU {
    private final int[] parent;
    private final int[] rank;

    private int components;
    private long findCalls;
    private long parentHops;
    private long unionCalls;

    public DSU(int n) {
        if (n < 1) {
            throw new IllegalArgumentException(
                    "DSU needs at least one element"
            );
        }

        parent = new int[n];
        rank = new int[n];
        components = n;

        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    public int find(int value) {
        check(value);
        findCalls++;

        if (parent[value] != value) {
            parentHops++;
            parent[value] = find(parent[value]);
        }

        return parent[value];
    }

    public boolean union(int a, int b) {
        unionCalls++;

        int rootA = find(a);
        int rootB = find(b);

        if (rootA == rootB) {
            return false;
        }

        if (rank[rootA] < rank[rootB]) {
            parent[rootA] = rootB;
        } else if (rank[rootA] > rank[rootB]) {
            parent[rootB] = rootA;
        } else {
            parent[rootB] = rootA;
            rank[rootA]++;
        }

        components--;
        return true;
    }

    private void check(int value) {
        if (value < 0 || value >= parent.length) {
            throw new IndexOutOfBoundsException(
                    "Invalid DSU index: " + value
            );
        }
    }

    public int components() {
        return components;
    }

    public long findCalls() {
        return findCalls;
    }

    public long parentHops() {
        return parentHops;
    }

    public long unionCalls() {
        return unionCalls;
    }
}