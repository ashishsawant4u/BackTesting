package com.stockpattern.demo.indicators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stockpattern.demo.models.StockPrice;
import com.stockpattern.demo.models.TradeEntryData;

public class Utils {

	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	
	public static Date getDateBefore(Date dateParam,int scale)
	{
		scale = scale +1;
		
		LocalDate date = dateParam.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		if (scale < 1) {
	        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
	    }

	    LocalDate result = date;
	    int minusDays = 0;
	    while (minusDays < scale) {
	        result = result.minusDays(1);
	        if (!(result.getDayOfWeek() == DayOfWeek.SATURDAY ||
	              result.getDayOfWeek() == DayOfWeek.SUNDAY)) {
	            ++minusDays;
	        }
	    }
	    
	    Date beforeDate =  Date.from(result.atStartOfDay(ZoneId.systemDefault()).toInstant());    
	    
	    System.out.println("date param "+dateParam+" before date "+beforeDate+"  scale "+scale);
	    
	   return beforeDate;
	}  
	
	
	/**
	 * Prepare candles data for given symbol via CSV
	 * 
	 * @param instrument
	 * @param fromDate
	 * @return
	 */
	public static List<StockPrice>  getCandlesData(String instrument,Date fromDate)
	{	
		List<StockPrice> candleList = readUsingCSVFile(instrument);
		
		Collections.sort(candleList, (c1, c2) -> c1.getMarketDate().compareTo(c2.getMarketDate()));
		 
		 if(null!=fromDate)
		 {
			 return candleList.stream().filter(s->!s.getMarketDate().before(fromDate)).collect(Collectors.toList());
		 }
		
		 return candleList;
	}
	
	/**
	 *  CSV reader to read candle data for given symbol
	 * @param instrument
	 * @return
	 */
	private static List<StockPrice> readUsingCSVFile (String instrument)
	{
		String COMMA_DELIMITER = ",";
		List<StockPrice> candleList = new ArrayList<StockPrice>();
		String FILE_LOC = StockConstants.CSV_FILE_LOC;
		int count = 0;
		try 
		{
			
			BufferedReader br = new BufferedReader(new FileReader(FILE_LOC+instrument+".csv"));
			    String line;
			    while ((line = br.readLine()) != null) {
			    	
			    	if(count != 0)
			    	{
			    		 	String[] lineData = line.split(COMMA_DELIMITER);
					        
//			            	Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(lineData[0]);  
//			            
//			            	StockPrice candle = new StockPrice();
//			            	candle.setSymbol(instrument);
//			            	candle.setMarketDate(currentDate);
//			            	candle.setOpenPrice(Float.parseFloat(lineData[4]));
//			            	candle.setHighPrice(Float.parseFloat(lineData[5]));
//			            	candle.setLowPrice(Float.parseFloat(lineData[6]));
//			            	candle.setClosePrice(Float.parseFloat(lineData[8]));
			    		 	
			    		 	Date marketDate = new SimpleDateFormat("dd-MMM-yyyy").parse(lineData[5].replace("\"", ""));  
				            
			            	StockPrice candle = new StockPrice();
			            	candle.setSymbol(instrument);
			            	candle.setMarketDate(marketDate);
			            	candle.setOpenPrice(Float.parseFloat(lineData[1].replace("\"", "")));
			            	candle.setHighPrice(Float.parseFloat(lineData[2].replace("\"", "")));
			            	candle.setLowPrice(Float.parseFloat(lineData[3].replace("\"", "")));
			            	candle.setClosePrice(Float.parseFloat(lineData[4].replace("\"", "")));
			            	
			            	candleList.add(candle);	
			    	}
			       
			    	count++;
			        
			    }
			
			
		} 
		catch (Exception e) {
			System.out.println("Exception while reading CSV "+e);
		}
		
		return candleList;
	}

	
	/**
	 * Set MA to all candles of the symbol
	 * @param candlesData
	 * @param averageScale = moving average
	 * @return
	 */
	public static List<StockPrice>  getCandlesDataForGivenScale(List<StockPrice>  candlesData, int averageScale)
	{
		averageScale = averageScale-1;
		
		for (int i = averageScale; i < candlesData.size(); i++) {
			
			List<Float> closePriceList = candlesData.subList(i-averageScale, i).stream().map(stock->stock.getClosePrice()).collect(Collectors.toList());
			
			float movingAvg = Indicators.getMovingAverage(closePriceList, averageScale);
			
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
			
			candlesData.get(i).setMovingAverage(Float.valueOf(decimalFormat.format(movingAvg)));
			
		}
		System.out.println("candlesData size "+candlesData.size());
		
		return candlesData;
	}
	
