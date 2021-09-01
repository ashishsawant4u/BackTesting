package com.stockpattern.demo.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;
import com.stockpattern.demo.indicators.Utils;
import com.stockpattern.demo.models.IpoHistoricalData;
import com.stockpattern.demo.models.StockPrice;

import ch.qos.logback.classic.pattern.Util;
import io.samco.client.model.CandleData;

@Controller
@RequestMapping("/ipo")
public class IPOController 
{
	@RequestMapping("/backtest/{noOfMonthsAfterListing}")
	public String backtestIPO(Model model,@PathVariable("noOfMonthsAfterListing") String noOfMonthsAfterListing)  throws Exception
	{		
		int monthsAfterListing = Integer.parseInt(noOfMonthsAfterListing);
		
		model.addAttribute("monthsAfterListing", monthsAfterListing+" Months");
		
		List<IpoHistoricalData> ipoHistoricalDatas = backTestIPO(monthsAfterListing);
		model.addAttribute("ipoHistoricalData",ipoHistoricalDatas);
		
		return "ipoBackTestPage";
	}
	
	private static List<IpoHistoricalData> backTestIPO(int noOfMonthsAfterListing)
	{
		String COMMA_DELIMITER = ",";
		List<IpoHistoricalData> ipoList = new ArrayList<IpoHistoricalData>();
		
		String ipoHistoricalDataFile = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\IPO\\IPO-Historical-Data.csv";
		
		
		
		int count = 0;
		try 
		{
			
			BufferedReader br = new BufferedReader(new FileReader(ipoHistoricalDataFile));
			    String line;
			    while ((line = br.readLine()) != null) {
			    	
			    	if(count != 0)
			    	{
			    		 	String[] lineData = line.split(COMMA_DELIMITER);
					        
			            	if(StringUtils.isNotBlank(lineData[0]))
			            	{
			            		Date listingDate = new SimpleDateFormat("dd-MM-yyyy").parse(lineData[0].replace("\"", ""));  
					            
				            	IpoHistoricalData ipoHistoricalData  = new IpoHistoricalData();
				            	
				            	ipoHistoricalData.setListingDate(listingDate);
				            	ipoHistoricalData.setIpoName(lineData[1].replace("\"", ""));
				            	ipoHistoricalData.setIssueSize(Float.parseFloat(lineData[2].replace("\"", "")));
				            	ipoHistoricalData.setQIB(Float.parseFloat(lineData[3].replace("\"", "")));
				            	ipoHistoricalData.setHNI(Float.parseFloat(lineData[4].replace("\"", "")));
				            	ipoHistoricalData.setRII(Float.parseFloat(lineData[5].replace("\"", "")));
				            	ipoHistoricalData.setIssuePrice(Float.parseFloat(lineData[7].replace("\"", "")));
				            	ipoHistoricalData.setListingOpenPrice(Float.parseFloat(lineData[8].replace("\"", "")));
				            	ipoHistoricalData.setListingClosePrice(Float.parseFloat(lineData[9].replace("\"", "")));
				            	ipoHistoricalData.setListingGainPecentage(Float.parseFloat(lineData[10].replace("\"", "")));
				            	ipoHistoricalData.setCMP(Float.parseFloat(lineData[11].replace("\"", "")));
				            	
				            	Calendar cal = Calendar.getInstance();  
				        		cal.setTime(listingDate);
				        		cal.add(Calendar.MONTH, 6); 
				        		Date futureDate =  cal.getTime().after(new Date()) ? new Date() : cal.getTime();
				        		
				            	
				        		ipoHistoricalData.setFutureDate(futureDate);
				        		
				        		//String symbol = Utils.getSymbolForCompanyName(lineData[1].replace("\"", ""));
				        		String symbol = lineData[13].replace("\"", "");
				        				
				        		if(StringUtils.isNotBlank(symbol))
				        		{
				        			ipoHistoricalData.setSymbol(symbol);
				        						        			
				        			List<StockPrice> candleList = Utils.getCandlesData(symbol, ipoHistoricalData.getListingDate());
				        			
				        			if(CollectionUtils.isNotEmpty(candleList))
				        			{
				        				Optional<StockPrice> matchedCandle = candleList.stream().filter(c->c.getMarketDate().equals(futureDate)).findFirst();
				        				if(!matchedCandle.isPresent())
				        				{
				        					Calendar cal2 = Calendar.getInstance();  
							        		cal2.setTime(futureDate);
							        		cal2.add(Calendar.DATE, 3); 
				        					
				        					matchedCandle = candleList.stream().filter(c->c.getMarketDate().equals(cal2.getTime())).findFirst();
				        				}
				        				
				        				if(matchedCandle.isPresent())
				        				{
				        					float futurePrice = matchedCandle.get().getOpenPrice(); 
									        		
								        	ipoHistoricalData.setFuturePrice(futurePrice);
								        	ipoHistoricalData.setFutureGainPecentage(((futurePrice-ipoHistoricalData.getListingClosePrice())/ipoHistoricalData.getListingClosePrice())*100);
				        				}
				        				
				        			}
				        		}
				        		
				        		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
				        		if(ipoHistoricalData.getFutureDate().before(new Date()) && !sdf.format(ipoHistoricalData.getFutureDate()).equals(sdf.format(new Date())))
				        		{	
				        			ipoList.add(ipoHistoricalData);
				        		}
			            	}
			    	}
			       
			    	count++;
			        
			    }
			
			
		} 
		catch (Exception e) {
			System.out.println("Exception while reading CSV "+e);
		}
		
		return ipoList;
	}
	
	
}
