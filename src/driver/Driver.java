package driver;
import org.apache.commons.cli.*;

import static internal.Helper.*;
import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;
import test.TimeKeeper;

public class Driver {

	public static void main(String[] args) throws Exception {

    /***** DEFINE COMMAND LINE OPTIONS *****/
    Options options = new Options();
    options.addOption("?", "help", false, "Display this help text");
    options.addOption("o", true, 
      "Specify the output file (stdout by default)");
    options.addOption("c", "config", true,
      "Specify the config file ('world.properties' by default)");
    options.addOption("d", "definitions", true,
    	      "Specify the definitions file ('world.definitions' by default)");
    options.addOption("map", false,
      "Dump a graphical representation of the map");
    options.addOption("dumpProperties", false,
      "Dump each node's properties (default)");
    
    /***** PARSE COMMAND LINE *****/
    CommandLineParser parser = new BasicParser();
    CommandLine argv = null;
    try { argv = parser.parse(options, args); }
    catch(ParseException e) { 
      System.err.println(e.getMessage()); 
      System.exit(-1);
    }
    if(argv.hasOption("?") || argv.hasOption("help")){
      (new HelpFormatter()).printHelp(
        "bin/wt [options] worldDefFile", 
        options
      );
      System.exit(0);                        
    }

    /***** INTERPRET COMMAND LINE *****/
    String outFile = Logger.STDOUT;
		String worldDefFile = "world.definitions";    
		String propertiesFile = null;
	if(argv.hasOption("d"))
	  { worldDefFile = argv.getOptionValue("d"); }
    if(argv.hasOption("o"))
      { outFile= argv.getOptionValue("o"); }
    if(argv.hasOption("config")) 
      { propertiesFile = argv.getOptionValue("config"); }
    else
      { propertiesFile = worldDefFile.replace(".definitions", "")+".properties"; }
    
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
		Logger.eraseFile(outFile);
		if(argv.hasOption("map")){
      write(map, outFile);
    }
    if(argv.hasOption("dumpProperties")){
      writeProperties(map, outFile);
    }
	}
}
