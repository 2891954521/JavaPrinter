package com.print.handler;

import com.print.Context;
import com.print.config.Messages;
import com.print.entity.User;
import com.print.handler.annotation.RequirePrintTask;
import com.print.print.task.PrintTask;
import org.jetbrains.annotations.NotNull;

/**
 * 用户消息处理类
 */
public abstract class BaseHandler{
	
	public String keyWord;
	
	public String help;
	
	/**
	 * 处理用户发送的文本消息，同时对注解进行处理
	 */
	public void handleMessage(Context context, User sender, String message){
		if(getClass().isAnnotationPresent(RequirePrintTask.class)){
			long uid = sender.getId();
			if(!context.hasPrintTask(uid)){
				sender.sendMessage(context, Messages.waiting_file);
			}else{
				handlerMessage(context, context.getPrintTask(uid), message);
			}
		}else{
			handlerMessage(context, sender, message);
		}
	}
	
	/**
	 * 处理文本消息
	 * @param context 应用上下文
	 * @param sender 发送者
	 * @param message 消息
	 */
	protected void handlerMessage(Context context, User sender, String message){ }
	
	/**
	 * 处理文本消息
	 * @param context 应用上下文
	 * @param printTask 打印任务
	 * @param message 消息
	 */
	protected void handlerMessage(Context context, PrintTask printTask, String message){ }
	
	/**
	 * 是否含有关键词
	 */
	public boolean hasKeyWord(String message){
		return keyWord.equals(message);
	}
	
	/**
	 * 获取帮助文本
	 */
	public String getHelp(){
		return help;
	}
	
	/**
	 * 回复消息
	 * @param user 发送者
	 * @param message 回复的消息
	 */
	protected final void sendMessage(Context context, @NotNull User user, String message){
		user.sendMessage(context, message);
	}
}
