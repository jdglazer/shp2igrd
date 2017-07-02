package com.jdglazer.shp2igrd.shp;

public class PartOutOfBoundsException extends Exception {
	
	public PartOutOfBoundsException() {
		
		super( "The part index provided is invalid" );
	}
}
