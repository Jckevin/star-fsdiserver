package com.starunion.jee.fsdiserver.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.starunion.jee.fsdiserver.po.UserGpsInfo;


@Component
public class DaoUserGpsInfo extends DbUtilsTemplate {

	public List<UserGpsInfo> findAll() {
		List<UserGpsInfo> list = new ArrayList<UserGpsInfo>();
		list = super.find(UserGpsInfo.class, "select * from dis_exten_gis order by id");
		return list;
	}

	public UserGpsInfo findByNumber(String number) {
		UserGpsInfo info = new UserGpsInfo();
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("select * from dis_exten_gis where exten = \"");
		strBuff.append(number);
		strBuff.append("\"");
		info = super.findFirst(UserGpsInfo.class, strBuff.toString());
		return info;
	}
	
	public int addExtenGps(String number,String lng,String lat) {
		StringBuffer sql = new StringBuffer();
		sql.append("insert into dis_exten_gis (exten,lng,lat) values (");
		sql.append(number).append(",");
		sql.append(lng).append(",");
		sql.append(lat);
		sql.append(")");
		int res = super.update(sql.toString());
		return res;
	}
	
	public int delExtenGps(String number) {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from dis_exten_gis where exten =");
		sql.append(number);
		int res = super.update(sql.toString());
		return res;
	}
	
	public int updateGpsByNumber(String number,String lng,String lat){
		StringBuffer sql = new StringBuffer();
		sql.append("update dis_exten_gis set lng = ");
		sql.append(lng);
		sql.append(",lat = ");
		sql.append(lat);
		sql.append(" where exten = \"");
		sql.append(number);
		sql.append("\"");
		
		int res = super.update(sql.toString());
		return res;
		
	}

}
