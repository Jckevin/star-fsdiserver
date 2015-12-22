package com.starunion.jee.fsdiserver.po;

import org.springframework.stereotype.Repository;

@Repository
public class UserSipBlack {

	private Integer id;
	private String num;
	
	public UserSipBlack(){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	
}
