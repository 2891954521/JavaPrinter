package com.print;

import com.print.config.Config;
import com.print.convert.ConverterManager;
import com.print.handler.MessageHandler;
import com.print.print.PrintManager;
import com.print.print.task.PrintTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * 应用程序上下文
 */
public class Context{
	
	/**
	 * 应用配置信息
	 */
	public Config config;
	
	public MessageHandler messageHandler;
	
	public ConverterManager converterManager;
	
	/**
	 * 全部打印任务
	 */
	private final HashMap<Long, PrintTask> tasks;
	
	public Context(){
		config = new Config();

		messageHandler = new MessageHandler(this);
		
		converterManager = new ConverterManager(this);
		
		tasks = new HashMap<>(4);
	}
	
	
	public boolean hasPrintTask(long uid){
		return tasks.containsKey(uid);
	}
	
	public PrintTask getPrintTask(long uid){
		return tasks.get(uid);
	}
	
	public void appendPrintTask(PrintTask task){
		tasks.put(task.sender.getId(), task);
	}
	
	public void removePrintTask(@NotNull PrintTask task){
		tasks.remove(task.sender.getId());
	}
	
	public void startPrintTask(PrintTask task){
		PrintManager.getInstance(this).appendPrintTask(task);
	}
	
	public void continuePrintTask(PrintTask task){
		PrintManager.getInstance(this).continuePrint(task);
	}
}
