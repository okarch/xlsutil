package com.emd.xlsutil.velocity;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.tika.Tika;

import org.apache.velocity.tools.generic.SafeConfig;

/**
 * <code>ExtractorTool</code> is a tool that can be placed in a velocity context to
 * extract text from various office formats by wrapping Apache Tika.
 *
 * Created: Tue Mar 13 14:01:33 2016
 *
 * @author <a href="mailto:">Oliver Karch</a>
 * @version 1.0
 */
public class ExtractorTool extends SafeConfig {
    private Tika tika = new Tika();

    private static Log log = LogFactory.getLog(ExtractorTool.class);

    /**
     * Creates the tool
     */
    public ExtractorTool() {
	super();
	this.tika = new Tika();
    }
    
    /**
     * Detects mime type from a given file or file path.
     *
     * @param file the file or file path.
     * @return the mime detected or null.
     */
    public String detectMime( Object file ) {
	String mime = null;
	if( file == null )
	    return null;
	if( file instanceof File ) {
	    try {
		mime = tika.detect( (File)file );
	    }
	    catch( IOException ioe ) {
		log.warn( ioe );
	    }
	}
	mime = tika.detect( file.toString() );
	return mime;
    }
 
}
