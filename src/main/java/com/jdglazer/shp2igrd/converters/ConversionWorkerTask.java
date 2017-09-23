package com.jdglazer.shp2igrd.converters;

import java.util.ArrayList;

import com.jdglazer.igrd.IGRDCommonDTO;

public interface ConversionWorkerTask {
	public int getIterationStartIndex( );
	public int getIterationEndIndex( );
	public boolean executeConversionForIndex( int index, ArrayList<IGRDCommonDTO> arrayListToFill );
}
