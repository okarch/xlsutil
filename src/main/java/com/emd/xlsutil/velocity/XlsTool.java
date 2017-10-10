package com.emd.xlsutil.velocity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.math.BigDecimal;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.CharSetUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.apache.velocity.tools.generic.SafeConfig;

import com.emd.xlsutil.format.DataTransformer;

import com.emd.util.Stringx;

/**
 * <code>XlsTool</code> is a tool that can be placed in a velocity context to
 * help dealing with Excel files.
 *
 * Created: Tue Oct 25 15:01:33 2016
 *
 * @author <a href="mailto:">Oliver Karch</a>
 * @version 1.0
 */
public class XlsTool extends SafeConfig {

    private static Log log = LogFactory.getLog(XlsTool.class);

    /**
     * Creates the tool
     */
    public XlsTool() {
	super();
    }
    
    /**
     * Opens a workbook.
     *
     * @param x the file or file name.
     * @return the workbook (or null if not successful)
     */
    public Workbook open( Object x ) {
	Workbook wb = null;
	try {
	    if( x instanceof File )
		wb = WorkbookFactory.create( (File)x );
	    else
		wb = WorkbookFactory.create( new File( x.toString() ) );
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	    wb = null;
	}
	catch( InvalidFormatException ife ) {
	    log.error( ife );
	    wb = null;
	}
	catch( EncryptedDocumentException ede ) {
	    log.error( ede );
	    wb = null;
	}
	return wb;
    }

    private File getDestination( Object x ) throws IOException {
	File dir = null;

	if( (x == null) || (x instanceof Workbook) ) {
	    File tempF = File.createTempFile( "excel", ".tmp" );
	    dir = tempF.getParentFile();
	    tempF.delete();
	}

	if( x instanceof File )
	    dir = (File)x;
	else
	    dir = new File( x.toString() );

	if( dir.exists() && dir.isFile() )
	    dir = dir.getParentFile();

	return dir;
    }

    private byte[] readWorkbook( Workbook wb ) {
	byte[] wbBuffer = null;
	try {
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    wb.write( bos );
	    bos.flush();
	    wbBuffer = bos.toByteArray();
	    bos.close();
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	    wbBuffer = null;
	}
	return wbBuffer;
    }

    private String createNamePrefix( Object x, String def ) {
	if( x instanceof Workbook )
	    return def;
	File theF = null;
	if( x instanceof File )
	    theF = (File)x;
	else
	    theF = new File( x.toString() );
	String fn = theF.getName();
	int len = Math.min( fn.length(), 8 );
	return fn.substring( 0, len );
    }

    private String appendSheetPrefix( String fnPrefix, Workbook wb, int sheetNum, String ext ) {
	StringBuilder stb = new StringBuilder( fnPrefix );
	stb.append( "_" );
	stb.append( String.valueOf(sheetNum) );
	if( wb.isSheetVeryHidden( sheetNum ) ) {
	    stb.append( "_vh_" );
	}
	else if( wb.isSheetHidden( sheetNum ) ) {
	    stb.append( "_h_" );
	}
	String shName = wb.getSheetName( sheetNum );
	if( shName != null ) {
	    String fsName = CharSetUtils.keep( shName, new String[] { "a-z", "A-Z", "0-9", "_.-" } );
	    int len = Math.min(fsName.length(), 8 );
	    if( len > 0 ) {
		stb.append( "_" );
		stb.append( fsName.substring( 0, len ) );
	    }
	}
	if( ext != null ) {
	    stb.append( ext );
	}
	else if( wb.getSpreadsheetVersion().equals(SpreadsheetVersion.EXCEL97) ) {
	    stb.append( ".xls" );
	}
	else {
	    stb.append( ".xlsx" );
	}
	return stb.toString();
    }

    private String appendSheetPrefix( String fnPrefix, Workbook wb, int sheetNum ) {
	return appendSheetPrefix( fnPrefix, wb, sheetNum, null );
    }

    private File writeSheet( File destDir, String fnPrefix, int sheetNum, Workbook wb ) {
	File theF = new File( destDir, fnPrefix );
	try {
	    OutputStream outs = new FileOutputStream( theF );
	    wb.write( outs );
	    outs.close();
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	    return null;
	}
	return theF;
    }

