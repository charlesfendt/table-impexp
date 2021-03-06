/**
 *
 */
package io.table.impl.xlsx;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.table.api.ITableReader;
import io.table.impl.xlsx.utils.RowCellUtils;
import io.table.impl.xlsx.utils.pojo.Row;
import io.table.impl.xlsx.utils.pojo.Value;
import io.table.impl.xlsx.utils.sax.handler.ContentTypesHandler;
import io.table.impl.xlsx.utils.sax.handler.SharedStringHandler;
import io.table.impl.xlsx.utils.sax.handler.SheetHandler;

/**
 * Implementation of the reader interface.
 *
 * @author charles
 */
public final class TableReaderXlsxImpl implements ITableReader {

    /** Logger for the class. */
    private static final Logger LOG = Logger.getLogger(TableReaderXlsxImpl.class.getName());

    /** size of the buffer. */
    private static final int BUFFER_SIZE = 1024 * 1024;

    /** List of temporary file to delete. */
    private final Map<String, byte[]> tmps = new HashMap<>();
    /** The rows. */
    private Map<Integer, Row> rows;
    /** Current row, to navigate. */
    private Row currentRow;
    /** Row counter, starting with 1. */
    private int rowCounter = 1;

    /*
     * (non-Javadoc)
     *
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() {
        this.tmps.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableReader#initialize(java.io.InputStream)
     */
    @Override
    public void initialize(final InputStream inputStream) throws IOException {
        try (ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(inputStream))) {

            final byte[] buffer = new byte[TableReaderXlsxImpl.BUFFER_SIZE];
            int readsize = 0;

            ZipEntry entry = zipInput.getNextEntry();
            while (entry != null) {
                if (!entry.isDirectory()) {
                    try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        readsize = zipInput.read(buffer);
                        while (readsize > 0) {
                            out.write(buffer, 0, readsize);
                            readsize = zipInput.read(buffer);
                        }
                        this.tmps.put(entry.getName(), out.toByteArray());
                    }
                }
                // go to the next entry
                entry = zipInput.getNextEntry();
            }
        }

        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true); //$NON-NLS-1$
            } catch (final Exception ex) {
                TableReaderXlsxImpl.LOG.info("Secure parsing not supported..."); //$NON-NLS-1$
                TableReaderXlsxImpl.LOG.log(Level.FINER, "Secure parsing not supported...", ex); //$NON-NLS-1$
            }
            final SAXParser saxParser = factory.newSAXParser();

            // verify that we have a description file !
            final ByteArrayInputStream contentDesc = new ByteArrayInputStream(this.tmps.get("[Content_Types].xml"));

            // parse contentsDesc
            final ContentTypesHandler contentTypesHandler = new ContentTypesHandler();
            saxParser.parse(contentDesc, contentTypesHandler);
            final Map<String, String> content = contentTypesHandler.getAvailableContents();

            final String locationSharedStrings = this.removeLeadingSlash(
                    content.get("application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml"));
            final String locationWorkBook = this.removeLeadingSlash(
                    content.get("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml")); // FIXME also use workbook to handle sheets
            final String locationSheet = this.removeLeadingSlash(
                    content.get("application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml")); // FIXME there can be several sheets with the
                                                                                                               // same id

            // check shared string maps
            Map<Integer, String> sharedStringMap = null;
            if (locationSharedStrings != null) {
                final ByteArrayInputStream sharedStringsFile = new ByteArrayInputStream(
                        this.tmps.get(locationSharedStrings));
                if (sharedStringsFile != null) {
                    final SharedStringHandler sharedStringHandler = new SharedStringHandler();
                    saxParser.parse(sharedStringsFile, sharedStringHandler);
                    sharedStringMap = sharedStringHandler.getMappingTable();
                }
            }

            // check worksheet file
            final ByteArrayInputStream sheetFile = new ByteArrayInputStream(this.tmps.get(locationSheet));
            if (sheetFile != null) {
                final SheetHandler handler = new SheetHandler(sharedStringMap);
                saxParser.parse(sheetFile, handler);
                this.rows = handler.getRows();
            }
        } catch (final Exception e) {
            TableReaderXlsxImpl.LOG.log(Level.INFO, "Reading error", e);
        }

    }

    /**
     * Removes the first slash in string.
     *
     * @param value
     *            to remove from
     * @return the string without leading slash
     */
    private String removeLeadingSlash(final String value) {
        if (value != null && value.startsWith("/")) {
            return value.replaceFirst("/", "");
        }
        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableReader#nextRow()
     */
    @Override
    public boolean nextRow() {
        if (this.rows == null) {
            return false;
        }
        this.currentRow = this.rows.get(this.rowCounter);
        return this.rows.containsKey(this.rowCounter++);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableReader#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return this.currentRow.getColumnCount();
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableReader#getCellAsString(int)
     */
    @Override
    public String getCellAsString(final int columnId) {
        final Value val = this.currentRow.getValues()
                .get(RowCellUtils.colIndexToString(columnId) + this.currentRow.getIndex());
        if (val == null || val.getVal() == null) {
            return null;
        }
        return val.getVal().toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableReader#getCellAsNumber(int)
     */
    @Override
    public Number getCellAsNumber(final int columnId) {
        final Value val = this.currentRow.getValues()
                .get(RowCellUtils.colIndexToString(columnId) + this.currentRow.getIndex());
        return Double.valueOf(val.getVal().toString());
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableReader#getCellAsDate(int)
     */
    @Override
    public Date getCellAsDate(final int columnId) {
        try {
            final Value val = this.currentRow.getValues()
                    .get(RowCellUtils.colIndexToString(columnId) + this.currentRow.getIndex());
            return RowCellUtils.getDateFromDays(Double.parseDouble(val.getVal().toString()));
        } catch (final Exception ex) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableReader#nextRowInt()
     */
    @Override
    public int nextRowInt() {
        if (this.rows == null) {
            return -1;
        }
        this.currentRow = this.rows.get(this.rowCounter);
        if (this.rows.containsKey(this.rowCounter++)) {
            return this.rowCounter;
        } else {
            return -1;
        }
    }

}
