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
 * ץȡ��ں���
 */
public class Launch {
	public static final Log LOG = LogFactory.getLog(Launch.class);
	public Launch(){
		
	}
	public void init(){
		/**�������ļ��ж�ȡ����*/
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
		/**���þ�̬����**/
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
		
		LOG.info("ץȡ��վ == "+main_site);
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
		/**��ʼ��*/
		launch.init();
		/**������ץȡ��URL�������ץȡ��URL����*/
		WaitingSnatchURLPool waitingSnatchPool = WaitingSnatchURLPool.getInstance();
		SnatchedURLPool snatchedPool = SnatchedURLPool.getInstance();
		/**��������*/
		SnatchHandler handler = new SnatchHandler(waitingSnatchPool,snatchedPool);
		/**��ʼץȡ*/
		handler.startSnatch(cmd);
		/**����Ƿ�ץȡ���*/
		while(true){
			try {
				//ÿ�������
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//ÿ��һ��ʱ�䱣��һ�ζϵ�
			waitingSnatchPool.writeInFile();
			
			LOG.debug("�̸߳��� = " + Thread.getAllStackTraces().size());
			Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
			Set<Thread> threads = maps.keySet();
			Iterator<Thread> it = threads.iterator();
			while(it.hasNext()){
				Thread thread = it.next();
				LOG.debug("�߳� "+thread.getName() + "\t" + thread.isAlive());
			}
//			snatchedPool.writeInFile();
			/**�ж������б��Ƿ�Ϊ�գ����Ϊ�գ��رն��߳��˳�����*/
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
		LOG.info("*******  -e   execute   ִ��         *******");
		LOG.info("*******  -r   recrawl   ��ץ         *******");
		LOG.info("******************************************");
	}
}
