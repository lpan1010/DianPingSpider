package com.mapbar.spider.test;

import java.util.ArrayList;

import com.mapbar.spider.util.GetPageContent;
import com.mapbar.spider.util.WriteFile;

public class test {
	
//	public static void main(String[] args){
//		String url = "http://www.dianping.com/search/category/2/0";
////		GetContent gc = new GetContent();
////		String content = gc.getContent(url, "UTF-8");
//		String content = GetPageContent.GetContentUseHttpClient(url);
//		System.out.println(content);
//		WriteFile wf = new WriteFile();
//		wf.writeString(content, "./res/test.txt","GBK");
//	}
	
	public static void main(String[] args){
		ArrayList<String> list = new ArrayList<String>();
		list.add("lpan");
		list.add("abc");
		list.add("mapbar");
		System.out.println(list.size());
		String one = list.get(0);
		list.remove(0);
		System.out.println(one + "\t"+list.size());
		String two = list.get(0);
		list.remove(0);
		System.out.println(two + "\t"+list.size());
	}
}
