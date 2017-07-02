package com.jdglazer.shp2igrd.shp;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;

import junit.framework.TestCase;

public class PolygonShapeFileTest extends TestCase {
	
	PolygonShapeFile psf = null;
	
	@Override
	public void setUp() {
		try {
			
			Properties properties = new Properties();
			InputStream inputStream = new FileInputStream( "shp-parser-tests.properties" );
			properties.load(inputStream);
			
			psf = new PolygonShapeFile( new ShapeFile(properties.getProperty("shp.polygon.path") ) );
			
		} catch (Exception e) {
			fail("Failed instantiating PolygonShapeFile. Error msg: "+e.getMessage() );
		}
	}
	
	@Test
	public void tests() {
		verifyParseability();
	}
	
	private void verifyParseability() {
		try {
			assertEquals("Invalid number of records found.", psf.recordCount(), 14488);
			assertEquals("Invalid record length found for record 8456", psf.recordLength(8456), 208 );
			assertEquals("Invalid offset for record 8456", psf.recordOffset(8456), 43600236);
			assertEquals("Invalid record min latitude for record 8456", psf.minLat(8456), 40.42248323644424, .0000001 );
			assertEquals("Invalid record max latitude for record 8456", psf.maxLat(8456), 40.42349539078907, .0000001 );
			assertEquals("Invalid record min latitude for record 8456", psf.minLon(8456), -80.47615731928515, .0000001 );
			assertEquals("Invalid record min latitude for record 8456", psf.maxLon(8456), -80.47402223503792, .0000001 );
			assertEquals("Invalid number of parts found for record 8456 ", psf.partCount(8456), 1 );
			assertEquals("Invalid number part length for record 8456, part 0", psf.partLength(8456, 0), 23);
			assertEquals("Invalid longitude for record 8456, part 0, point 21", psf.getLatLon(8456, 0, 21)[1], -80.47554f, .00001f);
			assertEquals("Invalid latitude for record 8456, part 0, point 21", psf.getLatLon(8456, 0, 21)[0], 40.42345f, .00001f);
		} catch (Exception e) {
			fail( "Exception thrown from method. Error: "+e.getMessage() );
		}
	}
	
	
}
