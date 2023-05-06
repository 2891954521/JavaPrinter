package com.print.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ClassUtil{
	
	/**
	 * 获取一个包下的所有类
	 */
	public static @NotNull List<Class<?>> getClasses(@NotNull String packageName){
		List<Class<?>> classes = new ArrayList<>();
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		String path = packageName.replace('.', '/');
		try{
			Enumeration<URL> enumeration = classLoader.getResources(path);
			while(enumeration.hasMoreElements()){
				URL resource = enumeration.nextElement();
				if(resource.getProtocol().equals("file")){
					String filePath = resource.getFile().replace("%20", " ");
					addClassesFromDirectory(classes, packageName, new File(filePath));
				}else if(resource.getProtocol().equals("jar")){
					addClassesFromJar(classes, packageName, resource);
				}
			}
		}catch(IOException e){
			// ignore
		}
		return classes;
	}
	
	private static void addClassesFromDirectory(List<Class<?>> classes, String packageName, @NotNull File directory){
		if(!directory.exists()){
			return;
		}
		for(File file : directory.listFiles()){
			if(file.isDirectory()){
				addClassesFromDirectory(classes, packageName + "." + file.getName(), file);
			}else if(file.getName().endsWith(".class")){
				String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
				try{
					classes.add(Class.forName(className));
				}catch(ClassNotFoundException e){
					// ignore
				}
			}
		}
	}
	
	private static void addClassesFromJar(List<Class<?>> classes, String packageName, @NotNull URL jarUrl){
		try{
			JarURLConnection jarConnection = (JarURLConnection)jarUrl.openConnection();
			JarFile jarFile = jarConnection.getJarFile();
			Enumeration<JarEntry> entries = jarFile.entries();
			while(entries.hasMoreElements()){
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if(name.startsWith(packageName.replace('.', '/')) && name.endsWith(".class")){
					String className = name.substring(0, name.length() - 6).replace('/', '.');
					try{
						classes.add(Class.forName(className));
					}catch(ClassNotFoundException e){
						// ignore
					}
				}
			}
		}catch(IOException e){
			// ignore
		}
	}
}