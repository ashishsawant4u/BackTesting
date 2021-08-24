package com.stockpattern.demo.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
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
	
	Map<String, String> montlyUnrealisedProfitTotalMap = new ConcurrentHashMap<String, String>();
	Map<String, List<Float>> montlyUnrealisedProfitMap = new ConcurrentHashMap<String, List<Float>>();
	
	Map<String, String> yearlyUnrealisedProfitTotalMap = new ConcurrentHashMap<String, String>();
	Map<String, List<Float>> yearlyUnrealisedProfitMap = new ConcurrentHashMap<String, List<Float>>();
	
	long totalNoOfTrades;
	
	List<StockPrice>  tradeList = new ArrayList<StockPrice>();

	
	@RequestMapping("/scale/{scale}/{symbol}/{sma}")
	public String getLandingPage2(@PathVariable("scale") String scale,@PathVariable("symbol") String symbol,@PathVariable("sma") String sma,Model model) throws Exception
	{
		logger.info("STOCKPATTERN.......");
		
		montlyUnrealisedProfitTotalMap.clear();
		montlyUnrealisedProfitMap.clear();
		yearlyUnrealisedProfitTotalMap.clear();
		yearlyUnrealisedProfitMap.clear();
		totalNoOfTrades=0;
		tradeList.clear();
		
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
		model.addAttribute("totalNoOfTrades", totalNoOfTrades);
		
		
		prepareReportForDispaly(model);
		return "landingPage";
	}

	private void prepareReportForDispaly(Model model)  throws Exception
	{
		//monthly
		Map<Date, String> tempMonthlyMap = new HashMap<Date, String>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
		
		for(String month : montlyUnrealisedProfitTotalMap.keySet())
		{
			tempMonthlyMap.put(sdf.parse(month), montlyUnrealisedProfitTotalMap.get(month));
		}
		
		Map<Date, String> sortedmontlyUnrealisedProfitTotalMap  = new TreeMap<Date, String>(Collections.reverseOrder());
		sortedmontlyUnrealisedProfitTotalMap.putAll(tempMonthlyMap);
		model.addAttribute("montlyUnrealisedProfitTotalMap", sortedmontlyUnrealisedProfitTotalMap);
		
		//yearly
		Map<String, String> sortedyearlyUnrealisedProfitTotalMap = new TreeMap<String, String>(yearlyUnrealisedProfitTotalMap);
		model.addAttribute("yearlyUnrealisedProfitTotalMap", sortedyearlyUnrealisedProfitTotalMap);
		
		
		//trades list

		Collections.sort(tradeList, (c1, c2) -> c1.getMarketDate().compareTo(c2.getMarketDate()));
		Collections.reverse(tradeList);
		
		model.addAttribute("tradeList", tradeList);
		
		//monthly investment
		monthlyInvestmentReport(model);
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
					  
					  tradeList.addAll(candlesWithEntryExit);
					  monthlyUnrealisedProfitReport(candlesWithEntryExit);
					  yearlyUnrealisedProfitReport(candlesWithEntryExit);
					  totalNoOfTrades = totalNoOfTrades + candlesWithEntryExit.size();
					  
					  candlesWithEntryExit.forEach(c->logger.info(c.getSymbol()+"|"+new SimpleDateFormat("dd-MMM-yyyy").format(c.getMarketDate())+"|"+c.getOrderDetails()+"|"+c.getTradeResult()+"|"+c.getTradeEntry().getTradeDuration()));
					  
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
		  
		  DecimalFormat df=new DecimalFormat("#.##");
		  
		  for(String month : montlyUnrealisedProfitMap.keySet())
		  {
			  montlyUnrealisedProfitTotalMap.put(month, df.format(montlyUnrealisedProfitMap.get(month).stream().mapToDouble(Float::doubleValue).sum()));
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
		  
		  DecimalFormat df=new DecimalFormat("#.##");
		  
		  for(String month : yearlyUnrealisedProfitMap.keySet())
		  {
			  yearlyUnrealisedProfitTotalMap.put(month, df.format(yearlyUnrealisedProfitMap.get(month).stream().mapToDouble(Float::doubleValue).sum()));
		  }
	}
	
	private void monthlyInvestmentReport(Model model) throws Exception
	{
		 SimpleDateFormat monthsFormat = new SimpleDateFormat("MMM-yyyy");
		 
		 Map<String, Float> monthlyInvestmentMap = new HashMap<String, Float>();
		
		for(StockPrice trade : tradeList)
		{
			String month = monthsFormat.format(trade.getMarketDate());
			
			 if(!monthlyInvestmentMap.containsKey(month)) 
			 {
				 monthlyInvestmentMap.put(month, trade.getTradeEntry().getInvestment());
			 }
			 else
			 {
				 monthlyInvestmentMap.put(month, monthlyInvestmentMap.get(month) + trade.getTradeEntry().getInvestment()); 
			 }
		}
		
		Map<Date, String> tempMonthlyMap = new HashMap<Date, String>();
		DecimalFormat df=new DecimalFormat("#.##");
		
		for(String month : monthlyInvestmentMap.keySet())
		{
			tempMonthlyMap.put(monthsFormat.parse(month), df.format(monthlyInvestmentMap.get(month)));
		}
		
		Map<Date, String> sortedMap  = new TreeMap<Date, String>(Collections.reverseOrder());
		sortedMap.putAll(tempMonthlyMap);
		
		model.addAttribute("monthlyInvestmentMap",sortedMap);
	}

	private List<String> getShortlistedStocks() throws Exception 
	{
		  File shortlistedile = ResourceUtils.getFile("classpath:Nifty500-tops-MA44.txt");
		  
		  List<String> lines = Collections.emptyList();
		  lines = Files.readAllLines(Paths.get(shortlistedile.getPath()));
		  
		  //lines.forEach(l->logger.info(l));
		  return lines;
	}
	
}
