package com.emd.xlsutil.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;

/**
 * <code>TransformerLog</code> records messages about transformations applied.
 *
 * Created: Tue Apr 25 08:10:06 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class TransformerLog {
    private List<LogEntry> entries;

    public static final int INFO  = 0;
    public static final int WARN  = 1;
    public static final int ERROR = 2;

    public TransformerLog() {
	this.entries = new ArrayList<LogEntry>();
    }

    /**
     * Adds a message to the log.
     *
     * @param cell the cell referenced.
     * @param level the log level.
     * @param message the message.
     */
    public LogEntry log( Cell cell, int level, String message ) {
	LogEntry le = new LogEntry();
	le.setRowIndex( cell.getRowIndex() );
	le.setColumnIndex( cell.getColumnIndex() );
	le.setLevel( level );
	le.setMessage( message );
	entries.add( le );
	return le;
    }

    /**
     * Adds a warning message to the log.
     *
     * @param cell the cell referenced.
     * @param message the message.
     */
    public LogEntry warn( Cell cell, String message ) {
	return log( cell, WARN, message );
    }

    /**
     * Adds an error message to the log.
     *
     * @param cell the cell referenced.
     * @param message the message.
     */
    public LogEntry error( Cell cell, String message ) {
	return log( cell, ERROR, message );
    }

    /**
     * Adds an error message to the log.
     *
     * @param cell the cell referenced.
     * @param message the message.
     */
    public LogEntry info( Cell cell, String message ) {
	return log( cell, INFO, message );
    }

    /**
     * Returns the list of entries.
     *
     * @return the list of entries.
     */
    public LogEntry[] getEntries() {
	LogEntry[] lEntries = new LogEntry[ entries.size() ];
	return (LogEntry[])entries.toArray( lEntries );
    }
    
}
