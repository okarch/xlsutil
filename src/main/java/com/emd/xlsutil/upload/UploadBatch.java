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
import com.emd.util.ZipCoder;

/**
 * <code>UploadBatch</code> describes the content of an upload.
 *
 * Created: Mon Feb  9 20:02:49 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class UploadBatch implements Copyable {
    private long uploadid;
    private long templateid;
    private long userid;
    private long filesetid;

    // private String upload;
    private String md5sum;
    private String mime;
    private String filename;

    private int nummsg;

    private Timestamp uploaded;
    private Timestamp logstamp;

    private UploadContentHandler uploadContentHandler;

    private List<String> uploadHeader;

    private static Log log = LogFactory.getLog(UploadBatch.class);

    public static final String FILESET_MIME = "inode/directory";

    public UploadBatch() {
	this.uploadid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	// this.upload = "";
	this.uploadHeader = new ArrayList<String>();
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
     * Get the <code>Uploaded</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getUploaded() {
	return uploaded;
    }

    /**
     * Set the <code>Uploaded</code> value.
     *
     * @param uploaded The new Uploaded value.
     */
    public final void setUploaded(final Timestamp uploaded) {
	this.uploaded = uploaded;
    }

    /**
     * Get the <code>Userid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getUserid() {
	return userid;
    }

    /**
     * Set the <code>Userid</code> value.
     *
     * @param userid The new Userid value.
     */
    public final void setUserid(final long userid) {
	this.userid = userid;
    }

    /**
     * Get the <code>Filesetid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getFilesetid() {
	return filesetid;
    }

    /**
     * Set the <code>Filesetid</code> value.
     *
     * @param filesetid The new Filesetid value.
     */
    public final void setFilesetid(final long filesetid) {
	this.filesetid = filesetid;
    }

    /**
     * Get the <code>Upload</code> value.
     *
     * @return a <code>String</code> value
     */
    // public final String getUpload() {

    // 	return upload;
    // }

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

    /**
     * Reads the content from the input stream.
     * The md5sum is updated accordingly.
     *
     * @param ins the input stream.
     */
    public void readUploadContent( InputStream ins ) 
	throws IOException {

	if( uploadContentHandler == null )
	    throw new IOException( "Cannot upload content" );
	String md5 = uploadContentHandler.storeUploadContent( null, null, ins );
	if( md5 != null )
	    this.setMd5sum( md5 );
    }

    /**
     * Get the <code>Created</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getCreated() {
	return logstamp;
    }

    /**
     * Set the <code>Created</code> value.
     *
     * @param created The new Created value.
     */
    public final void setCreated(final Timestamp created) {
	this.logstamp = created;
    }

    /**
     * Creates the content to represent a folder file.
     * The md5sum is updated accordingly.
     *
     * @param ins the input stream.
     */
    public void writeFileset() 
	throws IOException {

	if( uploadContentHandler == null )
	    throw new IOException( "Cannot upload content" );
	
	Properties props = new Properties();
	props.setProperty( "uploadid", String.valueOf( this.getUploadid() ) );
	props.setProperty( "templateid", String.valueOf( this.getTemplateid() ) );
	props.setProperty( "userid", String.valueOf( this.getUserid() ) );
	
	Timestamp dt = (this.getUploaded()==null)?new Timestamp(System.currentTimeMillis()):this.getUploaded();
	props.setProperty( "uploaded", Stringx.getDateString( "yyyy-MMM-dd hh:mm:ss", dt ) );

	props.setProperty( "mime", Stringx.getDefault( this.getMime(), FILESET_MIME ) );
	props.setProperty( "filename", Stringx.getDefault( this.getFilename(), "fileset_"+System.currentTimeMillis() ) );

	ByteArrayOutputStream outs = new ByteArrayOutputStream();
	props.storeToXML( outs, "fileset properties", "UTF-8" );
	byte[] fCont = outs.toByteArray();
	outs.close();
	
	InputStream ins = new ByteArrayInputStream( fCont );
	String md5 = uploadContentHandler.storeUploadContent( null, null, ins );
	if( md5 != null )
	    this.setMd5sum( md5 );
	ins.close();
    }

    /**
     * Get the <code>uploaded content</code> as a string value.
     *
     * @return a <code>String</code> value
     */
    private String getUploadContent() 
	throws IOException {

	if( uploadContentHandler == null )
	    throw new IOException( "Cannot upload content" );

	StringWriter sw = new StringWriter();
	WriterOutputStream outs = new WriterOutputStream( sw );
	uploadContentHandler.writeUploadContentTo( getMd5sum(), getMime(), outs );
	outs.flush();
	return sw.toString();
    }

    /**
     * Set the <code>Upload</code> value.
     *
     * @param upload The new Upload value.
     */
    // public final void setUpload(final String upload) {
    // 	this.upload = Stringx.getDefault(upload,"").trim();
    // 	initMd5( this.upload );
    // }

    /**
     * Get the <code>Valid</code> value.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean isValid() {
	return (uploaded != null);
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
     * Get the <code>Nummsg</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getNummsg() {
	return nummsg;
    }

    /**
     * Set the <code>Nummsg</code> value.
     *
     * @param nummsg The new Nummsg value.
     */
    public final void setNummsg(final int nummsg) {
	this.nummsg = nummsg;
    }

    /**
     * Get the <code>Logstamp</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getLogstamp() {
	return logstamp;
    }

    /**
     * Set the <code>Logstamp</code> value.
     *
     * @param logstamp The new Logstamp value.
     */
    public final void setLogstamp(final Timestamp logstamp) {
	this.logstamp = logstamp;
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
    public File writeUploadFile( File dest ) 
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
     * Prepares a temp workspace for processing files.
     *
     * @return a temporary file representing a directory.
     */
    public File createWorkspace() {
	File wsDir = null;
	try {
	    File tempF = File.createTempFile( "deleteme", null );
	    wsDir = new File( tempF.getParentFile(), "upd-"+UUID.randomUUID().toString() );
	    if( !wsDir.mkdir() ) {
		log.error( "Cannot create folder: "+wsDir );
		wsDir = null;
	    }
	    if( wsDir != null ) 
		writeUploadFile( wsDir );
	    tempF.delete();
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	    wsDir = null;
	}
	return wsDir; 
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new UploadBatch();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return String.valueOf(getUploadid());
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof UploadBatch ) {
	    UploadBatch f = (UploadBatch)obj;
	    return (f.getUploadid() == this.getUploadid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(getUploadid()).hashCode();
    }

    private void addUploadColumn( String colName ) {
	uploadHeader.add( colName );
    }

    private void parseHeader( String hLine ) {
	String[] headerLines = hLine.split( "[|,]" );
	for( int i = 0; i < headerLines.length; i++ ) 
	    addUploadColumn( headerLines[i] );
    }

    /**
     * Returns the column name at the given position.
     *
     * @param index the column index.
     * @return the column name (can be empty string if not existing).
     */
    public String getColumn( int index ) {
	if( (index < 0) || (index > uploadHeader.size()) )
	    return "";
	return uploadHeader.get( index );
    }

    /**
     * Returns the column index.
     *
     * @param colName the column name.
     * @return the column index (or -1 if not existing).
     */
    public int getColumnIndex( String colName ) {
	if( colName != null ) {
	    for( int i = 0; i < uploadHeader.size(); i++ ) {
		if( colName.equals( (String)uploadHeader.get(i) ) )
		    return i;
	    }
	}
	return -1;
    }

    /**
     * Returns the column names if any detected.
     *
     * @return a list of column names.
     */
    public List<String> readColumns() {
	return uploadHeader;
    }

    /**
     * Reads through the lines of the upload content and returns a list of lines.
     *
     * @return a list of lines.
     */
    public List<String> readLines() {
	String cont = null;
	try {
	    cont = Stringx.getDefault(getUploadContent(),"");
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	}

	StringReader sr = new StringReader( cont );
	uploadHeader.clear();
	try {
	    List<String> lines = IOUtils.readLines( sr );
	    if( lines.size() > 0 )
		parseHeader( lines.get(0) );
	    return lines;
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	}
	List<String> el = Collections.emptyList();
	return el;
    }

    /**
     * Normalizes a study name.
     *
     * @param study the unformatted study name.
     * @return a formatted study name
     */
    public String formatStudy( String study ) {
	if( (study == null) || (study.trim().length() <= 0) )
	    return "Unknown";
	String sName = study.trim().toUpperCase();
	sName = sName.replace(" ","").replace( "_", "-" );
	if( (sName.indexOf( "-" ) < 0) && (sName.length() > 3) ) 
	    sName = sName.substring(0, sName.length()-3 )+"-"+sName.substring(sName.length()-3);
	return sName;	
    }

    /**
     * Extracts the site id.
     *
     * @param subject the subject id.
     * @return a site id
     */
    public String formatSite( String subject ) {
	if( subject == null )
	    return "000";
	String sName = subject.trim().replace(" ","").replace( "-", "" );
	if( sName.length() <= 3 )
	    return "000";
	sName = StringUtils.leftPad( sName, 7, "0" );
	return sName.substring( 0, 3 );
    }

    /**
     * Extracts the site id.
     *
     * @param subject the subject id.
     * @return a site id
     */
    public String formatSubject( String subject ) {
	if( subject == null )
	    return "0000000";
	String sName = subject.trim().replace(" ","").replace( "-", "" );
	return StringUtils.leftPad( sName, 7, "0" );
    }

}
