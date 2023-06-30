package com.print.convert;

import com.print.Context;
import com.print.convert.annotation.SupportSuffix;
import com.print.convert.annotation.SupportSuffixes;
import com.print.print.task.PrintTask;
import com.print.utils.ClassUtil;
import com.print.utils.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class ConverterManager{
	
	/**
	 * 转换器
	 */
	public HashMap<String, BaseConverter> converters;
	
	private final Context context;
	
	public ConverterManager(@NotNull Context context){
		this.context = context;
		initConverter();
	}
	
	/**
	 * 初始化转换器
	 */
	public void initConverter(){
		List<Class<?>> classList = ClassUtil.getClasses("com.print.convert.impl");
		
		converters = new HashMap<>(classList.size());
		
		for(Class<?> clazz: classList){
			if(BaseConverter.class.isAssignableFrom(clazz)){
				try{
					if(clazz.isAnnotationPresent(SupportSuffixes.class)){
						BaseConverter converter = (BaseConverter)clazz.newInstance();
						SupportSuffix[] supportSuffixes = clazz.getAnnotation(SupportSuffixes.class).value();
						for(SupportSuffix suffix : supportSuffixes){
							if(suffix.value().length() > 0){
								converters.put(suffix.value(), converter);
							}
						}
					}else if(clazz.isAnnotationPresent(SupportSuffix.class)){
						BaseConverter converter = (BaseConverter)clazz.newInstance();
						String suffix = clazz.getAnnotation(SupportSuffix.class).value();
						if(suffix.length() > 0) converters.put(suffix, converter);
					}
				}catch(InstantiationException | IllegalAccessException e){
					Logger.log(e);
				}
			}
		}
	}
	
	public synchronized void convert(PrintTask printTask, String suffix) throws ConvertException{
		BaseConverter converter = converters.get(suffix);
		if(converter == null) throw new ConvertException("不支持的文件类型");
		printTask.sendMessage("正在将" + suffix + "转换为PDF，请稍候");
		converter.convert(context, printTask);
	}
	
	
	/*
	
	
	
	
	    public static final BufferedImage pdf2Png(InputStream input) {
        PDFRenderer renderer = null;
        try (PDDocument doc = PDDocument.load(input)) {
            renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            BufferedImage image = null;
            for (int i = 0; i < pageCount; i++) {
                if (image != null) {
                    image = combineBufferedImages(image, renderer.renderImageWithDPI(i, WINDOWS_NATIVE_DPI));
                }

                if (i == 0) {
                    // Windows jni DPI
                    image = renderer.renderImageWithDPI(i, WINDOWS_NATIVE_DPI);
                }

            }
            return combineBufferedImages(image);
        } catch (IOException e) {
            LOGGER.error("pdf 转换成png:{}", e);
            return null;
        }
    }
	 */
}
