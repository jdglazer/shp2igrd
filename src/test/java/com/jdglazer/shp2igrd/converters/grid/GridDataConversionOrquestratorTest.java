package com.jdglazer.shp2igrd.converters.grid;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.jdglazer.igrd.IGRDCommonDTO;
import com.jdglazer.igrd.grid.GridDataLineDTO;
import com.jdglazer.shp2igrd.ConverterSettingsLoader;
import com.jdglazer.shp2igrd.converters.ConversionWorkerTask;
import com.jdglazer.shp2igrd.generators.SerializedGridDataFileGenerator;
import com.jdglazer.shp2igrd.shp.InvalidFileTypeException;
import com.jdglazer.shp2igrd.shp.PolygonShapeFile;
import com.jdglazer.shp2igrd.shp.RecordOutOfBoundsException;
import com.jdglazer.shp2igrd.shp.ShapeFile;

public class GridDataConversionOrquestratorTest {
	
	private GridDataConversionOrquestrator orquestrator;
	
	private final String POLYGON_SHAPE_FILE   = getClass().getResource("/PAgeol_dd/pageol_poly_dd.shp").getPath();
	
	private final String DBF_FILE             = getClass().getResource("/PAgeol_dd/pageol_poly_dd.dbf").getPath();
	
	private static final double LATITUDE_INTERVAL    = .00018;
	
	private static final double LONGITUDE_INTERVAL   = .00018;
	
	@Before
	public void setUp() throws IOException {
		ConverterSettingsLoader.setWorkerThreadCount(3);
		ConverterSettingsLoader.setTempFolderPath("/home/jglazer/Documents");
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
	
	@Test
	public void verifyGridLineSplitting() throws FileNotFoundException, InvalidFileTypeException, IOException, RecordOutOfBoundsException, Exception {
		int previousLastIndex=-1;
		ArrayList<ConversionWorkerTask> cwt_list = orquestrator.getTaskQueue().get(1);
		
		assertEquals( "Grid Line worker indices do not start at zero index.", 0, cwt_list.get(0).getIterationStartIndex() );
		
		for( ConversionWorkerTask cwt: cwt_list ) {
			assertEquals( "Conversion workers are missing lines in between.", previousLastIndex+1, cwt.getIterationStartIndex() );
			previousLastIndex = cwt.getIterationEndIndex();
		}
		int lineCount = GridDataHeaderConversionStage1WorkerTask.getLineCount(new PolygonShapeFile( new ShapeFile( POLYGON_SHAPE_FILE ) ), LATITUDE_INTERVAL);
		assertEquals("Grid line workers indices don't end at last grid line.", lineCount-1, cwt_list.get( cwt_list.size() - 1 ).getIterationEndIndex() );
	}
	
	@Test
	public void verifyGridFlush() throws InterruptedException {
		
		ArrayList<IGRDCommonDTO> dtos = new ArrayList<IGRDCommonDTO>();
		dtos.add(new GridDataLineDTO((short)1));
		dtos.add(new GridDataLineDTO((short)1));
		// Stall the tests long enough to ensure no other preceding tests interfere
		Thread.sleep(500);
		// run flush
		orquestrator.onFlush(dtos, GridDataLineDTO.class, 0, 1);
		long currentTime = System.currentTimeMillis();
		File file = new File( ConverterSettingsLoader.getTempFolderPath() );
		int matches = 0;
		
		// count the number of serialized files written in the last 1 second
		for( String f : file.list() ) {
			long timeDelta =  currentTime - SerializedGridDataFileGenerator.parseTime(f);
			if( timeDelta < 1000 && 0 < timeDelta  ) {
				matches++;
			}
		}
		//check that only one file was written
		assertEquals( "Ensure that we wrote one file from flush",  1, matches);
		
		// Stall to make sure other tests don't get messed up due to file written by this test
		Thread.sleep(500);
	}

}
