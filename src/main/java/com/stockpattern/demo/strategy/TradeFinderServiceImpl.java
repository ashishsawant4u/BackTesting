package com.stockpattern.demo.strategy;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.stockpattern.demo.apis.NseSiteAPIService;
import com.stockpattern.demo.indicators.Indicators;
import com.stockpattern.demo.indicators.QuantityPlanner;
import com.stockpattern.demo.indicators.StockConstants;
import com.stockpattern.demo.indicators.Utils;
import com.stockpattern.demo.models.StockPrice;

@Component("tradeFinderService")
public class TradeFinderServiceImpl implements TradeFinderService {

	Logger logger = LoggerFactory.getLogger(TradeFinderServiceImpl.class);
	
	@Resource(name = "nseSiteAPIService")
	NseSiteAPIService nseSiteAPIService;
	
	@Override
	public List<StockPrice> findTradesForMA44Rising90ForDate(Date date) 
	{
		
		 int averageScale = StockConstants.MOVING_AVERAGE_SCALE;
		 
		 StockConstants.RISING_PRICE_MIN_SCALE = 90;
		 
		//Consolidating green candles with support and MA Rising
		 List<StockPrice>  potentialTradesList = new ArrayList<StockPrice>();
		 
		try 
		{
			List<String> shortListedStocks = getShortlistedMA44Rising90Stocks();
			 
			 for(String symbol : shortListedStocks)
			 {
				 List<StockPrice> candleData = nseSiteAPIService.getNSEDailyEodDataForSymbol(symbol,date);
				 
				 //Finding SMA
				 List<StockPrice>  stockPriceWithMA = Utils.getCandlesDataForGivenScale(candleData, averageScale);
				 
				 StockPrice yesterdayEodCandle = stockPriceWithMA.get(stockPriceWithMA.size()-1);
				 
				 //Finding Support
				 yesterdayEodCandle.setHasSupport(Indicators.hasSupport(yesterdayEodCandle, StockConstants.SUPPORT_PRICE_DIFFERENCE));
				
				 //Finding Candle Type RED/GREEN
				 yesterdayEodCandle.setGreenCandle(Indicators.isGreenCandleWithGoodBody(yesterdayEodCandle));
				 
				 //Finding Is MA Rising
				 yesterdayEodCandle.setMARising(Utils.isRisinngMAForCandle(yesterdayEodCandle, stockPriceWithMA, StockConstants.RISING_PRICE_MIN_SCALE));
				 
				 //Marking Entry
				 if(yesterdayEodCandle.isHasSupport() && yesterdayEodCandle.isGreenCandle() && yesterdayEodCandle.isMARising())
				 {
					 SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
					 
					 String month =  sdf.format(yesterdayEodCandle.getMarketDate());
					 
					 Utils.setBuyEntry(yesterdayEodCandle);
					
					 potentialTradesList.add(yesterdayEodCandle);
				 }
				 
			 }
		} 
		catch (Exception e) 
		 {
			logger.info("TradeFinderService findTradesForMA44Rising90ForDate Failed "+e);
		}
		 
			
		return potentialTradesList;
	}
	
	private List<String> getShortlistedMA44Rising90Stocks() throws Exception 
	{
		  File shortlistedile = ResourceUtils.getFile("classpath:Shortlisted-Stocks-MA-"+StockConstants.MOVING_AVERAGE_SCALE+".txt");
		  
		  List<String> lines = Collections.emptyList();
		  lines = Files.readAllLines(Paths.get(shortlistedile.getPath()));
		  
		  //lines.forEach(l->logger.info(l));
		  return lines;
	}

}
