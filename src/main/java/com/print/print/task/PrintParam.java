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
		type = PrintType.ALL_PAGES;
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
	 * - ALL_PAGES: 打印全部页
	 * - ODD_PAGES: 打印奇数页
	 * - EVEN_PAGES: 打印偶数页
	 */
	public enum PrintType{
		ALL_PAGES,
		ODD_PAGES,
		EVEN_PAGES
	}
}
