package com.stockpattern.demo.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.stockpattern.demo.indicators.StockConstants;
import com.stockpattern.demo.models.StockPrice;
import com.stockpattern.demo.models.TradeResults;
import com.stockpattern.demo.strategy.StockPatternStrategy;


@Controller
@RequestMapping("/stockpattern")
public class LandingPageController {
	
	
	Logger logger = LoggerFactory.getLogger(LandingPageController.class);


	@Resource(name = "stockPatternStrategy")
	StockPatternStrategy stockPatternStrategy;
	
	Map<String, Double> montlyUnrealisedProfitTotalMap = new ConcurrentHashMap<String, Double>();
	Map<String, List<Float>> montlyUnrealisedProfitMap = new ConcurrentHashMap<String, List<Float>>();
	
	Map<String, Double> yearlyUnrealisedProfitTotalMap = new ConcurrentHashMap<String, Double>();
	Map<String, List<Float>> yearlyUnrealisedProfitMap = new ConcurrentHashMap<String, List<Float>>();

	
	@RequestMapping("/home")
	public String getLandingPage(Model model) throws Exception
	{
		logger.info("STOCKPATTERN.......");
		
		String string = "Jan 17, 2011";	
		DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		Date fromDate = format.parse(string);
		
		//model.addAttribute("stockPriceList", stockPatternStrategy.getMovingAverage(fromDate, 44));
		
		//model.addAttribute("stockPriceList", stockPatternStrategy.backTestMovingAverage("",fromDate, 44));
		
		//model.addAttribute("stockPriceList",stockPatternStrategy.backTestMovingAverage("ADANIPORTS",fromDate, 44));
		
		
		
		 
		//allSymbolTXT(fromDate);
		allSymbolCSV(null); 
		
		return "landingPage";
	}
	
	@RequestMapping("/scale/{scale}/{symbol}/{sma}")
	public String getLandingPage2(@PathVariable("scale") String scale,@PathVariable("symbol") String symbol,@PathVariable("sma") String sma,Model model) throws Exception
	{
		logger.info("STOCKPATTERN.......");
		
		montlyUnrealisedProfitTotalMap.clear();
		montlyUnrealisedProfitMap.clear();
		yearlyUnrealisedProfitTotalMap.clear();
		yearlyUnrealisedProfitMap.clear();
		
		String string = "01-11-2018";	
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date fromDate = format.parse(string);
		
		if(null!=scale && !sma.equalsIgnoreCase("default"))
		{	
			StockConstants.RISING_PRICE_MIN_SCALE = Integer.parseInt(scale);
		}
		
		if(null!=sma && !sma.equalsIgnoreCase("default"))
		{
			StockConstants.MOVING_AVERAGE_SCALE = Integer.parseInt(sma);
		}
		 
		//allSymbolTXT(fromDate);
	
		List<TradeResults> tradeResultsList = backTestTradeResults(fromDate,symbol);
		
		List<TradeResults>  sortedList = tradeResultsList.stream()
		        .sorted(Comparator.comparingInt(TradeResults::getProfitableTrades).reversed())
		        .collect(Collectors.toList());
		
		model.addAttribute("simpleMovingAvg", sma);
		model.addAttribute("risingScale", scale);
		model.addAttribute("tradeResults", sortedList);
		model.addAttribute("profitabelTradesMoreThan9", sortedList.stream().filter(t->t.getProfitableTrades()>9).collect(Collectors.counting()));
		return "landingPage";
	}
	
	private List<TradeResults> backTestTradeResults(Date fromDate,String instrument) throws Exception
	{
		  List<TradeResults> tradeResultsList = new ArrayList<TradeResults>();
		
		  String CSV_FILE_LOC = StockConstants.CSV_FILE_LOC;
		  
		  File[] files = new File(CSV_FILE_LOC).listFiles();
		  
		  for (File file : files)
		  { 
			  if (file.isFile()) 
			  {
		  
				  String symbol = file.getName().replace(".csv", "");
				  
				  List<String> shortListedStocks = getShortlistedStocks();
				  
				  boolean process = (null==instrument || instrument.equalsIgnoreCase("default") || symbol.equalsIgnoreCase(instrument)) ? true  : false;
				  //&& shortListedStocks.contains(symbol)
				  if(process && shortListedStocks.contains(symbol))
				  {
					  List<StockPrice>  candlesWithEntryExit =  stockPatternStrategy.backTestMovingAverage(symbol,fromDate, StockConstants.MOVING_AVERAGE_SCALE);
					  
					  monthlyUnrealisedProfitReport(candlesWithEntryExit);
					  yearlyUnrealisedProfitReport(candlesWithEntryExit);
					  
					  candlesWithEntryExit.forEach(c->logger.info(c.getSymbol()+"|"+new SimpleDateFormat("dd-MMM-yyyy").format(c.getMarketDate())+"|"+c.getOrderDetails()+"|"+c.getTradeResult()));
					  
					  if(CollectionUtils.isNotEmpty(candlesWithEntryExit))
					  {
						    int targetExistCount = candlesWithEntryExit.stream().filter(c->null!=c.getTradeResult()).filter(candle->candle.getTradeResult().contains("TARGET_EXIT")).collect(Collectors.toList()).size();
							
						    int stopLossCount = candlesWithEntryExit.stream().filter(c->null!=c.getTradeResult()).filter(candle->candle.getTradeResult().contains("STOP_LOSS")).collect(Collectors.toList()).size();

						  	TradeResults tradeResults = new TradeResults();
							
							tradeResults.setInstrument(candlesWithEntryExit.iterator().next().getSymbol());
							tradeResults.setTradesCount(candlesWithEntryExit.size());
							tradeResults.setTargetExistCount(targetExistCount);
							tradeResults.setStopLossCount(stopLossCount);
							tradeResults.setProfitableTrades((targetExistCount*2)-stopLossCount);	
							tradeResultsList.add(tradeResults);	
					  }
				  }
			  } 
		  }
		  
		  //logger.info("Monthly Detail Report ");
		  //montlyUnrealisedProfitMap.forEach((key, value) -> logger.info(key + ":" + value));
		  
		  logger.info("Monthly Report ");
		  montlyUnrealisedProfitTotalMap.forEach((key, value) -> logger.info(key + "|" + value));
		  
		  logger.info("Yearly Report ");
		  yearlyUnrealisedProfitTotalMap.forEach((key, value) -> logger.info(key + "|" + value));
		  
		  logger.info("Symbol|Trades Found|Target Exit Count|Stop Loss Count|Overall P/L");
		  tradeResultsList.forEach(trades->logger.info(trades.getInstrument()+"|"+trades.getTradesCount()+"|"+trades.getTargetExistCount()+"|"+trades.getStopLossCount()+"|"+trades.getProfitableTrades()));
		  
		  return tradeResultsList;
	}

