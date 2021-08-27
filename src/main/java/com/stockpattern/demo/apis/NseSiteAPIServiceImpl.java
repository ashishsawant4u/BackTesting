package com.stockpattern.demo.apis;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.opencsv.CSVWriter;
import com.stockpattern.demo.models.StockPrice;


@Component("nseSiteAPIService")
public class NseSiteAPIServiceImpl implements NseSiteAPIService 
{
	Logger logger = LoggerFactory.getLogger(NseSiteAPIServiceImpl.class);
	
	@Override
	public List<StockPrice> getNSEDailyEodDataForSymbol(String symbol, Date forDate)  
	{	
		Calendar cal = Calendar.getInstance();  
		cal.setTime(forDate);
		cal.add(Calendar.YEAR, -2); 
		Date previousDate = cal.getTime();
		
		List<StockPrice> candleList = getCandlesData(symbol,previousDate,forDate);
		
		Collections.sort(candleList, (c1, c2) -> c1.getMarketDate().compareTo(c2.getMarketDate()));
		
		return candleList;
	}

	public static List<StockPrice>  getCandlesData(String instrument,Date fromDate,Date toDate)
	{	
		List<StockPrice> candleList = readUsingCSVFile(instrument);
		
		 
		 if(null!=fromDate)
		 {
			 return candleList.stream().filter(s->!s.getMarketDate().before(fromDate) && !s.getMarketDate().after(toDate)).collect(Collectors.toList());
		 }
		
		 return candleList;
	}
	
