package com.jdglazer.shp2igrd.shp;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.jdglazer.shp2igrd.igrd.IntegratedGridFileTest;

public class TestTestRunner {
	
	public static void main( String [] args ) {
		Result result = JUnitCore.runClasses(IntegratedGridFileTest.class);
		
		for( Failure f : result.getFailures() ) {
			System.out.println( f.getMessage() );
		}
	}
	
}
