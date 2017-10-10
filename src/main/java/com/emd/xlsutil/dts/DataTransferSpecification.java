package com.emd.xlsutil.dts;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import java.sql.Timestamp;

import org.apache.commons.lang.CharSetUtils;

import com.emd.xlsutil.dataset.AbstractVersionTrackable;
import com.emd.xlsutil.dataset.Property;
import com.emd.xlsutil.dataset.PropertySet;

import com.emd.xlsutil.util.DataHasher;

import com.emd.util.ClassUtils;
import com.emd.util.Stringx;

/**
 * DataTransferSpecification holds information of the data transfer specification.
 *
 * Created: Sat Aug 12 12:39:56 2017
 *
 * @author <a href="mailto:">Oliver</a>
 * @version 1.0
 */
public class DataTransferSpecification extends AbstractVersionTrackable {
    private String study;
    private String datatype;
    private String sender;
    private String receiver;
    private String previous;
    // private String version;
    private String setname;
    private String datasetid;

    private long dtsid;
    private long senderid;
    private long receiverid;
    private long listid;
    private long previousid;

    private Timestamp created;

    private List<DataTransferColumn> columns;
    private List<DataTransferMap> termMaps;

    private final static String DEFAULT_CRO = "Unknown";
    private final static String IGNORE_CHARS = " -/.:,;_#+[]()!\"'*~|<>={}&%$";

    private static final String ITEM_TYPE = "dts";

    public final static String DEFAULT_VERSION = "1.0";


    public DataTransferSpecification() {
	super( ITEM_TYPE, DEFAULT_VERSION );
	this.study = null;
	this.datatype = null;
	this.setname = null;
	this.sender = DEFAULT_CRO;
	this.receiver = DEFAULT_CRO;
	this.previous = "";
	this.dtsid = DataHasher.hash(UUID.randomUUID().toString().getBytes());
	this.created = new Timestamp(System.currentTimeMillis());
	this.columns = new ArrayList<DataTransferColumn>();
	this.termMaps = new ArrayList<DataTransferMap>();
	this.setTrackid( DataHasher.hash((String.valueOf(this.dtsid)+String.valueOf(this.getStamp())).getBytes()) );
    }

    public DataTransferSpecification( DataTransferSpecification dts, String vers ) {
	super( ITEM_TYPE, vers );
	this.study = dts.study;
	this.datatype = dts.datatype;
	this.setname = dts.setname;
	this.sender = dts.sender;
	this.receiver = dts.receiver;
	this.previous = dts.previous;
	this.dtsid = dts.dtsid;
	this.datasetid = dts.datasetid;
	this.created = dts.getCreated();
	this.columns = new ArrayList<DataTransferColumn>( dts.columns );
	this.termMaps = new ArrayList<DataTransferMap>( dts.termMaps );
	this.setTrackid( DataHasher.hash((String.valueOf(this.dtsid)+String.valueOf(this.getStamp())).getBytes()) );
    }


    /**
     * Calculates a content based hash for the given DTS.
     *
     * @param dts the DTS.
     * @return the crc value of the normalized content.
     */ 
    // public static long createDtsid( DataTransferSpecification dts ) {
    // 	return dts.createDtsid();
    // }

    /**
     * Get the Dtsid value.
     * @return the Dtsid value.
     */
    public long getDtsid() {
	// if( dtsid == 0L )
	//     dtsid = createDtsid();
	return dtsid;
    }

    /**
     * Set the Dtsid value.
     * @param newDtsid The new Dtsid value.
     */
    public void setDtsid(long newDtsid) {
	this.dtsid = newDtsid;
    }

    /**
     * Get the <code>Trackid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTrackid() {
	return DataHasher.hash( (String.valueOf(this.dtsid)+String.valueOf(super.getStamp())).getBytes() );
    }

    /**
     * Get the Created value.
     * @return the Created value.
     */
    public Timestamp getCreated() {
	return created;
    }

    /**
     * Set the Created value.
     * @param newCreated The new Created value.
     */
    public void setCreated(Timestamp newCreated) {
	this.created = newCreated;
    }

    /**
     * Get the <code>Senderid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getSenderid() {
	return senderid;
    }

    /**
     * Set the <code>Senderid</code> value.
     *
     * @param senderid The new Senderid value.
     */
    public final void setSenderid(final long senderid) {
	this.senderid = senderid;
    }

