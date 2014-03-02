package consoleUserInterface;

import commandParser.ParserAndExecuterEngine;

public class ConsoleUI {
	public static void main(String[] args) {
		new Thread(new ParserAndExecuterEngine(System.in, System.out, false))
				.start();
	}
}
