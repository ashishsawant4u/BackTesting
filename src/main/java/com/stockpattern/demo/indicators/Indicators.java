package com.stockpattern.demo.indicators;

import java.util.List;

import com.stockpattern.demo.models.StockPrice;

public class Indicators {
	
	/**
	 * Find moving average for given price list
	 * @param priceList
	 * @param averageScale
	 * @return
	 */
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
	
	/**
	 * Check whether candle is taking support on moving average
	 * @param candle
	 * @param maxDifference
	 * @return
	 */
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
