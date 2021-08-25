package com.stockpattern.demo.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
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
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.stockpattern.demo.indicators.QuantityPlanner;
import com.stockpattern.demo.indicators.StockConstants;
import com.stockpattern.demo.models.MonthlyReport;
import com.stockpattern.demo.models.StockPrice;
import com.stockpattern.demo.models.TradeResults;
import com.stockpattern.demo.models.YearlyReport;
import com.stockpattern.demo.strategy.StockPatternStrategy;


@Controller
@RequestMapping("/stockpattern")
public class LandingPageController {
	
	
	Logger logger = LoggerFactory.getLogger(LandingPageController.class);


	@Resource(name = "stockPatternStrategy")
	StockPatternStrategy stockPatternStrategy;
	
	List<StockPrice>  tradeList = new ArrayList<StockPrice>();

	
	@RequestMapping("/scale/{scale}/{symbol}/{sma}")
	public String getLandingPage2(@PathVariable("scale") String scale,@PathVariable("symbol") String symbol,@PathVariable("sma") String sma,Model model) throws Exception
	{
		logger.info("STOCKPATTERN.......");
		
		resetData();
		
		String string = "01-11-2019";	
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
		 
	
		List<TradeResults> tradeResultsList = backTestTradeResults(fromDate,symbol);
		
		List<TradeResults>  sortedList = tradeResultsList.stream()
		        .sorted(Comparator.comparingInt(TradeResults::getProfitableTrades).reversed())
		        .collect(Collectors.toList());
		
		model.addAttribute("simpleMovingAvg", sma);
		model.addAttribute("risingScale", scale);
		model.addAttribute("tradeResults", sortedList);
		model.addAttribute("profitabelTradesMoreThan9", sortedList.stream().filter(t->t.getProfitableTrades()>9).collect(Collectors.counting()));
		model.addAttribute("totalNoOfTrades", tradeList.size());
		
		
		prepareReportForDispaly(model);
		return "landingPage";
	}

	private void resetData() 
	{
		tradeList.clear();
		QuantityPlanner.monthlyTradeAmountTrackerMap.clear();
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
		  
		  logger.info("Symbol|Trades Found|Target Exit Count|Stop Loss Count|Overall P/L");
		  tradeResultsList.forEach(trades->logger.info(trades.getInstrument()+"|"+trades.getTradesCount()+"|"+trades.getTargetExistCount()+"|"+trades.getStopLossCount()+"|"+trades.getProfitableTrades()));
		  
		  return tradeResultsList;
	}
	
	private void prepareReportForDispaly(Model model)  throws Exception
	{
		List<MonthlyReport> monthlyReportList = prepareMonthlyTradingReport();
		model.addAttribute("monthlyReportForTrades", monthlyReportList);
		
		//yearly
		List<YearlyReport> yearlyReportList = prepareYearlyTradingReport(monthlyReportList);
		model.addAttribute("yearlyReportForTrades", yearlyReportList);
		
		//trades list
		Collections.sort(tradeList, (c1, c2) -> c1.getMarketDate().compareTo(c2.getMarketDate()));
		Collections.reverse(tradeList);
		model.addAttribute("tradeList", tradeList);
		
		model.addAttribute("totalTargetExitCount",tradeList.stream().filter(t->null!=t.getTradeEntry().getTradeStatus()).filter(t->t.getTradeEntry().getTradeStatus().equals(StockConstants.TARGET_EXIT)).collect(Collectors.toList()).size());
		model.addAttribute("totalStopLossCount",tradeList.stream().filter(t->null!=t.getTradeEntry().getTradeStatus()).filter(t->t.getTradeEntry().getTradeStatus().equals(StockConstants.STOP_LOSS)).collect(Collectors.toList()).size());

	}

