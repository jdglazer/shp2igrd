package com.jdglazer.shp2igrd.generators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jdglazer.igrd.grid.GridDataLineDTO;
import com.jdglazer.igrd.utils.GridSegmentShortOverflowDTO;
import com.jdglazer.shp2igrd.shp.InvalidFileTypeException;
import com.jdglazer.shp2igrd.shp.PolygonShapeFile;
import com.jdglazer.shp2igrd.shp.RecordOutOfBoundsException;
import com.jdglazer.shp2igrd.shp.ShapeFile;

public class GridLineGenerator {
	
	private PolygonShapeFile pShapeFile;
	
	public GridLineGenerator( PolygonShapeFile psf ) {
		pShapeFile = psf;
	}
	
	public GridDataLineDTO generateLine( double latitude, double lonInterval ) {
		ArrayList<Intersections[]> intersections = null;
		try {
			intersections = getAllIntersections( latitude );
		} catch (IOException e) {
		} catch (RecordOutOfBoundsException e) {
		}
		
		double longitude = intersections.get(0)[0].longitude + lonInterval/2.0;
		GridDataLineDTO gridLineRecordDTO = new GridDataLineDTO((short)2);
		GridDataLineDTO.PartDTO partDTO = gridLineRecordDTO.new PartDTO();
		partDTO.setStartLongitude((float)longitude);
		
		for ( int i = 0; i < intersections.size() ; i++ ) {
			Intersections intersection1 = intersections.get(i)[0],
					      intersection2 = intersections.get(i)[1];
			int intersectionCount = 0;
			
			if ( longitude <= intersection1.longitude && i > 0 ) {
				double longitudeDelta = intersection1.longitude - intersections.get( i - 1 )[1].longitude;
				longitude = ((int) ( ( intersection1.longitude - longitude )/lonInterval ) + 1)*lonInterval + longitude;
				if( lonInterval <= longitudeDelta ) {
					gridLineRecordDTO.addPart( partDTO );
					partDTO = gridLineRecordDTO.new PartDTO();
					partDTO.setStartLongitude((float)longitude);
				}
			} 
			
			if( longitude > intersection1.longitude && longitude <= intersection2.longitude ) {
				intersectionCount = (int) ( (intersection2.longitude - longitude)/lonInterval + 1 );
				GridSegmentShortOverflowDTO sodto = GridSegmentShortOverflowDTO.getOverflowDTO(intersectionCount, partDTO.getSegmentCount(), gridLineRecordDTO.getNumberParts());
				longitude = ( (double) intersectionCount ) * lonInterval + longitude;
				GridDataLineDTO.SegmentDTO segmentDTO = gridLineRecordDTO.new SegmentDTO();
				segmentDTO.setSegmentIndex(intersection1.index);
				segmentDTO.setSegmentLength((short)intersectionCount);
				partDTO.addSegment(segmentDTO);
				if( sodto != null ) {
					gridLineRecordDTO.addShortOverflow(sodto);
				}
				
			} else if( longitude > intersection2.longitude ) {
				continue;
			} 
		}
		
		if( partDTO.getSegmentCount() > 0 )
			gridLineRecordDTO.addPart(partDTO);

		return gridLineRecordDTO;
	}
	
	public static double intersectionLongitude( double latitude, double [] seg1, double [] seg2, double [] seg3 ) {
		
		double maxSegLat, minSegLat;
		if( seg1[0] > seg2[0]) {
			maxSegLat = seg1[0];  minSegLat = seg2[0];
		} else {
			maxSegLat = seg2[0];  minSegLat = seg1[0];			
		}
		if( latitude > minSegLat && latitude < maxSegLat ) {
			double b = seg1[0] - ((seg2[0] -seg1[0])/(seg2[1]-seg1[1]))*seg1[1];
			return (latitude - b)*(seg2[1]-seg1[1])/(seg2[0]-seg1[0]);
		}
		
		if( latitude == seg2[0]) {
			double m1 = (seg2[0] - seg1[0])/(seg2[1] - seg1[1]),
				   m2 = (seg3[0] - seg2[0])/(seg3[1] - seg2[1]);
			
			if( !(m1 >= 0 && m2 <=0 ) && !(m2 >= 0 && m1 <=0 ) ) {
				return seg2[1];
			}
			
		}
		
		return -181.0;
	}
	
	private boolean latitudeInRecordBounds( int recordIndex, double latitude ) throws RecordOutOfBoundsException, IOException {
		
		return latitude <= pShapeFile.maxLat(recordIndex) && latitude >= pShapeFile.minLat(recordIndex);
	}
	
