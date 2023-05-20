/*
 * Copyright (c) 1999-2007 Sourceforge JACOB Project.
 * All rights reserved. Originator: Dan Adler (http://danadler.com).
 * Get more information about JACOB at http://sourceforge.net/projects/jacob-project
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.jacob.com;

import com.print.Context;
import com.print.utils.FileUtil;
import com.print.utils.Logger;

import java.io.*;

public final class LibraryLoader{
	
	/**
	 * Load the jacob dll either from resource.
	 */
	public static void loadJacobLibrary(){
		
		String name = shouldLoad32Bit() ? "jacob-1.20-x86.dll" : "jacob-1.20-x64.dll";
		
		String path = FileUtil.getJarPath();
		if(path == null){
			Logger.log("无法加载jacob dll库");
			return;
		}
		File file = new File(path, name);
		if(!file.exists()){
			try(InputStream inputStream = Context.class.getClassLoader().getResourceAsStream("dlls/" + name);
				OutputStream outputStream = new FileOutputStream(file)){
				byte[] buffer = new byte[4096];
				int bytesRead;
				while((bytesRead = inputStream.read(buffer)) != -1){
					outputStream.write(buffer, 0, bytesRead);
				}
			}catch(IOException e){
				Logger.log("无法加载jacob dll库");
				return;
			}
		}
		System.load(file.toString());
	}
	
	/**
	 * Detects whether this is a 32-bit JVM.
	 *
	 * @return {@code true} if this is a 32-bit JVM.
	 */
	private static boolean shouldLoad32Bit(){
		// This guesses whether we are running 32 or 64 bit Java.
		// This works for Sun and IBM JVMs version 5.0 or later.
		// May need to be adjusted for non-Sun JVMs.
		
		String bits = System.getProperty("sun.arch.data.model", "?");
		if(bits.equals("32"))
			return true;
		else if(bits.equals("64"))
			return false;
		
		// this works for jRocket
		String arch = System.getProperty("java.vm.name", "?");
		return !arch.toLowerCase().contains("64-bit");
	}
}
