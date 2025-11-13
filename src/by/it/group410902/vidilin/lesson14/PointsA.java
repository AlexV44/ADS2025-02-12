package by.it.group410902.vidilin.lesson14;

import java.util.*;

public class PointsA {

    static class Point {
        double x, y, z;

        Point(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        double distanceTo(Point other) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            double dz = this.z - other.z;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }

    static class DSU {
        private int[] parent;
        private int[] rank;
        private int[] size;

        public DSU(int n) {
            parent = new int[n];
            rank = new int[n];
            size = new int[n];

            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
                rank[i] = 0;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) return;

            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
                size[rootY] += size[rootX];
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
                size[rootX] += size[rootY];
            } else {
                parent[rootY] = rootX;
                size[rootX] += size[rootY];
                rank[rootX]++;
            }
        }

        public List<Integer> getClusterSizes() {
            List<Integer> sizes = new ArrayList<>();
            boolean[] visited = new boolean[parent.length];

            for (int i = 0; i < parent.length; i++) {
                int root = find(i);
                if (!visited[root]) {
                    visited[root] = true;
                    sizes.add(size[root]);
                }
            }

            // Сортируем по убыванию, как в примере теста (60 2 1)
            sizes.sort(Collections.reverseOrder());
            return sizes;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Читаем максимальное расстояние и количество точек
        double maxDistance = scanner.nextDouble();
        int n = scanner.nextInt();

        // Читаем точки
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            double x = scanner.nextDouble();
            double y = scanner.nextDouble();
            double z = scanner.nextDouble();
            points[i] = new Point(x, y, z);
        }

        scanner.close();

        DSU dsu = new DSU(n);

        // Объединяем точки, если расстояние между ними строго меньше maxDistance
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double distance = points[i].distanceTo(points[j]);
                // Используем строгое сравнение < согласно условию [0,D)
                if (distance < maxDistance) {
                    dsu.union(i, j);
                }
            }
        }

        List<Integer> clusterSizes = dsu.getClusterSizes();

        // Вывод в формате, который ожидает тест
        if (!clusterSizes.isEmpty()) {
            System.out.print(clusterSizes.get(0));
            for (int i = 1; i < clusterSizes.size(); i++) {
                System.out.print(" " + clusterSizes.get(i));
            }
        }
        System.out.println();
    }
}