	public ArrayList<Double> getIntersections( double latitude, int recordIndex ) throws IOException, RecordOutOfBoundsException {
		
		ArrayList<Double> intersections = new ArrayList<Double>(); 
		
		for( int i = 0; i < pShapeFile.partCount(recordIndex); i++ ) {
			
			for( int j = 0; j < pShapeFile.partLength(recordIndex, i ); j++ ) {
				
				double [] latLon1 = j == 0 
						? pShapeFile.getLatLon(recordIndex, i, pShapeFile.partLength(recordIndex, i) - 1 ) 
						: pShapeFile.getLatLon(recordIndex, i, j-1);
				
				double [] latLon2 = pShapeFile.getLatLon(recordIndex, i, j);
				
				double [] latLon3 = pShapeFile.partLength( recordIndex, i ) == j + 1 
									? pShapeFile.getLatLon(recordIndex, i, 0 )
									: pShapeFile.getLatLon(recordIndex, i, j+1);
				
				double intersection = intersectionLongitude( latitude, latLon1, latLon2, latLon3 );
			    
				if( intersection <= 180.0 && intersection >= -180.0 )
					
					intersections.add(intersection);
			}
			
		}
		
		Collections.sort(intersections, new Comparator<Double>() {
			public int compare(Double arg0, Double arg1) {
				double value = arg0 - arg1;
				if( value > 0 )
					return 1;
				else if ( value < 0 )
					return -1;
				else
					return 0;
			}
			
		});
		
		return intersections;
	}
	
	public ArrayList<Intersections[]> getAllIntersections( double latitude ) throws IOException, RecordOutOfBoundsException {
		
		ArrayList<Intersections[]> intersections = new ArrayList<Intersections[]>();
		
		for( int i = 0 ; i < pShapeFile.recordCount() ; i++ ) {
			
			if( latitudeInRecordBounds( i, latitude ) ) {
				
				ArrayList<Double> inters = getIntersections( latitude, i );
				
				for( int j = 0 ; j < inters.size()/2; j++) {
					Intersections [] intersect = new Intersections[] {
									new Intersections( (short)i, latitude, inters.get(j*2)),
									new Intersections( (short)i, latitude, inters.get(j*2 + 1))
							};
					intersections.add( intersect );
				}
			}
		}
		
		Collections.sort( intersections, new Comparator<Intersections[]>() {

			public int compare(Intersections [] arg0, Intersections [] arg1) {
				double value = (arg0[0].longitude - arg1[0].longitude);
				if( value > 0 )
					return 1;
				else if ( value < 0 )
					return -1;
				else
					return 0;
			}
			
		});
		
		return intersections;
	}
	
	public PolygonShapeFile getPolygonShapeFile() {
		return pShapeFile;
	}
	
	public class Intersections {
		
		short index;
		double latitude;
		double longitude;
		
		public Intersections ( short index, double latitude, double longitude ) {
			this.index = index ;
			this.latitude = latitude ;
			this.longitude = longitude;
		}
	}
	
	public static void main( String [] args ) throws FileNotFoundException, InvalidFileTypeException, IOException, RecordOutOfBoundsException, Exception {

		Runnable run1 = new Runnable() {
			GridLineGenerator glg = new GridLineGenerator( new PolygonShapeFile( new ShapeFile( "/home/jglazer/Downloads/PAgeol_dd/pageol_poly_dd" ) ) );
			public void run() {
				for( double i = 0.0; i < 1.0 ; i++ ) {
					GridDataLineDTO gdl = glg.generateLine( 40.657+i*.00018, .00018 );
					System.out.println( "Part count: " + gdl.getNumberParts() );
					System.out.println( "Segment count: " + gdl.getPart(0).getSegmentCount() );
					System.out.println( "Segment 145 point count: " + gdl.getPart(0).getSegment(145).getSegmentLength() );
					System.out.println( "Part count: " + gdl.getNumberParts() );
					System.out.println( "Part 0 start longitude: " + gdl.getPart(0).getStartLongitude() );
					System.out.println( "Out of line point count: " + gdl.getPart(0).getPointCount() );
					System.out.println( "Expected Point Count: "+(-75.19543835004856 + 80.51917)/.00018 );
				}
			}
		};
		
		Runnable run2 = new Runnable() {
			GridLineGenerator glg = new GridLineGenerator( new PolygonShapeFile( new ShapeFile( "/home/jglazer/Downloads/PAgeol_dd/pageol_poly_dd" ) ) );
			public void run() {
				for( double i = 0.0; i < 66.0 ; i++ ) {
					glg.generateLine( 40.675+i*.00018, .00018 );
					System.out.println(i);
				}
			}
		};
		
		Runnable run3 = new Runnable() {
			GridLineGenerator glg = new GridLineGenerator( new PolygonShapeFile( new ShapeFile( "/home/jglazer/Downloads/PAgeol_dd/pageol_poly_dd" ) ) );
			public void run() {
				for( double i = 0.0; i < 67.0 ; i++ ) {
					glg.generateLine( 40.675+i*.00018, .00018 );
					System.out.println(i);
				}
			}
		};
		
		Thread thread1 = new Thread( run1 );
		Thread thread2 = new Thread( run2 );
		Thread thread3 = new Thread( run3 );
		long time = System.currentTimeMillis();
		thread1.start();
		//thread2.start();
		//thread3.start();
		do {
			Thread.sleep( 100 );
		} while ( thread1.isAlive() || thread2.isAlive() || thread3.isAlive());
		System.out.println( System.currentTimeMillis() - time );
		//glg.getIntersections(40.657, 6603);
	}
}
