package com.alexander.archive;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.BasicConfigurator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

public class Converter {

	//1. x Twenty four hour date format in excel format
	//2. x Filename extension format issue on xlsx
	//3. x Consolidate the conversion into a single entry point
	//4. Reading args from the command line
	//5. x Sheet name in xls and xlsx to reflect the filename
	
	//Extract the following fields: Time(fields[1]), Celsius(C)(fields[2]) Humidity(%rh)(fields[5]) from a csv formatted text file
	//Into a list of reading objects
	//Create an excel workbook in .xsl format
	//Create another excel workbook in .xlsx format
	//Create the graph in the workbook?

	public static final String XLS = ".xls";
	public static final String XLSX = ".xlsx";
	private final Logger log = LoggerFactory.getLogger(Converter.class);
	private StringBuilder errorBuilder = new StringBuilder();
	
	public static void main(String[] args) throws IOException {
		if (args == null || args.length != 1){
			System.out.println("\nCorrect syntax is java -jar ./Converter.jar {source_file_path}");
			System.out.println("\nWhere:");
			System.out.println("\tsource_file_path - specifies the full location of the source file to convert into excel files and charts e.g. C:\\Documents\\2015-04-13 Readings.txt");
			System.exit(-1);
		}
		//Extract the filename, discard the extension
		
//		String filename = "C:\\Users\\Alex\\Desktop\\Calderdale GF 15-04-2015.txt";
		BasicConfigurator.configure();
		Converter converter = new Converter();
		System.out.println("Attempting to convert file:"+args[0]);
		converter.convert(args[0]);
	}
	
	//Read from the .txt that is in the correct form, csv
	String[] headers = new String[]{
		"Calderdale GF","Time","Celsius(°C)",
		"High Alarm","Low Alarm","Humidity(%rh)",
		"High Alarm rh","Low Alarm rh","Dew Point(°C)",
		"Serial Number"
	};
	
	public Reading getFrom(String[] fields) {
		if (fields.length < 9){
			throw new IllegalArgumentException("There are insufficient fields to extract reading data "+fields);
		}
		Date time;
		try {
			time = Reading.getDateFormat().parse(fields[1]);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Could not create a date from input ["+fields[1]+"]", e.getCause());
		}
		Double temperature = new Double(fields[2]);
		Double humidity = new Double(fields[5]);
		Reading reading = new Reading(time, temperature, humidity);
		return reading;
	}
	
	public List<Reading> getAllFrom(List<String[]> rawReadings){
		List<Reading> readings = new LinkedList<Reading>();
		if (rawReadings != null){
			for (String[] rawReading : rawReadings){
				try {
					readings.add(getFrom(rawReading));
				} catch (Exception e){
					log.debug("Unable to convert [{}] into a reading", rawReading);
				}
			}
		}
		return readings;
	}
		
	public List<Reading> readFromCSV(String filename) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(filename));
		List<Reading> readings = null;
		try {
			readings = this.getAllFrom(reader.readAll());
		} finally {
			reader.close();
		}
		//Check the Headers exists
		//Read into a linked list of String[]
		return readings;
	}
	
	public void writeToXLS(List<Reading> readings, String filename) throws IOException{
		if (readings != null){
			filename = FilenameUtils.extractFileName(filename);
			Workbook wb = new HSSFWorkbook();
			String sheetName = "Calderdale GF 15-04-2015";//filename;
		    this.writeToSheet(sheetName, wb, readings);
		    FileOutputStream fileOut = new FileOutputStream(filename+XLS);		    
		    wb.write(fileOut);
		    fileOut.close();
		    wb.close();
		}//Null check
	}
	
	/* Chart values */
	private static String TIME_RANGE = "Time";
	private static String HUMIDITY_RANGE = "Humidity";
	private static String TEMPERATURE_RANGE = "Temperature";
	
	private static DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	public void writeToXLSX(List<Reading> readings, String filename) throws IOException{
		Workbook wb = null;
//		String sheetName = "Calderdale GF 15-04-2015";//filename;
		String sheetName = FilenameUtils.getSheetName(filename);
		filename = FilenameUtils.extractFileName(filename);
		try {
			wb = this.populateChart(filename, sheetName, readings);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		
	    FileOutputStream fileOut = new FileOutputStream(filename+XLSX);
	    wb.write(fileOut);
	    fileOut.close();
	    wb.close();
	}
	
	public void convert(String filePath) throws IOException {
		List<Reading> readings = readFromCSV(filePath);
		writeToXLS(readings, filePath);
		this.writeToXLSX(readings, filePath);
		this.writeToXLSX(readings, filePath);
	}
	
	public void writeToSheet(String sheetName, Workbook wb, List<Reading> readings){
		if (readings != null){
			//Create sheet and set name
			Sheet sheet = wb.getSheet(sheetName);
			if (sheet == null){
				sheet = wb.createSheet(sheetName);
			}
		    //Add headers
		    Row headerRow = sheet.createRow(0);
		    headerRow.createCell(0).setCellValue(headers[1]);
		    headerRow.createCell(1).setCellValue(headers[2]);
		    headerRow.createCell(2).setCellValue(headers[5]);
		    int headerLength = 1;
		    //Process readings and add to cells
		    for (int index = 0; index < readings.size(); index++){
		    	Reading reading = readings.get(index);
		    	Row row = sheet.createRow(index+headerLength);
		    		    	
		    	Cell dateCell = row.createCell(0);
//		    	dateCell.setCellStyle(cellStyle);
		    	dateCell.setCellValue(format.format(reading.getTime()));
		    	row.createCell(1).setCellValue(reading.getTemperature());
		    	row.createCell(2).setCellValue(reading.getHumidity());
		    }
		}
	}
	
	public Workbook populateChart(String filename, String sheetName, List<Reading> readings) throws InvalidFormatException, FileNotFoundException, IOException{
		URL url = this.getClass().getResource("ExampleChart1.xlsx");
		
		Workbook wb = new XSSFWorkbook(OPCPackage.open(url.openStream()));
		this.writeToSheet(sheetName, wb, readings);
		
		Name timeRange = wb.getName(TIME_RANGE);
		Name tempRange = wb.getName(TEMPERATURE_RANGE);
		Name humidityRange = wb.getName(HUMIDITY_RANGE);
		timeRange.setRefersToFormula(getFormula(sheetName, "A",2,"A",readings.size()+1));
		tempRange.setRefersToFormula(getFormula(sheetName, "B",2,"B",readings.size()+1));
		humidityRange.setRefersToFormula(getFormula(sheetName, "C",2,"C",readings.size()+1));
		
		return wb;
	}
	
	public String getFormula(String sheetName,String fromColumn, int fromRow, String toColumn, int toRow){
		return "'"+sheetName+"'!$"+fromColumn+"$"+fromRow+":$"+toColumn+"$"+toRow;
	}
	
	public String getErrors(){
		return errorBuilder.toString();
	}
	
}





















