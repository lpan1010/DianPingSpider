package com.mapbar.spider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.mapbar.spider.Parser.city.CityObject;
import com.mapbar.spider.URLPool.SnatchedURLPool;
import com.mapbar.spider.URLPool.WaitingSnatchURLPool;
import com.mapbar.spider.cfg.ConstantParameters;
import com.mapbar.spider.util.ReadFile;
import com.mapbar.spider.work.SnatchHandler;
import com.mapbar.spider.work.SnatchSeedWorker;
import com.mapbar.spider.work.SnatchWorker;
/***
 * 
 * @author liupa
 * 抓取入口函数
 */
public class Launch {
	public static final Log LOG = LogFactory.getLog(Launch.class);
	public Launch(){
		
	}
	public void init(){
		/**从配置文件中读取变量*/
		ResourceBundle bundle = ResourceBundle.getBundle("spider");
		String main_site = bundle.getString("main_site");
		String second_domain = bundle.getString("second_domain");
		String city_list = bundle.getString("city_list");
		String snatch_fail_list = bundle.getString("snatch_fail_file");
		int nThreads = Integer.parseInt(bundle.getString("nThreads"));
		String save_directory = bundle.getString("save_directory");
		String recrawl_seed_file = bundle.getString("recrawl_seed_file");
		String recrawl_content_file = bundle.getString("recrawl_content_file");
		String snatched_file = bundle.getString("snatched_file");
		String saved_seed_file = bundle.getString("saved_seed_file");
		String saved_content_file = bundle.getString("saved_content_file");
		/**设置静态变量**/
		ConstantParameters.setMain_site(main_site);
		ConstantParameters.setSecond_domain(second_domain);
		ConstantParameters.setCity_list(city_list);
		ConstantParameters.setSnatch_fail_list(snatch_fail_list);
		ConstantParameters.setNThreads(nThreads);
		ConstantParameters.setSave_directory(save_directory);
		ConstantParameters.CHARSET = "UTF-8";
		ConstantParameters.setRecrawl_seed_file(recrawl_seed_file);
		ConstantParameters.setRecrawl_content_file(recrawl_content_file);
		ConstantParameters.setSnatched_file(snatched_file);
		ConstantParameters.setSaved_seed_file(saved_seed_file);
		ConstantParameters.setSaved_content_file(saved_content_file);
		
		LOG.info("抓取主站 == "+main_site);
	}
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		if(args.length != 1){
			printUsage();
			System.exit(0);
		}
		String cmd = args[0];
		if( !cmd.equalsIgnoreCase("-r") && !cmd.equalsIgnoreCase("-e")){
			printUsage();
			System.exit(0);
		}
		Launch launch = new Launch();
		/**初始化*/
		launch.init();
		/**创建待抓取的URL对象和已抓取的URL对象*/
		WaitingSnatchURLPool waitingSnatchPool = WaitingSnatchURLPool.getInstance();
		SnatchedURLPool snatchedPool = SnatchedURLPool.getInstance();
		/**创建对象*/
		SnatchHandler handler = new SnatchHandler(waitingSnatchPool,snatchedPool);
		/**开始抓取*/
		handler.startSnatch(cmd);
		/**检测是否抓取完毕*/
		while(true){
			try {
				//每过五分钟
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//每过一段时间保存一次断点
			waitingSnatchPool.writeInFile();
			
			LOG.debug("线程个数 = " + Thread.getAllStackTraces().size());
			Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
			Set<Thread> threads = maps.keySet();
			Iterator<Thread> it = threads.iterator();
			while(it.hasNext()){
				Thread thread = it.next();
				LOG.debug("线程 "+thread.getName() + "\t" + thread.isAlive());
			}
//			snatchedPool.writeInFile();
			/**判断两个列表是否为空，如果为空，关闭多线程退出程序*/
			if(waitingSnatchPool.ContentURLQueue.isEmpty() && waitingSnatchPool.SeedURLQueue.isEmpty()){
				handler.closeMultiThread();
				LOG.info("break");
				break;
			}
		}
		System.exit(0);
	}
	
	public static void printUsage(){
		LOG.info("******************************************");
		LOG.info("*******Usage == java -jar -e(or -r)*******");
		LOG.info("*******  -e   execute   执行         *******");
		LOG.info("*******  -r   recrawl   重抓         *******");
		LOG.info("******************************************");
	}
}
