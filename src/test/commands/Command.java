package test.commands;

public enum Command {
	QUERY("QUERY"),
	INSPECT("INSPECT"),
	WRITE("WRITE"),
	UP("U"),
	DOWN("D"),
	LEFT("L"),
	RIGHT("R"),
	;
	
	private String command;
	private Command(String command) {
		this.command = command;
	}
	
	
	public static Command parse(String command) {
		command = command.toUpperCase();	//Sanity check
		for(Command value : values()) {
			if(value.command.matches(command + ".*"))
				return value;
		}
		return null;
	}
}
