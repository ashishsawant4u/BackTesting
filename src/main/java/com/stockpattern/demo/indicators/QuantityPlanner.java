package com.stockpattern.demo.indicators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stockpattern.demo.models.StockPrice;
import com.stockpattern.demo.models.TradeEntryData;

public class QuantityPlanner {

	private static final Logger logger = LoggerFactory.getLogger(QuantityPlanner.class);
	
	static double BALANCE_AMOUNT = 50000.00;
	
	static double MAX_AFFORDABLE_LOSS_AMOUNT = 50000.00;
	
	static int NUMBER_OF_TRADES_CAN_GO_WRONG = 20;
	
	static double MAX_AMOUNT_PER_TRADE = 1000.00;
	
	static double MAX_RISK_PER_TRADE = MAX_AFFORDABLE_LOSS_AMOUNT / NUMBER_OF_TRADES_CAN_GO_WRONG;
	
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
	
	
}
