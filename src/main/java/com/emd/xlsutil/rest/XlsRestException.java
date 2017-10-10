package com.emd.xlsutil.rest;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <code>XlsRestEception</code> signals invalid conditions while processing REST requests.
 *
 * Created: Mon Sep 26 12:50:19 2016
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class XlsRestException extends RuntimeException {

    public XlsRestException( Throwable t ) {
	super( t );
    }
    public XlsRestException( String msg ) {
	super( msg );
    }

}

