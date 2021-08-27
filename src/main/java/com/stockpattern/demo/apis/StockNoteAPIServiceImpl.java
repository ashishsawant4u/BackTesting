package com.stockpattern.demo.apis;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.stockpattern.demo.common.SpringWebConfigUtils;
import com.stockpattern.demo.models.StockPrice;

import io.samco.client.SamcoConstants;
import io.samco.client.api.HistoricalCandleDataApi;
import io.samco.client.api.UserLoginApi;
import io.samco.client.model.CandleData;
import io.samco.client.model.HistoricalCandleResponse;
import io.samco.client.model.LoginRequest;
import io.samco.client.model.LoginResponse;

@Component("stockNoteAPIService")
public class StockNoteAPIServiceImpl implements StockNoteAPIService {

	Logger logger = LoggerFactory.getLogger(StockNoteAPIServiceImpl.class);
	
	public String STOCKNOTE_STATUS_SUCCESS = "Success";

	
	@Resource(name = "stocknoteCandleDataConverter")
	StocknoteCandleDataConverter stocknoteCandleDataConverter;
	
	@Override
	public String getSessionToken() 
	{
		
		try 
		{
			String userId = "DA47742";
			String password = "May@2021";
			String yob = "1990";
			
			
//			HttpSession session = SpringWebConfigUtils.getHttpSession();
//			
//			String stockNoteSessionKey = null!= session.getAttribute(SpringWebConfigUtils.STOCKNOTE_SESSION_KEY) ? (String)session.getAttribute(SpringWebConfigUtils.STOCKNOTE_SESSION_KEY) : "";
//			
//			long stockNoteSessionKeyLastFetchTime = null!= session.getAttribute(SpringWebConfigUtils.STOCKNOTE_SESSION_KEY_LAST_FETCH_TIME) ? (Long)session.getAttribute(SpringWebConfigUtils.STOCKNOTE_SESSION_KEY_LAST_FETCH_TIME)  : 0; 
//			
//			
//			int diffhrs = 100;
//			
//			if(0!=stockNoteSessionKeyLastFetchTime)
//			{
//				Date currenDate = new Date();
//				diffhrs = (int)TimeUnit.MILLISECONDS.toHours(stockNoteSessionKeyLastFetchTime - currenDate.getTime());
//			}
//			
//			if(StringUtils.isNotBlank(stockNoteSessionKey) &&  0!=stockNoteSessionKeyLastFetchTime && diffhrs < 24)
//			{
//				return stockNoteSessionKey;
//			}
//			else
//			{
//				logger.info("STOCKNOTE Session Key Expired...Fetching New Session Key...");
//				UserLoginApi userLoginApi = new UserLoginApi();
//				LoginRequest loginRequest = new LoginRequest();
//				loginRequest.setUserId(userId);
//				loginRequest.setPassword(password);
//				loginRequest.setYob(yob);
//				LoginResponse loginResponse = userLoginApi.login(loginRequest);
//				session.setAttribute(SpringWebConfigUtils.STOCKNOTE_SESSION_KEY, loginResponse.getSessionToken());
//				session.setAttribute(SpringWebConfigUtils.STOCKNOTE_SESSION_KEY_LAST_FETCH_TIME,new Date().getTime());
//				return loginResponse.getSessionToken();
//			}
			
			return "fd3e00214b468f8042d7274b1cc6e2fa";
			
		} 
		catch (Exception e) 
		{
			logger.info("StockNoteAPIService getSessionToken Failed "+e.getMessage());
		}
		
		return null;
	}



	@Override
	public List<StockPrice> findTradesForMA44Rising90ForDate(String symbol,Date date) 
	{
		try 
		{
			Calendar cal = Calendar.getInstance();  
			cal.setTime(date);
			cal.add(Calendar.MONTH, -8); 
			Date date5MonthsBack = cal.getTime();
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String fromDate = dateFormat.format(date5MonthsBack);
			String toDate = dateFormat.format(date);
			
			HistoricalCandleDataApi historicalCandleDataApi = new HistoricalCandleDataApi();
			
			List<CandleData> candleDataList = new ArrayList<CandleData>();
			
			String stockNoteSessionKey = getSessionToken();
			
			
			HistoricalCandleResponse historicalCandleData = null;
			try 
			{
				historicalCandleData = historicalCandleDataApi.getHistoricalCandleData(stockNoteSessionKey, symbol, fromDate, SamcoConstants.EXCHANGE_NSE, toDate);
				
				if(StringUtils.equals(historicalCandleData.getStatus(), STOCKNOTE_STATUS_SUCCESS))
				{
					candleDataList.addAll(historicalCandleData.getHistoricalCandleData());
				}

			} 
			catch (Exception e) 
			{
				logger.info("Failure for "+symbol);
			}
			
			
			List<StockPrice> stockPriceList = candleDataList.stream().map(c->stocknoteCandleDataConverter.convert(c)).collect(Collectors.toList());
			stockPriceList.stream().forEach(s->s.setSymbol(symbol));
			
			return stockPriceList;
			
		} 
		catch (Exception e) 
		{
			logger.info("StockNoteAPIService findTradesForMA44Rising90ForDate Failed "+e.getMessage());
		}

		return null;
	}
	
	

}
