package com.print.handler.impl.print.setting;


import com.print.Context;
import com.print.handler.BaseHandler;
import com.print.handler.annotation.HelpMsg;
import com.print.handler.annotation.Keyword;
import com.print.handler.annotation.RequirePrintTask;
import com.print.print.task.PrintStatus;
import com.print.print.task.PrintTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Keyword("份数")
@HelpMsg("设置打印份数, 格式: n份")
@RequirePrintTask
public class SetCount extends BaseHandler{
	
	private static final Pattern PATTERN = Pattern.compile("([0-9]+)[份分]");
	
	@Override
	protected void handlerMessage(Context context, PrintTask printTask, String message){
		Matcher matcher = PATTERN.matcher(message);
		if(!matcher.find()){
			printTask.sendMessage("请输入正确的份数! 格式: n份");
			return;
		}
		int count = Integer.parseInt(matcher.group(1));
		if(printTask.status != PrintStatus.PRINTING && printTask.status != PrintStatus.WAITING_FLIP){
			if(count > context.config.configFile.Print.maxCount && printTask.sender.getId() != context.config.configFile.QQ.adminQQ){
				printTask.sendMessage("文件份数超过限制！最大" + context.config.configFile.Print.maxCount + "份");
			}else{
				printTask.printParam.count = count;
			}
			printTask.sendMessage(printTask.getInfo() + "\n发送'确认'开始打印");
		}else{
			printTask.sendMessage("打印任务已开始, 无法修改!");
		}
	}
	
	@Override
	public boolean hasKeyWord(String message){
		return PATTERN.matcher(message).find();
	}
}
