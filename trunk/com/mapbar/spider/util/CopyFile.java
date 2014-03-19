package com.mapbar.spider.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
/**
 * 文件拷贝
 * @author liupa
 *
 */
public class CopyFile {
	/**
	 * 拷贝文件oldPath到新文件newPath
	 * @param oldPath
	 * @param newPath
	 */
	public static void copy(String oldPath, String newPath){
		try{
			File oldFile = new File(oldPath);
			if(oldFile.exists()){
				InputStream is = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				int readByte = is.read(buffer);
				while(readByte != -1){
					fs.write(buffer, 0, readByte);
					readByte = is.read(buffer);
				}
				is.close();
				fs.close();
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		
		String oldPath = "D:/1.txt";
		String newPath = "D:/2.txt";
		
		CopyFile.copy(oldPath, newPath);
	}
}
