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
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.table.api.ITableWriter;
import io.table.impl.xlsx.utils.CommentCache;
import io.table.impl.xlsx.utils.RowCellUtils;
import io.table.impl.xlsx.utils.StringCache;
import io.table.impl.xlsx.utils.StringEscapeUtils;
import io.table.impl.xlsx.utils.StyleCache;

/**
 * XLSX implementation.
 *
 * @author charles
 */
public final class TableWriterXlsxImpl implements ITableWriter {

    /** date formater. */
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset

    /** The name of the application. */
    private final String applicationName;
    /** The version of the application. */
    private final String applicationVersion;

    /** The counter for the amount of worksheet. */
    private int wsCount;

    /** the list of worksheet. */
    private final List<WsDesc> wsList = new LinkedList<>();

    /** The output string cache. */
    private final StringCache stringCache = new StringCache();
    /** The output style cache. */
    private final StyleCache styleCache = new StyleCache();
    /** The output comment cache. */
    private final CommentCache commentCache = new CommentCache();

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
        final boolean matchesVersion = Pattern.matches("[1-9]*\\.[0-9]*", applicationVersion);
        if (!matchesVersion) {
            throw new IllegalArgumentException("Invalid application version");
        }
        this.applicationVersion = applicationVersion;

        final int index = ++this.wsCount;
        final String fileName = "sheet" + index + ".xml";

