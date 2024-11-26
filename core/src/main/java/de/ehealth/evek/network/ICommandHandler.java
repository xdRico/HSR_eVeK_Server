package de.ehealth.evek.network;

public interface ICommandHandler {

	static boolean processInput(String input) {
		String[] cmds = input.split(";");
		for(String s : cmds) {
			while(s.charAt(0) == ' ')
				s.replaceFirst(" ", "");
		}
		String[] cmd = input.split(" ", 1);
		
		switch (cmd[0].toLowerCase()) {
		case "create": 
			String obj = cmd[1].replace(" ", "").split("[")[0];
			String[] data =  cmd[1].replace(" ", "").split("[")[1].replace("]", "").split(",");
			switch(obj)
			break;
			//TODO
		case "update":
			
			break;
			
		case "delete":
			
			break;
		default:
			break;
		}

		
		return true;
	}
	
	
}
