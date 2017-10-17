package com.jdglazer.shp2igrd;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.jdglazer.igrd.IGRDCommonDTO;
import com.jdglazer.shp2igrd.converters.ConversionWorkerTask;
import com.jdglazer.shp2igrd.converters.Orquestrator;
import com.jdglazer.shp2igrd.converters.grid.GridDataConversionOrquestrator;
import com.jdglazer.shp2igrd.converters.line.LinearDataConversionOrquestrator;
import com.jdglazer.shp2igrd.converters.point.PointDataConversionOrquestrator;

public class ConversionThreadManager {
	
	//The logger for the class
	private Logger logger = Logger.getLogger(ConversionThreadManager.class);
	
	//Thread execution management and settings
	private final int ITERATION_INTERVAL = 1000;
	private short workerCount = 1;
	private boolean stop = false;
	
	//The worker threads. The thread limit will limit how many of these we run at once
	private ArrayList<WorkerThread> conversionWorkers = new ArrayList<WorkerThread>();
	
	// These are the queues for conversion tasks
	private HashMap<String,ArrayList<ConversionWorkerTask>> conversionWorkerQueues = new HashMap<String,ArrayList<ConversionWorkerTask>>();
	
	//The orquestrators to call
	private HashMap<String,Orquestrator> orquestrators = new HashMap<String,Orquestrator>();
	
	//Queue iteration variable to tell us which queue we pulled our last worker to run from
	private int queueIterator;
	
	public ConversionThreadManager( Orquestrator... os ) {
		BasicConfigurator.configure();
		
		// Initialize queues
		for( Orquestrator o : os ) {
			orquestrators.put( o.getOrquestratorType(), o );
			conversionWorkerQueues.put(o.getOrquestratorType(), new ArrayList<ConversionWorkerTask>() );
		}
	}
	
	// main executions code
	public void run() {
		long startTime, remainingInterval;
		logger.info("Starting converter");
		while(!stop) {
			logger.debug("Running a conversion management iteration");
			startTime = System.currentTimeMillis();
			stop = updateConversions();
			remainingInterval = ITERATION_INTERVAL - (System.currentTimeMillis() - startTime);
			if( remainingInterval > 0 ) {
				try {
					Thread.sleep(remainingInterval);
				} catch (InterruptedException e) {}
			}
		}
		logger.info("Exiting. Conversion tasks complete");
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
		
		return allOrquestratorsDone();
	}

	private void manageThreads() {
		//**********************************************************
		// TODO: starts and stops threads based on the thread limits
		//       and current run states of threads
		//**********************************************************
		logger.debug("Running thread management");
		
		//Let's give all orquestrators a chance to queue up tasks to be executed
		allowOrquestratorsToQueueTasks();
		
		//We remove finished workers and notify parent orquestrator
		cleanFinishedWorkers();
		
		//We fill the available worker slots opened up by the cleaning task above
		fillAvailableWorkerSpots();
		

	}
	

	private void flush() {
		long freeMemory = Runtime.getRuntime().freeMemory();
		logger.debug("Checking for thread flush condition");
		if( freeMemory < 100000l ) {
			logger.info("Low on free memory: "+freeMemory+" available");
			logger.info("Flushing running workers");
			for( WorkerThread wt : conversionWorkers ) {
				ArrayList<IGRDCommonDTO> flushedDTOS = new ArrayList<IGRDCommonDTO>();
				int [] indices = wt.getConversionWorker().flush(flushedDTOS);
				orquestrators.get( wt.getParentOrquestratorType() ).onFlush( flushedDTOS, wt.getConversionWorkerTask().getClass(), indices[0], indices[1] );
			}
		} 
		// Let's try to reclaim some memory
		Runtime.getRuntime().gc();
	}
	
	/**
	 * We give each orquestrator a chance to queue up to 10 tasks to be performed 
	 */
	private void allowOrquestratorsToQueueTasks() {
		ConversionWorkerTask [] cwts = new ConversionWorkerTask[10];
		for( String s : orquestrators.keySet() ) {
			Orquestrator o = orquestrators.get(s);
			if( !o.done() ) {
				o.addTaskToQueue(cwts);
				for( int i = 0; i < cwts.length ; i++ ) {
					ConversionWorkerTask cwt = cwts[i];
					if( cwt != null ) {
						ArrayList<ConversionWorkerTask> tasks = conversionWorkerQueues.get(o.getOrquestratorType());
						if( tasks != null ) {
							tasks.add(cwt);
						} else {
							logger.error("No registered queue for orquestrator type: Type = "+o.getOrquestratorType() );
						}
					}
					cwts[i] = null;
				}
			}
		}
	}
	