	/**
	 * get previous candles for given number of days before given candle
	 * 
	 * @param candleData
	 * @param stockPriceWithMA
	 * @param scale
	 * @return
	 */
	public static List<StockPrice>  getCandlesForLastNosOfDayForInstrument(StockPrice candleData,List<StockPrice>  stockPriceWithMA,int scale)
	{
		//logger.info("FINDING BACK CANDLE FOR "+candleData.toString());
		
		 Date marketDateForCandle = candleData.getMarketDate();
		 
		 if(null!=marketDateForCandle)
		 { 
			 List<StockPrice> candlesDataBeforeDate =  stockPriceWithMA.stream().filter(s->s.getMarketDate().before(marketDateForCandle) || s.getMarketDate().equals(marketDateForCandle)).collect(Collectors.toList());
			 
			 if(candlesDataBeforeDate.size() > scale)
			 {
				 candlesDataBeforeDate.sort(Comparator.comparing(o -> o.getMarketDate()));
				 
				 List<StockPrice> candlesDataBeforeDateForNoOfDays = candlesDataBeforeDate.subList(candlesDataBeforeDate.size()-scale, candlesDataBeforeDate.size());
				 
				 //logger.info("=============================================================================");
				 //candlesDataBeforeDateForNoOfDays.forEach(stock->logger.info(stock.toString()));
				 //logger.info("=============================================================================");
				 
				 return candlesDataBeforeDateForNoOfDays;
			 }
			 
		 } 
		 return null;
	}
	
	/**
	 * Identify whether moving average is rising or not for given number of days
	 * @param candleData
	 * @param stockPriceWithMA
	 * @param scale
	 * @return
	 */
	public static boolean isRisinngMAForCandle(StockPrice candleData,List<StockPrice>  stockPriceWithMA,int scale)
	{
		List<StockPrice> candlesDataBeforeDateForNoOfDays = getCandlesForLastNosOfDayForInstrument(candleData, stockPriceWithMA, scale);
		
		if(CollectionUtils.isNotEmpty(candlesDataBeforeDateForNoOfDays))
		{
			List<Float> movingAverages = candlesDataBeforeDateForNoOfDays.stream().map(s->s.getMovingAverage()).collect(Collectors.toList());
			
			//boolean isRising = isRisingLogic1(candleData, movingAverages);
			boolean isRising = isRisingLogic90(candleData, movingAverages);
			
			//Ordering.natural().isOrdered(movingAverages);
			if(isRising)
			{
				//logger.info("RISING >> ==================================================="+new SimpleDateFormat("dd-MMM-yyy").format(candleData.getMarketDate()));
				//movingAverages.forEach(p->logger.info(p.toString()));
			}
			else
			{
				isRising = isRisingLogic60(candleData, movingAverages);
			}
			
			return isRising;
		}
		else
		{
			return false;
		}
	}


	private static boolean isRisingLogic1(StockPrice candleData, List<Float> movingAverages) 
	{
		boolean isRising = (movingAverages.get(movingAverages.size()-1) - movingAverages.get(0)) >= Utils.getIdealRisingCurve(candleData);
		
		boolean is60Rising = (movingAverages.get(movingAverages.size()-1) - movingAverages.get(movingAverages.size() - 45)) >= Utils.getIdealRisingCurve(candleData);
		
		boolean is45Rising = (movingAverages.get(movingAverages.size()-1) - movingAverages.get(movingAverages.size() - 45)) >= Utils.getIdealRisingCurve(candleData);
		
		boolean is15Rising = (movingAverages.get(movingAverages.size()-1) - movingAverages.get(movingAverages.size() - 15)) >= Utils.getIdealRisingCurve(candleData);
		
		isRising = (isRising && is15Rising && is45Rising && is60Rising);
		return isRising;
	}
	
