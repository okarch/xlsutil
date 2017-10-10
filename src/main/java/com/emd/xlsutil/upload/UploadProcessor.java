package com.emd.xlsutil.upload;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import java.net.URL;

import java.sql.SQLException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
// import org.apache.commons.io.IOUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;

import com.emd.xlsutil.dao.DatasetDAO;

import com.emd.util.Stringx;

/**
 * <code>UploadProcessor</code> assembles the templates in a given directory 
 * and produces an aggregate document.
 *
 * @author <a href="mailto:Oliver.Karch@merck.de>Oliver Karch</a>
 *
 */
public class UploadProcessor {
    private ToolManager    toolManager;
    private VelocityEngine vEngine;

    private static UploadProcessor uploadProcessor;

    private static Log log = LogFactory.getLog(UploadProcessor.class);

    private static final String LOGGER_NAME      = "CONSOLE";
    private static final String TOOLS_CONFIG     = "tools.xml";

    private UploadProcessor() {
    }

    /**
     * Creates or retrieves the current <code>UploadProcessor</code> instance.
     *
     * @return an <code>UploadProcessor</code> instance.
     */
    public static UploadProcessor getInstance() {
	if( uploadProcessor == null )
	    uploadProcessor = new UploadProcessor();
	return uploadProcessor;
    }

    private ToolManager getToolManager() {
	if( toolManager == null ) {
	    toolManager = new ToolManager( false, true );
	    try {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream( TOOLS_CONFIG );
		if( is != null ) {
		    XmlFactoryConfiguration cfg = new XmlFactoryConfiguration();
		    cfg.read( is );
		    is.close();
		    toolManager.configure( cfg );
		    log.debug( "Configured context tools from "+TOOLS_CONFIG );
		}
		else
		    log.warn( "Cannot configure tools" );
	    }
	    catch( IOException ioe ) {
		log.warn( "Cannot configure tools: "+((ioe.getMessage()!=null)?ioe.getMessage():"") );
	    }
	}
	return toolManager;
    }

    private Context createVelocityContext( UploadBatch upd, 
					   UploadTemplate templ,
					   Map context ) {
	ToolManager manager = getToolManager();
	if( manager == null )
	    return new VelocityContext( context );
	ToolContext tc = manager.createContext();

	// add additional tools, e.g. FileUtils
	tc.put( "files", FileUtils.class );
	tc.put( "strings", StringUtils.class );
	tc.put( "dates", DateUtils.class );
	tc.put( "dateFormats", DateFormatUtils.class );

	// specific tools
	// tc.put( "samples", SampleType.class );
	// tc.put( "subjects", Subject.class );
	// tc.put( "studies", Study.class );

	tc.put( "db", DatasetDAO.getInstance() );
	if( upd != null )
	    tc.put( "upload", upd );
	tc.put( "template", templ );

	tc.put( "saveOutput", new Boolean(false) );

	// add additional context variables
	tc.putAll( context );

	return tc;
    }    

    // private Properties extractVelocityProperties( Map context ) {
    // 	Properties props = new Properties();
    // 	String rLoader = null;
    // 	if( (rLoader = (String)context.get( "resource.loader" )) == null )
    // 	    rLoader = "file";
    // 	props.put( "resource.loader", rLoader );
    // 	rLoader = rLoader + ".";
    // 	Iterator<Map.Entry> it = context.entrySet().iterator();
    // 	while( it.hasNext() ) {
    // 	    Map.Entry me = it.next();
    // 	    if( (me.getKey() != null) &&
    // 		(me.getKey().toString().startsWith( rLoader )) )
    // 		props.put( me.getKey().toString(), me.getValue() );
    // 	}
    // 	return props;
    // }

