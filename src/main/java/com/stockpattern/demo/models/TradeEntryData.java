package com.stockpattern.demo.models;

import java.util.Date;

public class TradeEntryData {
	
	float buyPrice;
	
	float stopLossPrice;
	
	float targetPrice;

	float lossPerUnit;
	
	int quantity;
	
	long tradeDuration;
	
	Date tradeExitDate;
	
	float investment;
	
	String tradeStatus;

	public float getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(float buyPrice) {
		this.buyPrice = buyPrice;
	}

	public float getStopLossPrice() {
		return stopLossPrice;
	}

	public void setStopLossPrice(float stopLossPrice) {
		this.stopLossPrice = stopLossPrice;
	}

	public float getTargetPrice() {
		return targetPrice;
	}

	public void setTargetPrice(float targetPrice) {
		this.targetPrice = targetPrice;
	}

	public float getLossPerUnit() {
		return lossPerUnit;
	}

	public void setLossPerUnit(float lossPerUnit) {
		this.lossPerUnit = lossPerUnit;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public long getTradeDuration() {
		return tradeDuration;
	}

	public void setTradeDuration(long tradeDuration) {
		this.tradeDuration = tradeDuration;
	}

	public Date getTradeExitDate() {
		return tradeExitDate;
	}

	public void setTradeExitDate(Date tradeExitDate) {
		this.tradeExitDate = tradeExitDate;
	}

	public float getInvestment() {
		return investment;
	}

	public void setInvestment(float investment) {
		this.investment = investment;
	}

	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	@Override
	public String toString() {
		return "TradeEntryData [buyPrice=" + buyPrice + ", stopLossPrice=" + stopLossPrice + ", targetPrice="
				+ targetPrice + ", lossPerUnit=" + lossPerUnit + ", quantity=" + quantity + ", tradeDuration="
				+ tradeDuration + ", tradeExitDate=" + tradeExitDate + ", investment=" + investment + ", tradeStatus="
				+ tradeStatus + "]";
	}
	
}
