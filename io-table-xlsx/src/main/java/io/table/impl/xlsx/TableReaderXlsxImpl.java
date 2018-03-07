/**
 *
 */
package io.table.impl.xlsx;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.table.api.ITableReader;
import io.table.impl.xlsx.utils.EncodingDetectionHelper;

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

    /*
     * (non-Javadoc)
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

        // verify that we have a description file !
        final File contentDesc = this.tmps.get("[Content_Types].xml");
        if (contentDesc == null) {
            throw new IOException(new IllegalArgumentException());
        }
        final byte[] contentDescBytes = TableReaderXlsxImpl.readFile(contentDesc);
        final String contentDescStr = EncodingDetectionHelper.doRawStream(contentDescBytes);

        final Pattern sheetPatter = Pattern.compile(
                "<[^>]*application/vnd\\.openxmlformats-officedocument\\.spreadsheetml\\.worksheet\\+xml[^>]*>");
    }

    /**
     * Method to fully read a file.
     *
     * @param file
     *            The file to read.
     * @return The raw data of the file.
     * @throws IOException
     *             Any I/O error.
     */
    private static byte[] readFile(final File file) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                final InputStream input = new FileInputStream(file)) {
            final byte[] buffer = new byte[TableReaderXlsxImpl.BUFFER_SIZE];
            int readsize = input.read(buffer);
            while (readsize > 0) {
                output.write(buffer, 0, readsize);
                readsize = input.read(buffer);
            }
            return output.toByteArray();
        }
    }

    @Override
    public boolean nextRow() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getColumnCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getCellAsString(final int columnId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Number getCellAsNumber(final int columnId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getCellAsDate(final int columnId) {
        // TODO Auto-generated method stub
        return null;
    }
}
