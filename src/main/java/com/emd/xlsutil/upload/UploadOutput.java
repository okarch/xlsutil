package com.emd.xlsutil.upload;

import java.math.BigInteger;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.sql.Timestamp;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.DataFormatException;

import org.apache.commons.io.IOUtils;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.xlsutil.util.DataHasher;

import com.emd.io.WriterOutputStream;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>UploadOutput</code> captures the output from applying the template.
 *
 * Created: Mon Jan  9 09:02:49 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class UploadOutput implements Copyable {
    private long outputid;
    private long uploadid;

    private String md5sum;
    private String mime;
    private String filename;

    private Timestamp created;

    private UploadContentHandler uploadContentHandler;

    private static Log log = LogFactory.getLog(UploadOutput.class);


    public UploadOutput() {
	this.outputid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.created = new Timestamp( System.currentTimeMillis() );
    }

    /**
     * Get the <code>Outputid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getOutputid() {
	return outputid;
    }

    /**
     * Set the <code>Outputid</code> value.
     *
     * @param outputid The new Outputid value.
     */
    public final void setOutputid(final long outputid) {
	this.outputid = outputid;
    }

    /**
     * Get the <code>Uploadid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getUploadid() {
	return uploadid;
    }

    /**
     * Set the <code>Uploadid</code> value.
     *
     * @param uploadid The new Uploadid value.
     */
    public final void setUploadid(final long uploadid) {
	this.uploadid = uploadid;
    }

    /**
     * Get the <code>Created</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getCreated() {
	return created;
    }

    /**
     * Set the <code>Created</code> value.
     *
     * @param created The new Created value.
     */
    public final void setCreated(final Timestamp created) {
	this.created = created;
    }

    /**
     * Get the <code>Valid</code> value.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean isValid() {
	return (created != null);
    }

    /**
     * Get the <code>Md5sum</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getMd5sum() {
	return this.md5sum;
    }

    /**
     * Set the <code>Md5sum</code> value.
     *
     * @param md5sum The new Md5sum value.
     */
    public final void setMd5sum(final String md5sum) {
     	this.md5sum = md5sum;
    }

    /**
     * Get the <code>Mime</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getMime() {
	return mime;
    }

    /**
     * Set the <code>Mime</code> value.
     *
     * @param mime The new Mime value.
     */
    public final void setMime(final String mime) {
	this.mime = mime;
    }

    /**
     * Get the <code>Filename</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getFilename() {
	return filename;
    }

    /**
     * Set the <code>Filename</code> value.
     *
     * @param filename The new Filename value.
     */
    public final void setFilename(final String filename) {
	this.filename = filename;
    }

    /**
     * Get the <code>UploadContentHandler</code> value.
     *
     * @return an <code>UploadContentHandler</code> value
     */
    public final UploadContentHandler getUploadContentHandler() {
	return uploadContentHandler;
    }

    /**
     * Set the <code>UploadContentHandler</code> value.
     *
     * @param uploadContentHandler The new UploadContentHandler value.
     */
    public final void setUploadContentHandler(final UploadContentHandler uploadContentHandler) {
	this.uploadContentHandler = uploadContentHandler;
    }

    /**
     * Decodes the content of upload and creates a file at the given destination.
     * If destination is a directory the filename provided by the batch upload will be used
     * otherwise content will be written to dest.
     *
     * @param the destination to write the content.
     */
    public File writeOutputFile( File dest ) 
	throws IOException {

	File destF = null;
	String fName = Stringx.getDefault(getFilename(),"");
	if( fName.length() <= 0 )
	    fName = UUID.randomUUID().toString()+".out";

	if( dest.isDirectory() )
	    destF = new File( dest, fName );
	else
	    destF = dest;

	if( uploadContentHandler == null )
	    throw new IOException( "Cannot upload content" );

	FileOutputStream fos = new FileOutputStream( destF );
	uploadContentHandler.writeUploadContentTo( getMd5sum(), getMime(), fos );
	fos.close();

	// byte[] buf = null;
	// try {
	//     buf = ZipCoder.decode( Stringx.getDefault(getUpload(),"") );
	// }
	// catch( DataFormatException de ) {
	//     throw new IOException( de );
	// }
	// FileOutputStream fos = new FileOutputStream( destF );
	// fos.write( buf );
	// fos.close();
	// buf = null;
	return destF;
    } 

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new UploadOutput();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	StringBuilder stb = new StringBuilder();
	String fn = Stringx.getDefault( getFilename(), "" ).trim();
	if( fn.length() <= 0 )
	    fn = Stringx.getDefault(getMd5sum(),String.valueOf(getOutputid()));
	stb.append( fn );
	fn = Stringx.getDefault( getMime(), "" ).trim();
	if( fn.length() > 0 ) {
	    stb.append( " (" );
	    stb.append( fn );
	    stb.append( ")" );
	}
	return stb.toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof UploadOutput ) {
	    UploadOutput f = (UploadOutput)obj;
	    return (f.getOutputid() == this.getOutputid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(getOutputid()).hashCode();
    }

}
