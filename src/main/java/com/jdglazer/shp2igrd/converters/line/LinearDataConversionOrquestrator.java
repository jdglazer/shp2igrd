package com.jdglazer.shp2igrd.converters.line;

import java.io.File;
import java.util.ArrayList;

import com.jdglazer.igrd.IGRDCommonDTO;
import com.jdglazer.shp2igrd.ConversionProgressDTO;
import com.jdglazer.shp2igrd.converters.ConversionWorkerTask;
import com.jdglazer.shp2igrd.converters.Orquestrator;

public class LinearDataConversionOrquestrator implements Orquestrator {

	public void addTaskToQueue(ConversionWorkerTask[] workerTask) {
		// TODO Auto-generated method stub

	}

	public void onWorkerFinished(ConversionWorkerTask finishedWorker) {
		// TODO Auto-generated method stub

	}

	public void onWorkerFailed(ConversionWorkerTask failedWorker) {
		// TODO Auto-generated method stub

	}

	public void updateConversionProgressBus(ConversionProgressDTO conversionProgressDTO) {
		// TODO Auto-generated method stub

	}

	public void onFlush(ArrayList<IGRDCommonDTO> flushedObjects, Class taskType, int startIndex, int endIndex) {
		// TODO Auto-generated method stub

	}

	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getOrquestratorType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean success() {
		// TODO Auto-generated method stub
		return false;
	}

	public void writeToFile(File file) {
		// TODO Auto-generated method stub
		
	}

}
