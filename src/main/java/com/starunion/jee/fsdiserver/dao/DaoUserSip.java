package com.starunion.jee.fsdiserver.dao;

import java.util.ArrayList;
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
		list = super.find(UserSip.class, "select * from sip_users where alltrans != \"\"");
		/**
		 * directly use Object[] params more beautiful, but can't work.
		 * list = super.find(UserSip.class, select * from sip_users where alltrans = \"800\""); OK 
		 * list = super.find(UserSip.class, "select * from sip_users where ? != ?", params); ERROR
		 * list = super.find(UserSip.class, "select * from sip_users where name = ? and value = ", params); OK
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
	
	public int updateTransByNumber(String number,String transnumber){
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

}
