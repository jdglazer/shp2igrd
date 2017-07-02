package com.jdglazer.shp2igrd.shp;

public class PointOutOfBoundsException extends Exception {
	
	public PointOutOfBoundsException() {
		super( "Invalid point index provided" );
	}
}
