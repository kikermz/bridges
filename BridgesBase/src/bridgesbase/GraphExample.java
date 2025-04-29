/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
//Graph example using BRIDGES visualization (https://bridgesuncc.github.io)
//Created by James Vanderhyde, 26 March 2025

package bridgesbase;
import bridges.base.Edge;
import bridges.base.GraphAdjList;
import bridges.connect.Bridges;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class GraphExample{
/**
* Runs all the logic for the BRIDGES assignment. Call from main.
* @param bridges The initialized Bridges object
* @throws java.io.IOException when there is a problem communicating
* with the BRIDGES server.
*/
public void run(Bridges bridges) throws java.io.IOException
{
//Set some information for the BRIDGES object
bridges.setTitle("Graph example");
//Start a graph
GraphAdjList<String, String, Double> highways = new GraphAdjList<>();
//Add vertices (cities) using the city name for the and for the vertex data
highways.addVertex("Chicago", "Chicago");
highways.addVertex("Detroit", "Detroit");
highways.addVertex("Atlanta", "Atlanta");
highways.addVertex("St. Loius", "St. Loius");
highways.addVertex("New Orleans", "New Orleans");
highways.addVertex("Birmingham", "Birmingham");
highways.addVertex("Kansas City", "Kansas City");
highways.addVertex("Indianapolis", "Indianapolis");


//Add edges (highways) using the length of the highway for the edge data
highways.addEdge("Chicago", "Detroit", 286.0);
highways.addEdge("Detroit", "Chicago", 286.0);
highways.addEdge("Chicago", "Indianapolis", 183.0);
highways.addEdge("Indianapolis", "Chicago", 183.0);
highways.addEdge("St. Loius", "Chicago", 297.0);
highways.addEdge("Chicago", "St. Loius", 297.0);
highways.addEdge("Detroit", "Atlanta", 722.0);
highways.addEdge("Atlanta", "Detroit", 722.0);

highways.addEdge("St. Loius", "Indianapolis", 242.0);
highways.addEdge("Indianapolis", "St. Loius", 242.0);
highways.addEdge("St. Loius", "Kansas City", 248.0);
highways.addEdge("Kansas City", "St. Loius", 248.0);
highways.addEdge("Kansas City", "New Orleans", 866.0);
highways.addEdge("New Orleans", "Kansas City", 866.0);
highways.addEdge("Kansas City", "Atlanta", 800.0);
highways.addEdge("Atlanta", "Kansas City", 800.0);
highways.addEdge("Birmingham", "Atlanta", 147.0);
highways.addEdge("Atlanta", "Birmingham", 147.0);
highways.addEdge("Birmingham", "New Orleans", 344.0);
highways.addEdge("New Orleans", "Birmingham", 344.0);
highways.addEdge("Indianapolis", "Atlanta", 535.0);
highways.addEdge("Atlanta", "Indianapolis", 535.0);
highways.addEdge("St. Loius", "Birmingham", 410.0);
highways.addEdge("Birmingham", "St. Loius", 410.0);
highways.addEdge("Detroit", "Indianapolis", 289.0);
highways.addEdge("Indianapolis", "Detroit", 289.0);

// Print the adjacency list of Chicago using outgoingEdgeSetOf
        System.out.println("Adjacency List for Chicago:");
        for (Edge<String, Double> e : highways.outgoingEdgeSetOf("Chicago")) {
            System.out.println("Chicago -> " + e.getTo());
        }

        // Perform a random walk from Atlanta to Chicago
        randomWalk(highways, "Atlanta", "Chicago");
//finish

//Create a scene
bridges.setDataStructure(highways);

//Print the adjacency list of Chicago, using outgoingEdgeSetOf and a for loop
//https://bridgesuncc.github.io/doc/java-api/current/html/classbridges_1_1base_1_1_graph_adj_list.html
}




public void randomWalk(GraphAdjList<String, String, Double> highways, String start, String end) {
    Random rand = new Random();
    String current = start;
    List<String> path = new ArrayList<>();
    path.add(current);

    while (!current.equals(end)) {
        // Get outgoing edges for the current city
        Collection<Edge<String, Double>> neighbors = (Collection<Edge<String, Double>>) highways.outgoingEdgeSetOf(current); 

        if (neighbors == null || neighbors.isEmpty()) {
            System.out.println("No further paths from " + current);
            break;
        }

        // Convert to a list (if not already) to randomly select an edge
        List<Edge<String, Double>> neighborList = new ArrayList<>(neighbors);
        Edge<String, Double> randomEdge = neighborList.get(rand.nextInt(neighborList.size()));

        current = randomEdge.getTo();
        path.add(current);
    }

    System.out.println("Random Walk Path: " + String.join(" -> ", path));
}

}

