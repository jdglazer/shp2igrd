package com.jdglazer.shp2igrd.igrd.grid;

import java.util.ArrayList;

import com.jdglazer.shp2igrd.utils.GridSegmentShortOverflow;

public class GridDataLineDTO {
	
	public GridDataLineDTO( short indexType ) {
		segmentIndexType = indexType <= 2 && indexType > 0 ? indexType : 2 ;
	}

/**
 * subject to parent grid data setting
 */
	private short segmentIndexType;
	
/**
 * The size of the line in bytes, grid line offset 0
 */
	private int lineSize = 12;
	
/**
 * The number of geographically separated parts in the line, grid line offset 4
 */
	private int numberParts = 0;
	
/**
 * The number of short overflows in the part, grid line offset 8
 */
	private int shortOverflowCount = 0;
	
/**
 * overflow segments where short value was insufficient, grid line offset 12
 */
	private ArrayList<GridSegmentShortOverflow> shortOverflows;
	
/**
 * The starting position in the grid line of each part
 */
	private ArrayList<Integer> partStarts;
	
/**
 * The actual part data
 */
	private ArrayList<PartDTO> parts;
	
	/**
	 * @return the lineSize
	 */
	public int getLineSize() {
		return lineSize;
	}

	/**
	 * @param lineSize the lineSize to set
	 */
	public void setLineSize(int lineSize) {
		this.lineSize = lineSize;
	}

	/**
	 * @return the numberParts
	 */
	public int getNumberParts() {
		return numberParts;
	}

	/**
	 * @param numberParts the numberParts to set
	 */
	public void setNumberParts(int numberParts) {
		this.numberParts = numberParts;
	}

	/**
	 * @return the shortOverflowCount
	 */
	public int getShortOverflowCount() {
		return shortOverflowCount;
	}

	/**
	 * @param shortOverflowCount the shortOverflowCount to set
	 */
	public void setShortOverflowCount(int shortOverflowCount) {
		this.shortOverflowCount = shortOverflowCount;
	}
	
	/**
	 * adds a part and updates part count, part start offset, and line byte length
	 * @param part
	 */
	public void addPart( PartDTO part ) {
		this.parts.add( part );
		numberParts++;
		partStarts.add( lineSize );
		lineSize += 16 + (4+segmentIndexType)*part.getSegmentCount();
	}
	
	public PartDTO getPart( int index ) {
		return this.parts.get( index );
	}

	public short getSegmentIndexType() {
		return segmentIndexType;
	}

	public void setSegmentIndexType(short segmentIndexType) {
		this.segmentIndexType = segmentIndexType;
	}

	public ArrayList<GridSegmentShortOverflow> getShortOverflows() {
		return shortOverflows;
	}

	public void addShortOverflow( GridSegmentShortOverflow overflow ) {
		this.shortOverflows.add( overflow );
		shortOverflowCount++;
		lineSize+= 6;
		for( int i = 0; i < partStarts.size(); i++ ) {
			partStarts.set(i, partStarts.get(i).intValue()+6 );
		}
	}
	
	public GridSegmentShortOverflow getShortOverflow( int index ) {
		return shortOverflows.get(index);
	}
	
	public class PartDTO {
		
	/**
	 * line -> part offset 0
	 */
		private float startLongitude;
		
	/**
	 * line -> part offset 4
	 */
		private int pointCount = 0;
		
	/**
	 * line -> part offset 8
	 */
		private int segmentCount = 0;
		
		private ArrayList<SegmentDTO> segments;

		/**
		 * @return the startLongitude
		 */
		public float getStartLongitude() {
			return startLongitude;
		}

		/**
		 * @param startLongitude the startLongitude to set
		 */
		public void setStartLongitude(float startLongitude) {
			this.startLongitude = startLongitude;
		}

		/**
		 * @return the pointCount
		 */
		public int getPointCount() {
			return pointCount;
		}

		/**
		 * @return the segmentCount
		 */
		public int getSegmentCount() {
			return segmentCount;
		}
		
		/**
		 * 
		 */
		public SegmentDTO getSegment( int index ) {
			return segments.get( index );
		}
		
		/**
		 * 
		 * @param segment
		 */
		public void addSegment( SegmentDTO segment ) {
			segments.add( segment );
			pointCount += segment.getSegmentLength();
			segmentCount++;
		}
		
	}
	
	public class SegmentDTO {
		
		/**
		 * line -> part -> segment offset 0
		 */
		private short segmentIndex;
		
		/**
		 * line -> part -> segment offset 1 or 2
		 */
		private short segmentLength;

		/**
		 * @return the segmentIndex
		 */
		public short getSegmentIndex() {
			return segmentIndex;
		}

		/**
		 * @param segmentIndex the segmentIndex to set
		 */
		public void setSegmentIndex(short segmentIndex) {
			this.segmentIndex = segmentIndex;
		}

		/**
		 * @return the segmentLength
		 */
		public short getSegmentLength() {
			return segmentLength;
		}

		/**
		 * @param segmentLength the segmentLength to set
		 */
		public void setSegmentLength(short segmentLength) {
			this.segmentLength = segmentLength;
		}
		
	}
	
}
