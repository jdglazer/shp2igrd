/**
 * An object to be used as a singleton instance that carries and delivers all information 
 * on the progress of the file conversion. Be sure that data draw and storage on this object
 * is done in a synchronized manner. Multiple threads may be interacting with this at once
 */
package com.jdglazer.shp2igrd;

import java.util.ArrayList;
import java.util.HashMap;

public class ConversionProgressDTO {
	
	private HashMap<String,ConversionTaskDTO> conversionTasks;
	
	public double getWeightedProgress( String name ) {
		ConversionTaskDTO ctdto = conversionTasks.get(name);
		if( ctdto != null ) {
			return ( (double)ctdto.taskWeight/(double)getTotalWeight() )*ctdto.progress;
		}
		// log warning conversion task DNE
		return 0.0;
	}
	
	public double getProgress( String name ) {
		ConversionTaskDTO ctdto = conversionTasks.get(name);
		if( ctdto != null ) {
			return ctdto.progress;
		}
		return 0.0;
	}
	
	public double getTotalProgress() {
		double progress = 0.0;
		for ( String s : conversionTasks.keySet() ) {
			progress += getWeightedProgress(s);
		}
		return progress;
	}
	
	public int getTotalWeight() {
		int total = 0;
		for ( String s : conversionTasks.keySet() ) {
			total += conversionTasks.get(s).taskWeight;
		}
		return total;
	}
	public class ConversionTaskDTO {
		// name of the conversion task
		String name;
		// progress of the task ( out of 1.0 )
		double progress;
		// The weight of the task relative to all the tasks
		int taskWeight;
		// Tells ui whether or not to display progress
		boolean display = false;
		// Allows converter to periodically provide messages to ui
		ArrayList<String> taskDescriptions = new ArrayList<String>();
	}
}
