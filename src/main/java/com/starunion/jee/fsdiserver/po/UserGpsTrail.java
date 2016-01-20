package com.starunion.jee.fsdiserver.po;

import java.sql.Timestamp;

import org.springframework.stereotype.Repository;

@Repository
public class UserGpsTrail {
	private Integer id;
	private String exten;
	private double lng;
	private double lat;
	private Timestamp time;
	
	public UserGpsTrail(){
		
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

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}	
}
