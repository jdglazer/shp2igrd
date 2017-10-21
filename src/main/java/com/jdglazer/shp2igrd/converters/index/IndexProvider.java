package com.jdglazer.shp2igrd.converters.index;

public interface IndexProvider {
	
	/**
	 * A function that provides a normalized dbfIndex. The normalization
	 * should be performed based on a given column or set of columns in 
	 * the dbf file
	 * @param dbfOriginalIndex
	 * @return
	 */
	public int provideNormalizedDbfIndex( int dbfOriginalIndex );
	
	/**
	 * Provides the string value or set of values from the dbf file by
	 * which the normalization occurred 
	 * @param dbfNormalizedIndex
	 * @return
	 */
	public String provideNomalizationValue( int dbfNormalizedIndex );
	
}
