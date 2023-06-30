package com.print.print;

import com.print.Context;
import com.print.config.Command;
import com.print.config.Messages;
import com.print.print.task.PrintParam;
import com.print.print.task.PrintStatus;
import com.print.print.task.PrintTask;
import com.print.utils.CommandUtil;
import com.print.utils.Logger;
import com.print.utils.WebUtil;
import org.jetbrains.annotations.NotNull;

import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.Paper;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 打印任务处理类
 */
public class PrintManager{
	
	private static volatile PrintManager INSTANCE;
	
	
	public final Paper paper;
	
	private final Context context;
	
	
	/**
	 * 是否正在打印
	 */
	private boolean isPrinting;
	
	/**
	 * 双面打印确认
	 */
	private boolean isConfirm;
	
	/**
	 * 等待双面打印翻面
	 */
	private int wait;
	
	/**
	 * 实际负责打印的线程
	 */
	private PrintThread thread;
	
	/**
	 * 当前的打印任务
	 */
	private PrintTask currentTask;
	
	/**
	 * 打印队列
	 */
	private Queue<PrintTask> queue;
	
	private HashMap<String, PrintService> printServiceHashMap;
	
	
	public static PrintManager getInstance(Context context){
		if(INSTANCE == null){
			synchronized(PrintManager.class){
				if(INSTANCE == null){
					INSTANCE = new PrintManager(context);
				}
			}
		}
		return INSTANCE;
	}
	
	
	private PrintManager(Context context){
		this.context = context;
		
		thread = new PrintThread();

		initPrintService();
		
		final int width = 595; //(int)(210 * 72 * 10 / 254.0);
		final int height = 842; //(int)(297 * 72 * 10 / 254.0);
		paper = new Paper();
		paper.setSize(width, height);
		// 边距
		paper.setImageableArea(0, 0, width, height);
	}
	
	private void initPrintService(){
		printServiceHashMap = new HashMap<>(2);
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		for(PrintService s : services){
//			if(s.getName().equalsIgnoreCase("Microsoft Print to PDF")){
//				printServiceHashMap.put("Microsoft Print to PDF", s);
//				break;
//			}
			printServiceHashMap.put(s.getName(), s);
		}
		if(printServiceHashMap.size() == 0){
			Logger.log("无法找到打印机");
		}
	}
	
	
	/**
	 * 追加一个打印任务
	 */
	public void appendPrintTask(@NotNull PrintTask task){
		synchronized(PrintManager.this){
			if(queue == null){
				queue = new LinkedList<>();
			}
			queue.add(task);
			if(!isPrinting){
				try{
					thread = new PrintThread();
					thread.start();
					isPrinting = true;
				}catch(IllegalThreadStateException e){
					Logger.log(e);
				}
			}
		}
	}
	
	/**
	 * 双面打印 确认打印另一面
	 */
	public void continuePrint(@NotNull PrintTask task){
		synchronized(PrintManager.this){
			if(currentTask.equals(task)){
				isConfirm = true;
			}
		}
	}

	public PrintService getPrintService(@NotNull PrintParam.PrintColor color){
		PrintService printService = null;
		switch(color){
			case GRAY:
				printService = printServiceHashMap.get(context.config.configFile.Print.grayscalePrinter);
				break;
			case COLOR:
				printService = printServiceHashMap.get(context.config.configFile.Print.colourPrinter);
				break;
		}
		if(printService == null){
			printService = printServiceHashMap.values().iterator().next();
		}
		return printService;
	}
	

	
	/**
	 * 获取下一个打印任务
	 */
	private PrintTask getNextTask(){
		PrintTask task;
		do{
			task = queue.poll();
			if(task == null) break;
		}while(task.status == PrintStatus.CANCEL || task.status == PrintStatus.FINISH);
		return task;
	}
	
	/**
	 * 检查打印状态
	 * @return 空闲时返还False
	 */
	private boolean checkPrintStatus() throws IOException, InterruptedException{
		// 获取系统打印队列
		String result = CommandUtil.execCommand(Command.GET_SYSTEM_PRINT_QUEUE);
		
		if(result == null) throw new RuntimeException("获取系统打印队列失败!");
		
		// 打印队列不为空，即仍未打印完成
		return result.length() > 0;
	}
	
	
	private class PrintThread extends Thread{
		
		@Override
		public void run(){
			isPrinting = true;
			
			try{
				while(true){
					
					Thread.sleep(5000L);
					
					if(isConfirm){
						// 双面打印等待到用户响应
						isConfirm = false;
						wait = 0;
						
					}else if(wait == 0){
						
						if(checkPrintStatus()) continue;
						
						if(currentTask != null){
							if(currentTask.printCompleted()){
								// 等待双面打印翻面
								wait = 12;
								continue;
							}
						}
						
						synchronized(PrintManager.this){
							// 获取队列中的下个打印任务
							currentTask = getNextTask();
						}
						
					}else if(wait == 1){
						// 双面打印等待超时
						if(currentTask != null){
							currentTask.status = PrintStatus.WAITING_PRINT;
							currentTask.sendMessage(Messages.wait_timeout);
						}
						wait--;
						continue;
					}else{
						wait--;
						continue;
					}
					
					// 打印任务不存在，结束打印循环
					if(currentTask == null){
						break;
					}
					
					currentTask.status = PrintStatus.PRINTING;
					
					currentTask.print();
					
					currentTask.sendMessage("开始打印!");
				}
			}catch(IOException | InterruptedException | RuntimeException | PrintException e){
				if(currentTask != null){
					currentTask.sendMessage("打印失败！\n" + e.getMessage());
				}else{
					Logger.log(e);
				}
			}
			
			isPrinting = false;
		}
	}
	
}
