package com.jdglazer.shp2igrd.shp;

import java.io.IOException;

import org.junit.Test;

import junit.framework.TestCase;

public class PolylineShapeFileTest extends TestCase {
	
	private static PolylineShapeFile psf;
	
	@Override
	public void setUp() {
		try {
			psf = new PolylineShapeFile( new ShapeFile( "/home/jglazer/Downloads/PAgeol_dd/pageol_arc_dd" ) );
		} catch (Exception e) {
			fail( "Unable to instatiate PolylineShapeFile. Error: "+e.getMessage() );
		}
	}
	
	@Test
	public static void verifyParseability() {
		try {
			assertEquals("Invalid max latitude for record", psf.maxLat(1032), 41.79920, .00001 );
			assertEquals("Invalid max longitude for record", psf.maxLon(1032), -75.07654, .00001 );
			assertEquals("Invalid min latitude for record", psf.minLat(1032), 41.79691 , .00001 );
			assertEquals("Invalid min longitude for record", psf.minLon(1032), -75.08003, .00001 );
			assertEquals("Failed to parse part count", psf.partCount( 1032 ), 1 );
			assertEquals("Failed to parse point count for record", psf.pointCount(1032), 5 );
			assertEquals( "Failed to parse point count for part", psf.pointCount(1032, 0), 5 );
		} catch( Exception e ) {
			fail( "Exception throw while parsing PolylineShapeFile "+e.getMessage() );
		}
		
	}
	
	public static void main( String [] args ) throws IOException, RecordOutOfBoundsException, PartOutOfBoundsException, InvalidFileTypeException {
		verifyParseability();
	}
}
