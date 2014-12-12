package test.sample;

import static org.junit.Assert.*;
import internal.Helper;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import driver.Driver;

public class SampleTest {
	public static List<File> reachabilityDefinitionsList = new ArrayList<File>();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		File path = new File("sample/test");
		for(File f : path.listFiles()) {
			if(f.getName().matches("reachability\\d+\\.definitions")) {
				reachabilityDefinitionsList.add(f);
			}
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testAllSamples() {
		Map<File, Boolean> results = new HashMap<File, Boolean>();
		Map<File, String> logs = new HashMap<File, String>();
		
		for(File f : reachabilityDefinitionsList) {
			results.put(f, true);
			logs.put(f, null);
		}
		File propertiesFile = new File("sample/test/reachability.properties");
		for(File definitionsFile : reachabilityDefinitionsList) {
			try {
				System.out.println("Testing :" + definitionsFile.getName());
				runDriver(propertiesFile, definitionsFile);
				System.out.println();
			} catch(Exception e) {
				results.put(definitionsFile, false);
				StringBuilder sb = new StringBuilder();
				StringWriter sw  = new StringWriter();
				PrintWriter pw   = new PrintWriter(sw);
				e.printStackTrace(pw);
				pw.flush();
				
				sb.append(e.getMessage() + "\n");
				sb.append(sw.toString() + "\n");
				logs.put(definitionsFile, sb.toString());
				continue;
			}
		}
		for(File f : reachabilityDefinitionsList) {
			if(results.get(f) == false) {
				System.err.println(f.getName() + "        " + results.get(f));
				System.err.println(Helper.indent(logs.get(f).toString(), 8));
			}
			else
				System.out.println(f.getName() + "        " + results.get(f));
		}
	}
	
	public void runDriver(File propertiesFile, File definitionsFile) throws Exception {
		String[] args = new String[] {
				"test", 
				"-d", definitionsFile.getAbsolutePath(), 
				"-c", propertiesFile.getAbsolutePath(), 
				"-dumpProperties",
				"map"};
		Driver.main(args);
	}
	@Test
	public void testSingleSample() throws Exception {
		File propertiesFile = new File("sample/test/reachability.properties");
		File definitionsFile = new File("sample/test/reachability6.definitions");
		
		runDriver(propertiesFile, definitionsFile);
	}
}
