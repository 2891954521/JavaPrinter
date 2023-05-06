package com.print.handler;

import com.print.Context;
import com.print.config.Config;
import com.print.entity.QQFile;
import com.print.entity.User;
import com.print.handler.annotation.HelpMsg;
import com.print.handler.annotation.Keyword;
import com.print.handler.annotation.RequireSetting;
import com.print.handler.impl.FileHandler;
import com.print.handler.impl.Help;
import com.print.utils.ClassUtil;
import com.print.utils.Logger;
import com.print.utils.WebUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理用户消息的类
 */
public class MessageHandler{
	
	/**
	 * 功能
	 */
	public List<BaseHandler> functions;
	
	private final FileHandler fileHandler;
	
	private final Context context;
	
	public MessageHandler(@NotNull Context context){
		this.context = context;
		initHandler(context.config);
		fileHandler = new FileHandler();
	}
	
	/**
	 * 初始化功能列表
	 */
	public void initHandler(Config config){
		List<Class<?>> classList = ClassUtil.getClasses("com.print.handler.impl");
		
		functions = new ArrayList<>(classList.size());
		
		for(Class<?> clazz: classList){
			if(BaseHandler.class.isAssignableFrom(clazz)){
				try{
					// 检查需要的配置文件设置有没有开启
					if(clazz.isAnnotationPresent(RequireSetting.class)){
						RequireSetting requireSetting = clazz.getAnnotation(RequireSetting.class);
						if(!config.boolSetting.getOrDefault(requireSetting.value(), false)) continue;
					}
					
					if(!clazz.isAnnotationPresent(Keyword.class)) continue;
					
					Keyword keyword = clazz.getAnnotation(Keyword.class);
					String val = keyword.value();
					
					if(val == null || val.length() == 0) continue;
					
					BaseHandler message = (BaseHandler)clazz.newInstance();
					message.keyWord = val;
					
					if(clazz.isAnnotationPresent(HelpMsg.class)){
						HelpMsg helpMsg = clazz.getAnnotation(HelpMsg.class);
						message.help = helpMsg.value();
					}
					
					functions.add(message);

				}catch(InstantiationException | IllegalAccessException e){
					Logger.log(e);
				}
			}
		}
	}
	
	/**
	 * 处理文本消息
	 */
	public void handlerTextMessage(User sender, String message){
		for(BaseHandler m : functions){
			if(m.hasKeyWord(message)){
				m.handleMessage(context, sender, message);
				break;
			}
		}
	}
	
	/**
	 * 处理文件消息
	 */
	public void handlerFileMessage(User sender, QQFile file){
		fileHandler.handlerFileMessage(context, sender, file);
	}
	
	/**
	 * 处理好友请求
	 */
	public void handlerFriendRequest(String flag){
		try{
			WebUtil.doGet(context.config.configFile.QQ.botUrl + "/set_friend_add_request?flag=" + flag);
		}catch(IOException e){
			Logger.log("添加好友失败", e);
		}
	}
	
	/**
	 * 好友添加后发送欢迎语
	 */
	public void handlerFriendAdd(@NotNull User user){
		user.sendMessage(context, context.config.configFile.welcomeMessage + "\n" + Help.getHelpText(context));
	}
	
}
