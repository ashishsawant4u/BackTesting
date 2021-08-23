package com.stockpattern.demo.strategy;

import java.text.SimpleDateFormat;
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

	int stopLossCount = 0;
	
	int targetExitCount = 0;
	
	float totalStopLoss = 0.0F;
	
	float totalTargetExit = 0.0F;
	

	
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
		
		//stockPriceGreenAndSupportWithMARising.forEach(candle->logger.info(candle.toString()));
		
		//Find Entry
			//checkBuyEntry(stockPriceWithMA);
		
		//checkBuyEntryWhenPriceBelowMA(stockPriceWithMA);
		
			//checkProfitAndLoss(symbol);
		
		return stockPriceGreenAndSupportWithMARising;
	}
	

	private void checkProfitAndLoss(String instrument) 
	{
		System.out.println("=============================================================================================================");
		System.out.println("STOP LOSS COUNT "+stopLossCount+" TARGET EXIT COUNT "+targetExitCount);
		System.out.println("TOTAL STOP LOSS AMOUNT "+totalStopLoss+" TOTAL TARGET EXIT AMOUNT "+totalTargetExit);
		System.out.println("TOTAL P/L "+ (totalTargetExit-totalStopLoss));
		System.out.println("=============================================================================================================");
	
		String str = instrument+"|"+stopLossCount+"|"+targetExitCount
		        + "|"+totalStopLoss+"|"+totalTargetExit
		        + "|"+ (totalTargetExit-totalStopLoss);
		
		logger.info(str);
		 
		 stopLossCount = 0;
			
	     targetExitCount = 0;
			
		 totalStopLoss = 0.0F;
			
		totalTargetExit = 0.0F;
		
	}

	private void checkBuyEntry(List<StockPrice> stockPriceWithMA) 
	{
		for(int i=0;i<stockPriceWithMA.size();i++)
		{
			List<Float> highPriceList = stockPriceWithMA.subList(0, i).stream().map(stock->stock.getHighPrice()).collect(Collectors.toList());
			List<Float> maList = stockPriceWithMA.subList(0, i).stream().map(stock->stock.getMovingAverage()).collect(Collectors.toList());
			
			StockPrice candle = stockPriceWithMA.get(i);
			
			//if(candle.isHasSupport() && Indicators.isGreenCandle(candle) && Indicators.isGreenCandleWithGoodBody(candle) && Indicators.isRising(maList, StockConstants.RISING_PRICE_MIN_SCALE))
			if(Indicators.isGreenCandle(candle) &&  candle.isHasSupport() && Indicators.isBullish(highPriceList,maList, StockConstants.RISING_PRICE_MIN_SCALE) && Indicators.isGreenCandleWithGoodBody(candle))
			{	
				candle.setEntry(true);
				float buyPrice = candle.getHighPrice()+1;
				float stopLoss = candle.getLowPrice()-1;
				float targetPrice = buyPrice+((buyPrice-stopLoss)*2);
				candle.setOrderDetails("BUY : "+buyPrice+" STOPLOSS : "+stopLoss+" TARGET "+targetPrice);
				System.out.println("ENTRY CANDLE >> "+candle);
				StockPrice exitCandle = checkBuyExit(candle,stockPriceWithMA.subList(i+1, stockPriceWithMA.size()));
				if(null!=exitCandle)
				{
					candle.setTradeResult(exitCandle.getTradeResult());
					System.out.println("EXIT  >> "+exitCandle.getTradeResult());
					
					String tradeLog = new SimpleDateFormat("dd-MMM-yyyy").format(candle.getMarketDate())+"|"+candle.getSymbol()
									  +"| BUY | "+buyPrice+"|STOPLOSS | "+stopLoss+"|TARGET | "+targetPrice
									  +"| EXIT | "+exitCandle.getTradeResult();
					
					//logger.info(tradeLog);
				}
			}
		}
	}
	
	private List<StockPrice> prepareMovingAverageByInstrument(String instrument,Date fromDate, int averageScale) 
	{	
		List<StockPrice>  stockPriceListData = Utils.getCandlesData(instrument,fromDate);
		
		List<StockPrice>  stockPriceWithMA = Utils.getCandlesDataForGivenScale(stockPriceListData, averageScale);
	
		return stockPriceWithMA;
	}
	
	
	private StockPrice checkBuyExit(StockPrice entryCandle,List<StockPrice> candlesAfterEntry)
	{
		List<StockPrice>  stockPriceList = candlesAfterEntry;
		
		float buyPrice = entryCandle.getHighPrice()+1;
		float stopLossPrice = entryCandle.getLowPrice()-1;
		float targetPrice = buyPrice+((buyPrice-stopLossPrice)*2);
		
		
		for(StockPrice candle : stockPriceList)
		{
			if(candle.getLowPrice() <= targetPrice && targetPrice <=candle.getHighPrice())
			{
				entryCandle.setExit(true);
				entryCandle.setTradeResult("TARGET_EXIT ON "+new SimpleDateFormat("dd-MMM-yyyy").format(candle.getMarketDate())+"|P/L|"+ (targetPrice-buyPrice));
				targetExitCount++;
				totalTargetExit =  totalTargetExit + (targetPrice-buyPrice);
				break;
			}
			if(candle.getLowPrice() <= stopLossPrice && stopLossPrice <=candle.getHighPrice())
			{
				entryCandle.setExit(true);
				entryCandle.setTradeResult("STOP_LOSS ON "+new SimpleDateFormat("dd-MMM-yyyy").format(candle.getMarketDate())+"|P/L|-"+ (buyPrice-stopLossPrice));
				stopLossCount++;
				totalStopLoss =  totalStopLoss + (buyPrice-stopLossPrice);
				break;
			}
			
		}
		
		return entryCandle;
	}
	

}
