/**
 * A class that queues and orders all conversion tasks for grid data
 * It orders/queues ConversionWorkerTasks on the main conversion thread
 * to build a full GridDataDTO. It implements an object such that on completion
 * it can sequentially provide the binary data to build the grid data section of the file
 */
package com.jdglazer.shp2igrd.converters.grid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.jdglazer.igrd.IGRDCommonDTO;
import com.jdglazer.igrd.grid.GridDataHeaderDTO;
import com.jdglazer.shp2igrd.ConversionProgressDTO;
import com.jdglazer.shp2igrd.ConverterSettingsLoader;
import com.jdglazer.shp2igrd.converters.ConversionWorkerTask;
import com.jdglazer.shp2igrd.converters.Orquestrator;
import com.jdglazer.shp2igrd.dbf.DbfNormalizer;
import com.jdglazer.shp2igrd.generators.SerializedGridDataFileGenerator;
import com.jdglazer.shp2igrd.shp.PolygonShapeFile;
import com.jdglazer.shp2igrd.shp.ShapeFile;

public class GridDataConversionOrquestrator implements Orquestrator {
	
	private static Logger logger = Logger.getLogger( GridDataConversionOrquestrator.class );
	
	private boolean SUCCESS_FLAG = true;
	
	private static String ORQUESTRATOR_TYPE = "grid";
	
	private GridDataHeaderDTO gridDataHeader = new GridDataHeaderDTO();
	
	private PolygonShapeFile polygonShapeFile;
	
	private DbfNormalizer dbfNormalizer;
	
	private ArrayList<String> serializedDataFiles = new ArrayList<String>();
	
	// A 2-dimensional array list. This an array list of array lists of concurrently executable tasks
	private ArrayList< ArrayList< ConversionWorkerTask > > taskQueue = new ArrayList< ArrayList< ConversionWorkerTask > >();
	
	// When this becomes empty we remove the next set of concurrent tasks from above and add them here
	private ArrayList<ConversionWorkerTask> currentTaskQueue = new ArrayList<ConversionWorkerTask>();
	
	// an array list of conversion worker tasks that are still running. When we remove a task from the above 
	//array list and pass it to the Converter to queue up, we add it here until the converter passes the task
	// back as finished or failed. Then we remove it from here as a well
	private ArrayList<ConversionWorkerTask> runningTasks = new ArrayList<ConversionWorkerTask>();
	
	public GridDataConversionOrquestrator( String polygonShapeFilePath, String dbfPath, double latInterval, double lonInterval, int indexIdentifier ) throws IOException {
		BasicConfigurator.configure();
		try {
			polygonShapeFile = new PolygonShapeFile( new ShapeFile ( polygonShapeFilePath ) );
		} catch (Exception e ) {
			logger.error( "Error reading polygon shape file. File may not exists: "+polygonShapeFilePath );
			throw new IOException();
		}
		try {
			dbfNormalizer = new DbfNormalizer( new File( dbfPath ) );
		} catch ( Exception e ) {
			logger.error("Error reading dbf file. File may not exist: "+dbfPath);
			throw new IOException();
		}
		// Let's initialize all tasks and queue them up
		setupTaskQueue(latInterval,lonInterval,indexIdentifier);
	}
	