	private List<YearlyReport> prepareYearlyTradingReport(List<MonthlyReport> monthlyReportList) throws ParseException 
	{
		SimpleDateFormat yearDf = new SimpleDateFormat("yyyy");
		
		List<YearlyReport> yearlyReportList = new ArrayList<YearlyReport>();
		
		Set<String> uniqueTradeYears = tradeList.stream().map(t->yearDf.format(t.getMarketDate())).collect(Collectors.toSet());
		
		for(String year : uniqueTradeYears)
		{	
			YearlyReport yearlyReport =  new YearlyReport();
			yearlyReport.setYear(yearDf.parse(year));
			yearlyReportList.add(yearlyReport);
		}
		
		for(YearlyReport yearReport : yearlyReportList)
		{
			List<MonthlyReport> allMonthlyReportsForYear =  monthlyReportList.stream().filter(m->yearDf.format(m.getMonth()).equals(yearDf.format(yearReport.getYear()))).collect(Collectors.toList());
			yearReport.setInvestment((float)allMonthlyReportsForYear.stream().mapToDouble(m->m.getInvestment()).sum());
			yearReport.setProfitAndLoss((float)allMonthlyReportsForYear.stream().mapToDouble(m->m.getProfitAndLoss()).sum());
			yearReport.setPercentageGain((yearReport.getProfitAndLoss()/yearReport.getInvestment())*100);
			yearReport.setTradesCount(allMonthlyReportsForYear.stream().mapToLong(m->m.getTradesCount()).sum());
		}
		
		Collections.sort(yearlyReportList, (c1, c2) -> c1.getYear().compareTo(c2.getYear()));
		Collections.reverse(yearlyReportList);
		return yearlyReportList;
	}

	private List<MonthlyReport> prepareMonthlyTradingReport() throws ParseException 
	{
		List<MonthlyReport> monthlyReportList = new ArrayList<MonthlyReport>();
	
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
		
		Set<String> uniqueTradeMonths = tradeList.stream().map(t->sdf.format(t.getMarketDate())).collect(Collectors.toSet());
		
		for(String month : uniqueTradeMonths)
		{	
			MonthlyReport monthlyReport =  new MonthlyReport();
			monthlyReport.setMonth(sdf.parse(month));
			monthlyReportList.add(monthlyReport);
		}
		
		for(StockPrice trade : tradeList)
		{	
			MonthlyReport monthlyReportForTrade = monthlyReportList.stream().filter(m->sdf.format(m.getMonth()).equals(sdf.format(trade.getMarketDate()))).findFirst().get();
			
			int indexForMonthlyReport = IntStream.range(0, monthlyReportList.size()).filter(index->sdf.format(monthlyReportList.get(index).getMonth()).equals(sdf.format(trade.getMarketDate()))).findFirst().getAsInt();
			
			//investment
			if(monthlyReportForTrade.getInvestment() == 0.0F)
			{
				monthlyReportForTrade.setInvestment(trade.getTradeEntry().getInvestment());
			}
			else
			{
				monthlyReportForTrade.setInvestment(monthlyReportForTrade.getInvestment() + trade.getTradeEntry().getInvestment());
			}
			
			//P&L
			if(monthlyReportForTrade.getProfitAndLoss() == 0.0F)
			{
				monthlyReportForTrade.setProfitAndLoss(trade.getPnlAmount());
			}
			else
			{
				monthlyReportForTrade.setProfitAndLoss(monthlyReportForTrade.getProfitAndLoss() + trade.getPnlAmount());
			}
			
			//tradeCount
			if(monthlyReportForTrade.getTradesCount() == 0)
			{
				monthlyReportForTrade.setTradesCount(1L);
			}
			else
			{
				monthlyReportForTrade.setTradesCount(monthlyReportForTrade.getTradesCount() + 1L);
			}
			
			monthlyReportForTrade.setPercentageGain((monthlyReportForTrade.getProfitAndLoss()/monthlyReportForTrade.getInvestment())*100);
			
			monthlyReportList.set(indexForMonthlyReport, monthlyReportForTrade);
		}
		
		Collections.sort(monthlyReportList, (c1, c2) -> c1.getMonth().compareTo(c2.getMonth()));
		Collections.reverse(monthlyReportList);
		return monthlyReportList;
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
