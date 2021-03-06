/**
 * TableWriterCsvImpl.java
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
package io.table.impl.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.table.api.ITableWriter;

/**
 * The Writer implementation.
 *
 * @author charles
 *
 */
public final class TableWriterCsvImpl implements ITableWriter {

    /** date formater. */
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** The separator between value. */
    private final String separator;
    /** The quote string. */
    private final String quote;
    /** the escape sequence for the quote. */
    private final String escape;
    /** The newline separator. */
    private final String newLine;
    /** The charset to use. */
    private final Charset charset;

    /** The wrapped stream. */
    private Writer outputWriter;

    /**
     * Default constructor.
     *
     * @param separator
     *            The separator between value.
     * @param quote
     *            The quote string.
     * @param escape
     *            the escape sequence for the quote.
     * @param newLine
     *            The newline separator.
     * @param charset
     *            The charset to use for the output.
     */
    public TableWriterCsvImpl(final String separator, final String quote, final String escape, final String newLine,
            final Charset charset) {
        super();

        this.separator = separator;
        this.quote = quote;
        this.escape = escape;
        this.newLine = newLine;
        this.charset = charset;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        if (this.outputWriter != null) {
            this.outputWriter.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#initialize(java.io.OutputStream)
     */
    @Override
    public void initialize(final OutputStream output) {

        if (output == null) {
            throw new IllegalArgumentException("The output stream must not be null.");
        }

        if (this.outputWriter != null) {
            throw new IllegalStateException("The stream is already initialized.");
        }
        this.outputWriter = new OutputStreamWriter(output, this.charset);

    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(java.lang.String)
     */
    @Override
    public void appendCell(final String value) throws IOException {
        this.outputWriter.append(this.quote);
        if (value != null) {
            this.outputWriter.append(value.replace(this.quote, this.escape));
        }
        this.outputWriter.append(this.quote).append(this.separator);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(long)
     */
    @Override
    public void appendCell(final long value) throws IOException {
        this.outputWriter.append(this.quote).append(Long.toString(value)).append(this.quote).append(this.separator);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(long)
     */
    @Override
    public void appendCell(final double value) throws IOException {
        this.outputWriter.append(this.quote).append(Double.toString(value)).append(this.quote).append(this.separator);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(java.util.Date)
     */
    @Override
    public void appendCell(final Date value) throws IOException {
        this.outputWriter.append(this.quote);
        if (value != null) {
            final String date = this.df.format(value);
            this.outputWriter.append(date.replace(this.quote, this.escape));
        }
        this.outputWriter.append(this.quote).append(this.separator);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendNewLine()
     */
    @Override
    public void appendNewLine() throws IOException {
        this.outputWriter.append(this.newLine);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendNewLine(java.lang.String[])
     */
    @Override
    public void appendNewLine(final String... cells) throws IOException {
        if (cells != null) {
            for (final String cell : cells) {
                this.outputWriter.append(this.quote);
                if (cell != null) {
                    this.outputWriter.append(cell.replace(this.quote, this.escape));
                }
                this.outputWriter.append(this.quote).append(this.separator);
            }
        }
        this.outputWriter.append(this.newLine);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(java.lang.String, boolean, java.lang.String)
     */
    @Override
    public void appendCell(final String value, final boolean isHeader, final String comment) throws IOException {
        // No support for comment or styling in CSV... ignore the parameters !
        this.appendCell(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(long, boolean, java.lang.String)
     */
    @Override
    public void appendCell(final long value, final boolean isHeader, final String comment) throws IOException {
        // No support for comment or styling in CSV... ignore the parameters !
        this.appendCell(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(double, boolean, java.lang.String)
     */
    @Override
    public void appendCell(final double value, final boolean isHeader, final String comment) throws IOException {
        // No support for comment or styling in CSV... ignore the parameters !
        this.appendCell(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendCell(java.util.Date, boolean, java.lang.String)
     */
    @Override
    public void appendCell(final Date value, final boolean isHeader, final String comment) throws IOException {
        // No support for comment or styling in CSV... ignore the parameters !
        this.appendCell(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see io.table.api.ITableWriter#appendNewHeaderLine(java.lang.String[])
     */
    @Override
    public void appendNewHeaderLine(final String... cells) throws IOException {
        // No support for styling in CSV... ignore the parameters !
        this.appendNewLine(cells);
    }

}
