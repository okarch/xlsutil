package com.emd.xlsutil.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.emd.xlsutil.dataset.AbstractTrackable;
import com.emd.xlsutil.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>UploadTemplate</code> specifies an upload template.
 *
 * Created: Sun Feb  8 16:21:26 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class UploadTemplate extends AbstractTrackable implements Copyable {
    private String template;
    private String templatename;
    private long templateid;
    private List<UploadBatch> uploads; 

    private static final String ITEM_TYPE = "template";

    public static final String  FILESET_CREATE = "Create fileset";

    public UploadTemplate() {
	super( ITEM_TYPE );
	this.templatename = "new template";
	this.templateid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.template="";
	this.uploads = new ArrayList<UploadBatch>();
    }

    /**
     * Get the <code>Templateid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTemplateid() {
	return templateid;
    }

    /**
     * Set the <code>Templateid</code> value.
     *
     * @param templateid The new Templateid value.
     */
    public final void setTemplateid(final long templateid) {
	this.templateid = templateid;
    }

    /**
     * Get the <code>Template</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTemplate() {
	return template;
    }

    /**
     * Set the <code>Template</code> value.
     *
     * @param template The new Template value.
     */
    public final void setTemplate(final String template) {
	this.template = template;
    }

    /**
     * Get the <code>Templatename</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTemplatename() {
	return templatename;
    }

    /**
     * Set the <code>Templatename</code> value.
     *
     * @param templatename The new Templatename value.
     */
    public final void setTemplatename(final String templatename) {
	this.templatename = templatename;
    }

    /**
     * Adds an upload batch entry.
     *
     * @param upload An upload batch entry.
     */
    public void addUploadBatch( UploadBatch upload ) {
	uploads.add( upload );
    }

    /**
     * Returns the upload batch entry.
     *
     * @param uploadId The upload id to be retrieved.
     * @return the upload entry or null (if not found).
     */
    public UploadBatch getUploadBatch( long uploadId ) {
	for( UploadBatch upd : uploads ) {
	    if( upd.getUploadid() == uploadId )
		return upd;
	}
	return null;
    }

    /**
     * Sets the upload batches.
     *
     * @param batches The array of upload batches.
     */
    public void setUploadBatches( UploadBatch[] batches ) {

    }

    /**
     * Returns the list of upload batches.
     *
     * @return array of upload batches.
     */
    public UploadBatch[] getUploadBatches() {
     	UploadBatch[] facs = new UploadBatch[ uploads.size() ];
     	return (UploadBatch[])uploads.toArray( facs );
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

    // private void initMd5( String cont ) {
    // 	try {
    // 	    MessageDigest md = MessageDigest.getInstance("MD5");
    // 	    byte[] md5sum = md.digest(Stringx.getDefault(cont,"").trim().getBytes());
    // 	    this.setMd5sum( String.format("%032X", new BigInteger(1, md5sum)) );
    // 	}
    // 	catch( NoSuchAlgorithmException nae ) {
    // 	    // do nothing
    // 	}
    // }

    protected long contentId() {
	StringBuilder stb = new StringBuilder();
	stb.append( Stringx.getDefault( getTemplatename(), "" ) );
	stb.append( Stringx.getDefault( getTemplate(), "" ) );
	return DataHasher.hash( stb.toString().getBytes() );
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new UploadTemplate();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( getTemplatename(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof UploadTemplate ) {
	    UploadTemplate f = (UploadTemplate)obj;
	    return (f.getTemplateid() == this.getTemplateid());
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return this.getTemplatename().hashCode();
    }

}
