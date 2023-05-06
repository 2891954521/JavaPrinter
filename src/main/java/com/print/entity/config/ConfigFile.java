package com.print.entity.config;

import com.print.utils.YamlUtil.Comment;
import com.print.utils.YamlUtil.Nestable;

import java.io.Serializable;

public class ConfigFile implements Serializable{
	
	/**
	 * 欢迎语
	 */
	@Comment("欢迎语")
	public String welcomeMessage;
	
	/**
	 * 命令前缀
	 */
	@Comment("命令前缀")
	public String commandPrefix = "/";
	
	/**
	 * 是否自动接收好友请求
	 */
	@Comment("是否自动接收好友请求")
	public boolean autoAcceptNewFriendRequest;
	
	@Comment("QQ相关信息")
	public QQ QQ = new QQ();
	
	@Comment("打印相关信息")
	public Print Print = new Print();
	
	
	@Nestable
	public static class QQ implements Serializable{
		
		/**
		 * 发送QQ消息的URL
		 */
		@Comment("发送QQ消息的URL")
		public String botUrl = "http://127.0.0.1:5700";
		
		/**
		 * 机器人QQ号（暂时没用）
		 */
		@Comment("机器人QQ号")
		public long botQQ = 0L;
		
		/**
		 * 管理员QQ号
		 */
		@Comment("管理员QQ号")
		public long adminQQ = 0L;
		
	}
	
	@Nestable
	public static class Print implements Serializable{
		
		/**
		 * 下载的文件和转换文件储存路径
		 */
		@Comment("下载的文件和转换文件储存路径")
		public String fileRoot = "C:/tmp";
		
		/**
		 * 黑白打印机名称
		 */
		@Comment("黑白打印机名称")
		public String grayscalePrinter;
		
		/**
		 * 彩色打印机名称
		 */
		@Comment("彩色打印机名称")
		public String colourPrinter;
		
		/**
		 * 是否启用计费
		 */
		@Comment("是否启用计费")
		public boolean enablePrice;
		
		/**
		 * 黑白价格
		 */
		@Comment("黑白价格")
		public double grayscalePrice;
		
		/**
		 * 彩色价格
		 */
		@Comment("彩色价格")
		public double colourPrice;
		
		/**
		 * 允许打印的文件类型
		 */
		@Comment("允许打印的文件类型")
		public String[] allowFile = new String[]{"pdf", "doc", "docx"};
		
		/**
		 * 最大允许的页数
		 */
		@Comment("最大允许的页数")
		public int maxPage = 10;
		
		/**
		 * 最大允许的大小，单位：MB
		 */
		@Comment("最大允许的大小，单位：MB")
		public int maxSize = 3;
		
		/**
		 * 最大允许的份数
		 */
		@Comment("最大允许的份数")
		public int maxCount = 10;
	}
	
}
