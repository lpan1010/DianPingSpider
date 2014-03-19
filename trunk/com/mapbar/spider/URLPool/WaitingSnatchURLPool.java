package com.mapbar.spider.URLPool;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mapbar.spider.cfg.ConstantParameters;
import com.mapbar.spider.util.DeleteFile;
import com.mapbar.spider.util.WriteFile;
/**
 * 
 * @author liupa
 *
 */
public class WaitingSnatchURLPool {
	/**存放待抓取的URL
	 * BlockingQueue是一个同步队列*/
	public static final Log LOG = LogFactory.getLog(WaitingSnatchURLPool.class);
	public static int POOL_SIZE = 10000;
	/**待抓取的内容页面URL，抓取下来保存内容的URL队列*/
	public static BlockingQueue <String> ContentURLQueue = null;
	/**待抓取的种子页面URL，用于保存解析网页中包含的URL***/
	public static LinkedList<String> SeedURLQueue = null;
	public static WaitingSnatchURLPool urlPoolInstance = null;
	
	
	public static int lock = 0;
	
	public static WriteFile wf = new WriteFile();
	/**是否保存过断点的标识*/
	public static int saveFlag = 0;
	
	public static ArrayList<Long> seedHashtable = new ArrayList<Long>();
	public static ArrayList<Long> contentHashtable = new ArrayList<Long>();
	
	public WaitingSnatchURLPool(){
		
	}
	public static WaitingSnatchURLPool getInstance(){
		if(urlPoolInstance == null){
			urlPoolInstance = new WaitingSnatchURLPool();
			ContentURLQueue = new ArrayBlockingQueue<String>(POOL_SIZE);
			SeedURLQueue = new LinkedList<String>();
			return urlPoolInstance;
		}
		else{
			return urlPoolInstance;
		}
	}
	/**存入待抓取URL队列*/
	public synchronized void putIntoContentQueue(ArrayList<String> urlList, SnatchedURLPool snatchedPool){
		if(urlList.size() == 0  ){
			return;
		}
		for(int i = 0; i < urlList.size(); i++){
			String urlString = urlList.get(i);
			putOneContentURL(urlString,snatchedPool);
		}
	}
	/**存入一条待抓取的内容URL*/
	public void putOneContentURL(String urlString, SnatchedURLPool snatchedPool){
		if(urlString.length() == 0 || !urlString.startsWith(ConstantParameters.main_site)){
			return;
		}
		/**判断是否抓取过，没有抓取过的网址存入队列中*/
		if(snatchedPool.find(urlString) == true){
			return;
		}
		/**判断是否存放过这个链接**/
		long hashValue = FNVHash1(urlString);
		if(contentHashtable.contains(hashValue)){
			return;
		}
		else{
			try {
//				System.out.println("存入队列");
				ContentURLQueue.put(urlString);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			contentHashtable.add(hashValue);
//			wf.writeString(String.valueOf(hashValue), ConstantParameters.saved_content_file, "UTF-8");
		}
		
	}
	/**存入种子URL列表,同步方法*/ 
	public synchronized void putIntoSeedQueue(ArrayList<String> urlList, SnatchedURLPool snatchedPool){
		lock = 0;
		if(urlList.size() == 0){
			return;
		}
		for(int i = 0; i < urlList.size(); i++){
			String urlString = urlList.get(i);
			putOneSeedURL(urlString, snatchedPool);
		}
		lock = 1;
	}
	/**存入一条待抓取的种子URL列表*/
	public void putOneSeedURL(String urlString, SnatchedURLPool snatchedPool){
		if(urlString.length() == 0 || urlString.length() > 50 ||!urlString.startsWith(ConstantParameters.main_site )){
			return;
		}
		/**判断是否抓取过，没有抓取过的网址存入队列中*/
		if(snatchedPool.find(urlString) == true){
			return;
		}
		/**判断是否被保存过*/
		long hashValue = FNVHash1(urlString);
		if(seedHashtable.contains(hashValue)){
			return;
		}
		else{
			seedHashtable.add(hashValue);
//			wf.writeString(String.valueOf(hashValue), ConstantParameters.saved_seed_file, "UTF-8");
		}
		SeedURLQueue.add(urlString);
	}
	/**从待抓取的种子URL列表中取出一条URL*/
	public synchronized String takeOneSeedURL(){
//		if(lock == 0){
//			return null;
//		}
		String urlString = "";
		if(SeedURLQueue.size() <= 0){
			return null;
		}
		/**获取并移除列表中第一个元素**/
		urlString = SeedURLQueue.pollFirst();
//		System.out.println("取出 "+urlString);
		return urlString;
	}
	/***
	 * 保存断点
	 */
	public synchronized void saveBreakPoint(){
		if(saveFlag == 0){
			writeInFile();
			saveFlag = 1;
		}
	}
	
	public void writeInFile(){
		/**定义文件名**/
		String contentURLSaveFile = ConstantParameters.getRecrawl_content_file();
		String seedURLSaveFile = ConstantParameters.getRecrawl_seed_file();
		File contentFile = new File(contentURLSaveFile);
		File seedFile = new File(seedURLSaveFile);
		if(contentFile.exists()){
			int flag = DeleteFile.delete(contentURLSaveFile);
			if(flag == 0){
				ConstantParameters.setRecrawl_content_file(contentURLSaveFile + ".txt");
				contentURLSaveFile = ConstantParameters.getRecrawl_content_file();
			}
		}
		if(seedFile.exists()){
			int flag = DeleteFile.delete(seedURLSaveFile);
			if(flag == 0){
				ConstantParameters.setRecrawl_seed_file(seedURLSaveFile + ".txt");
				seedURLSaveFile = ConstantParameters.getRecrawl_seed_file();
			}
		}
		WriteFile wf = new WriteFile();
		/**写入内容URL列表**/
		Iterator<String> it = ContentURLQueue.iterator();
		while(it.hasNext()){
			String line = it.next();
			wf.writeString(line, contentURLSaveFile, "UTF-8");
		}
//		Object[] objects = ContentURLQueue.toArray();
//		for(int i = 0; i < objects.length; i++){
//			String line = (String)objects[i];
//			wf.writeString(line, contentURLSaveFile, "UTF-8");
//		}
		
		/***写入种子URL列表*/
		wf.writeFile(SeedURLQueue, seedURLSaveFile);
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
}
