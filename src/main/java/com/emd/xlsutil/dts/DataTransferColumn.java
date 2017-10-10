package com.emd.xlsutil.dts;

import com.emd.xlsutil.dataset.Property;

import com.emd.util.ClassUtils;
import com.emd.util.Stringx;

/**
 * <code>DataTransferColumn</code> represents a column variable of a DTS.
 *
 * Created: Sat Aug 12 19:23:10 2017
 *
 * @author <a href="mailto:">Oliver</a>
 * @version 1.0
 */
public class DataTransferColumn {
    private long columnid;
    // private long   dtsid;

    private String colname;
    private String label;
    private String param;
    private String format;
    private String dbformat;
    private String coltype;
    private String description;

    private int    rank;
    private int    collength;
    private int    colprec;
    private int    minoccur;
    private int    maxoccur;

    private boolean mandatory;

    private static final String   DELIMITER = "|";
    public static final String[] FIELDS = {
	"colname",
	"label",
	"param",
	"format",
	"dbformat",
	"coltype",
	"collength",
	"colprec",
	"minoccur",
	"maxoccur",
        "mandatory",
	"description"
    };


    public DataTransferColumn() {
    }

    public DataTransferColumn( Property prop ) {
	this.columnid = prop.getPropertyid();
	this.colname = prop.getPropertyname();

	this.label = prop.getLabel();
	this.coltype = prop.getTypename();

	this.dbformat = prop.getDbformat();
	this.format = prop.getInformat();

	this.collength = prop.getColumnsize();
	this.colprec = prop.getDigits();
	this.minoccur = prop.getMinoccurs();
	this.maxoccur = prop.getMaxoccurs();

	this.mandatory = prop.isMandatory();
    }

    /**
     * Parses a column specification from a string.
     *
     * @param line the line to be parsed.
     * @return a new column instance.
     */
    public static DataTransferColumn parse( String line ) 
	throws DataTransferException {

	DataTransferColumn dtc = new DataTransferColumn();
	int k = 0;
	int l = 0;
	int f = 0;
	while( (f < FIELDS.length) && 
	       (k < line.length()) && 
	       ((k = line.indexOf( DELIMITER, k )) >= l) ) {
	    String val = "";
	    if( (k-l) > 0 )
		val = line.substring( l, k );
	    if( (FIELDS[f].equals( "format" )) && (!val.endsWith("/")) ) {
	    }
	    try {
		ClassUtils.set( dtc, FIELDS[f], val );
	    }
	    catch( NumberFormatException nfe ) {
		String msg = "Parse exception at line "+Stringx.strtrunc( line, 20, "..." )+" - "+nfe;
		throw new DataTransferException( msg );
	    }
	    l = k + DELIMITER.length();
	    k = l;
	    f++;
	}
	if( (f < FIELDS.length) && (l > 0) ) {
	    try {
		ClassUtils.set( dtc, FIELDS[f], line.substring(l) );
	    }
	    catch( NumberFormatException nfe ) {
		String msg = "Parse exception at line "+Stringx.strtrunc( line, 20, "..." )+" - "+nfe;
		throw new DataTransferException( msg );
	    }

	}	    
	return dtc;
    }

    /**
     * Get the <code>Columnid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getColumnid() {
	return columnid;
    }

    /**
     * Set the <code>Columnid</code> value.
     *
     * @param columnid The new Columnid value.
     */
    public final void setColumnid(final long columnid) {
	this.columnid = columnid;
    }

    /**
     * Get the Dtsid value.
     * @return the Dtsid value.
     */
    // public long getDtsid() {
    // 	return dtsid;
    // }

    /**
     * Set the Dtsid value.
     * @param newDtsid The new Dtsid value.
     */
    // public void setDtsid(long newDtsid) {
    // 	this.dtsid = newDtsid;
    // }

    /**
     * Get the Colname value.
     * @return the Colname value.
     */
    public String getColname() {
	return Stringx.getDefault(colname,"").trim();
    }

