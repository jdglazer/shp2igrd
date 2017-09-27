package com.jdglazer.shp2igrd;

import com.jdglazer.shp2igrd.converters.ConversionWorker.WorkerType;
import com.jdglazer.shp2igrd.converters.ConversionWorker;
import com.jdglazer.shp2igrd.converters.ConversionWorkerTask;

public class WorkerThread {
	//Tells us the type of worker
	public WorkerType type;
	// A specific task to be executed by a generic worker
	private ConversionWorkerTask task;
	// A generic runnable worker to execute task and store and flush data from task
	private ConversionWorker worker;
	//A thread to run the task
	private Thread thread;
	
	public WorkerThread( ConversionWorkerTask task, WorkerType type ) {
		worker = new ConversionWorker( task );
		this.type = type;
		thread = new Thread( worker );
	}
	
	public void run() {
		if( isRunning() )
			return;
		Thread.State state = thread.getState();
		if(  state != Thread.State.NEW )
			thread = new Thread(worker);
		thread.start();
	}
	
	public boolean isRunning() {
		return thread.isAlive();
	}
	
	public void stop() {
		thread.interrupt();
	}
	
	public boolean isFinished() {
		return thread.getState() == Thread.State.TERMINATED;
	}
	
	public ConversionWorkerTask getConversionWorkerTask() {
		return task;
	}
	
	public ConversionWorker getConversionWorker() {
		return worker;
	}
}
