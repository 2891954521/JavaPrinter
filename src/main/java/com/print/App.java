package com.print;

import com.print.entity.QQFile;
import com.print.entity.User;
import com.print.utils.Logger;
import com.print.utils.WebUtil;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App{
	
	private final Context context;
	
	public App(){
		context = new Context();
	}
	
	public void start(){
		try{
			HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
			httpServer.createContext("/", httpExchange -> {
				handlerHttpData(WebUtil.inputStream2string(httpExchange.getRequestBody()));
				httpExchange.getResponseHeaders().add("Content-Type:", "text/html");
				httpExchange.sendResponseHeaders(200, 2);
				OutputStream ops = httpExchange.getResponseBody();
				ops.write("OK".getBytes());
				ops.flush();
				ops.close();
			});
			httpServer.start();
		}catch(IOException e){
			Logger.log(e);
		}
	}
	
	public void handlerHttpData(String data){
		try{
			if(data.length() == 0) return;
			
			JSONObject json = new JSONObject(data);
			
			switch(json.getString("post_type")){
				case "message":
					if("private".equals(json.getString("message_type"))){
						// 好友消息
						User user = new User(json.getLong("user_id"));
						String message = json.getString("raw_message");
						context.messageHandler.handlerTextMessage(user, message);
					}
					break;
				
				case "notice":
					if("offline_file".equals(json.getString("notice_type"))){
						// 文件消息
						User user = new User(json.getLong("user_id"));
						QQFile file = new QQFile(json.getJSONObject("file"));
						context.messageHandler.handlerFileMessage(user, file);
					}else if("friend_add".equals(json.getString("notice_type"))){
						// 好友添加
						User user = new User(json.getLong("user_id"));
						context.messageHandler.handlerFriendAdd(user);
					}
					break;
				
				case "request":
					if("friend".equals(json.getString("request_type"))){
						// 好友请求
						if(context.config.configFile.autoAcceptNewFriendRequest){
							context.messageHandler.handlerFriendRequest(json.getString("flag"));
						}
					}
					break;
			}
		}catch(Exception e){
			Logger.log(e);
		}
	}
}
