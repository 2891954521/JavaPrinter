package com.print.config;

import java.util.HashMap;

public class Messages{
	
	public static final String task_existed = "当前有其他打印任务，请等待任务完成或取消当前任务!";
	
	public static final String task_submit = "已提交打印任务, 请稍后";
	
	public static final String task_waiting = "正在等待打印, 请稍后";
	
	public static final String task_printing = "正在打印, 请稍后";
	
	public static final String task_complete = "打印完成";
	
	public static final String task_cancel = "已取消打印任务!";
	
	public static final String task_cancel_fail = "打印任务已开始, 无法取消!";
	
	public static final String waiting_file = "请发送打印文件!";
	
	public static final String waiting_flip = "单面打印完成，请在一分钟内手动翻面并发送 '确认' 继续打印，超时将取消打印任务!";
	
	public static final String wait_timeout = "翻面等待超时，请在确认无其他打印任务时手动翻面并发送 '确认' 继续打印";
	
	public static final String receiving_file = "正在接收文件, 请稍后...";
	
	public static final String failed_receive_file = "接收文件失败!";
	
	public static final String too_big_file = "文件大小超过限制!";
	
	public static final String unsupported_file = "不支持的文件类型！";
	
//	public static final String supported_file;
	
	public static HashMap<String, String> messages = new HashMap<>();
	
	static{
//		StringBuilder sb = new StringBuilder("不支持的文件格式! 支持的格式：");
//		for(String s : Config.ALLOW_FILE){
//			sb.append(s).append("、");
//		}
//		supported_file = sb.toString();
	}
	
	public static String getMessage(String key){
		return messages.getOrDefault(key, key);
	}
}
