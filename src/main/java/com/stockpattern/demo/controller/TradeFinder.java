package com.stockpattern.demo.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.stockpattern.demo.apis.NseSiteAPIService;
import com.stockpattern.demo.apis.StockNoteAPIService;
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
	
	@RequestMapping("/ma44rising90")
	public String findTradesForMA44Rising90(Model model)  throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		
		Date forDate = dateFormat.parse("01-APR-2021");
		
		List<StockPrice> potentialTradesList =  tradeFinderService.findTradesForMA44Rising90ForDate(forDate);
		
		model.addAttribute("potentialTradesList", potentialTradesList);
		
		return "ma44rising90TradesPage";
	}
	
	@RequestMapping("/nseeodata")
	public String fetchNseEoddata(Model model)  throws Exception
	{
		return nseSiteAPIService.downloadHistoricalDailyEOD();
	}
	
	@RequestMapping("/update-nseeodata")
	public String updateNseEoddata(Model model)  throws Exception
	{
		return nseSiteAPIService.updateHistoricalDailyEOD();
	}
	
	
}
