package com.jdglazer.shp2igrd.generators;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jdglazer.shp2igrd.ConverterSettingsLoader;
import com.jdglazer.shp2igrd.utils.SerializationUtils;

public abstract class SerializedGridDataFileGenerator {
	
	private static final String FILE_FORMAT = "%o-%o_%s_%d.ser";
	
	private static Pattern fileNamePattern = Pattern.compile("^(\\d+)-(\\d+)_([a-zA-Z0-9_]*)_(\\d+)\\.ser$");
	
	public static <T> String writeToFile( int startIndex, int endIndex, String className, T object ) {
		String fileName = String.format( FILE_FORMAT, startIndex, endIndex, className, System.currentTimeMillis() );
		File file = new File( ConverterSettingsLoader.getTempFolderPath()+"/"+fileName );
		if( SerializationUtils.serialize( object, file) ) {
			// Log that the serialization succeeded
			return fileName;
		}
		// Log that serialization failed
		return null;
	}
	
	public static int parseStartIndex(String filename) {
		try {
			return Integer.parseInt( extractGroup( filename, 1 ) );
		} catch(Exception e) {
			return -1;
		}
	}
	
	public static int parseEndIndex(String filename) {
		try {
			return Integer.parseInt( extractGroup( filename, 2 ) );
		} catch(Exception e) {
			return -1;
		}
	}
	
	public static String parseClassName( String filename ) {
		return extractGroup( filename, 3 );
	}
	
	public static long parseTime( String filename ) {
		try {
			return Long.parseLong( extractGroup(filename,4) );
		} catch ( Exception e ) {
			return -1l;
		}
	}
	
	private static String extractGroup( String filename, int group ) {
		Matcher matcher = fileNamePattern.matcher(filename.trim());
		if( !matcher.matches() )
			return null;
		return matcher.group(group);
	}
}
