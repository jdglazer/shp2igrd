package com.jdglazer.shp2igrd.converters.grid;

import java.util.ArrayList;

import com.jdglazer.igrd.IGRDCommonDTO;
import com.jdglazer.igrd.grid.GridDataLineDTO;
import com.jdglazer.igrd.line.LineDataRecordDTO;
import com.jdglazer.shp2igrd.converters.ConversionWorkerTask;
import com.jdglazer.shp2igrd.generators.GridLineGenerator;

public class GridLineConversionWorkerTask implements ConversionWorkerTask {
	
	double lineInterval;
	
	private GridLineGenerator gridLineGenerator;
	
	private int startIndex, endIndex;
	
	double startLatitude = gridLineGenerator.getPolygonShapeFile().getLatMin() + lineInterval/2.0,
			   latitude;

	public int getIterationStartIndex() {
		return startIndex;
	}

	public int getIterationEndIndex() {
		return startIndex;
	}

	public boolean executeConversionForIndex(int index, ArrayList<IGRDCommonDTO> arrayListToFill) {
		latitude = startLatitude + ((double)index)*lineInterval;
		GridDataLineDTO ldr = gridLineGenerator.generateLine(latitude, lineInterval);
		arrayListToFill.add( ldr );
		return true;
	}

}
