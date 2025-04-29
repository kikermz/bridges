//Starter code for BRIDGES projects (https://bridgesuncc.github.io)
//Created by James Vanderhyde, 13 February 2025

package bridgesbase;

import bridges.base.Circle;
import bridges.base.Polyline;
import bridges.base.SymbolCollection;
import bridges.connect.Bridges;
import bridges.connect.DataSource;
import bridges.data_src_dependent.City;
import edu.sxu.cs.geometry.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BridgesAppUSCities 
{
    /**
     * Runs all the logic for the BRIDGES assignment. Call from main.
     * @param bridges The initialized Bridges object
     * @throws java.io.IOException when there is a problem communicating
     *   with the BRIDGES server.
     */
    public void run(Bridges bridges) throws java.io.IOException
    {
        //Set some information for the BRIDGES object
        bridges.setTitle("Title of the project");
	bridges.setDescription("Long description");

        //Create the data connection object
        DataSource ds = bridges.getDataSource();
        
        //Use the DataSource object to download a data set from the server
         //US Cities data source
        HashMap<String, String> options = new HashMap<>();
        options.put ("min_pop", "500000");
        List<City> cities = ds.getUSCitiesData(options);
        
        //Read the data set into a data structure
         ArrayList<Point> dataPoints = new ArrayList<>();
        for (City c : cities)
            dataPoints.add(new Point(c.getLongitude(), c.getLatitude()));
        
        //Process the data
         System.out.println(dataPoints.size()+" data points.");
        
        //Create a scene graph (SymbolCollection) object
        SymbolCollection scene = new SymbolCollection();
        bridges.setDataStructure(scene);
        
        //Set the window (the visible range of X and Y)
        //  These numbers need to be set to something that works for the data set.
        scene.setViewport(-100.0f, 100.0f, -100.0f, 100.0f);
        
        //Add shapes to the SymbolCollection for the server to draw

        for (Point p : dataPoints)
        {
            Circle c = new Circle((float)p.x, (float)p.y, 1.0f);
            c.setFillColor("red");
            c.setOpacity(0.6f);
            scene.addSymbol(c);
        }
        
         Polyline boundingBox = new Polyline();
        boundingBox.setStrokeColor("blue");
    boundingBox.addPoint(-130, 50);  // Top-left
    boundingBox.addPoint(-60, 50);   // Top-right
    boundingBox.addPoint(-60, 25);  // Bottom-right
    boundingBox.addPoint(-130, 25); // Bottom-left
     boundingBox.addPoint(-130, 50); 
        scene.addSymbol(boundingBox);
    }

}