    /**
     * Get the <code>Datasetid</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getDatasetid() {
	return datasetid;
    }

    /**
     * Set the <code>Datasetid</code> value.
     *
     * @param datasetid The new Datasetid value.
     */
    public final void setDatasetid(final String datasetid) {
	this.datasetid = datasetid;
    }

    /**
     * Get the <code>Receiverid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getReceiverid() {
	return receiverid;
    }

    /**
     * Set the <code>Receiverid</code> value.
     *
     * @param receiverid The new Receiverid value.
     */
    public final void setReceiverid(final long receiverid) {
	this.receiverid = receiverid;
    }

    /**
     * Get the <code>Listid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getListid() {
	return listid;
    }

    /**
     * Set the <code>Listid</code> value.
     *
     * @param listid The new Listid value.
     */
    public final void setListid(final long listid) {
	this.listid = listid;
    }

    /**
     * Get the <code>Previousid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getPreviousid() {
	return previousid;
    }

    /**
     * Set the <code>Previousid</code> value.
     *
     * @param previousid The new Previousid value.
     */
    public final void setPreviousid(final long previousid) {
	this.previousid = previousid;
    }

    /**
     * Checks the mandatory fileds to be present.
     *
     * @return true if it is valid
     */
    public boolean isValid() {
	return ((getStudy().length() > 0) && (getDatatype().length() > 0) && (columns.size() > 0) );
    }

    /**
     * Get the Study value.
     * @return the Study value.
     */
    public String getStudy() {
	return Stringx.getDefault(study,"").trim();
    }

    /**
     * Set the Study value.
     * @param newStudy The new Study value.
     */
    public void setStudy(String newStudy) {
	this.study = newStudy;
    }

    /**
     * Get the Datatype value.
     * @return the Datatype value.
     */
    public String getDatatype() {
	return Stringx.getDefault(datatype,"").trim();
    }

    /**
     * Set the Datatype value.
     * @param newDatatype The new Datatype value.
     */
    public void setDatatype(String newDatatype) {
	this.datatype = newDatatype;
    }

    /**
     * Get the <code>Setname</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getSetname() {
	return Stringx.getDefault(setname,"").trim();
    }

    /**
     * Set the <code>Setname</code> value.
     *
     * @param setname The new Setname value.
     */
    public final void setSetname(final String setname) {
	this.setname = setname;
    }

    /**
     * Get the Sender value.
     * @return the Sender value.
     */
    public String getSender() {
	return Stringx.getDefault(sender,"").trim();
    }

    /**
     * Set the Sender value.
     * @param newSender The new Sender value.
     */
    public void setSender(String newSender) {
	this.sender = newSender;
    }

    /**
     * Get the Receiver value.
     * @return the Receiver value.
     */
    public String getReceiver() {
	return Stringx.getDefault(receiver,"").trim();
    }

    /**
     * Set the Receiver value.
     * @param newReceiver The new Receiver value.
     */
    public void setReceiver(String newReceiver) {
	this.receiver = newReceiver;
    }

    /**
     * Get the Version value.
     * @return the Version value.
     */
    // public String getVersion() {
    // 	return version;
    // }

    /**
     * Set the Version value.
     * @param newVersion The new Version value.
     */
    // public void setVersion(String newVersion) {
    // 	this.version = newVersion;
    // }

    /**
     * Get the Previous value.
     * @return the Previous value.
     */
    public String getPrevious() {
	return previous;
    }

    /**
     * Set the Previous value.
     * @param newPrevious The new Previous value.
     */
    public void setPrevious(String newPrevious) {
	this.previous = newPrevious;
    }

    /**
     * Creates a new data column by parsing the given line content.
     * @param line The column specification line.
     * @return the DataTransferColumn object created.
     */
    public DataTransferColumn addColumnFromLine(String line) throws DataTransferException {
	DataTransferColumn col = DataTransferColumn.parse( line );
	columns.add( col );
	return col;
    }

    /**
     * Adds a set of <code>DataTransferColumn</code>s from a <code>PropertySet</code>.
     * @param pSet The set of properties.
     */
    public void addPropertySet( PropertySet pSet ) {
	Property[] props = pSet.getProperties();
	for( int i = 0; i < props.length; i++ ) {
	    DataTransferColumn dtCol = new DataTransferColumn(props[i]);
	    dtCol.setRank( i+1 );
	    addColumn( dtCol );
	}
    }

