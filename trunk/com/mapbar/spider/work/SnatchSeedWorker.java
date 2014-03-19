package com.mapbar.spider.work;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mapbar.spider.Parser.city.ParserChannel;
import com.mapbar.spider.URLPool.SnatchedURLPool;
import com.mapbar.spider.URLPool.WaitingSnatchURLPool;
import com.mapbar.spider.cfg.ConstantParameters;
import com.mapbar.spider.util.GetPageContent;
import com.mapbar.spider.util.WriteFile;


public class SnatchSeedWorker extends Thread{
	public WaitingSnatchURLPool waitingSnatchPool;
	public SnatchedURLPool snatchedPool;
	public final String empty = "empty";
	public static int count = 0;
	public static String filename = "";
	public static WriteFile writeFile = null;
	public static CountDownLatch threadSignal = null;
	
	public static final Log LOG = LogFactory.getLog(SnatchSeedWorker.class);
	
	public SnatchSeedWorker(WaitingSnatchURLPool waitingSnatchPool,SnatchedURLPool snatchedPool, CountDownLatch threadSignal){
		this.waitingSnatchPool = waitingSnatchPool;
		this.snatchedPool = snatchedPool;
		this.writeFile = new WriteFile();
		this.threadSignal = threadSignal;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			String URLString = null;
			synchronized(this){
				URLString = waitingSnatchPool.takeOneSeedURL();
			}
//			if(URLString != null){
//				LOG.info("URLString == " + URLString);
//			}
			if(URLString != null && URLString.equals("empty") == true){
				LOG.info("检测到empty,停止运行。 " + Thread.currentThread().getName());
				break;
			}
			else if(URLString != null){
				int state = work(URLString);
				/**检测到被禁了*/
				if(state == -1){
					/**存储已经抓取的URL以及仍在待抓取队列中的URL*/
					synchronized(this){
						waitingSnatchPool.saveBreakPoint();
//						snatchedPool.saveBreakPoint();
					}
				}
				/**检测到抓取失败了，抓取失败的链接保存起来*/
				if(state == -2){
					synchronized(this){
						writeFile.writeString(URLString, ConstantParameters.snatch_fail_list, ConstantParameters.CHARSET);
					}
				}
			}
		}
//		LOG.info("while 结束");
		threadSignal.countDown();
		return;
	}
	private int work(String URLString) {
		// TODO Auto-generated method stub
		String pageContent = GetPageContent.GetContentUseHttpClient(URLString);
		if(pageContent == null || pageContent.length() == 0){
//			System.out.println("page content snatch fail. URL == " + URLString);
			return 0;
		}
		/**如果检测到被禁*/
		if(pageContent.equals(ConstantParameters.FORBIDDEN)){
			return -1;
		}
		/**如果检测到抓取失败**/
		if(pageContent.equals(ConstantParameters.STATUS_CODE_ERROR)){
			return -2;
		}
		/**生成一个0到3000之间的随机数*/
		Random random = new Random();
		int sleeptime = random.nextInt(5000);
		/**判断网址是种子页面，还是内容页面。
		 * 如果是种子页面，解析网页中的网址；
		 * 如果是内容页面，保存网页内容*/
		int flag = parseOrSave(URLString);
		if(flag == 1){
			/**网页内容写入文件*/
			WriteFile wf = new WriteFile();
			if(count%10000 == 0){
				filename = ConstantParameters.save_directory + String.valueOf(count/10000)+".csv";
				LOG.info("保存文件 == " + filename);
			}
			wf.writeString(URLString+"\t"+pageContent, filename, "UTF-8");
			LOG.info("保存 == " + URLString);
			count++;
			/**放入已抓取队列中*/
			snatchedPool.putIn(URLString);
		}
		if(flag == 0){
			/**解析网页中的网址，放入待抓取队列*/
			LOG.info("解析 == " + URLString);
			ParserChannel pc = new ParserChannel();
			ArrayList<ArrayList<String>> parsedUrlList = pc.parse(pageContent);
//			System.out.println("解析完毕，放入待抓取队列");
			if(parsedUrlList.size() == 3){
				synchronized(this){
					waitingSnatchPool.putIntoContentQueue(parsedUrlList.get(0), snatchedPool);
					waitingSnatchPool.putIntoSeedQueue(parsedUrlList.get(1), snatchedPool);
					waitingSnatchPool.putIntoSeedQueue(parsedUrlList.get(2), snatchedPool);
				}
			}
			/**放入已抓取队列中*/
			snatchedPool.putIn(URLString);
		}
		/**歇一会再抓*/
		try {
			Thread.sleep(sleeptime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

	public int parseOrSave(String urlString){
		if(urlString.contains("search/category")){
			return 0;
		}
		else if(urlString.contains("shop")){
			return 1;
		}
		else {
			return -1;
		}
	}
	/**
	 * 停止线程操作，向线程队列中添加为空标识符
	 */
	public void stopThread() {
		try {
			waitingSnatchPool.ContentURLQueue.put("empty");
		} catch (InterruptedException e) {
			LOG.error(e);
		}
	}
}
