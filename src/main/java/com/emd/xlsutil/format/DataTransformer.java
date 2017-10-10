package com.emd.xlsutil.format;

import org.apache.poi.ss.usermodel.Cell;

/**
 * <code>DataTransformer</code> specifies implementations of data transforming strategies.
 *
 * Created: Fri Apr 21 16:05:17 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public interface DataTransformer {

    /**
     * Formats a string value.
     * 
     * @param cell the cell to be formatted.
     * @param defaultVal the default value.
     *
     * @return a formatted string value.
     */
    public String format( Cell cell, String defaultVal );

    /**
     * Returns the format used to format the output.
     *
     * @return a string characterizing the output format, e.g. "csv".
     */
    public String getOutputFormat();
}