	private static List<StockPrice> readUsingCSVFile (String instrument)
	{
		String COMMA_DELIMITER = ",";
		List<StockPrice> candleList = new ArrayList<StockPrice>();
		
		String symbolwiseFileDirectory = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\SCRIP\\data-symbol\\";
		
		String FILE_LOC = symbolwiseFileDirectory;
		
		int count = 0;
		try 
		{
			
			BufferedReader br = new BufferedReader(new FileReader(FILE_LOC+instrument+".csv"));
			    String line;
			    while ((line = br.readLine()) != null) {
			    	
			    	if(count != 0)
			    	{
			    		 	String[] lineData = line.split(COMMA_DELIMITER);
					        
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
	
	

	@Override
	public String downloadHistoricalDailyEOD() 
	{
		purgeEODDataDirectories();
		downloadEODZIP();
		
		return "EOD Data Fetched Successful";
	}
	
	
	private void purgeEODDataDirectories() 
	{
		String zipFileDirectory = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\SCRIP\\data-zip\\";
		String csvFileDirectory = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\SCRIP\\data-csv\\";
		String symbolwiseFileDirectory = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\SCRIP\\data-symbol\\";
		
		Arrays.stream(new File(zipFileDirectory).listFiles()).forEach(File::delete);
		Arrays.stream(new File(csvFileDirectory).listFiles()).forEach(File::delete);
		Arrays.stream(new File(symbolwiseFileDirectory).listFiles()).forEach(File::delete);
	}
	
	/**
	 * Download Zip File For daily eod data for date range
	 * https://www1.nseindia.com/content/historical/EQUITIES/2020/JAN/cm02JAN2010bhav.csv.zip
	 */
	private void downloadEODZIP() 
	{
		Date currentDate = new Date();
		
		Calendar cal = Calendar.getInstance();  
		cal.setTime(currentDate);
		cal.add(Calendar.YEAR, -10); 
		Date previousDate = cal.getTime();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
		
		String zipFileDirectory = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\SCRIP\\data-zip\\";
		String csvFileDirectory = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\SCRIP\\data-csv\\";
		
		
		try  
		{
			
			LocalDate start = previousDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate end = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			for (LocalDate ld = start; ld.isBefore(end); ld = ld.plusDays(1)) 
			{
				String marketDateStr = ld.getDayOfMonth()+""+ld.getMonthValue()+""+ld.getYear();
				Date marketDate = Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
				String day=  new SimpleDateFormat("dd").format(marketDate);
				String month = new SimpleDateFormat("MMM").format(marketDate).toUpperCase();
				String year = new SimpleDateFormat("yyyy").format(marketDate);
				
				String baseUrl = "https://www1.nseindia.com/content/historical/EQUITIES/"+year+"/"+month+"/";
				String fileName = "cm"+day+month+year+"bhav.csv.zip";
				String FILE_URL = baseUrl + fileName;
				logger.info(FILE_URL);
				try
				{
					downloadZip(zipFileDirectory, fileName, FILE_URL,csvFileDirectory);
				    
				}
				catch (Exception e) {
					logger.info("File Fetching Failed "+marketDateStr+" >> "+e);
				}
				
			}
			
			prepareSymbolwiseFiles();
			
		} 
		catch (Exception e) 
		{
			logger.info("NseSiteAPIServiceImpl downloadEODZIP Failed "+e.getMessage());
		}
	}

	
	/**
	 * Method to download zip file by hitting specific date file url
	 * https://www1.nseindia.com/content/historical/EQUITIES/2020/JAN/cm02JAN2010bhav.csv.zip
	 * @param zipFileDirectory
	 * @param fileName
	 * @param FILE_URL
	 * @param csvFileDirectory
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws FileNotFoundException
	 */
	private void downloadZip(String zipFileDirectory, String fileName, String FILE_URL,String csvFileDirectory)
			throws IOException, MalformedURLException, FileNotFoundException 
	{
		URL url = new URL(FILE_URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		conn.setRequestProperty("Referer", "https://www1.nseindia.com/products/content/equities/equities/archieve_eq.htm");
		
		int respCode = conn.getResponseCode(); 
		
		if(respCode!=404)
		{
			BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
			FileOutputStream fileOS = new FileOutputStream(zipFileDirectory+fileName);
			
			byte data[] = new byte[1024];
			int byteContent;
			while ((byteContent = inputStream.read(data, 0, 1024)) != -1) 
			{
			    fileOS.write(data, 0, byteContent);
			}
			logger.info(fileName+" download complete");
			
			extractFile(zipFileDirectory+fileName,csvFileDirectory,fileName.replace(".zip", ""));
		}
		
	}
	
	/**
	 * Extract the downloaded zip file to get csv
	 * @param zipFilePath
	 * @param destDir
	 * @param fileName
	 * @throws IOException
	 */
	private void extractFile(String zipFilePath,String destDir,String fileName) throws IOException 
	{
		File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                //String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
            logger.info(fileName+" extract complete");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
	
	/**
	 * create symbol wise csv file for daily eod data
	 */
	private void prepareSymbolwiseFiles()
	{
		String csvFileDirectory = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\SCRIP\\data-csv\\";
		String symbolwiseFileDirectory = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\SCRIP\\data-symbol\\";
		String COMMA_DELIMITER = ",";
		
		File[] files = new File(csvFileDirectory).listFiles();
		  
		  for (File file : files)
		  { 
			  if (file.isFile()) 
			  {
				  int count = 0;
				  try 
					{
						
						BufferedReader br = new BufferedReader(new FileReader(file));
						    String line;
						    while ((line = br.readLine()) != null) {
						    	
						    	if(count != 0)
						    	{
						    		 	String[] lineData = line.split(COMMA_DELIMITER);
								        
						    		 	if(lineData[1].equals("EQ"))
						    		 	{
						    		 		String symbol = lineData[0];
						    		 		String open = lineData[2];
						    		 		String high = lineData[3];
						    		 		String low = lineData[4];
						    		 		String close = lineData[5];
						    		 		String marketDate = lineData[10];
						    		 		
						    		 		String symbolFilePath = symbolwiseFileDirectory + symbol + ".csv";
						    		 		File symbolFile = new File(symbolFilePath);
						    		 		
						    		 		if(symbolFile.exists())
						    		 		{
						    		 			//append to existing symbol wise file
						    		 			 CSVWriter existingWriter = new CSVWriter(new FileWriter(symbolFilePath, true));
						    		 			 String[] newRow = { symbol, open, high, low, close, marketDate };
						    		 			 existingWriter.writeNext(newRow);
						    		 			 existingWriter.close();
						    		 		}
						    		 		else
						    		 		{
						    		 			//create new file for symbol
						    		 			FileWriter outputfile = new FileWriter(symbolFile);
						    		 			CSVWriter writer = new CSVWriter(outputfile);
						    		 			String[] header = { "SYMBOL", "OPEN", "HIGH" ,"LOW","CLOSE","MARKETDATE"};
						    		 	        writer.writeNext(header);
						    		 	        String[] row = { symbol, open, high, low, close, marketDate };
						    		 	        writer.writeNext(row);
						    		 	        writer.close();
						    		 		}
						    		 		
						    		 	}
						    		 	
						    	}
						       
						    	count++;
						        
						    }
						
						
					} 
					catch (Exception e) {
						logger.info("Exception while reading CSV "+e);
					}
			  }
			  logger.info(file.getName()+" done");
		  }	  
		  
		  logger.info("Symbolwise File Created Successfully.");
	}
	
	/**
	 * Download Zip File For daily eod data for date range
	 * https://www1.nseindia.com/content/historical/EQUITIES/2020/JAN/cm02JAN2010bhav.csv.zip
	 */
	@Override
	public String updateHistoricalDailyEOD() 
	{
		List<StockPrice> candleList = readUsingCSVFile("ACC");		
		Collections.sort(candleList, (c1, c2) -> c1.getMarketDate().compareTo(c2.getMarketDate()));
		
		
		Calendar cal = Calendar.getInstance();  
		cal.setTime(candleList.get(candleList.size()-1).getMarketDate());
		cal.add(Calendar.DATE, 1);
		Date previousDate = cal.getTime();
		Date currentDate = new Date();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		
		String latestFilesDir =  "latest_"+dateFormat.format(previousDate)+"-TO-"+dateFormat.format(currentDate)+"\\";
		String zipFileDirectory = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\SCRIP\\data-zip\\"+latestFilesDir;
		String csvFileDirectory = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\SCRIP\\data-csv\\"+latestFilesDir;
		
		
		try  
		{
			File zipFileDirectoryRef = new File(zipFileDirectory);
			if (zipFileDirectoryRef.exists())
			{
				zipFileDirectoryRef.delete();
				zipFileDirectoryRef.mkdir();
			}
			else
			{
				zipFileDirectoryRef.mkdir();
			}
			
			File csvFileDirectoryRef = new File(csvFileDirectory);
			if (csvFileDirectoryRef.exists())
			{
				csvFileDirectoryRef.delete();
				csvFileDirectoryRef.mkdir();
			}
			else
			{
				csvFileDirectoryRef.mkdir();
			}
			
			
			LocalDate start = previousDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate end = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			for (LocalDate ld = start; ld.isBefore(end); ld = ld.plusDays(1)) 
			{
				String marketDateStr = ld.getDayOfMonth()+""+ld.getMonthValue()+""+ld.getYear();
				Date marketDate = Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
				String day=  new SimpleDateFormat("dd").format(marketDate);
				String month = new SimpleDateFormat("MMM").format(marketDate).toUpperCase();
				String year = new SimpleDateFormat("yyyy").format(marketDate);
				
				String baseUrl = "https://www1.nseindia.com/content/historical/EQUITIES/"+year+"/"+month+"/";
				String fileName = "cm"+day+month+year+"bhav.csv.zip";
				String FILE_URL = baseUrl + fileName;
				logger.info(FILE_URL);
				try
				{
					downloadZip(zipFileDirectory, fileName, FILE_URL,csvFileDirectory);
				    
				}
				catch (Exception e) {
					logger.info("File Fetching Failed "+marketDateStr+" >> "+e);
				}
				
			}
			
			updateSymbolwiseFiles(csvFileDirectory);
			
		} 
		catch (Exception e) 
		{
			logger.info("NseSiteAPIServiceImpl downloadEODZIP Failed "+e.getMessage());
		}
		
		return "EOD Data Updates Successful";
	}
	
	private void updateSymbolwiseFiles(String csvFileDirectory)
	{
		String symbolwiseFileDirectory = "C:\\Users\\ashis\\Downloads\\archive\\Datasets\\SCRIP\\data-symbol\\";
		String COMMA_DELIMITER = ",";
		
		File[] files = new File(csvFileDirectory).listFiles();
		  
		  for (File file : files)
		  { 
			  if (file.isFile()) 
			  {
				  int count = 0;
				  try 
					{
						
						BufferedReader br = new BufferedReader(new FileReader(file));
						    String line;
						    while ((line = br.readLine()) != null) {
						    	
						    	if(count != 0)
						    	{
						    		 	String[] lineData = line.split(COMMA_DELIMITER);
								        
						    		 	if(lineData[1].equals("EQ"))
						    		 	{
						    		 		String symbol = lineData[0];
						    		 		String open = lineData[2];
						    		 		String high = lineData[3];
						    		 		String low = lineData[4];
						    		 		String close = lineData[5];
						    		 		String marketDate = lineData[10];
						    		 		
						    		 		String symbolFilePath = symbolwiseFileDirectory + symbol + ".csv";
						    		 		File symbolFile = new File(symbolFilePath);
						    		 		
						    		 		if(symbolFile.exists())
						    		 		{
						    		 			//append to existing symbol wise file
						    		 			 CSVWriter existingWriter = new CSVWriter(new FileWriter(symbolFilePath, true));
						    		 			 String[] newRow = { symbol, open, high, low, close, marketDate };
						    		 			 existingWriter.writeNext(newRow);
						    		 			 existingWriter.close();
						    		 		}
						    		 		else
						    		 		{
						    		 			//create new file for symbol
						    		 			FileWriter outputfile = new FileWriter(symbolFile);
						    		 			CSVWriter writer = new CSVWriter(outputfile);
						    		 			String[] header = { "SYMBOL", "OPEN", "HIGH" ,"LOW","CLOSE","MARKETDATE"};
						    		 	        writer.writeNext(header);
						    		 	        String[] row = { symbol, open, high, low, close, marketDate };
						    		 	        writer.writeNext(row);
						    		 	        writer.close();
						    		 		}
						    		 		
						    		 	}
						    		 	
						    	}
						       
						    	count++;
						        
						    }
						
						
					} 
					catch (Exception e) {
						logger.info("Exception while reading CSV "+e);
					}
			  }
			  logger.info(file.getName()+" done");
		  }	  
		  
		  logger.info("Symbolwise File Created Successfully.");
	}

}
