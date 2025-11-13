package by.it.group410902.vidilin.lesson13;

import java.util.*;

public class GraphC {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Считываем строку с описанием графа
        String input = scanner.nextLine();

        // Парсим строку и строим граф
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, List<String>> reverseGraph = new HashMap<>();
        Set<String> allNodes = new HashSet<>();

        // Разделяем по запятым для получения отдельных рёбер
        String[] edges = input.split(", ");

        for (String edge : edges) {
            // Разделяем каждое ребро на вершину источника и вершину назначения
            String[] parts = edge.split("->");
            String from = parts[0].trim();
            String to = parts[1].trim();

            // Добавляем ребро в граф
            graph.putIfAbsent(from, new ArrayList<>());
            graph.get(from).add(to);

            // Строим обратный граф
            reverseGraph.putIfAbsent(to, new ArrayList<>());
            reverseGraph.get(to).add(from);

            // Сохраняем все вершины
            allNodes.add(from);
            allNodes.add(to);
        }

        // Находим компоненты сильной связности
        List<List<String>> scc = findSCC(graph, reverseGraph, allNodes);

        // Сортируем компоненты в порядке исток -> сток
        sortSCCInTopologicalOrder(scc, graph);

        // Выводим результат
        for (List<String> component : scc) {
            Collections.sort(component); // лексикографическая сортировка внутри компонента
            StringBuilder sb = new StringBuilder();
            for (String node : component) {
                sb.append(node);
            }
            System.out.println(sb.toString());
        }
    }

    private static List<List<String>> findSCC(Map<String, List<String>> graph,
                                              Map<String, List<String>> reverseGraph,
                                              Set<String> allNodes) {
        List<List<String>> scc = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Stack<String> stack = new Stack<>();

        // Первый проход DFS для заполнения стека
        for (String node : allNodes) {
            if (!visited.contains(node)) {
                dfsFirstPass(node, graph, visited, stack);
            }
        }

        visited.clear();

        // Второй проход DFS по обратному графу
        while (!stack.isEmpty()) {
            String node = stack.pop();
            if (!visited.contains(node)) {
                List<String> component = new ArrayList<>();
                dfsSecondPass(node, reverseGraph, visited, component);
                scc.add(component);
            }
        }

        return scc;
    }

    private static void dfsFirstPass(String node, Map<String, List<String>> graph,
                                     Set<String> visited, Stack<String> stack) {
        visited.add(node);

        if (graph.containsKey(node)) {
            for (String neighbor : graph.get(node)) {
                if (!visited.contains(neighbor)) {
                    dfsFirstPass(neighbor, graph, visited, stack);
                }
            }
        }

        stack.push(node);
    }

    private static void dfsSecondPass(String node, Map<String, List<String>> reverseGraph,
                                      Set<String> visited, List<String> component) {
        visited.add(node);
        component.add(node);

        if (reverseGraph.containsKey(node)) {
            for (String neighbor : reverseGraph.get(node)) {
                if (!visited.contains(neighbor)) {
                    dfsSecondPass(neighbor, reverseGraph, visited, component);
                }
            }
        }
    }

    private static void sortSCCInTopologicalOrder(List<List<String>> scc, Map<String, List<String>> graph) {
        // Строим граф компонент
        Map<List<String>, List<List<String>>> componentGraph = new HashMap<>();
        Map<String, List<String>> nodeToComponent = new HashMap<>();

        // Создаем mapping вершина -> её компонента
        for (List<String> component : scc) {
            for (String node : component) {
                nodeToComponent.put(node, component);
            }
        }

        // Строим рёбра между компонентами
        for (List<String> component : scc) {
            componentGraph.put(component, new ArrayList<>());
            for (String node : component) {
                if (graph.containsKey(node)) {
                    for (String neighbor : graph.get(node)) {
                        List<String> neighborComponent = nodeToComponent.get(neighbor);
                        if (!component.equals(neighborComponent) &&
                                !componentGraph.get(component).contains(neighborComponent)) {
                            componentGraph.get(component).add(neighborComponent);
                        }
                    }
                }
            }
        }

        // Топологическая сортировка компонент
        Map<List<String>, Integer> inDegree = new HashMap<>();
        for (List<String> component : scc) {
            inDegree.put(component, 0);
        }

        for (List<String> component : scc) {
            for (List<String> neighbor : componentGraph.get(component)) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1);
            }
        }

        Queue<List<String>> queue = new LinkedList<>();
        for (List<String> component : scc) {
            if (inDegree.get(component) == 0) {
                queue.offer(component);
            }
        }

        List<List<String>> sortedSCC = new ArrayList<>();
        while (!queue.isEmpty()) {
            List<String> current = queue.poll();
            sortedSCC.add(current);

            for (List<String> neighbor : componentGraph.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        // Обновляем исходный список
        scc.clear();
        scc.addAll(sortedSCC);
    }
}