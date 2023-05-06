package com.print.utils;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandUtil{
	
	/**
	 * 执行命令
	 * @param command 命令
	 * @return 命令输出，发生错误时返还null
	 */
	public static @Nullable String execCommand(String command) throws IOException, InterruptedException{
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		
		if(p.exitValue() != 0) return null;
		
		String line;
		StringBuilder sb = new StringBuilder();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		while((line = reader.readLine()) != null){
			sb.append(line);
		}
		
		reader.close();
		
		return sb.toString();
	}
}
