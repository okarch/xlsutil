package com.emd.xlsutil.velocity;

import java.io.File;
import java.io.Reader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.velocity.tools.generic.SafeConfig;

import com.emd.xlsutil.dts.DataTransferSpecification;
import com.emd.xlsutil.dts.DTSUtils;

/**
 * <code>DtsTool</code> is a tool that can be placed in a velocity context to
 * handle data transfer specifications.
 *
 * Created: Tue Aug 12 19:51:33 2017
 *
 * @author <a href="mailto:">Oliver Karch</a>
 * @version 1.0
 */
public class DtsTool extends SafeConfig {

    private static Log log = LogFactory.getLog(DtsTool.class);

    /**
     * Creates the tool
     */
    public DtsTool() {
	super();
    }

    /**
     * Reads the given data specification file.
     *
     * @param dtsFile the specification.
     *
     * @return an initialized <code>DataTransferSpecification</code> object.
     *
     * @exception IOException thrown while reading spec file.
     */
    public DataTransferSpecification readDTS( Object dtsFile ) {
	DataTransferSpecification dts = null;
	
	if( dtsFile == null ) {
	    log.error( "DTS file is invalid" );
	}
	else if( dtsFile instanceof File ) {
	    try {
		dts = DTSUtils.readDTS( (File)dtsFile );
	    }
	    catch( IOException ioe ) {
		log.error( ioe );
		dts = null;
	    }
	}
	else if( dtsFile instanceof Reader ) {
	    try {
		dts = DTSUtils.readDTS( (Reader)dtsFile );
	    }
	    catch( IOException ioe ) {
		log.error( ioe );
		dts = null;
	    }
	}
	else {
	    try {
		dts = DTSUtils.readDTS( new File( dtsFile.toString() ) );
	    }
	    catch( IOException ioe ) {
		log.error( ioe );
		dts = null;
	    }
	}
    
	return ((dts == null)?new DataTransferSpecification():dts);
    }

}
