package com.jdglazer.shp2igrd.converters;

import java.util.ArrayList;

import com.jdglazer.igrd.IGRDCommonDTO;

public interface ConversionWorkerTask {
	
	public int getIterationStartIndex( );
	public int getIterationEndIndex( );
	/**
	 * A function in which the main conversion tasks will be executed
	 * @param index
	 * @param arrayListToFill
	 * @return false if the conversion tasks fails, true otherwise
	 */
	public boolean executeConversionForIndex( int index, ArrayList<IGRDCommonDTO> arrayListToFill );
}
