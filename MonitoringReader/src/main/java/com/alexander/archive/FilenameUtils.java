package com.alexander.archive;

public class FilenameUtils {

	public static final String DEFAULT_SHEETNAME = "default";
	private static String sep = System.getProperty("file.separator");
	
	public static String getSheetName(String filepath){
		if (filepath == null){
			return DEFAULT_SHEETNAME;
		}
		if (filepath.trim().isEmpty()){
			return DEFAULT_SHEETNAME;
		}
		filepath = FilenameUtils.extractFileName(filepath);
		String args[] = filepath.split("\\\\");
		if (args.length == 1){
			args = args[0].split("/");
		}
		return args[args.length-1];
	}
	
	public static String extractFileName(String filename){
		if (filename != null){
			filename = filename.trim();
			String[] filenameSplit = filename.split("\\.");
			if (filenameSplit.length > 0){
				return filenameSplit[0];
			}
			return "";
		}
		return "";
	}

}
