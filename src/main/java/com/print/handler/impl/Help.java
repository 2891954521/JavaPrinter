package com.print.handler.impl;

import com.print.Context;
import com.print.entity.User;
import com.print.handler.BaseHandler;
import com.print.handler.annotation.HelpMsg;
import com.print.handler.annotation.Keyword;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

@Keyword("help")
@HelpMsg("获取帮助")
public class Help extends BaseHandler{
	
	@Override
	public void handlerMessage(Context context, User sender, String message){
		sendMessage(context, sender, getHelpText(context));
	}
	
	public static String getHelpText(@NotNull Context context){
		StringJoiner sj = new StringJoiner("\n").add("自助打印指令：");
		for(BaseHandler f : context.messageHandler.functions){
			if(f.getHelp() != null){
				sj.add(f.keyWord + ": " + f.getHelp());
			}
		}
		sj.add("有问题联系管理员，QQ: " + context.config.configFile.QQ.adminQQ);
		return sj.toString();
	}
}
