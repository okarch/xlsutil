package com.emd.xlsutil.dao;

import java.math.BigDecimal;

import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import java.util.zip.DataFormatException;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import org.apache.commons.dbutils.DbUtils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.xlsutil.dataset.Dataset;
import com.emd.xlsutil.dataset.Organization;
import com.emd.xlsutil.dataset.Property;
import com.emd.xlsutil.dataset.PropertySet;
import com.emd.xlsutil.dataset.PropertyType;
import com.emd.xlsutil.dataset.Trackable;
import com.emd.xlsutil.dataset.User;

import com.emd.xlsutil.dts.DataTransferSpecification;

// import com.emd.simbiom.model.Accession;
// import com.emd.simbiom.model.SampleEvent;
// import com.emd.simbiom.model.SampleProcess;
// import com.emd.simbiom.model.SampleSummary;
// import com.emd.simbiom.model.SampleType;
// import com.emd.simbiom.model.Study;
// import com.emd.simbiom.model.StudySample;
// import com.emd.simbiom.model.Subject;

// import com.emd.simbiom.model.Treatment;

import com.emd.xlsutil.upload.UploadTemplate;
import com.emd.xlsutil.upload.UploadBatch;
import com.emd.xlsutil.upload.UploadContent;
import com.emd.xlsutil.upload.UploadContentHandler;
import com.emd.xlsutil.upload.UploadLog;
import com.emd.xlsutil.upload.UploadOutput;

import com.emd.io.ReaderInputStream;
import com.emd.io.WriterOutputStream;

import com.emd.util.Stringx;
import com.emd.util.ZipCoder;

/**
 * <code>DatasetDAO</code> the database access to the dataset inventory.
 *
 * Created: Tue Nov 15 18:27:17 2016
 *
 * @author <a href="mailto:">Oliver</a>
 * @version 1.0
 */
public class DatasetDAO implements UploadContentHandler {
    private String       url;
    private String       username;
    private String       password;
    private String       driver;
    private String       template;
    private String       schema;
    private String       test;
    private int          retryCount;
    private boolean      initDatabaseFromTemplate;
    private long         lastActivity;

    private Connection   dbSource;
    
    private PropertiesConfiguration templateConfig;
    private Map<String,PreparedStatement>  statements;

    private static Log log = LogFactory.getLog(DatasetDAO.class);
    private static DatasetDAO datasetDAO;

    private static final int DEFAULT_RETRY       = 10;

    private static final String DEFAULT_DRIVER   = "com.mysql.jdbc.Driver";
    private static final String DEFAULT_URL      = "jdbc:mysql://localhost/dataset";
    private static final String DEFAULT_USERNAME = "dataset";
    private static final String DEFAULT_PASSWORD = "dataset";
    private static final String DEFAULT_TEMPLATE = "dataset-mysql.properties";
    private static final String DEFAULT_SCHEMA   = "dataset";
    private static final String DEFAULT_TEST     = "select 1 from dual";

    private static final String STMT_DATASET_BY_ID          = "dataset.dataset.findById";
    private static final String STMT_DATASET_BY_DTS         = "dataset.dataset.findByStudySetType";
    private static final String STMT_DATASET_INSERT         = "dataset.dataset.insert";
    private static final String STMT_DATASET_UPDATE         = "dataset.dataset.update";

    private static final String STMT_DTS_BY_ID              = "dataset.dts.findById";
    private static final String STMT_DTS_INSERT             = "dataset.dts.insert";
    private static final String STMT_DTS_UPDATE             = "dataset.dts.update";

    private static final String STMT_LOG_FIND_BY_UPLOAD     = "dataset.log.findByUpload";
    private static final String STMT_LOG_INSERT             = "dataset.log.insert";
    private static final String STMT_LOG_DELETE             = "dataset.log.deleteAll";

    private static final String STMT_ORG_BY_ID              = "dataset.organization.findById";
    private static final String STMT_ORG_BY_NAME            = "dataset.organization.findByName";
    private static final String STMT_ORG_INSERT             = "dataset.organization.insert";
    private static final String STMT_ORG_UPDATE             = "dataset.organization.update";

    private static final String STMT_OUTPUT_BY_BATCH        = "dataset.output.findByBatch";
    private static final String STMT_OUTPUT_BY_CHECKSUM     = "dataset.output.findByChecksum";
    private static final String STMT_OUTPUT_BY_FILENAME     = "dataset.output.findByFilename";
    private static final String STMT_OUTPUT_BY_ID           = "dataset.output.findById";
    private static final String STMT_OUTPUT_INSERT          = "dataset.output.insert";
    private static final String STMT_OUTPUT_LATEST          = "dataset.output.findLatestOutput";

    private static final String STMT_PROPERTY_BY_ID         = "dataset.property.findById";

    private static final String STMT_PROPERTYSET_BY_NAME    = "dataset.propertyset.findByName";
    private static final String STMT_PROPERTYSET_BY_ID      = "dataset.propertyset.findById";
    private static final String STMT_PROPERTYSET_INSERT     = "dataset.propertyset.insert";

    private static final String STMT_PROPERTYTYPE_BY_NAME   = "dataset.propertytype.findByName";
    private static final String STMT_PROPERTYTYPE_INSERT    = "dataset.propertytype.insert";

    // private static final String STMT_RAW_FIND_OLD        = "dataset.uploadraw.findArchiveLoads";
    private static final String STMT_RAW_FIND_BY_MD5        = "dataset.uploadraw.findByChecksum";
    private static final String STMT_RAW_DELETE             = "dataset.uploadraw.delete";
    private static final String STMT_RAW_INSERT             = "dataset.uploadraw.insert";

    private static final String STMT_TEMPLATE_BY_ID         = "dataset.template.findById";
    private static final String STMT_TEMPLATE_BY_NAME       = "dataset.template.findByName";
    private static final String STMT_TEMPLATE_INSERT        = "dataset.template.insert";
    private static final String STMT_TEMPLATE_UPDATE        = "dataset.template.update";
    private static final String STMT_TEMPLATE_DELETE        = "dataset.template.delete";

    private static final String STMT_UPLOAD_FILESET_BY_NAME = "dataset.upload.findFilesetByName";
    private static final String STMT_UPLOAD_FILESET_FILES   = "dataset.upload.findFilesetFiles";
    private static final String STMT_UPLOAD_INSERT          = "dataset.upload.insert";
    private static final String STMT_UPLOAD_LOG             = "dataset.upload.findLatestLogs";
    private static final String STMT_UPLOAD_DELETE          = "dataset.upload.deleteAll";
    // private static final String STMT_UPLOAD_MOVE         = "dataset.upload.move";

    private static final String STMT_USER_BY_APIKEY         = "dataset.user.findByApikey";
    private static final String STMT_USER_BY_MUID           = "dataset.user.findByMuid";
    private static final String STMT_USER_BY_ID             = "dataset.user.findById";

    private static final String STMT_TRACK_BY_ID            = "dataset.track.findById";
    private static final String STMT_TRACK_DELETE           = "dataset.track.delete";
    private static final String STMT_TRACK_INSERT           = "dataset.track.insert";


    private static final String DEFAULT_RESOURCE         = "dataset-mysql.properties";

    private static final int    MAX_MSG_LENGTH   = 255;

    private static final long   DEFAULT_REFRESH  = 7L * 60L * 1000L; // 7 minutes
    private static final long   ONE_DAY          = 24L * 60L * 60L * 1000L; // 1 day
    private static final long   IDLE_PERIOD      =  10L * 60L * 1000L; // 10 minutes

    // private static final long   POLICY_INTERVAL  = 10L * 60L * 60L * 1000L; // every 10 minutes

    public DatasetDAO() {
	this.driver = DEFAULT_DRIVER;
	this.url = DEFAULT_URL;
	this.username = DEFAULT_USERNAME;
	this.password = DEFAULT_PASSWORD;
	this.template = DEFAULT_TEMPLATE;
	this.retryCount = DEFAULT_RETRY;
	this.schema = DEFAULT_SCHEMA;
	this.test = DEFAULT_TEST;
	this.statements = new HashMap<String,PreparedStatement>();
	this.initDatabaseFromTemplate = false;
	this.lastActivity = System.currentTimeMillis();
    }

    /**
     * Creates a singleton instance to be used for interpretation of
     * list activities
     */
    public static DatasetDAO getInstance() {
	if( datasetDAO == null ) {
	    datasetDAO = new DatasetDAO();
	    datasetDAO.setTemplate( DEFAULT_RESOURCE );
	    datasetDAO.setInitDatabaseFromTemplate( true );
	    log.debug( "New dataset inventory instance created" );
	}
	return datasetDAO;
    }

    /**
     * Get the Url value.
     * @return the Url value.
     */
    public String getUrl() {
	return url;
    }

    /**
     * Set the Url value.
     * @param newUrl The new Url value.
     */
    public void setUrl(String newUrl) {
	this.url = newUrl;
    }

    /**
     * Get the Username value.
     * @return the Username value.
     */
    public String getUsername() {
	return username;
    }

    /**
     * Set the Username value.
     * @param newUsername The new Username value.
     */
    public void setUsername(String newUsername) {
	this.username = newUsername;
    }

    /**
     * Get the Driver value.
     * @return the Driver value.
     */
    public String getDriver() {
	return driver;
    }

    /**
     * Set the Driver value.
     * @param newDriver The new Driver value.
     */
    public void setDriver(String newDriver) {
	this.driver = newDriver;
    }

    /**
     * Get the Password value.
     * @return the Password value.
     */
    public String getPassword() {
	return password;
    }

    /**
     * Set the Password value.
     * @param newUsername The new Password value.
     */
    public void setPassword(String newPassword) {
	this.password = newPassword;
    }

    /**
     * Get the Template value.
     * @return the Template value.
     */
    public String getTemplate() {
	return template;
    }

    /**
     * Set the Template value.
     * @param newTemplate The new Template value.
     */
    public void setTemplate(String newTemplate) {
	this.template = newTemplate;
    }

    /**
     * Get the RetryCount value.
     * @return the RetryCount value.
     */
    public int getRetryCount() {
	return retryCount;
    }

    /**
     * Set the RetryCount value.
     * @param newRetryCount The new RetryCount value.
     */
    public void setRetryCount(int newRetryCount) {
	this.retryCount = newRetryCount;
    }

