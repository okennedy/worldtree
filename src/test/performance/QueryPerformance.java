package test.performance;

import static org.junit.Assert.*;

import internal.Helper;
import internal.parser.Parser;
import internal.parser.containers.IStatement;
import internal.parser.resolve.query.QueryResolutionEngine;
import internal.piece.PieceFactory;
import internal.tree.WorldTreeFactory;
import internal.tree.IWorldTree.IMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.MemUnit;
import test.MemUsageMonitor;

public class QueryPerformance {
	private static File outputDir = new File("output/PerformanceTests/QueryPerformance");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PieceFactory.initialize(Helper.pieceStrings);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void simpleQueryIncreasingAreaTest() {
		int dimensionalLimit = 10000;
		File outputDir = setupOutputDir("simpleQueryIncreasingAreaTest/limit_" + dimensionalLimit);
		File propertiesFile	= new File(outputDir.getAbsolutePath() + "/IncreasingAreaTest.properties");
		try {
			Properties properties 	= new Properties();
			properties.put("Room.count", "1");
			properties.put("Region.count", "1");
			
			Statistics timingStats	= new Statistics();
			Statistics memoryStats	= new Statistics();
			Statistics maxMemStats	= new Statistics();
			
			for(int xDim = 10; xDim <= dimensionalLimit; xDim += 10) {
				long iterationStartTime	= System.nanoTime();
				System.out.println("Iteration    :" + xDim);
				
				int area = xDim * 100;
				
				MemUsageMonitor memUsageMonitor = new MemUsageMonitor(10, true);
				memUsageMonitor.start();
				
				propertiesFile.delete();
				FileWriter writer = new FileWriter(propertiesFile);
				properties.put("Region0.size", "" + xDim + "x" + 10);
				properties.store(writer, "Auto-generated properties");
				
				WorldTreeFactory factory = new WorldTreeFactory(propertiesFile.getAbsolutePath());
				
				IMap map = factory.newMap("testMap", null);
				map.initRooms();
				map.initRegions();
				map.initTiles();
				map.materializeConstraints();
				map.fill();
				
				long startTime 	= System.nanoTime();
				try {
					String query = "Tile A toeast B;";
					Parser parser = new Parser(new StringReader(query));
					IStatement statement	= parser.parse();
					QueryResolutionEngine.evaluate(map, statement);
				} catch(Exception e) {
					e.printStackTrace();
					fail("Failed!");
				}
				long endTime	= System.nanoTime();
				System.out.println("query: Time Taken  :" + String.format("%.4f", ((endTime - startTime) / 1e9)) + "seconds");
				System.gc();
				Thread.sleep(5);
				
				memUsageMonitor.interrupt();
				memUsageMonitor.join();
				
				if(!maxMemStats.containsKey(area))
					maxMemStats.put(area, new ArrayList<Long>());
				maxMemStats.get(area).add(memUsageMonitor.getMax().bytes());
				
				if(!timingStats.containsKey(area))
					timingStats.put(area, new ArrayList<Long>());
				timingStats.get(area).add(endTime - startTime);

				MemUnit usage	= MemUnit.getUsedMemory();
				if(!memoryStats.containsKey(area))
					memoryStats.put(area, new ArrayList<Long>());
				memoryStats.get(area).add(usage.bytes());
				
				factory = null;
				map		= null;
				long iterationEndTime	= System.nanoTime();
				System.out.println("Time Taken   :" + String.format("%.4f", (iterationEndTime - iterationStartTime) / 1e9) + "seconds\n");
			}
					
			timingStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/timing_data")), "Area - Time Taken");
			memoryStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/memory_data")), "Area - Memory Footprint");
			maxMemStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/max_mem_data")), "Area - Max Memory Footprint");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Set up the output directory for a test. <br>
	 * The final path is always the concatenation of the default {@code outputDir.getAbsolutePath} and <b>path</b>
	 * @param path {@code String} to append
	 * @return 
	 */
	private File setupOutputDir(String path) {
		String doubleSeparator = File.separator + File.separator;
		if(!(path.indexOf(0) == File.separatorChar))
			path = File.separator + path;
		path = outputDir.getAbsolutePath() + path;
		if(path.contains(doubleSeparator))
			path = path.replaceAll(doubleSeparator, File.separator);
		File outputDir = new File(path);
		if(outputDir.exists()) {
			System.out.println("Warning: " + outputDir.getAbsolutePath() + "\nAlready Exists!");
			System.exit(-1);
		}
		outputDir.mkdirs();
		return outputDir;
	}
}
