/**
 *
 */
package io.table.impl.xlsx;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

    /** size of the buffer. */
    private static final int BUFFER_SIZE = 1024 * 1024;

    /** List of temporary file to delete. */
    private final Map<String, File> tmps = new HashMap<>();
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
    public void close() throws IOException {
        // remove temporary files...
        for (final Map.Entry<String, File> entry : this.tmps.entrySet()) {
            entry.getValue().delete();
        }
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

            // TODO try to use a ram cache for small data to accelerate the reading
            ZipEntry entry = zipInput.getNextEntry();
            while (entry != null) {
                if (!entry.isDirectory()) {
                    // create a temporary file
                    final File tmp = File.createTempFile("io-table", "tmp");
                    this.tmps.put(entry.getName(), tmp);
                    // extract file !
                    try (OutputStream out = new FileOutputStream(tmp)) {
                        readsize = zipInput.read(buffer);
                        while (readsize > 0) {
                            out.write(buffer, 0, readsize);
                            readsize = zipInput.read(buffer);
                        }
                    }
                }
                // go to the next entry
                entry = zipInput.getNextEntry();
            }
        }

        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();

            // verify that we have a description file !
            final File contentDesc = this.tmps.get("[Content_Types].xml");
            if (contentDesc == null) {
                throw new IOException(new IllegalArgumentException());
            }

            // parse contentsDesc
            final ContentTypesHandler contentTypesHandler = new ContentTypesHandler();
            saxParser.parse(contentDesc, contentTypesHandler);
            final Map<String, String> content = contentTypesHandler.getAvailableContents();
            // FIXME use content and rels file to select the correct files

            final SharedStringHandler sharedStringHandler = new SharedStringHandler();
            saxParser.parse(this.tmps.get("xl/sharedStrings.xml"), sharedStringHandler);

            final SheetHandler handler = new SheetHandler(sharedStringHandler.getMappingTable());
            saxParser.parse(this.tmps.get("xl/worksheets/sheet1.xml"), handler);
            this.rows = handler.getRows();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // final byte[] contentDescBytes = TableReaderXlsxImpl.readFile(contentDesc);
        // final String contentDescStr = EncodingDetectionHelper.doRawStream(contentDescBytes);
        //
        // final Pattern sheetPatter = Pattern.compile(
        // "<[^>]*application/vnd\\.openxmlformats-officedocument\\.spreadsheetml\\.worksheet\\+xml[^>]*>");
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableReader#nextRow()
     */
    @Override
    public boolean nextRow() {
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
        if (val == null) {
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
        return Double.parseDouble(val.getVal().toString());
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
            final Double value = Double.parseDouble(val.getVal().toString());
            final double curVal = ((value * 86_400_000.0d) - (25569.0d * 86_400_000.0d));
            return new Date((long) curVal);
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
        this.currentRow = this.rows.get(this.rowCounter);
        if (this.rows.containsKey(this.rowCounter++)) {
            return this.rowCounter;
        } else {
            return -1;
        }
    }

}