    /**
     * Adds a new data column to the DTS.
     * @param col The column specification.
     * @return the DataTransferColumn object created.
     */
    public void addColumn( DataTransferColumn col ) {
	columns.add( col );
    }

    /**
     * Returns all columns of the specification.
     *
     */
    public DataTransferColumn[] getColumns() {
	DataTransferColumn[] cols = new DataTransferColumn[columns.size()];
	return (DataTransferColumn[])columns.toArray( cols );
    }

    /**
     * Returns the number of columns.
     *
     */
    public int getColumnCount() {
	return columns.size();
    }

    /**
     * Creates a new term map.
     * @param mapName the term map to be added.
     * @return the DataTransferMap object created.
     */
    public DataTransferMap addTermMap( String mapName ) {
	DataTransferMap map = new DataTransferMap( mapName );
	termMaps.add( map );
	return map;
    }

    /**
     * Creates a new term map.
     * @param map the term map to be added.
     * @return the DataTransferMap object created.
     */
    public DataTransferMap addTermMap( DataTransferMap map ) {
	termMaps.add( map );
	return map;
    }

    /**
     * Returns all term maps of the specification.
     *
     */
    public DataTransferMap[] getTermMaps() {
	DataTransferMap[] maps = new DataTransferMap[termMaps.size()];
	return (DataTransferMap[])termMaps.toArray( maps );
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getDtsid()).hashCode();
    }

    /**
     * Returns a human readable representation of the DTS.
     *
     * @return a human readable string.
     */
    public String toString() {
	StringBuilder stb = new StringBuilder( "[" );
	stb.append( getDtsid() );
	stb.append( "] " );
	stb.append( getStudy() );
	stb.append( " - " );
	stb.append( getDatatype() );
	stb.append( " " );
	stb.append( String.valueOf(columns.size()) );
	stb.append( " columns  " );
	stb.append( String.valueOf(termMaps.size()) );
	stb.append( " term mappings" );
	return stb.toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof DataTransferSpecification ) {
	    DataTransferSpecification f = (DataTransferSpecification)obj;
	    return (f.getDtsid() == this.getDtsid() );
	}
	return false;
    }

    /**
     * Calculates the column hash value used to determine
     * whether column specs have changed.
     *
     * @return a hash value of all columns in order.
     */
    public long getColumnHash() {
	DataTransferColumn[] cols = getColumns();
	StringBuilder stb = new StringBuilder();
	for( int i = 0; i < cols.length; i++ ) {
	    stb.append( String.valueOf(i) );
	    Properties props = ClassUtils.toProperties( cols[i] );
	    for( int j = 0; j < DataTransferColumn.FIELDS.length; j++ ) {
		String val = props.getProperty( DataTransferColumn.FIELDS[j],"_-_" );
		stb.append( "," );
		stb.append( val );
	    }
	}
	return DataHasher.hash( stb.toString().getBytes() );
    }

    /**
     * Calculates a content based hash.
     *
     * @return the crc value of the normalized content.
     */ 
    public long getDtsHash() {
     	StringBuilder stb = new StringBuilder();
     	stb.append( CharSetUtils.delete(getStudy().toLowerCase(),IGNORE_CHARS) );
     	stb.append( CharSetUtils.delete(getSetname().toLowerCase(),IGNORE_CHARS) );
     	stb.append( CharSetUtils.delete(getDatatype().toLowerCase(),IGNORE_CHARS) );
     	stb.append( CharSetUtils.delete(getSender().toLowerCase(),IGNORE_CHARS) );
     	stb.append( CharSetUtils.delete(getReceiver().toLowerCase(),IGNORE_CHARS) );
     	stb.append( CharSetUtils.delete(getVersion().toLowerCase(),IGNORE_CHARS) );
     	return DataHasher.hash( stb.toString().getBytes() );
    }

    /**
     * Checks if the given DTS differs from this DTS.
     * The check includes inspection of the column structure.
     *
     * @param dts the DTS to compare to.
     * @return true if this DTS differs.
     */
    public boolean changed( DataTransferSpecification dts ) {
	return ( (this.getDtsHash() != dts.getDtsHash()) || 
		 (this.getColumnHash() != dts.getColumnHash()) );
    }

}
