package com.print.utils;

import org.jetbrains.annotations.NotNull;

public class Logger{
	
	private static Logger logger;
	
	public static void setLogger(Logger logger){
		Logger.logger = logger;
	}
	
	public static void log(String message){
		System.out.println(message);
	}
	
	public static void log(@NotNull Throwable throwable){
		throwable.printStackTrace();
	}
	
	public static void log(String message, @NotNull Throwable throwable){
		System.out.println(message);
		throwable.printStackTrace();
	}
}
