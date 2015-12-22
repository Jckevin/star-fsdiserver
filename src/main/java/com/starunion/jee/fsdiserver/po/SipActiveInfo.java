package com.starunion.jee.fsdiserver.po;

import org.springframework.stereotype.Repository;

@Repository
public class SipActiveInfo {

	private String sipNumber;
	private String status;
	private String ownUuid;
	private String otherUuid;
	private String callStatus;
	private String callType;
	private String callWith;
	
	public SipActiveInfo(){
		
	}

	public String getSipNumber() {
		return sipNumber;
	}

	public void setSipNumber(String sipNumber) {
		this.sipNumber = sipNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(String callStatus) {
		this.callStatus = callStatus;
	}

	public String getOwnUuid() {
		return ownUuid;
	}

	public void setOwnUuid(String ownUuid) {
		this.ownUuid = ownUuid;
	}

	public String getOtherUuid() {
		return otherUuid;
	}

	public void setOtherUuid(String otherUuid) {
		this.otherUuid = otherUuid;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public String getCallWith() {
		return callWith;
	}

	public void setCallWith(String callWith) {
		this.callWith = callWith;
	}
	
	
}
