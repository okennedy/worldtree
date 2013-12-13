package test.performance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import internal.Helper;
import internal.parser.containers.Constraint;
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
			
			for(int areaPerRegion = dimensionalLimit; areaPerRegion >= 1000; areaPerRegion -= 1000) {
				int regionCount = (int) Math.ceil((double) (dimensionalLimit / (float) areaPerRegion));
				properties.put("Region.count", Integer.toString(regionCount));
				
				int remainingArea	= dimensionalLimit;
				for(int regionIndex = 0; regionIndex < regionCount; regionIndex++) {
					if(remainingArea >= areaPerRegion) {
						properties.put("Region" + regionIndex + ".size", "" + areaPerRegion + "x" + 1);
						remainingArea -= areaPerRegion;
					}
					else
						properties.put("Region" + regionIndex + ".size", "" + remainingArea + "x" + 1);
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
					
			timingStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/timing_data")), "Area - Time Taken");
			memoryStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/memory_data")), "Area - Memory Footprint");
			maxMemStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/max_mem_data")), "Area - Max Memory Footprint");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void IncreasingAreaWriteTest() {
		int dimensionalLimit = 100000;
		File outputDir = setupOutputDir("IncreasingAreaWriteTest/limit_" + dimensionalLimit);
		File propertiesFile	= new File(outputDir.getAbsolutePath() + "/IncreasingAreaTest.properties");
		try {
			Properties properties 	= new Properties();
			properties.put("Room.count", "1");
			properties.put("Region.count", "1");
			
			Statistics timingStats	= new Statistics();
			Statistics memoryStats	= new Statistics();
			Statistics maxMemStats	= new Statistics();
			
			for(int xDim = 1000; xDim <= dimensionalLimit; xDim += 1000) {
				long iterationStartTime	= System.nanoTime();
				System.out.println("Iteration    :" + xDim);
				int area = xDim * 1;
				if(timingStats.containsKey(area))
					continue;
				
				MemUsageMonitor memUsageMonitor = new MemUsageMonitor(1, true);
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
	
	@Test
	public void singleConstraintIncreasingAreaTest() {
		int dimensionalLimit = 200000;
		File outputDir 			= setupOutputDir("singleConstraintIncreasingAreaTest/limit_" + dimensionalLimit);
		File definitionsFile	= new File("src/test/performance/singleConstraintIncreasingAreaTest.definitions");
		assert definitionsFile.exists() : "Definitions file does not exist for singleConstraintIncreasingAreaTest\n";
		File propertiesFile	= new File(outputDir.getAbsolutePath() + "/IncreasingAreaTest.properties");
		try {
			Properties properties 	= new Properties();
			properties.put("Room.count", "1");
			properties.put("Region.count", "1");
			
			Statistics timingStats	= new Statistics();
			Statistics memoryStats	= new Statistics();
			Statistics maxMemStats	= new Statistics();
			
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
				
				WorldTreeFactory factory = new WorldTreeFactory(propertiesFile.getAbsolutePath(), definitionsFile.getAbsolutePath());
				
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
					
			timingStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/timing_data")), "Area - Time Taken");
			memoryStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/memory_data")), "Area - Memory Footprint");
			maxMemStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/max_mem_data")), "Area - Max Memory Footprint");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void multipleConstraintIncreasingAreaTest() {
		int dimensionalLimit = 1000000;
		File outputDir 			= setupOutputDir("multipleConstraintIncreasingAreaTest/limit_" + dimensionalLimit);
		File definitionsFile	= new File("src/test/performance/multipleConstraintIncreasingAreaTest.definitions");
		assert definitionsFile.exists() : "Definitions file does not exist for multipleConstraintIncreasingAreaTest\n";
		File propertiesFile	= new File(outputDir.getAbsolutePath() + "/IncreasingAreaTest.properties");
		try {
			Properties properties 	= new Properties();
			properties.put("Room.count", "1");
			properties.put("Region.count", "1");
			
			
			for(int constraintCount = 0; constraintCount < 5; constraintCount++) {
				Statistics timingStats	= new Statistics();
				Statistics memoryStats	= new Statistics();
				Statistics maxMemStats	= new Statistics();
				
				for(int xDim = 1000; xDim <= dimensionalLimit; xDim += 1000) {
					long iterationStartTime	= System.nanoTime();
					System.out.println("Iteration    :" + xDim);
					
					int area = xDim * 1;
					
					MemUsageMonitor memUsageMonitor = new MemUsageMonitor(10, true);
					memUsageMonitor.start();
					
					propertiesFile.delete();
					FileWriter writer = new FileWriter(propertiesFile);
					for(int regionIndex = 0; regionIndex < 1; regionIndex++)
						properties.put("Region" + regionIndex + ".size", "" + xDim + "x" + 1);
					properties.store(writer, "Auto-generated properties");
					
					WorldTreeFactory factory = new WorldTreeFactory(propertiesFile.getAbsolutePath(), definitionsFile.getAbsolutePath());
					List<Constraint> constraints = new ArrayList<Constraint>(factory.constraints());
					for(Constraint constraint : constraints) {
						int index = constraints.indexOf(constraint);
						if(index > constraintCount)
							factory.constraints().remove(constraint);
					}
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
						
				timingStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/timing_data_" + constraintCount)), "Area - Time Taken");
				memoryStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/memory_data_" + constraintCount)), "Area - Memory Footprint");
				maxMemStats.store(new FileWriter(new File(outputDir.getAbsolutePath() + "/max_mem_data_" + constraintCount)), "Area - Max Memory Footprint");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void increasingRangesTest() {
		int dimensionalLimit = 100000;
		int divisionLimit	 = 50;
		File outputDir 			= setupOutputDir("increasingRangesTest/limit_" + dimensionalLimit);
		File definitionsFile	= new File("src/test/performance/increasingRangesTest.definitions");
		assert definitionsFile.exists() : "Definitions file does not exist for increasingRangesTest\n";
		File propertiesFile	= new File(outputDir.getAbsolutePath() + "/IncreasingRangesTest.properties");
		try {
			int yDim = dimensionalLimit / (4 * 1000);
			Properties properties 	= new Properties();
			properties.put("Room.count", "2");
			properties.put("Region.count", "2");
			properties.put("Region0.size", "1000" + "x" + yDim);
			properties.put("Region1.size", "1000" + "x" + yDim);
			Statistics timingStats	= new Statistics();
			Statistics memoryStats	= new Statistics();
			Statistics maxMemStats	= new Statistics();
			
			properties.store(new FileWriter(propertiesFile), "");
			
			for(int divisions = 0; divisions <= divisionLimit; divisions++) {
				long iterationStartTime	= System.nanoTime();
				System.out.println("Iteration    :" + divisions);
				
				MemUsageMonitor memUsageMonitor = new MemUsageMonitor(10, true);
				memUsageMonitor.start();

				File currentDefinitionsFile	= new File(outputDir.getAbsolutePath() + "/" + divisions + ".definitions");
				Helper.fileCopy(definitionsFile, currentDefinitionsFile, false, true);
				BufferedWriter writer		= new BufferedWriter(new FileWriter(currentDefinitionsFile, true));
				
				List<Integer> values = new LinkedList<Integer>();
				for(int i = 0; i < 100; i++)
					values.add(i);
				
				Random rnd	= new Random();
				for(int i = 0; i <= divisions; i++) {
					int index = rnd.nextInt(values.size());
					int value = values.get(index);
					writer.newLine();
					writer.append("FOR ALL TILE T ASSERT T.treasure != " + value);
					values.remove(index);
				}				
				writer.close();
				WorldTreeFactory factory = new WorldTreeFactory(propertiesFile.getAbsolutePath(), currentDefinitionsFile.getAbsolutePath());
				
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
				
				if(!maxMemStats.containsKey(divisions))
					maxMemStats.put(divisions, new ArrayList<Long>());
				maxMemStats.get(divisions).add(memUsageMonitor.getMax().bytes());
				
				if(!timingStats.containsKey(divisions))
					timingStats.put(divisions, new ArrayList<Long>());
				timingStats.get(divisions).add(endTime - startTime);

				MemUnit usage	= MemUnit.getUsedMemory();
				if(!memoryStats.containsKey(divisions))
					memoryStats.put(divisions, new ArrayList<Long>());
				memoryStats.get(divisions).add(usage.bytes());
				
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
