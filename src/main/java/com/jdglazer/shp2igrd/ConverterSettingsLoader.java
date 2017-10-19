/**
 * Provides the default settings and loads settings out of configuration files
 */
package com.jdglazer.shp2igrd;

public abstract class ConverterSettingsLoader {
	
	private static int workerThreadCount  = 1;
	
	private static String tempFolderPath  = "/var/tmp";
	
	public static synchronized String getTempFolderPath() {
		return tempFolderPath;
	}
	
	public static synchronized int getWorkerThreadCount() {
		return workerThreadCount;
	}
	
	public static synchronized void setWorkerThreadCount( int workerThreadCount ) {
		ConverterSettingsLoader.workerThreadCount = workerThreadCount;
	}
	
	public static synchronized void setTempFolderPath( String path ) {
		tempFolderPath = path;
	}
}
