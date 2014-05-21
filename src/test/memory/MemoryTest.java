package test.memory;

import static internal.Helper.*;
import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import test.MemUnit;
import test.MemUsageMonitor;
import test.TimeKeeper;

public class MemoryTest {
	private IMap map;
	private WorldTreeFactory factory = null;
	
	@BeforeClass
	public static void setUp() {
		try {
			PieceFactory.initialize(pieceStrings);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void sizeTest() {
		Thread memUsage = new MemUsageMonitor(10, true);
		TimeKeeper timeKeeper = new TimeKeeper();
		memUsage.start();
		
		timeKeeper.start();
		factory = new WorldTreeFactory("world.properties", "world.definitions");
		map = factory.newMap("InitTestMap", null);
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
		
		timeKeeper.start();
		write(map);
		timeKeeper.stop();
		System.out.println("Time taken to write map                :" + timeKeeper.toString());
		
		getMemoryUsage();
		
		System.gc();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		getMemoryUsage();
		
		memUsage.interrupt();
	}
	
	private void getMemoryUsage() {
		MemUnit total = new MemUnit(Runtime.getRuntime().totalMemory());
		MemUnit free = new MemUnit(Runtime.getRuntime().freeMemory());
		
		System.out.println("Total Memory  :" + total);
		System.out.println("Free Memory   :" + free);
		System.out.println("Used Memory   :" + total.difference(free));
		System.out.println();
	}
}