        this.wsList.add(new WsDesc(wsName, fileName, index));

    }

    /**
     * Method to close the current tab.
     *
     * @throws IOException
     *             Any I/O error.
     */
    private void closeWs() throws IOException {
        this.closeRow();
        this.outputZip.write(
                ("</sheetData><pageMargins bottom=\"0.75\" footer=\"0.25\" header=\"0.25\" left=\"0.75\" right=\"0.75\" top=\"0.75\"/>" //$NON-NLS-1$
                        + "<legacyDrawing r:id=\"rId3\"/></worksheet>").getBytes(StandardCharsets.UTF_8)); //$NON-NLS-1$
        this.outputZip.closeEntry();

        // dump comment xl/comments1.xml
        this.outputZip.putNextEntry(new ZipEntry("xl/comments" + this.wsCount + ".xml"));
        this.commentCache.write(this.outputZip, this.applicationName);
        this.outputZip.closeEntry();

        this.outputZip.putNextEntry(new ZipEntry("xl/drawings/vmlDrawing" + this.wsCount + ".vml"));
        this.commentCache.writeVmlDrawing(this.outputZip);
        this.outputZip.closeEntry();

        this.commentCache.clear();

        this.indexRow = 0;
        this.indexCol = 0;
        this.rowOpened = false;

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
            this.closeWs();

            // dump the string cache
            this.outputZip.putNextEntry(new ZipEntry("xl/sharedStrings.xml"));
            this.stringCache.write(this.outputZip);
            this.outputZip.closeEntry();

            // dump styles xl/styles.xml
            this.outputZip.putNextEntry(new ZipEntry("xl/styles.xml"));
            this.styleCache.write(this.outputZip);
            this.outputZip.closeEntry();

            final StringBuilder content = new StringBuilder();
            final StringBuilder workbook = new StringBuilder();
            final StringBuilder sheets = new StringBuilder();

            for (final WsDesc ws : this.wsList) {
                content.append("<Override PartName=\"/xl/comments").append(ws.index).append( //$NON-NLS-1$
                        ".xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.comments+xml\"/>\n");
                content.append("<Override PartName=\"/xl/worksheets/").append(ws.fileName).append( //$NON-NLS-1$
                        "\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n");

                workbook.append("<Relationship Id=\"rId").append(ws.index + 2).append("\" Target=\"worksheets/") //$NON-NLS-1$
                        .append(ws.fileName)
                        .append("\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\"/>");

                sheets.append("<sheet name=\"").append(ws.name).append("\" r:id=\"rId").append(ws.index + 2) //$NON-NLS-1$
                        .append("\" sheetId=\"").append(ws.index).append("\"/>");

                // link comments
                this.writeFileContent("xl/worksheets/_rels/sheet" + ws.index + ".xml.rels",
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" //$NON-NLS-1$
                                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" //$NON-NLS-1$
                                + "<Relationship Id=\"rId2\" Target=\"../comments" + ws.index //$NON-NLS-1$
                                + ".xml\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/comments\"/>"
                                + "<Relationship Id=\"rId3\" Target=\"../drawings/vmlDrawing" + ws.index //$NON-NLS-1$
                                + ".vml\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/vmlDrawing\"/>"
                                + "</Relationships>", //$NON-NLS-1$
                        StandardCharsets.UTF_8);
            }

            this.writeFileContent("[Content_Types].xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" //$NON-NLS-1$
                            + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n" //$NON-NLS-1$
                            + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n" //$NON-NLS-1$
                            + "<Default Extension=\"vml\" ContentType=\"application/vnd.openxmlformats-officedocument.vmlDrawing\"/>\n" //$NON-NLS-1$
                            + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>\n" //$NON-NLS-1$
                            + "<Override PartName=\"/docProps/app.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.extended-properties+xml\"/>\n" //$NON-NLS-1$
                            + "<Override PartName=\"/docProps/core.xml\" ContentType=\"application/vnd.openxmlformats-package.core-properties+xml\"/>\n" //$NON-NLS-1$
                            + "<Override PartName=\"/xl/sharedStrings.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml\"/>\n" //$NON-NLS-1$
                            + "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>\n" //$NON-NLS-1$
                            + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>\n" //$NON-NLS-1$
                            + content.toString() //
                            + "</Types>",
                    StandardCharsets.UTF_8);

            this.writeFileContent("xl/_rels/workbook.xml.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" //$NON-NLS-1$
                            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" //$NON-NLS-1$
                            + "<Relationship Id=\"rId1\" Target=\"sharedStrings.xml\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings\"/>" //$NON-NLS-1$
                            + "<Relationship Id=\"rId2\" Target=\"styles.xml\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\"/>" //$NON-NLS-1$
                            + workbook.toString() //
                            + "</Relationships>",
                    StandardCharsets.UTF_8);

            this.writeFileContent("xl/workbook.xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">" //$NON-NLS-1$
                            + "<workbookPr date1904=\"false\"/>" //$NON-NLS-1$
                            + "<bookViews><workbookView activeTab=\"0\"/>" //$NON-NLS-1$
                            + "</bookViews><sheets>" //$NON-NLS-1$
                            + sheets.toString() + "</sheets></workbook>", //$NON-NLS-1$
                    StandardCharsets.UTF_8);

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

        this.writeFileContent("docProps/app.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Properties xmlns=\"http://schemas.openxmlformats.org/officeDocument/2006/extended-properties\"><Application>"
                        + StringEscapeUtils.escapeString(this.applicationName) + "</Application>"
                        + (this.applicationVersion == null ? ""
                                : "<AppVersion>" + this.applicationVersion + "</AppVersion>")
                        + "</Properties>",
                StandardCharsets.UTF_8);

        final String date = this.df.format(new Date());
        this.writeFileContent("docProps/core.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><cp:coreProperties xmlns:cp=\"http://schemas.openxmlformats.org/package/2006/metadata/core-properties\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><dcterms:created xsi:type=\"dcterms:W3CDTF\">"
                        + date + "</dcterms:created><dc:creator>" + StringEscapeUtils.escapeString(this.applicationName)
                        + "</dc:creator></cp:coreProperties>",
                StandardCharsets.UTF_8);
        this.writeFileContent("_rels/.rels",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties\" Target=\"docProps/app.xml\"/><Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties\" Target=\"docProps/core.xml\"/><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/></Relationships>",
                StandardCharsets.UTF_8);
        this.openWs();
    }

    /**
     * Method to open a new tab.
     *
     * @throws IOException
     *             Any I/O error.
     */
    private void openWs() throws IOException {
        // Initialize the worksheet
        this.outputZip.putNextEntry(new ZipEntry("xl/worksheets/sheet" + this.wsCount + ".xml"));
        this.outputZip.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //$NON-NLS-1$
                + "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">" //$NON-NLS-1$
                + "<dimension ref=\"A1\"/><sheetViews><sheetView workbookViewId=\"0\" tabSelected=\"true\"/></sheetViews><sheetFormatPr defaultRowHeight=\"15.0\"/><sheetData>\n")
                        .getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Method to close the current tab and open a new one.
     *
     * @param wsName
     *            the name of the new worksheet
     * @throws IOException
     *             I/O error.
     */
    public void openNewWs(final String wsName) throws IOException {
        this.closeWs();

        final int index = ++this.wsCount;
        final String fileName = "sheet" + index + ".xml";
        this.wsList.add(new WsDesc(wsName, fileName, index));

        this.openWs();
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(java.lang.String, boolean, java.lang.String)
     */
    @Override
    public void appendCell(final String value, final boolean isHeader, final String comment) throws IOException {
        // open a new row if not already opened
        this.openRow();
        ++this.indexCol;

        final int index = this.stringCache.addToCache(value);

        final String style = isHeader ? " s=\"1\"" : "";
        final String ref = RowCellUtils.colIndexToString(this.indexCol) + Integer.toString(this.indexRow);

        final String cellStr = "<c r=\"" + ref + "\" t=\"s\"" + style + "><v>" + Integer.toString(index) + "</v></c>\n";
        this.outputZip.write(cellStr.getBytes(StandardCharsets.UTF_8));
        if (comment != null && !comment.isEmpty()) {
            this.commentCache.addComment(this.indexCol - 1, this.indexRow - 1, ref, comment);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(long, boolean, java.lang.String)
     */
    @Override
    public void appendCell(final long value, final boolean isHeader, final String comment) throws IOException {
        // open a new row if not already opened
        this.openRow();
        ++this.indexCol;

        final String style = isHeader ? " s=\"1\"" : "";
        final String ref = RowCellUtils.colIndexToString(this.indexCol) + Integer.toString(this.indexRow);

        final String cellStr = "<c r=\"" + ref + "\" t=\"n\"" + style + "><v>" + Long.toString(value) + "</v></c>\n";
        this.outputZip.write(cellStr.getBytes(StandardCharsets.UTF_8));
        if (comment != null && !comment.isEmpty()) {
            this.commentCache.addComment(this.indexCol - 1, this.indexRow - 1, ref, comment);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(double, boolean, java.lang.String)
     */
    @Override
    public void appendCell(final double value, final boolean isHeader, final String comment) throws IOException {
        // open a new row if not already opened
        this.openRow();
        ++this.indexCol;

        final String style = isHeader ? " s=\"1\"" : "";
        final String ref = RowCellUtils.colIndexToString(this.indexCol) + Integer.toString(this.indexRow);

        final String cellStr = "<c r=\"" + ref + "\" t=\"n\"" + style + "><v>" + Double.toString(value) + "</v></c>\n";
        this.outputZip.write(cellStr.getBytes(StandardCharsets.UTF_8));
        if (comment != null && !comment.isEmpty()) {
            this.commentCache.addComment(this.indexCol - 1, this.indexRow - 1, ref, comment);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(java.util.Date, boolean, java.lang.String)
     */
    @Override
    public void appendCell(final Date value, final boolean isHeader, final String comment) throws IOException {
        // open a new row if not already opened
        this.openRow();
        ++this.indexCol;

        // number of days since 01.01.1900 + 2
        final String val = value == null ? "" : Double.toString(RowCellUtils.getDays(value));
        final String style = isHeader ? " s=\"3\"" : " s=\"2\"";
        final String ref = RowCellUtils.colIndexToString(this.indexCol) + Integer.toString(this.indexRow);

        final String cellStr = "<c r=\"" + ref + "\" t=\"n\"" + style + "><v>" + val + "</v></c>\n";
        this.outputZip.write(cellStr.getBytes(StandardCharsets.UTF_8));
        if (comment != null && !comment.isEmpty()) {
            this.commentCache.addComment(this.indexCol - 1, this.indexRow - 1, ref, comment);
        }
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
        this.appendNewLine(false, cells);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendNewHeaderLine(java.lang.String[])
     */
    @Override
    public void appendNewHeaderLine(final String... cells) throws IOException {
        this.appendNewLine(true, cells);
    }

    /**
     * Method to add a new line with style parameter.
     *
     * @param isHeader
     *            TRUE if the line is a header.
     * @param cells
     *            List of values to append.
     * @throws IOException
     *             Any I/O error.
     */
    public void appendNewLine(final boolean isHeader, final String... cells) throws IOException {
        // open a new row if not already opened
        this.openRow();

        final String style = isHeader ? " s=\"3\"" : " s=\"2\"";

        for (final String cell : cells) {
            ++this.indexCol;

            final int index = this.stringCache.addToCache(cell);

            final String ref = RowCellUtils.colIndexToString(this.indexCol) + Integer.toString(this.indexRow);

            final String cellStr = "<c r=\"" + ref + "\" t=\"s\"" + style + "><v>" + Integer.toString(index)
                    + "</v></c>";
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
            this.outputZip.write(
                    ("<row r=\"" + Integer.toString(++this.indexRow) + "\">\n").getBytes(StandardCharsets.UTF_8));
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
            this.outputZip.write("</row>\n".getBytes(StandardCharsets.UTF_8));
            this.rowOpened = false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(java.lang.String)
     */
    @Override
    public void appendCell(final String value) throws IOException {
        this.appendCell(value, false, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(long)
     */
    @Override
    public void appendCell(final long value) throws IOException {
        this.appendCell(value, false, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(double)
     */
    @Override
    public void appendCell(final double value) throws IOException {
        this.appendCell(value, false, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(java.util.Date)
     */
    @Override
    public void appendCell(final Date value) throws IOException {
        this.appendCell(value, false, null);
    }

    /**
     * POJO for work sheet description.
     *
     * @author fendtc
     */
    private static final class WsDesc {

        /** Name of the worksheet. */
        private final String name;
        /** file name. */
        private final String fileName;
        /** index for the tab. */
        private final int index;

        /**
         * Constructor of the POJO.
         *
         * @param name
         *            Name of the worksheet.
         * @param fileName
         *            file name.
         * @param index
         *            Index in the tab order.
         */
        public WsDesc(final String name, final String fileName, final int index) {
            super();
            this.name = name;
            this.fileName = fileName;
            this.index = index;
        }
    }
}
