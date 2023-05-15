package com.print.print.task;

import com.print.Context;
import com.print.config.Command;
import com.print.config.Messages;
import com.print.entity.User;
import com.print.print.PrintHandler;
import com.print.utils.CommandUtil;
import com.print.utils.FileUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.jetbrains.annotations.NotNull;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.Sides;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	 *
	 * @param url    文件URL
	 * @param name   任务名称
	 * @param suffix 后缀名
	 */
	public synchronized void setFile(@NotNull String url, @NotNull String name, @NotNull String suffix){
		if(status != PrintStatus.WAITING_FILE){
			return;
		}
		
		new Thread(() -> {
			status = PrintStatus.RECEIVING_FILE;
			File file =  FileUtil.getOnlyFileName(
					context.config.configFile.Print.fileRoot,
					name.replaceAll("[ <>?*&\"\\\\|]", ""),
					suffix
			);
			if(FileUtil.downloadFile(url, file)){
				setFile(file, name, suffix);
			}else{
				sendMessage(Messages.failed_receive_file);
				status = PrintStatus.WAITING_FILE;
			}
		}).start();
	}
	
	/**
	 * 设置打印文件
	 *
	 * @param file   文件
	 * @param name   任务名称
	 * @param suffix 后缀名
	 */
	public void setFile(@NotNull File file, @NotNull String name, @NotNull String suffix){
		status = PrintStatus.RECEIVING_FILE;
		printFile.file = file;
		processFile(name, suffix);
	}
	
	/**
	 * 计算价格
	 *
	 * @return 价格
	 */
	public double getPrice(){
		return printParam.count * printFile.totalPages * (printParam.color == PrintParam.PrintColor.GRAY ? context.config.configFile.Print.grayscalePrice : context.config.configFile.Print.colourPrice);
	}
	
	/**
	 * 获取打印任务信息
	 *
	 * @return 打印任务信息
	 */
	public String getInfo(){
		return "名称: " + printFile.name +
				"\n页数: " + printFile.totalPages + "页" +
				"\n颜色: " + (printParam.color == PrintParam.PrintColor.GRAY ? "黑白" : "彩色") +
				"\n类型: " + (printParam.type == PrintParam.PrintType.ALL_PAGES ? "单面打印" : (printParam.type == PrintParam.PrintType.ODD_PAGES ? "双面打印" : "仅打印偶数页")) +
				(printParam.count > 1 ? "\n份数: " + printParam.count + " 份" : "") +
				(context.config.configFile.Print.enablePrice ? "\n价格: " + String.format("%.2f 元", getPrice()) : "");
	}
	
	
	/**
	 * 接收完文件后对文件进行处理
	 */
	private void processFile(@NotNull String name, @NotNull String suffix){
		// 转换格式
		if("doc".equals(suffix) || "docx".equals(suffix)){
			if(!convertFile()) return;
		}
		
		try(PDDocument document = PDDocument.load(printFile.file)){
			printFile.totalPages = document.getNumberOfPages();
			
			if(printFile.totalPages > context.config.configFile.Print.maxPage && sender.getId() != context.config.configFile.QQ.adminQQ){
				status = PrintStatus.WAITING_FILE;
				sendMessage("页数超过限制! 最大为" + context.config.configFile.Print.maxPage + "页");
				return;
			}
		}catch(IOException | RuntimeException e){
			status = PrintStatus.WAITING_FILE;
			sendMessage("获取页数失败! " + e.getMessage());
			return;
		}
		
		printFile.name = name;
		
		status = PrintStatus.BEFORE_PRINT;
		
		sendMessage("文件处理完成!\n" + getInfo() + "\n发送'确认'开始打印");
	}
	
	/**
	 * 转换doc为pdf
	 *
	 * @return 是否成功
	 */
	private boolean convertFile(){
		sendMessage("正在将Word转换为PDF，请稍候");
		
		String name = printFile.file.getName();
		
		File newFile = new File(context.config.configFile.Print.fileRoot, name.substring(0, name.lastIndexOf(".")) + ".pdf");
		
		long start = System.currentTimeMillis();
		
		try{
			String command = String.format(Command.DOC_TO_PDF, printFile.file.toString().replaceAll("\\\\", "/"), newFile.toString().replaceAll("\\\\", "/"));
			sendMessage(command);
			
			String s = CommandUtil.execCommand(command);
			if(!"".equals(s)){
				if(!newFile.exists()){
					throw new RuntimeException("请手动转换为pdf，result: " + s);
				}
				printFile.file = newFile;
				sendMessage("转换完成, 耗时: " + (System.currentTimeMillis() - start) + " ms");
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
	 * 提交打印任务到打印机
	 */
	public void print() throws IOException, PrintException{
		status = PrintStatus.PRINTING;
		try(PDDocument document = PDDocument.load(printFile.file)){
			PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
			attr.add(new Copies(printParam.count));
			attr.add(MediaSizeName.ISO_A4);
			attr.add(Sides.ONE_SIDED);
			
			List<PDDocument> pds = new ArrayList<>();
			
			// 按指定页面拆分文档
			if(printParam.type == PrintParam.PrintType.ODD_PAGES){
				PDDocument oddPages = new PDDocument();
				for(int i = 0; i < printFile.totalPages; i += 2){
					oddPages.addPage(document.getPage(i));
				}
				pds.add(oddPages);
			}else if(printParam.type == PrintParam.PrintType.EVEN_PAGES){
				PDDocument evenPages = new PDDocument();
				for(int i = 1; i < printFile.totalPages; i += 2){
					evenPages.addPage(document.getPage(i));
				}
				if(printFile.totalPages % 2 == 1){
					// 总页数为奇数时补一页空白
					evenPages.addPage(new PDPage(document.getPage(0).getMediaBox()));
				}
				pds.add(evenPages);
			}else{
				pds.add(document);
			}
			
//			List<AbstractMap.SimpleEntry<Integer, Integer>> pageNumList = new ArrayList<>();
//			Splitter splitter = new Splitter();
//			for(AbstractMap.SimpleEntry<Integer, Integer> entry : pageNumList){
//				splitter.setStartPage(entry.getKey());
//				splitter.setEndPage(entry.getValue());
//				pds.addAll(splitter.split(document));
//			}
			
			PageFormat pageFormat = new PageFormat();
			pageFormat.setOrientation(PageFormat.PORTRAIT);
			pageFormat.setPaper(PrintHandler.getInstance(context).paper);
			
			Book book = new Book();
			for(PDDocument pd : pds){
				PDFPrintable printable = new PDFPrintable(pd, Scaling.SCALE_TO_FIT);
				book.append(printable, pageFormat, pd.getNumberOfPages());
			}
			
			DocPrintJob printJob = PrintHandler.getInstance(context).getPrintService(printParam.color).createPrintJob();
			Doc doc = new SimpleDoc(book, DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
			
			printJob.print(doc, attr);
		}
	}
	
	public boolean printCompleted(){
		if(printParam.type == PrintParam.PrintType.ODD_PAGES){
			status = PrintStatus.WAITING_FLIP;
			printParam.type = PrintParam.PrintType.EVEN_PAGES;
			sendMessage(Messages.waiting_flip);
			return true;
		}else{
			status = PrintStatus.FINISH;
			sendMessage(Messages.task_complete);
			context.removePrintTask(this);
			return false;
		}
	}
	
	public void sendMessage(String message){
		sender.sendMessage(context, message);
	}
	
}