	private void setupTaskQueue(double latInterval, double lonInterval, int indexIdentifier) {
		// Add the first set of concurrent tasks - builds the parts of the header it can
		ArrayList<ConversionWorkerTask> workers = new ArrayList<ConversionWorkerTask>();
		workers.add( new GridDataHeaderConversionStage1WorkerTask( polygonShapeFile, gridDataHeader, latInterval, lonInterval, indexIdentifier ) );
		taskQueue.add(workers);
		
		// Adds second set of conversion workers
		// We need to decide how many workers to split up line conversions to
		int gridLineThreadCount = ConverterSettingsLoader.getWorkerThreadCount(),
		    latitudeLineCount = GridDataHeaderConversionStage1WorkerTask.getLineCount(polygonShapeFile, latInterval);
		ArrayList<ConversionWorkerTask> lineConverters = new ArrayList<ConversionWorkerTask>();
		if( gridLineThreadCount > latitudeLineCount ) {
			gridLineThreadCount = latitudeLineCount;
		}
		int linesPerThread = latitudeLineCount/gridLineThreadCount,
			startIndex = 0;
		for( int j = 1; j <= gridLineThreadCount ; j++ ) {
			int endIndex = gridLineThreadCount == j ? latitudeLineCount - 1: startIndex + linesPerThread - 1;
			lineConverters.add( new GridLineConversionWorkerTask(polygonShapeFile,startIndex,endIndex,lonInterval,latInterval) );
			startIndex = endIndex + 1;
		}
		taskQueue.add(lineConverters);
		
		// Finish populating grid data header
		ArrayList<ConversionWorkerTask> header2Task = new ArrayList<ConversionWorkerTask>();
		header2Task.add( new GridDataHeaderConversionStage2WorkerTask() );
		taskQueue.add(header2Task);
		
		// write finished contents to file
		ArrayList<ConversionWorkerTask> finishGridWriter = new ArrayList<ConversionWorkerTask>();
		finishGridWriter.add( new GridDataConversionWorkerTask() );
		taskQueue.add(finishGridWriter);
	}
	
	public byte [] fetchBinary(int chunkSize) {
		if( !done() && !success() )
			return null;
		
		return null;
	}

	public synchronized void addTaskToQueue(ConversionWorkerTask[] workerTask) {
		if( workerTask != null ) {
			return;
		}
		logger.info( "Orquestrator type: "+ORQUESTRATOR_TYPE+". Given "+workerTask.length+" worker task spots to queue." );
		//If we don't have a set of concurrent tasks to run and all running tasks 
		//have completed, we need to pull the next set of 
		if( currentTaskQueue.size() == 0 && runningTasks.size() == 0 ) {
			if( taskQueue.size() > 0 ) {
				ArrayList<ConversionWorkerTask> tasks = taskQueue.remove(0);
				currentTaskQueue = tasks;
			}
		}
		//Let's give the main thread runner as many of the queue tasks as we can
		for( int i = 0; i < workerTask.length; i++ ) {
			if( currentTaskQueue.size() == 0 )
				break;
			ConversionWorkerTask cwt = currentTaskQueue.remove(0);
			workerTask[i] = cwt;
			runningTasks.add(cwt);
		}
	}

	public void updateConversionProgressBus(ConversionProgressDTO conversionProgressDTO) {
		// TODO Auto-generated method stub
		
	}

	public void onFlush(ArrayList<IGRDCommonDTO> flushedObjects, Class flushedTaskClass, int startIndex, int endIndex) {
		// TODO Auto-generated method stub
		logger.info("Writing data for executor indices: "+startIndex+" to "+endIndex+", for task type: "+flushedTaskClass.getCanonicalName() );
		String fileName = SerializedGridDataFileGenerator.writeToFile(startIndex, endIndex, flushedTaskClass.getCanonicalName(), flushedObjects);
		
		if ( fileName != null ) {
			logger.info( "Successfully wrote data to temporary file: "+fileName );
			serializedDataFiles.add(fileName);
		} else {
			logger.error( "Failed to serialize flushed data for "+flushedTaskClass.getCanonicalName()+", indices "+startIndex+" to "+endIndex);
			SUCCESS_FLAG = false;
		}
	}

	public boolean done() {
		return taskQueue.size() == 0 && currentTaskQueue.size() == 0 && runningTasks.size() == 0;
	}
	
	public boolean success() {
		return SUCCESS_FLAG;
	}

	public void onWorkerFinished(ConversionWorkerTask finishedWorker) {
		if( runningTasks.remove( finishedWorker ) ) {
			logger.info( "Removed a successfully completed worker task" );
		} else {
			logger.warn("Finished conversion worker not found on running workers list");
		}
	}

	public void onWorkerFailed(ConversionWorkerTask failedWorker) {
		if( runningTasks.remove( failedWorker ) ) {
			logger.info( "Removed a failed conversion worker task" );
		} else {
			logger.warn( "Failed to remove a failed worker task");
		}
		SUCCESS_FLAG = false;
	}

	public String getOrquestratorType() {
		// TODO Auto-generated method stub
		return ORQUESTRATOR_TYPE;
	}

	public ArrayList< ArrayList< ConversionWorkerTask > > getTaskQueue() {
		return taskQueue;
	}
}
