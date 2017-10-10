package com.emd.xlsutil.velocity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.emd.xlsutil.format.DataTransformer;

import com.emd.util.ClassUtils;
import com.emd.util.Stringx;

/**
 * <code>ExcelSheet</code> represents information about an Excel sheet.
 * Basically it acts as decorator for POI's <code>Sheet</code> interface
 *
 * Created: Wed Dec 21 07:50:21 2016
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class ExcelSheet {
    private Sheet sheet;

    private static Log log = LogFactory.getLog(ExcelSheet.class);

    private static final String[] suppressedKeys = {
	"header",
	"footer",
	"firstHeader",
	"firstFooter",
	"workbook",
	"cTWorksheet",
	"printSetup",
	"drawingPatriarch",
	"evenHeader",
	"evenFooter",
	"oddFooter",
	"oddHeader",
	"columnHelper",
	"sheetConditionalFormatting",
	"relationParts",
        "dataValidationHelper"
    }; 

    private static final String FMT_CSV = "csv";
    private static final String FMT_CSVQUTOTED = "csv-quoted";
    private static final String FMT_TAB = "tab";
    private static final String FMT_TABQUOTED = "tab-quoted";


    public ExcelSheet( Sheet sheet ) {
	this.sheet = sheet;
    }

    /**
     * Get the <code>Sheet</code> value.
     *
     * @return a <code>Sheet</code> value
     */
    public final Sheet getSheet() {
	return sheet;
    }

    /**
     * Set the <code>Sheet</code> value.
     *
     * @param sheet The new Sheet value.
     */
    public final void setSheet(final Sheet sheet) {
	this.sheet = sheet;
    }

    private Properties filter( Properties props ) {
	// log.debug( "Number of keys: "+props.size() );
	for( int i = 0; i < suppressedKeys.length; i++ ) {
	    Object o = props.remove( suppressedKeys[i] );
	    // log.debug( "Key suppressed: "+suppressedKeys[i]+" "+((o==null)?"FALSE":"TRUE") );
	}
	// log.debug( "Number of keys: "+props.size() );	    
	return props;
    }

    /**
     * Get the <code>Properties</code> value.
     *
     * @return a <code>Properties</code> value
     */
    public final Properties getProperties() {
	if( sheet != null ) {
	    return filter(ClassUtils.toProperties( sheet ));
	}
	return new Properties();
    }

    /**
     * Set the <code>Properties</code> value.
     *
     * @param properties The new Properties value.
     */
    // public final void setProperties(final Properties properties) {
    // 	this.properties = properties;
    // }

    /**
     * Returns a human readable name of the sheet.
     *
     * @return the sheet name.
     */
    public String toString() {
	if( sheet != null )
	    return sheet.getSheetName();
	return "";
    }

    /**
     * Returns start row number.
     *
     * @return the index of the start row.
     */
    public int getFirstRowNum() {
	if( sheet != null )
	    return sheet.getFirstRowNum();
	return 0;
    }

    /**
     * Returns last row number.
     *
     * @return the index of the last row.
     */
    public int getLastRowNum() {
	if( sheet != null )
	    return sheet.getLastRowNum();
	return 0;
    }

    /**
     * Returns the maximum column count over all rows.
     *
     * @return the maximum number of columns.
     */
    public int getColumnCount() {
	if( sheet == null )
	    return 0;

	int rowStart = sheet.getFirstRowNum();
	int rowEnd = sheet.getLastRowNum();
	int numCols = 0;
	for( int i = rowStart; i < rowEnd; i++ ) {
	    Row r = sheet.getRow( i );
	    int nCols = 0;
	    if( r != null ) 
		nCols = Math.max( r.getPhysicalNumberOfCells(), r.getLastCellNum() );
	    if( nCols > numCols )
		numCols = nCols;
	}
	return numCols;
    }

