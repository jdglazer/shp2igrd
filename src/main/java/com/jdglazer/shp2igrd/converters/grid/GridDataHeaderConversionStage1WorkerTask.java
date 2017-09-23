package com.jdglazer.shp2igrd.converters.grid;

import java.util.ArrayList;

import com.jdglazer.igrd.IGRDCommonDTO;
import com.jdglazer.shp2igrd.converters.ConversionWorkerTask;

public class GridDataHeaderConversionStage1WorkerTask implements ConversionWorkerTask {
	
	public GridDataHeaderConversionStage1WorkerTask() {
		
	}

	public int getIterationStartIndex() {
		return 0;
	}

	public int getIterationEndIndex() {
		return 1;
	}

	public boolean executeConversionForIndex(int index, ArrayList<IGRDCommonDTO> arrayListToFill) {
		
		return true;
	}

}
