package com.emd.xlsutil.dataset;

import java.util.UUID;

import com.emd.xlsutil.dts.DataTransferColumn;
import com.emd.xlsutil.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>Property</code> defines data columns and other attributes.
 *
 * Created: Mon Jul 17 10:55:20 2017
 *
 * @author <a href="mailto:m01061@tonga.bci.merck.de">Oliver Karch</a>
 * @version 1.0
 */
public class Property extends AbstractTrackable implements Comparable, Copyable {
    private long propertyid;
    private long typeid;
    private long columnid;

    private String propertyname;
    private String label;
    private String typename;
    private String dbformat;
    private String informat;
    private String outformat;

    private int columnsize;
    private int digits;
    private int minoccurs;
    private int maxoccurs;

    private boolean mandatory;

    private static final String ITEM_TYPE = "property";

  
    public Property() {
	super( ITEM_TYPE );
	this.propertyid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.setTrackid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
    }

    public Property( DataTransferColumn dtCol ) {
	super( ITEM_TYPE );
	this.propertyid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.setTrackid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );

	this.columnid = dtCol.getColumnid();

	this.propertyname = dtCol.getColname();
	this.label = dtCol.getLabel();
	this.typename = dtCol.getColtype();
	this.dbformat = dtCol.getDbformat();
	this.informat = dtCol.getFormat();

	this.columnsize = dtCol.getCollength();
	this.digits = dtCol.getColprec();
	this.minoccurs = dtCol.getMinoccur();
	this.maxoccurs = dtCol.getMaxoccur();

	this.mandatory = dtCol.isMandatory();
    }

    /**
     * Get the <code>Propertyid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getPropertyid() {
	return propertyid;
    }

    /**
     * Set the <code>Propertyid</code> value.
     *
     * @param propertyid The new Propertyid value.
     */
    public final void setPropertyid(final long propertyid) {
	this.propertyid = propertyid;
    }

    /**
     * Get the <code>Propertyname</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getPropertyname() {
	return propertyname;
    }

    /**
     * Set the <code>Propertyname</code> value.
     *
     * @param propertyname The new Propertyname value.
     */
    public final void setPropertyname(final String propertyname) {
	this.propertyname = propertyname;
    }

    /**
     * Get the <code>Label</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getLabel() {
	return label;
    }

    /**
     * Set the <code>Label</code> value.
     *
     * @param label The new Label value.
     */
    public final void setLabel(final String label) {
	this.label = label;
    }

    /**
     * Get the <code>Typeid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTypeid() {
	return typeid;
    }

    /**
     * Set the <code>Typeid</code> value.
     *
     * @param typeid The new Typeid value.
     */
    public final void setTypeid(final long typeid) {
	this.typeid = typeid;
    }

    /**
     * Get the <code>Typename</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTypename() {
	return typename;
    }

    /**
     * Set the <code>Typename</code> value.
     *
     * @param typename The new Typename value.
     */
    public final void setTypename(final String typename) {
	this.typename = typename;
    }

    /**
     * Updates the trackid to reflect the current content.
     *
     * @return the updated trackid
     */
    public long updateTrackid() {
	long contId = contentId();
	setTrackid( contId );
	return contId;
    }

    protected long contentId() {
	StringBuilder stb = new StringBuilder();
	stb.append( Stringx.getDefault( getPropertyname(), "" ) );
	stb.append( getTypeid() );
	return DataHasher.hash( stb.toString().getBytes() );
    }

    /**
     * Get the <code>Trackid</code> value.
     *
     * @return a <code>long</code> value
     */

    /**
     * Get the <code>Dbformat</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getDbformat() {
	return dbformat;
    }

    /**
     * Set the <code>Dbformat</code> value.
     *
     * @param dbformat The new Dbformat value.
     */
    public final void setDbformat(final String dbformat) {
	this.dbformat = dbformat;
    }

    /**
     * Get the <code>Informat</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getInformat() {
	return informat;
    }

    /**
     * Set the <code>Informat</code> value.
     *
     * @param informat The new Informat value.
     */
    public final void setInformat(final String informat) {
	this.informat = informat;
    }

    /**
     * Get the <code>Outformat</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getOutformat() {
	return outformat;
    }

    /**
     * Set the <code>Outformat</code> value.
     *
     * @param outformat The new Outformat value.
     */
    public final void setOutformat(final String outformat) {
	this.outformat = outformat;
    }

    /**
     * Get the <code>Columnsize</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getColumnsize() {
	return columnsize;
    }

    /**
     * Set the <code>Columnsize</code> value.
     *
     * @param columnsize The new Columnsize value.
     */
    public final void setColumnsize(final int columnsize) {
	this.columnsize = columnsize;
    }

    /**
     * Get the <code>Digits</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getDigits() {
	return digits;
    }

    /**
     * Set the <code>Digits</code> value.
     *
     * @param digits The new Digits value.
     */
    public final void setDigits(final int digits) {
	this.digits = digits;
    }

    /**
     * Get the <code>Minoccurs</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getMinoccurs() {
	return minoccurs;
    }

    /**
     * Set the <code>Minoccurs</code> value.
     *
     * @param minoccurs The new Minoccurs value.
     */
    public final void setMinoccurs(final int minoccurs) {
	this.minoccurs = minoccurs;
    }

    /**
     * Get the <code>Maxoccurs</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getMaxoccurs() {
	return maxoccurs;
    }

    /**
     * Set the <code>Maxoccurs</code> value.
     *
     * @param maxoccurs The new Maxoccurs value.
     */
    public final void setMaxoccurs(final int maxoccurs) {
	this.maxoccurs = maxoccurs;
    }

    /**
     * Get the <code>Mandatory</code> value.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean isMandatory() {
	return mandatory;
    }

    /**
     * Set the <code>Mandatory</code> value.
     *
     * @param mandatory The new Mandatory value.
     */
    public final void setMandatory(final boolean mandatory) {
	this.mandatory = mandatory;
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
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Property();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	String st =  Stringx.getDefault( Stringx.getDefault( getLabel(), getPropertyname() ), "" );
	return ((st.length() > 0)?st:String.valueOf(getPropertyid()));
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Property ) {
	    Property f = (Property)obj;
	    return (f.getPropertyid() == this.getPropertyid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getPropertyid()).hashCode();
    }

    /**
     * Compares this object with the specified object for order. 
     * Returns a negative integer, zero, or a positive integer as this object is less 
     * than, equal to, or greater than the specified object. 
     *
     */
    public int compareTo( Object o) {
	return getPropertyname().compareTo( ((Property)o).getPropertyname() );
    }

}
