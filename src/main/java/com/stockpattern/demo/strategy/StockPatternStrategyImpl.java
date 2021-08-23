package com.stockpattern.demo.strategy;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.stockpattern.demo.indicators.Indicators;
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
		
		
		//Marking Entry
		stockPriceGreenAndSupportWithMARising.forEach(candle->Utils.setBuyEntry(candle));
		
		stockPriceGreenAndSupportWithMARising.forEach(candle->Utils.setExit(candle, stockPriceWithMA));
		
		return stockPriceGreenAndSupportWithMARising;
	}
	

	
	
	private List<StockPrice> prepareMovingAverageByInstrument(String instrument,Date fromDate, int averageScale) 
	{	
		List<StockPrice>  stockPriceListData = Utils.getCandlesData(instrument,fromDate);
		
		List<StockPrice>  stockPriceWithMA = Utils.getCandlesDataForGivenScale(stockPriceListData, averageScale);
	
		return stockPriceWithMA;
	}
	
	
	
	

}
