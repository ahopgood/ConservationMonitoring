package com.alexander.archive;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Test;

public class FilenameUtilsTest {

	private String sep = System.getProperty("file.separator");
	private String sheetName = "Some file name";
	private String fileWithExtension = sheetName+Converter.XLS;
	private String fileWithPath = "C:"+sep+"blah"+sep+"blah"+sep+sheetName;
	private String pathOnly = "C:"+sep+"blah"+sep+"blah"+sep;
	private String fileWithPathAndExtension = "C:"+sep+"blah"+sep+"blah"+sep+sheetName+Converter.XLS;
	private String fileBeginsWithSeparator = ""+sep+"C:"+sep+"blah"+sep+"blah"+sep+sheetName+Converter.XLS;
	
	@Test
	public void testGetSheetName_givenNoExtensionOrPath(){
		assertEquals(sheetName, FilenameUtils.getSheetName(sheetName));
	}
	
	@Test
	public void testGetSheetName_givenExtension(){
		assertEquals(sheetName, FilenameUtils.getSheetName(fileWithExtension));
	}

	@Test
	public void testGetSheetName_givenPath(){
		assertEquals(sheetName, FilenameUtils.getSheetName(fileWithPath));
	}

	@Test
	public void testGetSheetName_givenPathAndExtension(){
		assertEquals(sheetName, FilenameUtils.getSheetName(fileWithPathAndExtension));
	}
	
	@Test
	public void testGetSheetName_pathOnlyWithTrailingSep(){
		assertEquals("blah", FilenameUtils.getSheetName(pathOnly));
	}

	@Test
	public void testGetSheetName_givenStringBeginsWithSeparator(){
		assertEquals(sheetName, FilenameUtils.getSheetName(fileBeginsWithSeparator));
	}

	@Test
	public void testGetSheetName_fromURL(){
		String testfilename = "TestFile"; //an actual file in the same package as this test to test loadng via URLs
		URL resouce = this.getClass().getResource(testfilename+".txt");
		String path = resouce.getPath().replaceAll("%20", " ");
		assertEquals(testfilename, FilenameUtils.getSheetName(path));
	}
		
	@Test
	public void testGetSheetName_nullString(){
		assertEquals(FilenameUtils.DEFAULT_SHEETNAME, FilenameUtils.getSheetName(null));
	}
	
	@Test
	public void testGetSheetName_givenEmptyString(){
		assertEquals(FilenameUtils.DEFAULT_SHEETNAME, FilenameUtils.getSheetName(""));
		assertEquals(FilenameUtils.DEFAULT_SHEETNAME, FilenameUtils.getSheetName("   "));
		assertEquals(FilenameUtils.DEFAULT_SHEETNAME, FilenameUtils.getSheetName("\t"));
	}
	
	@Test
	public void testExtractFileName_givenNull(){
		Converter converter = new Converter();
		assertEquals("", FilenameUtils.extractFileName(null));
	}
	
	@Test
	public void testExtractFileName_givenEmptyString(){
		Converter converter = new Converter();
		assertEquals("", FilenameUtils.extractFileName(""));
		
	}
	
	@Test
	public void testExtractFileName_givenWhitespaceString(){
		Converter converter = new Converter();
		assertEquals("", FilenameUtils.extractFileName(" "));
		assertEquals("", FilenameUtils.extractFileName("	"));
		assertEquals("", FilenameUtils.extractFileName("\n"));
		assertEquals("", FilenameUtils.extractFileName("\t"));
	}

	@Test
	public void testExtractFileName_givenInvalidFilenameFormat(){
		String filename = "testfile";
		Converter converter = new Converter();
		assertEquals(filename, FilenameUtils.extractFileName(filename+".txt.txt"));
		assertEquals(filename, FilenameUtils.extractFileName(filename+".txtxt"));
		assertEquals(filename, FilenameUtils.extractFileName(filename));
	}

	@Test
	public void testExtractFileName(){
		Converter converter = new Converter();
		assertEquals("testfile", FilenameUtils.extractFileName("testfile.txt"));
		
	}

}
