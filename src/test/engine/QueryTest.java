package test.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static internal.Helper.write;

import internal.parser.ParseException;
import internal.parser.Parser;
import internal.parser.containers.query.IQuery;
import internal.parser.resolve.ResolutionEngine;
import internal.parser.resolve.Result;
import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import static internal.Helper.*;

public class QueryTest {
	private IMap map;
	private WorldTreeFactory factory = null;
	private Parser parser			 = new Parser(new StringReader(""));
	private String queryTestsDir	 = "tests/query tests/";
	
	@BeforeClass
	public static void setUp() {
		try {
			PieceFactory.initialize(pieceStrings);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void test() {
//		Direction tests
		File dir = new File(queryTestsDir);
		
		StringBuffer errorMessage = new StringBuffer();
		for(File testDir : dir.listFiles()) {
			boolean failed = false;
			Result result = null, storedResult = null;
			
			errorMessage.delete(0, errorMessage.length());
			
			File testFile = new File(testDir + "/init.properties");
			if(!testFile.exists()) {
				errorMessage.append("\t" + testDir.getName() + " :init.properties file does not exist!\n");
				failed = true;
			}
			
			if(!failed) {
				factory = new WorldTreeFactory(testDir.getAbsolutePath() + "/init.properties");
				map = factory.newMap("InbuiltPropertiesTestMap", null);
				map.fullInit();
				write(map);
				try {
					testFile = new File(testDir + "/test");
					if(!testFile.exists()) {
						errorMessage.append("\t" + testDir.getName() + " :test file does not exist!\n");
						failed = true;
					}
					
					if(!failed) {
						BufferedReader in = new BufferedReader(new FileReader(testFile));
						String line = null;
						
						while( (line = in.readLine()) != null) {
							if(line.trim().startsWith("#") || line.length() < 1)
								continue;
							parser.ReInit(new StringReader(line));
							IQuery query = parser.query(null);
							result = ResolutionEngine.evaluate(map, query);
						}
					}
					
					testFile = new File(testDir + "/result.obj");
					if(!testFile.exists()) {
						errorMessage.append("\t" + testDir.getName() + " :result.obj file does not exist!\n");
						failed = true;
					}
					if(!failed) {
						ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(testDir + "/result.obj")));
						storedResult = (Result) ois.readObject();
						ois.close();
					}
					
					failed = !result.toString().equals(storedResult.toString());
					
					if(failed) {
						errorMessage.append("\t" + testDir.getName() + " :Results do not match!\n");
						
						testFile = new File(testDir + "/result");
						if(!testFile.exists())
							errorMessage.append("\t" + testDir.getName() + " :result file does not exist!\n");
						else {
							StringBuffer storedResultString = new StringBuffer();
							BufferedReader in = new BufferedReader(new FileReader(testFile));
							
							String line = null;
							while( (line = in.readLine()) != null)
								storedResultString.append(line + "\n");
							in.close();
							
							errorMessage.append(multiLine(Arrays.asList(new String[] {
							"Current Result\n" + result.toString(), "     ", "Stored Result\n" + storedResult.toString()		
							})));
							errorMessage.append("\n");
						}
					}
				} catch (ParseException e) {
					errorMessage.append("\t" + testDir.getName() + " :" + e.getMessage() + "\n");
					failed = true;
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					errorMessage.append("\t" + testDir.getName() + " :" + e.getMessage() + "\n");
					failed = true;
				} catch (IOException e) {
					errorMessage.append("\t" + testDir.getName() + " :" + e.getMessage() + "\n");
					failed = true;
				} catch (ClassNotFoundException e) {
					errorMessage.append("\t" + testDir.getName() + " :" + e.getMessage() + "\n");
					failed = true;
					e.printStackTrace();
				}
			}
			if(failed) {
				System.err.println(testDir.getName() + " :Failed!");
				System.err.println(errorMessage.toString());
			}
			else
				System.out.println(testDir.getName() + " :Passed!");
		}
	}
	
	@Test
	public void createNewQueryTest() {
		String testDir = null;
		factory = new WorldTreeFactory("init.properties");
		Properties properties = factory.properties();
		System.out.println("Query :");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			StringBuffer cmd = new StringBuffer();
			while(!cmd.toString().contains(";")) {
				cmd.append(in.readLine());
				cmd.append("\n");
			}
			System.out.println("\nComments (End with ';') :");
			
//			Comments for this test
			StringBuffer comments = new StringBuffer();
			while(!comments.toString().contains(";")) {
				comments.append("#");
				comments.append(in.readLine());
				comments.append("\n");
			}
			
//			Get new test directory
			testDir 	= getNewTest();
			File testFile	= new File(testDir + "/test");

//			Store the properties for this test
			File propertiesFile = new File(testDir + "/init.properties");
			properties.store(new FileOutputStream(propertiesFile), null);
			
//			Get the results to test for
			map = factory.newMap(testFile.getParentFile().getName(), null);
			map.fullInit();
			write(map);
			
			parser.ReInit(new StringReader(cmd.toString()));
			IQuery query 	= parser.query(null);
			Result result 	= ResolutionEngine.evaluate(map, query);
			
//			Now store this test
			BufferedWriter out = new BufferedWriter(new FileWriter(testFile));
			out.write(comments.toString());
			out.newLine();
			out.write(query.toString());
			out.newLine();
			out.close();
			
//			Store the result
			out = new BufferedWriter(new FileWriter(new File(testDir + "/result")));
			out.write(result.toString());
			out.newLine();
			
			out.close();
			
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(testDir + "/result.obj")));
			oos.writeObject(result);
			oos.close();
			System.out.println("Test " + testFile.getParentFile().getName() + " created!");
		} catch(IOException e) {
			File file = new File(testDir);
			if(file.exists()) {
				for(File f : file.listFiles())
					f.delete();
				file.delete();
			}
			
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			File file = new File(testDir);
			if(file.exists()) {
				for(File f : file.listFiles())
					f.delete();
				file.delete();
			}
			
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}


	private String getNewTest() {
		File returnTestDir = null;
		
		File dir = new File(queryTestsDir);
		
		String testPrefix 	= "test";
		int validTestID 		= 0;

//		Find a testID to use
		List<String> tests	= Arrays.asList(dir.list());
		while(validTestID >= 0) {
			if(!tests.contains(testPrefix + String.format("%03d", validTestID)))
				break;
			validTestID++;
		}
		
		returnTestDir = new File(queryTestsDir + testPrefix + String.format("%03d", validTestID));
		if(returnTestDir.exists())
			throw new IllegalStateException("Error in getNewFile code! Should have picked a directory that does not exist!");
		returnTestDir.mkdir();
		return returnTestDir.getAbsolutePath();
	}
}
