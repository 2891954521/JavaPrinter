package com.print.print.task;

import com.lowagie.text.pdf.PdfReader;
import com.print.Context;
import com.print.config.Command;
import com.print.config.Messages;
import com.print.entity.QQFile;
import com.print.entity.User;
import com.print.utils.CommandUtil;
import com.print.utils.FileUtil;
import org.jetbrains.annotations.NotNull;


import java.io.File;
import java.io.IOException;

public class PrintTask{
	
	/**
	 * 上下文
	 */
	private final Context context;
	
	/**
	 * 打印者
 	 */
	public User sender;
	
	/**
	 * 当前打印状态
	 */
	public PrintStatus status;
	
	
	public PrintFile printFile;
	
	
	public PrintParam printParam;
	
	
	public PrintTask(Context context, User sender){
		this.context = context;
		this.sender = sender;
		
		status = PrintStatus.WAITING_FILE;
		
		printFile = new PrintFile();
		printParam = new PrintParam();
	}
	
	/**
	 * 设置打印文件
	 * @param url 文件URL
	 * @param name 任务名称
	 * @param suffix 后缀名
	 */
	public void setFile(@NotNull String url, @NotNull String name, @NotNull String suffix){
		
		printFile.file = FileUtil.getOnlyFileName(
			context.config.configFile.Print.fileRoot,
			name.replaceAll("[ <>?*&\"\\\\|]", ""),
			suffix
		);
		
		new Thread(() -> {
			if(FileUtil.downloadFile(url, printFile.file)){
				processFile(name, suffix);
			}else{
				sendMessage(Messages.failed_receive_file);
			}
		}).start();
	}
	
	/**
	 * 接收完文件后对文件进行处理
	 */
	private void processFile(@NotNull String name, @NotNull String suffix){
		// 转换格式
		if("doc".equals(suffix) || "docx".equals(suffix)){
			if(!convertFile()) return;
		}
		
		if(!calculatePage()) return;
		
		printFile.name = name;
		
		status = PrintStatus.BEFORE_PRINT;
		
		sendMessage("文件处理完成!\n" + getInfo() + "\n发送'确认'开始打印");
	}
	
	
	/**
	 * 打印完成
	 * @return 是否移除打印任务（双面打印不移除任务
	 */
	public boolean completePrint(){
		if(status == PrintStatus.PRINTING){
			if(printParam.type == PrintParam.PrintType.DOUBLE){
				status = PrintStatus.WAITING_FLIP;
				sendMessage(Messages.waiting_flip);
				return false;
			}else{
				sendMessage(Messages.task_complete);
				context.removePrintTask(this);
				return true;
			}
		}else if(status == PrintStatus.PRINTING_OTHER_SIDE){
			sendMessage(Messages.task_complete);
			context.removePrintTask(this);
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 * 计算价格
	 * @return 价格
	 */
	public double getPrice(){
		return printParam.count * printFile.totalPages * (printParam.color == PrintParam.PrintColor.GRAY ? context.config.configFile.Print.grayscalePrice : context.config.configFile.Print.colourPrice);
	}
	
	/**
	 * 获取打印任务信息
	 * @return 打印任务信息
	 */
	public String getInfo(){
		return "名称: " + printFile.name +
				"\n页数: " + printFile.totalPages + "页" +
				"\n颜色: " + (printParam.color == PrintParam.PrintColor.GRAY ? "黑白" : "彩色") +
				 "\n类型: " + (printParam.type == PrintParam.PrintType.SINGLE ? "单面打印": "双面打印") +
				(printParam.count > 1 ? "\n份数: " + printParam.count + " 份" : "") +
				(context.config.configFile.Print.enablePrice ? "\n价格: " + String.format("%.2f 元", getPrice()) : "");
	}
	
	
	/**
	 * 转换doc为pdf
	 * @return 是否成功
	 */
	private boolean convertFile(){
		status = PrintStatus.CONVERTING;
		
		sendMessage("正在将Word转换为PDF，请稍候");
		
		String name = printFile.file.getName();
		
		File newFile = new File(context.config.configFile.Print.fileRoot, name.substring(0, name.lastIndexOf(".")) + ".pdf");
		
		long start = System.currentTimeMillis();
		
		try{
			String s = CommandUtil.execCommand(String.format(Command.DOC_TO_PDF, printFile.file, newFile));
			if(!"".equals(s)){
				if(!newFile.exists()){
					throw new RuntimeException("请手动转换为pdf，result: " + s);
				}
				printFile.file = newFile;
				sendMessage("转换完成, 耗时: " + (System.currentTimeMillis() - start) + " ms，发送 '预览' 以预览文件");
				return true;
			}else{
				throw new RuntimeException("请手动转换为pdf!");
			}
		}catch(IOException | InterruptedException | RuntimeException e){
			status = PrintStatus.WAITING_FILE;
			sendMessage("转换失败! " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * 计算文件页数
	 * @return 是否成功
	 */
	private boolean calculatePage(){
		status = PrintStatus.CALCULATING;
		
		try{
			PdfReader reader = new PdfReader(printFile.file.toString());
			printFile.totalPages = reader.getNumberOfPages();
			
			if(printFile.totalPages > context.config.configFile.Print.maxPage && sender.getId() != context.config.configFile.QQ.adminQQ){
				status = PrintStatus.WAITING_FILE;
				sendMessage("页数超过限制! 最大为" + context.config.configFile.Print.maxPage + "页");
				return false;
			}else{
				return true;
			}
			
			
		}catch(IOException | RuntimeException e){
			status = PrintStatus.WAITING_FILE;
			sendMessage("获取页数失败! " + e.getMessage());
			return false;
		}
	}
	
	
	/**
	 * 预览文件
	 */
	public boolean previewFile(){
//		String path = file.toString();
//		path = path.substring(0, path.lastIndexOf("."));
//		if(!hasPreview){
//			hasPreview = true;
//			try{
//				String result = Utils.execCommand(String.format("pdftoppm -png -f 1 -l %d %s %s", (Math.min(totalPages, 3)), file, path));
//				if(!"".equals(result)){
//					sendMessage("预览失败! ");
//					return false;
//				}
//			}catch(IOException | InterruptedException e){
//				e.printStackTrace();
//				sendMessage("预览失败! " + e.getMessage());
//				return false;
//			}
//		}
//
//		for(int i = 0; i < 3 && i < totalPages; i++){
//			sendMessage("[CQ:image,file=file://" + path + "-" + (i + 1) + ".png]");
//			try{
//				Thread.sleep(500);
//			}catch(InterruptedException ignored){ }
//		}
//
//		if(totalPages > 3){
//			sendMessage("剩余" + (totalPages - 3) + "页未预览");
//		}
		
		return true;
	}
	
	/**
	 * 获取打印的页码
	 */
	public String getPages(){
//		if(type == PrintType.SINGLE){
//			return "1-" + totalPages;
//		}else if(type == PrintType.DOUBLE){
//			StringBuilder sb = new StringBuilder();
//			// 打印反面时返还偶数页，其他情况返还奇数页
//			int i = status == PrintStatus.PRINTING_OTHER_SIDE ? 2 : 1;
//			for(; i <= totalPages; i += 2){
//				sb.append(i).append(',');
//			}
//			return sb.substring(0, sb.length() - 1);
//		}else{
//			return "1-" + totalPages;
//		}
		return "";
	}
	
	
	public void sendMessage(String message){
		sender.sendMessage(context, message);
	}
	
}

