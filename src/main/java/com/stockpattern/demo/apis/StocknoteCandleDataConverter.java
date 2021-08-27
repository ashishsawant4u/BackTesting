package com.stockpattern.demo.apis;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.stockpattern.demo.models.StockPrice;

import io.samco.client.model.CandleData;

@Component("stocknoteCandleDataConverter")
public class StocknoteCandleDataConverter  implements Converter<CandleData, StockPrice>{

	@Override
	public StockPrice convert(CandleData source) 
	{
		StockPrice stockPrice = new StockPrice();
		
		stockPrice.setOpenPrice(Float.parseFloat(source.getOpen()));
		stockPrice.setHighPrice(Float.parseFloat(source.getHigh()));
		stockPrice.setLowPrice(Float.parseFloat(source.getLow()));
		stockPrice.setClosePrice(Float.parseFloat(source.getClose()));
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try 
		{
			stockPrice.setMarketDate(dateFormat.parse(source.getDate()));
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		
		return stockPrice;
	}

}
