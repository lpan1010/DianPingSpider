package com.mapbar.spider.util;
/***
 * ÒÆ¶¯ÎÄ¼þ
 * @author liupa
 *
 */
public class MoveFile {
	/**
	 * 
	 * @param oldPath
	 * @param newPath
	 */
	public static void move(String oldPath, String newPath){
		CopyFile.copy(oldPath, newPath);
		DeleteFile.delete(oldPath);
	}

}
