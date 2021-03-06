package com.stockpattern.demo.strategy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.stockpattern.demo.indicators.Indicators;
import com.stockpattern.demo.indicators.QuantityPlanner;
import com.stockpattern.demo.indicators.StockConstants;
import com.stockpattern.demo.indicators.Utils;
import com.stockpattern.demo.models.StockPrice;

@Component("stockPatternStrategy")
public class StockPatternStrategyImpl implements StockPatternStrategy {
	
	Logger logger = LoggerFactory.getLogger(StockPatternStrategyImpl.class);

	
	@Override
	public List<StockPrice> backTestMovingAverage(String symbol,Date fromDate,int averageScale) {
		
		//Finding SMA
		//List<StockPrice>  stockPriceWithMA = prepareMovingAverage(fromDate, averageScale);
		List<StockPrice>  stockPriceWithMA = prepareMovingAverageByInstrument(symbol, fromDate, averageScale);
		
		//Finding Support
		stockPriceWithMA.stream().forEach(candle -> candle.setHasSupport(Indicators.hasSupport(candle, StockConstants.SUPPORT_PRICE_DIFFERENCE)));
		
		//Finding Candle Type RED/GREEN
		stockPriceWithMA.stream().forEach(candle -> candle.setGreenCandle(Indicators.isGreenCandleWithGoodBody(candle)));
		
		//Utils.prepareEntryCandles(stockPriceWithMA);
		
		//Consolidating green candles with support
		List<StockPrice>  stockPriceGreenAndSupportWithMA = stockPriceWithMA.stream().filter(candle-> candle.isGreenCandle() && candle.isHasSupport()).collect(Collectors.toList());
		
		//Finding green candle with rising MA and support
		stockPriceGreenAndSupportWithMA.forEach(candle->{
			candle.setMARising(Utils.isRisinngMAForCandle(candle, stockPriceWithMA, StockConstants.RISING_PRICE_MIN_SCALE));
							});
		
		//Consolidating green candles with support and MA Rising
		List<StockPrice>  stockPriceGreenAndSupportWithMARising = stockPriceGreenAndSupportWithMA.stream().filter(candle->candle.isMARising()).collect(Collectors.toList());
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
		//Marking Entry
		stockPriceGreenAndSupportWithMARising.forEach(candle->{
			
			String month =  sdf.format(candle.getMarketDate());
			//Buy only if monthly trade budget not exhausted
			if(null==QuantityPlanner.monthlyTradeAmountTrackerMap.get(month) || QuantityPlanner.monthlyTradeAmountTrackerMap.get(month) < QuantityPlanner.MAX_TRADE_AMOUNT_PER_MONTH)
			{
				Utils.setBuyEntry(candle);
			}
			
		});
		
		List<StockPrice> withingBudgetExecutedTrades = stockPriceGreenAndSupportWithMARising.stream().filter(c->c.isEntry()).collect(Collectors.toList());
		
		withingBudgetExecutedTrades.forEach(candle->Utils.setExit(candle, stockPriceWithMA));
		
		return withingBudgetExecutedTrades;
	}
	

	
	
	private List<StockPrice> prepareMovingAverageByInstrument(String instrument,Date fromDate, int averageScale) 
	{	
		List<StockPrice>  stockPriceListData = Utils.getCandlesData(instrument,fromDate);
		
		List<StockPrice>  stockPriceWithMA = Utils.getCandlesDataForGivenScale(stockPriceListData, averageScale);
	
		return stockPriceWithMA;
	}




	@Override
	public List<StockPrice> backTestMovingAverageForShorting(String symbol, Date fromDate, int averageScale) {
		
		//Finding SMA
		List<StockPrice>  stockPriceWithMA = prepareMovingAverageByInstrument(symbol, fromDate, averageScale);
		
		//Finding Support
		stockPriceWithMA.stream().forEach(candle -> candle.setHasSupport(Indicators.hasSupport(candle, StockConstants.SUPPORT_PRICE_DIFFERENCE)));
		
		//Consolidating red candles with support
		List<StockPrice>  stockPriceRedAndSupportWithMA = stockPriceWithMA.stream().filter(candle-> candle.isRedCandle() && candle.isHasSupport()).collect(Collectors.toList());
		
		//Finding red candle with falling MA and support
		stockPriceRedAndSupportWithMA.forEach(candle->{
			candle.setMARising(Utils.isFallingMAForCandle(candle, stockPriceWithMA, StockConstants.RISING_PRICE_MIN_SCALE) == true ? false : true);
							});
		
		//Consolidating red candles with support and MA Falling
		List<StockPrice>  stockPriceRedAndSupportWithMAFalling = stockPriceRedAndSupportWithMA.stream().filter(candle->!candle.isMARising()).collect(Collectors.toList());
				
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
		//Marking Entry
		stockPriceRedAndSupportWithMAFalling.forEach(candle->{
			
			String month =  sdf.format(candle.getMarketDate());
			//Buy only if monthly trade budget not exhausted
			if(null==QuantityPlanner.monthlyTradeAmountTrackerMap.get(month) || QuantityPlanner.monthlyTradeAmountTrackerMap.get(month) < QuantityPlanner.MAX_TRADE_AMOUNT_PER_MONTH)
			{
				Utils.setShortEntry(candle);
			}
			
		});
		
		List<StockPrice> withingBudgetExecutedTrades = stockPriceRedAndSupportWithMAFalling.stream().filter(c->c.isEntry()).collect(Collectors.toList());
		
		withingBudgetExecutedTrades.forEach(candle->Utils.setShortExit(candle, stockPriceWithMA));
		
		return withingBudgetExecutedTrades;
	}
	
	
	
	

}