    /**
     * Get the InitDatabaseFromTemplate value.
     * @return the InitDatabaseFromTemplate value.
     */
    public boolean isInitDatabaseFromTemplate() {
	return initDatabaseFromTemplate;
    }

    /**
     * Set the InitDatabaseFromTemplate value.
     * @param newInitDatabaseFromTemplate The new InitDatabaseFromTemplate value.
     */
    public void setInitDatabaseFromTemplate(boolean newInitDatabaseFromTemplate) {
	this.initDatabaseFromTemplate = newInitDatabaseFromTemplate;
    }

    /**
     * Get the <code>Schema</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getSchema() {
	return schema;
    }

    /**
     * Set the <code>Schema</code> value.
     *
     * @param schema The new Schema value.
     */
    public final void setSchema(final String schema) {
	this.schema = schema;
    }

    /**
     * Get the <code>Test</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTest() {
	return test;
    }

    /**
     * Set the <code>Test</code> value.
     *
     * @param test The new Test value.
     */
    public final void setTest(final String test) {
	this.test = test;
    }


    private Connection connect( boolean forceConnect ) throws SQLException {
	if( dbSource == null ) {
	    log.debug( "Creating dataset inventory datasource" );
	    String st = getDriver();
	    if( !DbUtils.loadDriver( st ) )
		throw new SQLException(  "Cannot load jdbc driver: "+st );
	    log.debug( "Database driver loaded: "+st );
	    dbSource = DriverManager.getConnection( getUrl(), getUsername(), getPassword() );
	}
	else if( dbSource.isClosed() || forceConnect ) {

	    if( forceConnect ) {
		try {
		    dbSource.close();
		}
		catch( SQLException sqe ) {
		    log.debug( "Cannot close: "+Stringx.getDefault( sqe.getMessage(), "" ) );
		}
	    }
		
	    int retries = getRetryCount();
	    do {
		retries--;
		try {
		    dbSource = DriverManager.getConnection( getUrl(), getUsername(), getPassword() );
		    return dbSource;
		}
		catch( SQLException sqe ) {
		    log.warn( sqe );
		}
		try {
		    Thread.currentThread().sleep( 1000L );
		}
		catch( InterruptedException iex ) {
		    throw new SQLException( "Interrupted retry" );
		}
	    }
	    while( retries > 0 );
	}

	return dbSource;
    }

    private PropertiesConfiguration readTemplate() throws SQLException {
	URL url = this.getClass().getClassLoader().getResource( getTemplate() );
	if( url == null )
	    url = this.getClass().getResource( "/"+getTemplate() );
	if( url == null ) 
	    throw new SQLException( "Cannot locate template configuration: "+getTemplate() );
	try {
	    PropertiesConfiguration config = new PropertiesConfiguration( url );
	    FileChangedReloadingStrategy frs = new FileChangedReloadingStrategy();
	    frs.setRefreshDelay( DEFAULT_REFRESH );
	    config.setReloadingStrategy( frs );
	    log.debug( "Sample inventory database template read from "+url );
	    return config;
	}
	catch( ConfigurationException cex ) {
	    throw new SQLException( "Error loading configuration from "+url );
	}
    }

    private void createTable( Connection con, String entity ) throws SQLException {
	String schemaName = Stringx.getDefault(getSchema(),DEFAULT_SCHEMA);
	String tabName = templateConfig.getString( schemaName+"."+entity+".table" );
	log.debug( "Verify table "+Stringx.getDefault(tabName,"UNKNOWN") );
	boolean tabExists = false;
	if( !(tabExists = TableUtils.tableExists( con, tabName )) &&
	    !TableUtils.createTable( con, tabName, templateConfig.getString(schemaName+"."+entity+".structure"), false ) ) {
	    con.close();
	    throw new SQLException( "Cannot create table "+tabName );
	}
	if( !tabExists ) {
	    String[] idx = templateConfig.getStringArray( schemaName+"."+entity+".index" );
	    for( int i = 0; i < idx.length; i++ ) {
		if( !TableUtils.indexTable( con, tabName, idx[i], false ) )
		    log.warn( "Cannot create index on "+tabName+" "+idx[i] );
		log.debug( "Index "+idx[i]+" verified ("+tabName+")" );
	    }
	}
    }

    private void initTemplate() throws SQLException {
	if( templateConfig == null ) {
	    log.debug( "Initialize dataset inventory" );

	    templateConfig = readTemplate();
	    if( isInitDatabaseFromTemplate() ) {
		setDriver( templateConfig.getString( "db.driver", DEFAULT_DRIVER ) );
		setUrl( templateConfig.getString( "db.url", DEFAULT_URL ) );
		setUsername( templateConfig.getString( "db.username", DEFAULT_USERNAME ) );
		setPassword( templateConfig.getString( "db.password", DEFAULT_PASSWORD ) );
		setRetryCount( templateConfig.getInt( "db.retryCount", DEFAULT_RETRY ) );
		setSchema( templateConfig.getString( "db.schema", DEFAULT_SCHEMA ) );
		setTest( templateConfig.getString( "db.test", DEFAULT_TEST ) );
	    }

	    Connection con = connect( false );

	    createTable( con, "dataset" ); 
	    createTable( con, "dts" ); 

	    createTable( con, "log" ); 

	    // property related tables

	    createTable( con, "column" ); 
	    createTable( con, "member" ); 
	    createTable( con, "property" ); 
	    createTable( con, "propertyset" ); 
	    createTable( con, "propertytype" ); 

	    createTable( con, "template" ); 
	    createTable( con, "track" ); 

	    createTable( con, "upload" ); 
	    createTable( con, "uploadraw" ); 
	    createTable( con, "output" ); 
	    createTable( con, "user" ); 

// 	    con.close();
	    log.debug( "Initialize dataset inventory database done" );
	}
    }

    private boolean connectionExpired() {
	return ((System.currentTimeMillis() - lastActivity) > IDLE_PERIOD);
    }

    private boolean testFailed( Connection con ) throws SQLException {
	boolean failed = false;
	if( connectionExpired() ) {
	    failed = true;
	    Statement stmt = con.createStatement();
	    ResultSet res = stmt.executeQuery( getTest() );
	    if( res.next() )
		failed = false;
	    res.close();
	    stmt.close();
	}
	return failed;
    }	

    private PreparedStatement getStatement( String stmtName ) throws SQLException {
	// initialize the database if not done yet

	initTemplate();

	PreparedStatement pstmt = statements.get( stmtName );

	// test prepared statement if it works properly

	boolean forceConnect = false;
	if( pstmt != null ) {
	    boolean invalidate = false;
	    try {
		Connection con = pstmt.getConnection();
		if( (con == null) || (con.isClosed()) || (testFailed(con)) ) 
		    invalidate = true;
	    }
	    catch( SQLException sqe ) {
		log.warn( sqe );
		invalidate = true;
	    }
	    if( invalidate ) {
		log.warn( "Invalidate statement \""+stmtName+"\"" );
		statements.remove( stmtName );
		pstmt = null;
		forceConnect = true;
	    }
	}

	// create it as required

	if( pstmt == null ) {
	    log.warn( "Cannot find statement \""+stmtName+"\". Need to create it" );
	    String querySt = templateConfig.getString( stmtName );
	    if( querySt == null )
		throw new SQLException( "Cannot find named statement: "+stmtName );
	    log.debug( "Preparing named statement \""+stmtName+"\": "+querySt );
	    Connection con = connect( forceConnect );
	    pstmt = con.prepareStatement( templateConfig.getString( stmtName ) );
	    statements.put( stmtName, pstmt );
	    log.debug( "Named statement \""+stmtName+"\" registered" );
	}

	lastActivity = System.currentTimeMillis();

	return pstmt;
    }

    private void insertTrack( Trackable track, 
			      long prevId,
			      long userId, 
			      String activity,
			      String remark ) 
	throws SQLException {

	PreparedStatement pstmt = getStatement( STMT_TRACK_INSERT );
	pstmt.setLong( 1, track.getTrackid() );
	pstmt.setTimestamp( 2, new Timestamp(System.currentTimeMillis()) );
	pstmt.setLong( 3, prevId );
	pstmt.setString( 4,  track.getItem() );
	pstmt.setString( 5, activity );
	pstmt.setLong( 6, userId );
	pstmt.setString( 7, Stringx.getDefault( remark, "" ) );
	pstmt.setString( 8, StringEscapeUtils.escapeSql(track.toContent()) );
	pstmt.executeUpdate();
    }

    private void trackChange( Trackable before,
			      Trackable changed,
			      long userId,
			      String activity,
			      String remark )
	throws SQLException {

	PreparedStatement pstmt = null;
	long prevId = -1L;
	if( before != null ) {
	    pstmt = getStatement( STMT_TRACK_BY_ID );
	    pstmt.setLong( 1, before.getTrackid() );
	    ResultSet res = pstmt.executeQuery();
	    if( res.next() ) 
		prevId = before.getTrackid();
	    res.close();
	    if( prevId == -1L ) {
		String msg = "Previous track information could not be found";
		log.warn( msg );
		insertTrack( before, -1L, userId, activity, msg );
	    }
	    
	}
	
	if( changed != null ) {
	    if( changed.getTrackid() == prevId )
		return;
	    insertTrack( changed, prevId, userId, activity, remark );
	}
	else if( prevId != -1L ) {
	    pstmt = getStatement( STMT_TRACK_DELETE );
	    pstmt.setTimestamp( 1, new Timestamp(System.currentTimeMillis()) );
	    pstmt.setString( 2, activity );
	    pstmt.setLong( 3, userId );
	    pstmt.setString( 4, Stringx.getDefault( remark, "" ) );
	    pstmt.setLong( 5, prevId );
	    pstmt.executeUpdate();
	}
    }

    /**
     * Creates a new dataset entry.
     *
     * @param userId the user id
     * @param setName the dataset name.
     *
     * @return the newly allocated file watch.
     */
    public Dataset createDataset( long userId, String setName ) throws SQLException {
	Dataset dset = Dataset.getInstance( setName );

	PreparedStatement pstmt = getStatement( STMT_DATASET_INSERT );

	pstmt.setString( 1, dset.getDatasetid() );
	pstmt.setString( 2, dset.getStudy() );
	pstmt.setString( 3, dset.getSetname() );
	pstmt.setString( 4, dset.getDatatype() );
	pstmt.setTimestamp( 5, dset.getCreated() );
	pstmt.setString( 6, dset.getVersion() );
	pstmt.setLong( 7, dset.getStamp() );
	pstmt.setLong( 8, dset.getTrackid() );
	pstmt.executeUpdate();

	log.debug( "Dataset created: "+dset.getSetname()+" ("+
		   dset.getDatasetid()+") trackid: "+dset.getTrackid() );
	
	trackChange( null, dset, userId, "Dataset created", null );
 
	return dset;
    }

