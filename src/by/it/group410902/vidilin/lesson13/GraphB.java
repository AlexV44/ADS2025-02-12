package by.it.group410902.vidilin.lesson13;

import java.util.*;

public class GraphB {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Считываем строку с описанием графа
        String input = scanner.nextLine();

        // Парсим строку и строим граф
        Map<Integer, List<Integer>> graph = new HashMap<>();
        Set<Integer> allNodes = new HashSet<>();

        // Разделяем по запятым для получения отдельных рёбер
        String[] edges = input.split(", ");

        for (String edge : edges) {
            // Разделяем каждое ребро на вершину источника и вершину назначения
            String[] parts = edge.split(" -> ");
            int from = Integer.parseInt(parts[0].trim());
            int to = Integer.parseInt(parts[1].trim());

            // Добавляем ребро в граф
            graph.putIfAbsent(from, new ArrayList<>());
            graph.get(from).add(to);

            // Сохраняем все вершины
            allNodes.add(from);
            allNodes.add(to);
        }

        // Проверяем наличие циклов
        boolean hasCycle = hasCycle(graph, allNodes);

        // Выводим результат
        System.out.println(hasCycle ? "yes" : "no");
    }

    private static boolean hasCycle(Map<Integer, List<Integer>> graph, Set<Integer> allNodes) {
        // Три состояния: 0 - не посещён, 1 - посещается, 2 - полностью обработан
        Map<Integer, Integer> visited = new HashMap<>();

        // Инициализируем все вершины как не посещённые
        for (Integer node : allNodes) {
            visited.put(node, 0);
        }

        // Проверяем каждую вершину на наличие циклов
        for (Integer node : allNodes) {
            if (visited.get(node) == 0) {
                if (dfs(node, graph, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean dfs(Integer node, Map<Integer, List<Integer>> graph,
                               Map<Integer, Integer> visited) {
        // Помечаем вершину как посещаемую (в текущем рекурсивном стеке)
        visited.put(node, 1);

        // Проверяем всех соседей
        if (graph.containsKey(node)) {
            for (Integer neighbor : graph.get(node)) {
                if (visited.get(neighbor) == 0) {
                    // Если сосед не посещён, рекурсивно проверяем его
                    if (dfs(neighbor, graph, visited)) {
                        return true;
                    }
                } else if (visited.get(neighbor) == 1) {
                    // Если сосед находится в текущем рекурсивном стеке - найден цикл
                    return true;
                }
            }
        }

        // Помечаем вершину как полностью обработанную
        visited.put(node, 2);
        return false;
    }
}