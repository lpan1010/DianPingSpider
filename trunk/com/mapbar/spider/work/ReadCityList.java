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
	/**׼��ץȡ�׶�*/
	public ArrayList<CityObject> PrepareSnatch(){
		/**��ȡ�����б��ļ�*/
		ArrayList<String> cities = ReadFile.readFile(ConstantParameters.city_list);
		LOG.info("��ȡ�����б��ļ�,���и��� == "+cities.size());
		/**д��CityObject����*/
		ArrayList<CityObject> cityObjects = CreateCityObject(cities);
		return cityObjects;
	}
	/***
	 * ��������ĳ����б��ļ�������ÿ������ҳ��Ķ���
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
				/**ƴ�ӳ�����ҳ��URI*/
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
