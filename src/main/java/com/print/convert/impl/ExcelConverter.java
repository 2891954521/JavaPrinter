package com.print.convert.impl;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.print.convert.BaseConverter;
import com.print.convert.ConvertException;
import com.print.convert.annotation.SupportSuffix;

import java.io.File;


@SupportSuffix("xls")
@SupportSuffix("xlsx")
public class ExcelConverter extends BaseConverter{
    
    @Override
    public void convert(File source, File target){
        ActiveXComponent app = null;
        try{
            ComThread.InitSTA();
            app = createApp("Excel.Application");
            Dispatch excel = Dispatch.call(app.getProperty("Workbooks").toDispatch(), "Open", source.toString(), false, true).toDispatch();
            Dispatch sheets = Dispatch.get(excel, "sheets").toDispatch();
        
            int count = Dispatch.get(sheets, "count").changeType(Variant.VariantInt).getInt();
            for (int i = 1; i <= count; i++) {
                Dispatch sheet = Dispatch.invoke(sheets, "Item", Dispatch.Get, new Object[]{i}, new int[1]).toDispatch();
                Dispatch page = Dispatch.get(sheet, "PageSetup").toDispatch();
                Dispatch.put(page, "PrintArea", false);
//				Dispatch.put(page, "Orientation", 2);
                Dispatch.put(page, "Zoom", false);
                Dispatch.put(page, "FitToPagesWide", 1); // 所有列为一页 (1 或 false)
                Dispatch.put(page, "FitToPagesTall", false);
            }
            
            Dispatch.call(excel, "SaveAs", target.toString(), xlsSaveAsPDF);
            Dispatch.call(excel, "Close", false);
        }catch(Exception e){
            throw new ConvertException(e.getMessage());
        }finally{
            if(app != null) app.invoke("Quit");
            ComThread.Release();
        }
    }
}
