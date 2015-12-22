package com.starunion.jee.fsdiserver.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.starunion.jee.fsdiserver.po.PrePlayInfo;


@Component
public class DaoPrePlayInfo extends DbUtilsTemplate {

	public DaoPrePlayInfo() {

	}

	public List<PrePlayInfo> findAll() {
		List<PrePlayInfo> list = new ArrayList<PrePlayInfo>();
		list = super.find(PrePlayInfo.class, "select * from dis_playlist");
		return list;
	}

	public PrePlayInfo findById(String id) {
		PrePlayInfo info = new PrePlayInfo();
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("select * from dis_playlist where id = \"");
		strBuff.append(id);
		strBuff.append("\"");
		info = super.findFirst(PrePlayInfo.class, strBuff.toString());
		return info;
	}

	public int addPrePlayInfo(PrePlayInfo f){
		Object[] info = new Object[8];
		info[0] = f.getTimestr();
		info[1] = f.getPmode();
		info[2] = f.getTmode();
		info[3] = f.getTimes();
		info[4] = f.getFile();
		info[5] = f.getList();
		info[6] = f.getPlayer();
		info[7] = f.getTkey();
		super.insert(PrePlayInfo.class, 
				"insert into dis_playlist(timestr,pmode,tmode,times,file,list,player,tkey) values(?,?,?,?,?,?,?,?)",info);
		return 0;
	}

	public int delPrePlayInfo(String id) {
		StringBuffer sql = new StringBuffer();
		sql.append("delete from dis_playlist where id = \"");
		sql.append(id);
		sql.append("\"");
		int res = super.update(sql.toString());
		return res;
	}
}
