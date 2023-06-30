package com.print.convert;

import com.jacob.activeX.ActiveXComponent;
import com.print.Context;
import com.print.print.task.PrintTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Created by zhangshichen on 2017/8/3.
 */
public abstract class BaseConverter{
    
    /**
     * word 不保存待定的更改
     */
    public static final int wdNotSaveChanges = 0;
    /**
     * xls不保存修改
     */
    public static final int xlDoNotSaveChanges = 2;
    
    /**
     * word转PDF 格式
     */
    public static final int docSaveAsPDF = 17;
    
    /**
     * ppt 转PDF 格式
     */
    public static final int pptSaveAsPDF = 32;
    
    /**
     * xls 转PDF 格式
     */
    public static final int xlsSaveAsPDF = 57;
    
    public void convert(@NotNull Context context, @NotNull PrintTask printTask) throws ConvertException{
    
        String name = printTask.printFile.file.getName();
    
        File newFile = new File(context.config.configFile.Print.fileRoot, name.substring(0, name.lastIndexOf(".")) + ".pdf");
        
        if(newFile.exists()) newFile.delete();
        
        long start = System.currentTimeMillis();

        convert(printTask.printFile.file, newFile);

        if(!newFile.exists()) throw new ConvertException("无输出文件");

        printTask.printFile.file = newFile;

        printTask.sendMessage("转换完成, 耗时: " + (System.currentTimeMillis() - start) + " ms");
    }
    
    protected final @NotNull ActiveXComponent createApp(String name){
        ActiveXComponent app = new ActiveXComponent(name);
        app.setProperty("Visible", false);
        return app;
    }

    public abstract void convert(File source, File target);
}
