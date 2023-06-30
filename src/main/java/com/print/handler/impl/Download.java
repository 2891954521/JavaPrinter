package com.print.handler.impl;

import com.print.Context;
import com.print.handler.BaseHandler;
import com.print.handler.annotation.HelpMsg;
import com.print.handler.annotation.Keyword;
import com.print.handler.annotation.RequirePrintTask;
import com.print.print.task.PrintTask;
import com.print.utils.Logger;
import com.print.utils.WebUtil;

import java.io.IOException;
import java.net.URLEncoder;

@Keyword("下载")
@HelpMsg("下载打印的文件")
@RequirePrintTask
public class Download extends BaseHandler{
	
	@Override
	protected void handlerMessage(Context context, PrintTask printTask, String message){
		super.handlerMessage(context, printTask, message);
		
		try{
			WebUtil.doPost(
					context.config.configFile.QQ.botUrl + "/upload_private_file",
					"user_id=" + printTask.sender.getId() +
							"&user_id=" + printTask.printFile.name +
							"&file=" + URLEncoder.encode(printTask.printFile.file.toString(), "UTF-8")
			);
		}catch(IOException e){
			Logger.log("发送文件失败", e);
			printTask.sendMessage("发送文件失败");
		}
	}
}
