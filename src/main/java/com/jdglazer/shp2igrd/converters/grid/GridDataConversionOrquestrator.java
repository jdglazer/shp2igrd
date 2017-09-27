/**
 * A class that queues and orders all conversion tasks for grid data
 * It orders/queues ConversionWorkerTasks on the main conversion thread
 * to build a full GridDataDTO. It implements an object such that on completion
 * it can sequentially provide the binary data to build the grid data section of the file
 */
package com.jdglazer.shp2igrd.converters.grid;

import java.util.ArrayList;

import com.jdglazer.igrd.IGRDCommonDTO;
import com.jdglazer.igrd.grid.GridDataHeaderDTO;
import com.jdglazer.shp2igrd.ConversionProgressDTO;
import com.jdglazer.shp2igrd.converters.ConversionWorkerTask;
import com.jdglazer.shp2igrd.converters.Orquestrator;

public class GridDataConversionOrquestrator implements Orquestrator {
	
	private GridDataHeaderDTO gridDataHeader = new GridDataHeaderDTO();
	
	// A 2-dimensional array list. This an array list of array lists of concurrently executable tasks
	private ArrayList< ArrayList< ConversionWorkerTask > > taskQueue = new ArrayList< ArrayList< ConversionWorkerTask > >();
	
	// When this becomes empty we remove the next set of concurrent tasks from above and add them here
	private ArrayList<ConversionWorkerTask> currentTaskQueue = new ArrayList<ConversionWorkerTask>();
	
	// an array list of conversion worker tasks that are still running. When we remove a task from the above 
	//array list and pass it to the Converter to queue up, we add it here until the converter passes the task
	// back as finished or failed. Then we remove it from here as a well
	private ArrayList<ConversionWorkerTask> runningTasks = new ArrayList<ConversionWorkerTask>();

	public byte [] fetchBinary(int chunkSize) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addTaskToQueue(ConversionWorkerTask[] workerTask) {
		workerTask[0] = new GridDataHeaderConversionStage1WorkerTask(null, gridDataHeader, .00018, .00018, 2);
		
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

	public void onWorkerFailed(ConversionWorkerTask failedWorker) {
		// TODO Auto-generated method stub
		
	}

}
