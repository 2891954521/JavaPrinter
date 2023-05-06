package com.print.handler.impl;

import com.print.Context;
import com.print.entity.User;
import com.print.handler.BaseHandler;
import com.print.handler.annotation.Keyword;
import org.jetbrains.annotations.NotNull;

@Keyword("/")
public class Admin extends BaseHandler{
	
	@Override
	public void handlerMessage(Context context, User sender, String message){
		if(sender.getId() == context.config.configFile.QQ.adminQQ){
			String[] sp = message.substring(1).split(" ");
			StringBuilder sb = new StringBuilder("\n");
//			switch(sp[0]){
//				case "all":
//					if(Info.tasks.isEmpty()){
//						sendMessage(group, sender, "没有任务");
//						break;
//					}
//					for(PrintTask t : Info.tasks.values()){
//						sb.append("状态: ").append(t.status.toString()).append("\n").append(t.getInfo()).append("\n\n");
//					}
//					sendMessage(group, sender, sb.toString());
//					break;
//				case "list":
//					if(Info.printThread.queue.isEmpty()){
//						sendMessage(group, sender, "没有任务");
//						break;
//					}
//					for(PrintTask t : Info.printThread.queue){
//						sb.append(t.getInfo()).append("\n\n");
//					}
//					sendMessage(group, sender, sb.toString());
//					break;
//				case "chancel":
//					if(sp.length > 1){
//						int index = Integer.parseInt(sp[1]);
//						if(Info.printThread.queue.size() > index){
//							PrintTask[] tasks = Info.printThread.queue.toArray(new PrintTask[0]);
//							Info.printThread.queue.remove(tasks[index]);
//							sendMessage(group, sender, "移除任务完成！");
//						}
//					}else{
//						sendMessage(group, sender, "参数错误");
//					}
//					break;
//			}
		}
	}
	
	@Override
	public boolean hasKeyWord(@NotNull String message){
		return message.startsWith(keyWord);
	}
}
