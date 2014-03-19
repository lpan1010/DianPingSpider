package com.mapbar.spider.work;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mapbar.spider.Parser.city.CityObject;
import com.mapbar.spider.cfg.ConstantParameters;
import com.mapbar.spider.util.ReadFile;
/***
 * 
 * @author liupa
 *
 */
public class ReadCityList {
	public static final Log LOG = LogFactory.getLog(ReadCityList.class);
	/**准备抓取阶段*/
	public ArrayList<CityObject> PrepareSnatch(){
		/**读取城市列表文件*/
		ArrayList<String> cities = ReadFile.readFile(ConstantParameters.city_list);
		LOG.info("读取城市列表文件,城市个数 == "+cities.size());
		/**写入CityObject类中*/
		ArrayList<CityObject> cityObjects = CreateCityObject(cities);
		return cityObjects;
	}
	/***
	 * 根据输入的城市列表文件，创建每个城市页面的对象。
	 * @param cities
	 * @return
	 */
	public ArrayList<CityObject> CreateCityObject(ArrayList<String> cities){
		ArrayList<CityObject> cityObjects = new ArrayList<CityObject>();
		String urlPattern = ConstantParameters.main_site + "/" + ConstantParameters.second_domain + "@" + "/0/";
		for(String city : cities){
			CityObject object = new CityObject();
			String terms[] = city.split("\t");
			if(terms.length == 3){
				/**拼接城市主页的URI*/
				String URI = urlPattern.replaceAll("@", terms[2]);
//				System.out.println("URI == " + URI);
				object.setCityname(terms[0]);
				object.setCitypinyin(terms[1]);
				object.setCitycode(terms[2]);
				object.setCityURI(URI);
			}
			cityObjects.add(object);
		}
		return cityObjects;
	}

}
