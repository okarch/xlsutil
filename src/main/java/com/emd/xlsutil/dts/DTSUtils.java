package com.emd.xlsutil.dts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.util.ClassUtils;
import com.emd.util.Stringx;

/**
 * <code>DTSUtils</code> provides utilities to read data transfer specifications.
 *
 * Created: Sat Aug 12 12:38:03 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class DTSUtils {

    private static Log log = LogFactory.getLog(DTSUtils.class);

    private static final String COLSPEC          = "columns";
    private static final String TERMSPEC         = "terms";

    private DTSUtils() { }

    /**
     * Reads the given data specification file.
     *
     * @param dtsFile the specification.
     *
     * @return an initialized <code>DataTransferSpecification</code> object.
     *
     * @exception IOException thrown while reading spec file.
     */
    public static DataTransferSpecification readDTS( File dtsFile )
	throws IOException {

	if( !dtsFile.exists() || !dtsFile.canRead() )
	    throw new IOException( "Cannot read: "+dtsFile );

	FileReader fr = new FileReader( dtsFile );
	DataTransferSpecification dts = readDTS( fr );
	fr.close();
	
	return dts;
    }

    /**
     * Reads the given data specification form the reader.
     *
     * @param dtsReader the specification reader.
     *
     * @return an initialized <code>DataTransferSpecification</code> object.
     *
     * @exception IOException thrown while reading spec file.
     */
    public static DataTransferSpecification readDTS( Reader dtsReader )
	throws IOException {

	BufferedReader br = new BufferedReader( dtsReader );
	String line = null;
	DataTransferSpecification dts = new DataTransferSpecification();
	boolean colSpecStart = false;
	boolean termSpecStart = false;
	DataTransferMap map = null;
	do {
	    line = br.readLine();
	    if( line != null ) {
		line = Stringx.strcut( line, "#" ).trim();
		if( line.length() > 0 ) {
		    if( line.startsWith( COLSPEC ) ) {
			colSpecStart = true;
			continue;
		    }			
		    else if( line.startsWith( TERMSPEC ) ) {
			String termMapName = Stringx.after( line, TERMSPEC ).trim();
			map = dts.addTermMap( termMapName );
			termSpecStart = true;
			colSpecStart = false;
			continue;
		    }
		    else if( colSpecStart ) {
			try {
			    dts.addColumnFromLine( line );
			}
			catch( DataTransferException dte ) {
			    throw new IOException( dte );
			}
		    }
		    else if( termSpecStart ) {
			map.addTermFromLine( line );
		    }
		    else {
			String an = Stringx.before( line, "=" ).trim();
			String av = Stringx.after( line, "=" );
			ClassUtils.set( dts, an, av );
		    }
		}
	    }
	}
	while( line != null );

	log.debug( "DTS read: "+dts );

	return dts;
    }

}
