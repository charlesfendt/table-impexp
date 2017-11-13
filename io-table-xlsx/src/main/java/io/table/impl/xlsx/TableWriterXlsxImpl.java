/**
 * TableWriterXlsxImpl.java
 *
 * Copyright (c) 2017, Charles Fendt. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package io.table.impl.xlsx;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.table.api.ITableWriter;
import io.table.impl.xlsx.utils.StringCache;

/**
 * @author charles
 *
 */
public final class TableWriterXlsxImpl implements ITableWriter {

    /** date formater. */
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset

    /** The name of the application. */
    private final String applicationName;
    /** The version of the application. */
    private final String applicationVersion;

    /** The name of the worksheet. */
    private final String wsName;

    /** The output string cache. */
    private final StringCache strCache = new StringCache();

    /** The ZIP output stream. */
    private ZipOutputStream outputZip;

    /** Position in the table. */
    private int indexRow;
    /** Position in the table. */
    private int indexCol;

    /** TRUE if the row is opened. */
    private boolean rowOpened;

    /**
     * Default constructor.
     * 
     * @param applicationName
     *            The application name for XLSX header
     * @param applicationVersion
     *            The version of the application for XLSX header
     * @param wsName
     *            The name of the generated worksheet.
     */
    public TableWriterXlsxImpl(final String applicationName, final String applicationVersion, final String wsName) {
        super();

        this.df.setTimeZone(TimeZone.getTimeZone("UTC"));

        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;

        this.wsName = wsName;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        if (this.outputZip != null) {

            // close the worksheet...
            this.closeRow();
            this.outputZip
                    .write("</sheetData><pageMargins bottom=\"0.75\" footer=\"0.25\" header=\"0.25\" left=\"0.75\" right=\"0.75\" top=\"0.75\"/></worksheet>"
                            .getBytes(StandardCharsets.UTF_8));
            this.outputZip.closeEntry();

            // dump the string cache
            this.outputZip.putNextEntry(new ZipEntry("xl/sharedStrings.xml"));
            this.strCache.write(this.outputZip);
            this.outputZip.closeEntry();

            // dump styles xl/styles.xml
            // FIXME

            this.outputZip.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#initialize(java.io.OutputStream)
     */
    @Override
    public void initialize(final OutputStream output) throws IOException {

        if (output == null) {
            throw new IllegalArgumentException("The output stream must not be null.");
        }

        if (this.outputZip != null) {
            throw new IllegalStateException("The stream is already initialized.");
        }
        this.outputZip = new ZipOutputStream(output);

        this.writeFileContent("[Content_Types].xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/xl/sharedStrings.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml\"/><Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/><Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                        + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                        + "<Override PartName=\"/docProps/core.xml\" ContentType=\"application/vnd.openxmlformats-package.core-properties+xml\"/><Override PartName=\"/docProps/app.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.extended-properties+xml\"/></Types>",
                StandardCharsets.UTF_8);
        this.writeFileContent("docProps/app.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Properties xmlns=\"http://schemas.openxmlformats.org/officeDocument/2006/extended-properties\"><Application>"
                        + this.applicationName + "</Application>"
                        + (this.applicationVersion == null ? ""
                                : "<AppVersion>" + this.applicationVersion + "</AppVersion>")
                        + "</Properties>",
                StandardCharsets.UTF_8);

        final String date = this.df.format(new Date());
        this.writeFileContent("docProps/core.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><cp:coreProperties xmlns:cp=\"http://schemas.openxmlformats.org/package/2006/metadata/core-properties\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><dcterms:created xsi:type=\"dcterms:W3CDTF\">"
                        + date + "</dcterms:created><dc:creator>" + this.applicationName
                        + "</dc:creator></cp:coreProperties>",
                StandardCharsets.UTF_8);
        this.writeFileContent("_rels/.rels",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties\" Target=\"docProps/app.xml\"/><Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties\" Target=\"docProps/core.xml\"/><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/></Relationships>",
                StandardCharsets.UTF_8);
        this.writeFileContent("xl/workbook.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><workbookPr date1904=\"false\"/><bookViews><workbookView activeTab=\"0\"/></bookViews><sheets>"
                        + "<sheet name=\"" + this.wsName + "\" r:id=\"rId3\" sheetId=\"1\"/></sheets></workbook>",
                StandardCharsets.UTF_8);
        this.writeFileContent("xl/_rels/workbook.xml.rels",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Target=\"sharedStrings.xml\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings\"/><Relationship Id=\"rId2\" Target=\"styles.xml\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\"/>"
                        + "<Relationship Id=\"rId3\" Target=\"worksheets/sheet1.xml\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\"/>"
                        + "</Relationships>",
                StandardCharsets.UTF_8);

        // Initialize the worksheet
        this.outputZip.putNextEntry(new ZipEntry("xl/worksheets/sheet1.xml"));
        this.outputZip
                .write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><dimension ref=\"A1\"/><sheetViews><sheetView workbookViewId=\"0\"/></sheetViews><sheetFormatPr defaultRowHeight=\"15.0\"/><sheetData>"
                        .getBytes(StandardCharsets.UTF_8));
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(java.lang.String)
     */
    @Override
    public void appendCell(final String value) throws IOException {
        // open a new row if not already opened
        this.openRow();

        ++this.indexCol;
        final int index = this.strCache.addToCache(value);
        final String cellStr = "<c r=\"" + TableWriterXlsxImpl.colToString(this.indexCol)
                + Integer.toString(this.indexRow) + "\" t=\"s\"><v>" + Integer.toString(index) + "</v></c>";
        this.outputZip.write(cellStr.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(long)
     */
    @Override
    public void appendCell(final long value) throws IOException {
        // open a new row if not already opened
        this.openRow();

        ++this.indexCol;
        final String cellStr = "<c r=\"" + TableWriterXlsxImpl.colToString(this.indexCol)
                + Integer.toString(this.indexRow) + "\" t=\"n\"><v>" + Long.toString(value) + "</v></c>";
        this.outputZip.write(cellStr.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(long)
     */
    @Override
    public void appendCell(final double value) throws IOException {
        // open a new row if not already opened
        this.openRow();

        ++this.indexCol;
        final String cellStr = "<c r=\"" + TableWriterXlsxImpl.colToString(this.indexCol)
                + Integer.toString(this.indexRow) + "\" t=\"n\"><v>" + Double.toString(value) + "</v></c>";
        this.outputZip.write(cellStr.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(java.util.Date)
     */
    @Override
    public void appendCell(final Date value) throws IOException {
        // open a new row if not already opened
        this.openRow();

        // FIXME !
        ++this.indexCol;
        final String cellStr = "<c r=\"" + TableWriterXlsxImpl.colToString(this.indexCol)
                + Integer.toString(this.indexRow) + "\" t=\"n\"><v>" + (value == null ? "" : this.df.format(value))
                + "</v></c>";
        this.outputZip.write(cellStr.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.table.api.ITableWriter#appendNewLine()
     */
    @Override
    public void appendNewLine() throws IOException {
        // open a new row if not already opened
        this.openRow();
        // close the current row
        this.closeRow();
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendNewLine(java.lang.String[])
     */
    @Override
    public void appendNewLine(final String... cells) throws IOException {
        // open a new row if not already opened
        this.openRow();

        for (final String cell : cells) {
            ++this.indexCol;
            final int index = this.strCache.addToCache(cell);
            final String cellStr = "<c r=\"" + TableWriterXlsxImpl.colToString(this.indexCol)
                    + Integer.toString(this.indexRow) + "\" t=\"s\"><v>" + Integer.toString(index) + "</v></c>";
            this.outputZip.write(cellStr.getBytes(StandardCharsets.UTF_8));
        }

        // close the current row
        this.closeRow();
    }

    /**
     * Method to add a complete entry in the ZIP.
     *
     * @param name
     *            Name of the ZIP entry.
     * @param content
     *            Content for the ZIP entry.
     * @param charset
     *            Charset to use.
     * @throws IOException
     *             Any I/O error.
     */
    private void writeFileContent(final String name, final String content, final Charset charset) throws IOException {
        this.outputZip.putNextEntry(new ZipEntry(name));
        this.outputZip.write(content.getBytes(charset));
        this.outputZip.closeEntry();
    }

    /**
     * Method to append a new row.
     *
     * @throws IOException
     *             Any I/O error.
     */
    private void openRow() throws IOException {
        if (!this.rowOpened) {
            this.outputZip
                    .write(("<row r=\"" + Integer.toString(++this.indexRow) + "\">").getBytes(StandardCharsets.UTF_8));
            this.indexCol = 0;
            this.rowOpened = true;
        }
    }

    /**
     * Method to close a new row.
     *
     * @throws IOException
     *             Any I/O error.
     */
    private void closeRow() throws IOException {
        if (this.rowOpened) {
            this.outputZip.write("</row>".getBytes(StandardCharsets.UTF_8));
            this.rowOpened = false;
        }
    }

    /**
     * Convert a column index to a column name.
     *
     * @param column
     *            one-based column index.
     * @return Column name.
     */
    private static String colToString(final int column) {
        final StringBuilder sb = new StringBuilder();
        int c = column - 1;
        while (c >= 0) {
            final int cur = c % 26;
            sb.append((char) ('A' + cur));
            c = (c - cur) / 26 - 1;
        }
        return sb.reverse().toString();
    }
}
