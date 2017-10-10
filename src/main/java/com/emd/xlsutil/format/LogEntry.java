package com.emd.xlsutil.format;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.StringUtils;


/**
 * <code>LogEntry</code> holds a single entry from the transformer log.
 *
 * Created: Tue May  2 07:56:25 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class LogEntry {
    private long created;

    private int rowIndex;
    private int columnIndex;
    private int level;

    private String message;

    private static final String[] LEVEL = new String[] {
	"INFO",
	"WARN",
	"ERROR"
    };

    public LogEntry() {
	this.created = System.currentTimeMillis();
	this.rowIndex = -1;
	this.columnIndex = -1;
	this.level = 0;
    }

    /**
     * Get the <code>Created</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getCreated() {
	return created;
    }

    /**
     * Get the <code>RowIndex</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getRowIndex() {
	return rowIndex;
    }

    /**
     * Set the <code>RowIndex</code> value.
     *
     * @param rowIndex The new RowIndex value.
     */
    public final void setRowIndex(final int rowIndex) {
	this.rowIndex = rowIndex;
    }

    /**
     * Get the <code>ColumnIndex</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getColumnIndex() {
	return columnIndex;
    }

    /**
     * Set the <code>ColumnIndex</code> value.
     *
     * @param columnIndex The new ColumnIndex value.
     */
    public final void setColumnIndex(final int columnIndex) {
	this.columnIndex = columnIndex;
    }

    /**
     * Get the <code>Level</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getLevel() {
	return level;
    }

    /**
     * Set the <code>Level</code> value.
     *
     * @param level The new Level value.
     */
    public final void setLevel(final int level) {
	this.level = level;
    }

    /**
     * Get the <code>Message</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getMessage() {
	return message;
    }

    /**
     * Set the <code>Message</code> value.
     *
     * @param message The new Message value.
     */
    public final void setMessage(final String message) {
	this.message = message;
    }

    /**
     * Returns a human readable log message.
     *
     * @return a human readable message.
     */
    public String toString() {
	StringBuilder stb = new StringBuilder();
	stb.append( DateFormatUtils.format( created, "dd-MMM-yyyy hh:mm:ss" ).toUpperCase() );
	stb.append( " " );
	stb.append( LEVEL[level] );
	stb.append( " - " );
	if( (rowIndex >= 0) || (columnIndex >= 0) ) {
	    stb.append( "Cell [" );
	    stb.append( rowIndex );
	    stb.append( "," );
	    stb.append( columnIndex );
	    stb.append( "]: " );
	}
	stb.append( StringUtils.defaultString(message) );
	return stb.toString();
    }

}
