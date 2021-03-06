package com.stockpattern.demo.indicators;

public class StockConstants {
	
	
	//difference between moving average and low price of the candle
	public static int SUPPORT_PRICE_DIFFERENCE = 2;
	
	//rise in the moving average in last RISING_PRICE_MAX_SCALE number of candles
	public static int RISING_MARGIN_MIN_SCALE = 50;
	
	//public static String CSV_FILE_LOC = "C:\\Users\\ashis\\Downloads\\archive-eod\\";
	//public static String CSV_FILE_LOC = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\SCRIP\\";
	public static String CSV_FILE_LOC = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\SCRIP\\data-symbol\\";
	
	//moving average should be rising for number of candles
	public static int RISING_PRICE_MIN_SCALE = 15;
	
	public static int MIN_BODY_SIZE_GREEN_CANDLE_FOR_BUY = 1;
	
	public static int MIN_BODY_SIZE_RED_CANDLE_FOR_SHORT = 1;
	
	public static float ENTRY_MARGIN = 0.5F;
	
	public static int MOVING_AVERAGE_SCALE = 50;
	
	public static int CANDLE_HEIGHT_RANGE_10 = 10;
	
	public static int CANDLE_HEIGHT_RANGE_15 = 15;
	
	public static String STOP_LOSS = "STOP LOSS";

	public static String TARGET_EXIT = "TARGET EXIT";
	
	public static float REWARD_RATIO = 4.0F;
	
	public static float RISK_RATIO = 2.0F;

}