    /**
     * Set the Colname value.
     * @param newColname The new Colname value.
     */
    public void setColname(String newColname) {
	this.colname = newColname;
    }

    /**
     * Get the Label value.
     * @return the Label value.
     */
    public String getLabel() {
	return Stringx.getDefault(label,"").trim();
    }

    /**
     * Set the Label value.
     * @param newLabel The new Label value.
     */
    public void setLabel(String newLabel) {
	this.label = newLabel;
    }

    /**
     * Get the Param value.
     * @return the Param value.
     */
    public String getParam() {
	return Stringx.getDefault(param,"").trim();
    }

    /**
     * Set the Param value.
     * @param newParam The new Param value.
     */
    public void setParam(String newParam) {
	this.param = newParam;
    }

    /**
     * Get the Format value.
     * @return the Format value.
     */
    public String getFormat() {
	return Stringx.getDefault(format,"").trim();
    }

    /**
     * Set the Format value.
     * @param newFormat The new Format value.
     */
    public void setFormat(String newFormat) {
	this.format = newFormat;
    }

    /**
     * Get the Dbformat value.
     * @return the Dbformat value.
     */
    public String getDbformat() {
	return Stringx.getDefault(dbformat,"").trim();
    }

    /**
     * Set the Dbformat value.
     * @param newDbformat The new Dbformat value.
     */
    public void setDbformat(String newDbformat) {
	this.dbformat = newDbformat;
    }

    /**
     * Get the Coltype value.
     * @return the Coltype value.
     */
    public String getColtype() {
	return Stringx.getDefault(coltype,"").trim();
    }

    /**
     * Set the Coltype value.
     * @param newColtype The new Coltype value.
     */
    public void setColtype(String newColtype) {
	this.coltype = newColtype;
    }

    /**
     * Get the Description value.
     * @return the Description value.
     */
    public String getDescription() {
	return Stringx.getDefault(description,"").trim();
    }

    /**
     * Set the Description value.
     * @param newDescription The new Description value.
     */
    public void setDescription(String newDescription) {
	this.description = newDescription;
    }

    /**
     * Get the Rank value.
     * @return the Rank value.
     */
    public int getRank() {
	return rank;
    }

    /**
     * Set the Rank value.
     * @param newRank The new Rank value.
     */
    public void setRank(int newRank) {
	this.rank = newRank;
    }

    /**
     * Get the Collength value.
     * @return the Collength value.
     */
    public int getCollength() {
	return collength;
    }

    /**
     * Set the Collength value.
     * @param newCollength The new Collength value.
     */
    public void setCollength(int newCollength) {
	this.collength = newCollength;
    }

    /**
     * Get the Colprec value.
     * @return the Colprec value.
     */
    public int getColprec() {
	return colprec;
    }

    /**
     * Set the Colprec value.
     * @param newColprec The new Colprec value.
     */
    public void setColprec(int newColprec) {
	this.colprec = newColprec;
    }

    /**
     * Get the Minoccur value.
     * @return the Minoccur value.
     */
    public int getMinoccur() {
	return minoccur;
    }

    /**
     * Set the Minoccur value.
     * @param newMinoccur The new Minoccur value.
     */
    public void setMinoccur(int newMinoccur) {
	this.minoccur = newMinoccur;
    }

    /**
     * Get the Maxoccur value.
     * @return the Maxoccur value.
     */
    public int getMaxoccur() {
	return maxoccur;
    }

    /**
     * Set the Maxoccur value.
     * @param newMaxoccur The new Maxoccur value.
     */
    public void setMaxoccur(int newMaxoccur) {
	this.maxoccur = newMaxoccur;
    }

    /**
     * Get the Mandatory value.
     * @return the Mandatory value.
     */
    public boolean isMandatory() {
	return mandatory;
    }

    /**
     * Set the Mandatory value.
     * @param newMandatory The new Mandatory value.
     */
    public void setMandatory(boolean newMandatory) {
	this.mandatory = newMandatory;
    }

}
