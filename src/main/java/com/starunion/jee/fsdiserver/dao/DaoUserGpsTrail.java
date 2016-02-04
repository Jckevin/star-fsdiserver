package com.starunion.jee.fsdiserver.dao;

import java.math.BigDecimal;
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
	
	public List<UserGpsTrail> findByNumberTime(String number,String tsStart,String tsEnd) {
		List<UserGpsTrail> infoList = new ArrayList<UserGpsTrail>();
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("select * from dis_exten_gistrail where exten = \"");
		strBuff.append(number);
		strBuff.append("\" and time between '");
		strBuff.append(tsStart);
		strBuff.append("' and '");
		strBuff.append(tsEnd);
		strBuff.append("'");
		infoList = super.find(UserGpsTrail.class, strBuff.toString());
		return infoList;
	}
	
	public int addExtenGpsTrail(String number,String lng,String lat) {
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
	public int batchInsertGps(String number){
		Date date = new Date();
		String currT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		System.out.println("====>start mysql transaction time = "+currT);
		double d1 = 118.795;
		double d2 = 31.984;
		for(int i=0;i<200;i++){
			BigDecimal bg1 = new BigDecimal(d1);  
	        double f1 = bg1.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
	        BigDecimal bg2 = new BigDecimal(d2);  
	        double f2 = bg2.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
			String s1 = String.valueOf(f1);
			String s2 = String.valueOf(f2);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			addExtenGpsTrail(number,s1,s2);
			d1-=0.01;
			d2+=0.01;
		}
		
//		StringBuffer sql = new StringBuffer();
//		sql.append("insert into dis_exten_gistrail");
//		sql.append("(exten,lng,lat)");
//		sql.append("values(?,?,?)");
		
		
//		Object[][] arr2 = new Object[200][3];
//		double d1 = 118.795;
//		double d2 = 31.984;
//		
//		for(int i=0;i<200;i++){
//			arr2[i][0] = number;
//			arr2[i][1] = d1;
//			arr2[i][2] = d2;
//			
//			d1+=0.01;
//			d2-=0.01;
//		}
//		
//		int[] res = super.batchUpdate(sql.toString(),arr2);
		double f = 139.3213134321414;
		BigDecimal bg = new BigDecimal(f);  
        double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.println("=========>"+f1);
		Date date2 = new Date();
		String currT2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date2);
		System.out.println("====>end mysql transaction time = "+currT2);
		return 0;
		
	}
	
}
