package com.jdglazer.shp2igrd.converters;

import java.util.ArrayList;

import com.jdglazer.igrd.IGRDCommonDTO;

public class ConversionWorker implements Runnable {
	
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
	
	public synchronized boolean getLines() {
		
		stopped = false;
		pause = false;
		
		if( finished() ) {
			progress = 0;
			lineRecords = new ArrayList<IGRDCommonDTO>();
		}
		
		for( ; progress <= endLine; progress++ ) {
			failed = !conversionWorkerTask.executeConversionForIndex( progress, lineRecords );
			if( pause && progress != endLine ) {
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
	
	public ArrayList<IGRDCommonDTO> flush() {
		flushing = true;
		while( !flushBlocked ) {
			try {
				Thread.sleep( 25 );
			} catch (InterruptedException e) {}
		}
		ArrayList<IGRDCommonDTO> flush = new ArrayList<IGRDCommonDTO>();
		for( int i = 0; i < lineRecords.size(); i++ ) {
			flush.add( lineRecords.get(i) );
		}
		flushPoint += lineRecords.size();
		lineRecords.clear();
		flushing = false;
		return flush;
	}
	
	private void flushBlock() {
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
		getLines();
	}
	
}