	private static boolean isRisingLogic90(StockPrice candleData, List<Float> movingAverages) 
	{
		boolean isRising = false;
	
		
		
		float previous90thCandleMA = movingAverages.get(0);
		float previous60thCandleMA = movingAverages.get(movingAverages.size()-60);
		float previous45thCandleMA = movingAverages.get(movingAverages.size()-45);
		float previous30thCandleMA = movingAverages.get(movingAverages.size()-30);
		float previous15thCandleMA = movingAverages.get(movingAverages.size()-15);
		float currentCandleMA = movingAverages.get(89);
		
	
		
		double risePercentage = (previous90thCandleMA / currentCandleMA)*100;
		
		 if((currentCandleMA > previous15thCandleMA) && 
			(previous15thCandleMA > previous30thCandleMA) &&
			(previous30thCandleMA > previous45thCandleMA) && 
			(previous45thCandleMA > previous60thCandleMA) && 
			(previous60thCandleMA > previous90thCandleMA) &&
					(
							risePercentage < 10 || risePercentage < 20 || risePercentage < 30 || risePercentage < 40 || risePercentage < 50
 					)
		  )
		 {
			 isRising = true; 
		 }
		
		return isRising;
	}
	
	private static boolean isRisingLogic60(StockPrice candleData, List<Float> movingAverages) 
	{
		boolean isRising = false;
	
		
		
		//float previous90thCandleMA = movingAverages.get(0);
		float previous60thCandleMA = movingAverages.get(movingAverages.size()-60);
		float previous45thCandleMA = movingAverages.get(movingAverages.size()-45);
		float previous30thCandleMA = movingAverages.get(movingAverages.size()-30);
		float previous15thCandleMA = movingAverages.get(movingAverages.size()-15);
		float currentCandleMA = movingAverages.get(89);
		
	
		
		double risePercentage = (previous60thCandleMA / currentCandleMA)*100;
		
		 if((currentCandleMA > previous15thCandleMA) && 
			(previous15thCandleMA > previous30thCandleMA) &&
			(previous30thCandleMA > previous45thCandleMA) && 
			(previous45thCandleMA > previous60thCandleMA) && 
					(
							risePercentage < 10 || risePercentage < 20 || risePercentage < 30 || risePercentage < 40 || risePercentage < 50
 					)
		  )
		 {
			 isRising = true; 
		 }
		
		return isRising;
	}
	
	
	
	public static boolean isFallingMAForCandle(StockPrice candleData,List<StockPrice>  stockPriceWithMA,int scale)
	{
		List<StockPrice> candlesDataBeforeDateForNoOfDays = getCandlesForLastNosOfDayForInstrument(candleData, stockPriceWithMA, scale);
		
		if(CollectionUtils.isNotEmpty(candlesDataBeforeDateForNoOfDays))
		{
			List<Float> movingAverages = candlesDataBeforeDateForNoOfDays.stream().map(s->s.getMovingAverage()).collect(Collectors.toList());
			
			
			boolean isFalling = isFallingLogic2(candleData, movingAverages);
			

			if(isFalling)
			{
				//logger.info("FALLING >> ==================================================="+new SimpleDateFormat("dd-MMM-yyy").format(candleData.getMarketDate()));
				//movingAverages.forEach(p->logger.info(p.toString()));
			}
			
			return isFalling;
		}
		else
		{
			return false;
		}
	}
	
	private static boolean isFallingLogic2(StockPrice candleData, List<Float> movingAverages) 
	{
		boolean isFalling = false;
	
		
		
//		float previous90thCandleMA = movingAverages.get(0);
//		float previous60thCandleMA = movingAverages.get(movingAverages.size()-60);
//		float previous45thCandleMA = movingAverages.get(movingAverages.size()-45);
//		float previous30thCandleMA = movingAverages.get(movingAverages.size()-30);
//		float previous15thCandleMA = movingAverages.get(movingAverages.size()-15);
//		float currentCandleMA = movingAverages.get(89);
		
		
		
		
		float previous15thCandleMA = movingAverages.get(0);
		float currentCandleMA = movingAverages.get(1);
		
		
		double fallPercentage = ( currentCandleMA  / previous15thCandleMA )*100;
		
		 if((currentCandleMA < previous15thCandleMA) && 
					(
							fallPercentage < 10 || fallPercentage < 20 || fallPercentage < 30 || fallPercentage < 40 || fallPercentage < 50
 					)
		  )
		 {
			 isFalling = true; 
		 }
		
		return isFalling;
	}
	
