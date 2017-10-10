package com.emd.xlsutil.format;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;


/**
 * <code>DefaultTransformer</code> implements an Excel default formatter based on POI.
 *
 * Created: Fri Apr 21 16:05:17 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class DefaultTransformer implements DataTransformer {
    private DataFormatter dataFormatter;
    private String outputFormat;
    private TransformerLog logger;
    private FormulaEvaluator formulaEvaluator;

    private int precision;

    private boolean round;

    private static Log log = LogFactory.getLog(DefaultTransformer.class);

    public DefaultTransformer() {
	this.dataFormatter = new DataFormatter();
	this.logger = new TransformerLog();
	this.formulaEvaluator = null;
	this.precision = -1;
	this.round = true;
    }

    private void logReplace( Cell cell, int nTimes, String repl, String byStr ) {
	StringBuilder stb = new StringBuilder();
	stb.append( nTimes );
	stb.append( " occurence" );
	if( nTimes > 1 )
	    stb.append( "s" );
	stb.append( " of " );
	stb.append( repl );
	if( nTimes > 1 )
	    stb.append( " have " );
	else
	    stb.append( " has " );
	stb.append( "been replaced by " );
	stb.append( "\"" );
	stb.append( byStr );
	stb.append( "\"" );
	logger.warn( cell, stb.toString() );
    }

    private String replaceNonPrintable( Cell cell, String textVal ) {
	int nn = StringUtils.countMatches( textVal, "\r\n" );
	if( nn > 0 ) {
	    textVal = StringUtils.replace( textVal, "\r\n", ";" );
	    logReplace( cell, nn, "CRLF", ";" );
	}
	nn = StringUtils.countMatches( textVal, "\n" );
	if( nn > 0 ) {
	    textVal = StringUtils.replace( textVal, "\n", ";" );
	    logReplace( cell, nn, "CR", ";" );
	}
	nn = StringUtils.countMatches( textVal, "\r" );
	if( nn > 0 ) {
	    textVal = StringUtils.replace( textVal, "\r", ";" );
	    logReplace( cell, nn, "LF", ";" );
	}
	nn = StringUtils.countMatches( textVal, "\t" );
	if( nn > 0 ) {
	    textVal = StringUtils.replace( textVal, "\t", " " );
	    logReplace( cell, nn, "TAB", " " );
	}
	return textVal;
    }

    private FormulaEvaluator getFormulaEvaluator( Cell cell ) {
	if( formulaEvaluator == null ) {
	    formulaEvaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
	    log.debug( "Formula evaluator: "+formulaEvaluator );
	}
	return formulaEvaluator;
    }

    private boolean requireRounding( double val ) {
	return ((isRound()) && ((Math.abs( val - Math.rint(val) )) <= 0d));
    }
    
    private String evaluateFormula( Cell cell ) {
	FormulaEvaluator eval = getFormulaEvaluator( cell );
	CellValue cVal = eval.evaluate( cell );

	log.debug( "Evaluating formula of cell "+cell+" result is "+cVal );

        if( CellType.ERROR.equals(cVal.getCellTypeEnum()) ) {
	    logger.error( cell, "Error while evaluating formula" );
	    return null;
	}

	if( CellType.STRING.equals(cVal.getCellTypeEnum()) )
	    return cVal.getStringValue();

	if( CellType.NUMERIC.equals(cVal.getCellTypeEnum()) ) {
	    if( requireRounding(cVal.getNumberValue()) ) {
		logger.info( cell, "Formula result has been rounded" ); 
		return String.valueOf(Math.round(cVal.getNumberValue()));
	    }
	    return String.valueOf(cVal.getNumberValue());
	}

	if( CellType.BOOLEAN.equals(cVal.getCellTypeEnum()) )
	    return String.valueOf(cVal.getBooleanValue());

	return null;
    }

    /**
     * Formats a string value.
     * 
     * @param cell the cell to be formatted.
     * @param defaultVal the default value.
     *
     * @return a formatted string value.
     */
    public String format( Cell cell, String defaultVal ) {
	if( cell == null )
	    return defaultVal;
	CellType ct = cell.getCellTypeEnum();
	String textVal = null;
	if( CellType.FORMULA.equals( ct ) ) 
	    textVal = evaluateFormula( cell );
	else
	    textVal = dataFormatter.formatCellValue( cell );
	if( textVal == null ) {
	    logger.warn( cell, "Cell value cannot be retrieved. Default value \""+defaultVal+"\" has been applied." );
	    return defaultVal;
	}
	textVal = replaceNonPrintable( cell, textVal );
	return textVal;
    }

    /**
     * Returns the complete log of transformations.
     *
     * @return the <code>TransformerLog</code> instance.
     */
    public TransformerLog getLogger() {
	return logger;
    }

    /**
     * Returns the format used to format the output.
     *
     * @return a string characterizing the output format, e.g. "csv".
     */
    // public String getOutputFormat();
    public final String getOutputFormat() {
	return outputFormat;
    }

    /**
     * Set the <code>OutputFormat</code> value.
     *
     * @param outputFormat The new OutputFormat value.
     */
    public final void setOutputFormat(final String outputFormat) {
	this.outputFormat = outputFormat;
    }

    /**
     * Get the <code>Precision</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getPrecision() {
	return precision;
    }

    /**
     * Set the <code>Precision</code> value.
     *
     * @param precision The new Precision value.
     */
    public final void setPrecision(final int precision) {
	this.precision = precision;
    }

    /**
     * Get the <code>Round</code> value.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean isRound() {
	return round;
    }

    /**
     * Set the <code>Round</code> value.
     *
     * @param round The new Round value.
     */
    public final void setRound(final boolean round) {
	this.round = round;
    }

// switch (cell.getCellTypeEnum()) {
//                 case CellType.STRING:
//                     System.out.println(cell.getRichStringCellValue().getString());
//                     break;
//                 case CellType.NUMERIC:
//                     if (DateUtil.isCellDateFormatted(cell)) {
//                         System.out.println(cell.getDateCellValue());
//                     } else {
//                         System.out.println(cell.getNumericCellValue());
//                     }
//                     break;
//                 case CellType.BOOLEAN:
//                     System.out.println(cell.getBooleanCellValue());
//                     break;
//                 case CellType.FORMULA:
//                     System.out.println(cell.getCellFormula());
//                     break;
//                 case CellType.BLANK:
//                     System.out.println();
//                     break;
//                 default:
//                     System.out.println();

}
