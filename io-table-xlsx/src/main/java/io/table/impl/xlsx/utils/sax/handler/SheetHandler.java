package io.table.impl.xlsx.utils.sax.handler;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import io.table.impl.xlsx.utils.RowCellUtils;
import io.table.impl.xlsx.utils.pojo.EnumDataType;
import io.table.impl.xlsx.utils.pojo.Row;
import io.table.impl.xlsx.utils.pojo.Value;

/**
 * Handler for a single sheet of excel file.
 *
 * @author smarty
 *
 */
public class SheetHandler extends DefaultHandler {

    /** TRUE if currently in cell value. */
    private boolean inCellValue;
    /** TRUE if currently a string is parsed. */
    private boolean isString;
    /** TRUE if currently a inline string is parsed. */
    private boolean isInlineString;
    /** TRUE if currently a inline string is parsed. */
    private boolean is;
    /** TRUE if currently an error is parsed. */
    private boolean isError;
    /** TRUE if currently a number is parsed. */
    private boolean isNumber;
    /** TRUE if currently a boolean is parsed. */
    private boolean isBoolean;
    /** TRUE if currently a date is parsed. */
    private boolean isDate;
    /** The current cell value as string. */
    private String cellValue;

    /** The current matrix index, e.g. A1. */
    private String cellIndexValue;
    /** The map of shared string, from the shared string reading, null if not available. */
    private final Map<Integer, String> sharedStrings;

    /** The rows. */
    private final Map<Integer, Row> rows = new HashMap<>();
    /** The current row. */
    private Row currentRow = null;
    /** The current value. */
    private Value currentValue = null;
    /** The current counter. */
    private int rowCounter = 0;
    /** The current column counter. */
    private int columnCounter = 1;

    /**
     * Constructor.
     *
     * @param sharedStrings
     *            the shared strings table, null if no one exists
     */
    public SheetHandler(final Map<Integer, String> sharedStrings) {
        this.sharedStrings = sharedStrings;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName,
            final Attributes atts) {

        switch (qName) {
            case "sheetData":
                // start of data
                break;
            case "row":
                // start of row
                int rowIndex = 0;
                for (int i = 0; i < atts.getLength(); i++) {
                    if ("r".equals(atts.getQName(i))) {
                        rowIndex = Integer.parseInt(atts.getValue(i));
                        break;
                    }
                }
                while (this.rowCounter < rowIndex) {
                    this.rowCounter++;
                    this.currentRow = new Row();
                    this.currentRow.setIndex(this.rowCounter);
                    this.rows.put(this.rowCounter, this.currentRow);
                }
                break;
            case "c":
                // start of cell
                this.isNumber = true; // default is number?? -> seems so
                for (int i = 0; i < atts.getLength(); i++) {
                    if ("r".equals(atts.getQName(i))) {
                        this.cellIndexValue = atts.getValue(i);
                    } else if ("t".equals(atts.getQName(i))) {
                        switch (atts.getValue(i)) {
                            case "e":
                                this.isError = true;
                                this.isNumber = false;
                                break;
                            case "s":
                                this.isNumber = false;
                                this.isString = true;
                                break;
                            case "inlineStr":
                                this.isNumber = false;
                                this.isInlineString = true;
                                break;
                            case "n":
                                this.isNumber = true;
                                break;
                            case "b":
                                this.isNumber = false;
                                this.isBoolean = true;
                                break;
                            default: // nothing to do
                                break;
                        }
                    } else if ("s".equals(atts.getQName(i))) {
                        // to know if a value is a date, we have to parse styles.xml -> who did this crap??
                        // this.isDate = true;
                    }
                }
                break;
            case "v":
                // start of value
                this.inCellValue = true;
                break;
            case "is":
                // start of value
                if (this.isInlineString) {
                    this.is = true;
                }
                break;
            case "t":
                // start of value
                if (this.is) {
                    this.inCellValue = true;
                }
                break;
            default: // do nothing
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        switch (qName) {
            case "sheetData":
                // end of data
                break;
            case "row":
                // end of row
                if (this.cellIndexValue != null) {
                    this.currentRow.setColumnCount(RowCellUtils.stringToColIndex(this.cellIndexValue));
                }
                this.cellIndexValue = null;
                this.columnCounter = 1;
                break;
            case "c":
                // end of cell
                // adding empty cells if required
                while (this.columnCounter < RowCellUtils.stringToColIndex(this.cellIndexValue)) {
                    final Value emptyValue = new Value();
                    emptyValue.setVal("");
                    emptyValue.setDataType(EnumDataType.STRING);
                    final String localIndex = RowCellUtils.colIndexToString(this.columnCounter) + this.rowCounter;
                    emptyValue.setCell(localIndex);
                    this.currentRow.getValues().put(localIndex, emptyValue);
                    this.columnCounter++;
                }

                // add value if this not required element is missing
                if (this.currentValue == null) {
                    this.currentValue = new Value();
                    this.currentValue.setVal("");
                    this.currentValue.setDataType(EnumDataType.STRING);
                }

                this.currentRow.getValues().put(this.cellIndexValue, this.currentValue);
                this.currentValue.setCell(this.cellIndexValue);
                this.currentValue = null;
                this.columnCounter++;
                break;
            case "t":
                if (this.isInlineString && this.is) {
                    this.isInlineString = false;
                    this.is = false;
                    this.currentValue = new Value();
                    this.currentValue.setDataType(EnumDataType.STRING);
                    if (this.cellValue == null) {
                        this.currentValue.setVal("");
                    } else {
                        this.currentValue.setVal(this.cellValue);
                    }
                    this.cellValue = null;
                    this.inCellValue = false;
                }
                break;
            case "v":
                // end of value
                this.currentValue = new Value();
                if (this.isString) {
                    this.isString = false;
                    this.currentValue.setDataType(EnumDataType.STRING);
                    if (this.sharedStrings != null) {
                        try {
                            final int index = Integer.parseInt(this.cellValue);
                            final String value = this.sharedStrings.get(index);
                            if (value != null) {
                                this.cellValue = value;
                            }
                        } catch (final Exception e) {
                            // FIXME LOG me
                            return;
                        }
                    }
                } else if (this.isInlineString) {
                    this.isInlineString = false;
                    this.currentValue.setDataType(EnumDataType.STRING);
                } else if (this.isNumber) {
                    this.isNumber = false;
                    this.currentValue.setDataType(EnumDataType.NUMBER);
                } else if (this.isDate) {
                    this.isDate = false;
                    // this.currentValue.setVal(val);
                } else if (this.isBoolean) {
                    this.isBoolean = false;
                    this.currentValue.setDataType(EnumDataType.BOOLEAN);
                    final int booleanInt = Integer.parseInt(this.cellValue);
                    final boolean valueBoolean = booleanInt == 1 ? true : false;
                    this.cellValue = Boolean.toString(valueBoolean);
                } else if (this.isError) {
                    this.isError = false;
                    this.currentValue.setDataType(EnumDataType.ERROR);
                    this.cellValue = ""; // in case of error set value to empty FIXME??
                }
                this.currentValue.setVal(this.cellValue);
                this.cellValue = null;
                this.inCellValue = false;
                break;
            default: // do nothing
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(final char[] ch, final int start, final int length) {
        if (this.inCellValue) {
            this.cellValue = new String(ch, start, length);
        }
    }

    /**
     * Getter for rows.
     *
     * @return a map of all rows
     */
    public Map<Integer, Row> getRows() {
        return this.rows;
    }

}
