package com.stockpattern.demo.indicators;

import java.util.List;

import com.stockpattern.demo.models.StockPrice;

public class Indicators {
	
	
	public static float getMovingAverage(List<Float> priceList,int averageScale)
    {
		float sum = 0.0F;
		
		
		for(float price : priceList)
		{	
			sum = sum +price;
		}
		
		float avg = sum/averageScale;
		
		return avg; 
    }
	
	public static boolean isRising(List<Float> price,int scale)
	{
		boolean ascending = true;
		int listSize = price.size();
		
		scale = listSize < scale ? listSize : scale;
		
		List<Float> subList = price.subList(listSize-scale, listSize);
		
		final float[] arr = new float[subList.size()];
		int index = 0;
		for (final Float value: subList) {
		   arr[index++] = value;
		}
		
		for (int i = 1; i < arr.length && ascending; i++) {
		    ascending = ascending && arr[i] >= arr[i-1];
		}
		
		if(ascending)
		{
			System.out.println("RISING............."+subList);
			
		}
		
		return ascending;
	}
	
	public static boolean isBullish(List<Float> priceList,List<Float> averragePriceList,int scale)
	{	
		int listSize = priceList.size();
		
		scale = listSize < scale ? listSize : scale;
		
		List<Float> pricesubList = priceList.subList(listSize-scale, listSize);
		
		List<Float> avgPricesubList = averragePriceList.subList(listSize-scale, listSize);
		
		boolean isBullish = true;
		
		for (int i = 0; i < pricesubList.size(); i++) 
		{	
			if(pricesubList.get(i) <= avgPricesubList.get(i))
			{
				System.out.println("BEARISH | HIGH "+pricesubList.get(i)+" AVG "+avgPricesubList.get(i));
				isBullish = false;
				break;
			}
		}
		
		boolean isMA_Rising = isRising(averragePriceList, scale);
		
		if(isBullish && isMA_Rising)
		{	
			System.out.println("IS BULLISH WITH MA RISING "+isBullish);
		}
		
		return (isBullish && isMA_Rising);
	}
	
	public static boolean hasSupport(StockPrice candle,float maxDifference)
	{
		boolean hasSupport = false;
		
		boolean hasHighWick = (candle.getHighPrice() - candle.getOpenPrice())>=2;
		
		boolean hasLowWick = (candle.getClosePrice() - candle.getLowPrice()) >=2;
		
		boolean hasGoodWicks = (hasHighWick && hasLowWick);
		
		boolean isMAWithingCandleHeightRange = (candle.getHighPrice() > candle.getMovingAverage() && candle.getLowPrice() < candle.getMovingAverage());
		
		boolean isCandleLowCloseToMA = ((candle.getLowPrice() > candle.getMovingAverage()) && (candle.getLowPrice() - candle.getMovingAverage())<=2);
		
		boolean isCandleHeightWithinRange = (candle.getHighPrice()-candle.getLowPrice())<= Utils.getIdealCandleHeight(candle);
		
		boolean isCandleOpenAboveMA = candle.getOpenPrice() > candle.getMovingAverage();
		
		if((isMAWithingCandleHeightRange || isCandleLowCloseToMA) && hasGoodWicks && isCandleHeightWithinRange)
		{		
			hasSupport = true;
		}
		
		return hasSupport;
	}
	
	public static boolean isGreenCandle(StockPrice candle)
	{
		return candle.getClosePrice()>candle.getOpenPrice();
	}
	
	public static boolean isRedCandle(StockPrice candle)
	{
		return candle.getClosePrice()<candle.getOpenPrice();
	}
	
	public static boolean isGreenCandleWithGoodBody(StockPrice candle)
	{
		
		if(isGreenCandle(candle))
		{
			float bodySize = candle.getClosePrice()-candle.getOpenPrice();
			
			boolean goodbodySize = (bodySize >= StockConstants.MIN_BODY_SIZE_GREEN_CANDLE_FOR_BUY);
			
			float highWickSize = candle.getHighPrice() - candle.getClosePrice();
			float lowWickSize = candle.getOpenPrice() - candle.getLowPrice();
			
			return goodbodySize && (bodySize>highWickSize) && (bodySize>lowWickSize);
		}
		
		return false;
	}

}
