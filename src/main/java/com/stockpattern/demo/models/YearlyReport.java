package com.stockpattern.demo.models;

import java.util.Date;

public class YearlyReport 
{
	private Date year;
	
	private float investment;
	
	private float profitAndLoss;
	
	private float percentageGain;
	
	private long tradesCount;

	public Date getYear() {
		return year;
	}

	public void setYear(Date year) {
		this.year = year;
	}

	public float getInvestment() {
		return investment;
	}

	public void setInvestment(float investment) {
		this.investment = investment;
	}

	public float getProfitAndLoss() {
		return profitAndLoss;
	}

	public void setProfitAndLoss(float profitAndLoss) {
		this.profitAndLoss = profitAndLoss;
	}

	public float getPercentageGain() {
		return percentageGain;
	}

	public void setPercentageGain(float percentageGain) {
		this.percentageGain = percentageGain;
	}

	public long getTradesCount() {
		return tradesCount;
	}

	public void setTradesCount(long tradesCount) {
		this.tradesCount = tradesCount;
	}

	@Override
	public String toString() {
		return "YearlyReport [year=" + year + ", investment=" + investment + ", profitAndLoss=" + profitAndLoss
				+ ", percentageGain=" + percentageGain + ", tradesCount=" + tradesCount + "]";
	}
	
}
