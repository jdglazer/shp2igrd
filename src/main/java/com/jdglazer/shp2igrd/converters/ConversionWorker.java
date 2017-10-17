package com.jdglazer.shp2igrd.converters;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.jdglazer.igrd.IGRDCommonDTO;

public class ConversionWorker implements Runnable {
	
	private Logger logger = Logger.getLogger(ConversionWorker.class);
	
	private int startLine, endLine;
	
	private ArrayList<IGRDCommonDTO> lineRecords = new ArrayList<IGRDCommonDTO>();
	
	private int progress;
	
	private boolean pause;
	
	private boolean failed = false;
	
	private boolean stopped = true;
	
	private boolean flushing = false;
	
	private boolean flushBlocked = false;
	
	private int flushPoint;
	
	ConversionWorkerTask conversionWorkerTask;
	
	public enum WorkerType {
		GRID,
		LINE,
		POINT
	}
	
	public ConversionWorker( ConversionWorkerTask cwt ) {
		conversionWorkerTask = cwt;
		startLine = cwt.getIterationStartIndex();
		progress = startLine;
		flushPoint = startLine - 1;
		this.endLine = cwt.getIterationEndIndex();
	}
	
	public synchronized boolean execute() {
		
		stopped = false;
		pause = false;
		
		if( finished() ) {
			progress = startLine;
			lineRecords = new ArrayList<IGRDCommonDTO>();
		}
		
		for( ; progress <= endLine; progress++ ) {
			logger.debug("Executing worker tasks for iteration "+progress);
			failed = !conversionWorkerTask.executeConversionForIndex( progress, lineRecords );
			if( pause && progress != endLine ) {
				logger.info("Paused worker at task iteration "+progress);
				progress++;
				stopped = true;
				break;
			}
			flushBlock();
		}
		return true;
	}
	
	public double getProgress() {
		return (double)(progress-startLine)/(double)(endLine-startLine);
	}
	
	public ArrayList<IGRDCommonDTO> getAllRecords() {
		return lineRecords;
	}
	
	public void pause() {
		pause = true;
	}
	
	public boolean finished() {
		return progress == endLine && endLine == ( flushPoint + lineRecords.size() );
	}
	
	public boolean failed() {
		return failed;
	}
	
	public boolean stopped() {
		return stopped;
	}
	
	public int [] flush( ArrayList<IGRDCommonDTO> listToFill ) {
		logger.info("Flushing worker");
		flushing = true;
		int startFlushPoint = flushPoint + 1;
		while( !flushBlocked ) {
			try {
				Thread.sleep( 25 );
			} catch (InterruptedException e) {}
		}
		for( int i = 0; i < lineRecords.size(); i++ ) {
			listToFill.add( lineRecords.get(i) );
		}
		flushPoint += lineRecords.size();
		logger.info("Flushed "+lineRecords.size()+" IGRDCommonDTO objects from worker");
		lineRecords.clear();
		flushing = false;
		return new int[]{ startFlushPoint, flushPoint };
	}
	
	private void flushBlock() {
		logger.debug("Flush blocking worker task execution");
		while( flushing ) {
			flushBlocked = true;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
		flushBlocked = false;
	}

	public void run() {
		execute();
	}
	
}
