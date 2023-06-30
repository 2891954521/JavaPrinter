package com.print.convert.impl;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.print.convert.BaseConverter;
import com.print.convert.ConvertException;
import com.print.convert.annotation.SupportSuffix;

import java.io.File;


@SupportSuffix("ppt")
@SupportSuffix("pptx")
public class PowerpointConverter extends BaseConverter{
	
	@Override
	public void convert(File source, File target){
		ComThread.InitSTA();
		ActiveXComponent app = null;
		try{
			app = new ActiveXComponent("Powerpoint.Application");
			
			Dispatch presentation = Dispatch.call(app.getProperty("Presentations").toDispatch(), "Open", source.toString(), true).toDispatch();
			
			Dispatch.call(presentation, "SaveAs", target.toString(), pptSaveAsPDF);
			
			Dispatch.call(presentation, "Close");
		}catch(Exception e){
			throw new ConvertException(e.getMessage());
		}finally{
			if(app != null) app.invoke("Quit");
			ComThread.Release();
		}
	}
}
