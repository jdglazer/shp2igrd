package com.jdglazer.shp2igrd.igrd;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.jdglazer.shp2igrd.utils.FileModel;

public class IntegratedGridFile extends FileModel {
	
	public static final int FILE_CODE = 12739; 
	
	private GridData gridData;
	
	private LineData linearData;
	
	private PointData pointData;

	public IntegratedGridFile( RandomAccessFile raf ) {
		super( new RandomAccessFile[]{ raf } );
	}
	
	public IntegratedGridFile( FileInputStream fis ) throws IOException {
		super ( new FileInputStream[]{ fis } );
	}
	
	public long getFileLength() {
		try {
			return getFileLength(0);
		} catch (IOException e) {
			return -1L;
		}
	}

	public int getGridDataOffset( ) {
		return getIntFrom( (short) 0, FileModel.L_END, 4 );
	}
	
	public boolean isGridDataPresent( ) {
		return 16 <= getGridDataOffset( );
	}
	
	public int getLinearDataOffset( ) {
		return getIntFrom( (short) 0, FileModel.L_END, 8 );
	}
	
	public boolean isLinearDataPresent( ) {
		return 16 <= getLinearDataOffset( );
	}
	
	public int getPointDataOffset( ) {
		return getIntFrom( (short) 0, FileModel.L_END, 12 );
	}
	
	public boolean isPointDataPresent( ) {
		return 16 <= getPointDataOffset( );
	}
	
	public GridData getGridData() {
		return gridData;
	}
	
	public LineData getLineData() {
		return linearData;
	}
	
	public PointData getPointData() {
		return pointData;
	}
	
	private class GridData {
		
		private int o() {
			return getGridDataOffset();
		}
		
		private double dv( int offset ) throws IOException {
			return getDoubleFrom( (short) 0, FileModel.B_END, o() + offset );
		}
		
		private float fv( int offset ) {
			return getFloatFrom( (short) 0, FileModel.B_END, o() + offset );
		}
		
		private int iv( int offset ) {
			return getIntFrom( (short) 0, FileModel.B_END, o() + offset );
		}
		
		private short sv( int offset ) {
			return getShortFrom( (short) 0, FileModel.B_END, o() + offset );
		}
		
		private int lo( int lineIndex ) throws IOException, IndexOutOfBounds {
			if( lineIndex < 0 || getLineCount() <= lineIndex )
				throw new IndexOutOfBounds( "Invalid line index provided" );
			return iv( 54 + lineIndex*4 );
		}
		
		private void validPart( int lineIndex, int partIndex ) throws IndexOutOfBounds, IOException {
			if( partIndex < 0 || getPartCount( lineIndex ) <= partIndex )
				throw new IndexOutOfBounds( "invalid part count for line "+lineIndex );
		}
		
		private void validSegment( int lineIndex, int partIndex, int segmentIndex ) throws IndexOutOfBounds, IOException {
			if( segmentIndex < 0 || segmentIndex <= getPartSegmentCount( lineIndex, partIndex) )
				throw new IndexOutOfBounds( "Segment "+segmentIndex+" is out bounds for line "+lineIndex+" part "+partIndex );
		}
		
		public double getMinLat() throws IOException {
			return dv( 0 );
		}
		
		public double getMaxLat() throws IOException {
			return dv( 16 );
		}
		
		public double getMinLon() throws IOException {
			return dv( 8 );
		}		
		
		public double getMaxLon() throws IOException {
			return dv( 24 );
		}
		
		public double getLatInterval() throws IOException {
			return dv( 32 );
		}
		
		public double getLonInterval() throws IOException {
			return dv( 40 );
		}
		
		public int getLineCount() throws IOException {
			return iv( 48 );
		}
		
		public short getIndexLength() throws IOException {
			return sv( 52 );
		}
		
		public int getLineSize( int lineIndex ) throws IOException, IndexOutOfBounds {
			return iv( lo( lineIndex )  );
		}
		
		public int getPartCount( int lineIndex ) throws IOException, IndexOutOfBounds {
			return iv( lo( lineIndex ) + 4  );
		}
		
		public int getShortOverflowCount( int lineIndex ) throws IOException, IndexOutOfBounds {
			return iv( lo( lineIndex ) + 8  );
		}
		
		public int getShortOverflowOffset( int lineIndex, int shortOverflowNum ) throws IOException, IndexOutOfBounds {
			if( shortOverflowNum < 0 || shortOverflowNum >=  getShortOverflowCount( lineIndex ) )
				throw new IndexOutOfBoundsException( "Invalid short overflow count provided" );
			return iv( lo( lineIndex ) + 12 + shortOverflowNum*6 );
		}
		
		public int getShortOverflowIndex( int lineIndex, int shortOverflowNum ) throws IOException, IndexOutOfBounds {
			if( shortOverflowNum < 0 || shortOverflowNum >=  getShortOverflowCount( lineIndex ) )
				throw new IndexOutOfBoundsException( "Invalid short overflow count provided" );
			return sv( lo( lineIndex ) + 16 + shortOverflowNum*6 );
		}
		