	private void monthlyUnrealisedProfitReport(List<StockPrice> candlesWithEntryExit) 
	{	
		  SimpleDateFormat monthsFormat = new SimpleDateFormat("MMM-yyyy");
		  for(StockPrice candle : candlesWithEntryExit)
		  {
			  String key = monthsFormat.format(candle.getMarketDate());
			  
			  if(!montlyUnrealisedProfitMap.containsKey(key)) 
			  {
				  List<Float> tradeProfits = new ArrayList<Float>();
				  tradeProfits.add(candle.getPnlAmount());
				  montlyUnrealisedProfitMap.put(key, tradeProfits);
		      }
			  else
			  {
				  List<Float> tradeExistingProfits = montlyUnrealisedProfitMap.get(key);
				  tradeExistingProfits.add(candle.getPnlAmount());
				  montlyUnrealisedProfitMap.put(key,tradeExistingProfits);  
			  }
		  }
		  
		  for(String month : montlyUnrealisedProfitMap.keySet())
		  {
			  montlyUnrealisedProfitTotalMap.put(month, montlyUnrealisedProfitMap.get(month).stream().mapToDouble(Float::doubleValue).sum());
		  }
	}
	
	private void yearlyUnrealisedProfitReport(List<StockPrice> candlesWithEntryExit) 
	{	
		  SimpleDateFormat yearsFormat = new SimpleDateFormat("yyyy");
		  for(StockPrice candle : candlesWithEntryExit)
		  {
			  String key = yearsFormat.format(candle.getMarketDate());
			  
			  if(!yearlyUnrealisedProfitMap.containsKey(key)) 
			  {
				  List<Float> tradeProfits = new ArrayList<Float>();
				  tradeProfits.add(candle.getPnlAmount());
				  yearlyUnrealisedProfitMap.put(key, tradeProfits);
		      }
			  else
			  {
				  List<Float> tradeExistingProfits = yearlyUnrealisedProfitMap.get(key);
				  tradeExistingProfits.add(candle.getPnlAmount());
				  yearlyUnrealisedProfitMap.put(key,tradeExistingProfits);  
			  }
		  }
		  
		  for(String month : yearlyUnrealisedProfitMap.keySet())
		  {
			  yearlyUnrealisedProfitTotalMap.put(month, yearlyUnrealisedProfitMap.get(month).stream().mapToDouble(Float::doubleValue).sum());
		  }
	}

	private List<String> getShortlistedStocks() throws Exception 
	{
		  File shortlistedile = ResourceUtils.getFile("classpath:Nifty500-tops-MA44.txt");
		  
		  List<String> lines = Collections.emptyList();
		  lines = Files.readAllLines(Paths.get(shortlistedile.getPath()));
		  
		  //lines.forEach(l->logger.info(l));
		  return lines;
	}


	private List<StockPrice> allSymbolCSV(Date fromDate) 
	{
		 String header ="Symbol|STOP LOSS COUNT|TARGET EXIT COUNT|TOTAL STOP LOSS AMOUNT|TOTAL TARGET EXIT AMOUNT| TOTAL P/L " ;
		  
		 //logger.info(header);
		
		 // String CSV_FILE_LOC = "C:\\Users\\ashis\\Downloads\\EOD_DATA\\archive-01\\data_1990_2020\\stock_data\\";
		  String CSV_FILE_LOC = StockConstants.CSV_FILE_LOC;
		  
		  File[] files = new File(CSV_FILE_LOC).listFiles();
		  
		  for (File file : files)
		  { 
			  if (file.isFile()) 
			  {
		  
				  String symbol = file.getName().replace(".csv", "");
				  
				  return stockPatternStrategy.backTestMovingAverage(symbol,fromDate, StockConstants.MOVING_AVERAGE_SCALE);
		  
			  } 
		  }
		  
		  return null;
	}
	
	
	
	private void allSymbolTXT(Date fromDate) 
	{
		String header ="Symbol|STOP LOSS COUNT|TARGET EXIT COUNT|TOTAL STOP LOSS AMOUNT|TOTAL TARGET EXIT AMOUNT| TOTAL P/L " ;
		  
		 // logger.info(header);
		
		 String TXT_FILE_LOC = "C:\\KeyStocks-Lite\\EOD\\data\\"; 
		  
		  File[] files = new File(TXT_FILE_LOC).listFiles();
		  
		  for (File file : files)
		  { 
			  if (file.isFile()) 
			  {
		  
				  String symbol = file.getName().replace(".txt", "");
				  
				  stockPatternStrategy.backTestMovingAverage(symbol,fromDate, 44);
		  
			  } 
		  }
	}
	
}
