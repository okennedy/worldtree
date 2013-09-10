package test.performance;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import internal.Helper;
import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import test.MemUsageMonitor;
import test.memory.MemUnit;

import static internal.Helper.*;

public class PerformanceTests {
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
		result	= null;
	}

	/**
	 * In this test, we have just 1 Room and 1 Region but keep increasing the number of Tiles. <br>
	 * There are also no constraints to materialize. Thus, IMap.materializeConstraints is never called<br>
	 * This test measures the following: <br> 
	 * <pre>
	 *     - Time taken to materialize instances
	 *     - Memory used by each materialized rinstance
	 *     - Maximum memory requirement during materialization
	 * </pre>
	 */
	@Test
	public void increasingAreaTest() {
		
		int dimensionalLimit = 1000000;
		File outputDir = setupOutputDir("IncreasingAreaTest/limit_" + dimensionalLimit);
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
			
			for(int xDim = 1000; xDim <= dimensionalLimit; xDim += 1000) {
				long iterationStartTime	= System.nanoTime();
				System.out.println("Iteration    :" + xDim);
				
				int area = xDim * 1;
				
				MemUsageMonitor memUsageMonitor = new MemUsageMonitor(10, true);
				memUsageMonitor.start();
				
				propertiesFile.delete();
				FileWriter writer = new FileWriter(propertiesFile);
				properties.put("Region0.size", "" + xDim + "x" + 1);
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
				long iterationEndTime	= System.nanoTime();
				System.out.println("Time Taken   :" + String.format("%.4f", (iterationEndTime - iterationStartTime) / 1e9) + "seconds\n");
			}
					
			storeAverages(timingStats, timeProperty);
			storeAverages(memoryStats, memoryProperty);
			storeAverages(maxMemStats, maxMemProperty);
			
			timeProperty.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/timing_data")), "Area - Time Taken");
			memoryProperty.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/memory_data")), "Area - Memory Footprint");
			maxMemProperty.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/max_mem_data")), "Area - Max Memory Footprint");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This test aims to evaluate performance by increasing the number of Regions required to cover a fixed area. <br>
	 * The area is defined by {@code dimensionalLimit} and the test iterates area-per-region from 1,000 upto {@code dimensionalLimit} <br>
	 * The number of regions is computed as {@code dimensionalLimit / areaPerRegion} <br>
	 */
	@Test
	public void increasingRegionTest() {
		int dimensionalLimit = 1000000;
		File outputDir = setupOutputDir("/increasingRegionTest/limit_" + dimensionalLimit);
		
		File propertiesFile	= new File(outputDir.getAbsolutePath() + "/IncreasingAreaTest.properties");
		try {
			Properties properties 	= new Properties();
			properties.put("Room.count", "1");
			
			Statistics timingStats	= new Statistics();
			Statistics memoryStats	= new Statistics();
			Statistics maxMemStats	= new Statistics();
			
			Properties timeProperty 	= new Properties();
			Properties memoryProperty	= new Properties();
			Properties maxMemProperty	= new Properties();
			
			for(int areaPerRegion = 1000; areaPerRegion <= dimensionalLimit; areaPerRegion += 1000) {
				int regionCount = dimensionalLimit / areaPerRegion;
				properties.put("Region.count", Integer.toString(regionCount));
				for(int regionIndex = 0; regionIndex < regionCount; regionIndex++) {
					properties.put("Region" + regionIndex + ".size", "" + areaPerRegion + "x" + 1);
				}
				
				long iterationStartTime	= System.nanoTime();
				System.out.println("Iteration    :" + areaPerRegion);
				
				MemUsageMonitor memUsageMonitor = new MemUsageMonitor(10, true);
				memUsageMonitor.start();
				
				propertiesFile.delete();
				FileWriter writer = new FileWriter(propertiesFile);
				
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
				
				if(!maxMemStats.containsKey(areaPerRegion))
					maxMemStats.put(areaPerRegion, new ArrayList<Long>());
				maxMemStats.get(areaPerRegion).add(memUsageMonitor.getMax().bytes());
				
				if(!timingStats.containsKey(areaPerRegion))
					timingStats.put(areaPerRegion, new ArrayList<Long>());
				timingStats.get(areaPerRegion).add(endTime - startTime);

				MemUnit usage	= MemUnit.getUsedMemory();
				if(!memoryStats.containsKey(areaPerRegion))
					memoryStats.put(areaPerRegion, new ArrayList<Long>());
				memoryStats.get(areaPerRegion).add(usage.bytes());
				
				factory = null;
				map		= null;
				long iterationEndTime	= System.nanoTime();
				System.out.println("Time Taken   :" + String.format("%.4f", (iterationEndTime - iterationStartTime) / 1e9) + "seconds\n");
			}
					
			storeAverages(timingStats, timeProperty);
			storeAverages(memoryStats, memoryProperty);
			storeAverages(maxMemStats, maxMemProperty);
			
			timeProperty.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/timing_data")), "Area - Time Taken");
			memoryProperty.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/memory_data")), "Area - Memory Footprint");
			maxMemProperty.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/max_mem_data")), "Area - Max Memory Footprint");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void IncreasingAreaWriteTest() {
		int dimensionalLimit = 10000;
		
		File propertiesFile	= new File(outputDir.getAbsolutePath() + "/IncreasingAreaTest.properties");
		try {
			Properties properties 	= new Properties();
			properties.put("Room.count", "1");
			properties.put("Region.count", "1");
			
			Statistics timingStats	= new Statistics();
			Statistics memoryStats	= new Statistics();
			Statistics maxMemStats	= new Statistics();
			
			for(int xDim = 1; xDim <= dimensionalLimit; xDim++) {
				long iterationStartTime	= System.nanoTime();
				System.out.println("Iteration    :" + xDim);
				int area = xDim * 1;
				if(timingStats.containsKey(area))
					continue;
				
				MemUsageMonitor memUsageMonitor = new MemUsageMonitor(10, true);
				memUsageMonitor.start();
				
				propertiesFile.delete();
				FileWriter writer = new FileWriter(propertiesFile);
				properties.put("Region0.size", "" + xDim + "x" + 1);
				properties.store(writer, "Auto-generated properties");
				
				WorldTreeFactory factory = new WorldTreeFactory(propertiesFile.getAbsolutePath());
				
				long startTime 	= System.nanoTime();
				IMap map = factory.newMap("testMap", null);
				map.initRooms();
				map.initRegions();
				map.initTiles();
				map.materializeConstraints();
				map.fill();
				
				
				write(map);
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
				long iterationEndTime	= System.nanoTime();
				System.out.println("Time Taken   :" + String.format("%.4f", (iterationEndTime - iterationStartTime) / 1e9) + "seconds\n");
			}

			timingStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/timing_data")), "Area - Time Taken");
			memoryStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/mem_data")), "Area - Memory Footprint");
			maxMemStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/max_mem_data")), "Area - Max Memory Footprint");
			
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
			
			property.put(Integer.toString(entry.getKey()), Long.toString(average));
		}
	}

	private class Statistics extends TreeMap<Integer, List<Long>> {
		private static final long serialVersionUID = -7576749890787643554L;

		public Statistics() {
			super();
		}
		
		public void store(FileWriter fileWriter, String comments) {
			try {
				BufferedWriter out = new BufferedWriter(fileWriter);
				out.write("#" + comments);
				out.newLine();
				SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
				out.write("#" + sdf.format(Calendar.getInstance().getTime()));
				out.newLine();
				for(Map.Entry<Integer, List<Long>> entry : entrySet()) {
					int key 		= entry.getKey();
					double value 	= 0;
					
					List<Long> list	= entry.getValue();
					for(Long l : list)
						value += l;
					value /= list.size();
					
					out.write(Integer.toString(key) + "=" + Double.toString(value));
					out.newLine();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
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
	
	
	/**
	 * Set up the output directory for a test. <br>
	 * The final path is always the concatenation of the default {@code outputDir.getAbsolutePath} and <b>path</b>
	 * @param path {@code String} to append
	 * @return 
	 */
	private File setupOutputDir(String path) {
		File outputDir = new File(PerformanceTests.outputDir.getAbsolutePath() + path);
		if(outputDir.exists()) {
			System.out.println("Warning: " + outputDir.getAbsolutePath() + "\nAlready Exists!");
			System.exit(-1);
		}
		outputDir.mkdirs();
		return outputDir;
	}
}
