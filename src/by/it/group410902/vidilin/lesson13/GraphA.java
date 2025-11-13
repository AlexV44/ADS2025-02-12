package by.it.group410902.vidilin.lesson13;

import java.util.*;

public class GraphA {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        String input = scanner.nextLine();

        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        String[] edges = input.split(", ");

        for (String edge : edges) {
            String[] parts = edge.split(" -> ");
            String from = parts[0].trim();
            String to = parts[1].trim();

            // Добавляем ребро в граф
            graph.putIfAbsent(from, new ArrayList<>());
            graph.putIfAbsent(to, new ArrayList<>());
            graph.get(from).add(to);

            // Обновляем полустепени захода
            inDegree.putIfAbsent(from, 0);
            inDegree.put(to, inDegree.getOrDefault(to, 0) + 1);
        }

        // Топологическая сортировка
        List<String> result = topologicalSort(graph, inDegree);

        // Выводим результат
        for (int i = 0; i < result.size(); i++) {
            System.out.print(result.get(i));
            if (i < result.size() - 1) {
                System.out.print(" ");
            }
        }
    }

    private static List<String> topologicalSort(Map<String, List<String>> graph,
                                                Map<String, Integer> inDegree) {
        List<String> result = new ArrayList<>();

        // PriorityQueue для лексикографического порядка при равнозначности
        PriorityQueue<String> queue = new PriorityQueue<>();

        // Добавляем вершины с нулевой полустепенью захода
        for (String node : inDegree.keySet()) {
            if (inDegree.get(node) == 0) {
                queue.offer(node);
            }
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            // Уменьшаем полустепень захода для всех соседей
            if (graph.containsKey(current)) {
                for (String neighbor : graph.get(current)) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                    if (inDegree.get(neighbor) == 0) {
                        queue.offer(neighbor);
                    }
                }
            }
        }

        return result;
    }
}