    /**
     * Stores a dataset entry.
     *
     * @param userId the user id
     * @param dSet the dataset.
     *
     * @return the stored <code>Dataset</code> object.
     */
    public Dataset storeDataset( long userId, Dataset dSet ) throws SQLException {
	Dataset oldSet = findDatasetById( dSet.getDatasetid() );

     	PreparedStatement pstmt = null;
     	int nn = 2;
     	if( oldSet == null ) {
     	    pstmt = getStatement( STMT_DATASET_INSERT );
     	    pstmt.setString( 1, dSet.getDatasetid() );
     	    log.debug( "Creating a new dataset entry: "+dSet.getDatasetid()+" called \""+dSet.toString()+"\"" );
     	}
     	else {
     	    pstmt = getStatement( STMT_DATASET_UPDATE );
     	    pstmt.setString( 8, dSet.getDatasetid() );
     	    nn--;
     	    log.debug( "Updating existing dataset entry: "+dSet.getDatasetid()+" called \""+dSet.toString()+"\"" );
     	}
	
     	pstmt.setString( nn, dSet.getStudy() );
     	nn++;
     	pstmt.setString( nn, dSet.getSetname() );
     	nn++;
     	pstmt.setString( nn, dSet.getDatatype() );
     	nn++;
	pstmt.setTimestamp( nn, dSet.getCreated() );
     	nn++;
	pstmt.setString( nn, dSet.getVersion() );
	nn++;

	dSet.updateStamp();
	    
	pstmt.setLong( nn, dSet.getStamp() );
	nn++;
	pstmt.setLong( nn, dSet.getTrackid() );

     	pstmt.executeUpdate();

     	trackChange( oldSet, dSet, userId, "Dataset "+((oldSet==null)?"created":"updated"), null );

	return dSet;
    }

