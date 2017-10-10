package com.emd.xlsutil.dts;

import java.util.ArrayList;
import java.util.List;

import com.emd.util.Stringx;

/**
 * <code>DataTransferMap</code> stores term mappings and controlled vocabulary.
 *
 * Created: Sat Aug 12 12:50:28 2017
 *
 * @author <a href="mailto:">Oliver</a>
 * @version 1.0
 */
public class DataTransferMap {
    private long   mapid;
    private long   dtsid;
    private String mapname;

    private List<DataTransferTerm> terms;
    
    public DataTransferMap( String mapName ) {
	this.mapname = mapName;
	this.terms = new ArrayList<DataTransferTerm>();
    }
    public DataTransferMap() {
	this( "" );
    }

    /**
     * Get the Mapid value.
     * @return the Mapid value.
     */
    public long getMapid() {
	return mapid;
    }

    /**
     * Set the Mapid value.
     * @param newMapid The new Mapid value.
     */
    public void setMapid(long newMapid) {
	this.mapid = newMapid;
    }

    /**
     * Get the Dtsid value.
     * @return the Dtsid value.
     */
    public long getDtsid() {
	return dtsid;
    }

    /**
     * Set the Dtsid value.
     * @param newDtsid The new Dtsid value.
     */
    public void setDtsid(long newDtsid) {
	this.dtsid = newDtsid;
    }

    /**
     * Get the Mapname value.
     * @return the Mapname value.
     */
    public String getMapname() {
	return Stringx.getDefault(mapname,"").trim();
    }

    /**
     * Set the Mapname value.
     * @param newMapname The new Mapname value.
     */
    public void setMapname(String newMapname) {
	this.mapname = newMapname;
    }

    /**
     * Creates a new term mapping by parsing the given line content.
     * @param line The mapping specification line.
     * @return the DataTransferTerm object created.
     */
    public DataTransferTerm addTermFromLine(String line) {
	DataTransferTerm term = DataTransferTerm.parse( line );
	terms.add( term );
	return term;
    }

    /**
     * Adds a new term mapping.
     * @param term The term mapping.
     * @return the DataTransferTerm object created.
     */
    public void addTerm( DataTransferTerm term ) {
	terms.add( term );
    }

    /**
     * Returns all term maps of the specification.
     *
     */
    public DataTransferTerm[] getTerms() {
	DataTransferTerm[] dTerms = new DataTransferTerm[terms.size()];
	return (DataTransferTerm[])terms.toArray( dTerms );
    }

}
