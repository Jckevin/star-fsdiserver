package com.starunion.jee.fsdiserver.po;

import org.springframework.stereotype.Repository;

@Repository
public class UserSip {
	private Integer id;
	private String number;
	private String password;
	private String name;
	private String privilege;
	private String vmpass;
	private String record;
	private String department;
	private String oname;
	private String onum;
	private String lines;
	private Integer vmopen;
	private String alltrans;
	private Integer type;
	private Integer trunknum;
	
	public UserSip(){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}

	public String getVmpass() {
		return vmpass;
	}

	public void setVmpass(String vmpass) {
		this.vmpass = vmpass;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getOname() {
		return oname;
	}

	public void setOname(String oname) {
		this.oname = oname;
	}

	public String getOnum() {
		return onum;
	}

	public void setOnum(String onum) {
		this.onum = onum;
	}

	public String getLines() {
		return lines;
	}

	public void setLines(String lines) {
		this.lines = lines;
	}

	public Integer getVmopen() {
		return vmopen;
	}

	public void setVmopen(Integer vmopen) {
		this.vmopen = vmopen;
	}

	public String getAlltrans() {
		return alltrans;
	}

	public void setAlltrans(String alltrans) {
		this.alltrans = alltrans;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getTrunknum() {
		return trunknum;
	}

	public void setTrunknum(Integer trunknum) {
		this.trunknum = trunknum;
	}
	
}
