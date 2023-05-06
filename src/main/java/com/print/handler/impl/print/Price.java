package com.print.handler.impl.print;

import com.print.Context;
import com.print.entity.User;
import com.print.handler.BaseHandler;
import com.print.handler.annotation.HelpMsg;
import com.print.handler.annotation.Keyword;
import com.print.handler.annotation.RequireSetting;
import org.jetbrains.annotations.NotNull;

@Keyword("价格")
@HelpMsg("获取打印价格")
@RequireSetting("enablePrice")
public class Price extends BaseHandler{
	
	@Override
	public void handlerMessage(@NotNull Context context, @NotNull User sender, String message){
		sender.sendMessage(context,
		"打印价格:\n" +
				"黑白 " + context.config.configFile.Print.grayscalePrice + " 元/面\n" +
				"彩色 " + context.config.configFile.Print.colourPrice + " 元/面"
		);
	}
}
