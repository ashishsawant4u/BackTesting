package com.stockpattern.demo.strategy;

import java.util.Date;
import java.util.List;

import com.stockpattern.demo.models.StockPrice;

public interface TradeFinderService {
	
	public List<StockPrice> findTradesForMA44Rising90ForDate(Date date);

	public List<StockPrice> findTradesForMARising90ForDateForVolatileStocks(Date date);
}
