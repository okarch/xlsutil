package com.emd.xlsutil.velocity;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.emd.util.Stringx;

/**
 * <code>ExcelFileReport</code> holds a description of an Excel file format and content.
 *
 * Created: Mon Nov 14 18:19:15 2016
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class ExcelFileReport {
    private String fileName;
    private String error;
    private String version;

    private Workbook workbook;

    public ExcelFileReport() {
	this.fileName = "";
	this.error = "";
	this.workbook = null;
    }

    /**
     * Get the <code>FileName</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getFileName() {
	return fileName;
    }

    /**
     * Set the <code>FileName</code> value.
     *
     * @param fileName The new FileName value.
     */
    public final void setFileName(final String fileName) {
	this.fileName = fileName;
    }

    /**
     * Get the <code>Error</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getError() {
	return error;
    }

    /**
     * Set the <code>Error</code> value.
     *
     * @param error The new Error value.
     */
    public final void setError(final String error) {
	this.error = error;
    }

    /**
     * Get the <code>Workbook</code> value.
     *
     * @return a <code>Workbook</code> value
     */
    public final Workbook getWorkbook() {
	return workbook;
    }

    /**
     * Set the <code>Workbook</code> value.
     *
     * @param workbook The new Workbook value.
     */
    public final void setWorkbook(final Workbook workbook) {
	this.workbook = workbook;
    }

    /**
     * Get the <code>Version</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getVersion() {
	if( workbook != null ) 
	    return workbook.getSpreadsheetVersion().toString();
	return "unknown";
    }

    /**
     * Get the names of the sheets.
     *
     * @return a <code>String</code> value
     */
    public final ExcelSheet[] getSheets( boolean includeHidden ) {
	List<ExcelSheet> sNames = new ArrayList<ExcelSheet>();
	if( workbook != null ) {
	    int numS = workbook.getNumberOfSheets();
	    for( int i = 0; i < numS; i++ ) {
		if( (workbook.isSheetHidden(i) || workbook.isSheetVeryHidden(i)) && !includeHidden )
		    continue;
		Sheet sheet = workbook.getSheetAt( i );
		sNames.add( new ExcelSheet( sheet ) );
	    }
	}
	ExcelSheet[] sheets = new ExcelSheet[ sNames.size() ];
	return (ExcelSheet[])sNames.toArray( sheets );
    }

}
