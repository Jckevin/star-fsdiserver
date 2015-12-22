package com.starunion.jee.fsdiserver.po;

import org.springframework.stereotype.Repository;

@Repository
public class IntercomMember {

	private Integer id;
	private String name;
	private String exten;
	private Integer priotity;
	
	public IntercomMember(){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExten() {
		return exten;
	}

	public void setExten(String exten) {
		this.exten = exten;
	}

	public Integer getPriotity() {
		return priotity;
	}

	public void setPriotity(Integer priotity) {
		this.priotity = priotity;
	}
}
