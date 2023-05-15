package com.print.print.task;

/**
 * 打印状态
 */
public enum PrintStatus{
	
	CANCEL("任务已取消"),
	
	WAITING_FILE("等待打印文件"),
	
	RECEIVING_FILE("正在接收文件"),
	
	/**
	 * 处理完文件的状态，可以设置打印参数
	 */
	BEFORE_PRINT("准备打印"),
	
	/**
	 * 已提交至打印队列，等待打印，此时可以设置打印参数
	 */
	WAITING_PRINT("等待打印"),
	
	/**
	 * 打印机正在打印
	 */
	PRINTING("打印中"),
	
	/**
	 * 双面打印时等待翻面
	 */
	WAITING_FLIP("等待翻面"),
	
	
	FINISH("结束打印");
	
	public final String name;
	
	PrintStatus(String name){
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
	
}