    /**
     * Saves a sheet in a separate workbook.
     *
     * @param x the file name, file or workbook to work on.
     * @param dest the destination directory.
     * @return an array of file names.
     */
    public File[] saveSheets( Object x, Object dest ) {
	Workbook wb = null;

	// determine destination folder

	File destDir = null;
	try {
	    if( dest == null )
		destDir = getDestination( x );
	    else
		destDir = getDestination( dest );
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	    return new File[0];
	}

	// open workbook

	if( x instanceof Workbook ) {
	    wb = (Workbook)x;
	}
	else {
	    wb = open( x );
	}
	if( wb == null )
	    return new File[0];

	int numSheets = wb.getNumberOfSheets();

	// create in memory workbook

	byte[] wbBuffer =  readWorkbook( wb );
	if( wbBuffer == null )
	    return new File[0];

	String fnPrefix = createNamePrefix( x, "sheet_" );
	List sheets = new ArrayList<File>();

	// save the sheets by removing the other sheets

	for( int i = numSheets-1; i >= 0; i-- ) {
	    File fnSheet = null;
	    try {
		InputStream bai = new ByteArrayInputStream( wbBuffer ); 
		Workbook wbCopy = WorkbookFactory.create( bai );
		String fnp = appendSheetPrefix( fnPrefix, wbCopy, i );
		log.debug( "Sheet file prefix: "+fnp );
		for( int j = 0; j < i; j++ )
		    wbCopy.removeSheetAt( 0 );
		int ns = wbCopy.getNumberOfSheets();
		for( int j = 1; j < ns; j++ )
		    wbCopy.removeSheetAt( 1 );
		fnSheet = writeSheet( destDir, fnp, i, wbCopy );
		wbCopy.close();
		bai.close();
	    }
	    catch( IOException ioe ) {
		log.error( ioe );
		fnSheet = null;
	    }
	    catch( InvalidFormatException ife ) {
		log.error( ife );
		fnSheet = null;
	    }
	    catch( EncryptedDocumentException ede ) {
		log.error( ede );
		fnSheet = null;
	    }
	    if( fnSheet != null )
		sheets.add( fnSheet );
	}

	File[] aSheets = new File[ sheets.size() ];
	return (File[])sheets.toArray( aSheets ); 
    }

    /**
     * Saves a sheet in a separate workbook.
     *
     * @param x the file name, file or workbook to work on.
     * @param dest the destination directory.
     * @return an array of file names.
     */
    public File[] saveSheetsAsText( Object x, Object dest, Object format ) {
	Workbook wb = null;

	// determine destination folder

	File destDir = null;
	try {
	    if( dest == null )
		destDir = getDestination( x );
	    else
		destDir = getDestination( dest );
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	    return new File[0];
	}

	// open workbook

	if( x instanceof Workbook ) {
	    wb = (Workbook)x;
	}
	else {
	    wb = open( x );
	}
	if( wb == null )
	    return new File[0];

	String fmt = null;
	DataTransformer transformer = null;
	if( format != null ) {
	    if( format instanceof DataTransformer ) {
		transformer = (DataTransformer)format;
		fmt = transformer.getOutputFormat();
	    }
	    else
		fmt = format.toString().toLowerCase();
	}

	if( fmt == null )
	    fmt = "csv";

	String ext = null;
	if( fmt.indexOf( "csv" ) >= 0 )
	    ext = ".csv";
	else
	    ext = ".txt";

	String fnPrefix = createNamePrefix( x, "sheet_" );

	ExcelFileReport xlReport = createExcelFileReport( wb );
	ExcelSheet[] xSheets = xlReport.getSheets( true );
	List sheets = new ArrayList<File>();
	for( int i = 0; i < xSheets.length; i++ ) {
	    ColumnHeader cHead = xSheets[i].guessHeader();
	    int startRow = xSheets[i].getFirstRowNum();
	    int numRows = xSheets[i].getLastRowNum()-startRow;
	    if( cHead != null ) 
		startRow = cHead.getStartRow();

	    String fnp = appendSheetPrefix( fnPrefix, wb, i, ext );
	    File theF = new File( destDir, fnp );

	    File fnSheet = xSheets[i].writeTextFile( theF, fmt, startRow, numRows, transformer );
	    if( fnSheet != null )
		sheets.add( fnSheet );
	}
	File[] aSheets = new File[ sheets.size() ];
	return (File[])sheets.toArray( aSheets ); 
    }

    /**
     * Returns a report which descirbes the Excel file characteristics.
     *
     * @param x the file name, file or workbook to work on.
     * @return an <code>ExcelFileReport</code> object.
     */
    public ExcelFileReport createExcelFileReport( Object x ) {
	ExcelFileReport rep = new ExcelFileReport();
	Workbook wb = null;
	try {
	    if( x instanceof File ) {
		wb = WorkbookFactory.create( (File)x );
		rep.setFileName( x.toString() );
	    }
	    else if( x instanceof Workbook ) {
		rep.setFileName( "Cannot determine from workbook" );
		wb = (Workbook)x;
	    }
	    else {
		wb = WorkbookFactory.create( new File( x.toString() ) );
		rep.setFileName( x.toString() );		
	    }
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	    rep.setError( "I/O error: "+Stringx.getDefault(ioe.getMessage(),"cannot load file") );
	    wb = null;
	}
	catch( InvalidFormatException ife ) {
	    log.error( ife );
	    rep.setError( "Invalid format: cannot load file" );
	    wb = null;
	}
	catch( EncryptedDocumentException ede ) {
	    log.error( ede );
	    rep.setError( "Document seems to be encrypted. Cannot read file" );
	    wb = null;
	}
	rep.setWorkbook( wb );
	return rep;
    }

}
