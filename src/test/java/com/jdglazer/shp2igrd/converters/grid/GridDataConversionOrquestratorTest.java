package com.jdglazer.shp2igrd.converters.grid;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class GridDataConversionOrquestratorTest {
	
	private GridDataConversionOrquestrator orquestrator;
	
	private final String POLYGON_SHAPE_FILE   = getClass().getResource("/PAgeol_dd/pageol_poly_dd.shp").getPath();
	
	private final String DBF_FILE             = getClass().getResource("/PAgeol_dd/pageol_poly_dd.dbf").getPath();
	
	private static final double LATITUDE_INTERVAL    = .00018;
	
	private static final double LONGITUDE_INTERVAL   = .00018;
	
	@Before
	public void setUp() throws IOException {
		orquestrator = new GridDataConversionOrquestrator( 
				   POLYGON_SHAPE_FILE, 
				   DBF_FILE, 
				   LATITUDE_INTERVAL,
				   LONGITUDE_INTERVAL,
				   2
				  );
	}

	@Test
	public void creationTest() {
		assertNotNull( "Failed to instantiate grid data orquestrator", orquestrator );
	}

}
