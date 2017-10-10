package com.emd.xlsutil.dataset;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.emd.util.Stringx;

/**
 * <code>AbstractVersionTrackable</code> adds functionality to version artifacts.
 *
 * Created: Wed Oct  4 07:56:16 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0 
 */
public class AbstractVersionTrackable extends AbstractTrackable {
    private long   stamp;
    private String version;

    protected AbstractVersionTrackable( String iType, String version  ) {
	super( iType );
	this.version = version;
	this.stamp = 0L;
	this.updateStamp();
    }

    /**
     * Get the <code>Version</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getVersion() {
	return version;
    }

    /**
     * Set the <code>Version</code> value.
     *
     * @param version The new Version value.
     */
    public final void setVersion(final String version) {
	this.version = version;
    }

    /**
     * Calculates the version stamp based on the version.
     *
     */
    public void updateStamp() {
	String ver = Stringx.getDefault(getVersion(),"").trim().toLowerCase();
	long oldStamp = getStamp();
	long nStamp = 0L;
	if( ver.length() > 0 ) {
	    ver = StringUtils.replaceChars( ver, " -;,:_#+*~!&/%", ".............." ); 
	    char[] verCC = ver.toCharArray();
	    boolean numAssigned = false;
	    List<Integer> verNums = new ArrayList<Integer>();
	    StringBuilder vNum = new StringBuilder();
	    for( int i = 0; i < verCC.length; i++ ) {
		if( verCC[i] == '.' ) {
		    verNums.add( new Integer(Stringx.toInt( vNum.toString(), 0 )) );
		    vNum.setLength(0);
		    numAssigned = false;
		}
		else if( Character.isDigit(verCC[i]) ) {
		    vNum.append( verCC[i] );
		}
		else if( Character.isLetter(verCC[i]) && !numAssigned  ) {
		    if( vNum.length() > 0 ) {
			verNums.add( new Integer(Stringx.toInt( vNum.toString(), 0 )) );
			vNum.setLength(0);
		    }
		    verNums.add( new Integer(Character.getNumericValue(verCC[i])) );
		    numAssigned = true;
		}
	    }
	    int nLen = String.valueOf( oldStamp ).length();
	    int exp = verNums.size();
	    if( exp < nLen )
		exp = nLen;
	    for( Integer nVer : verNums ) {
		nStamp = nStamp + (nVer.longValue()*(10L^(long)exp));
		exp--;
	    }
	}
	setStamp( nStamp );
    }

    /**
     * Get the <code>Stamp</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getStamp() {
	return stamp;
    }

    /**
     * Set the <code>Stamp</code> value.
     *
     * @param stamp The new Stamp value.
     */
    public final void setStamp(final long stamp) {
	this.stamp = stamp;
    }

}
