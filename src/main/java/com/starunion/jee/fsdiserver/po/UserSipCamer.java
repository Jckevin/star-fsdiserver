package com.starunion.jee.fsdiserver.po;

public class UserSipCamer {

	private Integer id;
	private String name;
	private String ip;
	private Integer type;
	private String username;
	private String password;
	private Integer base_id;
	private String port;
	
	public UserSipCamer(){
		
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getBase_id() {
		return base_id;
	}

	public void setBase_id(Integer base_id) {
		this.base_id = base_id;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}