	/**
	 * Removes finished workers threads and notifies parent orquestrator that the task finished or
	 * failed
	 */
	private void cleanFinishedWorkers() {
		//Finds finished worker threads
		ArrayList<WorkerThread> finished = new ArrayList<WorkerThread>();
		for( WorkerThread workerThread : conversionWorkers ) {
			if( workerThread.isFinished() ) {
				finished.add(workerThread);
				logger.debug("Found a terminated thread of type "+workerThread.getParentOrquestratorType() );
			}
		}
		
		// Sifts through finished worker threads to determine which orquestrator
		// they belong to. Then executes orquestrator callback functions based
		// on success or failure of worker task
		for( WorkerThread workerThread : finished ) {
			conversionWorkers.remove(workerThread);
			logger.info("Removing completed conversion worker thread");
			Orquestrator orquestrator = orquestrators.get(workerThread.getParentOrquestratorType());
			if( orquestrator != null ) {
				ConversionWorkerTask cwt = workerThread.getConversionWorkerTask();
				if( workerThread.getConversionWorker().failed() ) {
					logger.warn("Detected a failed conversion worker task - type: "+workerThread.getParentOrquestratorType() );
					orquestrator.onWorkerFailed(cwt);
				} else {
					logger.debug("Successful conversion worker task - type: "+workerThread.getParentOrquestratorType() );
					orquestrator.onWorkerFinished(cwt);
				}
			}
		}
	}
	
	/**
	 * A function to fills available worker thread spots in a round robin fashion
	 * from each orquestrator's queue
	 */
	private void fillAvailableWorkerSpots() {
		// Fill the remaining available threads with tasks in a round robin
		// fashion from the line grid and  point conversion worker queues. 
		int availableWorkerSlots = workerCount - conversionWorkers.size();
		Object [] keySet = conversionWorkerQueues.keySet().toArray();
		
		if( availableWorkerSlots > 0 ) {
			logger.debug(""+availableWorkerSlots+" available worker slots with "
							+totalQueuedWorkerTasks()+" queued workers waiting");
			int workersAdded=0;
			while( workersAdded < availableWorkerSlots && totalQueuedWorkerTasks() > 0 ) {
				ArrayList<ConversionWorkerTask> cwt = null;
				String parentOrquestrator = null;
				if( queueIterator > keySet.length ) {
					cwt = conversionWorkerQueues.get( keySet[queueIterator] );
					parentOrquestrator = (String) keySet[queueIterator];
					queueIterator++;
					if( cwt != null ) {
						logger.info("starting a new worker thread");
						workersAdded += startThreadFromQueue( cwt, parentOrquestrator );
					}
				} else {
					queueIterator = 0;
					continue;
				}
			}
		}
	}
	
	
	/**
	 * Returns the number of workers started
	 * @param queue
	 * @param type
	 * @return
	 */
	private int startThreadFromQueue( ArrayList<ConversionWorkerTask> queue, String type ) {
		if( queue.size() > 0 ) {
			ConversionWorkerTask workerTask = queue.remove(0);
			WorkerThread thread = new WorkerThread(workerTask, type);
			thread.run();
			conversionWorkers.add(thread);
			return 1;
		} else {
			logger.warn("No workers on the queue to start");
			return 0;
		}
	}
	
	/**
	 * Gets the total number of queued tasks for all orquestators
	 * @return
	 */
	private int totalQueuedWorkerTasks() {
		int taskCount = 0;
		for( String s : conversionWorkerQueues.keySet() ) {
			taskCount += conversionWorkerQueues.get(s).size();
		}
		return taskCount;
	}
	
	private boolean allOrquestratorsDone() {
		int doneness = 0;
		for( String s : orquestrators.keySet() ) {
			doneness += orquestrators.get(s).done() ? 1 : 0;
		}
		if( orquestrators.size() == 0 ) {
			logger.warn("No task orquestrators registered");
		}
		return doneness == orquestrators.size();
	}
	
}
