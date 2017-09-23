/**
 * A class that queues and orders all conversion tasks for grid data
 * It orders/queues ConversionWorkerTasks on the main conversion thread
 * to build a full GridDataDTO. It implements an object such that on completion
 * it can sequentially provide the binary data to build the grid data section of the file
 */
package com.jdglazer.shp2igrd.converters.grid;

import java.util.ArrayList;

import com.jdglazer.igrd.IGRDCommonDTO;
import com.jdglazer.shp2igrd.ConversionProgressDTO;
import com.jdglazer.shp2igrd.converters.ConversionWorkerTask;
import com.jdglazer.shp2igrd.converters.Orquestrator;

public class GridDataConversionOrquestrator implements Orquestrator {

	public byte [] fetchBinary(int chunkSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addTaskToQueue(ConversionWorkerTask[] workerTask) {
		// TODO Auto-generated method stub
		
	}

	public void updateQueuedWorkers(ConversionWorkerTask[] queuedWorkers) {
		// TODO Auto-generated method stub
		
	}

	public void updateConversionProgressBus(ConversionProgressDTO conversionProgressDTO) {
		// TODO Auto-generated method stub
		
	}

	public void onFlush(ArrayList<IGRDCommonDTO> flushedObjects) {
		// TODO Auto-generated method stub
		
	}

	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onWorkerFinished(ConversionWorkerTask finishedWorker) {
		// TODO Auto-generated method stub
		
	}

}
