package bridgesbase;

import bridges.data_src_dependent.City;
import bridges.connect.DataSource;
import bridges.validation.RateLimitException;
import bridges.base.SymbolCollection;
import bridges.connect.Bridges;
import bridges.base.Text;
import bridges.base.Circle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Scanner;

//So for this program a user will input a city name and a radius, and the program will find all cities within that radius using a quadtree for spatial indexing
// The program will also visualize the cities and the search radius using BRIDGES

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
            ExtendedOsmVertex vertex = new ExtendedOsmVertex(lat, lon, cityName);
            quadtree.insert(vertex);

            // Create a text symbol for each city
            Text cityText = new Text(cityName);
            cityText.setAnchorLocation((float) lon, (float) lat);
            cityText.setFontSize((float) 0.02); // Reduced font size to make it smaller
            symbolCollection.addSymbol(cityText);
        }

        // Keep prompting the user for searches until they input 'q'
try (Scanner scanner = new Scanner(System.in)) {
    while (true) {
        System.out.print("Enter the name of the city to search (or 'q' to quit): ");
        String searchCityName = scanner.nextLine();

        // Exit the loop if the user inputs 'q'
        if (searchCityName.equalsIgnoreCase("q")) {
            break;
        }

        System.out.print("Enter the radius (in miles) to search: ");
        double radiusInMiles = Double.parseDouble(scanner.nextLine());

        // Convert radius from miles to degrees
        double radiusInDegrees = radiusInMiles / 69.0;

        // Find the city to use as the query point
        City queryCity = null;
        for (City city : cities) {
            if (city.getCity().equalsIgnoreCase(searchCityName)) {
                queryCity = city;
                break;
            }
        }

        if (queryCity == null) {
            System.out.println("City not found in the dataset.");
            continue;
        }

        // Query the quadtree for cities within the radius
        List<ExtendedOsmVertex> nearbyCities = quadtree.pointsWithinRadius(queryCity.getLongitude(), queryCity.getLatitude(), radiusInDegrees);

        if (!nearbyCities.isEmpty()) {
            System.out.println("Cities within the radius:");
            for (ExtendedOsmVertex city : nearbyCities) {
                System.out.printf("City: %s at %.4f, %.4f\n", city.getCity(), city.getLatitude(), city.getLongitude());
            }
        } else {
            System.out.println("No cities found within the radius.");
        }

        // Draw a circle representing the search radius
        Circle searchRadiusCircle = new Circle((float) queryCity.getLongitude(), (float) queryCity.getLatitude(), (float) radiusInDegrees);
        searchRadiusCircle.setStrokeColor("blue"); // Set the circle's border color
        searchRadiusCircle.setStrokeWidth(0.02f); // Set the border thickness
        searchRadiusCircle.setFillColor("black"); // Set the fill color with transparency
        searchRadiusCircle.setOpacity(0.5f);
        symbolCollection.addSymbol(searchRadiusCircle);

        // Update the visualization for each search
        bridges.setDataStructure(symbolCollection);
        try {
            bridges.visualize();
        } catch (RateLimitException e) {
            System.err.println("Visualization failed due to rate limit: " + e.getMessage());
        }
    }
}

    }

    // Quadtree implementation to handle 2D spatial data
    static class Quadtree {
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

       
        public List<ExtendedOsmVertex> pointsWithinRadius(double qx, double qy, double radius) {
            List<ExtendedOsmVertex> results = new ArrayList<>();
            pointsWithinRadiusHelper(this, qx, qy, radius, results);
            return results;
        }

        private void pointsWithinRadiusHelper(Quadtree node, double qx, double qy, double radius, List<ExtendedOsmVertex> results) {
            if (node == null) return;

            // Compute squared min distance from (qx, qy) to this node's rectangle
            double dx = 0, dy = 0;
            if (qx < node.xMin) dx = node.xMin - qx;
            else if (qx > node.xMax) dx = qx - node.xMax;
            if (qy < node.yMin) dy = node.yMin - qy;
            else if (qy > node.yMax) dy = qy - node.yMax;
            double rectDistSq = dx * dx + dy * dy;
            if (rectDistSq > radius * radius) {
                
                return;
            }

            // Check each city in this node
            for (ExtendedOsmVertex city : node.cities) {
                double ddx = city.getLongitude() - qx;
                double ddy = city.getLatitude() - qy;
                double distSq = ddx * ddx + ddy * ddy;
                if (distSq <= radius * radius) {
                    results.add(city);
                }
            }

            // Recurse into children
            if (node.children[0] != null) {
                for (Quadtree child : node.children) {
                    pointsWithinRadiusHelper(child, qx, qy, radius, results);
                }
            }
        }
    }
}
