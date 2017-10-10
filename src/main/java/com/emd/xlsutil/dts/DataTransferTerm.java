package com.emd.xlsutil.dts;

import com.emd.util.ClassUtils;
import com.emd.util.Stringx;

/**
 * <code>DataTransferTerm</code> holds a term mapping.
 *
 * Created: Sat Aug 12 13:22:47 2017
 *
 * @author <a href="mailto:">Oliver</a>
 * @version 1.0
 */
public class DataTransferTerm {
    private long   mapid;
    private long   termid;

    private String term;
    private String termdesc;

    private static final String   DELIMITER = "|";
    private static final String[] FIELDS = {
	"term",
	"termdesc"
    };

    public DataTransferTerm() {
    }

    /**
     * Parses a column specification from a string.
     *
     * @param line the line to be parsed.
     * @return a new column instance.
     */
    public static DataTransferTerm parse( String line ) {
	DataTransferTerm dtc = new DataTransferTerm();
	int k = 0;
	int l = 0;
	int f = 0;
	while( (f < FIELDS.length) && 
	       (k < line.length()) && 
	       ((k = line.indexOf( DELIMITER, k )) >= l) ) {
	    String val = "";
	    if( (k-l) > 0 )
		val = line.substring( l, k );
	    ClassUtils.set( dtc, FIELDS[f], val );
	    l = k + DELIMITER.length();
	    k = l;
	    f++;
	}
	if( (f < FIELDS.length) && (l > 0) )
	    ClassUtils.set( dtc, FIELDS[f], line.substring(l) );
	    
	return dtc;
    }

    /**
     * Get the Mapid value.
     * @return the Mapid value.
     */
    public long getMapid() {
	return mapid;
    }

    /**
     * Set the Mapid value.
     * @param newMapid The new Mapid value.
     */
    public void setMapid(long newMapid) {
	this.mapid = newMapid;
    }

    /**
     * Get the Termid value.
     * @return the Termid value.
     */
    public long getTermid() {
	return termid;
    }

    /**
     * Set the Termid value.
     * @param newTermid The new Termid value.
     */
    public void setTermid(long newTermid) {
	this.termid = newTermid;
    }

    /**
     * Get the Term value.
     * @return the Term value.
     */
    public String getTerm() {
	return Stringx.getDefault(term,"").trim();
    }

    /**
     * Set the Term value.
     * @param newTerm The new Term value.
     */
    public void setTerm(String newTerm) {
	this.term = newTerm;
    }

    /**
     * Get the Termdesc value.
     * @return the Termdesc value.
     */
    public String getTermdesc() {
	return Stringx.getDefault(termdesc,"").trim();
    }

    /**
     * Set the Termdesc value.
     * @param newTermdesc The new Termdesc value.
     */
    public void setTermdesc(String newTermdesc) {
	this.termdesc = newTermdesc;
    }

}
