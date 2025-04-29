/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bridgesbase;

import bridges.base.Color;
import bridges.base.Edge;
import bridges.base.Element;
import bridges.base.GraphAdjList;
import bridges.connect.Bridges;
import bridges.validation.RateLimitException;
import java.util.*;

public class GraphSearch {
    /**
     * Runs all the logic for the BRIDGES assignment. Call from main.
     * @param bridges The initialized Bridges object
     * @throws java.io.IOException when there is a problem communicating
     * with the BRIDGES server.
     */
    public void run(Bridges bridges) throws java.io.IOException, RateLimitException {
        // Set some information for the BRIDGES object
        bridges.setTitle("Graph search algorithms");
        var highways = buildHighwayGraph();
        var searchTreeDFS = depthFirstSearch(highways, "Chicago");
        var searchTreeBFS = breadthFirstSearch(highways, "Chicago");
        
        int numComponents = countAndLabelComponents(highways);
        System.out.println("Number of connected components: " + numComponents);

        // Display the original graph
        bridges.setDataStructure(highways);
        bridges.visualize();
        
        // Display the result of the DFS search
        bridges.setDataStructure(searchTreeDFS);
        bridges.visualize();
        
        // Display the result of the BFS search
        bridges.setDataStructure(searchTreeBFS);
        bridges.visualize();
    }

    /**
     * Depth-First Search
     */
    private GraphAdjList<String, String, Void> depthFirstSearch(GraphAdjList<String, String, Double> g, String start) {
        GraphAdjList<String, String, Void> searchTree = new GraphAdjList<>();
        HashMap<String, Boolean> vertexMarks = new HashMap<>();
        for (String v : g.getVertices().keySet())
            vertexMarks.put(v, false);
        
        Stack<Edge<String, Void>> stack = new Stack<>();
        stack.push(new Edge<>(null, start, null));
        
        while (!stack.isEmpty()) {
            Edge<String, Void> e = stack.pop();
            String p = e.getFrom();
            String v = e.getTo();
            
            if (!vertexMarks.get(v)) {
                vertexMarks.put(v, true);
                searchTree.addVertex(v, v);
                searchTree.getVertex(v).setColor(g.getVertex(v).getColor());
                if (p != null)
                    searchTree.addEdge(p, v);
                
                // Push neighbors onto the stack (LIFO order)
                for (Edge<String, Double> vw : g.getAdjacencyList(v))
                    stack.push(new Edge<>(v, vw.getTo(), null));
            }
        }
        return searchTree;
    }
    
    /**
     * Breadth-First Search 
     */
    private GraphAdjList<String, String, Void> breadthFirstSearch(GraphAdjList<String, String, Double> g, String start) {
        GraphAdjList<String, String, Void> searchTree = new GraphAdjList<>();
        HashMap<String, Boolean> vertexMarks = new HashMap<>();
        for (String v : g.getVertices().keySet())
            vertexMarks.put(v, false);
        
        Queue<Edge<String, Void>> queue = new LinkedList<>();
        queue.add(new Edge<>(null, start, null));
        
        while (!queue.isEmpty()) {
            Edge<String, Void> e = queue.poll();
            String p = e.getFrom();
            String v = e.getTo();
            
            if (!vertexMarks.get(v)) {
                vertexMarks.put(v, true);
                searchTree.addVertex(v, v);
                searchTree.getVertex(v).setColor(g.getVertex(v).getColor());
                if (p != null)
                    searchTree.addEdge(p, v);
                
                // Enqueue neighbors (FIFO order)
                for (Edge<String, Double> vw : g.getAdjacencyList(v))
                    queue.add(new Edge<>(v, vw.getTo(), null));
            }
        }
        return searchTree;
    }
    
    private static GraphAdjList<String, String, Double> buildHighwayGraph() {
        // Start a graph
        GraphAdjList<String, String, Double> highways = new GraphAdjList<>();
        
        // Add vertices (cities)
        highways.addVertex("Chicago", "Chicago");
        highways.addVertex("Detroit", "Detroit");
        highways.addVertex("Atlanta", "Atlanta");
        highways.addVertex("St. Louis", "St. Louis");
        highways.addVertex("New Orleans", "New Orleans");
        highways.addVertex("Birmingham", "Birmingham");
        highways.addVertex("Kansas City", "Kansas City");
        highways.addVertex("Indianapolis", "Indianapolis");
        highways.addVertex("Cincinnati", "Cincinnati");
        
        // Add edges (highways)
        highways.addEdge("Chicago", "Detroit", 286.0);
        highways.addEdge("Detroit", "Chicago", 286.0);
        highways.addEdge("Chicago", "Indianapolis", 183.0);
        highways.addEdge("Indianapolis", "Chicago", 183.0);
        highways.addEdge("Chicago", "Kansas City", 510.0);
        highways.addEdge("Kansas City", "Chicago", 510.0);
        highways.addEdge("St. Louis", "Chicago", 297.0);
        highways.addEdge("Chicago", "St. Louis", 297.0);
        highways.addEdge("Detroit", "Cincinnati", 264.0);
        highways.addEdge("Cincinnati", "Detroit", 264.0);
        highways.addEdge("Atlanta", "Cincinnati", 461.0);
        highways.addEdge("Cincinnati", "Atlanta", 461.0);
        highways.addEdge("Indianapolis", "Cincinnati", 112.0);
        highways.addEdge("Cincinnati", "Indianapolis", 112.0);
        highways.addEdge("Birmingham", "New Orleans", 342.0);
        highways.addEdge("New Orleans", "Birmingham", 342.0);
        
        // Assign colors to vertices
        for (Element<String> vertex : highways.getVertices().values())
            vertex.setColor(randomColor(vertex.getValue()));
        
        return highways;
    }
    
    private static Color randomColor(String s) {
        int hash = s.hashCode();
        float hue = (hash % 1000) / 1000.0f;
        java.awt.Color c555 = java.awt.Color.getHSBColor(hue, .5f, .8f);
        return new Color(c555.getRed(), c555.getGreen(), c555.getBlue());
    }
    
    private int countAndLabelComponents(GraphAdjList<String, String, Double> g) {
        int compLabel = 0;
        Map<String, Integer> labels = new HashMap<>();
    
        // Initialize labels
        for (String vertex : g.getVertices().keySet()) {
            labels.put(vertex, 0);
        }
    
        // Traverse unvisited components
        for (String vertex : g.getVertices().keySet()) {
            if (labels.get(vertex) == 0) {
                compLabel++;
                dfsLabel(g, vertex, compLabel, labels);
            }
        }
    
        // Optional: You can print the labels for debugging
        for (var entry : labels.entrySet()) {
            System.out.println("Vertex: " + entry.getKey() + ", Component: " + entry.getValue());
        }
    
        return compLabel;
    }

    private void dfsLabel(GraphAdjList<String, String, Double> g, String start, int label, Map<String, Integer> labels) {
        Stack<String> stack = new Stack<>();
        stack.push(start);
    
        while (!stack.isEmpty()) {
            String current = stack.pop();
    
            if (labels.get(current) == 0) {
                labels.put(current, label);
    
                var neighbors = g.getAdjacencyList(current);
                if (neighbors != null) {
                    for (Edge<String, Double> edge : neighbors) {
                        String neighbor = edge.getTo();
                        if (labels.get(neighbor) == 0) {
                            stack.push(neighbor);
                        }
                    }
                }
            }
        }
    }
}

