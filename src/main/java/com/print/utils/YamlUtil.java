package com.print.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class YamlUtil{
	
	public static @NotNull String dump(Object obj){
		StringBuilder sb = new StringBuilder();
		dump(sb, obj, 0);
		return sb.toString();
	}
	
	private static void dump(StringBuilder sb, Object obj, int deep){
		if(obj == null) return;
		
		char[] tab = new char[deep * 4];
		Arrays.fill(tab, ' ');
		
		Field[] fields = obj.getClass().getFields();
		try{
			for(Field field : fields){
				if(field.isAnnotationPresent(Comment.class)){
					sb.append(tab);
					sb.append("# ");
					sb.append((field.getAnnotation(Comment.class).value()));
					sb.append('\n');
				}
				sb.append(tab);
				sb.append(field.getName());
				sb.append(": ");
				
				if(field.getType().isAnnotationPresent(Nestable.class)){
					sb.append('\n');
					dump(sb, field.get(obj), deep + 1);
					
				}else if(field.getType().isArray()){
					sb.append('\n');
					Object[] T = (Object[])field.get(obj);
					for(Object o : T){
						sb.append(tab);
						sb.append("  - ");
						sb.append(o);
						sb.append('\n');
					}
					
				}else{
					sb.append(field.get(obj));
					sb.append('\n');
				}
			}
		}catch(IllegalAccessException e){
			Logger.log(e);
		}
	}
	
	public static void dumpBool(Map<String, Boolean> hashMap, @NotNull Object obj){
		Field[] fields = obj.getClass().getFields();
		try{
			for(Field field : fields){
				if(field.getType().isAnnotationPresent(Nestable.class)){
					dumpBool(hashMap, field.get(obj));

				}else if(field.getType() == Boolean.TYPE){
					hashMap.put(field.getName(), (Boolean)field.get(obj));
				}
			}
		}catch(IllegalAccessException e){
			Logger.log(e);
		}
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Comment{
		String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Nestable{
	
	}
}
