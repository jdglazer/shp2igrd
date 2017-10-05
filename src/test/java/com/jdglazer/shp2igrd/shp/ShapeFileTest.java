package com.jdglazer.shp2igrd.shp;

import java.io.IOException;

import org.junit.Test;

import junit.framework.TestCase;

public class ShapeFileTest extends TestCase {
	
	public static final String TEST_NAME = "Shape File Parse Test";
	
	private ShapeFile shpConstr1, shpConstr2, shpConstr3;
	
	private final String SHAPE_FILE_PATH = getClass().getResource( "/PAgeol_dd/pageol_poly_dd.shp" ).getPath();
	private final int version = 1000;
	private final int shapeType = 5;
	private final double minLat = 39.71955;
	private final double maxLat = 42.51601;
	private final double minLon = -80.51986;
	private final double maxLon = -74.68990;
	
	
	@Test 
	public void testParse() {
		initiateShapeObjects();
		testsWithConstructors( shpConstr1, "ShapeFile(String shpPath)" );
		testsWithConstructors( shpConstr2, "ShapeFile(String shpPath, String shxPath, String dbfPath)" );
		testsWithConstructors( shpConstr3, "ShapeFile(ShapeFile shapeFile)" );
	}
	
	private void initiateShapeObjects() {
		String shapeFilePath = SHAPE_FILE_PATH.trim().substring( 0, SHAPE_FILE_PATH.length() - 4 );
		try {
			shpConstr1 = new ShapeFile(shapeFilePath+".shp");
			shpConstr2 = new ShapeFile(shapeFilePath+".shp",shapeFilePath+".shx",shapeFilePath+".dbf");
			shpConstr3 = new ShapeFile( shpConstr2 );
		} catch (Exception e) {
			fail( "One of the ShapeFile constructor instantiations failed: "+e.getMessage());
		}
	}
	
	private void testsWithConstructors( ShapeFile shapeFile, String constructor ) {
		assertEquals( "invalid shape file version "+constructor, shapeFile.getVersion(), version, 0.00001);
		assertEquals( "invalid shapeType from "+constructor, shapeFile.getShapeType(), shapeType, 0.00001);
		assertEquals( "invalid minimum latitude from "+constructor, shapeFile.getLatMin(), minLat, 0.00001);
		assertEquals( "invalid maximum latitude from "+constructor, shapeFile.getLatMax(), maxLat, 0.00001);
		assertEquals( "invalid minimum longitude from "+constructor, shapeFile.getLonMin(), minLon, 0.00001);
		assertEquals( "invalid maximum longitude from "+constructor, shapeFile.getLonMax(), maxLon, 0.00001);
		try {
			assertEquals( "invalid file length from "+constructor, shapeFile.getFileLength(), 57438072L );
		} catch (IOException e) {
			fail( "Error in getting file length" );
		}
	}
}
