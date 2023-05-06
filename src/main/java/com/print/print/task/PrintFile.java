package com.print.print.task;

import java.io.File;

/**
 * 打印文件
 */
public class PrintFile{
	
	/**
	 * 总页数
	 */
	public int totalPages;
	
	/**
	 * 打印任务名称
	 */
	public String name;
	
	/**
	 * 文件路径
	 */
	public File file;
	
	/**
	 * 是否已经预览过
	 */
	public boolean hasPreview;
	
}
