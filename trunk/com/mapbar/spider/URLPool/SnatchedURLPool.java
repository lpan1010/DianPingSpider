package com.mapbar.spider.URLPool;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mapbar.spider.cfg.ConstantParameters;
import com.mapbar.spider.util.DeleteFile;
import com.mapbar.spider.util.WriteFile;
/***
 * 
 * @author liupa
 *
 */
public class SnatchedURLPool {
	public static final Log LOG = LogFactory.getLog(SnatchedURLPool.class);
	/**用于存储哈希代码*/
	public static HashMap<Long,Integer> hashCode = null;
	public static SnatchedURLPool urlPoolInstance = null;
	public static WriteFile wf = null;
	public static int count = 0;
	public static int saveFlag = 0;
	
	public static SnatchedURLPool getInstance(){
		if(urlPoolInstance == null){
			urlPoolInstance = new SnatchedURLPool();
			hashCode = new HashMap<Long,Integer>();
			wf = new WriteFile();
			return urlPoolInstance;
		}
		else{
			return urlPoolInstance;
		}
	}
	
	public SnatchedURLPool(){
		
	}
	/**存入已抓取的URL列表*/
	public void putIn(ArrayList<String> urlList){
		if(urlList.size() == 0){
			return;
		}

		for(int i = 0; i < urlList.size(); i++){
			String urlString = urlList.get(i);
			putIn(urlString);
		}
	}
	/**存入一条已抓取的URL，对URL进行哈希再存储*/
	public void putIn(String urlString){
		if(urlString == ""){
			return;
		}
		long hashValue = FNVHash1(urlString);
		hashCode.put(hashValue,count);
		wf.writeString(String.valueOf(hashValue), ConstantParameters.getSnatched_file(), "UTF-8");
		count++;
	}
	/**查找某条URL是否抓去过*/
	public boolean find(String urlString){
		long hashValue = FNVHash1(urlString);
		if(hashCode.containsKey(hashValue) == true){
//			System.out.println("抓取过 == " + urlString);
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * 保存断点
	 */
	public synchronized void saveBreakPoint(){
		if(saveFlag == 0){
			writeInFile();
			saveFlag = 1;
		}
	}
	/**写入文件*/
	public synchronized void writeInFile(){
		String saveFileName = ConstantParameters.getSnatched_file();
		File snatchedFile = new File(saveFileName);
		if(snatchedFile.exists() == true){
			int flag = DeleteFile.delete(saveFileName);
		}
		Set<Long> code = hashCode.keySet();
		WriteFile wf = new WriteFile();
		Iterator it = code.iterator();
		while(it.hasNext()){
			String line = String.valueOf(it.next());
			wf.writeString(line, saveFileName, "UTF-8");
		}
	}
	
	/**读取断点数据*/
	public void readBreakPoint(ArrayList<String> snatchedList){
		for(int i = 0; i < snatchedList.size(); i++){
			String line = snatchedList.get(i);
			if(line != null && line.length() > 0){
				long key = Long.valueOf(line);
				hashCode.put(key, i);
			}
		}
	}
	 /**   
     * 改进的32位FNV算法1   
     * @param data 字符串   
     * @return int值   
     */    
    public static int FNVHash1(String data){     
        final int p = 16777619;     
        int hash = (int)2166136261L;     
        for(int i=0;i<data.length();i++)     
            hash = (hash ^ data.charAt(i)) * p;     
        hash += hash << 13;     
        hash ^= hash >> 7;     
        hash += hash << 3;     
        hash ^= hash >> 17;     
        hash += hash << 5;     
        return hash;     
    }  
    /**   
     * 混合hash算法，输出64位的值   
     */   
//	public static long mixHash(String str){     
//	    long hash = str.hashCode();     
//	     hash <<= 32;     
//	     hash |= SnatchedURLPool.FNVHash1(str);     
//	    return hash;     
//	}  
}
