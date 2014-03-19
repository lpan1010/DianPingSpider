package com.mapbar.spider;

import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mapbar.spider.URLPool.SnatchedURLPool;
import com.mapbar.spider.URLPool.WaitingSnatchURLPool;
import com.mapbar.spider.cfg.ConstantParameters;
import com.mapbar.spider.util.ReadFile;
import com.mapbar.spider.work.SnatchHandler;


/**
 * 
 * @author liupa
 * 重抓机制
 */
public class Recrawl {
	public static final Log LOG = LogFactory.getLog(Recrawl.class);
	public Recrawl(){
		init();
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
		LOG.info("抓取主站 == "+main_site);
	}

}
