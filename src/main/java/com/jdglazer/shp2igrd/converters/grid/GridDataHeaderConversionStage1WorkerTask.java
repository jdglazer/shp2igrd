package com.jdglazer.shp2igrd.converters.grid;

import java.util.ArrayList;

import com.jdglazer.igrd.IGRDCommonDTO;
import com.jdglazer.igrd.grid.GridDataHeaderDTO;
import com.jdglazer.shp2igrd.converters.ConversionWorkerTask;
import com.jdglazer.shp2igrd.shp.PolygonShapeFile;

public class GridDataHeaderConversionStage1WorkerTask implements ConversionWorkerTask {
	
	private PolygonShapeFile polygonSF;
	
	private GridDataHeaderDTO gridDataHeader;
	
	private double latInterval, lonInterval;
	
	private int indexIdentifier;
	
	public GridDataHeaderConversionStage1WorkerTask( PolygonShapeFile polygonSF, GridDataHeaderDTO gridDataHeader, double latInterval, double lonInterval, int indexIdentifier ) {
		this.polygonSF = polygonSF;
		this.gridDataHeader = gridDataHeader;
		this.latInterval = latInterval;
		this.lonInterval = lonInterval;
		this.indexIdentifier = indexIdentifier;
	}

	public int getIterationStartIndex() {
		return 0;
	}

	public int getIterationEndIndex() {
		return 0;
	}

	public boolean executeConversionForIndex(int index, ArrayList<IGRDCommonDTO> arrayListToFill) {
		if( latInterval > ( polygonSF.getLatMin() - polygonSF.getLatMin() )/2.0 ) {
			//log error message
			return false;
		}
		
		if( indexIdentifier <= 0  ||  indexIdentifier > 2) {
			//log error message
			return false;
		}
			
		gridDataHeader.setMinimumLatitude(polygonSF.getLatMin());
		gridDataHeader.setMaximumLatitude(polygonSF.getLatMax());
		gridDataHeader.setMinimumLongitude(polygonSF.getLonMin());
		gridDataHeader.setMaximumLongitude(polygonSF.getLonMax());
		gridDataHeader.setLatitudeInterval(latInterval);
		gridDataHeader.setLongitudeInterval(lonInterval);
		

		gridDataHeader.setLatitudeLineCount(getLineCount(polygonSF, latInterval) );
		
		gridDataHeader.setIndexIdentifierType( (short) indexIdentifier );
		return true;
	}
	
	public static int getLineCount( PolygonShapeFile polygonShapeFile, double latInterval ) {
		return (int) ( ( polygonShapeFile.getLatMax() - ( polygonShapeFile.getLatMin() + latInterval/2.0 ) )/latInterval ) + 1;
	
	}

	public void addFinalConversionOutput(ArrayList<IGRDCommonDTO> dtoList) {
		// TODO Auto-generated method stub
		
	}

}
