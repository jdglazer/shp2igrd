package com.jdglazer.shp2igrd.converters.grid;

import java.util.ArrayList;

import com.jdglazer.igrd.IGRDCommonDTO;
import com.jdglazer.igrd.grid.GridDataLineDTO;
import com.jdglazer.igrd.line.LineDataRecordDTO;
import com.jdglazer.shp2igrd.converters.ConversionWorkerTask;
import com.jdglazer.shp2igrd.generators.GridLineGenerator;

public class GridLineConversionWorkerTask implements ConversionWorkerTask {
	
	private ArrayList<GridDataLineDTO> gridDataLines = new ArrayList<GridDataLineDTO>();
	
	private GridLineGenerator gridLineGenerator;
	
	private double lineInterval;
	
	private int startIndex, endIndex;
	
	double startLatitude, latitude;
	
	public GridLineConversionWorkerTask( int startIndex, int endIndex, double lineInterval ) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.lineInterval = lineInterval;
		startLatitude = gridLineGenerator.getPolygonShapeFile().getLatMin() + lineInterval/2.0;	
	}
	
	public int getIterationStartIndex() {
		return startIndex;
	}

	public int getIterationEndIndex() {
		return endIndex;
	}

	public boolean executeConversionForIndex(int index, ArrayList<IGRDCommonDTO> arrayListToFill) {
		latitude = startLatitude + ((double)index)*lineInterval;
		GridDataLineDTO ldr = gridLineGenerator.generateLine(latitude, lineInterval);
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

}
