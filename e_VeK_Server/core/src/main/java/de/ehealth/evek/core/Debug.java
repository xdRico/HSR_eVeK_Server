package de.ehealth.evek.core;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Debug {

	@SuppressWarnings("rawtypes")
	public static void error(Class throwing, String info) {
		out(System.err, throwing.getName(), info);

	}
	
	@SuppressWarnings("rawtypes")
	public static void error(Class throwing, Throwable e) {
		out(System.err, throwing.getName(), e.getMessage());

	}
	
	public static void error(String throwing, String info) {
		out(System.err, throwing, info);

	}
	
	public static void error(String throwing, Throwable e) {
		out(System.err, throwing, e.getMessage());

	}
	
	@SuppressWarnings("rawtypes")
	public static void out(Class throwing, String message) {
		out(System.out, throwing.getName(), message);
	}
	
	public static void out(String throwing, String message) {
		out(System.out, throwing, message);
	}
	
	
	private static void out(PrintStream stream, String throwing, String message) {
		stream.print(String.format("[%s][%s] %s", 
				LocalDate.parse((new Date()).toString(), DateTimeFormatter.ofPattern("dd.MM.yyyy")), 
				throwing, 
				message));
	}
}
