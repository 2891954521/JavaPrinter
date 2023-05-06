package com.print.utils;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebUtil{
	
	public static String inputStream2string(@NotNull InputStream inputStream){
		try{
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while((len = inputStream.read(buffer)) != -1) outStream.write(buffer, 0, len);
			inputStream.close();
			return outStream.toString("UTF-8");
		}catch(IOException e){
			return "";
		}
	}
	
	public static String doGet(String url) throws IOException{
		HttpURLConnection connection = get(url);
		if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
			return inputStream2string(connection.getInputStream());
		}else{
			return "";
		}
	}
	
	public static @NotNull HttpURLConnection get(String url) throws IOException{
		HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
		
		connection.setInstanceFollowRedirects(false);
		
		connection.setRequestMethod("GET");
		
		connection.connect();
		
		return connection;
	}
	
	public static String doPost(String url, String data) throws IOException{
		HttpURLConnection connection = post(url, data);
		if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
			return inputStream2string(connection.getInputStream());
		}else{
			return "";
		}
	}
	
	public static @NotNull HttpURLConnection post(String url, @NotNull String data) throws IOException{
		HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
		
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		
		connection.setRequestMethod("POST");
		
		connection.getOutputStream().write(data.getBytes());
		
		connection.connect();
		
		return connection;
	}
	
	
}
