/**
 * A class designed to normalize a set of dbf data based on the uniqueness 
 * of values in a specific row or set of rows. The result of this class is
 * a map that maps Indexes of original shapefile/dbf records to a new set of 
 * indices that represents a normalized set of the dbf data. The relationship
 * between the shapefile indexes and new indexed should be one to many respectively
 */
package com.jdglazer.shp2igrd.dbf;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.jamel.dbf.DbfReader;

public class DbfNormalizer {
	
	private int [] columnsToNormalizeOn;
	private DbfReader dbfReader;
	private boolean normalized = false;
	private HashMap<Integer,Integer> normalizedList = new HashMap<Integer,Integer>();
	private HashMap<Integer,ArrayList<String>> indexValues = new HashMap<Integer, ArrayList<String>>();
	
	public DbfNormalizer( File dbfFile, int... columnsToNormalizeOn ) throws Exception {
		dbfReader = new DbfReader( dbfFile );
		//validate that column indices are valid
		for( int i : columnsToNormalizeOn ) {
			if( dbfReader.getHeader().getFieldsCount() <= i ) {
				//log message and change exception type
				throw new Exception();
			}
		}
		this.columnsToNormalizeOn = columnsToNormalizeOn;
	}
	
	public void normalize() {
		if( normalized ) {
			// log message already normalized
			return;
		}
		HashMap<ArrayList<String>, Integer> uniqueDbfRecords = new HashMap<ArrayList<String>,Integer>();
		Object [] record;
		int shapeIndex = 0;
		while( ( record = dbfReader.nextRecord() ) != null ) {
			ArrayList<String> combined = new ArrayList<String>();
			for( int i : columnsToNormalizeOn ) {
				combined.add( convertToString( record[i] ).trim() );
			}
			Integer currentValue = uniqueDbfRecords.putIfAbsent(combined, uniqueDbfRecords.size() );
			normalizedList.put(shapeIndex, currentValue != null ? currentValue : uniqueDbfRecords.size()-1);
			shapeIndex++;
		}
		for( ArrayList<String> list : uniqueDbfRecords.keySet() ) {
			indexValues.put(uniqueDbfRecords.get(list), list);
		}
		normalized = true;
	}
	
	public boolean isNormalized() {
		return normalized;
	}
	
	public String convertToString( Object object ) {
		if( object instanceof Boolean ) {
			return Boolean.toString( (Boolean) object );
		}
		if( object instanceof Number ) {
			return ( (Number) object ).toString();
		}
		if( object instanceof byte[] ) {
			return new String((byte[]) object);
		}
		if (object instanceof Date ) {
			return ((Date)object).toString();
		}
		return null;
	}
	
	public int getIndexForShapeFileRecord( int recordIndex ) {
		return normalizedList.get(recordIndex);
	}
	
	public String getDbfValueWithIndex( int normalizedIndex, int rowIndex ) {
		return indexValues.get(normalizedIndex).get(rowIndex);
	}
	
}
