package bridgesbase;

import bridges.data_src_dependent.City;
import bridges.connect.DataSource;
import bridges.data_src_dependent.OsmVertex;
import bridges.data_src_dependent.OsmData;
import bridges.validation.RateLimitException;
import bridgesbase.QuadtreeSearch.Quadtree;
import bridges.data_src_dependent.OsmEdge;
import bridges.base.SymbolCollection;
import bridges.connect.Bridges;
import bridges.base.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Scanner;

public class QuadtreeSearch {

    public void run(Bridges bridges) throws java.io.IOException {
        // Set up BRIDGES
        bridges.setTitle("Quadtree Construction and Search");

        // Create the data connection object
        DataSource ds = bridges.getDataSource();

        // Fetch all US cities
        HashMap<String, String> params = new HashMap<>(); // Empty params for all cities
        Vector<City> cities = ds.getUSCitiesData(params);

        // Check if data is retrieved
        if (cities == null || cities.isEmpty()) {
            System.err.println("Error: No city data retrieved. Please check the parameters or API connection.");
            return;
        }

        System.out.println("Number of cities retrieved: " + cities.size());

        // Create a SymbolCollection for visualization
        SymbolCollection symbolCollection = new SymbolCollection();
        symbolCollection.setViewport(-125.0f, -66.93457f, 24.396308f, 49.384358f); // U.S. bounds

        // Create a Quadtree for spatial indexing
        Quadtree quadtree = new Quadtree(-180, 180, -90, 90); // Using lat/long bounds for the entire world

        // Add cities to the Quadtree and SymbolCollection
        for (City city : cities) {
            double lat = city.getLatitude();
            double lon = city.getLongitude();
            String cityName = city.getCity();

            // Add the city to the Quadtree
            ExtendedOsmVertex vertex = new ExtendedOsmVertex(lat, lon);
            quadtree.insert(vertex);

            // Create a text symbol for each city
            Text cityText = new Text(cityName);
            cityText.setAnchorLocation((float) lon, (float) lat);
            cityText.setFontSize((float) 0.02); // Reduced font size to make it smaller
            symbolCollection.addSymbol(cityText);
        }

        // Keep prompting the user for searches until they input 'q'
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter the name of the city to search (or 'q' to quit): ");
            String searchCityName = scanner.nextLine();

            // Exit the loop if the user inputs 'q'
            if (searchCityName.equalsIgnoreCase("q")) {
                break;
            }

            // Search for all cities with the given name
            List<ExtendedOsmVertex> matchingVertices = new ArrayList<>();
            for (City city : cities) {
                if (city.getCity().equalsIgnoreCase(searchCityName)) {
                    matchingVertices.add(new ExtendedOsmVertex(city.getLatitude(), city.getLongitude()));
                }
            }

            if (!matchingVertices.isEmpty()) {
                // Find the most populous city among the matches
                City mostPopulousCity = null;
                for (City city : cities) {
                    if (city.getCity().equalsIgnoreCase(searchCityName)) {
                        if (mostPopulousCity == null || city.getPopulation() > mostPopulousCity.getPopulation()) {
                            mostPopulousCity = city;
                        }
                    }
                }

                if (mostPopulousCity != null) {
                    double lat = mostPopulousCity.getLatitude();
                    double lon = mostPopulousCity.getLongitude();

                    // Adjust the viewport to zoom in closely to the most populous city
                    symbolCollection.setViewport((float) lon - 0.5f, (float) lon + 0.5f, (float) lat - 0.5f, (float) lat + 0.5f);

                    // Highlight the most populous city in red
                    Text cityText = new Text(mostPopulousCity.getCity());
                    cityText.setAnchorLocation((float) lon, (float) lat);
                    cityText.setFontSize((float) 0.05); // Slightly larger font size
                    cityText.setFillColor("red"); // Set color to red
                    symbolCollection.addSymbol(cityText);

                    System.out.println(mostPopulousCity.getCity() + " found and highlighted in red.");
                }
            } else {
                System.out.println(searchCityName + " not found in the dataset.");
            }

            try {
                // Update the visualization for each search
                bridges.setDataStructure(symbolCollection);
                bridges.visualize();
            } catch (RateLimitException e) {
                System.err.println("Rate limit exceeded: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Quadtree implementation to handle 2D spatial data
    class Quadtree {
        double xMin, xMax, yMin, yMax;  // Bounds of the region
        List<ExtendedOsmVertex> cities;  // Cities contained in this node
        Quadtree[] children;  // Child nodes

        Quadtree(double xMin, double xMax, double yMin, double yMax) {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
            this.cities = new ArrayList<>();
            this.children = new Quadtree[4];  // Four quadrants
        }

        // Check if a city is within this node's bounds
        boolean isWithinBounds(ExtendedOsmVertex city) {
            return city.getLatitude() >= yMin && city.getLatitude() <= yMax &&
                   city.getLongitude() >= xMin && city.getLongitude() <= xMax;
        }

        // Subdivide the node into four quadrants
        void subdivide() {
            double xMid = (xMin + xMax) / 2;
            double yMid = (yMin + yMax) / 2;

            children[0] = new Quadtree(xMin, xMid, yMin, yMid);  // Bottom-left
            children[1] = new Quadtree(xMid, xMax, yMin, yMid);  // Bottom-right
            children[2] = new Quadtree(xMin, xMid, yMid, yMax);  // Top-left
            children[3] = new Quadtree(xMid, xMax, yMid, yMax);  // Top-right
        }

        // Insert city into the quadtree
        void insert(ExtendedOsmVertex city) {
            if (!isWithinBounds(city)) return;  // If the city is outside bounds

            if (cities.size() < 4) {  // If the node can hold more cities, add here
                cities.add(city);
            } else {
                if (children[0] == null) subdivide();  // Subdivide if needed

                // Try inserting city in the children
                for (Quadtree child : children) {
                    child.insert(city);
                }
            }
        }

        // Find city in the quadtree
        boolean search(ExtendedOsmVertex target) {
            if (cities.contains(target)) {
                return true;  // Found the city
            }

            // Check in children nodes if subdivided
            if (children[0] != null) {
                for (Quadtree child : children) {
                    if (child.search(target)) {
                        return true;
                    }
                }
            }

            return false;  // City not found
        }
    }
}