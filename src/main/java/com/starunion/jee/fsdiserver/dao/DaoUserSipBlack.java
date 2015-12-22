package com.starunion.jee.fsdiserver.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.starunion.jee.fsdiserver.po.UserSipBlack;

@Component
public class DaoUserSipBlack extends DbUtilsTemplate {
	public List<UserSipBlack> findAll() {
		List<UserSipBlack> list = new ArrayList<UserSipBlack>();
		list = super.find(UserSipBlack.class, "select * from sip_blacklist");
		return list;
	}
	
	public int addUserSip(String number){
		StringBuffer sql = new StringBuffer();
		sql.append("insert into sip_blacklist (num) values (");
		sql.append(number);
		sql.append(")");
		int res = super.update(sql.toString());
		return res;
	}
	
	public int delUserSip(String number){
		StringBuffer sql = new StringBuffer();
		sql.append("delete from sip_blacklist where num = \"");
		sql.append(number);
		sql.append("\"");
		int res = super.update(sql.toString());
		return res;
	}
}
