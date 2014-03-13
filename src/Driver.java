import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.ParseException;

import static internal.Helper.*;
import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;
import test.TimeKeeper;

public class Driver {

	public static void main(String[] args) throws Exception {

    /***** DEFINE COMMAND LINE OPTIONS *****/
    Options options = new Options();
    options.addOption("o", true, 
      "Specify the output file (stdout by default)");
    options.addOption("config", true,
      "Specify the config file ('<worldDef>.properties' by default)");
    options.addOption("map", false,
      "Dump a graphical representation of the map");
    options.addOption("properties", false,
      "Dump a the node properties (default)");
    
    /***** PARSE COMMAND LINE *****/
    CommandLineParser parser = new BasicParser();
    CommandLine argv = null;
    try { argv = parser.parse(options, args); }
    catch(ParseException e) { 
      System.err.println(e.getMessage()); 
      System.exit(-1);
    }

    /***** INTERPRET COMMAND LINE *****/
    String outFile = "-";
		String worldDefFile = "world.definitions";    
		String propertiesFile = null;
    if(argv.hasOption("o"))
      { outFile= argv.getOptionValue("o"); }
    if(argv.getArgs().length > 0)
      { worldDefFile = argv.getArgs()[0]; }
    if(argv.hasOption("config")) 
      { propertiesFile = argv.getOptionValue("config"); }
    else
      { propertiesFile = worldDefFile.replace(".definitions", "")+".properties"; }
    
    System.out.println(propertiesFile);
    
    /***** CREATE THE WORLD *****/
    PieceFactory.initialize(pieceStrings);
		TimeKeeper timeKeeper = new TimeKeeper();
		WorldTreeFactory factory = new WorldTreeFactory(propertiesFile, worldDefFile);

		timeKeeper.start();
		IMap map = factory.newMap("InitTestMap", null);
		map.initRooms();
		map.initRegions();
		map.initTiles();
		
		timeKeeper.stop();
		System.out.println("Time taken to create skeleton          :" + timeKeeper.toString());
		
		timeKeeper.start();
		map.fill();
		timeKeeper.stop();
		System.out.println("Time taken to fill entire map          :" + timeKeeper.toString());
		
		
		timeKeeper.start();
		map.materializeConstraints();
		timeKeeper.stop();
		System.out.println("Time taken to materialize constraints  :" + timeKeeper.toString());
		
    /***** OUTPUT *****/
		boolean propertiesByDefault = true;
		if(argv.hasOption("map")){
      write(map, outFile);
      propertiesByDefault = false;
    }
    if(propertiesByDefault || argv.hasOption("properties")){
      writeProperties(map, outFile);
    }
	}
}
