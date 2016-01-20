package com.starunion.jee.fsdiserver.dao;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;
import com.starunion.jee.fsdiserver.po.UserGpsTrail;

@Component
public class DaoUserGpsTrail extends DbUtilsTemplate {
	public List<UserGpsTrail> findAll() {
		List<UserGpsTrail> list = new ArrayList<UserGpsTrail>();
		list = super.find(UserGpsTrail.class, "select * from dis_exten_gistrail order by id");
		return list;
	}

	public List<UserGpsTrail> findByNumber(String number) {
		List<UserGpsTrail> infoList = new ArrayList<UserGpsTrail>();
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("select * from dis_exten_gistrail where exten = \"");
		strBuff.append(number);
		strBuff.append("\"");
		infoList = super.find(UserGpsTrail.class, strBuff.toString());
		return infoList;
	}
	
	public int addExtenGpsTrail(String number,String lng,String lat) {
		Date date = new Date();
		String currT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		System.out.println("===========>"+currT);
		Timestamp ts_date = Timestamp.valueOf(currT);
		System.out.println("ts:===========>"+ts_date.toString());
		StringBuffer sql = new StringBuffer();
		sql.append("insert into dis_exten_gistrail (exten,lng,lat) values (");
		sql.append(number).append(",");
		sql.append(lng).append(",");
//		sql.append(lat).append(",");
		sql.append(lat);
		sql.append(")");
		int res = super.update(sql.toString());
		return res;
	}
	
	public int delExtenGps(String number) {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from dis_exten_gistrail where exten =");
		sql.append(number);
		int res = super.update(sql.toString());
		return res;
	}
	
}
