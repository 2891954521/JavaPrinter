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

@Keyword("取消")
@HelpMsg("取消当前的打印任务")
@RequirePrintTask
public class Chancel extends BaseHandler{
	
	@Override
	protected void handlerMessage(Context context, @NotNull PrintTask printTask, String message){
		if(printTask.status == PrintStatus.PRINTING){
			printTask.sendMessage(Messages.task_cancel_fail);
			
		}else{
			printTask.status = PrintStatus.CANCEL;
			context.removePrintTask(printTask);
			printTask.sendMessage(Messages.task_cancel);
		}
	}

}
