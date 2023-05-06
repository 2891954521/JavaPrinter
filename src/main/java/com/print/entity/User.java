package com.print.entity;

import com.print.Context;
import com.print.utils.Logger;
import com.print.utils.WebUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * 用户类
 */
public class User{
	
	private final long id;
	
	public User(long id){
		this.id = id;
	}
	
	public long getId(){
		return id;
	}
	
	/**
	 * 给用户发送消息
	 */
	public void sendMessage(@NotNull Context context, String msg){
		try{
			WebUtil.doPost(context.config.configFile.QQ.botUrl + "/send_msg", "user_id=" + id + "&message=" + URLEncoder.encode(msg, "UTF-8"));
		}catch(IOException e){
			Logger.log("发送消息失败", e);
		}
	}
}
