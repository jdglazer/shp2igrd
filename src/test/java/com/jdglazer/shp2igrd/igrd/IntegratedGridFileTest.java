package com.jdglazer.shp2igrd.igrd;

import java.io.FileInputStream;

import org.junit.Test;

import junit.framework.TestCase;

public class IntegratedGridFileTest extends TestCase {
	
	IntegratedGridFile igf;
	
	@Override
	public void setUp() {
		try {
			igf = new IntegratedGridFile(new FileInputStream("/home/jglazer/Documents/sample.igrd") );
		} catch ( Exception e ) {
			fail("Failure to instantiate an IntegratedGridFile instance");
		}
	}
	
	@Test
	public void tests() {
		//verifyIGRDGridDataParse();
	}

	private void verifyIGRDGridDataParse() {
		IntegratedGridFile.GridData gd = igf.getGridData();
		try {
			assertEquals("Grid Data: invalid max lat", 42.32424, gd.getMaxLat() );
			assertEquals("Grid Data: invalid max lat",  39.73932423, gd.getMinLat() );
			assertEquals("Grid Data: invalid max lat", -74.4252, gd.getMaxLon() );
			assertEquals("Grid Data: invalid max lat", -80.4222, gd.getMinLon() );
			assertEquals("Grid Data: invalid inter-point latitude interval",  .00018, gd.getLatInterval() );
			assertEquals("Grid Data: invalid inter-point longitude interval",  .00018 , gd.getLonInterval());
			assertEquals("Grid Data: invalid lat line count", 3, gd.getLineCount() );
			assertEquals("Grid Data: invalid grid line size",  89, gd.getLineSize(1));
			assertEquals("Grid Data: invalid line part count", 2, gd.getPartCount(1));
			assertEquals("Grid Data: invalid short overflow count",  0, gd.getShortOverflowCount(1) );
			assertEquals("Grid Data: invalid part start longitude", -75.569932f, gd.getPartStartLongitude(1, 1) );
			assertEquals("Grid Data: invalid part point count",  500, gd.getPartPointCount(1, 1) );
			assertEquals("Grid Data: invalid part segment count",  3, gd.getPartSegmentCount(1, 1) );
			assertEquals("Grid Data: invalid segment index", (byte)201, gd.getSegmentIndex(1, 1, 2) );
			assertEquals("Grid Data: invalid segment point count",  282, gd.getSegmentPointCount(1, 1, 2) );
		} catch (Exception e) {
			fail( "Exception thrown from method. Error: "+e.getMessage() );
		}
	}
}
