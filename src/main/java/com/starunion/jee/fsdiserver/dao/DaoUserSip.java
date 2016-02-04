package com.starunion.jee.fsdiserver.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.starunion.jee.fsdiserver.po.UserSip;

@Component
public class DaoUserSip extends DbUtilsTemplate {

	public List<UserSip> findAll() {
		List<UserSip> list = new ArrayList<UserSip>();
		list = super.find(UserSip.class, "select * from sip_users order by id");
		return list;
	}

	public List<UserSip> findAllTransUsers() {
		List<UserSip> list = new ArrayList<UserSip>();
		list = super.find(UserSip.class,
				"select * from sip_users where alltrans != \"\"");
		/**
		 * directly use Object[] params more beautiful, but can't work. list =
		 * super.find(UserSip.class, select * from sip_users where alltrans =
		 * \"800\""); OK list = super.find(UserSip.class,
		 * "select * from sip_users where ? != ?", params); ERROR list =
		 * super.find(UserSip.class,
		 * "select * from sip_users where name = ? and value = ", params); OK
		 */
		return list;
	}

	public UserSip findByNumber(String name) {
		UserSip info = new UserSip();
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("select * from sip_users where name = \"");
		strBuff.append(name);
		strBuff.append("\"");
		info = super.findFirst(UserSip.class, strBuff.toString());
		return info;
	}

	public int updateTransByNumber(String number, String transnumber) {
		StringBuffer sql = new StringBuffer();
		sql.append("update sip_users set alltrans = \"");
		sql.append(transnumber);
		sql.append("\"");
		sql.append(" where number = \"");
		sql.append(number);
		sql.append("\"");

		int res = super.update(sql.toString());
		return res;

	}

	public int batchInsertUser(String number,String passMode,String passwd,String times){
		Date date = new Date();
		String currT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		System.out.println("====>start mysql transaction time = "+currT);
		StringBuffer sql = new StringBuffer();
		sql.append("insert into sip_users");
		sql.append("(number,password,name,privilege,vmpass,record,department,");
		sql.append("vmopen,alltrans,type,trunknum)");
		sql.append("values(?,?,?,?,?,?,?,?,?,?,?)");
		int len = number.length();
		int bnum;
		if(len<8){
			bnum = Integer.parseInt(number);
		}else{
			//split the two long String,change the last 3 bits.
			bnum = Integer.parseInt(number);
		}
		Object[][] arr2 = new Object[Integer.parseInt(times)][11];
		for(int i=0;i<Integer.parseInt(times);i++){
			arr2[i][0] = String.valueOf(bnum);
			if(passMode.equals("0")){
				arr2[i][1] = String.valueOf(bnum);	
			}else{
				arr2[i][1] = passwd;
			}
			arr2[i][2] = String.valueOf(bnum);
			arr2[i][3] = "local";
			arr2[i][4] = "1234";
			arr2[i][5] = "0";
			arr2[i][6] = "develop";
			arr2[i][7] = 0;
			arr2[i][8] = "";
			arr2[i][9] = 2;
			arr2[i][10] = 0;
			
			bnum++;
		}
		
		int[] res = super.batchUpdate(sql.toString(),arr2);
		Date date2 = new Date();
		String currT2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date2);
		System.out.println("====>end mysql transaction time = "+currT2);
		
//		Column count doesn't match value count at row 1 Query: 
//		insert into sip_users
//		(number,password,name,privilege,vmpass,record,department,vmopen,alltrans,type,trunknum)values(?,?,?,?,?,?,?,?,?,?,?,?) 
//		Parameters: [
//		[10000, 10000, 10000, local, 1234, 0, develop, 0, , 2, 0, null], 
//		[10001, 10001, 10001, local, 1234, 0, develop, 0, , 2, 0, null], 
//		[10002, 10002, 10002, local, 1234, 0, develop, 0, , 2, 0, null], 
//		[10003, 10003, 10003, local, 1234, 0, develop, 0, , 2, 0, null], 
//		[10004, 10004, 10004, local, 1234, 0, develop, 0, , 2, 0, null],
		return 0;
		
	}
	
}
