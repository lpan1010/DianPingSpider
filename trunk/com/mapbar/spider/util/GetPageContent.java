package com.mapbar.spider.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mapbar.spider.cfg.ConstantParameters;

/***
 * 
 * @author liupa
 *
 */
public class GetPageContent {
	public static final Log LOG = LogFactory.getLog(GetPageContent.class);
	
	public static String[] proxyArray = 
	{   
		"192.168.0.67","192.168.9.19",
		"192.168.0.43"
	//	192.168.0.43:9099

    };
	/**
	 * ʹ��HttpClientץȡһ����ҳ
	 * @param urlString ץȡ����
	 * @param charset �ַ�����
	 * @return
	 */
	public static String GetContentUseHttpClient(String urlString){
		String content = new String();
		Random random = new Random();
		/**�������[0-length)֮����������Ϊ�����±�*/
		int index = random.nextInt(proxyArray.length);
//		String proxyHost = proxyArray[index];
//		String proxyHost = "10.10.21.111";//��
		String proxyHost = "192.168.0.43";//��
//		LOG.info("index == "+ index);
		int proxyPort = 9099;
		//����HttpClient
		List<Header> headers = new ArrayList<Header>(); 
		HttpClient httpClient = new HttpClient();
		headers.add(new Header("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)"));   
	    httpClient.getHostConfiguration().getParams().setParameter("http.default-headers", headers);  
	    httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort);
	    //����get����ʱ3��
	    httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(8000);	
        GetMethod getMethod = new GetMethod(urlString); 
        /**����ģʽ������2��*/
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,   
                new DefaultHttpMethodRetryHandler(2, false));
        /**����get����������ʱ*/
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,5000);  
        getMethod.addRequestHeader("Content-type" , "text/html; charset=utf-8");
        /**ץȡ���ص�״̬��*/
        int statusCode;
		try {
			statusCode = httpClient.executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_FORBIDDEN){
				/**�����ӱ����ڱ���ֹ���ļ���*/
				LOG.info("forbidden.");
				return ConstantParameters.FORBIDDEN;
			}
			else if(statusCode != HttpStatus.SC_OK) {
				LOG.info(("Method failed: " + getMethod.getStatusLine() + "  url == "+urlString));
				return ConstantParameters.STATUS_CODE_ERROR;
			}
			else{
				//��ȡ���ݲ�ȥ����ҳ���� 
        		content = new String(getMethod.getResponseBodyAsString().getBytes("gb2312"));
        		content = content.replaceAll("\r\n","").replaceAll("\r", "").replaceAll("\t", "").replaceAll("\n", "").replaceAll("  ", "");
			}
		}catch (ConnectTimeoutException e){
			LOG.info("���ӳ�ʱ == " + urlString);
			return ConstantParameters.STATUS_CODE_ERROR;
		}catch (SocketException e){
			LOG.info("socket error " + urlString);
			return ConstantParameters.STATUS_CODE_ERROR;
		}
		catch (SocketTimeoutException e){
			return ConstantParameters.STATUS_CODE_ERROR;
		} 
		catch (HttpException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			return ConstantParameters.STATUS_CODE_ERROR;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ConstantParameters.STATUS_CODE_ERROR;
		}
		return content;
	}
	
	public static void main(String[] args){
		String urlString =  "http://www.dianping.com/shop/5858890";
		String content = GetPageContent.GetContentUseHttpClient(urlString);
		System.out.println(content);
		WriteFile wf = new WriteFile();
		wf.writeString(urlString + "\t" + content, "./res/test.txt", "UTF-8");
		wf.writeString(urlString + "\t" + content, "./res/test.txt", "UTF-8");
	}
}
