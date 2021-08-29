package com.stockpattern.demo.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.stockpattern.demo.apis.NseSiteAPIService;
import com.stockpattern.demo.apis.StockNoteAPIService;
import com.stockpattern.demo.indicators.StockConstants;
import com.stockpattern.demo.models.StockPrice;
import com.stockpattern.demo.strategy.TradeFinderService;

@Controller
@RequestMapping("/tradefinder")
public class TradeFinder 
{
	Logger logger = LoggerFactory.getLogger(TradeFinder.class);
	
	@Resource(name = "tradeFinderService")
	TradeFinderService tradeFinderService;
	
	@Resource(name = "stockNoteAPIService")
	StockNoteAPIService stockNoteAPIService;

	@Resource(name = "nseSiteAPIService")
	NseSiteAPIService nseSiteAPIService;
	
	@RequestMapping("/ma44rising90/{date}")
	public String findTradesForMA44Rising90(Model model,@PathVariable("date") String date)  throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		
		Date forDate = dateFormat.parse(date);
		
		List<StockPrice> potentialTradesList =  tradeFinderService.findTradesForMA44Rising90ForDate(forDate);
		
		model.addAttribute("potentialTradesList", potentialTradesList);
		
		return "ma44rising90TradesPage";
	}
	
	@RequestMapping("/rising90ma/{sma}/{date}")
	public String findTradesRising90ForGivenSMA(Model model,@PathVariable("sma") String sma,@PathVariable("date") String date)  throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		
		Date forDate = dateFormat.parse(date);
		
		StockConstants.MOVING_AVERAGE_SCALE = Integer.parseInt(sma);
		
		List<StockPrice> potentialTradesList =  tradeFinderService.findTradesForMA44Rising90ForDate(forDate);
		
		model.addAttribute("simpleMovingAvg", sma);
		model.addAttribute("potentialTradesList", potentialTradesList);
		
		return "rising90ForSMATradesPage";
	}
	
	@RequestMapping("/nseeodata")
	@ResponseBody
	public String fetchNseEoddata(Model model)  throws Exception
	{
		return nseSiteAPIService.downloadHistoricalDailyEOD();
	}
	
	@RequestMapping("/update-nseeodata")
	@ResponseBody
	public String updateNseEoddata(Model model)  throws Exception
	{
		return nseSiteAPIService.updateHistoricalDailyEOD();
	}
	
	
}
