package com.print.utils;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class FileUtil{
	
	/**
	 * 读文件
	 */
	@NotNull
	public static String readFile(String str){
		return readFile(new File(str));
	}
	
	/**
	 * 读文件
	 */
	@NotNull
	public static String readFile(@NotNull File f){
		if(!f.exists()) return "";
		StringBuilder sb = new StringBuilder();
		try{
			FileReader b = new FileReader(f);
			char[] c = new char[1024];
			int len;
			while((len = b.read(c)) != -1) sb.append(c, 0, len);
		}catch(IOException ignored){
		
		}
		return sb.toString();
	}
	
	/**
	 * 写文件
	 */
	public static void writeFile(String f, String string){
		writeFile(new File(f), string);
	}
	
	/**
	 * 写文件
	 */
	public static void writeFile(File f, String string){
		try(Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)){
			w.write(string);
			w.flush();
		}catch(IOException ignored){
		}
	}
	
	/**
	 * 获取Jar路径
	 */
	public static @Nullable String getJarPath(){
		URL url = FileUtil.class.getProtectionDomain().getCodeSource().getLocation();
		
		String filePath;
		
		try{
			filePath = URLDecoder.decode(url.getPath(), "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		if(filePath.endsWith(".jar")){
			filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		}
		
		filePath = new File(filePath).getAbsolutePath();
		
		return filePath;
	}
	
	/**
	 * 获取不重命文件名
	 *
	 * @param name   文件名
	 * @param suffix 后缀名 (不带'.')
	 */
	public static File getOnlyFileName(String parent, String name, String suffix){
		File file = new File(parent, name + "." + suffix);
		int count = 1;
		while(file.exists()){
			file = new File(parent, name + "_" + count++ + "." + suffix);
		}
		return file;
	}
	
	/**
	 * 下载文件
	 *
	 * @param url  url
	 * @param file 下载到的文件
	 * @return 是否下载成功
	 */
	public static boolean downloadFile(String url, File file){
		InputStream input = null;
		FileOutputStream writer = null;
		try{
			writer = new FileOutputStream(file);
			
			HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			
			if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
				input = connection.getInputStream();
				int len;
				byte[] buffer = new byte[8 * 1024];
				while((len = input.read(buffer)) != -1) writer.write(buffer, 0, len);
				writer.flush();
				return true;
			}else{
				return false;
			}
		}catch(IOException e){
			return false;
		}finally{
			try{
				if(input != null) input.close();
				if(writer != null) writer.close();
			}catch(IOException ignored){
			}
		}
	}
	
	/**
	 * doc转pdf
	 * @param source 输入doc路径
	 * @param target 输出pdf路径
	 * @return 转换耗时，单位ms，-1表示转换失败
	 */
	public static long doc2pdf(File source, @NotNull File target){
		long start = System.currentTimeMillis();
		
		if(target.exists()) target.delete();
		
		Dispatch doc = null;
		ActiveXComponent app = null;
		try{
			ComThread.InitSTA();
			app = new ActiveXComponent("Word.Application");
			app.setProperty("Visible", false);
			doc = Dispatch.call(app.getProperty("Documents").toDispatch(), "Open", source.toString(), false, true).toDispatch();
			Dispatch.call(doc, "SaveAs", target.toString(), 17);
		}finally{
			if(doc != null) Dispatch.call(doc, "Close", false);
			if(app != null) app.invoke("Quit", 0);
			ComThread.Release();
		}
		return System.currentTimeMillis() - start;
	}
	
	public static void excel2pdf(File source, @NotNull File target) {
		long start = System.currentTimeMillis();
		
		if(target.exists()) target.delete();
		
		ActiveXComponent app = null;
		try{
			ComThread.InitSTA();
			app = new ActiveXComponent("Excel.Application");
			app.setProperty("Visible", false);
			Dispatch excels = app.getProperty("Workbooks").toDispatch();
			Dispatch excel = Dispatch.call(excels, "Open", source.toString(), false, true).toDispatch();
			
//			Dispatch.call(excel, "ExportAsFixedFormat",  0, target.toString(), 0, true, true);
			
			Dispatch sheets = Dispatch.get(excel, "sheets").toDispatch();
			setPrintArea(sheets);
			int count = Dispatch.get(sheets, "count").getInt();
			for (int i = 1; i <= count; i++) {
				Dispatch sheet = Dispatch.invoke(sheets, "Item", Dispatch.Get, new Object[]{i}, new int[1]).toDispatch();
				Dispatch.call(sheet, "Select", false);
			}
			
			Dispatch.call(excel, "SaveAs", target.toString(), 57);
			
			Dispatch.call(excel, "Close", false);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(app != null) app.invoke("Quit");
			ComThread.Release();
		}
	}
	
	/*
	 *  为每个表设置打印区域
	 */
	private static void setPrintArea(Dispatch sheets) {
		int count = Dispatch.get(sheets, "count").changeType(Variant.VariantInt).getInt();
		for (int i = count; i >= 1; i--) {
			Dispatch sheet = Dispatch.invoke(sheets, "Item",
					Dispatch.Get, new Object[]{i}, new int[1]).toDispatch();
			Dispatch page = Dispatch.get(sheet, "PageSetup").toDispatch();
//			Dispatch.put(page, "PrintArea", false);
//			Dispatch.put(page, "Orientation", 2);
//			Dispatch.put(page, "Zoom", 100);      //值为100或false
//			Dispatch.put(page, "FitToPagesTall", false);  //所有行为一页
			Dispatch.put(page, "FitToPagesWide", 1);      //所有列为一页(1或false)
		}
	}
}
