package com.alexander.archive;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConverterTest {

	Calendar cal = Calendar.getInstance();	
	Double temperature = new Double(12.5);
	Double humidity = new Double(24.6);
	
	@Before
	public void setUp() throws Exception {
		cal.set(Calendar.YEAR, 2015);
		cal.set(Calendar.MONTH, 2);
		cal.set(Calendar.DAY_OF_MONTH, 11);
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.MINUTE, 42);
		cal.set(Calendar.SECOND, 05);
		cal.set(Calendar.MILLISECOND, 0);
	}

	@After
	public void tearDown() throws Exception {
	}

	private String[] tooManyFields = new String[]{"one","two","three","four","five","six","seven","eight"};
	private String[] invalidDateFields = new String[]{"one","2015-03-11 blah 11:42:05","12.5","four","five","24.6","seven","eight","nine"};
	private String[] invalidTempFields = new String[]{"one","2015-03-11 11:42:05","12.5aaaa","four","five","24.6","seven","eight","nine"};
	private String[] invalidHumidityFields = new String[]{"one","2015-03-11 11:42:05","12.5","four","five","24.6aaa","seven","eight","nine"};
	private String[] validFields = new String[]{"one","2015-03-11 11:42:05","12.5","four","five","24.6","seven","eight","nine"};
	
	@Test (expected=IllegalArgumentException.class)
	public void testFrom_givenIncorrectNumberOfFields() {
		Converter converter = new Converter();
		converter.getFrom(tooManyFields);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testFrom_givenInvalidDateString() {
		Converter converter = new Converter();
		converter.getFrom(invalidDateFields);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testFrom_givenInvalidTemperatureString() {
		Converter converter = new Converter();
		converter.getFrom(invalidTempFields);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testFrom_givenHumidityString() {
		Converter converter = new Converter();
		converter.getFrom(invalidHumidityFields);
	}
	
	@Test 
	public void testFrom() {
		String[] fields = new String[]{"one","2015-03-11 11:42:05","12.5","four","five","24.6","seven","eight","nine"};
		Converter converter = new Converter();
		Reading reading = converter.getFrom(fields);
				
		assertEquals("Times should match", cal.getTime(), reading.getTime());
		assertEquals("Temperature should match", temperature, reading.getTemperature());
		assertEquals("Humidity should match", humidity, reading.getHumidity());	
	}
	
	@Test
	public void testAllFrom_givenNullList(){
		List<String[]> rawReadings = null;
		Converter converter = new Converter();
		List<Reading> readings = converter.getAllFrom(rawReadings);
		assertEquals(0, readings.size());
	}
	
	@Test
	public void testAllFrom_givenEmptyList(){
		List<String[]> rawReadings = new LinkedList<String[]>();
		Converter converter = new Converter();
		List<Reading> readings = converter.getAllFrom(rawReadings);
		assertEquals(0, readings.size());
	}
	
	@Test
	public void testAllFrom_givenInvalidArgs(){
		List<String[]> rawReadings = new LinkedList<String[]>();
		rawReadings.add(validFields);
		rawReadings.add(invalidDateFields);
		rawReadings.add(invalidTempFields);
		rawReadings.add(invalidHumidityFields);
		rawReadings.add(tooManyFields);
		
		//All except the first value should be invalid
		Converter converter = new Converter();
		List<Reading> readings = converter.getAllFrom(rawReadings);
		assertEquals(1, readings.size());
		
		assertEquals("Times should match", cal.getTime(), readings.get(0).getTime());
		assertEquals("Temperature should match", temperature, readings.get(0).getTemperature());
		assertEquals("Humidity should match", humidity, readings.get(0).getHumidity());	
	}

	@Test
	public void testAllFrom(){
		List<String[]> rawReadings = new LinkedList<String[]>();
		rawReadings.add(new String[]{"one","2015-03-11 11:42:05","12.5","four","five","24.6","seven","eight","nine"});
		rawReadings.add(new String[]{"one","2015-03-11 11:42:05","12.5","four","five","24.6aaa","seven","eight","nine"});
		rawReadings.add(new String[]{"one","2015-03-11 11:42:05","12.5aaaa","four","five","24.6","seven","eight","nine"});
		rawReadings.add(new String[]{"one","2015-03-11 blah 11:42:05","12.5","four","five","24.6","seven","eight","nine"});
		rawReadings.add(new String[]{"one","two","three","four","five","six","seven","eight"});
		
		//All except the first value should be invalid
		Converter converter = new Converter();
		List<Reading> readings = converter.getAllFrom(rawReadings);
		assertEquals(1, readings.size());
		
		assertEquals("Times should match", cal.getTime(), readings.get(0).getTime());
		assertEquals("Temperature should match", temperature, readings.get(0).getTemperature());
		assertEquals("Humidity should match", humidity, readings.get(0).getHumidity());	
	}
	
	@Test
	public void testExtractFileName_givenNull(){
		Converter converter = new Converter();
		assertEquals("", converter.extractFileName(null));
	}
	
	@Test
	public void testExtractFileName_givenEmptyString(){
		Converter converter = new Converter();
		assertEquals("", converter.extractFileName(""));
		
	}
	
	@Test
	public void testExtractFileName_givenWhitespaceString(){
		Converter converter = new Converter();
		assertEquals("", converter.extractFileName(" "));
		assertEquals("", converter.extractFileName("	"));
		assertEquals("", converter.extractFileName("\n"));
		assertEquals("", converter.extractFileName("\t"));
	}

	@Test
	public void testExtractFileName_givenInvalidFilenameFormat(){
		String filename = "testfile";
		Converter converter = new Converter();
		assertEquals(filename, converter.extractFileName(filename+".txt.txt"));
		assertEquals(filename, converter.extractFileName(filename+".txtxt"));
		assertEquals(filename, converter.extractFileName(filename));
	}

	@Test
	public void testExtractFileName(){
		Converter converter = new Converter();
		assertEquals("testfile", converter.extractFileName("testfile.txt"));
		
	}

	@Test
	public void testReadFromCSV() throws Exception{
		String testfile = "testfile.csv";
		URL path = this.getClass().getResource(testfile);
		Converter converter = new Converter();
		List<Reading> readings = converter.readFromCSV(path.getPath().replaceAll("%20", " "));
		
		assertNotNull(readings);
		assertEquals(3, readings.size());
		for (Reading reading : readings){
			System.out.println(reading.toString());
		}		
	}

	@Test (expected=FileNotFoundException.class)
	public void testReadFromCSV_givenFileDoesNotExist() throws IOException{
		String testfile = "nonExistentFile";
		Converter converter = new Converter();
		List<Reading> readings = converter.readFromCSV(testfile);
		assertNull(readings);
	}
	
	@Test
	public void testReadFromCSV_givenWrongFileType() throws IOException{
		String testfile = "wrongFileType.xls";
		URL path = this.getClass().getResource(testfile);
		Converter converter = new Converter();
		List<Reading> readings = converter.readFromCSV(path.getPath().replaceAll("%20", " "));
		
		assertNotNull(readings);
		assertEquals(0, readings.size());
		converter.getErrors();
	}
	
	@Test
	public void testWriteToXLS() throws IOException{
		Converter converter = new Converter();
		
		List<Reading> readings = new LinkedList<Reading>();
		readings.add(converter.getFrom(validFields));
		
		URL resouce = this.getClass().getResource("");
		String path = resouce.getPath().replaceAll("%20", " ");
		converter.writeToXLS(readings, path+"test");
	}

	@Test
	public void testWriteToXLS_givenNullFilename() throws IOException{
		Converter converter = new Converter();
		
		List<Reading> readings = new LinkedList<Reading>();
		readings.add(converter.getFrom(validFields));
		converter.writeToXLS(readings, null);
		converter.getErrors();
	}

	@Test
	public void testWriteToXLS_givenNullReadings() throws IOException{
		Converter converter = new Converter();
		
		List<Reading> readings = new LinkedList<Reading>();
		readings.add(converter.getFrom(validFields));
		
		URL resouce = this.getClass().getResource("");
		String path = resouce.getPath().replaceAll("%20", " ");
		converter.writeToXLS(null, path+"test");
	}

	@Test
	public void testWriteToXLS_givenEmptyReadings() throws IOException{
		Converter converter = new Converter();
		
		URL resouce = this.getClass().getResource("");
		String path = resouce.getPath().replaceAll("%20", " ");
		converter.writeToXLS(new LinkedList<Reading>(), path+"test");
	}

	@Test
	public void testWriteToXLSX_givenNullFilename() throws Exception {
		Converter converter = new Converter();
		
		List<Reading> readings = new LinkedList<Reading>();
		readings.add(converter.getFrom(validFields));
		
		URL resouce = this.getClass().getResource("");
		String path = resouce.getPath().replaceAll("%20", " ");
		converter.writeToXLSX(readings, path+"test");
		
		File output = new File(path+"test"+Converter.XLSX);
		assertTrue(output.exists());
		assertTrue(output.isFile());
		output.delete();
	}
	
	@Test
	public void testWriteToXLSX_givenNullReadings() throws Exception {
		Converter converter = new Converter();
		
		List<Reading> readings = new LinkedList<Reading>();
		readings.add(converter.getFrom(validFields));
		
		URL resouce = this.getClass().getResource("");
		String path = resouce.getPath().replaceAll("%20", " ");
		converter.writeToXLSX(readings, path+"test");
		
		File output = new File(path+"test"+Converter.XLSX);
		assertTrue(output.exists());
		assertTrue(output.isFile());
		output.delete();
	}
	
	@Test
	public void testWriteToXLSX_givenEmptyReadings() throws Exception {
		Converter converter = new Converter();
		
		List<Reading> readings = new LinkedList<Reading>();
		readings.add(converter.getFrom(validFields));
		
		URL resouce = this.getClass().getResource("");
		String path = resouce.getPath().replaceAll("%20", " ");
		converter.writeToXLSX(readings, path+"test");
		
		File output = new File(path+"test"+Converter.XLSX);
		assertTrue(output.exists());
		assertTrue(output.isFile());
		output.delete();
	}
	
	@Test
	public void testWriteToXLSX_givenEmptyH() throws Exception {
		Converter converter = new Converter();
		
		List<Reading> readings = new LinkedList<Reading>();
		readings.add(converter.getFrom(validFields));
		
		URL resouce = this.getClass().getResource("");
		String path = resouce.getPath().replaceAll("%20", " ");
		converter.writeToXLSX(readings, path+"test");
		
		File output = new File(path+"test"+Converter.XLSX);
		assertTrue(output.exists());
		assertTrue(output.isFile());
		output.delete();
	}

	@Test
	public void testWriteToXLSX() throws Exception {
		Converter converter = new Converter();
		
		List<Reading> readings = new LinkedList<Reading>();
		readings.add(converter.getFrom(validFields));
		
		URL resouce = this.getClass().getResource("");
		String path = resouce.getPath().replaceAll("%20", " ");
		converter.writeToXLSX(readings, path+"test");
		
		File output = new File(path+"test"+Converter.XLSX);
		assertTrue(output.exists());
		assertTrue(output.isFile());
//		output.delete();
//		Calderdale GF 15-04-2015
	}
	private String sep = System.getProperty("file.separator");
	private String sheetName = "Some file name";
	private String fileWithExtension = sheetName+Converter.XLS;
	private String fileWithPath = "C:\\blah\\blah\\"+sheetName;
	private String pathOnly = "C:\\blah\\blah\\";
	private String fileWithPathAndExtension = "C:\\blah\\blah\\"+sheetName+Converter.XLS;
	
	@Test
	public void testGetSheetName_givenNoExtensionOrPath(){
		Converter converter = new Converter();
		assertEquals(sheetName, converter.getSheetName(sheetName));
	}
	
	@Test
	public void testGetSheetName_givenExtension(){
		Converter converter = new Converter();
		assertEquals(sheetName, converter.getSheetName(fileWithExtension));
	}

	@Test
	public void testGetSheetName_givenPath(){
		Converter converter = new Converter();
		assertEquals(sheetName, converter.getSheetName(fileWithPath));
	}

	@Test
	public void testGetSheetName_givenPathAndExtension(){
		Converter converter = new Converter();
		assertEquals(sheetName, converter.getSheetName(fileWithPathAndExtension));
	}
	
	@Test
	public void testGetSheetName_pathOnlyWithTrailingSep(){
		Converter converter = new Converter();
		assertEquals(Converter.DEFAULT_SHEETNAME, converter.getSheetName(pathOnly));
	}

	@Test
	public void testGetSheetName_nullString(){
		Converter converter = new Converter();
		assertEquals(Converter.DEFAULT_SHEETNAME, converter.getSheetName(null));
	}
	
	@Test
	public void testGetSheetName_givenEmptyString(){
		Converter converter = new Converter();
		assertEquals(Converter.DEFAULT_SHEETNAME, converter.getSheetName(""));
		assertEquals(Converter.DEFAULT_SHEETNAME, converter.getSheetName("   "));
		assertEquals(Converter.DEFAULT_SHEETNAME, converter.getSheetName("\t"));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
