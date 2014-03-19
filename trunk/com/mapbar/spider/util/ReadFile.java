package com.mapbar.spider.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mapbar.spider.Launch;
/***
 * 
 * @author liupa
 *
 */
public class ReadFile {
	public static final Log LOG = LogFactory.getLog(ReadFile.class);
	public static String readFile(File file){
		StringBuffer result = new StringBuffer();
		 try
	        {
	        	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
	            {
	                String temp;
	                temp = br.readLine();
	                while (temp != null && temp.length() >0)
	                {
	                    result.append(temp);
	                    temp = br.readLine();
	                }
	            }
	        }
	        catch (FileNotFoundException e) {
				System.out.println("Error opening the file " + file);
				System.exit(0);
	        }
	        catch (IOException e) {
				System.out.println("Error reading the file " + file);
			}
		return result.toString();
	}

	public static ArrayList<String> readFile(String filename){
		File file = new File(filename);
		ArrayList<String> result = new ArrayList<String>();
		if(!file.exists()){
			LOG.info("文件不存在！\t" + filename);
			return result;
		}
		 try
	        {
	        	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
	            {
	                String temp;
	                temp = br.readLine();
	                
	                while (temp != null && temp.length() >0)
	                {
	                	String line =temp.replaceAll("\"", "").trim();
	                    result.add(line);
	                    temp = br.readLine();
	                }
	            }
	        }
		
	        catch (FileNotFoundException e) {
				System.out.println("Error opening the file " + file);
				System.exit(0);
	        }
	        catch (IOException e) {
				System.out.println("Error reading the file " + file);
			}
		return result;
	}
}
