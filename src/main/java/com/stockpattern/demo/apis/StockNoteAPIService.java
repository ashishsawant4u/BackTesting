package com.stockpattern.demo.apis;

import java.util.Date;
import java.util.List;

import com.stockpattern.demo.models.StockPrice;

public interface StockNoteAPIService {
	
	public String getSessionToken();
	
	public List<StockPrice> findTradesForMA44Rising90ForDate(String symbol,Date date);

}
