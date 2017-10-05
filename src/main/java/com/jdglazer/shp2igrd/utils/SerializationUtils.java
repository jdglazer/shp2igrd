package com.jdglazer.shp2igrd.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

import com.jdglazer.shp2igrd.ConverterSettingsLoader;

public abstract class SerializationUtils {
	
	public static final Logger logger = Logger.getLogger(SerializationUtils.class);
	
	public static boolean serialize ( Object object, File file ) {
		if( object == null ) {
			logger.error("Can not serialize null objet");
			return false;
		}
		logger.info( "Serializing object of type "+object.getClass().getName()+" to file "+file );
		try {
			FileOutputStream fileOutputStream = new FileOutputStream( file );
			ObjectOutputStream objectOutput = new ObjectOutputStream( fileOutputStream );
			objectOutput.writeObject( object );
			objectOutput.close();
			fileOutputStream.close();
		} catch ( IOException ioe ) {
			logger.error( "Serialization to of object failed: "+object.getClass().getName() );
			return false;
		}
		return true;
	}
	
	public static <T> T deserialize( String filePath ) {
		File file = new File( ConverterSettingsLoader.getTempFolderPath()+"/"+filePath );
		if( file.exists() ) {
			logger.info( "Deserializing "+filePath );
			try {
				FileInputStream fileInput = new FileInputStream( file );
				ObjectInputStream objectInput = new ObjectInputStream( fileInput );
				T object = (T) objectInput.readObject();
				objectInput.close();
				fileInput.close();
				return object;
			} catch (IOException ioe ) {
				logger.error("Error deserializing class");
			} catch (ClassNotFoundException e) {
				logger.error("Class cast exception");
			}
		} else {
			logger.error( "Deseriaization failed - file does not exist: "+filePath );
		}
		return null;
	}
}
