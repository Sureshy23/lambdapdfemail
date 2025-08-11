package com.tradefinance.lambda_pdf_email;

import java.util.List;

public class PdfEmailRequest {

	private String date;
    private String tfNo;
    private String cpr;
    private String customerID;
    private String email;
    private String header;
    private String customerName;
    private String sender;
    private String receiver;

    private List<String> msgColumn1;
    private List<String> msgColumn2;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTfNo() {
		return tfNo;
	}
	public void setTfNo(String tfNo) {
		this.tfNo = tfNo;
	}
	public String getCpr() {
		return cpr;
	}
	public void setCpr(String cpr) {
		this.cpr = cpr;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public List<String> getMsgColumn1() {
		return msgColumn1;
	}
	public void setMsgColumn1(List<String> msgColumn1) {
		this.msgColumn1 = msgColumn1;
	}
	public List<String> getMsgColumn2() {
		return msgColumn2;
	}
	public void setMsgColumn2(List<String> msgColumn2) {
		this.msgColumn2 = msgColumn2;
	}
	
	
	
	
}
