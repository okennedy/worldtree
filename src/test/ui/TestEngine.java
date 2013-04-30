package test.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import test.commands.Command;

import internal.tree.IWorldTree;
import internal.tree.IWorldTree.IMap;
import internal.tree.IWorldTree.IRegion;

public class TestEngine {

	
	public static void init(IMap map) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(System.in));
			String command;
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
		switch(cmd) {
		case UP:
		case DOWN:
		case LEFT:
		case RIGHT:
			object.move(cmd);
		case INSPECT:
			break;
		
		case QUERY:
			break;
		
		case WRITE:
			break;
		default:
			break;
		}
		return null;
	}
}
