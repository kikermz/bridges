//Starter code for BRIDGES projects (https://bridgesuncc.github.io)
//Created by James Vanderhyde, 13 February 2025

package bridgesbase;

import bridges.base.SymbolCollection;
import bridges.connect.Bridges;
import bridges.connect.DataSource;

public class BridgesApp 
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
        
        
        //Read the data set into a data structure
        
        
        //Process the data
        
        
        //Create a scene graph (SymbolCollection) object
        SymbolCollection scene = new SymbolCollection();
        bridges.setDataStructure(scene);
        
        //Set the window (the visible range of X and Y)
        //  These numbers need to be set to something that works for the data set.
        scene.setViewport(-100.0f, 100.0f, -100.0f, 100.0f);
        
        //Add shapes to the SymbolCollection for the server to draw

        
    }

}
