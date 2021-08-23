package com.stockpattern.demo.models;

public class TradeResults {
	
	private String instrument;
	
	private int tradesCount;
	
	private int targetExistCount;
	
	private int stopLossCount;
	
	private int profitableTrades;

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public int getTradesCount() {
		return tradesCount;
	}

	public void setTradesCount(int tradesCount) {
		this.tradesCount = tradesCount;
	}

	public int getTargetExistCount() {
		return targetExistCount;
	}

	public void setTargetExistCount(int targetExistCount) {
		this.targetExistCount = targetExistCount;
	}

	public int getStopLossCount() {
		return stopLossCount;
	}

	public void setStopLossCount(int stopLossCount) {
		this.stopLossCount = stopLossCount;
	}

	public int getProfitableTrades() {
		return profitableTrades;
	}

	public void setProfitableTrades(int profitableTrades) {
		this.profitableTrades = profitableTrades;
	}

	@Override
	public String toString() {
		return "TradeResults [instrument=" + instrument + ", tradesCount=" + tradesCount + ", targetExistCount="
				+ targetExistCount + ", stopLossCount=" + stopLossCount + ", profitableTrades=" + profitableTrades
				+ "]";
	}

}
