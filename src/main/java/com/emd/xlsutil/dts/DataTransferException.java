package com.emd.xlsutil.dts;

/**
 * <code>DataTransferException</code> signals exceptional states while processing
 * data transfer specs
 *
 * Created: Fri Aug 18 21:22:18 2017
 *
 * @author <a href="mailto:Oliver.Karch@merckgroup.com"></a>
 * @version 0.1
 */

public class DataTransferException extends Exception {

    public DataTransferException( String msg ) {
	super( msg );
    }
    public DataTransferException( Throwable t ) {
	super( t );
    }
    
}
