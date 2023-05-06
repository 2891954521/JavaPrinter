package com.print.print.task;

/**
 * 打印参数
 */
public class PrintParam{
	
	/**
	 * 打印颜色
	 */
	public PrintColor color;
	
	/**
	 * 打印类型
	 */
	public PrintType type;
	
	/**
	 * 打印份数
	 */
	public int count;
	
	
	public PrintParam(){
		color = PrintColor.GRAY;
		type = PrintType.SINGLE;
		count = 1;
	}
	
	/**
	 * 打印颜色
	 */
	public enum PrintColor{
		GRAY,
		COLOR
	}
	
	/**
	 * 打印类型
	 * 单面 or 双面
	 */
	public enum PrintType{
		SINGLE,
		DOUBLE
	}
}
