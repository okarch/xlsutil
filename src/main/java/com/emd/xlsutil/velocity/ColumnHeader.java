package com.emd.xlsutil.velocity;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>ColumnHeader</code> holds information about the column header.
 *
 * Created: Sun Apr  2 12:04:58 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class ColumnHeader {
    private int startRow;
    private List<String> header;

    public ColumnHeader() {
	this.startRow = -1;
	this.header = new ArrayList<String>();
    }

    /**
     * Get the <code>StartRow</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getStartRow() {
	return startRow;
    }

    /**
     * Set the <code>StartRow</code> value.
     *
     * @param startRow The new StartRow value.
     */
    public final void setStartRow(final int startRow) {
	this.startRow = startRow;
    }

    /**
     * Get the <code>ColumnCount</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getColumnCount() {
	return header.size();
    }

    /**
     * Get the <code>Header</code> value.
     *
     * @return a <code>String[]</code> value
     */
    public final String[] getHeader() {
	String[] aHeader = new String[ header.size() ];
	return (String[])header.toArray( aHeader );
    }

    /**
     * Set the <code>Header</code> value.
     *
     * @param header The new Header value.
     */
    // public final void setHeader(final String[] header) {
    // 	this.header = header;
    // }

    /**
     * Set the <code>Header</code> value.
     *
     * @param col The column name to be added.
     */
    public final void addHeader(final String col ) {
	this.header.add( col );
    }

    /**
     * Returns a human readable column header.
     *
     * @return a comma separated list of column names.
     */
    public String toString() {
	StringBuilder stb = new StringBuilder();
	boolean first = true;
	for( String col : header ) {
	    if( first )
		first = false;
	    else
		stb.append( ", " );
	    stb.append( col );
	}
	return stb.toString();
    }

}
