package com.emd.xlsutil.dataset;

import java.sql.Timestamp;

import java.util.UUID;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

import com.emd.xlsutil.util.DataHasher;

/**
 * <code>Dataset</code> represents a dataset entry.
 *
 * Created: Tue Nov 29 11:13:14 2016
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class Dataset extends AbstractVersionTrackable implements Comparable, Copyable {
    private String datasetid;
    private String study;
    private String setname;
    private String datatype;
    // private String version;

    // private long   stamp;
    private Timestamp created;

    private static final String ITEM_TYPE = "dataset";

    public Dataset() {
	super( ITEM_TYPE, "" );
	this.setname = "";
	this.study = "";
	this.datasetid = UUID.randomUUID().toString();
	this.datatype = "";
	// this.version = "";
	// this.stamp = 0L;
	this.setTrackid( DataHasher.hash( (this.datasetid+"0").getBytes() ) );
	this.created = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Creates a dataset from the given setname.
     *
     * @param setName the set name.
     * @return a <code>Dataset</code> object.
     */
    public static Dataset getInstance( String setName ) {
	Dataset st = new Dataset();
	st.setSetname( setName );
	return st;
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
     * Get the <code>Study</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getStudy() {
	return study;
    }

    /**
     * Set the <code>Study</code> value.
     *
     * @param study The new Study value.
     */
    public final void setStudy(final String study) {
	this.study = study;
    }

    /**
     * Get the <code>Setname</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getSetname() {
	return setname;
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
     * Get the <code>Datatype</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getDatatype() {
	return datatype;
    }

    /**
     * Set the <code>Datatype</code> value.
     *
     * @param datatype The new Datatype value.
     */
    public final void setDatatype(final String datatype) {
	this.datatype = datatype;
    }

    /**
     * Get the <code>Version</code> value.
     *
     * @return a <code>String</code> value
     */
    // public final String getVersion() {
    // 	return version;
    // }

    /**
     * Set the <code>Version</code> value.
     *
     * @param version The new Version value.
     */
    // public final void setVersion(final String version) {
    // 	this.version = version;
    // }

    // public void updateStamp() {
    // 	String ver = Stringx.getDefault(getVersion(),"").trim().toLowerCase();
    // 	long oldStamp = getStamp();
    // 	long nStamp = 0L;
    // 	if( ver.length() > 0 ) {
    // 	    ver = StringUtils.replaceChars( ver, " -;,:_#+*~!&/%", ".............." ); 
    // 	    char[] verCC = ver.toCharArray();
    // 	    boolean numAssigned = false;
    // 	    List verNums = new ArrayList<Integer>();
    // 	    for( int i = 0; i < verCC.length; i++ ) {
    // 		if( verCC[i] == "." ) {
    // 		    verNums.add( new Integer(Stringx.toInt( vNum.toString(), 0 )) );
    // 		    vNum.setLength(0);
    // 		    numAssigned = false;
    // 		}
    // 		else if( Character.isDigit(verCC[i]) ) {
    // 		    vNum.append( verCC[i] );
    // 		}
    // 		else if( Charater.isLetter(verCC[i]) && !numAssigned  ) {
    // 		    if( vNum.length() > 0 ) {
    // 			verNums.add( new Integer(Stringx.toInt( vNum.toString(), 0 )) );
    // 			vNum.setLength(0);
    // 		    }
    // 		    verNums.add( new Integer(Character.getNumericValue(verCC[i])) );
    // 		    numAssigned = true;
    // 		}
    // 	    }
    // 	    int nLen = String.valueOf( oldStamp ).;
    // 	    int exp = verNums.size();
    // 	    if( exp < nLen )
    // 		exp = nLen;
    // 	    for( Integer nVer : verNums ) {
    // 		nStamp = nStamp + (nVer.longValue()*(10L^(long)exp));
    // 		exp--;
    // 	    }
    // 	}
    // 	setStamp( nStamp );
    // }

    /**
     * Get the <code>Stamp</code> value.
     *
     * @return a <code>long</code> value
     */
    // public final long getStamp() {
    // 	return stamp;
    // }

    /**
     * Set the <code>Stamp</code> value.
     *
     * @param stamp The new Stamp value.
     */
    // public final void setStamp(final long stamp) {
    // 	this.stamp = stamp;
    // }

    /**
     * Get the <code>Trackid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTrackid() {
	return DataHasher.hash( (this.datasetid+String.valueOf(super.getStamp())).getBytes() );
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
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Dataset();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	String st =  Stringx.getDefault( getSetname(), "" );
	return ((st.length() > 0)?st:getDatasetid());
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Dataset ) {
	    Dataset f = (Dataset)obj;
	    return (f.getDatasetid().equals( this.getDatasetid() ));
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return this.getDatasetid().hashCode();
    }

    /**
     * Compares this object with the specified object for order. 
     * Returns a negative integer, zero, or a positive integer as this object is less 
     * than, equal to, or greater than the specified object. 
     *
     */
    public int compareTo( Object o) {
	return getSetname().compareTo( ((Dataset)o).getSetname() );
    }
}
