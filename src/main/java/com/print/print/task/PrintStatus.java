package com.print.print.task;

/**
 * 打印状态
 */
public enum PrintStatus{
	
	CANCEL("任务已取消"),
	
	WAITING_FILE("等待打印文件"),
	
	CONVERTING("转换中"),
	
	CALCULATING("计算页数"),
	
	/**
	 * 处理完文件的状态，可以设置打印参数
	 */
	BEFORE_PRINT("准备打印"),
	
	/**
	 * 已提交至打印队列，等待打印，此时可以设置打印参数
	 */
	WAITING_PRINT("等待打印"),
	
	/**
	 * 已提交打印任务给打印机
	 */
	PRINTING("打印中"),
	
	/**
	 * 双面打印时等待翻面
	 */
	WAITING_FLIP("等待翻面"),
	
	/**
	 * 提交打印另一面的请求
	 */
	WAITING_PRINT_OTHER_SIDE("等待打印反面中"),
	
	/**
	 * 正在打印另一面
	 */
	PRINTING_OTHER_SIDE("打印反面中"),
	
	FINISH("结束打印");
	
	public final String name;
	
	PrintStatus(String name){
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
	
}
