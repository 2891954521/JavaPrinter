package com.print.entity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * QQ文件
 */
public class QQFile{
	
	/**
	 * 文件名
	 */
	private final String name;
	
	/**
	 * 文件大小
	 */
	private final int size;
	
	/**
	 * 文件下载地址
	 */
	private final String url;
	
	public QQFile(String name, int size, String url){
		this.name = name;
		this.size = size;
		this.url = url;
	}
	
	public QQFile(@NotNull JSONObject json){
		name = json.getString("name");
		size = json.getInt("size");
		url = json.getString("url");
	}
	
	public String getUrl(){
		return url;
	}
	
	public int getSize(){
		return size;
	}
	
	public String getName(){
		return name;
	}

}