// short minColIx = row.getFirstCellNum();
//  short maxColIx = row.getLastCellNum();
//  for(short colIx=minColIx; colIx<maxColIx; colIx++) {
//    Cell cell = row.getCell(colIx);
//    if(cell == null) {
//      continue;
//    }
//    //... do something with cell
//  }

	    // int lastColumn = Math.max(r.getLastCellNum(), MY_MINIMUM_COLUMN_COUNT);
       // for (int cn = 0; cn < lastColumn; cn++) {
       //    Cell c = r.getCell(cn, Row.RETURN_BLANK_AS_NULL);
       //    if (c == null) {
       //       // The spreadsheet is empty in this cell
       //    } else {
       //       // Do something useful with the cell's contents
       //    }
       // }

    /**
     * Returns a list of row numbers of empty rows.
     *
     * @return a list of empty row numbers
     */
    public Integer[] getEmptyRowNums() {
	if( sheet == null )
	    return new Integer[0];
	int rowStart = sheet.getFirstRowNum();
	List<Integer> eRows = new ArrayList<Integer>();
	for( int i = 0; i < rowStart; i++ ) 
	    eRows.add( new Integer(i) );

	int rowEnd = sheet.getLastRowNum();
	for( int i = rowStart; i < rowEnd; i++ ) {
	    Row r = sheet.getRow( i );
	    if( r == null ) {
		eRows.add( new Integer(i) );
		continue;
	    }

	    if( r.getPhysicalNumberOfCells() <= 0 )
		eRows.add( new Integer(i) );
	}
	Integer[] emptyRows = new Integer[ eRows.size() ];
	return (Integer[])eRows.toArray( emptyRows );
    }

    /**
     * Returns a row iterator including empty rows.
     *
     */
    public Iterator getRows() {
	return new ExcelRowIterator( sheet );
    }

    /**
     * Returns a cell length distribution.
     *
     * @return a list of empty row numbers
     */
    public Properties getCellLengths() {
	Properties props = new Properties();
	if( sheet == null )
	    return props;

	int rowStart = sheet.getFirstRowNum();
	int zr = 0;
	Map cLen = new HashMap();
	for( int i = 0; i < rowStart; i++ ) 
	    zr++;
	if( zr > 0 )
	    cLen.put( new Integer(0), new Integer(zr) );

	int rowEnd = sheet.getLastRowNum();
	for( int i = rowStart; i < rowEnd; i++ ) {
	    Row r = sheet.getRow( i );
	    if( r == null ) {
		Integer len = (Integer)cLen.get( new Integer(0) );
		if( len == null )
		    cLen.put( new Integer(0), new Integer(1) );
		else {
		    zr = len.intValue();
		    zr++;
		    cLen.put( new Integer(0), new Integer(zr) );
		}
		continue;
	    }
	    Integer len = new Integer(Math.min( r.getPhysicalNumberOfCells(), Math.abs(r.getLastCellNum()) ));
	    Integer xlen = (Integer)cLen.get( len );
	    if( xlen == null )
		cLen.put( len, new Integer(1) );
	    else {
		zr = xlen.intValue();
		zr++;
		cLen.put( len, new Integer(zr) );
	    }
	}

	// log.debug( "Cell length map: "+cLen );

	Set lenKeys = cLen.keySet();
	if( lenKeys.size() > 0 ) {
	    Integer[] sortKeys = new Integer[ lenKeys.size() ]; 
	    sortKeys = (Integer[])lenKeys.toArray( sortKeys );
	    Arrays.sort( sortKeys );
	    Integer maxLen = sortKeys[ sortKeys.length-1 ];
	    int zeroFill = maxLen.toString().length();
	    for( int i = 0; i < sortKeys.length; i++ ) {
		String pKey = Stringx.zeroPad( sortKeys[i].intValue(), zeroFill );
		props.setProperty( pKey, (cLen.get( sortKeys[i] )).toString() );
	    }
	}

	log.debug( "Cell length properties: "+props );

	return props;
    }

    private String createRegionKey( int count ) {
	return "region."+Stringx.zeroPad( count, 6 );
    }

    private void appendRegion( Map cType, int row, CellType ct ) {
	CellType lastType = (CellType)cType.get( "lastCellType" );
	Integer lastRegion = (Integer)cType.get( "lastRegion" );
	if( lastType == null ) {
	    String rKey = createRegionKey( 0 );
	    cType.put( rKey+".begin", new Integer(row) );
	    cType.put( rKey+".end", new Integer(row) );
	    cType.put( rKey+".type", ct );
	    lastRegion = new Integer(0);
	}
	else if( !lastType.equals( ct ) ) {
	    int r = lastRegion.intValue()+1;
	    String rKey = createRegionKey( r );
	    cType.put( rKey+".begin", new Integer( row ) );
	    cType.put( rKey+".end", new Integer(row) );
	    cType.put( rKey+".type", ct );
	    lastRegion = new Integer( r ); 
	}
	else {
	    String rKey = createRegionKey( lastRegion.intValue() );
	    cType.put( rKey+".end", new Integer(row) );
	}
	cType.put( "lastRegion", lastRegion );
	cType.put( "lastCellType", ct );
    }

    /**
     * Analyzes the cell types of a given column.
     *
     * @param col the column index.
     * @param maxRows limits the number of rows to analyze (0 means all).
     *
     * @return a <code>ColumnRegion</code> object holding the analysis result.
     */
    public ColumnRegion[] analyzeCellTypes( int col, int maxRows ) {
	int rowStart = sheet.getFirstRowNum();
	int zr = 0;
	Map cType = new HashMap();
	for( int i = 0; i < rowStart; i++ ) 
	    appendRegion( cType, i, CellType.BLANK );
	int rowEnd = Math.min(sheet.getLastRowNum(),maxRows);
	for( int i = rowStart; i < rowEnd; i++ ) {
	    Row r = sheet.getRow( i );
	    if( r == null ) 
		appendRegion( cType, i, CellType.BLANK );
	    else {
		Cell cell = r.getCell(col);
		CellType ct = null;
		if(cell == null) 
		    ct = CellType.BLANK;
		else 
		    ct = cell.getCellTypeEnum();
		appendRegion( cType, i, ct );
	    }
	}

	log.debug( "Cell type region map: "+cType );

	Set regKeys = cType.keySet();

	Integer lastRegion = (Integer)cType.get( "lastRegion" );
	if( lastRegion == null )
	    return new ColumnRegion[0];

	List<ColumnRegion> regs = new ArrayList<ColumnRegion>();
	for( int i = 0; i <= lastRegion.intValue(); i++ ) {
	    String rKey = createRegionKey( i );

	    Integer begin = (Integer)cType.get( rKey+".begin" );
	    Integer end = (Integer)cType.get( rKey+".end" );
	    CellType ct = (CellType)cType.get( rKey+".type" );
	    
	    regs.add( new ColumnRegion( col, begin.intValue(), end.intValue(), ct ) );
	}
	ColumnRegion[] colRegs = new ColumnRegion[ regs.size() ];
	return (ColumnRegion[])regs.toArray( colRegs );
	
    }

    /**
     * Analyzes the cell types of a given column.
     *
     * @param col the column index.
     * @param maxRows limits the number of rows to analyze (0 means all).
     *
     * @return a <code>ColumnRegion</code> object holding the analysis result.
     */
    public ColumnRegion[] analyzeCellTypes( int col ) {
	return analyzeCellTypes( col, Integer.MAX_VALUE );
    }

    /**
     * Analyzes the columns to find a header.
     *
     * @return the row number potentially representing the header.
     */
    // public int guessHeaderRow() {
    // 	int nCols = getColumnCount();
    // 	TreeMap<Integer,Integer> rowStarts = new TreeMap<Integer,Integer>();
    // 	int nRows = 0;
    // 	int longestRow = -1;
    // 	for( int i = 0; i < nCols; i++ ) {
    // 	    ColumnRegion[] cRegs = analyzeCellTypes(i);
    // 	    for( int j = 0; j < cRegs.length; j++ ) {
    // 		if( CellType.STRING.equals(cRegs[j].getCellType()) ) {
    // 		    Integer rStart = new Integer(cRegs[j].getStartRow());
    // 		    Integer cRow = rowStarts.get( rStart );
    // 		    if( cRow == null )
    // 			cRow = new Integer(0);
    // 		    int nnRows = cRow.intValue() + 1;
    // 		    if( nnRows > nRows ) {
    // 			longestRow = rStart.intValue();
    // 			nRows = nnRows;
    // 		    }
    // 		    cRow = new Integer( nnRows );
    // 		    rowStarts.put( rStart, cRow );
    // 		}
    // 	    }
    // 	}
    // 	return longestRow;
    // }

    /**
     * Analyzes the columns to find a header.
     *
     * @return an array of <code>String</code> objects potentially representing the header.
     */
    public ColumnHeader guessHeader() {
	int nCols = getColumnCount();
	TreeMap<Integer,Integer> rowStarts = new TreeMap<Integer,Integer>();
	int nRows = 0;
	int longestRow = -1;
	for( int i = 0; i < nCols; i++ ) {
	    ColumnRegion[] cRegs = analyzeCellTypes(i);
	    for( int j = 0; j < cRegs.length; j++ ) {
	 	if( CellType.STRING.equals(cRegs[j].getCellType()) ) {
	 	    Integer rStart = new Integer(cRegs[j].getStartRow());
	 	    Integer cRow = rowStarts.get( rStart );
	 	    if( cRow == null )
	 		cRow = new Integer(0);
	 	    int nnRows = cRow.intValue() + 1;
	 	    if( nnRows > nRows ) {
	 		longestRow = rStart.intValue();
	 		nRows = nnRows;
	 	    }
	 	    cRow = new Integer( nnRows );
	 	    rowStarts.put( rStart, cRow );
	 	}
	     }
	}
	log.debug( "row starts: "+rowStarts+" longest row: "+longestRow+" length: "+nRows );

	ColumnHeader header = new ColumnHeader();
	if( longestRow < 0 )
	    return header;
	header.setStartRow( longestRow );

	Row r = sheet.getRow( longestRow );
	if( r == null ) 
	    return header;

	DataFormatter formatter = new DataFormatter();
	// List<String> hNames = new ArrayList<String>();
	for( int i = 0; i < nRows; i++ ) {
	    Cell cell = r.getCell(i);
	    String text = formatter.formatCellValue(cell);
	    header.addHeader( text );
	}
	return header;
	// String[] header = new String[ hNames.size() ];
	// return (String[])hNames.toArray( header );
    }


    /**
     * Creates a text file output of the content.
     *
     * @param file the file name.
     * @param format the format to be used (csv,csv-quoted,tab etc.)
     * @param startRow the row to start the export
     * @param numRows the number of rows to be exported.
     * @param trans the data transformer to be used (can be null meaning no transform is applied).
     *
     * @return the file name or null (in case of error)
     */
    public File writeTextFile( File file, 
			       String format,
			       int startRow, 
			       int numRows,
			       DataTransformer trans ) {

	if( sheet == null )
	    return null;

	int rowStart = Math.max(sheet.getFirstRowNum(),startRow);
	int rowEnd = Math.min( sheet.getLastRowNum(), numRows );
	int nCols = -1;

	String fmt = Stringx.getDefault( format, FMT_CSV ).trim().toLowerCase();

	DataFormatter formatter = new DataFormatter();
	StringBuilder stb = new StringBuilder();
		    
	String delim = ",";
	String quoted = "";
	if( fmt.indexOf(FMT_CSV) >= 0 )
	    delim = ",";
	if( fmt.indexOf(FMT_TAB) >= 0 )
	    delim = "\t";
	if( fmt.indexOf("quoted") >= 0 )
	    quoted = "\"";

	try {
	    Writer fOut = new FileWriter( file );
	    for( int i = rowStart; i < rowEnd; i++ ) {
		Row r = sheet.getRow( i );
		if( r != null ) {
		    if( nCols < 0 ) 
			nCols = Math.max( r.getPhysicalNumberOfCells(), r.getLastCellNum() );

		    stb.setLength(0);
		    boolean firstCol = true;
		    for( int j = 0; j < nCols; j++ ) {
			Cell cell = r.getCell( j );
			String textVal = null;
			if( cell != null ) {
			    if( trans != null )
				textVal = trans.format( cell, "" );
			    else 
				textVal = formatter.formatCellValue( cell );
			}
			else
			    textVal = "";
			if( firstCol )
			    firstCol = false;
			else
			    stb.append( delim );
			stb.append( quoted );
			stb.append( Stringx.getDefault(textVal,"") );
			stb.append( quoted );
		    }
		    if( !firstCol ) {
			stb.append( "\n" );
			fOut.write( stb.toString() );
		    }
		}
	    }
	    fOut.close();
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	    return null;
	}

	return file;
    }

    /**
     * Creates a text file output of the content.
     *
     * @param file the file name.
     * @param format the format to be used (csv,csv-quoted,tab etc.)
     * @param startRow the row to start the export
     * @param numRows the number of rows to be exported.
     *
     * @return the file name or null (in case of error)
     */
    public File writeTextFile( File file, 
			       String format,
			       int startRow, 
			       int numRows ) {
	return writeTextFile( file, format, startRow, numRows, null );
    } 

}

