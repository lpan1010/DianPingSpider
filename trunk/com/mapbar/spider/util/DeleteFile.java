package com.mapbar.spider.util;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/***
 * 
 * @author liupa
 * 删除某文件
 */
public class DeleteFile {
	public static final Log LOG = LogFactory.getLog(DeleteFile.class);
	public static int delete(String filename){
		File file = new File(filename);
		if(!file.exists()){
			LOG.info("文件不存在！\t" + filename);
			return -1;
		}
		else{
			if(!file.delete()){
				LOG.info("文件删除失败\t" + filename);
				return 0;
			}
			else{
				return 1;
			}
		}
	}
	public static void main(String[] args){
		DeleteFile.delete("./res/fail_list.txt");
	}
}
