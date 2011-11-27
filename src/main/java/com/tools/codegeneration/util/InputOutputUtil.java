package com.tools.codegeneration.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public class InputOutputUtil {
	//TODO: Replace System.out.println with log statements in this file
	
	/**
	 * Creates the directory named by the specified <code>absolutePath</code> 
	 * including any necessary but nonexistent parent directories.
	 * 
	 * @param absolutePath the absolute path that needs to be created on the 
	 * file system
	 * 
	 * @return the specified <code>absolutePath</code>
	 * 
	 * @see File#mkdirs()
	 */
	public static final String createDirectories(String absolutePath) {
		File filePath = new File(absolutePath);
		
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		
		return absolutePath;
	}
	
	/**
	 * 
	 * @param fileAbsolutePath the absolute path of the file that needs to be 
	 * created on the file system.
	 * 
	 * @return true if the named file does not exist and was successfully created;
	 * false if the named file already exists
	 * 
	 *  @see File#createNewFile()
	 */
	public static final boolean createFile(String fileAbsolutePath) {
		File file = new File(fileAbsolutePath);
		boolean flag = false;
		try {
			flag = file.createNewFile();
		} catch (IOException e) {
			flag = false;
			System.out.println("Could not create file [" + fileAbsolutePath + "]");
			System.out.println("Cause: " + e.getMessage());
		}
		return flag;
	}
	
	/**
	 * 
	 * @param fileAbsolutePath the absolute path of the file on the file system 
	 * to which the specified be <code>fileContents</code> needs to be written 
	 * to
	 * 
	 * @param fileContents the file contents
	 */
	public static void writeToFile(
			String fileAbsolutePath, Collection<String> fileContents) {
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(fileAbsolutePath);
		} catch (FileNotFoundException e) {
			fos = null;
			System.out.println("Could not open outstream for file [" + fileAbsolutePath + "]");
			System.out.println("Cause: " + e.getMessage());
		}
		
		if (fos != null) {
			System.out.println("Writing to file: [" + fileAbsolutePath + "]");
			byte[] bytes = null;
			for (String str : fileContents) {
//				System.out.println(str);
				bytes = str.getBytes();
				
				writeBytes(fos, bytes);
			}
			
			closeStream(fos);
			
			System.out.println("Completed writing to file [" + fileAbsolutePath + "]");
		}
	}
	
	/**
	 * 
	 * @param os an implementation instance of {@link OutputStream} to which
	 * the specified <code>bytes</code> are to be written to.
	 * 
	 * @param bytes an array of bytes
	 */
	public static final void writeBytes(OutputStream os, byte[] bytes) {
		try {
			os.write(bytes);
		} catch (IOException e) {
			System.out.println("Could not write to outputstream.");
			System.out.println("Cause: " + e.getMessage());
		}	
	}
	
	/**
	 * Closes a {@link Closeable} stream
	 * @param closeable an implementation instance of {@link Closeable} which
	 * needs to be closed
	 */
	public static final void closeStream(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				System.out.println("Could not close the stream.");
				System.out.println("Cause: " + e.getMessage());
			}
		}
	}
}
