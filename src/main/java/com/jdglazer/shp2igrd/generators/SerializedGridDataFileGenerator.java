package com.jdglazer.shp2igrd.generators;

import java.io.File;

import com.jdglazer.shp2igrd.ConverterSettingsLoader;
import com.jdglazer.shp2igrd.utils.SerializationUtils;

public abstract class SerializedGridDataFileGenerator {
	
	private static final String FILE_FORMAT = "%o-%o_%s_%o.ser";
	
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
}
