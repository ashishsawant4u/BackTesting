package com.stockpattern.demo.models;

import java.util.Date;

public class IpoHistoricalData 
{
	private Date listingDate;
	
	private String ipoName;
	
	private float issueSize;
	
	private float QIB;
	
	private float HNI;
	
	private float RII;
	
	private float issuePrice;
	
	private float listingOpenPrice;
	
	private float listingClosePrice;
	
	private float listingGainPecentage;
	
	private float CMP;
	
	private Date futureDate;
	
	private float futurePrice;
	
	private String symbol;
	
	private float futureGainPecentage;

	public Date getListingDate() {
		return listingDate;
	}

	public void setListingDate(Date listingDate) {
		this.listingDate = listingDate;
	}

	public String getIpoName() {
		return ipoName;
	}

	public void setIpoName(String ipoName) {
		this.ipoName = ipoName;
	}

	public float getIssueSize() {
		return issueSize;
	}

	public void setIssueSize(float issueSize) {
		this.issueSize = issueSize;
	}

	public float getQIB() {
		return QIB;
	}

	public void setQIB(float qIB) {
		QIB = qIB;
	}

	public float getHNI() {
		return HNI;
	}

	public void setHNI(float hNI) {
		HNI = hNI;
	}

	public float getRII() {
		return RII;
	}

	public void setRII(float rII) {
		RII = rII;
	}

	public float getIssuePrice() {
		return issuePrice;
	}

	public void setIssuePrice(float issuePrice) {
		this.issuePrice = issuePrice;
	}

	public float getListingOpenPrice() {
		return listingOpenPrice;
	}

	public void setListingOpenPrice(float listingOpenPrice) {
		this.listingOpenPrice = listingOpenPrice;
	}

	public float getListingClosePrice() {
		return listingClosePrice;
	}

	public void setListingClosePrice(float listingClosePrice) {
		this.listingClosePrice = listingClosePrice;
	}

	public float getListingGainPecentage() {
		return listingGainPecentage;
	}

	public void setListingGainPecentage(float listingGainPecentage) {
		this.listingGainPecentage = listingGainPecentage;
	}

	public float getCMP() {
		return CMP;
	}

	public void setCMP(float cMP) {
		CMP = cMP;
	}

	public Date getFutureDate() {
		return futureDate;
	}

	public void setFutureDate(Date futureDate) {
		this.futureDate = futureDate;
	}

	public float getFuturePrice() {
		return futurePrice;
	}

	public void setFuturePrice(float futurePrice) {
		this.futurePrice = futurePrice;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public float getFutureGainPecentage() {
		return futureGainPecentage;
	}

	public void setFutureGainPecentage(float futureGainPecentage) {
		this.futureGainPecentage = futureGainPecentage;
	}

	@Override
	public String toString() {
		return "IpoHistoricalData [listingDate=" + listingDate + ", ipoName=" + ipoName + ", issueSize=" + issueSize
				+ ", QIB=" + QIB + ", HNI=" + HNI + ", RII=" + RII + ", issuePrice=" + issuePrice
				+ ", listingOpenPrice=" + listingOpenPrice + ", listingClosePrice=" + listingClosePrice
				+ ", listingGainPecentage=" + listingGainPecentage + ", CMP=" + CMP + ", futureDate=" + futureDate
				+ ", futurePrice=" + futurePrice + ", symbol=" + symbol + ", futureGainPecentage=" + futureGainPecentage
				+ "]";
	}
	
}
