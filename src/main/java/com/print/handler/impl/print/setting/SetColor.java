package com.print.handler.impl.print.setting;

import com.print.Context;
import com.print.handler.BaseHandler;
import com.print.handler.annotation.HelpMsg;
import com.print.handler.annotation.Keyword;
import com.print.handler.annotation.RequirePrintTask;
import com.print.print.task.PrintParam;
import com.print.print.task.PrintStatus;
import com.print.print.task.PrintTask;
import org.jetbrains.annotations.NotNull;

@Keyword("彩色")
@HelpMsg("设置为彩色")
@RequirePrintTask
public class SetColor extends BaseHandler{
	
	@Override
	protected void handlerMessage(Context context, @NotNull PrintTask printTask, String message){
		if(printTask.status != PrintStatus.PRINTING){
			printTask.printParam.color = PrintParam.PrintColor.COLOR;
			printTask.sendMessage(printTask.getInfo() + "\n发送'确认'开始打印");
		}else{
			printTask.sendMessage("打印任务已开始, 无法修改!");
		}
	}
	
	@Override
	public boolean hasKeyWord(@NotNull String message){
		return message.contains(keyWord);
	}

}
