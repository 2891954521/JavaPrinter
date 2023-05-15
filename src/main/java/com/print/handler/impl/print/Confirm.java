package com.print.handler.impl.print;

import com.print.Context;
import com.print.config.Messages;
import com.print.entity.User;
import com.print.handler.BaseHandler;
import com.print.handler.annotation.HelpMsg;
import com.print.handler.annotation.Keyword;
import com.print.handler.annotation.RequirePrintTask;
import com.print.print.task.PrintStatus;
import com.print.print.task.PrintTask;
import org.jetbrains.annotations.NotNull;


@Keyword("确认")
@HelpMsg("确认打印")
@RequirePrintTask
public class Confirm extends BaseHandler{
	
	@Override
	protected void handlerMessage(Context context, @NotNull PrintTask printTask, String message){
		boolean isPrint = handleStart(context, printTask);
		
		// 给管理员发送通知消息
		if(isPrint && printTask.sender.getId() != context.config.configFile.QQ.adminQQ){
			new User(context.config.configFile.QQ.adminQQ).sendMessage(context, "用户 (" + printTask.sender.getId() + ") 提交打印任务\n" + printTask.getInfo());
		}
	}
	
	
	private boolean handleStart(Context context, @NotNull PrintTask printTask){
		switch(printTask.status){
			case WAITING_FILE:
				printTask.sendMessage(Messages.waiting_file);
				return false;
			
			case RECEIVING_FILE:
				printTask.sendMessage(Messages.receiving_file);
				return false;
			
			case BEFORE_PRINT:
				// 提交打印任务
				printTask.status = PrintStatus.WAITING_PRINT;
				context.startPrintTask(printTask);
				printTask.sendMessage(Messages.task_submit);
				return true;
			
			case WAITING_PRINT:
				printTask.sendMessage(Messages.task_waiting);
				return false;
			
			case PRINTING:
				printTask.sendMessage(Messages.task_printing);
				return false;
			
			case WAITING_FLIP:
				// 翻面完成，继续打印
				context.continuePrintTask(printTask);
				printTask.sendMessage(Messages.task_submit);
				return true;
			
			default:
				printTask.sendMessage("未知的状态: " + printTask.status.name);
				return false;
		}
	}
	
}
