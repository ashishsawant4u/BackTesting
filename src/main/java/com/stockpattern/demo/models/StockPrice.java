package com.stockpattern.demo.models;

import java.util.Date;

public class StockPrice {

	private String symbol;
	
	private Date marketDate;
	
	private float openPrice;

	private float highPrice;

	private float lowPrice;
	
	private float closePrice;
	
	private float movingAverage;
	
	private boolean hasSupport;
	
	private String tradeResult;
	
	private boolean entry;
	
	private boolean exit;
	
	private String orderDetails;
	
	private boolean isGreenCandle;
	
	private boolean isRedCandle;
	
	private boolean isMARising;
	
	private float pnlAmount;
	
	private float movingAverage200;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Date getMarketDate() {
		return marketDate;
	}

	public void setMarketDate(Date marketDate) {
		this.marketDate = marketDate;
	}

	public float getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(float openPrice) {
		this.openPrice = openPrice;
	}

	public float getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(float highPrice) {
		this.highPrice = highPrice;
	}

	public float getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(float lowPrice) {
		this.lowPrice = lowPrice;
	}

	public float getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(float closePrice) {
		this.closePrice = closePrice;
	}

	public float getMovingAverage() {
		return movingAverage;
	}

	public void setMovingAverage(float movingAverage) {
		this.movingAverage = movingAverage;
	}

	public boolean isHasSupport() {
		return hasSupport;
	}

	public void setHasSupport(boolean hasSupport) {
		this.hasSupport = hasSupport;
	}

	public String getTradeResult() {
		return tradeResult;
	}

	public void setTradeResult(String tradeResult) {
		this.tradeResult = tradeResult;
	}

	public boolean isEntry() {
		return entry;
	}

	public void setEntry(boolean entry) {
		this.entry = entry;
	}

	public boolean isExit() {
		return exit;
	}

	public void setExit(boolean exit) {
		this.exit = exit;
	}

	public String getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(String orderDetails) {
		this.orderDetails = orderDetails;
	}

	public boolean isGreenCandle() {
		return isGreenCandle;
	}

	public void setGreenCandle(boolean isGreenCandle) {
		this.isGreenCandle = isGreenCandle;
	}

	public boolean isRedCandle() {
		return isRedCandle;
	}

	public void setRedCandle(boolean isRedCandle) {
		this.isRedCandle = isRedCandle;
	}

	public boolean isMARising() {
		return isMARising;
	}

	public void setMARising(boolean isMARising) {
		this.isMARising = isMARising;
	}

	public float getPnlAmount() {
		return pnlAmount;
	}

	public void setPnlAmount(float pnlAmount) {
		this.pnlAmount = pnlAmount;
	}

	public float getMovingAverage200() {
		return movingAverage200;
	}

	public void setMovingAverage200(float movingAverage200) {
		this.movingAverage200 = movingAverage200;
	}

	@Override
	public String toString() {
		return "StockPrice [symbol=" + symbol + ", marketDate=" + marketDate + ", openPrice=" + openPrice
				+ ", highPrice=" + highPrice + ", lowPrice=" + lowPrice + ", closePrice=" + closePrice
				+ ", movingAverage=" + movingAverage + ", hasSupport=" + hasSupport + ", tradeResult=" + tradeResult
				+ ", entry=" + entry + ", exit=" + exit + ", orderDetails=" + orderDetails + ", isGreenCandle="
				+ isGreenCandle + ", isRedCandle=" + isRedCandle + ", isMARising=" + isMARising + ", pnlAmount="
				+ pnlAmount + ", movingAverage200=" + movingAverage200 + "]";
	}

	
	
}
