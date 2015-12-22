package com.starunion.jee.fsdiserver.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.starunion.jee.fsdiserver.po.IntercomMember;


@Component
public class DaoIntercomMember extends DbUtilsTemplate {
	public List<IntercomMember> findAllByGrpNumber(String name) {
		List<IntercomMember> info = new ArrayList<IntercomMember>();
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("select * from conf_intercom_exten where name = \"");
		strBuff.append(name);
		strBuff.append("\"");
		info = super.find(IntercomMember.class, strBuff.toString());
		return info;
	}
}