	/**
	 * Marking entry
	 * @param candle
	 */
	public static void setBuyEntry(StockPrice candle)
	{
		StockConstants.ENTRY_MARGIN = getEntryMargin(candle);
		
		candle.setEntry(true);
		float buyPrice = candle.getHighPrice() + StockConstants.ENTRY_MARGIN;
		float stopLoss = candle.getLowPrice() - StockConstants.RISK_RATIO;
		float targetPrice = buyPrice+((buyPrice-stopLoss) * StockConstants.REWARD_RATIO);
		
		
		TradeEntryData tradeEntry = new TradeEntryData();
		tradeEntry.setBuyPrice(buyPrice);
		tradeEntry.setStopLossPrice(stopLoss);
		tradeEntry.setTargetPrice(targetPrice);
		tradeEntry.setLossPerUnit(buyPrice-stopLoss);
		
		
		candle.setTradeEntry(tradeEntry);
		
		tradeEntry.setQuantity(QuantityPlanner.getQuantity(candle));
		
		tradeEntry.setInvestment(tradeEntry.getQuantity()*buyPrice);
		
		candle.setTradeEntry(tradeEntry);
		
		candle.setOrderDetails("BUY : "+buyPrice+" STOPLOSS : "+stopLoss+" TARGET : "+targetPrice+" QUANTITY : "+tradeEntry.getQuantity()
								+"|INVESTMENT|"+tradeEntry.getInvestment());
		
		QuantityPlanner.trackTradeAmountForMonth(candle);
	}
	
	public static void setShortEntry(StockPrice candle)
	{
		StockConstants.ENTRY_MARGIN = getEntryMargin(candle);
		
		candle.setEntry(true);
		
		float sellPrice = candle.getLowPrice() - StockConstants.ENTRY_MARGIN;
		float stopLoss = candle.getHighPrice() + StockConstants.RISK_RATIO;
		float targetBuyPrice =  sellPrice - ((stopLoss-sellPrice) * StockConstants.REWARD_RATIO);
				
				
		TradeEntryData tradeEntry = new TradeEntryData();
		tradeEntry.setShortEntryPrice(sellPrice);
		tradeEntry.setStopLossPrice(stopLoss);
		tradeEntry.setShortTargetPrice(targetBuyPrice);
		tradeEntry.setLossPerUnit(stopLoss-sellPrice);
		
		
		candle.setTradeEntry(tradeEntry);
		
		tradeEntry.setQuantity(QuantityPlanner.getQuantity(candle));
		
		tradeEntry.setInvestment(tradeEntry.getQuantity()*sellPrice);
		
		candle.setTradeEntry(tradeEntry);
		
		candle.setOrderDetails("SHORT SELL : "+sellPrice+" STOPLOSS : "+stopLoss+" TARGET BUY : "+targetBuyPrice+" QUANTITY : "+tradeEntry.getQuantity()
								+"|INVESTMENT|"+tradeEntry.getInvestment());
		
		QuantityPlanner.trackTradeAmountForMonth(candle);
	}
	
	/**
	 * get all upcoming next candles after given candle
	 * @param candleData
	 * @param stockPriceWithMA
	 * @return
	 */
	public static List<StockPrice>  getCandlesAfter(StockPrice candleData,List<StockPrice>  stockPriceWithMA)
	{
		//logger.info("FINDING BACK CANDLE FOR "+candleData.toString());
		
		 Date marketDateForCandle = candleData.getMarketDate();
		 
		 if(null!=marketDateForCandle)
		 { 
			 List<StockPrice> candlesDataAfterDate =  stockPriceWithMA.stream().filter(s->s.getMarketDate().after(marketDateForCandle)).collect(Collectors.toList());
			 
			 candlesDataAfterDate.sort(Comparator.comparing(o -> o.getMarketDate()));
			 
			 //logger.info("=============================================================================");
			 //candlesDataAfterDate.forEach(stock->logger.info(stock.toString()));
			 //logger.info("=============================================================================");
			 
			 return candlesDataAfterDate;
			 
		 } 
		 return null;
	}
	
