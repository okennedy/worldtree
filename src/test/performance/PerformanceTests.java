package test.performance;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import internal.Helper;
import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.MemUsageMonitor;
import test.memory.MemUnit;

public class PerformanceTests {
	private static WorldTreeFactory factory;
	private static StringBuffer result;
	private static File outputDir = new File("output/PerformanceTests");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if(outputDir.exists())
			outputDir.delete();
		outputDir.mkdirs();
		
		PieceFactory.initialize(Helper.pieceStrings);
		result = new StringBuffer();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		factory = null;
		result	= null;
	}

	/**
	 * In this test, we have just 1 Room and 1 Region but keep increasing the number of Tiles.
	 * 
	 */
	@Test
	public void increasingAreaTest() {
		
		int dimensionalLimit = 1000;
		
		File propertiesFile	= new File(outputDir.getAbsolutePath() + "/IncreasingAreaTest.properties");
		try {
			Properties properties 	= new Properties();
			properties.put("Room.count", "1");
			properties.put("Region.count", "1");
			
			Statistics timingStats	= new Statistics();
			Statistics memoryStats	= new Statistics();
			Statistics maxMemStats	= new Statistics();
			
			Properties timeProperty 	= new Properties();
			Properties memoryProperty	= new Properties();
			Properties maxMemProperty	= new Properties();
			
			for(int xDim = dimensionalLimit; xDim >= 1; xDim--) {
				long iterationStartTime	= System.nanoTime();
				System.out.println("Iteration    :" + xDim);
				for(int yDim = dimensionalLimit; yDim >= 1; yDim--) {
					int area = xDim * yDim;
					if(timingStats.containsKey(area))
						continue;
					
					MemUsageMonitor memUsageMonitor = new MemUsageMonitor(10, true);
					memUsageMonitor.start();
					
					propertiesFile.delete();
					FileWriter writer = new FileWriter(propertiesFile);
					properties.put("Region0.size", "" + xDim + "x" + yDim);
					properties.store(writer, "Auto-generated properties");
					
					WorldTreeFactory factory = new WorldTreeFactory(propertiesFile.getAbsolutePath());
					
					long startTime 	= System.nanoTime();
					
					IMap map = factory.newMap("testMap", null);
					map.initRooms();
					map.initRegions();
					map.initTiles();
					map.materializeConstraints();
					map.fill();
					
					long endTime	= System.nanoTime();
					
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
				}
				long iterationEndTime	= System.nanoTime();
				System.out.println("Time Taken   :" + String.format("%.4f", (iterationEndTime - iterationStartTime) / 1e9) + "seconds\n");
			}
					
			storeAverages(timingStats, timeProperty);
			storeAverages(memoryStats, memoryProperty);
			storeAverages(maxMemStats, maxMemProperty);
			
			timeProperty.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/timing_data")), "Area - Time Taken");
			memoryProperty.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/memory_data")), "Area - Memory Footprint");
			maxMemProperty.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/memory_data")), "Area - Max Memory Footprint");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void storeAverages(Statistics stats, Properties property) {
		for(Map.Entry<Integer, List<Long>> entry : stats.entrySet()) {
			long average = 0;
			
			List<Long> list = entry.getValue();
			for(long value : list)
				average += value;
			average /= list.size();
			
			property.put(entry.getKey(), average);
		}
	}

	private class Statistics extends HashMap<Integer, List<Long>> {
		
		public Statistics() {
			super();
		}
		
		@Override
		public synchronized List<Long> put(Integer integer, List<Long> list) {
			return super.put(integer, list);
		}
		
		@Override
		public synchronized List<Long> get(Object key) {
			return super.get(key);
		}
		
		@Override
		public synchronized boolean containsKey(Object key) {
			return super.containsKey(key);
		}
	}
}