class ExcelRowIterator implements Iterator {
    private Sheet sheet;
    private int currentRow;
    private int rowStart;
    private int rowEnd;

    ExcelRowIterator( Sheet sheet ) {
	this.sheet = sheet;
	this.currentRow = 0;
	this.rowStart = sheet.getFirstRowNum();
	this.rowEnd = sheet.getLastRowNum();
    }

    /**
     * Returns true if the iteration has more elements. (In other words, returns true 
     * if next() would return an element rather than throwing an exception.)
     *
     * @return true if the iteration has more elements.
     */
    public boolean hasNext() {
	return ((currentRow+1) < rowEnd);
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException if the iteration has no more elements.
     */
    public Object next() {
	if( !hasNext() )
	    throw new NoSuchElementException();
	currentRow++;
	Row r = sheet.getRow( currentRow );
	if( r == null ) 
	    return new Cell[0];

	short minColIx = r.getFirstCellNum();
	short maxColIx = r.getLastCellNum();
	List cells = new ArrayList<Cell>(); 
	for(short colIx=minColIx; colIx<maxColIx; colIx++) {
	    Cell cell = r.getCell(colIx);
	    if(cell == null) 
		cell = r.createCell( colIx );
	    cells.add( cell );
	}
	Cell[] ca = new Cell[cells.size()];
	return (Cell[])cells.toArray( ca );
    }

    /**
     * Removes from the underlying collection the last element returned by this iterator (optional operation). 
     * This method can be called only once per call to next(). The behavior of an iterator is unspecified if 
     * the underlying collection is modified while the iteration is in progress in any way other than by calling this method.
     *
     * @exception UnsupportedOperationException if the remove operation is not supported by this iterator
     * @exception IllegalStateException if the next method has not yet been called, or the remove method has already been called after the last call to the next method
     */
    public void remove() {
	throw new UnsupportedOperationException();
    }
    
}
 
