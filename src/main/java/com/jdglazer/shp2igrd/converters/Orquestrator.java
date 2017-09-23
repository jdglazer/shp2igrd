package com.jdglazer.shp2igrd.converters;

import java.util.ArrayList;

import com.jdglazer.igrd.IGRDCommonDTO;
import com.jdglazer.shp2igrd.ConversionProgressDTO;

public interface Orquestrator {
	
	/**
	 * A function called to get the the raw igrd data once the conversion task is complete
	 * should return an empty array or null if the conversion is not complete
	 */
	public byte [] fetchBinary( int chunkSize );
	/** 
	 * A function that the main conversion thread will call periodically to give the 
	 * orchestrator a chance to queue new conversion tasks when old ones have gone to 
	 * completion. The calling thread should check the reference to the array after the 
	 * function executes and queue up added tasks. The calling thread may limit the number
	 * of tasks the Orquestrator can put on the queue by allocating a specific size to array
	 * passed in
	 */
	public void addTaskToQueue( ConversionWorkerTask [] workerTask );
	
	/**
	 * This is called when a worker thread has completed
	 */
	public void onWorkerFinished( ConversionWorkerTask finishedWorker );
	
	/**
	 * This is how the calling thread tells the Orquestrator which Workers are queued
	 * periodically
	 */
	public void updateQueuedWorkers( ConversionWorkerTask [] queuedWorkers );
	
	/**
	 * This function should be called once to inject the conversion progress bus into 
	 * the Orquestrator so it can pass updates to it based on observing the progress
	 * of the conversion workers
	 */
	public void updateConversionProgressBus( ConversionProgressDTO conversionProgressDTO );
	
	/**
	 * This function passes the objects that have been flushed from a worker ( due to memory
	 * limits being reached ) to the Orquestrator to stash the information where it sees
	 * fit.
	 */
	public void onFlush( ArrayList<IGRDCommonDTO> flushedObjects );
	
	/**
	 * A function called to periodically give the orquestrator a chance to tell the calling thread
	 * that it is done the conversion task and is ready to provide binary data for writing to the
	 * file
	 */
	public boolean done();
	
}