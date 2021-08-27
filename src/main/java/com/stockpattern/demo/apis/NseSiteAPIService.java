package com.stockpattern.demo.apis;

import java.util.Date;
import java.util.List;

import com.stockpattern.demo.models.StockPrice;

public interface NseSiteAPIService {

	public List<StockPrice> getNSEDailyEodDataForSymbol(String symbol,Date date);
	
	public String downloadHistoricalDailyEOD();
	
	public String updateHistoricalDailyEOD();
}
