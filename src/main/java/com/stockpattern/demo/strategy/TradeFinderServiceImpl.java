package com.stockpattern.demo.strategy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	@Override
	public List<StockPrice> findTradesForMARising90ForDateForVolatileStocks(Date date)
	{
		
		try 
		{
			List<String> shortlistedVolatileStocks = getShortlistedVolatileStocksForDate(date); 
			
			int averageScale = StockConstants.MOVING_AVERAGE_SCALE;
			 
			 StockConstants.RISING_PRICE_MIN_SCALE = 90;
			 
			//Consolidating green candles with support and MA Rising
			 List<StockPrice>  potentialTradesList = new ArrayList<StockPrice>();
			 
			try 
			{
				 
				 for(String symbol : shortlistedVolatileStocks)
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
		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	private List<String> getShortlistedVolatileStocksForDate(Date date) throws Exception 
	{
		String COMMA_DELIMITER = ",";
		//List<String> shortlistedVolatileStocks = Collections.emptyList();
		//https://www1.nseindia.com/archives/nsccl/volt/CMVOLT_27082021.CSV
		String fileDateStr = new SimpleDateFormat("ddMMyyyy").format(date);
		String fileName = "CMVOLT_"+fileDateStr+".CSV";
		String volatileStockUrl = "https://www1.nseindia.com/archives/nsccl/volt/"+fileName;
		String voliteStockFile = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\Daily_Volatile\\"+fileName;
		Map<String, Double> volatityMap = new HashMap<String, Double>();
		
		List<String> nifty100List = getNifty100();
		
		try 
		 {
			 	URL url = new URL(volatileStockUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
				conn.setRequestProperty("Referer", "https://www1.nseindia.com/products/content/equities/equities/archieve_eq.htm");
				
				int respCode = conn.getResponseCode(); 
				
				if(respCode==403)
				{
					logger.info("403 error for "+volatileStockUrl);
				}
				
				if(respCode!=404)
				{
					BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
					FileOutputStream fileOS = new FileOutputStream(voliteStockFile);
					
					byte data[] = new byte[1024];
					int byteContent;
					while ((byteContent = inputStream.read(data, 0, 1024)) != -1) 
					{
					    fileOS.write(data, 0, byteContent);
					}
					
					logger.info(fileName+" download complete");
					
					
					int count = 0;
					BufferedReader br = new BufferedReader(new FileReader(voliteStockFile));
				    String line;
				    while ((line = br.readLine()) != null) {
				    	
				    	if(count != 0)
				    	{
				    		 	String[] lineData = line.split(COMMA_DELIMITER);
				            	
				    		 	String symbol = lineData[1].replace("\"", "");
				    		 	if(nifty100List.contains(symbol))
				    		 	{
				    		 		volatityMap.put(symbol, new Double(lineData[6].replace("\"", "")));
				    		 	}
				    	}
				       
				    	count++;
				        
				    }
					
				}
				
				Map<String, Double> sortedByVol = volatityMap.entrySet()
		                .stream()
		                .sorted(Map.Entry.comparingByValue())
		                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
				
				return sortedByVol.keySet().stream().collect(Collectors.toList());
				
		 } 
		 catch (Exception e) {
			 logger.info(e.getMessage());
		}
		
		return null;
	}
	
	private List<String> getNifty100() throws Exception 
	{
		String COMMA_DELIMITER = ",";
		
		List<String> nifty100List = new ArrayList<String>();
		
		String nifty100Url = "https://www1.nseindia.com/content/indices/ind_nifty100list.csv";
		
		String nifty100FileLoc = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\ind_nifty100list.csv";
		 try 
		 {
			 	URL url = new URL(nifty100Url);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
				conn.setRequestProperty("Referer", "https://www1.nseindia.com/products/content/equities/equities/archieve_eq.htm");
				
				int respCode = conn.getResponseCode(); 
				
				if(respCode==403)
				{
					logger.info("403 error for "+nifty100Url);
				}
				
				if(respCode!=404)
				{
					BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
					FileOutputStream fileOS = new FileOutputStream(nifty100FileLoc);
					
					byte data[] = new byte[1024];
					int byteContent;
					while ((byteContent = inputStream.read(data, 0, 1024)) != -1) 
					{
					    fileOS.write(data, 0, byteContent);
					}
					
					logger.info("ind_nifty100list.csv download complete");
					
					int count = 0;
					BufferedReader br = new BufferedReader(new FileReader(nifty100FileLoc));
				    String line;
				    while ((line = br.readLine()) != null) {
				    	
				    	if(count != 0)
				    	{
				    		 	String[] lineData = line.split(COMMA_DELIMITER);
				            	
				    		 	nifty100List.add(lineData[2]);	
				    	}
				       
				    	count++;
				        
				    }
				    return nifty100List;
				}
				
		 } 
		 catch (Exception e) {
			 logger.info(e.getMessage());
		}
		 
		 return nifty100List;
	}

}
