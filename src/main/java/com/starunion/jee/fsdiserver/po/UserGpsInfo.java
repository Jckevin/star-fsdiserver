package com.starunion.jee.fsdiserver.po;

import org.springframework.stereotype.Repository;

@Repository
public class UserGpsInfo {

	private Integer id;
	private String exten;
	private double lng;
	private double lat;
	
	public UserGpsInfo(){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getExten() {
		return exten;
	}

	public void setExten(String exten) {
		this.exten = exten;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

}
