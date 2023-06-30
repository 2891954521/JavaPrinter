package com.print.handler.impl;

import com.print.Context;
import com.print.config.Messages;
import com.print.entity.QQFile;
import com.print.entity.User;
import com.print.handler.BaseHandler;
import com.print.print.task.PrintStatus;
import com.print.print.task.PrintTask;

public class FileHandler extends BaseHandler{
	
	
	/**
	 * 处理文件消息
	 */
	public void handlerFileMessage(Context context, User sender, QQFile file){
		
		if(file == null) return;
		
		PrintTask task;
		
		if(context.hasPrintTask(sender.getId())){
			// 获取打印任务
			task = context.getPrintTask(sender.getId());
			if(task.status != PrintStatus.WAITING_FILE){
				sender.sendMessage(context, Messages.task_existed);
				return;
			}
		}else{
			// 没有就新建打印任务
			task = new PrintTask(context, sender);
			context.appendPrintTask(task);
		}
		
		// 限制文件大小
		if(file.getSize() > context.config.configFile.Print.maxSize * 1024 * 1024 && sender.getId() != context.config.configFile.QQ.adminQQ){
			sender.sendMessage(context, Messages.too_big_file);
			return;
		}
		
		// 判断文件后缀名
		String[] sp = file.getName().split("\\.");
		
		// 无后缀名
		if(sp.length < 2){
			sender.sendMessage(context, Messages.unsupported_file);
			return;
		}
		
		// 白名单模式匹配文件后缀名
//		boolean allow = false;
		String suffix = sp[sp.length - 1];
//		for(String s : context.config.configFile.Print.allowFile){
//			if(s.equals(suffix)){
//				allow = true;
//				break;
//			}
//		}
//
//		if(!allow){
//			sender.sendMessage(context, Messages.unsupported_file);
//			return;
//		}
		
		task.sendMessage(Messages.receiving_file);
		
		task.setFile(file.getUrl(), sp[0], suffix);
	}
	
}
