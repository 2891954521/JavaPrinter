package com.print.config;


/**
 * 系统命令
 */
public class Command{
	
	/*
	 * ===============
	 *   Windows
	 * ===============
	 */
//
//	/**
//	 * 将DOC转为PDF
//	 */
//	public static final String DOC_TO_PDF = "docto -f \"%s\" -O \"%s\" -T wdFormatPDF";

//	/**
//	 * 缩放到A4尺寸
//	 */
//	 public static final String SCALE_TO_A4 = "cpdf -scale-to-fit a4portrait %s -o %s";
	
	/**
	 * 获取系统打印队列
	 * 为空时打印队列为空
	 */
	public static final String GET_SYSTEM_PRINT_QUEUE = "wmic printjob get";
	
//	/**
//	 * 打印PDF文件
//	 */
//	public static final String PRINT_FILE = "PDFtoPrinter %s \"%s\" copies=%d";
	
	
}
