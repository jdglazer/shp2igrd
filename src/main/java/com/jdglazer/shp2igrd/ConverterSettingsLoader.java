/**
 * Provides the default settings and loads settings out of configuration files
 */
package com.jdglazer.shp2igrd;

public abstract class ConverterSettingsLoader {
	
	public static synchronized String getTempFolderPath() {
		return "/var/tmp";
	}

}
