package com.stockpattern.demo.indicators;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stockpattern.demo.models.StockPrice;
import com.stockpattern.demo.models.TradeEntryData;

public class QuantityPlanner {

	private static final Logger logger = LoggerFactory.getLogger(QuantityPlanner.class);
	
	static float BALANCE_AMOUNT = 50000.00F;
	
	public static float MAX_TRADE_AMOUNT_PER_MONTH = 30001.00F;
	
	static float MAX_AFFORDABLE_LOSS_AMOUNT = 50000.00F;
	
	static int NUMBER_OF_TRADES_CAN_GO_WRONG = 20;
	
	static float MAX_AMOUNT_PER_TRADE = 10000.00F;
	
	static float MAX_RISK_PER_TRADE = MAX_AFFORDABLE_LOSS_AMOUNT / NUMBER_OF_TRADES_CAN_GO_WRONG;
	
	public static Map<String,Float> monthlyTradeAmountTrackerMap = new HashMap<String, Float>();
	
	public static int getQuantity(StockPrice entryCandle)
	{
		TradeEntryData tradeEntry =  entryCandle.getTradeEntry();
		
		int quantity = (int) (   Math.round(MAX_RISK_PER_TRADE) / Math.round(tradeEntry.getLossPerUnit())    );
		
		float investmentAmount = tradeEntry.getBuyPrice() * Math.round(quantity);
		
		if(investmentAmount > MAX_AMOUNT_PER_TRADE)
		{
			int tempQty = (int) (MAX_AMOUNT_PER_TRADE / tradeEntry.getBuyPrice());
			quantity = (tempQty<1) ? 0 : tempQty;
			investmentAmount = tradeEntry.getBuyPrice() * Math.round(quantity);
		}
		
		//quantity=1;
		
		return quantity;
	} 
	
	public static void trackTradeAmountForMonth(StockPrice trade)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
		String month = sdf.format(trade.getMarketDate());
		
		
		if(!monthlyTradeAmountTrackerMap.containsKey(month))
		{
			monthlyTradeAmountTrackerMap.put(month, trade.getTradeEntry().getInvestment());
		}
		else
		{
			monthlyTradeAmountTrackerMap.put(month, monthlyTradeAmountTrackerMap.get(month) + trade.getTradeEntry().getInvestment());
		}
		
	}
	
	
}