	/**
	 * Mark trade exit
	 * @param entryCandle
	 * @param stockPriceWithMA
	 */
	public static void setExit(StockPrice entryCandle,List<StockPrice>  stockPriceWithMA)
	{
		List<StockPrice>  stockPriceList = getCandlesAfter(entryCandle, stockPriceWithMA);
		
		StockConstants.ENTRY_MARGIN = getEntryMargin(entryCandle);
		
		float buyPrice = entryCandle.getHighPrice() + StockConstants.ENTRY_MARGIN;
		float stopLossPrice = entryCandle.getLowPrice() - StockConstants.RISK_RATIO;
		float targetPrice = buyPrice+((buyPrice-stopLossPrice) * StockConstants.REWARD_RATIO);
		
		
		for(StockPrice candle : stockPriceList)
		{
			if(candle.getLowPrice() <= targetPrice && targetPrice <=candle.getHighPrice())
			{
				entryCandle.setExit(true);
				//entryCandle.setPnlAmount(targetPrice-buyPrice);
				entryCandle.getTradeEntry().setTradeStatus(StockConstants.TARGET_EXIT);
				entryCandle.getTradeEntry().setTradeExitDate(candle.getMarketDate());
				entryCandle.getTradeEntry().setTradeDuration(getNosDaysBetweenDates(entryCandle.getMarketDate(), candle.getMarketDate()));
				entryCandle.setPnlAmount((targetPrice-buyPrice)*entryCandle.getTradeEntry().getQuantity());
				entryCandle.setTradeResult("TARGET_EXIT ON "+new SimpleDateFormat("dd-MMM-yyyy").format(candle.getMarketDate())+"|P/L|"+ entryCandle.getPnlAmount());
				break;
			}
			if(candle.getLowPrice() <= stopLossPrice && stopLossPrice <=candle.getHighPrice())
			{
				entryCandle.setExit(true);
				//entryCandle.setPnlAmount(-(buyPrice-stopLossPrice));
				entryCandle.getTradeEntry().setTradeStatus(StockConstants.STOP_LOSS);
				entryCandle.getTradeEntry().setTradeExitDate(candle.getMarketDate());
				entryCandle.getTradeEntry().setTradeDuration(getNosDaysBetweenDates(entryCandle.getMarketDate(), candle.getMarketDate()));
				entryCandle.setPnlAmount(-((buyPrice-stopLossPrice)*entryCandle.getTradeEntry().getQuantity()));
				entryCandle.setTradeResult("STOP_LOSS ON "+new SimpleDateFormat("dd-MMM-yyyy").format(candle.getMarketDate())+"|P/L|"+ entryCandle.getPnlAmount());
				break;
			}
			
		}
	}
	
	public static void setShortExit(StockPrice entryCandle,List<StockPrice>  stockPriceWithMA)
	{
		List<StockPrice>  stockPriceList = getCandlesAfter(entryCandle, stockPriceWithMA);
		
		StockConstants.ENTRY_MARGIN = getEntryMargin(entryCandle);
		
		float sellPrice = entryCandle.getLowPrice() - StockConstants.ENTRY_MARGIN;
		float stopLoss = entryCandle.getHighPrice() + StockConstants.RISK_RATIO;
		float targetBuyPrice =  sellPrice - ((stopLoss-sellPrice) * StockConstants.REWARD_RATIO);
		
		
		for(StockPrice candle : stockPriceList)
		{
			if(candle.getLowPrice() <= targetBuyPrice && targetBuyPrice <=candle.getHighPrice())
			{
				entryCandle.setExit(true);
				//entryCandle.setPnlAmount(targetPrice-buyPrice);
				entryCandle.getTradeEntry().setTradeStatus(StockConstants.TARGET_EXIT);
				entryCandle.getTradeEntry().setTradeExitDate(candle.getMarketDate());
				entryCandle.getTradeEntry().setTradeDuration(getNosDaysBetweenDates(entryCandle.getMarketDate(), candle.getMarketDate()));
				entryCandle.setPnlAmount((sellPrice-targetBuyPrice)*entryCandle.getTradeEntry().getQuantity());
				entryCandle.setTradeResult("TARGET_EXIT ON "+new SimpleDateFormat("dd-MMM-yyyy").format(candle.getMarketDate())+"|P/L|"+ entryCandle.getPnlAmount());
				break;
			}
			if(candle.getLowPrice() <= stopLoss && stopLoss <=candle.getHighPrice())
			{
				entryCandle.setExit(true);
				//entryCandle.setPnlAmount(-(buyPrice-stopLossPrice));
				entryCandle.getTradeEntry().setTradeStatus(StockConstants.STOP_LOSS);
				entryCandle.getTradeEntry().setTradeExitDate(candle.getMarketDate());
				entryCandle.getTradeEntry().setTradeDuration(getNosDaysBetweenDates(entryCandle.getMarketDate(), candle.getMarketDate()));
				entryCandle.setPnlAmount(((stopLoss-sellPrice)*entryCandle.getTradeEntry().getQuantity()));
				entryCandle.setTradeResult("STOP_LOSS ON "+new SimpleDateFormat("dd-MMM-yyyy").format(candle.getMarketDate())+"|P/L|"+ entryCandle.getPnlAmount());
				break;
			}
			
		}
	}
	
