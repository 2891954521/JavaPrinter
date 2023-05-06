package com.print.utils;

import com.print.config.Config;
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
		}catch(IOException ignored){ }
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
	 * @param name 文件名
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
	 * @param url url
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
			}catch(IOException ignored){}
		}
	}
	
}
