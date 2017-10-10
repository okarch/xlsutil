package com.emd.xlsutil.rest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;

import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.emd.xlsutil.dao.DatasetDAO;

import com.emd.xlsutil.dataset.Roles;
import com.emd.xlsutil.dataset.User;

import com.emd.xlsutil.upload.UploadBatch;
import com.emd.xlsutil.upload.UploadException;
import com.emd.xlsutil.upload.UploadOutput;
import com.emd.xlsutil.upload.UploadProcessor;
import com.emd.xlsutil.upload.UploadTemplate;

import com.emd.io.WriterOutputStream;

import com.emd.util.Stringx;
import com.emd.util.ZipCoder;

/**
 * <code>XlsUtilController</code> implements the REST controller of the xls tool.
 *
 * Created: Fri Sep  6 17:49:26 2016
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
@RestController
public class XlsUtilController {

    private static Log log = LogFactory.getLog(XlsUtilController.class);


    // @RequestMapping("/sample")
    // public Sample getSamples(@RequestParam(value="type", defaultValue="blood") String type) {
    // 	SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
    // 	SampleType sType = SampleType.getInstance( type );
    //     return Sample.getInstance( sType );
    // }

    // @RequestMapping("/sample/type")
    // public SampleType[] getSampleTypes(@RequestParam(value="name", defaultValue="") String name) {
    // 	SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	
    // 	try {
    // 	    SampleType[] tList = sInv.findSampleTypeByNameAll( name );
    // 	    log.debug( "Number of sample types matching \""+name+"\": "+tList.length );
    // 	    return tList;
    // 	}
    // 	catch( SQLException sqe ) {
    // 	    log.error( sqe );
    // 	    throw new SampleInventoryException( sqe );
    // 	}
    // }

    // @RequestMapping( value = "/sample/type/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    // public SampleType createSampleType(@RequestBody SampleType input) {
    // // ResponseEntity<?> registerTemplate(@RequestBody InventoryUploadTemplate input) {

    // 	DatasetDAO sInv = DatasetDAO.getInstance();
    // 	try {
    // 	    log.debug( "Creating sample type: "+input );
    // 	    SampleType newT = sInv.createSampleType( input.getTypename() );
    // 	    return newT;
    // 	}
    // 	catch( SQLException sqe ) {
    // 	    log.error( sqe );
    // 	    throw new SampleInventoryException( sqe );
    // 	}
	    
    //     // return accountRepository.findByUsername(userId)
    //     //         .map(account -> {
    //     //                     Bookmark bookmark = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

    //     //                     HttpHeaders httpHeaders = new HttpHeaders();

    //     //                     Link forOneBookmark = new BookmarkResource(bookmark).getLink("self");
    //     //                     httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

    //     //                     return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    //     //                 }
    //     //         ).get();
    // }

    @RequestMapping("/template")
    public UploadTemplate[] getUploadTemplates(@RequestParam(value="name", defaultValue="") String name) {
	DatasetDAO sInv = DatasetDAO.getInstance();
	
	try {
	    UploadTemplate[] tList = sInv.findTemplateByName( name );
	    log.debug( "Number of templates matching \""+name+"\": "+tList.length );
	    return tList;
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new XlsRestException( sqe );
	}
    }

    @RequestMapping( value = "/template/{apiKey}/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    public UploadTemplate registerTemplate( @PathVariable String apiKey,
					    @RequestBody UploadTemplate input) {
    // ResponseEntity<?> registerTemplate(@RequestBody UploadTemplate input) {

	User user = validateApiKey( apiKey, Roles.TEMPLATE_CREATE );

	DatasetDAO sInv = DatasetDAO.getInstance();
	try {
	    log.debug( "Creating inventory upload template: "+input );
	    UploadTemplate newT = sInv.storeTemplate( user.getUserid(), input );
	    return newT;
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new XlsRestException( sqe );
	}
	    
        // return accountRepository.findByUsername(userId)
        //         .map(account -> {
        //                     Bookmark bookmark = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

        //                     HttpHeaders httpHeaders = new HttpHeaders();

        //                     Link forOneBookmark = new BookmarkResource(bookmark).getLink("self");
        //                     httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

        //                     return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
        //                 }
        //         ).get();
    }

    @RequestMapping( value = "/template/{apiKey}/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    public UploadTemplate updateTemplate( @PathVariable String apiKey, 
					  @RequestBody UploadTemplate input) {

	User user = validateApiKey( apiKey, Roles.TEMPLATE_UPDATE );

	DatasetDAO sInv = DatasetDAO.getInstance();
	try {
	    log.debug( "Updating inventory upload template: "+input );
	    UploadTemplate newT = sInv.updateTemplateByName( user.getUserid(), input );
	    return newT;
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new XlsRestException( sqe );
	}
	    
        // return accountRepository.findByUsername(userId)
        //         .map(account -> {
        //                     Bookmark bookmark = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

        //                     HttpHeaders httpHeaders = new HttpHeaders();

        //                     Link forOneBookmark = new BookmarkResource(bookmark).getLink("self");
        //                     httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

        //                     return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
        //                 }
        //         ).get();
    }

    private File createTempDir() 
	throws IOException {

	File tempF = File.createTempFile( "deleteme", null );
	File wsDir = new File( tempF.getParentFile(), "upd-"+UUID.randomUUID().toString() );
	if( !wsDir.mkdir() ) {
	    log.error( "Cannot create folder: "+wsDir );
	    tempF.delete();
	    throw new IOException( "Cannot create temp folder" );
	}
	return wsDir;
    }

    @RequestMapping(value = "/output/{outputId}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getOutputFile(@PathVariable("outputId") String outputId ) {

     	DatasetDAO sInv = DatasetDAO.getInstance();
	UploadOutput updOut = null;

	// try to retrieve via outputid first

	long outId = Stringx.toLong( outputId, 0L );
	if( outId != 0L ) {
	    log.info( "Lookup output using id: "+outId );
	    try {
		updOut = sInv.findOutputById( outId );
	    }
	    catch( SQLException sqe ) {
		log.error( sqe );
		throw new XlsRestException( sqe );
	    }
	}

	// try to retrieve using md5sum

	if( updOut == null ) {
	    log.info( "Lookup output using md5sum: "+outputId );
	    try {
		UploadOutput[] upds = sInv.findOutputByChecksum( outputId );
		if( upds.length > 0 ) 
		    updOut = upds[0];
	    }
	    catch( SQLException sqe ) {
		log.error( sqe );
		throw new XlsRestException( sqe );
	    }
	}

	// try to retrieve using file name

	if( updOut == null ) {
	    log.info( "Lookup output using file name: "+outputId );
	    try {
		UploadOutput[] upds = sInv.findOutputByFilename( outputId );
		if( upds.length > 0 ) 
		    updOut = upds[0];
		    
	    }
	    catch( SQLException sqe ) {
		log.error( sqe );
		throw new XlsRestException( sqe );
	    }
	}

	if( updOut == null ) {
	    String msg = "Cannot retrieve output for "+outputId;
	    log.error( msg );
	    throw new XlsRestException( msg );
	}

	File destF = null;
	try {
	    destF = updOut.writeOutputFile( createTempDir() );
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	    throw new XlsRestException( ioe );
	}
	return new FileSystemResource( destF );
    }

    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
	return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping( value = "/fileset/{apiKey}/create/{filesetName}", method = RequestMethod.GET )
    public UploadTemplate createFileset( @PathVariable String apiKey,
					 @PathVariable String filesetName ) { 

	User user = validateApiKey( apiKey, Roles.INVENTORY_UPLOAD );

     	// find template

     	DatasetDAO sInv = DatasetDAO.getInstance();
     	UploadTemplate templ = null;
     	try {
     	    UploadTemplate[] tList = sInv.findTemplateByName( UploadTemplate.FILESET_CREATE );
     	    if( tList.length <= 0 )
     		throw new XlsRestException( "Unknown template: \""+UploadTemplate.FILESET_CREATE+"\"" );
     	    templ = tList[0];
     	    log.info( "Start upload using template: "+templ.getTemplatename() );
	}
     	catch( SQLException sqe ) {
     	    log.error( sqe );
     	    throw new XlsRestException( sqe );
     	}

     	// create the fileset if it is not existing already

	String fName = Stringx.getDefault( filesetName, "fileset_"+System.currentTimeMillis() );

     	UploadBatch uBatch = null;
     	try {
     	    uBatch = sInv.findFilesetByName( fName );
     	}
     	catch( SQLException sqe ) {
     	    log.error( sqe );
     	    throw new XlsRestException( sqe );
     	}

	if( uBatch != null ) {
	    log.debug( "Fileset is existing already: "+fName+" Checksum: "+uBatch.getMd5sum() );
	}
	else {
	    log.debug( "Creating fileset: "+fName );
	    try {
		uBatch = sInv.createUploadBatch( user.getUserid(), templ );
	    }
	    catch( SQLException sqe ) {
		log.error( sqe );
		throw new XlsRestException( sqe );
	    }

	    // register folder content as file
	    
	    uBatch.setFilename( fName );
	    uBatch.setMime( UploadBatch.FILESET_MIME );
	    
	    try {
		uBatch.writeFileset();
		log.debug( "Fileset content created. Checksum: "+uBatch.getMd5sum() );
	    } 
	    catch( IOException e ) {
		log.error( e );
		throw new XlsRestException( "Upload failed: "+Stringx.getDefault( e.getMessage(), "unknown reason" ) );
	    }

	    // register fileset with the template

	    templ.addUploadBatch( uBatch );

	    try {
		templ = sInv.storeTemplate( user.getUserid(), templ );
	    }
	    catch( SQLException sqe ) {
		log.error( sqe );
		throw new XlsRestException( sqe );
	    }

	    log.info( "Fileset registered: "+uBatch.getUploadid() );
	}

     	// launch upload process

     	UploadProcessor uProcessor = UploadProcessor.getInstance();
     	try {

	    Map ctxt = new HashMap();
	    ctxt.put( "user", user );

     	    uProcessor.processUpload( templ, uBatch.getUploadid(), ctxt );
     	}
     	catch( UploadException sqe ) {
     	    log.error( sqe );
     	    throw new XlsRestException( sqe );
     	}
	
     	return templ;
    }

    @RequestMapping(value="/upload/{apiKey}/{templatename}", method=RequestMethod.POST)
    public UploadTemplate handleFileUpload( @PathVariable String apiKey,
					    @PathVariable String templatename, 
					    @RequestParam("file") MultipartFile file) {

	User user = validateApiKey( apiKey, Roles.INVENTORY_UPLOAD );

     	// find template

     	DatasetDAO sInv = DatasetDAO.getInstance();
     	UploadTemplate templ = null;
     	try {
     	    UploadTemplate[] tList = sInv.findTemplateByName( templatename );
     	    if( tList.length <= 0 )
     		throw new XlsRestException( "Unknown template: \""+templatename+"\"" );
     	    templ = tList[0];
     	    log.info( "Start upload using template: "+templ.getTemplatename() );
	}
     	catch( SQLException sqe ) {
     	    log.error( sqe );
     	    throw new XlsRestException( sqe );
     	}

     	// upload the file

     	String updContent = null;
	String fName = Stringx.getDefault(file.getOriginalFilename(), "" );
	String fType = Stringx.getDefault(file.getContentType(), "" );
	log.debug( "Uploading content file: "+fType+" "+fName );

     	// register upload with the template

     	UploadBatch uBatch = null;
     	try {
     	    uBatch = sInv.createUploadBatch( user.getUserid(), templ );
     	}
     	catch( SQLException sqe ) {
     	    log.error( sqe );
     	    throw new XlsRestException( sqe );
     	}

	uBatch.setFilename( fName );
	uBatch.setMime( fType );

	if (!file.isEmpty()) {
	    try {
		InputStream ins = file.getInputStream(); 	    
		uBatch.readUploadContent( ins );
		ins.close();
		log.debug( "Content uploaded. Checksum: "+uBatch.getMd5sum() );
	    } 
     	    catch( IOException e ) {
     		log.error( e );
		throw new XlsRestException( "Upload failed: "+Stringx.getDefault( e.getMessage(), "unknown reason" ) );
	    }
	} 
     	else {
     	    throw new XlsRestException( "Upload failed: empty file" );
	}

     	// register upload with the template

     	templ.addUploadBatch( uBatch );

     	try {
     	    templ = sInv.storeTemplate( user.getUserid(), templ );
     	}
     	catch( SQLException sqe ) {
     	    log.error( sqe );
     	    throw new XlsRestException( sqe );
     	}

     	log.info( "Upload batch registered: "+uBatch.getUploadid() );
	
     	// launch upload process

     	UploadProcessor uProcessor = UploadProcessor.getInstance();
     	try {

	    Map ctxt = new HashMap();
	    ctxt.put( "user", user );

     	    uProcessor.processUpload( templ, uBatch.getUploadid(), ctxt );
     	}
     	catch( UploadException sqe ) {
     	    log.error( sqe );
     	    throw new XlsRestException( sqe );
     	}
	
     	return templ;
    }

    // @RequestMapping( value = "/cost/estimate/add/{estimateId}/{itemCount}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    // public CostEstimate addCostItem( @PathVariable long estimateId,
    // 				     @PathVariable long itemCount, 
    // 				     @RequestBody CostSample item ) {

    // 	DatasetDAO sInv = DatasetDAO.getInstance();
    // 	try {
    // 	    CostEstimate estimate = sInv.addCostItem( estimateId, item, itemCount );
    // 	    log.debug( "Added costs to estimate. Update: "+estimate );
    // 	    return estimate;
    // 	}
    // 	catch( SQLException sqe ) {
    // 	    log.error( sqe );
    // 	    throw new XlsRestException( sqe );
    // 	}
    // }


    private void validateUser(String userId) {
	
	throw new UserNotFoundException(userId);
    }

    private User validateApiKey( String apiKey, long reqRole ) {
     	DatasetDAO sInv = DatasetDAO.getInstance();
	User user = null;
     	try {
	    user = sInv.findUserByApikey( apiKey );	    
     	}
     	catch( SQLException sqe ) {
     	    log.error( sqe );
     	}

	if( user == null )
	    throw new UserNotFoundException( "Cannot access user information" );

	if( !user.hasRole( reqRole ) )
	    throw new UserNotFoundException( "User "+user+" requires "+Roles.roleToString( reqRole ) );
	return user;
    }

}
