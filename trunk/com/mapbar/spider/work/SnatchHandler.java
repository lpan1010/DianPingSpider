package com.mapbar.spider.work;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.mapbar.spider.Launch;
import com.mapbar.spider.Parser.city.CityObject;
import com.mapbar.spider.URLPool.SnatchedURLPool;
import com.mapbar.spider.URLPool.WaitingSnatchURLPool;
import com.mapbar.spider.cfg.ConstantParameters;
import com.mapbar.spider.util.CopyFile;
import com.mapbar.spider.util.DeleteFile;
import com.mapbar.spider.util.MoveFile;
import com.mapbar.spider.util.ReadFile;

public class SnatchHandler {
	public static final Log LOG = LogFactory.getLog(SnatchHandler.class);
	private ArrayList<CityObject> cityObjects = new ArrayList<CityObject>();
	private WaitingSnatchURLPool waitingSnatchPool;
	private SnatchedURLPool snatchedPool;
	private SnatchWorker workers[];
	private SnatchSeedWorker seedWorkers[];
	
	public SnatchHandler(WaitingSnatchURLPool waitingSnatchPool,SnatchedURLPool snatchedPool){
		int nThreads = ConstantParameters.nThreads;
		this.waitingSnatchPool = waitingSnatchPool;
		this.snatchedPool = snatchedPool;
		this.workers = new SnatchWorker[nThreads ];
		this.seedWorkers = new SnatchSeedWorker[nThreads];

	}
	/***
	 * 启动多线程
	 */
	public void startMultiThread(CountDownLatch threadSignal1, CountDownLatch threadSignal2){
		int nThreads = ConstantParameters.nThreads;
		ExecutorService pool = Executors.newFixedThreadPool(nThreads + nThreads);
		for(int i = 0; i < nThreads; i++){
			seedWorkers[i] = new SnatchSeedWorker(waitingSnatchPool,snatchedPool, threadSignal1);
			pool.execute(seedWorkers[i]);
		}
		for(int i = 0; i < nThreads; i++){
			workers[i] = new SnatchWorker(waitingSnatchPool,snatchedPool, threadSignal2);
			pool.execute(workers[i]);
		}
		LOG.info("启动多线程");
		pool.shutdown();
	}
	/**
	 * 关闭多线程
	 */
	public void closeMultiThread(){
		LOG.info("启动关闭多线程");
		for(int i = 0; i < workers.length; i++){
			workers[i].stopThread();
		}
		for(int i = 0; i < seedWorkers.length; i++){
			seedWorkers[i].stopThread();
		}
		LOG.info("抓取完毕!关闭多线程!");
	}
	/**重新抓取**/
	public void startRecrawl(){
		cityObjects = null;
		ArrayList<String> seedList = ReadFile.readFile(ConstantParameters.recrawl_seed_file);
		ArrayList<String> contentList = ReadFile.readFile(ConstantParameters.recrawl_content_file);
		ArrayList<String> failList = ReadFile.readFile(ConstantParameters.snatch_fail_list);
		ArrayList<String> snatchedList = ReadFile.readFile(ConstantParameters.snatched_file);
		LOG.info("断点文件大小：\n1.种子列表:" + seedList.size() + "\n2.内容列表：" + contentList.size() + "\n3.抓取失败列表：" + failList.size() + "\n4.已抓取列表："+snatchedList.size());
		LOG.info("正在读取已抓取列表...");
		if(snatchedList != null){
			snatchedPool.readBreakPoint(snatchedList);
		}
		LOG.info("正在读取种子列表...");
		if(seedList != null){
			waitingSnatchPool.putIntoSeedQueue(seedList, snatchedPool);
		}
		LOG.info("正在读取内容列表...");
		if(contentList != null){
			waitingSnatchPool.putIntoSeedQueue(contentList, snatchedPool);
		}
		LOG.info("正在读取抓取失败列表...");
		if(failList != null){
			waitingSnatchPool.putIntoSeedQueue(failList, snatchedPool);
		}
		LOG.info("断点数据读取完毕!");
		LOG.info("备份断点数据");
		//保存备份
		MoveFile.move(ConstantParameters.snatch_fail_list, ConstantParameters.snatch_fail_list + ".txt");
		CopyFile.copy(ConstantParameters.snatched_file, ConstantParameters.snatched_file + ".txt");
		//		DeleteFile.delete(ConstantParameters.recrawl_seed_file);
//		DeleteFile.delete(ConstantParameters.recrawl_content_file);
//		DeleteFile.delete(ConstantParameters.snatch_fail_list);
//		DeleteFile.delete(ConstantParameters.snatched_file);
		
	}
	/**开始抓取*/
	public void startSnatch(String cmd){
		CountDownLatch threadSignal1 = new CountDownLatch(ConstantParameters.nThreads);
		CountDownLatch threadSignal2 = new CountDownLatch(ConstantParameters.nThreads);
		startMultiThread(threadSignal1, threadSignal2);
		LOG.info("开始抓取");
		if(cmd.equalsIgnoreCase("-e")){
			ReadCityList readCityList = new ReadCityList();
			cityObjects = readCityList.PrepareSnatch();
			for(int i = 0; i < cityObjects.size(); i++){
				CityObject object = cityObjects.get(i);
				String urlString = object.cityURI;
				waitingSnatchPool.putOneSeedURL(urlString,snatchedPool);
			}
		}
		else if(cmd.equalsIgnoreCase("-r")){
			LOG.info("开启重抓机制");
			startRecrawl();
		}
		else {
			Launch.printUsage();
			System.exit(0);
		}
//		LOG.info("size == "+waitingSnatchPool.SeedURLQueue.size());
		
		LOG.info("*****************************");
		
		return;
	}
}
