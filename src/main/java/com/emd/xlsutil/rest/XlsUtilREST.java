package com.emd.xlsutil.rest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * <code>XlsUtilREST</code> provides web endpoint of the xls tool REST service.
 *
 * Created: Mon Sep 26 18:40:01 2016
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
@ComponentScan({"com.emd.xlsutil.rest"})
@EnableAutoConfiguration
public class XlsUtilREST {

    public static void main(String[] args) {
        SpringApplication.run(XlsUtilREST.class, args);
    }
}

