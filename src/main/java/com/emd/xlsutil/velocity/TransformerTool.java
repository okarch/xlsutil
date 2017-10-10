package com.emd.xlsutil.velocity;

// import java.io.ByteArrayInputStream;
// import java.io.ByteArrayOutputStream;
// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.InputStream;
// import java.io.IOException;
// import java.io.OutputStream;

// import java.math.BigDecimal;
// import java.net.URLEncoder;

// import java.util.ArrayList;
// import java.util.List;

import org.apache.commons.lang.CharSetUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// import org.apache.poi.EncryptedDocumentException;
// import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
// import org.apache.poi.ss.SpreadsheetVersion;
// import org.apache.poi.ss.usermodel.Sheet;
// import org.apache.poi.ss.usermodel.Workbook;
// import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.apache.velocity.tools.generic.SafeConfig;

import com.emd.xlsutil.format.DataTransformers;
import com.emd.xlsutil.format.DefaultTransformer;

import com.emd.util.Stringx;

/**
 * <code>TransformerTool</code> is a tool that can be placed in a velocity context to
 * help dealing with Excel cell formatting.
 *
 * Created: Tue Oct 25 15:01:33 2016
 *
 * @author <a href="mailto:">Oliver Karch</a>
 * @version 1.0
 */
public class TransformerTool extends SafeConfig {

    private static Log log = LogFactory.getLog(TransformerTool.class);

    /**
     * Creates the tool
     */
    public TransformerTool() {
	super();
    }
    
    /**
     * Creates a new transformer by evaluating the given parameters, e.g.
     * "csv" creates a DefaultTransformer producing output in comma seperated format
     * "tab;my.transformer.SpecialTransformer" creates an instance of SpecialTransformer(if it can be found from searching classpath)
     * "csv-quoted;my-named;specialChars=txcf" creates an instance of a named transformer and sets a property specialChars to txcf
     *
     * @param x the parameters to be used for instantiation.
     * @return an instiated transformer.
     */
    public DefaultTransformer create( Object x ) {
	if( x == null )
	    return DataTransformers.getInstance();
	return DataTransformers.getInstance( x.toString() );
    }

}
