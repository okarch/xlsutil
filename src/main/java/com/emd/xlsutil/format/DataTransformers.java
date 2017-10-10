package com.emd.xlsutil.format;

import java.util.ArrayList;
import java.util.List;

import com.emd.util.ClassUtils;
import com.emd.util.Stringx;

/**
 * <code>DataTransformers</code> contains a list of data transformers available.
 *
 * Created: Tue May  2 18:54:03 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class DataTransformers {
    private List transformers;

    private final static DataTransformers dataTransformer = new DataTransformers(); 

    private DataTransformers() {
	this.transformers = new ArrayList<DefaultTransformer>();
    }

    /**
     * Returns an instance of a transformer.
     * "csv" creates a DefaultTransformer producing output in comma seperated format
     * "tab;my.transformer.SpecialTransformer" creates an instance of SpecialTransformer(if it can be found from searching classpath)
     * "csv-quoted;my-named;specialChars=txcf" creates an instance of a named transformer and sets a property specialChars to txcf
     *
     * @param params a string describing the config.
     * @return the new instance.
     */
    public static DefaultTransformer getInstance( String params ) {
	if( params == null ) {
	    DefaultTransformer dt = new DefaultTransformer();
	    dt.setOutputFormat( "csv" );
	}
	String[] cfg = params.split( ";" );
	String output = cfg[0];
	DefaultTransformer transformer = null;
	if( cfg.length > 1 ) {
	    try {
		Class clazz = Class.forName( cfg[1] );
		transformer = (DefaultTransformer)clazz.newInstance();
	    }
	    catch( Exception ex ) {
		transformer = null;
	    }
	}
	if( transformer == null )
	    transformer = new DefaultTransformer();
	transformer.setOutputFormat( output );
	for( int i = 2; i < cfg.length; i++ ) {
	    String pName = Stringx.before( cfg[i], "=" ).trim();
	    String pVal = Stringx.after( cfg[i], "=" ).trim();
	    if( pName.length() > 0 )
		ClassUtils.set( transformer, pName, pVal );
	}
	return transformer;
    }

    /**
     * Returns an instance of a default transformer.
     * @return the new instance.
     */
    public static DefaultTransformer getInstance() {
	return getInstance( null );
    }
}
