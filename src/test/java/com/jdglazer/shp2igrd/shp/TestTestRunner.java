package com.jdglazer.shp2igrd.shp;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestTestRunner {
	
	public static void main( String [] args ) {
		Result result = JUnitCore.runClasses(PolygonShapeFileTest.class);
		
		for( Failure f : result.getFailures() ) {
			System.out.println( f.getMessage() );
		}
	}
	
}
