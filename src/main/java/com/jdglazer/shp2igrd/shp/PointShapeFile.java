package com.jdglazer.shp2igrd.shp;

import java.io.FileNotFoundException;
import java.io.IOException;

public class PointShapeFile extends ShapeFile {
	
	public PointShapeFile( ShapeFile shapeFile ) throws FileNotFoundException, IOException, InvalidFileTypeException {
		
		super( shapeFile );
		
		if( shapeFile.getShapeType() != ShapeFile.POINT )
			
			throw new InvalidFileTypeException("Not a point file type shape.");
	}
	
	public float [] getPoint( int recordIndex ) throws IOException, RecordOutOfBoundsException {

		double longitude =  getDoubleFrom( (short) 0, L_END, recordOffset( recordIndex ) + 12 );
		
		double latitude = getDoubleFrom( (short) 0, L_END, recordOffset( recordIndex ) + 16 );
		
		return new float[] { (float) latitude, (float) longitude };
	}
}