	public static long getNosDaysBetweenDates(Date entryDate, Date existDate) 
	{
	    long diff = existDate.getTime() - entryDate.getTime();
	    return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * testing method for checking immediate next candle after support candle
	 * @param stockPriceWithMA
	 */
	public static void prepareEntryCandles(List<StockPrice>  stockPriceWithMA)
	{
		for (int i = 0; i < stockPriceWithMA.size(); i++) 
		{
			StockPrice candle = stockPriceWithMA.get(i);
			
			if(candle.isHasSupport() && candle.isGreenCandle()  && (i+1)<stockPriceWithMA.size() && !candle.isEntry())
			{
				StockPrice nextCandle = stockPriceWithMA.get(i+1);
				
				if(null!=nextCandle)
				{
					boolean isNextCandleGreen = Indicators.isGreenCandleWithGoodBody(nextCandle);
					
					//logger.info("isNextCandleGreen "+isNextCandleGreen);
					if(isNextCandleGreen)
					{
						//preparing next candle for entry
						nextCandle.setEntry(true);
						nextCandle.setHasSupport(true);
						nextCandle.setGreenCandle(true);
						//avoiding first support candle as next green candle found
						candle.setHasSupport(false);
						candle.setGreenCandle(false);
						
						stockPriceWithMA.set(i+1, nextCandle);
						stockPriceWithMA.set(i, candle);
					}
					else
					{
						//avoiding first support candle as next green candle not found
						candle.setHasSupport(false);
						candle.setGreenCandle(false);
						stockPriceWithMA.set(i, candle);
					}
				}
				
			}
		}
	}
	
	/**
	 * Identify minimum height candle must have
	 * @param candle
	 * @return
	 */
	public static int getIdealCandleHeight(StockPrice  candle)
	{
		int idealHeaight = 0;
		
		if(candle.getOpenPrice()<=100)
		{
			idealHeaight = 5;
		}
		else if(candle.getOpenPrice() > 100 && candle.getOpenPrice()<=900)
		{
			idealHeaight = 15;
		}
		else
		{
			idealHeaight = 20;
		}
		return idealHeaight;
	}
	
	/**
	 * Identify minimum points of rise of moving average for given candle
	 * @param candle
	 * @return
	 */
	public static int getIdealRisingCurve(StockPrice  candle)
	{
		int idealRise = 0;
		
		if(candle.getOpenPrice()<=100)
		{
			idealRise = 2;
		}
		else if(candle.getOpenPrice() > 100 && candle.getOpenPrice()<=900)
		{
			idealRise = 4;
		}
		else
		{
			idealRise = 5;
		}
		
		return idealRise;
	}
	
	/**
	 * Identify entry margin for given candle while placing order
	 * @param candle
	 * @return
	 */
	public static float getEntryMargin(StockPrice  candle)
	{
		float entryMargin = 0.0F;
		
		if(candle.getOpenPrice()<=400)
		{
			entryMargin = 0.5F;
		}
		else
		{
			entryMargin = 1.0F;
		}
		
		return entryMargin;
	}
	
	public static String getSymbolForCompanyName(String companyName)
	{
		String COMMA_DELIMITER = ",";
		
		String equitySymbolFile = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\EQUITY_LIST.csv";
		
		int count = 0;
		try 
		{
			
			BufferedReader br = new BufferedReader(new FileReader(equitySymbolFile));
			    String line;
			    while ((line = br.readLine()) != null) {
			    	
			    	if(count != 0)
			    	{
			    		 	String[] lineData = line.split(COMMA_DELIMITER);
					        
			            	String name = lineData[1].replace("\"", "");
			            	if(StringUtils.containsIgnoreCase(name, companyName) || StringUtils.containsIgnoreCase(name,companyName.split(" ")[0]))
			            	{
			            		return lineData[0].replace("\"", "");
			            	}
			    	}
			       
			    	count++;
			        
			    }
			
			
		} 
		catch (Exception e) {
			System.out.println("Exception while reading CSV "+e);
		}
		
		return null;
			
	}
}
