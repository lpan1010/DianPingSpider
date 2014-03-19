package com.mapbar.spider.Parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.mapbar.spider.Launch;
import com.mapbar.spider.cfg.ConstantParameters;
import com.mapbar.spider.util.GetPageContent;
import com.mapbar.spider.util.WriteFile;
/**
 * 
 * @author liupa
 *
 */
public class ParseDirectoryPage {
	/***
	 * 解析目录页面
	 */
	public static final Log LOG = LogFactory.getLog(ParseDirectoryPage.class);
	public ArrayList<String> parse(String pageContent){
		ArrayList<String> urlList = new ArrayList<String>();
		try {
			String regexString = "<li class=\"shopname\"><a href=(.*?) class=(.*?) title=(.*?)";
			Parser htmlparser = new Parser(pageContent);
			NodeFilter tag_filter = new TagNameFilter("dd");
			NodeList nodelist = htmlparser.extractAllNodesThatMatch(tag_filter);
//			System.out.println("size: " + nodelist.size());
			for(int i = 0; i < nodelist.size(); i++){
				Pattern pattern = Pattern.compile(regexString);
				Matcher matcher = pattern.matcher(nodelist.elementAt(i).toHtml());
				while(matcher.find()){
					String url = matcher.group(1);
					//    /shop/13703311
					urlList.add(ConstantParameters.main_site + url.replaceAll("\"", ""));
//					System.out.println(url);
				}
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlList;
	}
	/**
	 * 测试函数
	 * @param args
	 */
	public static void main(String[] args){
		String urlString = "http://www.dianping.com/search/category/2/0/r2578";
		String pageContent = GetPageContent.GetContentUseHttpClient(urlString);
//		System.out.println(pageContent);
		ParseDirectoryPage parser = new ParseDirectoryPage();
		parser.parse(pageContent);
	}

}
