package com.mapbar.spider.cfg;
/***
 * 
 * @author liupa
 *
 */
public class ConstantParameters {
	
	public static String CHARSET = null;
	public static String main_site = "";
	public static String second_domain="";
	public static String city_list = "";
	public static  int nThreads;
	public static String save_directory = "";
	public static String snatch_fail_list = "";
	public static final String FORBIDDEN = "forbidden";
	public static final String STATUS_CODE_ERROR = "error";
	public static boolean isRecrawl = false;
	public static String recrawl_seed_file = "";
	public static String recrawl_content_file = "";
	public static String snatched_file = "";
	public static String saved_seed_file = "";
	public static String saved_content_file = "";
	
	public static String getCHARSET() {
		return CHARSET;
	}
	public static void setCHARSET(String charset) {
		CHARSET = charset;
	}
	public static String getMain_site() {
		return main_site;
	}
	public static void setMain_site(String main_site) {
		ConstantParameters.main_site = main_site;
	}
	public static String getSecond_domain() {
		return second_domain;
	}
	public static void setSecond_domain(String second_domain) {
		ConstantParameters.second_domain = second_domain;
	}
	public static String getCity_list() {
		return city_list;
	}
	public static void setCity_list(String city_list) {
		ConstantParameters.city_list = city_list;
	}
	public static int getNThreads() {
		return nThreads;
	}
	public static void setNThreads(int threads) {
		nThreads = threads;
	}
	public static String getSave_directory() {
		return save_directory;
	}
	public static void setSave_directory(String save_directory) {
		ConstantParameters.save_directory = save_directory;
	}
	public static String getSnatch_fail_list() {
		return snatch_fail_list;
	}
	public static void setSnatch_fail_list(String snatch_fail_list) {
		ConstantParameters.snatch_fail_list = snatch_fail_list;
	}
	public static boolean isRecrawl() {
		return isRecrawl;
	}
	public static void setRecrawl(boolean isRecrawl) {
		ConstantParameters.isRecrawl = isRecrawl;
	}
	public static String getRecrawl_seed_file() {
		return recrawl_seed_file;
	}
	public static void setRecrawl_seed_file(String recrawl_seed_file) {
		ConstantParameters.recrawl_seed_file = recrawl_seed_file;
	}
	public static String getRecrawl_content_file() {
		return recrawl_content_file;
	}
	public static void setRecrawl_content_file(String recrawl_content_file) {
		ConstantParameters.recrawl_content_file = recrawl_content_file;
	}
	public static String getSnatched_file() {
		return snatched_file;
	}
	public static void setSnatched_file(String snatched_file) {
		ConstantParameters.snatched_file = snatched_file;
	}
	public static String getSaved_seed_file() {
		return saved_seed_file;
	}
	public static void setSaved_seed_file(String saved_seed_file) {
		ConstantParameters.saved_seed_file = saved_seed_file;
	}
	public static String getSaved_content_file() {
		return saved_content_file;
	}
	public static void setSaved_content_file(String saved_content_file) {
		ConstantParameters.saved_content_file = saved_content_file;
	}

}
