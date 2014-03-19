package com.mapbar.spider.work;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mapbar.spider.Launch;
import com.mapbar.spider.Parser.city.ParserChannel;
import com.mapbar.spider.URLPool.SnatchedURLPool;
import com.mapbar.spider.URLPool.WaitingSnatchURLPool;
import com.mapbar.spider.cfg.ConstantParameters;
import com.mapbar.spider.util.GetPageContent;
import com.mapbar.spider.util.WriteFile;

/***
 * 
 * @author liupa
 *
 */
public class SnatchWorker extends Thread{
	public WaitingSnatchURLPool waitingSnatchPool;
	public SnatchedURLPool snatchedPool;
	public final String empty = "empty";
	public static int count = 0;
	public static String filename = "";
	public static WriteFile writeFile = null;
	public static CountDownLatch threadSignal = null;
	
	public static final Log LOG = LogFactory.getLog(SnatchWorker.class);
	public SnatchWorker(WaitingSnatchURLPool waitingSnatchPool,SnatchedURLPool snatchedPool, CountDownLatch threadSignal){
		this.waitingSnatchPool = waitingSnatchPool;
		this.snatchedPool = snatchedPool;
		this.writeFile = new WriteFile();
		this.threadSignal = threadSignal;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(true){
//				if(waitingSnatchPool.ContentURLQueue.size() == 0){
//					LOG.info("����URL�б�Ϊ�գ��ȴ�");
//				}
				
				String URLString = waitingSnatchPool.ContentURLQueue.take();
//				LOG.info("����URL�б�Ϊ��\t" + URLString);
				if(URLString != null && URLString.equals("empty") == true){
					LOG.info("��⵽empty,ֹͣ���С� " + Thread.currentThread().getName());
					break;
				}
				else{
					int state = work(URLString);
					/**��⵽������*/
					if(state == -1){
						/**�洢�Ѿ�ץȡ��URL�Լ����ڴ�ץȡ�����е�URL*/
						synchronized(this){
							waitingSnatchPool.saveBreakPoint();
//							snatchedPool.saveBreakPoint();
						}
					}
					/**��⵽ץȡʧ���ˣ�ץȡʧ�ܵ����ӱ�������*/
					if(state == -2){
						synchronized(this){
							writeFile.writeString(URLString, ConstantParameters.snatch_fail_list, ConstantParameters.CHARSET);
						}
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		threadSignal.countDown();
		return;
	}
	
	public int work(String URLString){
		/**��ȡ��ҳ����*/
		String pageContent = GetPageContent.GetContentUseHttpClient(URLString);
		if(pageContent == null || pageContent.length() == 0){
//			System.out.println("page content snatch fail. URL == " + URLString);
			return 0;
		}
		/**�����⵽����*/
		if(pageContent.equals(ConstantParameters.FORBIDDEN)){
			return -1;
		}
		/**�����⵽ץȡʧ��**/
		if(pageContent.equals(ConstantParameters.STATUS_CODE_ERROR)){
			return -2;
		}
		/**����һ��0��1000֮��������*/
		Random random = new Random();
		int sleeptime = random.nextInt(4000);
		sleeptime += 1000;
		/**�ж���ַ������ҳ�棬��������ҳ�档
		 * ���������ҳ�棬������ҳ�е���ַ��
		 * ���������ҳ�棬������ҳ����*/
		int flag = parseOrSave(URLString);
		if(flag == 1){
			/**��ҳ����д���ļ�*/
			WriteFile wf = new WriteFile();
			LOG.info("���� == " + URLString);
			if(count%10000 == 0){
				filename = ConstantParameters.save_directory + String.valueOf(count/10000)+".csv";
				LOG.info("�����ļ� == " + filename);
			}
			if(count%100 == 0){
				LOG.info("������ " + count + " ������.");
			}
			wf.writeString(URLString+"\t"+pageContent, filename, "UTF-8");
			count++;
		}
		if(flag == 0){
			/**������ҳ�е���ַ�������ץȡ����*/
			LOG.info("���� == " + URLString);
			ParserChannel pc = new ParserChannel();
			ArrayList<ArrayList<String>> parsedUrlList = pc.parse(pageContent);
//			System.out.println("������ϣ������ץȡ����");
			if(parsedUrlList.size() == 3){
				waitingSnatchPool.putIntoContentQueue(parsedUrlList.get(0), snatchedPool);
				synchronized(this){
					waitingSnatchPool.putIntoSeedQueue(parsedUrlList.get(1), snatchedPool);
				}
				waitingSnatchPool.putIntoSeedQueue(parsedUrlList.get(2), snatchedPool);
			}
		}
		/**������ץȡ������*/
		snatchedPool.putIn(URLString);
		/**Ъһ����ץ*/
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
	 * ֹͣ�̲߳��������̶߳��������Ϊ�ձ�ʶ��
	 */
	public void stopThread() {
		try {
			waitingSnatchPool.ContentURLQueue.put("empty");
		} catch (InterruptedException e) {
			LOG.error(e);
		}
	}
}
