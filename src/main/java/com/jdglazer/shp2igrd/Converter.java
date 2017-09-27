package com.jdglazer.shp2igrd;

import java.util.ArrayList;

import com.jdglazer.shp2igrd.converters.ConversionWorker.WorkerType;
import com.jdglazer.shp2igrd.converters.ConversionWorkerTask;
import com.jdglazer.shp2igrd.converters.Orquestrator;
import com.jdglazer.shp2igrd.converters.grid.GridDataConversionOrquestrator;
import com.jdglazer.shp2igrd.converters.line.LinearDataConversionOrquestrator;
import com.jdglazer.shp2igrd.converters.point.PointDataConversionOrquestrator;

public class Converter {
	//Thread execution management and settings
	private final int ITERATION_INTERVAL = 1000;
	private short workerCount = 1;
	private boolean stop = false;
	
	//The worker threads. The thread limit will limit how many of these we run at once
	private ArrayList<WorkerThread> conversionWorkers = new ArrayList<WorkerThread>();
	
	// These are the queues for conversion tasks
	private ArrayList<ConversionWorkerTask> gridConversionWorkersQueue = new ArrayList<ConversionWorkerTask>();
	private ArrayList<ConversionWorkerTask> lineConversionWorkersQueue = new ArrayList<ConversionWorkerTask>();
	private ArrayList<ConversionWorkerTask> pointConversionWorkersQueue = new ArrayList<ConversionWorkerTask>();
	
	//Queue iterator variable for deciding which queue to draw from next
	private int queueIterator = 0;
	
	//The Orquestrators for each of the data type conversions ( grid, line, point, igrd parent )
	private GridDataConversionOrquestrator gridOrquestrator = new GridDataConversionOrquestrator();
	private LinearDataConversionOrquestrator lineOrquestrator = new LinearDataConversionOrquestrator();
	private PointDataConversionOrquestrator pointOrquestrator = new PointDataConversionOrquestrator();
	
	public Converter() {
	}
	
	// main executions code
	public void run() {
		long startTime, remainingInterval;
		while(!stop) {
			System.out.println("Loop");
			startTime = System.currentTimeMillis();
			stop = updateConversions();
			remainingInterval = ITERATION_INTERVAL - (System.currentTimeMillis() - startTime);
			if( remainingInterval > 0 ) {
				try {
					Thread.sleep(remainingInterval);
				} catch (InterruptedException e) {}
			}
		}
		//**********************************************************************
		// TODO: This is where we will call a function to write the data from
		//       the orquestrators to the file and write the overall file header
		//**********************************************************************
	}
	
	private boolean updateConversions() {
		//*******************************************************************
		// TODO: This is where we interact with all the orquestrators to help 
		//       them manage their conversion processes
		
		//*******************************************************************
		// This is where we check the state of threads and pull more 
		// threads off the queue for execution
		manageThreads();
		//*******************************************************************
		// TODO: This is where we will flush workers of their stored data 
		//       if we are using too much memory. Orquestrators should 
		//       serialize flushed data and store in temp (.ser) files
		//       in application's configured tmp directory. This might
		//       block for a while. Garbage collection should be run here
		//       after flush is done. The orchestrators are expected to
		//       nullify references to flushed data once it's is written
		//       to files.
		flush();
		
		return gridOrquestrator.done(); //&& others
	}

	private void manageThreads() {
		//**********************************************************
		// TODO: starts and stops threads based on the thread limits
		//       and current run states of threads
		//**********************************************************
		
		//Finds finished worker threads
		ArrayList<WorkerThread> finished = new ArrayList<WorkerThread>();
		for( WorkerThread workerThread : conversionWorkers ) {
			if( workerThread.isFinished() ) {
				finished.add(workerThread);
			}
		}
		
		// Sifts through finished worker threads to determine which orquestrator
		// they belong to. Then executes orquestrator callback functions based
		// on success or failure of worker task
		for( WorkerThread workerThread : finished ) {
			conversionWorkers.remove(workerThread);
			Orquestrator orquestrator = null;
			switch( workerThread.type ) {
			case GRID:
				orquestrator = gridOrquestrator;
			case LINE:
				orquestrator = lineOrquestrator;
			case POINT:
				orquestrator = pointOrquestrator;
			}
			if( orquestrator != null ) {
				ConversionWorkerTask cwt = workerThread.getConversionWorkerTask();
				if( workerThread.getConversionWorker().failed() ) {
					orquestrator.onWorkerFailed(cwt);
				} else {
					orquestrator.onWorkerFinished(cwt);
				}
			}
		}
		
		// Fill the remaining available threads with tasks in a round robin
		// fashion from the line grid and  point conversion worker queues
		int availableWorkerSlots = workerCount - conversionWorkers.size();
		
		if( availableWorkerSlots > 0 && totalQueuedWorkerTasks() > 0) {
			int workersAdded=0;
			while( workersAdded < availableWorkerSlots ) {
				ArrayList<ConversionWorkerTask> cwt = null;
				WorkerType wt = null;
				switch(queueIterator) {
				case 0:
					cwt = gridConversionWorkersQueue;
					wt = WorkerType.GRID;
					break;
				case 1:
					cwt = lineConversionWorkersQueue;
					wt = WorkerType.LINE;
					break;
				case 2:
					cwt = pointConversionWorkersQueue;
					wt = WorkerType.POINT;
					break;
				}
				
				if( cwt != null ) {
					startThreadFromQueue( cwt, wt );
					workersAdded++;
				}
				
				queueIterator = queueIterator == 2 ? 0 : queueIterator+1;
			}		
		}
	}
	

	private void flush() {
		
		if( Runtime.getRuntime().freeMemory() < 100000l ) {
			// TODO: flush Workers as needed
		}
		
		// Let's try to reclaim some memory
		Runtime.getRuntime().gc();
	}
	
	private int startThreadFromQueue( ArrayList<ConversionWorkerTask> queue, WorkerType type ) {
		if( queue.size() > 0 ) {
			ConversionWorkerTask workerTask = queue.remove(0);
			WorkerThread thread = new WorkerThread(workerTask, type);
			thread.run();
			return queue.size();
		} else {
			return 0;
		}
	}
	
	private int totalQueuedWorkerTasks() {
		return gridConversionWorkersQueue.size() + lineConversionWorkersQueue.size() + pointConversionWorkersQueue.size();
	}
	
	public static void main( String [] args ) {
		new Converter().run();
	}
	
}
