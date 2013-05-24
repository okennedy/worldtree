package test.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import test.commands.Command;

import internal.tree.IWorldTree;
import internal.tree.IWorldTree.IMap;
import internal.tree.IWorldTree.IRegion;

public class TestEngine {
	private static IWorldTree map = null;
	
	public static void init(IMap map) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(System.in));
			String command;
			TestEngine.map = map;
			map.initialize();	//Map initialized
			map.initialize();	//Rooms initialized
			IRegion child = (IRegion)((IWorldTree) map.children().toArray()[0]).children().toArray()[0];	//Region0
			while(true) {
				command = in.readLine();
				parse(command, child);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private static Command parse(String command, IRegion object) {
		Command cmd = Command.parse(command);
		if(cmd != null){
      switch(cmd) {
      case UP:
      case DOWN:
      case LEFT:
      case RIGHT:
        object.move(cmd);
        write();
      case INSPECT:
        break;
      
      case QUERY:
        break;
      
      case WRITE:
        break;
      default:
        break;
      }
    } else {
      System.err.println("Invalid command: '" + command + "'");
    }
		return null;
	}
	
	
	public static void write() {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(new File("output/output.txt")));
			out.write(map.toString());
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