    // private VelocityEngine getVelocityEngine( Properties props ) 
    private VelocityEngine getVelocityEngine() 
	throws UploadException {

	if( vEngine == null ) {
	    // Properties vProps = extractVelocityProperties( props );

	    // log.debug( "Creating velocity engine using properties: "+vProps );

	    vEngine = new VelocityEngine();
 	    vEngine.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
 				 "org.apache.velocity.runtime.log.Log4JLogChute" );
 	    vEngine.setProperty("runtime.log.logsystem.log4j.logger",
 				LOGGER_NAME);
 	    vEngine.setProperty("directive.set.null.allowed", "true" );

	    log.debug( "Velocity engine logging setup done." );
	    try {
		// vEngine.init( vProps );
		vEngine.init();
		log.debug( "Velocity engine initialized." );
	    }
	    catch( Exception ex ) {
		log.error( ex );
		throw new UploadException( ex.getMessage() );
	    }
	}
	return vEngine;
    }

    // private Properties createVelocityProperties( File templDir, Map context ) {
    // 	Properties props = new Properties();

    // 	StringBuilder stb = new StringBuilder( System.getProperty( "user.dir" ) );
    // 	if( templDir.isFile() ) {
    // 	    File parent = templDir.getParentFile();
    // 	    if( parent != null ) {
    // 		stb.append( ", " );
    // 		stb.append( parent );
    // 	    }
    // 	}
    // 	else {
    // 	    stb.append( ", " );
    // 	    stb.append( templDir.getAbsoluteFile() );
    // 	}
	    
    // 	props.put("file.resource.loader.path", stb.toString() );
    // 	props.put("file.resource.loader.modificationCheckInterval", "120" );
    // 	props.put("file.resource.loader.cache", "true" );

    // 	if( context != null ) {
    // 	    String rLoader = null;
    // 	    if( (rLoader = (String)context.get( "resource.loader" )) == null )
    // 		rLoader = "file";
    // 	    props.put( "resource.loader", rLoader );
    // 	    props.putAll( context );
    // 	}
    // 	else
    // 	    props.put( "resource.loader", "file" );

    // 	return props;
    // }

    private String toTemplate( String tCont ) {
	return tCont.trim().replace( "\\n", "\n" );
    }

    /**
     * Process the template(s).
     *
     * @param templDir a directory or file where the template is located.
     * @param outDir a directory or file where the output should be written to. 
     * This can be null (current directory is used instead)
     * @param context a map of properties for use inside the velocity context.
     * @exception TemplateException signals abnormal behavior.
     */
    public void processUpload( UploadTemplate template, long uploadId, Map context ) 
	throws UploadException {

	UploadBatch upd = template.getUploadBatch( uploadId );
	if( upd == null )
	    throw new UploadException( "Cannot retrieve upload batch: "+uploadId );

	VelocityEngine ve = getVelocityEngine();
	Context vc = createVelocityContext( upd, template, context );

	String tCont = toTemplate( template.getTemplate() );
	log.debug( "Template:\n"+tCont );

	try {
	    StringWriter sw = new StringWriter();
	    if( !ve.evaluate( vc, sw, template.getTemplatename(), tCont ) )
		log.error( "Cannot transform template "+template.getTemplatename() );
	    sw.flush();
	    String logCont = sw.toString();
	    sw.close();
	    log.debug( "Upload document produced:\n"+logCont ); 
	    Boolean sOut = (Boolean)vc.get( "saveOutput" );
	    if( (sOut.booleanValue()) && (logCont.length() > 0) ) {
		String mime = Stringx.getDefault((String)vc.get( "outputMime" ),"text/plain");
		log.debug( "Storing output of type "+mime );
		DatasetDAO db = (DatasetDAO)vc.get( "db" );
		if( db == null )
		    throw new UploadException( "Database access is invalid" );
		db.appendOutput( upd, null, mime, logCont );
	    }
	}
	catch( Exception ex ) {
	    log.error( ex );
	    throw new UploadException( ex.getMessage() );
	}
    }

    /**
     * Process the template(s).
     *
     * @param template the template to be used.
     * @param context a map of properties for use inside the velocity context.
     * @exception TemplateException signals abnormal behavior.
     */
    public void processTemplate( UploadTemplate template, Map context ) 
	throws UploadException {

	VelocityEngine ve = getVelocityEngine();
	Context vc = createVelocityContext( null, template, context );

	String tCont = toTemplate( template.getTemplate() );
	log.debug( "Template:\n"+tCont );

	try {
	    StringWriter sw = new StringWriter();
	    if( !ve.evaluate( vc, sw, template.getTemplatename(), tCont ) )
		log.error( "Cannot transform template "+template.getTemplatename() );
	    sw.flush();
	    String logCont = sw.toString();
	    sw.close();
	    log.debug( "Upload document produced:\n"+logCont ); 
	}
	catch( Exception ex ) {
	    log.error( ex );
	    throw new UploadException( ex.getMessage() );
	}
    }

}
