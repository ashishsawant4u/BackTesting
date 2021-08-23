package com.stockpattern.demo.indicators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Ordering;
import com.stockpattern.demo.models.StockPrice;

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
	
	public static List<StockPrice>  getCandlesData(String instrument,Date fromDate)
	{
		//List<StockPrice> candleList = readUsingTextFile(instrument);
		
		List<StockPrice> candleList = readUsingCSVFile(instrument);
		
		 
		 if(null!=fromDate)
		 {
			 return candleList.stream().filter(s->!s.getMarketDate().before(fromDate)).collect(Collectors.toList());
		 }
		
		 return candleList;
	}
	
	/*
	 * private static List<StockPrice> readUsingCSVFile (String instrument) { String
	 * COMMA_DELIMITER = ","; List<StockPrice> candleList = new
	 * ArrayList<StockPrice>(); String FILE_LOC =
	 * "C:\\Users\\ashis\\Downloads\\EOD_DATA\\archive-01\\data_1990_2020\\stock_data\\";
	 * int count = 0; try {
	 * 
	 * BufferedReader br = new BufferedReader(new
	 * FileReader(FILE_LOC+instrument+".csv")); String line; while ((line =
	 * br.readLine()) != null) {
	 * 
	 * if(count != 0) { String[] lineData = line.split(COMMA_DELIMITER);
	 * 
	 * Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(lineData[0]);
	 * 
	 * StockPrice candle = new StockPrice(); candle.setSymbol(instrument);
	 * candle.setMarketDate(currentDate);
	 * candle.setOpenPrice(Float.parseFloat(lineData[4]));
	 * candle.setHighPrice(Float.parseFloat(lineData[5]));
	 * candle.setLowPrice(Float.parseFloat(lineData[6]));
	 * candle.setClosePrice(Float.parseFloat(lineData[8]));
	 * 
	 * candleList.add(candle); }
	 * 
	 * count++;
	 * 
	 * }
	 * 
	 * 
	 * } catch (Exception e) { System.out.println("Exception while reading CSV "+e);
	 * }
	 * 
	 * return candleList; }
	 */
	
	private static List<StockPrice> readUsingCSVFile (String instrument)
	{
		String COMMA_DELIMITER = ",";
		List<StockPrice> candleList = new ArrayList<StockPrice>();
		//String FILE_LOC = "C:\\Users\\ashis\\Downloads\\EOD_DATA\\archive-01\\data_1990_2020\\stock_data\\";
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
					        
			            	Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(lineData[0]);  
			            
			            	StockPrice candle = new StockPrice();
			            	candle.setSymbol(instrument);
			            	candle.setMarketDate(currentDate);
			            	candle.setOpenPrice(Float.parseFloat(lineData[4]));
			            	candle.setHighPrice(Float.parseFloat(lineData[5]));
			            	candle.setLowPrice(Float.parseFloat(lineData[6]));
			            	candle.setClosePrice(Float.parseFloat(lineData[8]));
			            	
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

	private static List<StockPrice> readUsingTextFile(String instrument) 
	{
		String fileName = "C:\\KeyStocks-Lite\\EOD\\data\\"+instrument+".txt";
		
		List<StockPrice> candleList = new ArrayList<StockPrice>();
		
		
		 try {
		        BufferedReader in = new BufferedReader(new FileReader(fileName));
		        String str;

		        while ((str = in.readLine())!= null) {
		            String[] lineData = str.split(",");
		            	Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(lineData[0]);  
		            
		            	StockPrice candle = new StockPrice();
		            	candle.setSymbol(instrument);
		            	candle.setMarketDate(currentDate);
		            	candle.setOpenPrice(Float.parseFloat(lineData[1]));
		            	candle.setHighPrice(Float.parseFloat(lineData[2]));
		            	candle.setLowPrice(Float.parseFloat(lineData[3]));
		            	candle.setClosePrice(Float.parseFloat(lineData[4]));
		            	
		            	candleList.add(candle);	
		            	
		        }
		        in.close();
		    } catch (Exception e) {
		        System.out.println("File Read Error");
		    }
		return candleList;
	}
	
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
	
	public static boolean isRisinngMAForCandle(StockPrice candleData,List<StockPrice>  stockPriceWithMA,int scale)
	{
		List<StockPrice> candlesDataBeforeDateForNoOfDays = getCandlesForLastNosOfDayForInstrument(candleData, stockPriceWithMA, scale);
		
		if(CollectionUtils.isNotEmpty(candlesDataBeforeDateForNoOfDays))
		{
			List<Float> movingAverages = candlesDataBeforeDateForNoOfDays.stream().map(s->s.getMovingAverage()).collect(Collectors.toList());
			
			boolean isRising = Ordering.natural().isOrdered(movingAverages);
			
			isRising = (movingAverages.get(movingAverages.size()-1) - movingAverages.get(0)) >= Utils.getIdealRisingCurve(candleData);
			
			if(isRising)
			{
				//logger.info("RISING >> ==================================================="+new SimpleDateFormat("dd-MMM-yyy").format(candleData.getMarketDate()));
				//movingAverages.forEach(p->logger.info(p.toString()));
				//isRising = (movingAverages.get(movingAverages.size()-1) - movingAverages.get(0)) >= Utils.getIdealRisingCurve(candleData); // rising curve upwards
			}
			
			return isRising;
		}
		else
		{
			return false;
		}
	}
	
	public static void setBuyEntry(StockPrice candle)
	{
		StockConstants.ENTRY_MARGIN = getEntryMargin(candle);
		
		candle.setEntry(true);
		float buyPrice = candle.getHighPrice() + StockConstants.ENTRY_MARGIN;
		float stopLoss = candle.getLowPrice() - StockConstants.ENTRY_MARGIN;
		float targetPrice = buyPrice+((buyPrice-stopLoss)*2);
		candle.setOrderDetails("BUY : "+buyPrice+" STOPLOSS : "+stopLoss+" TARGET "+targetPrice);
	}
	
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
	
	
	public static void setExit(StockPrice entryCandle,List<StockPrice>  stockPriceWithMA)
	{
		List<StockPrice>  stockPriceList = getCandlesAfter(entryCandle, stockPriceWithMA);
		
		float buyPrice = entryCandle.getHighPrice()+1;
		float stopLossPrice = entryCandle.getLowPrice()-1;
		float targetPrice = buyPrice+((buyPrice-stopLossPrice)*2);
		
		
		for(StockPrice candle : stockPriceList)
		{
			if(candle.getLowPrice() <= targetPrice && targetPrice <=candle.getHighPrice())
			{
				entryCandle.setExit(true);
				entryCandle.setTradeResult("TARGET_EXIT ON "+new SimpleDateFormat("dd-MMM-yyyy").format(candle.getMarketDate())+"|P/L|"+ (targetPrice-buyPrice));
				entryCandle.setPnlAmount(targetPrice-buyPrice);
				break;
			}
			if(candle.getLowPrice() <= stopLossPrice && stopLossPrice <=candle.getHighPrice())
			{
				entryCandle.setExit(true);
				entryCandle.setTradeResult("STOP_LOSS ON "+new SimpleDateFormat("dd-MMM-yyyy").format(candle.getMarketDate())+"|P/L|-"+ (buyPrice-stopLossPrice));
				entryCandle.setPnlAmount(-(buyPrice-stopLossPrice));
				break;
			}
			
		}
	}
	
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
}
