package com.print.print;

import com.print.Context;
import com.print.config.Command;
import com.print.print.task.PrintParam;
import com.print.print.task.PrintStatus;
import com.print.print.task.PrintTask;
import com.print.utils.CommandUtil;
import com.print.utils.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 打印队列
 */
public class PrintQueue{
	
	/**
	 * 是否正在打印
	 */
	private boolean isPrinting;
	
	/**
	 * 定时器，用于实时检查打印进度，处理打印队列
	 */
	private final Timer timer;
	
	
	private final Context context;
	
	/**
	 * 当前的打印任务
	 */
	private PrintTask currentTask;
	
	/**
	 * 打印队列
	 */
	private final Queue<PrintTask> queue;
	
	
	public PrintQueue(Context context){
		this.context = context;
		timer = new Timer();
		queue = new LinkedList<>();
	}

	/**
	 * 追加一个打印任务
	 */
	public void appendPrintTask(@NotNull PrintTask task){
		queue.add(task);
		if(!isPrinting){
			timer.schedule(new Task(), 5000, 5000);
		}
	}
	
	/**
	 * 当双面打印翻面完成时继续打印
	 */
	public void continuePrint(@NotNull PrintTask task){
		if(task.sender.getId() == currentTask.sender.getId()){
			if(!isPrinting){
				timer.schedule(new Task(), 5000, 5000);
			}
		}
	}
	
	/**
	 * 是否正在打印
	 */
	public boolean isPrinting(){
		return isPrinting;
	}
	
	/**
	 * 获取下一个打印任务
	 */
	private void getNextTask(){
		do{
			currentTask = queue.poll();
			if(currentTask == null) break;
		}while(currentTask.status == PrintStatus.CANCEL);
	}
	
	
	public class Task extends TimerTask{
		
		@Override
		public void run(){
			isPrinting = true;
			
			try{
				// 获取系统打印队列
				String result = CommandUtil.execCommand(Command.GET_SYSTEM_PRINT_QUEUE);
				
				if(result == null) throw new RuntimeException("获取系统打印队列失败!");
				
				// 打印队列不为空，即仍未打印完成
				if(!"".equals(result)) return;
					
				if(currentTask != null){
					// 认为打印任务完成，结束任务
					if(currentTask.completePrint()){
						getNextTask();
					}else{
						// 只有双面打印时会执行到
						if(currentTask.status != PrintStatus.WAITING_PRINT_OTHER_SIDE){
							timer.cancel();
							isPrinting = false;
							return;
						}
					}
				}else{
					getNextTask();
				}
				
				// 取不到下个打印任务了，结束打印循环
				if(currentTask == null){
					timer.cancel();
					isPrinting = false;
					return;
				}
				
				result = CommandUtil.execCommand(String.format(Command.PRINT_FILE,
						currentTask.printFile.file,
						currentTask.printParam.color == PrintParam.PrintColor.GRAY ? context.config.configFile.Print.grayscalePrinter : context.config.configFile.Print.colourPrinter,
						currentTask.printParam.count
						// currentTask.type == PrintTask.PrintType.SINGLE ? "" : "-P " + currentTask.getPages(),
				));
				
				// 处理打印状态
				if(currentTask.status == PrintStatus.WAITING_PRINT){
					currentTask.status = PrintStatus.PRINTING;
				}else if(currentTask.status == PrintStatus.WAITING_PRINT_OTHER_SIDE){
					currentTask.status = PrintStatus.PRINTING_OTHER_SIDE;
				}
				
				if(result == null){
					currentTask.sendMessage("打印失败!");
					timer.cancel();
					isPrinting = false;
				}else{
					currentTask.sendMessage("开始打印!");
				}
				
				
			}catch(IOException | InterruptedException | RuntimeException e){
				if(currentTask != null){
					currentTask.sendMessage("打印失败！\n" + e.getMessage());
				}else{
					Logger.log(e);
				}
				timer.cancel();
				isPrinting = false;
			}
		}
		
	}

}