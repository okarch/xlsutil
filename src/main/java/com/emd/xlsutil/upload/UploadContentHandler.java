package com.emd.xlsutil.upload;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <code>UploadContentHandler</code> specifies an interface to read and write uploaded content.
 *
 * Created: Mon Dec 12 12:58:00 2016
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public interface UploadContentHandler {

    /**
     * Writes uploaded content to the given <code>OutputStream</code>.
     *
     * @param md5sum the content identifier.
     * @param mime the mime type (can be null if provided could be used to apply some output encoding)
     * @param outs the output stream to write content to.
     *
     * @return true if content available.
     *
     * @exception IOException is thrown when an error occurs.
     */
    public boolean writeUploadContentTo( String md5sum, String mime, OutputStream outs )
	throws IOException;


    /**
     * Reads from the given <code>InputStream</code> and stores the content.
     *
     * @param md5sum the content identifier (if null it will be calculated based on the content).
     * @param mime the mime type (can be null if provided could be used to apply some input encoding)
     * @param ins the input stream to read from.
     *
     * @return the md5sum calculated from the content (null signals error).
     *
     * @exception IOException is thrown when an error occurs.
     */
    public String storeUploadContent( String md5sum, String mime, InputStream ins )
	throws IOException;
}
