package com.print.handler.impl.print;

import com.print.Context;
import com.print.config.Messages;
import com.print.handler.BaseHandler;
import com.print.handler.annotation.HelpMsg;
import com.print.handler.annotation.Keyword;
import com.print.handler.annotation.RequirePrintTask;
import com.print.print.task.PrintStatus;
import com.print.print.task.PrintTask;
import org.jetbrains.annotations.NotNull;

@Keyword("打印")
@HelpMsg("获取打印信息")
@RequirePrintTask
public class Print extends BaseHandler{
	
	@Override
	protected void handlerMessage(Context context, @NotNull PrintTask printTask, String message){
		if(printTask.status == PrintStatus.BEFORE_PRINT){
			printTask.sendMessage(printTask.getInfo() + "\n发送'确认'开始打印");
			
		}else if(printTask.status == PrintStatus.WAITING_FILE){
			printTask.sendMessage(Messages.waiting_file);
			
		}else{
			printTask.sendMessage("已有打印任务进行中, 是否取消? (发送'取消'以取消打印任务)");
		}
	}
	
}