		public int getPartOffset( int lineIndex, int partIndex ) throws IOException, IndexOutOfBounds {
			validPart( lineIndex, partIndex );
			return iv( lo( lineIndex ) + 12 + getShortOverflowCount( lineIndex )*6 + partIndex*4 );
		}
		
		public float getPartStartLongitude( int lineIndex, int partIndex ) throws IOException, IndexOutOfBounds {
			return fv( getPartOffset( lineIndex, partIndex ) );
		}
		
		public int getPartPointCount( int lineIndex, int partIndex ) throws IOException, IndexOutOfBounds {
			return iv( getPartOffset( lineIndex, partIndex ) + 4 );
		}
		
		public int getPartSegmentCount( int lineIndex, int partIndex ) throws IOException, IndexOutOfBounds {
			return iv( getPartOffset( lineIndex, partIndex ) + 8 );
		}
		
		public short getSegmentIndex( int lineIndex, int partIndex, int segmentNum ) throws IndexOutOfBounds, IOException {
			validSegment( lineIndex, partIndex, segmentNum );
			int iLen = getIndexLength(),
				sOff = getPartOffset( lineIndex, partIndex ) + 12 + segmentNum*(4+iLen);
			return iLen == 1 
					? (short) getByteFrom( (short) 0, FileModel.B_END, sOff )
					: sv( sOff ) ;
		}
		
		public int getSegmentPointCount( int lineIndex, int partIndex, int segmentNum ) throws IndexOutOfBounds, IOException {
			validSegment( lineIndex, partIndex, segmentNum );
			int iLen = getIndexLength();
			return iv( getPartOffset( lineIndex, partIndex ) + 12 + iLen + segmentNum*(iLen+4) );
		}
	}
	
	public class LineData {
		private int o() {
			return getLinearDataOffset();
		}
		
		private double dv( int offset ) throws IOException {
			return getDoubleFrom( (short) 0, FileModel.B_END, o() + offset );
		}
		
		private float fv( int offset ) {
			return getFloatFrom( (short) 0, FileModel.B_END, o() + offset );
		}
		
		private int iv( int offset ) {
			return getIntFrom( (short) 0, FileModel.B_END, o() + offset );
		}
		
		private short sv( int offset ) {
			return getShortFrom( (short) 0, FileModel.B_END, o() + offset );
		}
		
		public double getMinLat() throws IOException {
			return dv( 0 );
		}
		
		public double getMaxLat() throws IOException {
			return dv( 16 );
		}
		
		public double getMinLon() throws IOException {
			return dv( 8 );
		}		
		
		public double getMaxLon() throws IOException {
			return dv( 24 );
		}
		
		public int getLineCount() throws IOException {
			return iv( 32 );
		}
		
		public int getRecordOffset( int recordIndex ) throws IOException, IndexOutOfBounds {
			if( getLineCount() <= recordIndex || recordIndex < 0 )
				throw new IndexOutOfBounds( "Invalid linear data record index" );
			return iv( 36 + recordIndex*4 );
		}
		
		public short getRecordIndex( int recordOffsetIndex ) throws IOException, IndexOutOfBounds {
			return sv( getRecordOffset( recordOffsetIndex ) );
		}
		
		public float getLineMinLat( int recordOffsetIndex ) throws IOException, IndexOutOfBounds {
			return fv( getRecordOffset( recordOffsetIndex ) + 2 );
		}
		
		public float getLineMaxLat( int recordOffsetIndex ) throws IOException, IndexOutOfBounds {
			return fv( getRecordOffset( recordOffsetIndex ) + 6 );
		}
		
		public float getLineMinLon( int recordOffsetIndex ) throws IOException, IndexOutOfBounds {
			return fv( getRecordOffset( recordOffsetIndex ) + 10 );
		}		
		
		public float getLineMaxLon( int recordOffsetIndex ) throws IOException, IndexOutOfBounds {
			return fv( getRecordOffset( recordOffsetIndex ) + 14 );
		}
		
		public int getPointCount( int recordOffsetIndex ) throws IOException, IndexOutOfBounds {
			return iv( getRecordOffset( recordOffsetIndex ) + 18 );
		}
		
		public float [] getPoint( int recordOffsetIndex, int pointOffsetIndex ) throws IOException, IndexOutOfBounds {
			if( getPointCount( recordOffsetIndex ) <= pointOffsetIndex || pointOffsetIndex < 0 )
				throw new IndexOutOfBounds("Invalid point for record "+recordOffsetIndex);
			return new float[] { fv ( getRecordOffset( recordOffsetIndex ) + 18 + 8*pointOffsetIndex ), 
								 fv ( getRecordOffset( recordOffsetIndex ) + 22 + 8*pointOffsetIndex )
								}; 
		}
	}
	
	public class PointData {
		
	}
}
