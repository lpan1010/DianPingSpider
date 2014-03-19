package com.mapbar.spider.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mapbar.spider.Parser.ParseDirectoryPage;
/***
 * 
 * @author liupa
 *
 */
public class WriteFile {
	public static final Log LOG = LogFactory.getLog(WriteFile.class);
	public synchronized void writeString(String content, String filename, String charset){
		
		File outputFile = new File(filename);
		try {
			FileOutputStream fos = new FileOutputStream(outputFile, true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, charset);
			osw.write(content);
			osw.write("\n");
			osw.flush();
			fos.close();
			osw.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
public synchronized void writeFile(LinkedList<String> content, String filename){
		
		File outputFile = new File(filename);
		try {
			FileOutputStream fos = new FileOutputStream(outputFile, true);
			 OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8"); 
			 String str = null;
			 for(int i = 0; i < content.size();i++){
				str=content.get(i);
				//如果字符串没有内容，则不写入文件
				if(str == null || str.length() < 1){
					continue;
				}
				else{
					osw.write(str+"\r\n");
					osw.flush();
				}
			}
			 fos.close();
			 osw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public synchronized void writeFile(ArrayList<String> content, String filename){
		
		File outputFile = new File(filename);
		try {
			FileOutputStream fos = new FileOutputStream(outputFile, true);
			 OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8"); 
			 String str;
			 for(int i = 0; i < content.size();i++){
				str=content.get(i);
				//如果字符串没有内容，则不写入文件
				if(str == null || str.length() < 1){
					continue;
				}
				else{
					osw.write(str+"\r\n");
					osw.flush();
				}
			}
			 fos.close();
			 osw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
