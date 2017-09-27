package com.jdglazer.shp2igrd.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.jdglazer.shp2igrd.ConverterSettingsLoader;

public abstract class SerializationUtils {
	
	public static boolean serialize ( Object object, File file ) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream( file );
			ObjectOutputStream objectOutput = new ObjectOutputStream( fileOutputStream );
			objectOutput.writeObject( object );
			objectOutput.close();
			fileOutputStream.close();
		} catch ( IOException ioe ) {
			//log error
			return false;
		}
		return true;
	}
	
	public static <T> T deserialize( String filePath ) {
		File file = new File( ConverterSettingsLoader.getTempFolderPath()+"/"+filePath );
		if( file.exists() ) {
			try {
				FileInputStream fileInput = new FileInputStream( file );
				ObjectInputStream objectInput = new ObjectInputStream( fileInput );
				T object = (T) objectInput.readObject();
				objectInput.close();
				fileInput.close();
				return object;
			} catch (IOException ioe ) {
				// log general object write error
			} catch (ClassNotFoundException e) {
				// log class caste error
			}
		} else {
			//log error/warning about file not existing
		}
		return null;
	}
}
