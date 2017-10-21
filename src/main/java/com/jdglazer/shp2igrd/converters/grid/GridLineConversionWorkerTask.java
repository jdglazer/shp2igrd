package com.jdglazer.shp2igrd.converters.grid;

import java.util.ArrayList;

import com.jdglazer.igrd.IGRDCommonDTO;
import com.jdglazer.igrd.grid.GridDataLineDTO;
import com.jdglazer.shp2igrd.converters.ConversionWorkerTask;
import com.jdglazer.shp2igrd.generators.GridLineGenerator;
import com.jdglazer.shp2igrd.shp.PolygonShapeFile;

public class GridLineConversionWorkerTask implements ConversionWorkerTask {
	
	private ArrayList<GridDataLineDTO> gridDataLines = new ArrayList<GridDataLineDTO>();
	
	private GridLineGenerator gridLineGenerator;
	
	private double lonInterval, latInterval;
	
	private int startIndex, endIndex;
	
	double startLatitude, latitude;
	
	public GridLineConversionWorkerTask( PolygonShapeFile psf, int startIndex, int endIndex, double lonInterval, double latInterval ) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.lonInterval = lonInterval;
		this.latInterval = latInterval;
		this.gridLineGenerator = new GridLineGenerator(psf);
		startLatitude = gridLineGenerator.getPolygonShapeFile().getLatMin() + latInterval/2.0;	
	}
	
	public int getIterationStartIndex() {
		return startIndex;
	}

	public int getIterationEndIndex() {
		return endIndex;
	}

	public boolean executeConversionForIndex(int index, ArrayList<IGRDCommonDTO> arrayListToFill) {
		latitude = startLatitude + ((double)index)*latInterval;
		GridDataLineDTO ldr = gridLineGenerator.generateLine(latitude, lonInterval);
		arrayListToFill.add( ldr );
		return true;
	}
	
	public GridDataLineDTO getGridDataLine(int index) {
		if( validLineIndex( index ) ) {
			return gridDataLines.get( index - startIndex );
		} else {
			//log error
			return null;
		}
	}
	
	private boolean validLineIndex( int index ) {
		return index >= startIndex && index <= endIndex && gridDataLines.size() > (index - startIndex );
	}

	public void addFinalConversionOutput( ArrayList<IGRDCommonDTO> dtoList) {
 		for( IGRDCommonDTO icdto : dtoList ) {
 			if( icdto instanceof GridDataLineDTO ) {
 				gridDataLines.add( (GridDataLineDTO) icdto );
 			} else {
 				
 			}
 		}
		
	}

	public ArrayList<GridDataLineDTO> getConversionOutput() {
		return gridDataLines;
	}

}
