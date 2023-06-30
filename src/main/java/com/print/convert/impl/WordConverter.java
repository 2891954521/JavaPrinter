package com.print.convert.impl;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.print.convert.BaseConverter;
import com.print.convert.ConvertException;
import com.print.convert.annotation.SupportSuffix;

import java.io.File;

/**
 * Word2PDF
 */
@SupportSuffix("doc")
@SupportSuffix("docx")
public class WordConverter extends BaseConverter{
    
    @Override
    public void convert(File source, File target){
        ActiveXComponent app = null;
        try{
            ComThread.InitSTA();
            app = createApp("Word.Application");
            Dispatch doc = Dispatch.call(app.getProperty("Documents").toDispatch(), "Open", source.toString(), false, true).toDispatch();
            Dispatch.call(doc, "SaveAs", target.toString(), docSaveAsPDF);
            Dispatch.call(doc, "Close", false);
        }catch(Exception e){
            throw new ConvertException(e.getMessage());
        }finally{
            if(app != null) app.invoke("Quit", wdNotSaveChanges);
            ComThread.Release();
        }
    }
}
