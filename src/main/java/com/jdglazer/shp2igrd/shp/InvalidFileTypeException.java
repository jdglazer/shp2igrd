package com.jdglazer.shp2igrd.shp;
public class InvalidFileTypeException extends Exception {
	
	InvalidFileTypeException() {}
	
	public InvalidFileTypeException(String msg) {
		super(msg);
	}
}