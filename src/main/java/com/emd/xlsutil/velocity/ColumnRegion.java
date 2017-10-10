package com.emd.xlsutil.velocity;

import org.apache.poi.ss.usermodel.CellType;

/**
 * <code>ColumnRegion</code> holds information about neighboring cells which are formatted using the same type.
 *
 * Created: Fri Mar  3 18:45:20 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class ColumnRegion {
    private int endRow;
    private int startRow;
    private int column;

    private CellType cellType;

    public ColumnRegion( int column,
			 int startRow,
			 int endRow,
			 CellType cellType ) {
	this.column = column;
	this.startRow = startRow;
	this.endRow = endRow;
	this.cellType = cellType;
    }

    /**
     * Get the <code>Column</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getColumn() {
	return column;
    }

    /**
     * Set the <code>Column</code> value.
     *
     * @param column The new Column value.
     */
    public final void setColumn(final int column) {
	this.column = column;
    }

    /**
     * Get the <code>StartRow</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getStartRow() {
	return startRow;
    }

    /**
     * Set the <code>StartRow</code> value.
     *
     * @param startRow The new StartRow value.
     */
    public final void setStartRow(final int startRow) {
	this.startRow = startRow;
    }

    /**
     * Get the <code>EndRow</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getEndRow() {
	return endRow;
    }

    /**
     * Set the <code>EndRow</code> value.
     *
     * @param endRow The new EndRow value.
     */
    public final void setEndRow(final int endRow) {
	this.endRow = endRow;
    }

    /**
     * Get the <code>CellType</code> value.
     *
     * @return a <code>CellType</code> value
     */
    public final CellType getCellType() {
	return cellType;
    }

    /**
     * Set the <code>CellType</code> value.
     *
     * @param cellType The new CellType value.
     */
    public final void setCellType(final CellType cellType) {
	this.cellType = cellType;
    }

    /**
     * Prints a human readable string representing this column region.
     *
     * @return a human readable representation of this <code>ColumnRegion</code> object.
     */
    public String toString() {
	StringBuilder stb = new StringBuilder( "Column " );
	stb.append( getColumn() );
	stb.append( " [" );
	stb.append( getStartRow() );
	stb.append( "," );
	stb.append( getEndRow() );
	stb.append( "]: " );
	stb.append( getCellType() );
	return stb.toString();
    }

}
