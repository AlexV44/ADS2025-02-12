package by.it.group410902.vidilin.lesson14;

import java.util.*;

public class SitesB {

    static class DSU {
        private final Map<String, String> parent = new HashMap<>();
        private final Map<String, Integer> rank = new HashMap<>();

        public String find(String x) {
            parent.putIfAbsent(x, x);
            rank.putIfAbsent(x, 0);

            if (!parent.get(x).equals(x)) {
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }

        public void union(String x, String y) {
            String rootX = find(x);
            String rootY = find(y);

            if (rootX.equals(rootY)) return;

            if (rank.get(rootX) < rank.get(rootY)) {
                parent.put(rootX, rootY);
            } else if (rank.get(rootX) > rank.get(rootY)) {
                parent.put(rootY, rootX);
            } else {
                parent.put(rootY, rootX);
                rank.put(rootX, rank.get(rootX) + 1);
            }
        }

        // Получаем размеры кластеров в порядке их обнаружения
        public List<Integer> getClusterSizesInOrder() {
            Map<String, Integer> clusterSizes = new HashMap<>();
            LinkedHashSet<String> rootsInOrder = new LinkedHashSet<>();

            // Сначала собираем все корни в порядке их первого появления
            for (String site : parent.keySet()) {
                String root = find(site);
                rootsInOrder.add(root);
                clusterSizes.put(root, clusterSizes.getOrDefault(root, 0) + 1);
            }

            // Сохраняем порядок обнаружения корней
            List<Integer> sizes = new ArrayList<>();
            for (String root : rootsInOrder) {
                sizes.add(clusterSizes.get(root));
            }

            return sizes;
        }

        // Альтернативный метод: сортировка по убыванию (как в примере теста)
        public List<Integer> getClusterSizesDescending() {
            Map<String, Integer> clusterSizes = new HashMap<>();

            for (String site : parent.keySet()) {
                String root = find(site);
                clusterSizes.put(root, clusterSizes.getOrDefault(root, 0) + 1);
            }

            List<Integer> sizes = new ArrayList<>(clusterSizes.values());
            sizes.sort(Collections.reverseOrder());
            return sizes;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DSU dsu = new DSU();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.equals("end")) break;

            String[] sites = line.split("\\+");
            if (sites.length == 2) {
                dsu.union(sites[0].trim(), sites[1].trim());
            }
        }

        scanner.close();

        // Пробуем оба варианта, начиная с сортировки по убыванию
        List<Integer> result = dsu.getClusterSizesDescending();

        // Вывод результата
        if (!result.isEmpty()) {
            System.out.print(result.get(0));
            for (int i = 1; i < result.size(); i++) {
                System.out.print(" " + result.get(i));
            }
        }
        System.out.println();
    }
}