    /**
     * Returns a dataset by id.
     *
     * @param sampleId The sample id.
     * @return the Sample object or null (if not existing).
     */
    public Dataset findDatasetById( String datasetId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_DATASET_BY_ID );
     	pstmt.setString( 1, datasetId );
     	ResultSet res = pstmt.executeQuery();
     	Dataset sType = null;
     	if( res.next() ) 
     	    sType = (Dataset)TableUtils.toObject( res, new Dataset() );
     	res.close();
     	return sType;
    }

    /**
     * Returns a dataset by study and id.
     *
     * @param dts The data transfer specification.
     * @return a (potentially empty) list of <code>Dataset</code> objects.
     */
    public Dataset[] findDatasetByDts( DataTransferSpecification dts )
	throws SQLException {

	if( (dts == null) || (!dts.isValid()) )
	    return new Dataset[0];

	PreparedStatement pstmt = getStatement( STMT_DATASET_BY_DTS );
	pstmt.setString( 1, dts.getStudy() );
	pstmt.setString( 2, dts.getSetname() );
	pstmt.setString( 3, dts.getDatatype() );

     	ResultSet res = pstmt.executeQuery();

     	List<Dataset> fl = new ArrayList<Dataset>();
     	Iterator it = TableUtils.toObjects( res, new Dataset() );
	while( it.hasNext() ) {
	    Dataset ds = (Dataset)it.next();
	    fl.add( ds );
	}	       
	res.close();

     	Dataset[] facs = new Dataset[ fl.size() ];
     	return (Dataset[])fl.toArray( facs );
    }

    /**
     * Returns a list of templates and associated upload batches.
     *
     * @return the template object.
     */
    public UploadTemplate[] findTemplateByName( String templateName ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_TEMPLATE_BY_NAME );
	StringBuilder stb = new StringBuilder( "%" );
	if( (templateName != null) && (templateName.length() > 0) ) {
	    stb.append( templateName.toLowerCase() );
	    stb.append( "%" );
	}
	pstmt.setString( 1, stb.toString() );

     	ResultSet res = pstmt.executeQuery();
	
     	List<UploadTemplate> fl = new ArrayList<UploadTemplate>();
     	UploadTemplate lastTemplate = null;
	while( res.next() ) {
     	    UploadTemplate templ = (UploadTemplate)TableUtils.toObject( res, new UploadTemplate() );
     	    if( (lastTemplate == null) || (templ.getTemplateid() != lastTemplate.getTemplateid()) ) {
     		fl.add( templ );
     		lastTemplate = templ;
     	    }
     	    UploadBatch uBatch = (UploadBatch)TableUtils.toObject( res, new UploadBatch() );
	    if( uBatch.isValid() ) {
		uBatch.setUploadContentHandler( this );
		lastTemplate.addUploadBatch( uBatch );
	    }
     	}
	res.close();
     	UploadTemplate[] facs = new UploadTemplate[ fl.size() ];
     	return (UploadTemplate[])fl.toArray( facs );
    }

    /**
     * Returns a list of templates and associated upload batches.
     *
     * @return the SampleType object.
     */
    public UploadTemplate findTemplateById( long templateId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_TEMPLATE_BY_ID );
	pstmt.setLong( 1, templateId );

     	ResultSet res = pstmt.executeQuery();
	
     	List<UploadTemplate> fl = new ArrayList<UploadTemplate>();
     	UploadTemplate lastTemplate = null;
	while( res.next() ) {
     	    UploadTemplate templ = (UploadTemplate)TableUtils.toObject( res, new UploadTemplate() );
     	    if( (lastTemplate == null) || (templ.getTemplateid() != lastTemplate.getTemplateid()) ) {
     		fl.add( templ );
     		lastTemplate = templ;
     	    }
     	    UploadBatch uBatch = (UploadBatch)TableUtils.toObject( res, new UploadBatch() );
	    if( uBatch.isValid() ) {
		uBatch.setUploadContentHandler( this );
		lastTemplate.addUploadBatch( uBatch );
	    }
     	}
	res.close();
	if( fl.size() > 0 )
	    return fl.get( 0 );
	return null;
    }

    /**
     * Returns a list of upload batches ordered by logstamp descending.
     * Limits list to 100 entries.
     * 
     * @return the list of uploads.
     */
    public UploadBatch[] findLatestOutput() throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_OUTPUT_LATEST );

     	ResultSet res = pstmt.executeQuery();

     	List<UploadBatch> fl = new ArrayList<UploadBatch>();
     	Iterator it = TableUtils.toObjects( res, new UploadBatch() );
	while( it.hasNext() ) {
	    UploadBatch uBatch = (UploadBatch)it.next();
	    uBatch.setUploadContentHandler( this );
	    log.debug( "Output: "+uBatch.getUploadid()+", "+((uBatch.getLogstamp()==null)?"":uBatch.getLogstamp().toString()) );
	    fl.add( uBatch );
	}	       
	res.close();

     	UploadBatch[] facs = new UploadBatch[ fl.size() ];
     	return (UploadBatch[])fl.toArray( facs );
    }


    /**
     * Returns a list of upload batches ordered by logstamp descending.
     * Limits list to 100 entries.
     * 
     * @return the list of uploads.
     */
    public UploadBatch[] findLatestLogs() throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_UPLOAD_LOG );

     	ResultSet res = pstmt.executeQuery();

     	List<UploadBatch> fl = new ArrayList<UploadBatch>();
     	Iterator it = TableUtils.toObjects( res, new UploadBatch() );
	while( it.hasNext() ) {
	    UploadBatch uBatch = (UploadBatch)it.next();
	    uBatch.setUploadContentHandler( this );
	    fl.add( uBatch );
	}	       
	res.close();

     	UploadBatch[] facs = new UploadBatch[ fl.size() ];
     	return (UploadBatch[])fl.toArray( facs );
    }

    /**
     * Returns a list of log messages related to the given upload batch.
     * 
     * @param upBatch the upload batch.
     * @param levels comma separated list of log levels to be included.
     * @return the list of log messages.
     */
    public UploadLog[] findLogByUpload( UploadBatch upBatch, String levels ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_LOG_FIND_BY_UPLOAD );
	pstmt.setLong( 1, upBatch.getUploadid() );

     	ResultSet res = pstmt.executeQuery();

     	List<UploadLog> fl = new ArrayList<UploadLog>();
     	Iterator it = TableUtils.toObjects( res, new UploadLog() );
	while( it.hasNext() ) {
	    UploadLog log = (UploadLog)it.next();
	    if( (levels == null) || (levels.trim().length() <= 0) || (levels.indexOf(log.getLevel()) >= 0) )
		fl.add( log );
	}	       
	res.close();

     	UploadLog[] facs = new UploadLog[ fl.size() ];
     	return (UploadLog[])fl.toArray( facs );
    }

    /**
     * Creates an upload batch and properly initializes the upload content handler.
     *
     * @param userId the user id.
     * @param templ the template this upload is linked to.
     *
     * @return an initialized <code>UploadBatch</code> object.
     */
    public UploadBatch createUploadBatch( long userId, UploadTemplate templ )
	throws SQLException {

	if( templ == null )
	    throw new SQLException( "Template is invalid" );

     	UploadBatch uBatch = new UploadBatch();
     	uBatch.setTemplateid( templ.getTemplateid() );
     	uBatch.setUploaded( new Timestamp(System.currentTimeMillis()) );
     	uBatch.setUserid( userId );
	uBatch.setUploadContentHandler( this );

	return uBatch;
    }

    /**
     * Returns the upload batch associated with the given fileset name.
     *
     * @param setName the fileset name.
     * @return the <code>UploadBatch</code> object (or null if not existing).
     */
    public UploadBatch findFilesetByName( String setName ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_UPLOAD_FILESET_BY_NAME );
     	pstmt.setString( 1, Stringx.getDefault(setName,"") );
     	ResultSet res = pstmt.executeQuery();
     	UploadBatch upd = null;
     	if( res.next() ) 
     	    upd = (UploadBatch)TableUtils.toObject( res, new UploadBatch() );
     	res.close();
     	return upd;
    }

    /**
     * Returns a list of files contained in a fileset.
     * 
     * @param fid the filesetid.
     * @return the list of uploads.
     */
    public UploadBatch[] findFilesByFileset( long filesetId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_UPLOAD_FILESET_FILES );
     	pstmt.setLong( 1, filesetId );

     	ResultSet res = pstmt.executeQuery();

     	List<UploadBatch> fl = new ArrayList<UploadBatch>();
     	Iterator it = TableUtils.toObjects( res, new UploadBatch() );
	while( it.hasNext() ) {
	    UploadBatch uBatch = (UploadBatch)it.next();
	    uBatch.setUploadContentHandler( this );
	    fl.add( uBatch );
	}	       
	res.close();

     	UploadBatch[] facs = new UploadBatch[ fl.size() ];
     	return (UploadBatch[])fl.toArray( facs );
    }

    /**
     * Appends the output produced by an upload.
     *
     * @param batch the upload batch.
     * @param fileName the uploads file name.
     * @param mime the mime type.
     * @param output the output content.
     *
     * @return the (newly) stored template.
     */
    public UploadOutput appendOutput( UploadBatch batch, 
				      String fileName, 
				      String mime, 
				      InputStream output )
	throws SQLException {

	if( batch == null )
	    throw new SQLException( "Invalid upload batch" );

	UploadOutput updOut = new UploadOutput();
	updOut.setUploadid( batch.getUploadid() );
	updOut.setFilename( Stringx.getDefault(fileName,System.currentTimeMillis()+".out").trim() );
	updOut.setMime( Stringx.getDefault(mime,"text/plain") );

	String md5 = null;
	try {
	    md5 = storeUploadContent( null, null, output );
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	    throw new SQLException( ioe );
	}

	updOut.setMd5sum( md5 );

	PreparedStatement pstmt = getStatement( STMT_OUTPUT_INSERT );

	pstmt.setLong( 1, updOut.getOutputid() );
	pstmt.setLong( 2, updOut.getUploadid() );
	pstmt.setTimestamp( 3, updOut.getCreated() );
	pstmt.setString( 4, updOut.getMd5sum() ); 
	pstmt.setString( 5, updOut.getFilename() ); 
	pstmt.setString( 6, updOut.getMime() ); 
	pstmt.executeUpdate();	

	return updOut;
    }

    /**
     * Appends the output produced by an upload.
     *
     * @param batch the upload batch.
     * @param fileName the uploads file name.
     * @param mime the mime type.
     * @param output the output content.
     *
     * @return the (newly) stored template.
     */
    public UploadOutput appendOutput( UploadBatch batch, 
				      String fileName, 
				      String mime, 
				      String output )
	throws SQLException {

	UploadOutput updOut = null;
	try {
	    ReaderInputStream rin = new ReaderInputStream( new StringReader( output ) );
	    updOut = appendOutput( batch, fileName, mime, rin );
	    rin.close();
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	    throw new SQLException( ioe );
	}
	return updOut;
    }

    private String getMimeType( File theF ) throws IOException {
	FileNameMap fileNameMap = URLConnection.getFileNameMap();
	return fileNameMap.getContentTypeFor( theF.toURI().toString() );
    }

    /**
     * Appends the output produced by an upload.
     *
     * @param batch the upload batch.
     * @param file the output content.
     *
     * @return the (newly) stored template.
     */
    public UploadOutput appendOutput( UploadBatch batch, 
				      File fileOut ) 
	throws SQLException {

	UploadOutput updOut = null;
	try {
	    String mime = getMimeType( fileOut );
	    InputStream rin = new FileInputStream( fileOut );
	    updOut = appendOutput( batch, fileOut.getName(), mime, rin );
	    rin.close();
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	    throw new SQLException( ioe );
	}
	return updOut;	
    }

    /**
     * Appends the output produced by an upload.
     *
     * @param batch the upload batch.
     * @param file the output content.
     * @param mime the mime type.
     *
     * @return the (newly) stored template.
     */
    public UploadOutput appendOutput( UploadBatch batch, 
				      File fileOut,
				      String mime )
	throws SQLException {

	UploadOutput updOut = null;
	try {
	    // String mime = getMimeType( fileOut );
	    InputStream rin = new FileInputStream( fileOut );
	    updOut = appendOutput( batch, fileOut.getName(), mime, rin );
	    rin.close();
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	    throw new SQLException( ioe );
	}
	return updOut;	
    }

    /**
     * Returns output by id.
     *
     * @param outputId The output id.
     * @return the <code>UploadOutput</code> object or null (if not found).
     */
    public UploadOutput findOutputById( long outputId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_OUTPUT_BY_ID );
     	pstmt.setLong( 1, outputId );
     	ResultSet res = pstmt.executeQuery();
     	UploadOutput upd = null;
     	if( res.next() ) {
     	    upd = (UploadOutput)TableUtils.toObject( res, new UploadOutput() );
	    upd.setUploadContentHandler( this );
	}
     	res.close();
     	return upd;
    }

    /**
     * Returns outputs by checksum.
     *
     * @param checksum The output content's checksum.
     * @return the list of <code>UploadOutput</code> objects or empty list (if nothing was found).
     */
    public UploadOutput[] findOutputByChecksum( String checksum ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_OUTPUT_BY_CHECKSUM );
     	pstmt.setString( 1, checksum );

     	ResultSet res = pstmt.executeQuery();

     	List<UploadOutput> fl = new ArrayList<UploadOutput>();
     	Iterator it = TableUtils.toObjects( res, new UploadOutput() );
	while( it.hasNext() ) {
	    UploadOutput uBatch = (UploadOutput)it.next();
	    uBatch.setUploadContentHandler( this );
	    fl.add( uBatch );
	}	       
	res.close();

     	UploadOutput[] facs = new UploadOutput[ fl.size() ];
     	return (UploadOutput[])fl.toArray( facs );
    }

    /**
     * Returns outputs by file name.
     *
     * @param fName The output's file name.
     * @return the list of <code>UploadOutput</code> objects or empty list (if nothing was found).
     */
    public UploadOutput[] findOutputByFilename( String fName ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_OUTPUT_BY_CHECKSUM );
     	pstmt.setString( 1, fName );

     	ResultSet res = pstmt.executeQuery();

     	List<UploadOutput> fl = new ArrayList<UploadOutput>();
     	Iterator it = TableUtils.toObjects( res, new UploadOutput() );
	while( it.hasNext() ) {
	    UploadOutput uBatch = (UploadOutput)it.next();
	    uBatch.setUploadContentHandler( this );
	    fl.add( uBatch );
	}	       
	res.close();

     	UploadOutput[] facs = new UploadOutput[ fl.size() ];
     	return (UploadOutput[])fl.toArray( facs );
    }

    /**
     * Returns outputs by batch id.
     *
     * @param batchId The batch id.
     * @return the list of <code>UploadOutput</code> objects or empty list (if nothing was found).
     */
    public UploadOutput[] findOutputByBatch( long batchId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_OUTPUT_BY_BATCH );
     	pstmt.setLong( 1, batchId );

     	ResultSet res = pstmt.executeQuery();

     	List<UploadOutput> fl = new ArrayList<UploadOutput>();
     	Iterator it = TableUtils.toObjects( res, new UploadOutput() );
	while( it.hasNext() ) {
	    UploadOutput uBatch = (UploadOutput)it.next();
	    uBatch.setUploadContentHandler( this );
	    fl.add( uBatch );
	}	       
	res.close();

     	UploadOutput[] facs = new UploadOutput[ fl.size() ];
     	return (UploadOutput[])fl.toArray( facs );
    }

    /**
     * Writes uploaded content to the given <code>OutputStream</code>.
     *
     * @param md5sum the content identifier.
     * @param mime the mime type (can be null if provided could be used to apply some output encoding)
     * @param outs the output stream to write content to.
     *
     * @exception IOException is thrown when an error occurs.
     */
    public boolean writeUploadContentTo( String md5sum, String mime, OutputStream outs )
	throws IOException {

	UploadContent cont = null;
	try {
	    PreparedStatement pstmt = getStatement( STMT_RAW_FIND_BY_MD5 );
	    pstmt.setString( 1, md5sum );
	    ResultSet res = pstmt.executeQuery();
	    cont = null;
	    if( res.next() ) 
		cont = (UploadContent)TableUtils.toObject( res, new UploadContent() );
	    res.close();
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new IOException( sqe );
	}

	if( cont == null ) {
	    log.warn( "Content not available: "+md5sum );
	    return false;
	}
	    
	byte[] buf = null;
	try {
	    buf = ZipCoder.decode( Stringx.getDefault(cont.getUpload(),"") );
	}
	catch( DataFormatException de ) {
	    log.error( de );
	    throw new IOException( de );
	}
	outs.write( buf );
	outs.close();
	buf = null;
	return true;
    }

    /**
     * Reads from the given <code>InputStream</code> and stores the content.
     *
     * @param md5sum the content identifier (if null it will be calculated based on the content).
     * @param mime the mime type (can be null if provided could be used to apply some input encoding)
     * @param ins the input stream to read from.
     *
     * @return the md5sum calculated from the content.
     *
     */
    public String storeUploadContent( String md5sum, String mime, InputStream ins )
	throws IOException {

	// String updCont = null;
	StringWriter sw = new StringWriter();
	WriterOutputStream outs = new WriterOutputStream( sw );
	ZipCoder.encodeTo( ins, outs );
	outs.flush();
	String updCont = sw.toString();
	ins.close();
	outs.close();
	String md5 = UploadContent.calculateMd5sum( updCont );
	log.debug( "Coded content length "+String.valueOf(updCont.length())+" md5sum: "+md5 );

	PreparedStatement pstmt = null;
	try {
	    pstmt = getStatement( STMT_RAW_DELETE );
	    pstmt.setString( 1, md5 );
	    pstmt.executeUpdate();
	}
	catch( SQLException sqe ) {
	    log.warn( "Deleting "+md5+": "+Stringx.getDefault(sqe.getMessage(),"") );
	}

	try {
	    pstmt = getStatement( STMT_RAW_INSERT );
	    pstmt.setString( 1, md5 );
	    pstmt.setString( 2, updCont );
	    pstmt.executeUpdate();
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new IOException( sqe );
	}

	return md5;
    }

    /**
     * Stores the given upload template.
     *
     * @param template the template to store.
     *
     * @return the (newly) stored template.
     */
    public UploadTemplate storeTemplate( long userId, UploadTemplate template ) throws SQLException {
	UploadTemplate templ = findTemplateById( template.getTemplateid() );

     	PreparedStatement pstmt = null;
     	int nn = 2;
     	if( templ == null ) {
     	    pstmt = getStatement( STMT_TEMPLATE_INSERT );
     	    pstmt.setLong( 1, template.getTemplateid() );
     	    log.debug( "Creating a new template entry: "+template.getTemplateid()+" called \""+template.toString()+"\"" );
     	}
     	else {
     	    pstmt = getStatement( STMT_TEMPLATE_UPDATE );
     	    pstmt.setLong( 4, template.getTemplateid() );
     	    nn--;
     	    log.debug( "Updating existing template entry: "+template.getTemplateid()+" called \""+template.toString()+"\"" );
     	}
	
     	pstmt.setString( nn, template.getTemplatename() );
     	nn++;

     	pstmt.setString( nn, template.getTemplate() );
     	nn++;

	template.updateTrackid();

     	pstmt.setLong( nn, template.getTrackid() );
     	nn++;

     	pstmt.executeUpdate();

     	trackChange( templ, template, userId, "Template "+((templ==null)?"created":"updated"), null );

	UploadBatch[] nBatches = template.getUploadBatches();
	List<UploadBatch> nb = new ArrayList<UploadBatch>();
	if( templ != null ) {
	    UploadBatch[] oBatches = templ.getUploadBatches();
	    for( int i = 0; i < nBatches.length; i++ ) {
		boolean foundIt = false;
		for( int j = 0; j < oBatches.length; j++ ) {
		    if( oBatches[j].equals( nBatches[i] ) ) {
			foundIt = true;
			break;
		    }
		}
		if( !foundIt )
		    nb.add( nBatches[i] );
	    }
	}
	else if( nBatches.length > 0 ) {
	    nb.addAll( Arrays.asList( nBatches ) );
	}

	pstmt = getStatement( STMT_UPLOAD_INSERT );
	for( UploadBatch upd : nb ) {
	    log.debug( "Upload file name: "+Stringx.getDefault(upd.getFilename(),"") );
	    pstmt.setLong( 1, upd.getUploadid() );
	    pstmt.setLong( 2, upd.getTemplateid() );
	    pstmt.setTimestamp( 3, upd.getUploaded() ); 
	    pstmt.setLong( 4, upd.getUserid() ); 
	    pstmt.setString( 5, upd.getMd5sum() ); 
	    pstmt.setString( 6, upd.getFilename() ); 
	    pstmt.setString( 7, upd.getMime() ); 
	    pstmt.setLong( 8, upd.getFilesetid() ); 
	    pstmt.executeUpdate();
	}

	// archiveUploads( template.getTemplateid() );

	if( templ != null )
	    template = findTemplateById( template.getTemplateid() );
     	return template;
    }

    /**
     * Updates a given upload template.
     *
     * @param template the template to store.
     *
     * @return the (newly) stored template.
     */
    public UploadTemplate updateTemplateByName( long userId, UploadTemplate template ) throws SQLException {
	UploadTemplate[] templs = findTemplateByName( template.getTemplatename() );
	if( (templs.length <= 0) || (templs.length > 1) )
	    throw new SQLException( "Cannot find "+template.getTemplatename()+" or ambiguous" );

	templs[0].setTemplate( template.getTemplate() );
	return storeTemplate( userId, templs[0] );
    }

    /**
     * Deletes the given upload template and accompanying uploads and logs.
     *
     * @param template the template to delete.
     * @return false if template cannot be deleted.
     */
    public boolean deleteTemplate( UploadTemplate template ) throws SQLException {
	UploadTemplate templ = findTemplateById( template.getTemplateid() );
	if( templ == null ) {
	    log.warn( "Cannot delete non-existing template id "+template.getTemplateid() );
	    return false;
	}

	// Remove log entries

     	PreparedStatement pstmt = getStatement( STMT_LOG_DELETE );
	pstmt.setLong( 1, template.getTemplateid() );
     	pstmt.executeUpdate();

	// Remove upload batches

     	pstmt = getStatement( STMT_UPLOAD_DELETE );
	pstmt.setLong( 1, templ.getTemplateid() );
     	pstmt.executeUpdate();

	// Remove the template itself

     	pstmt = getStatement( STMT_TEMPLATE_DELETE );
	pstmt.setLong( 1, templ.getTemplateid() );
     	pstmt.executeUpdate();

	log.debug( "Template \""+Stringx.getDefault(templ.getTemplatename(),String.valueOf(templ.getTemplateid()))+"\" has been removed" );

	return true;
    }

    /**
     * Appends a log message to the upload log.
     *
     * @param template the template to store.
     *
     * @return the (newly) stored template.
     */
    public UploadLog addUploadMessage( UploadBatch upload, String level, long line, String msg ) 
	throws SQLException {

	PreparedStatement pstmt = getStatement( STMT_LOG_INSERT );

	UploadLog updLog = new UploadLog();
	updLog.setUploadid( upload.getUploadid() );
	updLog.setLogstamp( new Timestamp(System.currentTimeMillis()) );
	updLog.setLevel( level );
	updLog.setLine( line );
	updLog.setMessage( Stringx.strtrunc(Stringx.getDefault(msg,"").trim(),MAX_MSG_LENGTH) );

	pstmt.setLong( 1, updLog.getLogid() );
	pstmt.setLong( 2, updLog.getUploadid() );
	pstmt.setTimestamp( 3, updLog.getLogstamp() );
	pstmt.setString( 4, updLog.getLevel() );
	pstmt.setLong( 5, updLog.getLine() );
	pstmt.setString( 6, updLog.getMessage() );
	pstmt.executeUpdate();

	return updLog;
    }

    /**
     * Appends a log message to the upload log.
     *
     * @param template the template to store.
     *
     * @return the (newly) stored template.
     */
    public UploadLog addUploadMessage( UploadBatch upload, String level, String msg ) 
	throws SQLException {

	return addUploadMessage( upload, level, 0L, msg );
    }

    /**
     * Returns user information by apikey.
     *
     * @param apikey The API key.
     * @return the User object.
     */
    public User findUserByApikey( String apikey ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_USER_BY_APIKEY );
     	pstmt.setString( 1, apikey );
     	ResultSet res = pstmt.executeQuery();
     	User user = null;
     	if( res.next() ) 
     	    user = (User)TableUtils.toObject( res, new User() );
     	res.close();
     	return user;
    }

    /**
     * Returns user information by muid.
     *
     * @param muid The MUID.
     * @return the User object.
     */
    public User findUserByMuid( String apikey ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_USER_BY_MUID );
     	pstmt.setString( 1, apikey );
     	ResultSet res = pstmt.executeQuery();
     	User user = null;
     	if( res.next() ) 
     	    user = (User)TableUtils.toObject( res, new User() );
     	res.close();
     	return user;
    }

    /**
     * Returns user information by apikey.
     *
     * @param apikey The API key.
     * @return the User object.
     */
    public User findUserById( long userid ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_USER_BY_ID );
     	pstmt.setLong( 1, userid );
     	ResultSet res = pstmt.executeQuery();
     	User user = null;
     	if( res.next() ) 
     	    user = (User)TableUtils.toObject( res, new User() );
     	res.close();
     	return user;
    }

    /**
     * Returns a specific <code>PropertyType</code> by name.
     *
     * @param typeName The type name.
     * @return the <code>PropertyType</code> object.
     */
    public PropertyType findTypeByName( String typeName ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_PROPERTYTYPE_BY_NAME );
     	pstmt.setString( 1, typeName );
     	ResultSet res = pstmt.executeQuery();
     	PropertyType prop = null;
     	if( res.next() ) 
     	    prop = (PropertyType)TableUtils.toObject( res, new PropertyType() );
     	res.close();
     	return prop;
    }

    /**
     * Returns a newly created <code>PropertyType</code> object.
     *
     * @param typeName The type name.
     * @param label The type label (can be null).
     * @return the <code>PropertyType</code> object.
     */
    public PropertyType createType( String typeName, String label ) throws SQLException {
	if( (typeName == null) || (typeName.trim().length() <= 0) ) 
	    throw new SQLException( "Property type is invalid" );

	PropertyType pType = new PropertyType();
	pType.setTypename( typeName.trim() );
	pType.setLabel( Stringx.getDefault( label, StringUtils.capitalize( typeName.trim() ) ) );

	PreparedStatement pstmt = getStatement( STMT_PROPERTYTYPE_INSERT );

	pstmt.setLong( 1, pType.getTypeid() );
	pstmt.setString( 2, pType.getTypename() );
	pstmt.setString( 3, pType.getLabel() );
	pstmt.executeUpdate();

	log.debug( "Property type created: ("+pType.getTypeid()+") "+pType.getTypename() );

	return pType;
    }

    /**
     * Returns a newly created <code>PropertyType</code> object.
     *
     * @param typeName The type name.
     * @return the <code>PropertyType</code> object.
     */
    public PropertyType createType( String typeName ) throws SQLException {
	return createType( typeName, null );
    }

    /**
     * Returns property information by apikey.
     *
     * @param propertyid The property id.
     * @return the <code>Property</code> object.
     */
    public Property findPropertyById( long propertyid ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_PROPERTY_BY_ID );
     	pstmt.setLong( 1, propertyid );
     	ResultSet res = pstmt.executeQuery();
     	Property prop = null;
     	if( res.next() ) 
     	    prop = (Property)TableUtils.toObject( res, new Property() );
     	res.close();
     	return prop;
    }

    /**
     * Returns an property set by name.
     *
     * @param setName the name of the property set.
     *
     * @return the <code>PropertySet</code> objects matching the query.
     */
    public PropertySet[] findPropertySetByName( String setName ) throws SQLException {
	String sName = Stringx.getDefault( setName, "" ).trim();
	if( sName.length() <= 0 )
	    sName = "%";
     	
	PreparedStatement pstmt = getStatement( STMT_PROPERTYSET_BY_NAME );
	pstmt.setString( 1, sName );
	ResultSet res = pstmt.executeQuery();
	List<PropertySet> fl = new ArrayList<PropertySet>();
	Iterator it = TableUtils.toObjects( res, new PropertySet() );
	long lastSetid = -1L;
	PropertySet propSet = null;
     	while( it.hasNext() ) {
     	    PropertySet pSet = (PropertySet)it.next();
	    if( pSet.getListid() != lastSetid ) {
		propSet = pSet;
		fl.add( propSet );
		lastSetid = pSet.getListid();
	    }
	    Property prop = findPropertyById( pSet.getPropertyid() );
	    if( prop != null )
		propSet.addProperty( prop );
     	}	       
     	res.close();

      	PropertySet[] facs = new PropertySet[ fl.size() ];
      	return (PropertySet[])fl.toArray( facs );	
    }

    /**
     * Returns an property set by name.
     *
     * @param setName the name of the property set.
     *
     * @return the <code>PropertySet</code> objects matching the query.
     */
    public PropertySet findPropertySetById( long setId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_PROPERTYSET_BY_ID );
	pstmt.setLong( 1, setId );
	ResultSet res = pstmt.executeQuery();
	List<PropertySet> fl = new ArrayList<PropertySet>();
	Iterator it = TableUtils.toObjects( res, new PropertySet() );
	long lastSetid = -1L;
	PropertySet propSet = null;
     	while( it.hasNext() ) {
     	    PropertySet pSet = (PropertySet)it.next();
	    if( pSet.getListid() != lastSetid ) {
		propSet = pSet;
		fl.add( propSet );
		lastSetid = pSet.getListid();
	    }
	    Property prop = findPropertyById( pSet.getPropertyid() );
	    if( prop != null )
		propSet.addProperty( prop );
	}
     	res.close();

	return ((fl.size() <= 0)?null:fl.get(0));
    }

    /**
     * Create a property set.
     *
     * @param setName the property set name.
     * @param setType the type of the property set.
     *
     * @return the newly allocated property set.
     */
    public PropertySet createPropertySet( String setName, String setType ) throws SQLException {
	String sName = setName;
	if( (setName == null) || (setName.trim().length() <= 0) )
	    sName = UUID.randomUUID().toString();

	// return property set if existing

	PropertySet[] pSets = findPropertySetByName( sName );
     	if( pSets.length > 0 )
	    return pSets[0]; 

	// create property set type if not existing
	
	String sType = Stringx.getDefault( setType, "unknown" ).trim();
	if( sType.length() <= 0 )
	    sType = "unknown";
	PropertyType pType = findTypeByName( sType );
	if( pType == null )
	    pType = createType( sType );

	// create property set

	PropertySet pSet = new PropertySet();
	pSet.setListname( sName );
	pSet.setTypeid( pType.getTypeid() );

	PreparedStatement pstmt = getStatement( STMT_PROPERTYSET_INSERT );

	pstmt.setLong( 1, pSet.getListid() );
	pstmt.setString( 2, pSet.getListname() );
	pstmt.setLong( 3, pSet.getTypeid() );

	pstmt.executeUpdate();

	log.debug( "Property set created: "+pSet.getListname()+" ("+
		   pSet.getListid()+") typeid: "+pSet.getTypeid() );

	return pSet;
    }

    /**
     * Returns a DTS by id.
     *
     * @param dtsId The DTS id.
     * @return the <code>DataTransferSpecification</code> object or null (if not existing).
     */
    public DataTransferSpecification findDtsById( long dtsId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_DTS_BY_ID );
     	pstmt.setLong( 1, dtsId );
     	ResultSet res = pstmt.executeQuery();
     	DataTransferSpecification dts = null;
     	if( res.next() ) {
     	    dts = (DataTransferSpecification)TableUtils.toObject( res, new DataTransferSpecification() );
	    PropertySet pSet = findPropertySetById( dts.getListid() );
	    if( pSet != null )
		dts.addPropertySet( pSet );
	}
     	res.close();
     	return dts;
    }

    /**
     * Returns a specific version of a DTS.
     *
     * @param dts The DTS.
     * @param 
     * @return the <code>DataTransferSpecification</code> object or null (if not existing).
     */
    public DataTransferSpecification findDtsById( long dtsId ) throws SQLException {

    /**
     * Creates a new dataset specification from a data transfer specification.
     *
     * @param userId the user id
     * @param dts the data specs.
     * @return the newly created data specification.
     */
    public DataTransferSpecification storeDts( long userId, DataTransferSpecification dts )
	throws SQLException {
	
	if( (dts == null) || (!dts.isValid()) )
	    throw new SQLException( "Data transfer specification is invalid" );

	DataTransferSpecification dSpec = findDtsById( dts.getDtsid() );

	// find or create associated dataset

	Dataset dSet = null;
	if( (dSpec != null) && (dSpec.getDatasetid() != null) ) {
	    log.debug( "Retrieve dataset from existing DTS: "+dSpec.getDtsid() );
	    dSet = findDatasetById( dSpec.getDatasetid() );
	}
	else if( dts.getDatasetid() != null ) {
	    dSet = findDatasetById( dts.getDatasetid() );
	}

	if( dSet == null ) {
	    Dataset[] dSets = findDatasetByDts( dts );
	    if( dSets.length <= 0 ) {
		dSet = new Dataset();
		dSet.setStudy( dts.getStudy() );
		dSet.setSetname( dts.getSetname() );
		dSet.setDatatype( dts.getDatatype() );
		dSet.setVersion( dts.getVersion() );
		dSet.updateStamp();

		log.debug( "Creating new dataset: "+dSet );
		dSet = storeDataset( userId, dSet );
	    }
	    else {
		dSet = dSets[0];
	    }
	}
	
	dts.setDatasetid( dSet.getDatasetid() );

	// resolve sender and receiver

	Organization org = findOrganizationById( dts.getSenderid() );
	if( org == null ) {
	    org = findOrganizationByName( dts.getSender() );
	    if( org == null ) {
		log.debug( "Sending organization does not exist" );
		org = createOrganization( dts.getSender(), "" );
	    }
	    dts.setSenderid( org.getOrgid() );
	}
	org = findOrganizationById( dts.getReceiverid() );
	if( org == null ) {
	    org = findOrganizationByName( dts.getReceiver() );
	    if( org == null ) {
		log.debug( "Receiving organization does not exist" );
		org = createOrganization( dts.getReceiver(), "" );
	    }
	    dts.setReceiverid( org.getOrgid() );
	}

	boolean dtsChanged = ( (dSpec != null) && (dSpec.changed(dts)) );

	// determine and link previous version of the dts

	long prevId = dts.getPreviousid();
	boolean keepPropertySet = false;
	if( dtsChanged ) {

	    // create a new version if previous differs from current version

	    String prevVersion = Stringx.getDefault( dts.getPrevious(), DataTransferSpecification.DEFAULT_VERSION );

	    // DataTransferSpecification sSpec = new DataTransferSpecification( dts, prevVersion );	    
	    DataTransferSpecification prevSpec = findDtsVersion( dts, prevVersion );

	    if( (prevSpec != null) && (prevSpec.getDtsid() != prevId) ) {
		prevId = prevSpec.getDtsid();
		keepPropertySet = true;
	    }
	}
	dts.setPreviousid( prevId );

	PreparedStatement pstmt = null;
     	int nn = 2;
	if( (dSpec == null) || (dtsChanged) ) {

	    // create header column description

	    PropertySet pSet = findPropertySetById( dts.getListid() );
	    if( (pSet != null) && (!keepPropertySet) ) 
		deletePropertySet( pSet );

	    pSet = createPropertySet( dSet.getSetname(), "header" );
	    dts.setListid( pSet.getListid() );

	    DataTransferColumn[] tCols = dts.getColumns();
	    for( int i = 0; i < tCols.length; i++ ) {
		Property prop = Property( tCols[i] );
		pSet.addProperty( prop );
	    }

	    storePropertySet( pSet );

	    pstmt = getStatement( STMT_DTS_INSERT );
	    pstmt.setLong( 1, dts.getDtsid() );
	}
	else {
	    pstmt = getStatement( STMT_DTS_UPDATE );
	    pstmt.setLong( 10, dts.getDtsid() );
	    nn--;
	}

	pstmt.setString( nn, dts.getDatasetid() );
	nn++;
	pstmt.setLong( nn, dts.getListid() );
	nn++;
	pstmt.setLong( nn, dts.getSenderid() );
	nn++;
	pstmt.setLong( nn, dts.getReceiverid() );
	nn++;
	pstmt.setTimestamp( nn, dts.getCreated() );
	nn++;
	pstmt.setLong( nn, dts.getPreviousid() );
	nn++;
	pstmt.setString( nn, dts.getVersion() );
	nn++;

	dts.updateStamp();
	pstmt.setLong( nn, dts.getStamp() );
	nn++;

	pstmt.setLong( nn, dts.getTrackid() );

	pstmt.executeUpdate();

     	trackChange( dSpec, dts, userId, "Data transfer specification "+((dSpec==null)?"created":"updated"), null );

	log.debug( "Data transfer specification created: "+dts );

	return dts;
    }

    /**
     * Returns an organization by name.
     *
     * @return the Organization object.
     */
    public Organization findOrganizationByName( String orgName ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_ORG_BY_NAME );
	pstmt.setString( 1, Stringx.getDefault(orgName,"").trim() );
      	ResultSet res = pstmt.executeQuery();
      	Organization sType = null;
      	if( res.next() ) 
      	    sType = (Organization)TableUtils.toObject( res, new Organization() );
      	res.close();
      	return sType;
    }
    
    /**
     * Returns an organization by name.
     *
     * @return the Organization object.
     */
    public Organization findOrganizationById( long orgid ) throws SQLException {
     	PreparedStatement pstmt = getStatement( STMT_ORG_BY_ID );
	pstmt.setLong( 1, orgid );
      	ResultSet res = pstmt.executeQuery();
      	Organization sType = null;
      	if( res.next() ) 
      	    sType = (Organization)TableUtils.toObject( res, new Organization() );
      	res.close();
      	return sType;
    }

    /**
     * Create a study.
     *
     * @param studyName the study name.
     *
     * @return the newly allocated study.
     */
    public Organization createOrganization( String orgName, String orgType ) throws SQLException {
     	Organization org = findOrganizationByName( orgName );
     	if( org != null )
     	    throw new SQLException( "Organization already exists: "+orgName );

     	org = new Organization();
     	org.setOrgname( orgName );
     	org.setOrgtype( orgType );

     	PreparedStatement pstmt = getStatement( STMT_ORG_INSERT );
     	pstmt.setLong( 1, org.getOrgid() );
     	pstmt.setString( 2, org.getOrgname() );
     	pstmt.setString( 3, org.getSiteid() );
     	pstmt.setInt( 4, org.getCountryid() );
     	pstmt.setString( 5, org.getOrgtype() );
     	pstmt.executeUpdate();

     	log.debug( "Organization created: "+org.getOrgname()+" ("+
     		   org.getOrgid()+")" );
	 
     	return org;
    }

    /**
     * Create a study.
     *
     * @param studyName the study name.
     *
     * @return the newly allocated study.
     */
    // public Organization storeOrganization( Organization org ) throws SQLException {
    // 	PreparedStatement pstmt = getStatement( STMT_ORG_UPDATE );

    // 	pstmt.setString( 1, org.getOrgname() );
    // 	pstmt.setString( 2, org.getSiteid() );
    // 	pstmt.setInt( 3, org.getCountryid() );
    // 	pstmt.setString( 4, org.getOrgtype() );

    // 	pstmt.setLong( 5, org.getOrgid() );

    // 	pstmt.executeUpdate();
    // 	return org;
    // }

    /**
     * Returns an organization by name.
     *
     * @return the Organization object.
     */
    // public Subject findSubjectByName( Study study, String subjectId ) throws SQLException {
    // 	PreparedStatement pstmt = getStatement( STMT_SUBJECT_BY_NAME );
    // 	pstmt.setLong( 1, study.getStudyid() );
    //  	pstmt.setString( 2, Stringx.getDefault(subjectId,"").trim() );
    //  	ResultSet res = pstmt.executeQuery();
    //  	Subject sType = null;
    //  	if( res.next() ) 
    //  	    sType = (Subject)TableUtils.toObject( res, new Subject() );
    //  	res.close();
    //  	return sType;
    // }



    /**
     * Returns a list of accessions issued by a certain organization across all studies.
     *
     * @param organization the organization issueing the accession.
     * @param acc the accession.
     *
     * @return the SampleType object.
     */
    // public SampleEvent[] findSiteEvent( Organization org, String visitDesc ) throws SQLException {
    // 	PreparedStatement pstmt = getStatement( STMT_EVENT_BY_ORG );
    // 	pstmt.setLong( 1, org.getOrgid() );
    // 	pstmt.setString( 2, visitDesc );

    //  	ResultSet res = pstmt.executeQuery();
    //  	List<SampleEvent> fl = new ArrayList<SampleEvent>();
    //  	Iterator it = TableUtils.toObjects( res, new SampleEvent() );
    // 	while( it.hasNext() ) {
    // 	    fl.add( (SampleEvent)it.next() );
    // 	}	       
    // 	res.close();

    //  	SampleEvent[] facs = new SampleEvent[ fl.size() ];
    //  	return (SampleEvent[])fl.toArray( facs );	
    // }

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    // public SampleEvent findEventById( long timeid ) throws SQLException {
    // 	PreparedStatement pstmt = getStatement( STMT_EVENT_BY_ID );
    //  	pstmt.setLong( 1, timeid );
    //  	ResultSet res = pstmt.executeQuery();
    //  	SampleEvent sType = null;
    //  	if( res.next() ) 
    //  	    sType = (SampleEvent)TableUtils.toObject( res, new SampleEvent() );
    //  	res.close();
    //  	return sType;
    // }

    /**
     * Creates a new site visit.
     *
     * @param organization the site of the event conducted.
     * @param visit the visit label.
     *
     * @return the <code>SampleEvent</code> object.
     */
    // public SampleEvent createSiteEvent( Organization org, String visitDesc ) throws SQLException {
    // 	SampleEvent[] events = findSiteEvent( org, visitDesc );
    // 	if( events.length > 0 ) 
    // 	    throw new SQLException( "Site timpoint exists already" );
	
    // 	SampleEvent se = SampleEvent.parseVisit( visitDesc );
    // 	se.setOrgid( org.getOrgid() );

    // 	PreparedStatement pstmt = getStatement( STMT_EVENT_INSERT );
    // 	pstmt.setLong( 1, se.getTimeid() );
    // 	pstmt.setLong( 2, se.getOrgid() );
    // 	pstmt.setString( 3, visitDesc );
    // 	pstmt.setString( 4, se.getCycle() );
    // 	pstmt.setInt( 5, se.getDay() );
    // 	pstmt.setFloat( 6, se.getHour() );
    // 	pstmt.setString( 7, se.getDosage() );
    // 	pstmt.setFloat( 8, se.getQuantity() );
    // 	pstmt.setString( 9, se.getUnit() );

    // 	pstmt.executeUpdate();

    // 	return se;
    // }

    /**
     * Creates a new shipment event.
     *
     * @param organization the sender organization.
     * @param eventName the event name.
     * @param desc the logistics description.
     *
     * @return the <code>SampleEvent</code> object.
     */
    // public SampleEvent createLogisticsEvent( Organization org, String eventName, String desc ) throws SQLException {
    // 	String dDesc = Stringx.getDefault(desc,"").trim();
    // 	SampleEvent[] events = findSiteEvent( org, eventName );
    // 	if( events.length > 0 ) {
    // 	    for( int i = 0; i < events.length; i++ ) {
    // 		String sDesc = Stringx.getDefault(events[i].getDosage(),"").trim();
    // 		if( sDesc.equals(dDesc) )
    // 		    return events[i];
    // 	    }
    // 	}
	
    // 	SampleEvent se = SampleEvent.createLogistics( eventName, dDesc );
    // 	se.setOrgid( org.getOrgid() );

    // 	PreparedStatement pstmt = getStatement( STMT_EVENT_INSERT );
    // 	pstmt.setLong( 1, se.getTimeid() );
    // 	pstmt.setLong( 2, se.getOrgid() );
    // 	pstmt.setString( 3, se.getVisit() );
    // 	pstmt.setString( 4, se.getCycle() );
    // 	pstmt.setInt( 5, se.getDay() );
    // 	pstmt.setFloat( 6, se.getHour() );
    // 	pstmt.setString( 7, se.getDosage() );
    // 	pstmt.setFloat( 8, se.getQuantity() );
    // 	pstmt.setString( 9, se.getUnit() );

    // 	pstmt.executeUpdate();

    // 	return se;
    // }

    /**
     * Creates a new shipment event.
     *
     * @param organization the sender organization.
     * @param desc the shipment description.
     *
     * @return the <code>SampleEvent</code> object.
     */
    // public SampleEvent createShipmentEvent( Organization org, String desc ) throws SQLException {
    // 	return createLogisticsEvent( org, SampleEvent.SHIPMENT, desc );
    // }

    /**
     * Creates a new reception event.
     *
     * @param organization the receiving organization.
     * @param desc the reception description.
     *
     * @return the <code>SampleEvent</code> object.
     */
    // public SampleEvent createReceiverEvent( Organization org, String desc ) throws SQLException {
    // 	return createLogisticsEvent( org, SampleEvent.RECEIVED, desc );
    // }

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    // public SampleProcess[] findSampleProcess( SampleEvent event, Sample sample, long treatid ) throws SQLException {
    // 	PreparedStatement pstmt = getStatement( STMT_PROCESS_BY_EVENT );
    // 	pstmt.setString( 1, sample.getSampleid() );
    // 	pstmt.setLong( 2, treatid );
    // 	pstmt.setLong( 3, event.getTimeid() );

    //  	ResultSet res = pstmt.executeQuery();
    //  	List<SampleProcess> fl = new ArrayList<SampleProcess>();
    //  	Iterator it = TableUtils.toObjects( res, new SampleProcess() );
    // 	while( it.hasNext() ) {
    // 	    fl.add( (SampleProcess)it.next() );
    // 	}	       
    // 	res.close();

    //  	SampleProcess[] facs = new SampleProcess[ fl.size() ];
    //  	return (SampleProcess[])fl.toArray( facs );
    // }

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    // public SampleProcess findCollectionProcess( SampleEvent event, Sample sample ) throws SQLException {
    // 	SampleProcess[] collEvents = findSampleProcess( event, sample, SampleProcess.TREATID_COLLECTION );
    // 	if( collEvents.length <= 0 )
    // 	    return null;
    // 	return collEvents[0];
    // }

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    // public SampleProcess findVisit( Sample sample ) throws SQLException {
    // 	PreparedStatement pstmt = getStatement( STMT_PROCESS_BY_VISIT );
    // 	pstmt.setString( 1, sample.getSampleid() );
    // 	pstmt.setLong( 2, SampleProcess.TREATID_COLLECTION );

    //  	ResultSet res = pstmt.executeQuery();
    //  	SampleProcess sType = null;
    //  	if( res.next() ) 
    //  	    sType = (SampleProcess)TableUtils.toObject( res, new SampleProcess() );
    //  	res.close();

    // 	return sType;
    // }
    
    /**
     * Assign a sample to a colletion event.
     *
     * @param userId the user id.
     * @param event the sample event.
     * @param sample the sample.
     * @param collDate the collection date.
     * @param dtFormat the date format.
     *
     * @return the <code>SampleEvent</code> object.
     */
    // public SampleProcess assignCollectionEvent( long userId, 
    // 						SampleEvent event, 
    // 						Sample sample,
    // 						String collDate,
    // 						String dtFormat ) 
    // 	throws SQLException {

    // 	SampleProcess collEvent = findCollectionProcess( event, sample );
    // 	SampleProcess prevEvent = null;

    // 	if( collEvent == null ) {
    // 	    log.debug( "Parse collection string: "+collDate );
    // 	    collEvent = SampleProcess.parseCollection( dtFormat, collDate );
    // 	    collEvent.setSampleid( sample.getSampleid() );
    // 	    collEvent.setTimeid( event.getTimeid() );
    // 	}
    // 	else {
    // 	    try {
    // 		prevEvent = (SampleProcess)collEvent.copy();
    // 	    }
    // 	    catch( CloneNotSupportedException cne ) {
    // 		log.error( cne );
    // 	    }
    // 	    collEvent.initProcessed( dtFormat, collDate );
    // 	}

    // 	collEvent.updateTrackid();

    // 	if( (prevEvent != null) && (prevEvent.equals( collEvent )) ) {
    // 	    log.warn( "Nothing to be updated. Collection event exists already" );
    // 	    return collEvent;
    // 	}

    //  	PreparedStatement pstmt = null;
    //  	int nn = 4;
    //  	if( prevEvent == null ) {
    //  	    pstmt = getStatement( STMT_PROCESS_INSERT );
    //  	    pstmt.setString( 1, collEvent.getSampleid() );
    //  	    pstmt.setLong( 2, collEvent.getTreatid() );
    //  	    pstmt.setLong( 3, collEvent.getTimeid() );
    // 	    log.debug( "Creating new collection event: "+collEvent+" sample: "+sample );
    //  	}
    //  	else {
    //  	    pstmt = getStatement( STMT_PROCESS_UPDATE );
    //  	    pstmt.setString( 4, collEvent.getSampleid() );
    //  	    pstmt.setLong( 5, collEvent.getTreatid() );
    //  	    pstmt.setLong( 6, collEvent.getTimeid() );
    //  	    nn = 1;
    // 	    log.debug( "Updating existing collection event: "+collEvent+" sample: "+sample );
    //  	}
	
    //  	pstmt.setInt( nn, collEvent.getStep() );
    //  	nn++;

    //  	pstmt.setTimestamp( nn, collEvent.getProcessed() );
    //  	nn++;

    // 	pstmt.setLong( nn, collEvent.getTrackid() );
    //  	nn++;

    //  	pstmt.executeUpdate();

    // 	trackChange( prevEvent, collEvent, userId, "Sample collection event "+((prevEvent==null)?"assigned":"updated"), null );
    // 	return collEvent;
    // }

    /**
     * Returns a list of treatments.
     *
     * @param treatment the treatment to search.
     *
     * @return the Treatment objects.
     */
    // public Treatment[] findTreatment( String treatment ) throws SQLException {
    // 	PreparedStatement pstmt = getStatement( STMT_TREAT_BY_NAME );
    // 	pstmt.setString( 1, treatment );
    // 	pstmt.setString( 2, treatment );

    //  	ResultSet res = pstmt.executeQuery();
    //  	List<Treatment> fl = new ArrayList<Treatment>();
    //  	Iterator it = TableUtils.toObjects( res, new Treatment() );
    // 	while( it.hasNext() ) {
    // 	    fl.add( (Treatment)it.next() );
    // 	}	       
    // 	res.close();

    //  	Treatment[] facs = new Treatment[ fl.size() ];
    //  	return (Treatment[])fl.toArray( facs );	
    // }

    /**
     * Creates a new treatment entry.
     *
     * @param treatment the treatment name.
     * @param desc the treatment description.
     *
     * @return the <code>Treatment</code> object.
     */
    // public Treatment createTreatment( String treatment, String desc ) throws SQLException {
    // 	Treatment treat = new Treatment();
    // 	treat.setTreatment( treatment );
    // 	treat.setTreatdesc( desc );

    // 	PreparedStatement pstmt = getStatement( STMT_TREAT_INSERT );
    // 	pstmt.setLong( 1, treat.getTreatid() );
    // 	pstmt.setString( 2, treat.getTreatment() );
    // 	pstmt.setString( 3, treat.getTreatdesc() );

    // 	pstmt.executeUpdate();

    // 	return treat;
    // }

    /**
     * Returns the site where the sample has been taken.
     *
     * @param sampleid The sample id.
     * @return the Orgainzation or clinical site the sample has been taken.
     */
    // public Organization findCollectionSite( String sampleid ) throws SQLException {
    // 	PreparedStatement pstmt = getStatement( STMT_ORG_COLLECT );
    //  	pstmt.setString( 1, sampleid );
    //  	ResultSet res = pstmt.executeQuery();

    //  	Organization sType = null;
    //  	if( res.next() ) 
    //  	    sType = (Organization)TableUtils.toObject( res, new Organization() );
    //  	res.close();
    //  	return sType;
    // }

    /**
     * Assign a sample to a colletion event.
     *
     * @param userId the user id.
     * @param event the sample event.
     * @param sample the sample.
     * @param collDate the collection date.
     * @param dtFormat the date format.
     *
     * @return the <code>SampleEvent</code> object.
     */
    // public SampleProcess assignTreatment( long userId, 
    // 					  Sample sample,
    // 					  Treatment treatment )
    // 	throws SQLException {

    // 	// find the collection site

    // 	Organization org = findCollectionSite( sample.getSampleid() );
    // 	if( org == null )
    // 	    org = findOrganizationById( -2L );  // generic Merck Serono sponsor

    // 	// check if medication event exists

    // 	SampleEvent[] medicEvents = findSiteEvent( org, "study medication" );
    // 	SampleEvent medicEvent = null;
    // 	if( medicEvents.length > 0 ) {
    // 	    medicEvent = medicEvents[0];
    // 	}
    // 	else {
    // 	    medicEvent = createSiteEvent( org, "study medication" );
    // 	    log.debug( "New study medication event created for site "+org );
    // 	}

    // 	// Test if the treatment has been assigned already

    // 	SampleProcess[] mProcs = findSampleProcess( medicEvent, sample, treatment.getTreatid() );
    // 	if( mProcs.length > 0 ) 
    // 	    return mProcs[0];

    // 	// create sample processing

    // 	SampleProcess mProc = new SampleProcess();
    // 	mProc.setSampleid( sample.getSampleid() );
    // 	mProc.setTreatid( treatment.getTreatid() );
    // 	mProc.setTimeid( medicEvent.getTimeid() );
    // 	mProc.setStep( 100 );
    // 	mProc.setProcessed( new Timestamp( SampleProcess.MISSING_DATETIME ) );

    // 	mProc.updateTrackid();

    //  	PreparedStatement pstmt = getStatement( STMT_PROCESS_INSERT );
    // 	pstmt.setString( 1, mProc.getSampleid() );
    // 	pstmt.setLong( 2, mProc.getTreatid() );
    // 	pstmt.setLong( 3, mProc.getTimeid() );
    //  	pstmt.setInt( 4, mProc.getStep() );
    //  	pstmt.setTimestamp( 5, mProc.getProcessed() );
    // 	pstmt.setLong( 6, mProc.getTrackid() );

    //  	pstmt.executeUpdate();

    // 	trackChange( null, mProc, userId, "Sample medication event assigned", null );
    // 	return mProc;
    // }

    /**
     * Assign a sample to a shipment event.
     *
     * @param userId the user id.
     * @param event the sample event.
     * @param sample the sample.
     * @param collDate the collection date.
     * @param dtFormat the date format.
     *
     * @return the <code>SampleEvent</code> object.
     */
    // public SampleProcess assignShipmentEvent( long userId, 
    // 					      SampleEvent event, 
    // 					      Sample sample,
    // 					      String evDate,
    // 					      String dtFormat ) 
    // 	throws SQLException {

    // 	SampleProcess shipProc = SampleProcess.parseShipment( dtFormat, evDate );
    // 	shipProc.setSampleid( sample.getSampleid() );
    // 	shipProc.setTimeid( event.getTimeid() );
    // 	shipProc.updateTrackid();

    // 	SampleProcess[] shipped = findSampleProcess( event, sample, SampleProcess.TREATID_PACKAGED );
    // 	for( int i = 0; i < shipped.length; i++ ) {
    // 	    if( shipped[i].equals( shipProc ) ) {
    // 		log.warn( "Nothing to be updated. Shipment at "+shipProc+" is existing already" );
    // 		return shipped[i];
    // 	    }
    // 	}

    // 	PreparedStatement pstmt = getStatement( STMT_PROCESS_INSERT );
    // 	pstmt.setString( 1, shipProc.getSampleid() );
    // 	pstmt.setLong( 2, shipProc.getTreatid() );
    // 	pstmt.setLong( 3, shipProc.getTimeid() );
    //  	pstmt.setInt( 4, shipProc.getStep() );
    //  	pstmt.setTimestamp( 5, shipProc.getProcessed() );
    // 	pstmt.setLong( 6, shipProc.getTrackid() );

    //  	pstmt.executeUpdate();
    // 	// log.debug( "Creating new shipment event: "+shipProc+" sample: "+sample );

    // 	trackChange( null, shipProc, userId, "Sample shipment event assigned", null );
    // 	return shipProc;
    // }

    /**
     * Assign a sample to a receiver event.
     *
     * @param userId the user id.
     * @param event the sample event.
     * @param sample the sample.
     * @param collDate the collection date.
     * @param dtFormat the date format.
     *
     * @return the <code>SampleEvent</code> object.
     */
    // public SampleProcess assignReceiverEvent( long userId, 
    // 					      SampleEvent event, 
    // 					      Sample sample,
    // 					      String evDate,
    // 					      String dtFormat ) 
    // 	throws SQLException {

    // 	SampleProcess shipProc = SampleProcess.parseReceipt( dtFormat, evDate );
    // 	shipProc.setSampleid( sample.getSampleid() );
    // 	shipProc.setTimeid( event.getTimeid() );
    // 	shipProc.updateTrackid();

    // 	SampleProcess[] shipped = findSampleProcess( event, sample, SampleProcess.TREATID_UNPACKAGED );
    // 	for( int i = 0; i < shipped.length; i++ ) {
    // 	    if( shipped[i].equals( shipProc ) ) {
    // 		log.warn( "Nothing to be updated. Reception at "+shipProc+" is existing already" );
    // 		return shipped[i];
    // 	    }
    // 	}

    // 	PreparedStatement pstmt = getStatement( STMT_PROCESS_INSERT );
    // 	pstmt.setString( 1, shipProc.getSampleid() );
    // 	pstmt.setLong( 2, shipProc.getTreatid() );
    // 	pstmt.setLong( 3, shipProc.getTimeid() );
    //  	pstmt.setInt( 4, shipProc.getStep() );
    //  	pstmt.setTimestamp( 5, shipProc.getProcessed() );
    // 	pstmt.setLong( 6, shipProc.getTrackid() );

    //  	pstmt.executeUpdate();
    // 	// log.debug( "Creating new shipment event: "+shipProc+" sample: "+sample );

    // 	trackChange( null, shipProc, userId, "Sample reception event assigned", null );
    // 	return shipProc;
    // }

    /**
     * Clean up resources occupied by this DAO.
     *
     */
    public synchronized void close() {
	Iterator<PreparedStatement> it = statements.values().iterator();
	while( it.hasNext() ) {
	    PreparedStatement pstmt = it.next();
	    try {
		pstmt.close();
	    }
	    catch( SQLException sqe ) {
		// we don't care...
	    }
	}
	statements.clear();
	if( dbSource != null ) {
	    try {
		dbSource.close();
	    }
	    catch( SQLException sqe ) {
		// we don't care...
	    }
	    dbSource = null;
	}
	templateConfig = null;
	log.debug( "Data access object cleaned" );
    }

}
