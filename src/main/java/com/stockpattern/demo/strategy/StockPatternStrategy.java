package com.stockpattern.demo.strategy;

import java.util.Date;
import java.util.List;

import com.stockpattern.demo.models.StockPrice;

public interface StockPatternStrategy {
	
	public List<StockPrice> backTestMovingAverage(String symbol,Date fromDate,int averageScale);

}
